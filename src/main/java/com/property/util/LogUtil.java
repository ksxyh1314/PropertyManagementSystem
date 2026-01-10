package com.property.util;

import java.sql.*;
import javax.servlet.http.HttpServletRequest;

/**
 * 系统日志工具类
 * 功能：统一记录所有重要操作到 operation_logs 表
 */
public class LogUtil {

    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=PropertyManagementSystem;encrypt=false";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "123456";

    /**
     * 核心日志记录方法（✅ 修改为返回 boolean）
     * @param userId 用户ID
     * @param username 用户名
     * @param operationType 操作类型（login/logout/user_add/user_update等）
     * @param operationDesc 操作描述
     * @param ipAddress IP地址
     * @return 是否记录成功
     */
    public static boolean log(Integer userId, String username, String operationType,
                              String operationDesc, String ipAddress) {

        String sql = "INSERT INTO operation_logs (user_id, username, operation_type, operation_desc, ip_address) " +
                "VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            System.out.println("🔥 LogUtil.log 被调用:");
            System.out.println("   - userId: " + userId);
            System.out.println("   - username: " + username);
            System.out.println("   - operationType: " + operationType);
            System.out.println("   - operationDesc: " + operationDesc);
            System.out.println("   - ipAddress: " + ipAddress);

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            System.out.println("✅ 数据库连接成功");

            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, userId != null ? userId : 0);
            pstmt.setString(2, username != null ? username : "anonymous");
            pstmt.setString(3, operationType);
            pstmt.setString(4, operationDesc);
            pstmt.setString(5, ipAddress != null ? ipAddress : "unknown");

