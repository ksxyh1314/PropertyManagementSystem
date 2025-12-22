package com.property.filter;

import com.property.entity.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 登录过滤器
 * 拦截需要登录才能访问的页面
 */
@WebFilter(urlPatterns = {"/admin/*", "/owner/*", "/finance/*"})
public class LoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 获取请求URI
        String uri = req.getRequestURI();

        // 静态资源放行
        if (uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".png") ||
                uri.endsWith(".jpg") || uri.endsWith(".jpeg") || uri.endsWith(".gif")) {
            chain.doFilter(request, response);
            return;
        }

        // 检查用户是否登录
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        // 检查用户角色权限
        User user = (User) session.getAttribute("currentUser");
        String userRole = user.getUserRole();

        if (uri.contains("/admin/") && !"admin".equals(userRole)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "没有权限访问");
            return;
        }
        if (uri.contains("/owner/") && !"owner".equals(userRole)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "没有权限访问");
            return;
        }
        if (uri.contains("/finance/") && !"finance".equals(userRole)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "没有权限访问");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
