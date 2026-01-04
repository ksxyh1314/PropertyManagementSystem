package com.property.servlet;

import com.google.gson.*;
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
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 基础Servlet类
 * 提供通用的请求处理方法
 */
public class BaseServlet extends HttpServlet {
    protected static final Logger logger = LoggerFactory.getLogger(BaseServlet.class);

    // Gson 配置（支持各种日期类型）
    protected static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
                @Override
                public JsonElement serialize(Date date, Type type, JsonSerializationContext context) {
                    if (date == null) return JsonNull.INSTANCE;
                    return new JsonPrimitive(new SimpleDateFormat("yyyy-MM-dd").format(date));
                }
            })
            .registerTypeAdapter(java.sql.Date.class, new JsonSerializer<java.sql.Date>() {
                @Override
                public JsonElement serialize(java.sql.Date date, Type type, JsonSerializationContext context) {
                    if (date == null) return JsonNull.INSTANCE;
                    return new JsonPrimitive(new SimpleDateFormat("yyyy-MM-dd").format(date));
                }
            })
            .registerTypeAdapter(java.sql.Timestamp.class, new JsonSerializer<java.sql.Timestamp>() {
                @Override
                public JsonElement serialize(java.sql.Timestamp timestamp, Type type, JsonSerializationContext context) {
                    if (timestamp == null) return JsonNull.INSTANCE;
                    return new JsonPrimitive(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp));
                }
            })
            .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                @Override
                public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext context) {
                    if (localDateTime == null) return JsonNull.INSTANCE;
                    return new JsonPrimitive(localDateTime.format(formatter));
                }
            })
            .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
                private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                @Override
                public JsonElement serialize(LocalDate localDate, Type type, JsonSerializationContext context) {
                    if (localDate == null) return JsonNull.INSTANCE;
                    return new JsonPrimitive(localDate.format(formatter));
                }
            })
            .serializeNulls()
            .create();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 设置请求和响应编码
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        // ✅ 同时支持 method 和 action 参数
        String methodName = req.getParameter("method");
        if (methodName == null || methodName.trim().isEmpty()) {
            methodName = req.getParameter("action");
        }
        if (methodName == null || methodName.trim().isEmpty()) {
            methodName = "index";
        }

        logger.info("========================================");
        logger.info("请求 Servlet: {}", this.getClass().getSimpleName());
        logger.info("Method/Action: {}", methodName);
        logger.info("HTTP Method: {}", req.getMethod());
        logger.info("Request URI: {}", req.getRequestURI());
        logger.info("========================================");

        try {
            // 通过反射调用对应的方法
            Method method = this.getClass().getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
            method.invoke(this, req, resp);

            logger.info("✅ 请求处理成功: {}", methodName);

        } catch (NoSuchMethodException e) {
            logger.error("❌ 未找到方法: {}", methodName);
            writeError(resp, 404, "不支持的操作: " + methodName);

        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable cause = e.getCause();
            logger.error("❌ 方法执行异常: {}", methodName, cause);

            String errorMsg = cause != null ? cause.getMessage() : e.getMessage();
            if (errorMsg == null || errorMsg.isEmpty()) {
                errorMsg = "操作失败，请联系管理员";
            }

            writeError(resp, 500, errorMsg);

        } catch (Exception e) {
            logger.error("❌ 请求处理失败：method=" + methodName, e);
            writeError(resp, 500, "服务器内部错误：" + e.getMessage());
        }
    }

    /**
     * 获取当前登录用户
     */
    protected User getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            // ✅ 先尝试从 session 获取完整的 User 对象
            User user = (User) session.getAttribute("user");
            if (user == null) {
                user = (User) session.getAttribute("currentUser");
            }

            // 🔥 如果没有完整的 User 对象，从 session 属性构建一个
            if (user == null) {
                Integer userId = (Integer) session.getAttribute("userId");
                String username = (String) session.getAttribute("username");
                String role = (String) session.getAttribute("role");
                String realName = (String) session.getAttribute("realName");

                if (userId != null && username != null && role != null) {
                    user = new User();
                    user.setUserId(userId);
                    user.setUsername(username);
                    user.setUserRole(role);  // 🔥 从 session 的 role 设置到 userRole
                    user.setRealName(realName);

                    logger.debug("✅ 从 Session 属性构建 User 对象: {} ({})", username, role);
                }
            }

            return user;
        }
        return null;
    }
 /**
     * 检查用户是否登录
     */
    protected boolean checkLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = getCurrentUser(req);
        if (user == null) {
            String requestedWith = req.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equals(requestedWith)) {
                writeError(resp, 401, "未登录或登录已过期");
            } else {
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
            }
            return false;
        }
        return true;
    }

    /**
     * 检查登录并返回用户对象
     */
    protected User checkLoginAndGetUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = getCurrentUser(req);

        if (currentUser == null) {
            logger.warn("❌ 用户未登录");
            writeError(resp, 401, "未登录或登录已过期，请重新登录");
            return null;
        }

        logger.info("✅ 用户验证通过：{} - {} ({})",
                currentUser.getUsername(),
                currentUser.getRealName() != null ? currentUser.getRealName() : "未设置",
                currentUser.getUserRole());

        return currentUser;
    }

    /**
     * 检查业主权限并返回用户对象
     */
    protected User checkOwnerLoginAndGetUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = checkLoginAndGetUser(req, resp);

        if (currentUser == null) {
            return null;
        }

        if (!"owner".equals(currentUser.getUserRole())) {
            logger.warn("❌ 角色验证失败：{} 不是业主角色", currentUser.getUsername());
            writeError(resp, 403, "无权限访问，仅限业主使用");
            return null;
        }

        return currentUser;
    }

    /**
     * 检查用户权限
     */
    protected boolean checkRole(HttpServletRequest req, HttpServletResponse resp, String... roles) throws IOException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            logger.warn("❌ 用户未登录");
            String requestedWith = req.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equals(requestedWith)) {
                writeError(resp, 401, "未登录或登录已过期");
            } else {
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
            }
            return false;
        }

        // 🔥 直接从 Session 获取 role
        String userRole = (String) session.getAttribute("role");
        String username = (String) session.getAttribute("username");

        if (userRole == null) {
            logger.warn("❌ 用户角色信息缺失");
            writeError(resp, 403, "用户角色信息缺失");
            return false;
        }

        // 检查角色权限
        for (String role : roles) {
            if (role.equals(userRole)) {
                logger.info("✅ 权限验证通过：{} - {}", username, userRole);
                return true;
            }
        }

        // 没有权限
        logger.warn("❌ 权限不足：{} - {} (需要: {})",
                username, userRole, String.join(",", roles));

        String requestedWith = req.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(requestedWith)) {
            writeError(resp, 403, "没有权限访问");
        } else {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "没有权限访问");
        }
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
     * 返回成功结果（只有消息）
     */
    protected void writeSuccess(HttpServletResponse resp, String message) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("success", true);
        result.put("message", message);
        writeJson(resp, result);
    }

    /**
     * 返回成功结果（带数据）
     */
    protected void writeSuccess(HttpServletResponse resp, Object data) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("success", true);
        result.put("message", "success");
        result.put("data", data);
        writeJson(resp, result);
    }

    /**
     * 返回成功结果（带消息和数据）
     */
    protected void writeSuccess(HttpServletResponse resp, String message, Object data) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("success", true);
        result.put("message", message);
        result.put("data", data);
        writeJson(resp, result);
    }

    /**
     * 返回失败结果
     */
    protected void writeError(HttpServletResponse resp, String message) throws IOException {
        writeError(resp, 500, message);
    }

    /**
     * 返回失败结果（带错误码）
     */
    protected void writeError(HttpServletResponse resp, int code, String message) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("success", false);
        result.put("message", message);
        result.put("data", null);
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
                logger.warn("参数 {} 不是有效的整数: {}", name, value);
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
     * 获取长整型参数
     */
    protected Long getLongParameter(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        if (value != null && !value.trim().isEmpty()) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                logger.warn("参数 {} 不是有效的长整数: {}", name, value);
                return null;
            }
        }
        return null;
    }

    /**
     * 获取长整型参数（带默认值）
     */
    protected long getLongParameter(HttpServletRequest req, String name, long defaultValue) {
        Long value = getLongParameter(req, name);
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

    /**
     * 获取布尔参数
     */
    protected Boolean getBooleanParameter(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        if (value != null && !value.trim().isEmpty()) {
            return "true".equalsIgnoreCase(value) || "1".equals(value);
        }
        return null;
    }

    /**
     * 获取布尔参数（带默认值）
     */
    protected boolean getBooleanParameter(HttpServletRequest req, String name, boolean defaultValue) {
        Boolean value = getBooleanParameter(req, name);
        return value != null ? value : defaultValue;
    }

    /**
     * 验证参数是否为空
     */
    protected boolean validateRequired(HttpServletResponse resp, String... params) throws IOException {
        for (String param : params) {
            if (param == null || param.trim().isEmpty()) {
                writeError(resp, 400, "参数不能为空");
                return false;
            }
        }
        return true;
    }

    /**
     * 记录操作日志
     */
    protected void logOperation(HttpServletRequest req, String operation) {
        User user = getCurrentUser(req);
        String username = user != null ? user.getUsername() : "未登录";
        String ip = getClientIP(req);
        logger.info("用户操作 - 用户: {}, IP: {}, 操作: {}", username, ip, operation);
    }

    /**
     * 获取客户端IP地址
     */
    protected String getClientIP(HttpServletRequest req) {
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 检查业主权限（业主只能访问自己的数据）
     */
    protected void checkOwnerPermission(HttpServletRequest req, String ownerId) throws ServletException {
        User currentUser = getCurrentUser(req);

        if (currentUser == null) {
            throw new ServletException("未登录");
        }

        if (!"owner".equals(currentUser.getUserRole())) {
            throw new ServletException("无权限访问");
        }

        if (!currentUser.getUsername().equals(ownerId)) {
            throw new ServletException("无权限访问其他业主数据");
        }
    }
}