            System.out.println("📝 准备执行 SQL: " + sql);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ 日志记录成功，影响行数: " + rows);
                return true;
            } else {
                System.err.println("❌ 日志记录失败，影响行数为 0");
                return false;
            }

        } catch (Exception e) {
            // 日志记录失败不应影响业务，只打印错误
            System.err.println("❌ 记录日志失败: " + e.getMessage());
            e.printStackTrace();
            return false; // ✅ 返回 false
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
                System.out.println("🔒 数据库连接已关闭");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取客户端真实IP（支持代理和负载均衡）
     * @param request HttpServletRequest
     * @return IP地址
     */
    public static String getClientIP(HttpServletRequest request) {
        if (request == null) {
            return "127.0.0.1";
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 处理多个代理的情况，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    // ==================== 用户管理日志 ====================

    /**
     * 记录用户登录（简化版）
     */
    public static boolean logLogin(Integer userId, String username, HttpServletRequest request) {
        return log(userId, username, "login", "用户登录系统", getClientIP(request));
    }

    /**
     * 记录用户登录（完整版：支持成功/失败）
     */
    public static boolean logLogin(Integer userId, String username, boolean success, HttpServletRequest request) {
        String desc = success ?
                "用户登录成功" :
                "用户登录失败：用户名或密码错误";
        return log(userId, username, "login", desc, getClientIP(request));
    }

    /**
     * 记录用户登出
     */
    public static boolean logLogout(Integer userId, String username, HttpServletRequest request) {
        return log(userId, username, "logout", "用户退出系统", getClientIP(request));
    }

    /**
     * 记录用户登出（从 request 中自动获取用户信息）
     */
    public static boolean logLogout(HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        String username = (String) request.getSession().getAttribute("username");
        return log(userId, username, "logout", "用户退出系统", getClientIP(request));
    }

    /**
     * 记录用户注册
     */
    public static boolean logRegister(String username, String realName, HttpServletRequest request) {
        return log(0, username, "register", "新用户注册：" + realName, getClientIP(request));
    }

    /**
     * 记录修改密码
     */
    public static boolean logChangePassword(HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        String username = (String) request.getSession().getAttribute("username");
        return log(userId, username, "change_password", "修改密码", getClientIP(request));
    }

    // ==================== 业主管理日志 ====================

    /**
     * 记录添加业主
     */
    public static boolean logAddOwner(String ownerId, String ownerName, HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        String username = (String) request.getSession().getAttribute("username");
        return log(userId, username, "owner_add",
                "添加业主：" + ownerName + "（" + ownerId + "）", getClientIP(request));
    }

    /**
     * 记录修改业主
     */
    public static boolean logUpdateOwner(String ownerId, String ownerName, HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        String username = (String) request.getSession().getAttribute("username");
        return log(userId, username, "owner_update",
                "修改业主信息：" + ownerName + "（" + ownerId + "）", getClientIP(request));
    }

    /**
     * 记录删除业主
     */
    public static boolean logDeleteOwner(String ownerId, String ownerName, HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        String username = (String) request.getSession().getAttribute("username");
        return log(userId, username, "owner_delete",
                "删除业主：" + ownerName + "（" + ownerId + "）", getClientIP(request));
    }

    // ==================== 房屋管理日志 ====================

    /**
     * 记录添加房屋
     */
    public static boolean logAddHouse(String houseId, String buildingNo, HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        String username = (String) request.getSession().getAttribute("username");
        return log(userId, username, "house_add",
                "添加房屋：" + buildingNo + "栋-" + houseId, getClientIP(request));
    }

    /**
     * 记录修改房屋
     */
    public static boolean logUpdateHouse(String houseId, HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        String username = (String) request.getSession().getAttribute("username");
        return log(userId, username, "house_update",
                "修改房屋信息：" + houseId, getClientIP(request));
    }

    /**
     * 记录删除房屋
     */
    public static boolean logDeleteHouse(String houseId, HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        String username = (String) request.getSession().getAttribute("username");
        return log(userId, username, "house_delete",
                "删除房屋：" + houseId, getClientIP(request));
    }

    /**
     * 记录分配业主
     */
    public static boolean logAssignOwner(String houseId, String ownerId, String ownerName, HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        String username = (String) request.getSession().getAttribute("username");
        return log(userId, username, "house_assign",
                "房屋" + houseId + "分配给业主：" + ownerName + "（" + ownerId + "）", getClientIP(request));
    }

    // ==================== 收费管理日志 ====================

    /**
     * 记录生成账单
     */
    public static boolean logGenerateBill(String itemName, int count, HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        String username = (String) request.getSession().getAttribute("username");
        return log(userId, username, "bill_generate",
                "批量生成账单：" + itemName + "，共" + count + "条", getClientIP(request));
    }

    /**
     * 记录缴费
     */
    public static boolean logPayment(int recordId, String ownerId, double amount, HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        String username = (String) request.getSession().getAttribute("username");
        return log(userId, username, "payment",
                "业主" + ownerId + "缴费，账单号：" + recordId + "，金额：" + amount + "元",
                getClientIP(request));
    }

    /**
     * 记录退费
     */
    public static boolean logRefund(int recordId, String ownerId, double amount, HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        String username = (String) request.getSession().getAttribute("username");
        return log(userId, username, "refund",
                "业主" + ownerId + "退费，账单号：" + recordId + "，金额：" + amount + "元",
                getClientIP(request));
    }

    // ==================== 报修管理日志 ====================

    /**
     * 记录提交报修
     */
    public static boolean logSubmitRepair(int repairId, String ownerId, String repairType, HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        String username = (String) request.getSession().getAttribute("username");
        return log(userId, username, "repair_submit",
                "业主" + ownerId + "提交报修：" + repairType + "（ID:" + repairId + "）",
                getClientIP(request));
    }

    /**
     * 记录受理报修
     */
    public static boolean logAcceptRepair(int repairId, HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        String username = (String) request.getSession().getAttribute("username");
        return log(userId, username, "repair_accept",
                "受理报修：ID=" + repairId, getClientIP(request));
    }

    /**
     * 记录完成报修
     */
    public static boolean logCompleteRepair(int repairId, HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        String username = (String) request.getSession().getAttribute("username");
        return log(userId, username, "repair_complete",
                "完成报修：ID=" + repairId, getClientIP(request));
    }

    // ==================== 公告管理日志 ====================

    /**
     * 记录发布公告
     */
    public static boolean logPublishAnnouncement(int announcementId, String title, HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        String username = (String) request.getSession().getAttribute("username");
        return log(userId, username, "announcement_publish",
                "发布公告：" + title + "（ID:" + announcementId + "）", getClientIP(request));
    }

    /**
     * 记录修改公告
     */
    public static boolean logUpdateAnnouncement(int announcementId, String title, HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        String username = (String) request.getSession().getAttribute("username");
        return log(userId, username, "announcement_update",
                "修改公告：" + title + "（ID:" + announcementId + "）", getClientIP(request));
    }

    /**
     * 记录删除公告
     */
    public static boolean logDeleteAnnouncement(int announcementId, String title, HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        String username = (String) request.getSession().getAttribute("username");
        return log(userId, username, "announcement_delete",
                "删除公告：" + title + "（ID:" + announcementId + "）", getClientIP(request));
    }
}
