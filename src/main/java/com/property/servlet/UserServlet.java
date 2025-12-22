package com.property.servlet;

import com.property.entity.User;
import com.property.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 用户管理Servlet
 */
@WebServlet("/admin/user")
public class UserServlet extends BaseServlet {
    private UserService userService = new UserService();

    /**
     * 分页查询用户列表
     */
    public void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        int pageNum = getIntParameter(req, "pageNum", 1);
        int pageSize = getIntParameter(req, "pageSize", 10);
        String keyword = getStringParameter(req, "keyword");

        try {
            Map<String, Object> result = userService.findByPage(pageNum, pageSize, keyword);
            writeJson(resp, result);
        } catch (Exception e) {
            logger.error("查询用户列表失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询所有用户
     */
    public void findAll(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        try {
            List<User> users = userService.findAll();
            writeSuccess(resp, "查询成功", users);
        } catch (Exception e) {
            logger.error("查询用户失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID查询用户
     */
    public void findById(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        Integer userId = getIntParameter(req, "userId");
        if (userId == null) {
            writeError(resp, "用户ID不能为空");
            return;
        }

        try {
            User user = userService.findById(userId);
            if (user != null) {
                writeSuccess(resp, "查询成功", user);
            } else {
                writeError(resp, "用户不存在");
            }
        } catch (Exception e) {
            logger.error("查询用户失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 添加用户
     */
    public void add(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        String username = getStringParameter(req, "username");
        String password = getStringParameter(req, "password");
        String realName = getStringParameter(req, "realName");
        String userRole = getStringParameter(req, "userRole");
        String phone = getStringParameter(req, "phone");
        String idCard = getStringParameter(req, "idCard");

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRealName(realName);
        user.setUserRole(userRole);
        user.setPhone(phone);
        user.setIdCard(idCard);
        user.setStatus(1);

        try {
            Integer userId = userService.addUser(user);
            if (userId != null) {
                writeSuccess(resp, "添加用户成功", userId);
            } else {
                writeError(resp, "添加用户失败");
            }
        } catch (IllegalArgumentException e) {
            writeError(resp, e.getMessage());
        } catch (Exception e) {
            logger.error("添加用户失败", e);
            writeError(resp, "添加用户失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户
     */
    public void update(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        Integer userId = getIntParameter(req, "userId");
        if (userId == null) {
            writeError(resp, "用户ID不能为空");
            return;
        }

        String realName = getStringParameter(req, "realName");
        String phone = getStringParameter(req, "phone");
        String idCard = getStringParameter(req, "idCard");
        Integer status = getIntParameter(req, "status");

        User user = new User();
        user.setUserId(userId);
        user.setRealName(realName);
        user.setPhone(phone);
        user.setIdCard(idCard);
        user.setStatus(status);

        try {
            boolean success = userService.updateUser(user);
            if (success) {
                writeSuccess(resp, "更新用户成功");
            } else {
                writeError(resp, "更新用户失败");
            }
        } catch (IllegalArgumentException e) {
            writeError(resp, e.getMessage());
        } catch (Exception e) {
            logger.error("更新用户失败", e);
            writeError(resp, "更新用户失败：" + e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    public void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        Integer userId = getIntParameter(req, "userId");
        if (userId == null) {
            writeError(resp, "用户ID不能为空");
            return;
        }

        try {
            boolean success = userService.deleteUser(userId);
            if (success) {
                writeSuccess(resp, "删除用户成功");
            } else {
                writeError(resp, "删除用户失败");
            }
        } catch (Exception e) {
            logger.error("删除用户失败", e);
            writeError(resp, "删除用户失败：" + e.getMessage());
        }
    }

    /**
     * 重置密码
     */
    public void resetPassword(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        Integer userId = getIntParameter(req, "userId");
        String newPassword = getStringParameter(req, "newPassword");

        if (userId == null) {
            writeError(resp, "用户ID不能为空");
            return;
        }
        if (newPassword == null || newPassword.isEmpty()) {
            writeError(resp, "新密码不能为空");
            return;
        }

        try {
            boolean success = userService.resetPassword(userId, newPassword);
            if (success) {
                writeSuccess(resp, "重置密码成功");
            } else {
                writeError(resp, "重置密码失败");
            }
        } catch (IllegalArgumentException e) {
            writeError(resp, e.getMessage());
        } catch (Exception e) {
            logger.error("重置密码失败", e);
            writeError(resp, "重置密码失败：" + e.getMessage());
        }
    }

    /**
     * 启用/禁用用户
     */
    public void updateStatus(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        Integer userId = getIntParameter(req, "userId");
        Integer status = getIntParameter(req, "status");

        if (userId == null) {
            writeError(resp, "用户ID不能为空");
            return;
        }
        if (status == null) {
            writeError(resp, "状态不能为空");
            return;
        }

        try {
            boolean success = userService.updateStatus(userId, status);
            if (success) {
                writeSuccess(resp, "更新状态成功");
            } else {
                writeError(resp, "更新状态失败");
            }
        } catch (Exception e) {
            logger.error("更新状态失败", e);
            writeError(resp, "更新状态失败：" + e.getMessage());
        }
    }
}
