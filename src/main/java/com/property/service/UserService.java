package com.property.service;

import com.property.dao.UserDao;
import com.property.entity.User;
import com.property.util.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户服务类
 */
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private UserDao userDao = new UserDao();

    /**
     * 用户登录
     */
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        // MD5加密密码
        String encryptedPassword = MD5Util.encrypt(password);

        User user = userDao.login(username, encryptedPassword);
        if (user == null) {
            logger.warn("登录失败：用户名或密码错误 - {}", username);
            return null;
        }

        // 更新最后登录时间
        userDao.updateLastLogin(user.getUserId());
        logger.info("用户登录成功：{} - {}", username, user.getRealName());

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
     * 分页查询用户
     */
    public Map<String, Object> findByPage(int pageNum, int pageSize, String keyword) {
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1) pageSize = 10;

        List<User> list = userDao.findByPage(pageNum, pageSize, keyword);
        long total = userDao.count(keyword);
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

        // 验证密码强度
        if (!isValidPassword(newPassword)) {
            throw new IllegalArgumentException("密码必须8位以上，且包含字母和数字");
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

        // 验证密码强度
        if (!isValidPassword(user.getPassword())) {
            throw new IllegalArgumentException("密码必须8位以上，且包含字母和数字");
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
}
