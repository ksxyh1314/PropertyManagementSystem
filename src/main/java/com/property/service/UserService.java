package com.property.service;

import com.property.dao.UserDao;
import com.property.entity.User;
import com.property.util.DBUtil;
import com.property.util.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户服务类（完善版：支持角色和状态筛选）
 */
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private UserDao userDao = new UserDao();

    /**
     * 用户登录 (修改版：增加身份验证)
     * @param username 用户名
     * @param password 密码
     * @param role 身份 (admin/owner/finance)
     */
    public User login(String username, String password, String role) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        // 新增：校验身份参数
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("请选择登录身份");
        }

        // 保持原有的 MD5 加密逻辑 (非常重要，不要动)
        String encryptedPassword = MD5Util.encrypt(password);

        // 调用 DAO 层，传入 role 进行匹配
        User user = userDao.login(username, encryptedPassword, role);

        if (user == null) {
            logger.warn("登录失败：用户名、密码错误或身份不匹配 - {} (身份: {})", username, role);
            return null;
        }

        // 更新最后登录时间
        userDao.updateLastLogin(user.getUserId());
        logger.info("用户登录成功：{} - {} (身份: {})", username, user.getRealName(), user.getUserRole());

        return user;
    }

    /**
     * 根据ID查询用户
     */
    public User findById(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return userDao.findById(userId);
    }

    /**
     * 根据用户名查询用户
     */
    public User findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        return userDao.findByUsername(username);
    }

    /**
     * 查询所有用户
     */
    public List<User> findAll() {
        return userDao.findAll();
    }

    /**
     * 根据角色查询用户
     */
    public List<User> findByRole(String role) {
        return userDao.findByRole(role);
    }

    /**
     * 🔥 分页查询用户（支持关键词、角色、状态筛选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param keyword 关键词（用户名、真实姓名、手机号）
     * @param userRole 角色筛选（admin/owner/finance，为空则不筛选）
     * @param status 状态筛选（0/1，为null则不筛选）
     */
    public Map<String, Object> findByPage(int pageNum, int pageSize, String keyword, String userRole, Integer status) {
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1) pageSize = 10;

        // 🔥 调用支持筛选的 DAO 方法
        List<User> list = userDao.findByPageWithFilter(pageNum, pageSize, keyword, userRole, status);
        long total = userDao.countByFilter(keyword, userRole, status);
        int totalPages = (int) Math.ceil((double) total / pageSize);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", totalPages);

        return result;
    }

    /**
     * 🔥 分页查询用户（兼容旧版本，不带筛选）
     */
    public Map<String, Object> findByPage(int pageNum, int pageSize, String keyword) {
        return findByPage(pageNum, pageSize, keyword, null, null);
    }

    /**
     * 添加用户
     */
    public Integer addUser(User user) {
        // 参数验证
        validateUser(user);

        // 检查用户名是否已存在
        if (userDao.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("用户名已存在：" + user.getUsername());
        }

        // 密码加密
        if (user.getPassword() != null) {
            user.setPassword(MD5Util.encrypt(user.getPassword()));
        }

        // 设置默认状态
        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        Integer userId = userDao.insert(user);
        logger.info("添加用户成功：{} - {}", user.getUsername(), user.getRealName());

        return userId;
    }

    /**
     * 更新用户信息
     */
    public boolean updateUser(User user) {
        if (user.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 检查用户是否存在
        User existUser = userDao.findById(user.getUserId());
        if (existUser == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        int rows = userDao.update(user);
        if (rows > 0) {
            logger.info("更新用户成功：{}", user.getUserId());
            return true;
        }
        return false;
    }

    /**
     * 修改密码
     */
    public boolean changePassword(Integer userId, String oldPassword, String newPassword) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("原密码不能为空");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("新密码不能为空");
        }

        // 验证密码强度（8位以上，含字母和数字）
        if (!isValidPassword(newPassword)) {
            throw new IllegalArgumentException("密码必须8位以上，且包含字母和数字");
        }

        // 验证原密码
        User user = userDao.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        String encryptedOldPassword = MD5Util.encrypt(oldPassword);
        if (!encryptedOldPassword.equals(user.getPassword())) {
            throw new IllegalArgumentException("原密码错误");
        }

        // 更新密码
        String encryptedNewPassword = MD5Util.encrypt(newPassword);
        int rows = userDao.updatePassword(userId, encryptedNewPassword);

        if (rows > 0) {
            logger.info("修改密码成功：用户ID={}", userId);
            return true;
        }
        return false;
    }

    /**
     * 重置密码（管理员功能）
     */
    public boolean resetPassword(Integer userId, String newPassword) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("新密码不能为空");
        }

        // 🔥 修改：重置密码时不强制要求密码强度（管理员可以设置简单密码如 123456）
        // 但仍然建议使用强密码
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("密码长度至少6位");
        }

        String encryptedPassword = MD5Util.encrypt(newPassword);
        int rows = userDao.updatePassword(userId, encryptedPassword);

        if (rows > 0) {
            logger.info("重置密码成功：用户ID={}", userId);
            return true;
        }
        return false;
    }

    /**
     * 删除用户
     */
    public boolean deleteUser(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        int rows = userDao.delete(userId);
        if (rows > 0) {
            logger.info("删除用户成功：用户ID={}", userId);
            return true;
        }
        return false;
    }

    /**
     * 启用/禁用用户
     */
    public boolean updateStatus(Integer userId, Integer status) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new IllegalArgumentException("状态值无效");
        }

        int rows = userDao.updateStatus(userId, status);
        if (rows > 0) {
            logger.info("更新用户状态成功：用户ID={}, 状态={}", userId, status);
            return true;
        }
        return false;
    }

    /**
     * 🔥 根据角色统计用户数量
     */
    public Map<String, Long> countByRole() {
        Map<String, Long> result = new HashMap<>();
        result.put("admin", userDao.countByRole("admin"));
        result.put("finance", userDao.countByRole("finance"));
        result.put("owner", userDao.countByRole("owner"));
        return result;
    }

    /**
     * 🔥 根据状态统计用户数量
     */
    public Map<String, Long> countByStatus() {
        Map<String, Long> result = new HashMap<>();
        result.put("active", userDao.countByStatus(1));
        result.put("inactive", userDao.countByStatus(0));
        return result;
    }

    /**
     * 验证用户信息
     */
    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("用户信息不能为空");
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        if (user.getRealName() == null || user.getRealName().trim().isEmpty()) {
            throw new IllegalArgumentException("真实姓名不能为空");
        }
        if (user.getUserRole() == null || user.getUserRole().trim().isEmpty()) {
            throw new IllegalArgumentException("用户角色不能为空");
        }

        // 验证角色
        if (!user.getUserRole().matches("^(admin|owner|finance)$")) {
            throw new IllegalArgumentException("用户角色无效");
        }

        // 🔥 修改：添加用户时密码长度至少6位即可（不强制要求字母+数字）
        if (user.getPassword().length() < 6) {
            throw new IllegalArgumentException("密码长度至少6位");
        }

        // 验证手机号
        if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
            if (!user.getPhone().matches("^1[3-9]\\d{9}$")) {
                throw new IllegalArgumentException("手机号格式不正确");
            }
        }

        // 验证身份证号
        if (user.getIdCard() != null && !user.getIdCard().trim().isEmpty()) {
            if (!user.getIdCard().matches("^\\d{17}[\\dXx]$")) {
                throw new IllegalArgumentException("身份证号格式不正确");
            }
        }
    }

    /**
     * 验证密码强度（8位以上，含字母和数字）
     */
    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        // 必须包含字母和数字
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        return hasLetter && hasDigit;
    }
    /**
     * 修改密码
     */
    public boolean updatePassword(String username, String newPassword) {
        logger.info("🔐 修改密码: username={}", username);

        String sql = "UPDATE users SET password = ? WHERE username = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                logger.info("✅ 密码修改成功");
                return true;
            } else {
                logger.warn("⚠️ 密码修改失败: 用户不存在");
                return false;
            }
        } catch (SQLException e) {
            logger.error("❌ 修改密码失败", e);
            throw new RuntimeException("修改密码失败", e);
        }
    }

}
