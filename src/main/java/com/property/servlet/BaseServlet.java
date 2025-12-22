package com.property.servlet;

import com.google.gson.Gson;
import com.property.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 基础Servlet类
 * 提供通用的请求处理方法
 */
public class BaseServlet extends HttpServlet {
    protected static final Logger logger = LoggerFactory.getLogger(BaseServlet.class);
    protected Gson gson = new Gson();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 设置请求和响应编码
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        // 获取请求的方法名
        String methodName = req.getParameter("method");
        if (methodName == null || methodName.trim().isEmpty()) {
            methodName = "index";
        }

        try {
            // 通过反射调用对应的方法
            Method method = this.getClass().getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
            method.invoke(this, req, resp);
        } catch (Exception e) {
            logger.error("处理请求失败：method=" + methodName, e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器内部错误");
        }
    }

    /**
     * 获取当前登录用户
     */
    protected User getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("currentUser");
        }
        return null;
    }

    /**
     * 检查用户是否登录
     */
    protected boolean checkLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = getCurrentUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return false;
        }
        return true;
    }

    /**
     * 检查用户权限
     */
    protected boolean checkRole(HttpServletRequest req, HttpServletResponse resp, String... roles) throws IOException {
        User user = getCurrentUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return false;
        }

        for (String role : roles) {
            if (role.equals(user.getUserRole())) {
                return true;
            }
        }

        resp.sendError(HttpServletResponse.SC_FORBIDDEN, "没有权限访问");
        return false;
    }

    /**
     * 返回JSON数据
     */
    protected void writeJson(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.print(gson.toJson(data));
        out.flush();
    }

    /**
     * 返回成功结果
     */
    protected void writeSuccess(HttpServletResponse resp, String message) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", message);
        writeJson(resp, result);
    }

    /**
     * 返回成功结果（带数据）
     */
    protected void writeSuccess(HttpServletResponse resp, String message, Object data) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", message);
        result.put("data", data);
        writeJson(resp, result);
    }

    /**
     * 返回失败结果
     */
    protected void writeError(HttpServletResponse resp, String message) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        writeJson(resp, result);
    }

    /**
     * 获取整数参数
     */
    protected Integer getIntParameter(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        if (value != null && !value.trim().isEmpty()) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 获取整数参数（带默认值）
     */
    protected int getIntParameter(HttpServletRequest req, String name, int defaultValue) {
        Integer value = getIntParameter(req, name);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取字符串参数
     */
    protected String getStringParameter(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        return value != null ? value.trim() : null;
    }

    /**
     * 获取字符串参数（带默认值）
     */
    protected String getStringParameter(HttpServletRequest req, String name, String defaultValue) {
        String value = getStringParameter(req, name);
        return value != null && !value.isEmpty() ? value : defaultValue;
    }
}
