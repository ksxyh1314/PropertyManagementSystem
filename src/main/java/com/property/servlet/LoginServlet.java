package com.property.servlet;

import com.property.entity.User;
import com.property.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 登录Servlet
 */
@WebServlet("/login")
public class LoginServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private UserService userService = new UserService();

    /**
     * 用户登录 (修改版：增加身份验证 + 禁用账号提示)
     */
    public void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("========================================");
        System.out.println("【用户登录】");
        System.out.println("请求时间: " + new java.util.Date());
        System.out.println("========================================");

        String username = getStringParameter(req, "username");
        String password = getStringParameter(req, "password");
        String role = getStringParameter(req, "role");

        System.out.println("登录参数：");
        System.out.println("  username: " + username);
        System.out.println("  password: " + (password != null ? "******" : "null"));
        System.out.println("  role: " + role);

        // 1. 参数校验
        if (username == null || username.isEmpty()) {
            System.out.println("❌ 用户名为空");
            writeError(resp, "用户名不能为空");
            return;
        }
        if (password == null || password.isEmpty()) {
            System.out.println("❌ 密码为空");
            writeError(resp, "密码不能为空");
            return;
        }
        if (role == null || role.isEmpty()) {
            System.out.println("❌ 身份为空");
            writeError(resp, "请选择登录身份");
            return;
        }

        try {
            System.out.println("\n开始验证用户...");

            // 2. 调用 Service 验证用户
            User user = userService.login(username, password, role);

            if (user == null) {
                System.out.println("❌ 用户名、密码错误或身份不匹配");
                writeError(resp, "用户名、密码错误或身份不匹配");
                return;
            }

            System.out.println("✅ 用户验证成功: " + user.getUsername() + " - " + user.getRealName());

            // 🔥 3. 检查账号状态（如果被禁用，返回 403）
            if (!user.isActive()) {
                System.out.println("⚠️ 账号已被禁用: " + user.getUsername());
                System.out.println("========================================");

                // 🔥 返回 403 状态码，前端会显示橙色警告
                writeError(resp, 403, "该账号已被禁用，无法登录");
                return;
            }

            System.out.println("✅ 账号状态正常");

            // 4. 保存用户信息到 Session
            HttpSession session = req.getSession();

            // 主要的用户对象
            session.setAttribute("currentUser", user);
            session.setAttribute("user", user);

            // 常用的用户属性
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("realName", user.getRealName());
            session.setAttribute("role", user.getUserRole());
            session.setAttribute("userRole", user.getUserRole());

            // 设置 Session 超时时间（30分钟）
            session.setMaxInactiveInterval(30 * 60);

            System.out.println("\n✅ Session 创建成功:");
            System.out.println("  Session ID: " + session.getId());
            System.out.println("  userId: " + user.getUserId());
            System.out.println("  username: " + user.getUsername());
            System.out.println("  realName: " + user.getRealName());
            System.out.println("  role: " + user.getUserRole());

            logger.info("用户登录成功：{} - {} (身份: {})", username, user.getRealName(), role);

            // 5. 根据角色返回不同的跳转页面
            String redirectUrl = getRedirectUrl(user.getUserRole());
            System.out.println("  跳转页面: " + redirectUrl);
            System.out.println("========================================");

            writeSuccess(resp, "登录成功", redirectUrl);

        } catch (Exception e) {
            System.err.println("❌ 登录失败: " + e.getMessage());
            e.printStackTrace();
            System.out.println("========================================");
            logger.error("登录失败", e);
            writeError(resp, "登录失败：" + e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    public void logout(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("========================================");
        System.out.println("【用户登出】");
        System.out.println("========================================");

        HttpSession session = req.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("currentUser");
            if (user != null) {
                System.out.println("用户登出: " + user.getUsername() + " - " + user.getRealName());
                logger.info("用户登出：{} - {}", user.getUsername(), user.getRealName());
            }
            session.invalidate();
            System.out.println("✅ Session 已销毁");
        } else {
            System.out.println("⚠️ Session 不存在");
        }

        System.out.println("========================================");
        resp.sendRedirect(req.getContextPath() + "/login.jsp");
    }

    /**
     * 修改密码
     */
    public void changePassword(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("========================================");
        System.out.println("【修改密码】");
        System.out.println("========================================");

        if (!checkLogin(req, resp)) {
            System.out.println("❌ 用户未登录");
            return;
        }

        User currentUser = getCurrentUser(req);
        String oldPassword = getStringParameter(req, "oldPassword");
        String newPassword = getStringParameter(req, "newPassword");
        String confirmPassword = getStringParameter(req, "confirmPassword");

        System.out.println("用户: " + currentUser.getUsername());
        System.out.println("旧密码: " + (oldPassword != null ? "******" : "null"));
        System.out.println("新密码: " + (newPassword != null ? "******" : "null"));

        if (oldPassword == null || oldPassword.isEmpty()) {
            writeError(resp, "原密码不能为空");
            return;
        }
        if (newPassword == null || newPassword.isEmpty()) {
            writeError(resp, "新密码不能为空");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            writeError(resp, "两次输入的密码不一致");
            return;
        }

        try {
            boolean success = userService.changePassword(currentUser.getUserId(), oldPassword, newPassword);
            if (success) {
                System.out.println("✅ 密码修改成功");
                System.out.println("========================================");
                writeSuccess(resp, "密码修改成功，请重新登录");

                // 清除Session
                HttpSession session = req.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
            } else {
                System.out.println("❌ 密码修改失败");
                System.out.println("========================================");
                writeError(resp, "密码修改失败");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("❌ 参数错误: " + e.getMessage());
            System.out.println("========================================");
            writeError(resp, e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ 修改密码失败: " + e.getMessage());
            e.printStackTrace();
            System.out.println("========================================");
            logger.error("修改密码失败", e);
            writeError(resp, "修改密码失败：" + e.getMessage());
        }
    }

    /**
     * 根据角色获取跳转URL
     */
    private String getRedirectUrl(String role) {
        switch (role) {
            case "admin":
                return "admin/index.jsp";
            case "owner":
                return "owner/index.jsp";
            case "finance":
                return "finance/index.jsp";
            default:
                return "index.jsp";
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        // 同时支持 method 和 action 参数
        String methodName = req.getParameter("method");
        if (methodName == null || methodName.trim().isEmpty()) {
            methodName = req.getParameter("action");
        }
        if (methodName == null || methodName.trim().isEmpty()) {
            methodName = "login";
        }

        try {
            Method method = this.getClass().getMethod(methodName,
                    HttpServletRequest.class, HttpServletResponse.class);
            method.invoke(this, req, resp);
        } catch (Exception e) {
            logger.error("处理请求失败：method=" + methodName, e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "服务器内部错误");
        }
    }
}
