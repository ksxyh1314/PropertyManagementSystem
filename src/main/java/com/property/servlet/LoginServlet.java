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
     * 用户登录
     */
    public void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = getStringParameter(req, "username");
        String password = getStringParameter(req, "password");

        if (username == null || username.isEmpty()) {
            writeError(resp, "用户名不能为空");
            return;
        }
        if (password == null || password.isEmpty()) {
            writeError(resp, "密码不能为空");
            return;
        }

        try {
            User user = userService.login(username, password);
            if (user == null) {
                writeError(resp, "用户名或密码错误");
                return;
            }

            // 检查账号状态
            if (!user.isActive()) {
                writeError(resp, "账号已被禁用，请联系管理员");
                return;
            }

            // 保存用户信息到Session
            HttpSession session = req.getSession();
            session.setAttribute("currentUser", user);
            session.setMaxInactiveInterval(30 * 60); // 30分钟

            logger.info("用户登录成功：{} - {}", username, user.getRealName());

            // 根据角色返回不同的跳转页面
            String redirectUrl = getRedirectUrl(user.getUserRole());
            writeSuccess(resp, "登录成功", redirectUrl);

        } catch (Exception e) {
            logger.error("登录失败", e);
            writeError(resp, "登录失败：" + e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    public void logout(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("currentUser");
            if (user != null) {
                logger.info("用户登出：{}", user.getUsername());
            }
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/login.jsp");
    }

    /**
     * 修改密码
     */
    public void changePassword(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
            return;
        }

        User currentUser = getCurrentUser(req);
        String oldPassword = getStringParameter(req, "oldPassword");
        String newPassword = getStringParameter(req, "newPassword");
        String confirmPassword = getStringParameter(req, "confirmPassword");

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
                writeSuccess(resp, "密码修改成功，请重新登录");
                // 清除Session
                HttpSession session = req.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
            } else {
                writeError(resp, "密码修改失败");
            }
        } catch (IllegalArgumentException e) {
            writeError(resp, e.getMessage());
        } catch (Exception e) {
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
