package com.property.filter;

import com.property.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 登录过滤器
 * 拦截需要登录才能访问的页面
 */
@WebFilter(urlPatterns = {"/admin/*", "/owner/*", "/finance/*"})
public class LoginFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LoginFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("✅ LoginFilter 初始化成功");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 获取请求URI
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();

        logger.debug("🔍 LoginFilter 拦截请求: {}", uri);

        // ========================================
        // 🔥 1. 静态资源放行
        // ========================================
        if (uri.endsWith(".css") || uri.endsWith(".js") ||
                uri.endsWith(".png") || uri.endsWith(".jpg") ||
                uri.endsWith(".jpeg") || uri.endsWith(".gif") ||
                uri.endsWith(".ico") || uri.endsWith(".woff") ||
                uri.endsWith(".woff2") || uri.endsWith(".ttf")) {
            chain.doFilter(request, response);
            return;
        }

        // ========================================
        // 🔥 2. 检查用户是否登录
        // ========================================
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            logger.warn("⚠️ 未登录访问: {}", uri);

            // 🔥 判断是否为 AJAX 请求
            if (isAjaxRequest(req)) {
                // ✅ AJAX 请求返回 JSON
                handleAjaxUnauthorized(resp, "未登录或登录已过期，请重新登录");
            } else {
                // ✅ 普通请求重定向到登录页面
                resp.sendRedirect(contextPath + "/login.jsp");
            }
            return;
        }

        // ========================================
        // 🔥 3. 检查用户角色权限
        // ========================================
        User user = (User) session.getAttribute("currentUser");
        String userRole = user.getUserRole();

        // 检查管理员权限
        if (uri.contains("/admin/") && !"admin".equals(userRole)) {
            logger.warn("⚠️ 无权限访问: {} (角色: {})", uri, userRole);

            if (isAjaxRequest(req)) {
                handleAjaxForbidden(resp, "没有权限访问管理员功能");
            } else {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "没有权限访问");
            }
            return;
        }

        // 检查业主权限
        if (uri.contains("/owner/") && !"owner".equals(userRole)) {
            logger.warn("⚠️ 无权限访问: {} (角色: {})", uri, userRole);

            if (isAjaxRequest(req)) {
                handleAjaxForbidden(resp, "没有权限访问业主功能");
            } else {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "没有权限访问");
            }
            return;
        }

        // 检查财务权限
        if (uri.contains("/finance/") && !"finance".equals(userRole)) {
            logger.warn("⚠️ 无权限访问: {} (角色: {})", uri, userRole);

            if (isAjaxRequest(req)) {
                handleAjaxForbidden(resp, "没有权限访问财务功能");
            } else {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "没有权限访问");
            }
            return;
        }

        // ========================================
        // 🔥 4. 放行请求
        // ========================================
        logger.debug("✅ 请求放行: {} (用户: {}, 角色: {})", uri, user.getUsername(), userRole);
        chain.doFilter(request, response);
    }

    /**
     * 判断是否为 AJAX 请求
     */
    private boolean isAjaxRequest(HttpServletRequest request) {
        // 方式1：检查请求头 X-Requested-With
        String ajaxHeader = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(ajaxHeader)) {
            return true;
        }

        // 方式2：检查 Accept 头是否包含 application/json
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            return true;
        }

        // 方式3：检查 Content-Type 是否为 application/json
        String contentType = request.getHeader("Content-Type");
        if (contentType != null && contentType.contains("application/json")) {
            return true;
        }

        return false;
    }

    /**
     * 处理 AJAX 请求的未登录响应（401）
     */
    private void handleAjaxUnauthorized(HttpServletResponse response, String message)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401

        PrintWriter out = response.getWriter();
        out.write("{");
        out.write("\"success\": false,");
        out.write("\"code\": 401,");
        out.write("\"message\": \"" + message + "\"");
        out.write("}");
        out.flush();
        out.close();
    }

    /**
     * 处理 AJAX 请求的无权限响应（403）
     */
    private void handleAjaxForbidden(HttpServletResponse response, String message)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403

        PrintWriter out = response.getWriter();
        out.write("{");
        out.write("\"success\": false,");
        out.write("\"code\": 403,");
        out.write("\"message\": \"" + message + "\"");
        out.write("}");
        out.flush();
        out.close();
    }

    @Override
    public void destroy() {
        logger.info("🔴 LoginFilter 销毁");
    }
}
