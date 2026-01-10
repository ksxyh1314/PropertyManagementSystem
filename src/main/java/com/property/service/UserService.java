package com.property.service;

import com.property.dao.UserDao;
import com.property.entity.User;
import com.property.util.DBUtil;
import com.property.util.MD5Util;
import com.property.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户服务类（完善版：支持角色和状态筛选 + 日志记录）
 */
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private UserDao userDao = new UserDao();

    /**
     * 用户登录（不传 request，兼容旧代码）
     */
    public User login(String username, String password, String role) {
        return login(username, password, role, null);
    }

    /**
     * 用户登录 (修改版：增加身份验证 + 日志记录)
     * @param username 用户名
     * @param password 密码
     * @param role 身份 (admin/owner/finance)
     * @param request HttpServletRequest（用于记录日志）
     */
    public User login(String username, String password, String role, HttpServletRequest request) {
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
            // ✅ 记录登录失败日志
            if (request != null) {
                LogUtil.logLogin(null, username, false, request);
            }
            return null;
        }

        // 更新最后登录时间
        userDao.updateLastLogin(user.getUserId());
        logger.info("用户登录成功：{} - {} (身份: {})", username, user.getRealName(), user.getUserRole());

        // ✅ 记录登录成功日志
        if (request != null) {
            LogUtil.logLogin(user.getUserId(), username, true, request);
        }

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
     * 添加用户（不传 request，兼容旧代码）
     */
    public Integer addUser(User user) {
        return addUser(user, null);
    }

    /**
     * 添加用户（增加日志记录）
     */
    public Integer addUser(User user, HttpServletRequest request) {
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

        // ✅ 记录添加用户日志
        if (request != null) {
            LogUtil.log(
                    getUserId(request),
                    getUsername(request),
                    "user_add",
                    "添加用户：" + user.getUsername() + "（" + user.getRealName() + "），角色：" + user.getUserRole(),
                    LogUtil.getClientIP(request)
            );
        }

        return userId;
    }

    /**
     * 更新用户信息（不传 request，兼容旧代码）
     */
    public boolean updateUser(User user) {
        return updateUser(user, null);
    }

    /**
     * 更新用户信息（增加日志记录）
     */
    public boolean updateUser(User user, HttpServletRequest request) {
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

            // ✅ 记录更新用户日志
            if (request != null) {
                LogUtil.log(
                        getUserId(request),
                        getUsername(request),
                        "user_update",
                        "修改用户信息：" + existUser.getUsername() + "（" + existUser.getRealName() + "）",
                        LogUtil.getClientIP(request)
                );
            }

            return true;
        }
        return false;
    }

    /**
     * 修改密码（不传 request，兼容旧代码）
     */
    public boolean changePassword(Integer userId, String oldPassword, String newPassword) {
        return changePassword(userId, oldPassword, newPassword, null);
    }

    /**
     * 修改密码（增加日志记录）
     */
    public boolean changePassword(Integer userId, String oldPassword, String newPassword, HttpServletRequest request) {
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

            // ✅ 记录修改密码日志
            if (request != null) {
                LogUtil.log(
                        userId,
                        user.getUsername(),
                        "change_password",
                        "用户修改密码",
                        LogUtil.getClientIP(request)
                );
            }

            return true;
        }
        return false;
    }

    /**
     * 重置密码（管理员功能，不传 request，兼容旧代码）
     */
    public boolean resetPassword(Integer userId, String newPassword) {
        return resetPassword(userId, newPassword, null);
    }

    /**
     * 重置密码（管理员功能，增加日志记录）
     */
    public boolean resetPassword(Integer userId, String newPassword, HttpServletRequest request) {
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

        // 查询用户信息（用于日志）
        User user = userDao.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        String encryptedPassword = MD5Util.encrypt(newPassword);
        int rows = userDao.updatePassword(userId, encryptedPassword);

        if (rows > 0) {
            logger.info("重置密码成功：用户ID={}", userId);

            // ✅ 记录重置密码日志
            if (request != null) {
                LogUtil.log(
                        getUserId(request),
                        getUsername(request),
                        "reset_password",
                        "管理员重置密码：" + user.getUsername() + "（" + user.getRealName() + "）",
                        LogUtil.getClientIP(request)
                );
            }

            return true;
        }
        return false;
    }

    /**
     * 删除用户（不传 request，兼容旧代码）
     */
    public boolean deleteUser(Integer userId) {
        return deleteUser(userId, null);
    }

    /**
     * 删除用户（增加日志记录）
     */
    public boolean deleteUser(Integer userId, HttpServletRequest request) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 查询用户信息（用于日志）
        User user = userDao.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        int rows = userDao.delete(userId);
        if (rows > 0) {
            logger.info("删除用户成功：用户ID={}", userId);

            // ✅ 记录删除用户日志
            if (request != null) {
                LogUtil.log(
                        getUserId(request),
                        getUsername(request),
                        "user_delete",
                        "删除用户：" + user.getUsername() + "（" + user.getRealName() + "）",
                        LogUtil.getClientIP(request)
                );
            }

            return true;
        }
        return false;
    }

    /**
     * 启用/禁用用户（不传 request，兼容旧代码）
     */
    public boolean updateStatus(Integer userId, Integer status) {
        return updateStatus(userId, status, null);
    }

    /**
     * 启用/禁用用户（增加日志记录）
     */
    public boolean updateStatus(Integer userId, Integer status, HttpServletRequest request) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new IllegalArgumentException("状态值无效");
        }

        // 查询用户信息（用于日志）
        User user = userDao.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        int rows = userDao.updateStatus(userId, status);
        if (rows > 0) {
            logger.info("更新用户状态成功：用户ID={}, 状态={}", userId, status);

            // ✅ 记录更新状态日志
            if (request != null) {
                String statusDesc = status == 1 ? "启用" : "禁用";
                LogUtil.log(
                        getUserId(request),
                        getUsername(request),
                        "user_status",
                        statusDesc + "用户：" + user.getUsername() + "（" + user.getRealName() + "）",
                        LogUtil.getClientIP(request)
                );
            }

            return true;
        }
        return false;
    }

    /**
     * 修改密码（支持不传 operatorId，兼容旧代码）
     */
    public boolean updatePassword(String username, String newPassword, HttpServletRequest request) {
        return updatePassword(username, newPassword, null, request);
    }

    /**
     * ✅ 修改密码（增加日志记录，支持传入 operatorId）
     *
     * @param username 用户名
     * @param newPassword 新密码（已加密）
     * @param operatorId 操作员ID（如果是用户自己修改密码，传入用户自己的ID）
     * @param request HTTP请求对象（用于记录日志）
     * @return 是否成功
     */
    public boolean updatePassword(String username, String newPassword, Integer operatorId, HttpServletRequest request) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("新密码不能为空");
        }

        logger.info("🔐 修改密码: username={}", username);

        String sql = "UPDATE users SET password = ? WHERE username = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                logger.info("✅ 密码修改成功");

                // ✅ 记录修改密码日志
                if (request != null) {
                    // 查询用户信息获取真实姓名
                    User user = userDao.findByUsername(username);
                    String realName = user != null ? user.getRealName() : username;

                    // 如果没有传入 operatorId，尝试从 session 获取
                    if (operatorId == null) {
                        operatorId = getUserId(request);
                    }

                    LogUtil.log(
                            operatorId != null ? operatorId : 0,
                            username,
                            "password_update",
                            "修改密码：" + realName + "（" + username + "）",
                            LogUtil.getClientIP(request)
                    );
                }

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
     * 从 Session 获取当前用户ID
     */
    private Integer getUserId(HttpServletRequest request) {
        if (request == null) return 0;
        Object userId = request.getSession().getAttribute("userId");
        return userId != null ? (Integer) userId : 0;
    }

    /**
     * 从 Session 获取当前用户名
     */
    private String getUsername(HttpServletRequest request) {
        if (request == null) return "system";
        Object username = request.getSession().getAttribute("username");
        return username != null ? username.toString() : "system";
    }
}
