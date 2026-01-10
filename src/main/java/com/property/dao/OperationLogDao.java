package com.property.dao;

import com.property.entity.OperationLog;
import com.property.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作日志 DAO
 */
public class OperationLogDao {

    /**
     * 分页查询操作日志
     */
    public List<OperationLog> findByPage(int pageNum, int pageSize, String keyword,
                                         String operationType, String startDate, String endDate) {
        List<OperationLog> logs = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM operation_logs WHERE 1=1");

        List<Object> params = new ArrayList<>();

        // 关键字搜索（用户名或操作描述）
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (username LIKE ? OR operation_desc LIKE ?)");
            String likeKeyword = "%" + keyword + "%";
            params.add(likeKeyword);
            params.add(likeKeyword);
        }

        // 🔥 操作类型筛选（改为模糊匹配）
        if (operationType != null && !operationType.trim().isEmpty()) {
            sql.append(" AND operation_type LIKE ?");
            params.add("%" + operationType + "%");
        }

        // 时间范围筛选
        if (startDate != null && !startDate.trim().isEmpty()) {
            sql.append(" AND operation_time >= ?");
            params.add(startDate + " 00:00:00");
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            sql.append(" AND operation_time <= ?");
            params.add(endDate + " 23:59:59");
        }

        // 排序和分页
        sql.append(" ORDER BY operation_time DESC");
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((pageNum - 1) * pageSize);
        params.add(pageSize);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                OperationLog log = new OperationLog();
                log.setLogId(rs.getInt("log_id"));
                log.setUserId(rs.getInt("user_id"));
                log.setUsername(rs.getString("username"));
                log.setOperationType(rs.getString("operation_type"));
                log.setOperationDesc(rs.getString("operation_desc"));
                log.setIpAddress(rs.getString("ip_address"));
                log.setOperationTime(rs.getTimestamp("operation_time"));
                logs.add(log);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return logs;
    }

    /**
     * 统计日志总数
     */
    public long count(String keyword, String operationType, String startDate, String endDate) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM operation_logs WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (username LIKE ? OR operation_desc LIKE ?)");
            String likeKeyword = "%" + keyword + "%";
            params.add(likeKeyword);
            params.add(likeKeyword);
        }

        // 🔥 操作类型筛选（改为模糊匹配）
        if (operationType != null && !operationType.trim().isEmpty()) {
            sql.append(" AND operation_type LIKE ?");
            params.add("%" + operationType + "%");
        }

        if (startDate != null && !startDate.trim().isEmpty()) {
            sql.append(" AND operation_time >= ?");
            params.add(startDate + " 00:00:00");
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            sql.append(" AND operation_time <= ?");
            params.add(endDate + " 23:59:59");
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return 0;
    }

    /**
     * 获取操作类型统计
     */
    public List<Map<String, Object>> getOperationTypeStats() {
        List<Map<String, Object>> stats = new ArrayList<>();
        String sql = "SELECT operation_type, COUNT(*) as count " +
                "FROM operation_logs " +
                "GROUP BY operation_type " +
                "ORDER BY count DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> stat = new HashMap<>();
                stat.put("operationType", rs.getString("operation_type"));
                stat.put("count", rs.getInt("count"));
                stats.add(stat);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return stats;
    }

    /**
     * 获取用户操作统计（Top 10）
     */
    public List<Map<String, Object>> getUserOperationStats() {
        List<Map<String, Object>> stats = new ArrayList<>();
        String sql = "SELECT TOP 10 username, COUNT(*) as count " +
                "FROM operation_logs " +
                "GROUP BY username " +
                "ORDER BY count DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> stat = new HashMap<>();
                stat.put("username", rs.getString("username"));
                stat.put("count", rs.getInt("count"));
                stats.add(stat);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return stats;
    }

    /**
     * 根据ID查询日志
     */
    public OperationLog findById(int logId) {
        String sql = "SELECT * FROM operation_logs WHERE log_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, logId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                OperationLog log = new OperationLog();
                log.setLogId(rs.getInt("log_id"));
                log.setUserId(rs.getInt("user_id"));
                log.setUsername(rs.getString("username"));
                log.setOperationType(rs.getString("operation_type"));
                log.setOperationDesc(rs.getString("operation_desc"));
                log.setIpAddress(rs.getString("ip_address"));
                log.setOperationTime(rs.getTimestamp("operation_time"));
                return log;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return null;
    }

    /**
     * 查询所有日志（用于导出）
     */
    public List<OperationLog> findAll(String keyword, String operationType,
                                      String startDate, String endDate) {
        List<OperationLog> logs = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM operation_logs WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (username LIKE ? OR operation_desc LIKE ?)");
            String likeKeyword = "%" + keyword + "%";
            params.add(likeKeyword);
            params.add(likeKeyword);
        }

        // 🔥 操作类型筛选（改为模糊匹配）
        if (operationType != null && !operationType.trim().isEmpty()) {
            sql.append(" AND operation_type LIKE ?");
            params.add("%" + operationType + "%");
        }

        if (startDate != null && !startDate.trim().isEmpty()) {
            sql.append(" AND operation_time >= ?");
            params.add(startDate + " 00:00:00");
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            sql.append(" AND operation_time <= ?");
            params.add(endDate + " 23:59:59");
        }

        sql.append(" ORDER BY operation_time DESC");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                OperationLog log = new OperationLog();
                log.setLogId(rs.getInt("log_id"));
                log.setUserId(rs.getInt("user_id"));
                log.setUsername(rs.getString("username"));
                log.setOperationType(rs.getString("operation_type"));
                log.setOperationDesc(rs.getString("operation_desc"));
                log.setIpAddress(rs.getString("ip_address"));
                log.setOperationTime(rs.getTimestamp("operation_time"));
                logs.add(log);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return logs;
    }
}
