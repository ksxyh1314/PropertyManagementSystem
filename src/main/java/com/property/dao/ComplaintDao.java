package com.property.dao;

import com.property.entity.Complaint;
import com.property.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 投诉数据访问层 (完整版：新增 updateStatus 支持灵活撤销/驳回)
 */
public class ComplaintDao {

    /**
     * 🔥 新增核心方法：通用状态更新
     * 用于：业主撤销、管理员驳回、状态流转等，支持同时更新回复内容和处理人
     *
     * @param complaintId 投诉ID
     * @param status      新状态 (如 cancelled, closed)
     * @param reply       回复/备注内容
     * @param handlerId   处理人ID (可为 null，例如业主撤销时)
     */
    public boolean updateStatus(Integer complaintId, String status, String reply, Integer handlerId) {
        // 构建 SQL：如果 handlerId 不为空，则更新 handler_id 字段，否则保持原样
        StringBuilder sql = new StringBuilder("UPDATE complaints SET complaint_status = ?, reply = ?, reply_time = GETDATE()");

        if (handlerId != null) {
            sql.append(", handler_id = ?");
        }

        sql.append(" WHERE complaint_id = ?");

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql.toString());

            // 设置参数
            pstmt.setString(1, status);
            pstmt.setString(2, reply);

            int index = 3;
            if (handlerId != null) {
                pstmt.setInt(index++, handlerId);
            }
            pstmt.setInt(index, complaintId);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("❌ DAO updateStatus 失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 提交投诉
     */
    public Map<String, Object> submitComplaint(Complaint complaint) {
        Connection conn = null;
        CallableStatement stmt = null;
        Map<String, Object> result = new HashMap<>();

        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareCall("{CALL sp_submit_complaint(?, ?, ?, ?, ?, ?, ?)}");

            // 输入参数
            stmt.setString(1, complaint.getOwnerId());
            stmt.setString(2, complaint.getComplaintType());
            stmt.setString(3, complaint.getTitle());
            stmt.setString(4, complaint.getContent());
            stmt.setInt(5, complaint.getIsAnonymous() != null ? complaint.getIsAnonymous() : 0);

            // 输出参数
            stmt.registerOutParameter(6, Types.INTEGER);  // complaintId
            stmt.registerOutParameter(7, Types.NVARCHAR); // message

            stmt.execute();

            result.put("complaintId", stmt.getInt(6));
            result.put("message", stmt.getString(7));
            result.put("success", stmt.getInt(6) > 0);

        } catch (SQLException e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "提交失败：" + e.getMessage());
        } finally {
            DBUtil.close(conn, stmt, null);
        }

        return result;
    }

    /**
     * 查询投诉列表
     */
    public Map<String, Object> getComplaints(String ownerId, String complaintType,
                                             String complaintStatus, String keyword,
                                             Integer pageNum, Integer pageSize) {

        System.out.println("\n=== ComplaintDao.getComplaints 开始执行 ===");

        // 🔥 将空字符串转为 null
        if (ownerId != null && ownerId.trim().isEmpty()) ownerId = null;
        if (complaintType != null && complaintType.trim().isEmpty()) complaintType = null;
        if (complaintStatus != null && complaintStatus.trim().isEmpty()) complaintStatus = null;
        if (keyword != null && keyword.trim().isEmpty()) keyword = null;

        Connection conn = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        Map<String, Object> result = new HashMap<>();
        List<Complaint> list = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareCall("{CALL sp_get_complaints(?, ?, ?, ?, ?, ?, ?)}");

            // 输入参数
            stmt.setString(1, ownerId);
            stmt.setString(2, complaintType);
            stmt.setString(3, complaintStatus);
            stmt.setString(4, keyword);
            stmt.setInt(5, pageNum != null ? pageNum : 1);
            stmt.setInt(6, pageSize != null ? pageSize : 10);

            // 输出参数
            stmt.registerOutParameter(7, Types.INTEGER);

            rs = stmt.executeQuery();

            while (rs.next()) {
                Complaint complaint = new Complaint();
                complaint.setComplaintId(rs.getInt("complaint_id"));
                complaint.setOwnerId(rs.getString("owner_id"));
                complaint.setOwnerName(rs.getString("owner_name"));
                complaint.setOwnerPhone(rs.getString("owner_phone"));
                complaint.setComplaintType(rs.getString("complaint_type"));
                complaint.setComplaintTypeName(rs.getString("complaint_type_name"));
                complaint.setTitle(rs.getString("title"));
                complaint.setContent(rs.getString("content"));
                complaint.setIsAnonymous(rs.getInt("is_anonymous"));
                complaint.setComplaintStatus(rs.getString("complaint_status"));
                complaint.setComplaintStatusName(rs.getString("complaint_status_name"));

                Timestamp submitTime = rs.getTimestamp("submit_time");
                if (submitTime != null) {
                    complaint.setSubmitTime(submitTime.toLocalDateTime());
                }

                Object handlerIdObj = rs.getObject("handler_id");
                if (handlerIdObj != null) {
                    complaint.setHandlerId(((Number) handlerIdObj).intValue());
                }

                complaint.setHandlerName(rs.getString("handler_name"));
                complaint.setReply(rs.getString("reply"));

                Timestamp replyTime = rs.getTimestamp("reply_time");
                if (replyTime != null) {
                    complaint.setReplyTime(replyTime.toLocalDateTime());
                }

                Object responseHoursObj = rs.getObject("response_hours");
                if (responseHoursObj != null) {
                    complaint.setResponseHours(((Number) responseHoursObj).intValue());
                }

                list.add(complaint);
            }

            int totalCount = stmt.getInt(7);
            result.put("list", list);
            result.put("totalCount", totalCount);

        } catch (SQLException e) {
            e.printStackTrace();
            result.put("list", new ArrayList<>());
            result.put("totalCount", 0);
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return result;
    }

    /**
     * 根据ID查询投诉详情
     */
    public Complaint getComplaintById(Integer complaintId) {
        System.out.println("🔍 DAO: 查询投诉详情，ID: " + complaintId);

        String sql = "SELECT c.*, " +
                "o.owner_name, o.phone AS owner_phone, " +
                "u.real_name AS handler_name " +
                "FROM complaints c " +
                "LEFT JOIN owners o ON c.owner_id = o.owner_id " +
                "LEFT JOIN users u ON c.handler_id = u.user_id " +
                "WHERE c.complaint_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, complaintId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                Complaint complaint = new Complaint();
                complaint.setComplaintId(rs.getInt("complaint_id"));
                complaint.setOwnerId(rs.getString("owner_id"));
                complaint.setComplaintType(rs.getString("complaint_type"));
                complaint.setTitle(rs.getString("title"));
                complaint.setContent(rs.getString("content"));
                complaint.setIsAnonymous(rs.getInt("is_anonymous"));
                complaint.setComplaintStatus(rs.getString("complaint_status"));

                Object handlerIdObj = rs.getObject("handler_id");
                if (handlerIdObj != null) {
                    complaint.setHandlerId(((Number) handlerIdObj).intValue());
                }

                complaint.setReply(rs.getString("reply"));

                Timestamp submitTime = rs.getTimestamp("submit_time");
                if (submitTime != null) {
                    complaint.setSubmitTime(submitTime.toLocalDateTime());
                }

                Timestamp replyTime = rs.getTimestamp("reply_time");
                if (replyTime != null) {
                    complaint.setReplyTime(replyTime.toLocalDateTime());
                }

                complaint.setOwnerName(rs.getString("owner_name"));
                complaint.setOwnerPhone(rs.getString("owner_phone"));
                complaint.setHandlerName(rs.getString("handler_name"));

                return complaint;
            }

        } catch (SQLException e) {
            System.err.println("❌ DAO 查询详情异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }

        return null;
    }

    /**
     * 查询投诉详情（使用存储过程）
     */
    public Complaint getComplaintDetail(Integer complaintId) {
        Connection conn = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        Complaint complaint = null;

        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareCall("{CALL sp_get_complaint_detail(?)}");
            stmt.setInt(1, complaintId);

            rs = stmt.executeQuery();

            if (rs.next()) {
                complaint = new Complaint();
                complaint.setComplaintId(rs.getInt("complaint_id"));
                complaint.setOwnerId(rs.getString("owner_id"));
                complaint.setOwnerName(rs.getString("owner_name"));
                complaint.setOwnerPhone(rs.getString("owner_phone"));
                complaint.setHouseId(rs.getString("house_id"));
                complaint.setBuildingNo(rs.getString("building_no"));
                complaint.setUnitNo(rs.getString("unit_no"));
                complaint.setFloor(rs.getString("floor"));
                complaint.setComplaintType(rs.getString("complaint_type"));
                complaint.setComplaintTypeName(rs.getString("complaint_type_name"));
                complaint.setTitle(rs.getString("title"));
                complaint.setContent(rs.getString("content"));
                complaint.setIsAnonymous(rs.getInt("is_anonymous"));
                complaint.setComplaintStatus(rs.getString("complaint_status"));
                complaint.setComplaintStatusName(rs.getString("complaint_status_name"));
                complaint.setSubmitTime(rs.getTimestamp("submit_time").toLocalDateTime());

                Object handlerIdObj = rs.getObject("handler_id");
                if (handlerIdObj != null) {
                    complaint.setHandlerId(((Number) handlerIdObj).intValue());
                }

                complaint.setHandlerName(rs.getString("handler_name"));
                complaint.setHandlerPhone(rs.getString("handler_phone"));
                complaint.setReply(rs.getString("reply"));

                Timestamp replyTime = rs.getTimestamp("reply_time");
                if (replyTime != null) {
                    complaint.setReplyTime(replyTime.toLocalDateTime());
                }

                Object responseHoursObj = rs.getObject("response_hours");
                if (responseHoursObj != null) {
                    complaint.setResponseHours(((Number) responseHoursObj).intValue());
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return complaint;
    }

    /**
     * 受理投诉
     */
    public Map<String, Object> acceptComplaint(Integer complaintId, Integer handlerId) {
        Connection conn = null;
        CallableStatement stmt = null;
        Map<String, Object> result = new HashMap<>();

        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareCall("{CALL sp_accept_complaint(?, ?, ?)}");

            stmt.setInt(1, complaintId);
            stmt.setInt(2, handlerId);
            stmt.registerOutParameter(3, Types.NVARCHAR);

            stmt.execute();

            String message = stmt.getString(3);
            result.put("message", message);
            result.put("success", "受理成功".equals(message));

        } catch (SQLException e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "受理失败：" + e.getMessage());
        } finally {
            DBUtil.close(conn, stmt, null);
        }

        return result;
    }

    /**
     * 回复投诉
     */
    public Map<String, Object> replyComplaint(Integer complaintId, Integer handlerId,
                                              String reply, String newStatus) {
        Connection conn = null;
        CallableStatement stmt = null;
        Map<String, Object> result = new HashMap<>();

        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareCall("{CALL sp_reply_complaint(?, ?, ?, ?, ?)}");

            stmt.setInt(1, complaintId);
            stmt.setInt(2, handlerId);
            stmt.setString(3, reply);
            stmt.setString(4, newStatus != null ? newStatus : "resolved");
            stmt.registerOutParameter(5, Types.NVARCHAR);

            stmt.execute();

            String message = stmt.getString(5);
            result.put("message", message);
            result.put("success", "回复成功".equals(message));

        } catch (SQLException e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "回复失败：" + e.getMessage());
        } finally {
            DBUtil.close(conn, stmt, null);
        }

        return result;
    }

    /**
     * 关闭投诉
     */
    public Map<String, Object> closeComplaint(Integer complaintId, Integer handlerId) {
        Connection conn = null;
        CallableStatement stmt = null;
        Map<String, Object> result = new HashMap<>();

        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareCall("{CALL sp_close_complaint(?, ?, ?)}");

            stmt.setInt(1, complaintId);
            stmt.setInt(2, handlerId);
            stmt.registerOutParameter(3, Types.NVARCHAR);

            stmt.execute();

            String message = stmt.getString(3);
            result.put("message", message);
            result.put("success", "关闭成功".equals(message));

        } catch (SQLException e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "关闭失败：" + e.getMessage());
        } finally {
            DBUtil.close(conn, stmt, null);
        }

        return result;
    }

    /**
     * 删除投诉
     */
    public Map<String, Object> deleteComplaint(Integer complaintId, Integer operatorId) {
        Connection conn = null;
        CallableStatement stmt = null;
        Map<String, Object> result = new HashMap<>();

        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareCall("{CALL sp_delete_complaint(?, ?, ?)}");

            stmt.setInt(1, complaintId);
            stmt.setInt(2, operatorId);
            stmt.registerOutParameter(3, Types.NVARCHAR);

            stmt.execute();

            String message = stmt.getString(3);
            result.put("message", message);
            result.put("success", "删除成功".equals(message));

        } catch (SQLException e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "删除失败：" + e.getMessage());
        } finally {
            DBUtil.close(conn, stmt, null);
        }

        return result;
    }

    /**
     * 投诉统计（使用存储过程）
     */
    public Map<String, Object> getComplaintStatistics(String startDate, String endDate) {
        System.out.println("📊 DAO: 查询统计数据");
        Connection conn = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        Map<String, Object> result = new HashMap<>();

        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareCall("{CALL sp_get_complaint_statistics(?, ?)}");

            stmt.setString(1, startDate);
            stmt.setString(2, endDate);

            // 第一个结果集：总体统计
            rs = stmt.executeQuery();
            if (rs.next()) {
                Map<String, Object> overall = new HashMap<>();
                overall.put("totalCount", rs.getInt("total_count"));
                overall.put("pendingCount", rs.getInt("pending_count"));
                overall.put("processingCount", rs.getInt("processing_count"));
                overall.put("resolvedCount", rs.getInt("resolved_count"));
                overall.put("closedCount", rs.getInt("closed_count"));
                overall.put("resolveRate", rs.getDouble("resolve_rate"));
                overall.put("avgResponseHours", rs.getDouble("avg_response_hours"));
                result.put("overall", overall);
            }

            // 第二个结果集：按类型统计
            if (stmt.getMoreResults()) {
                rs = stmt.getResultSet();
                List<Map<String, Object>> byType = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> typeStats = new HashMap<>();
                    typeStats.put("complaintType", rs.getString("complaint_type"));
                    typeStats.put("complaintTypeName", rs.getString("complaint_type_name"));
                    typeStats.put("count", rs.getInt("count"));
                    typeStats.put("resolvedCount", rs.getInt("resolved_count"));
                    typeStats.put("resolveRate", rs.getDouble("resolve_rate"));
                    byType.add(typeStats);
                }
                result.put("byType", byType);
            }

            // 第三个结果集：按状态统计
            if (stmt.getMoreResults()) {
                rs = stmt.getResultSet();
                List<Map<String, Object>> byStatus = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> statusStats = new HashMap<>();
                    statusStats.put("complaintStatus", rs.getString("complaint_status"));
                    statusStats.put("complaintStatusName", rs.getString("complaint_status_name"));
                    statusStats.put("count", rs.getInt("count"));
                    statusStats.put("percentage", rs.getDouble("percentage"));
                    byStatus.add(statusStats);
                }
                result.put("byStatus", byStatus);
            }

            result.put("success", true);

        } catch (SQLException e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "统计失败: " + e.getMessage());
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return result;
    }

    /**
     * 根据状态统计数量
     */
    public int getCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM complaints WHERE complaint_status = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); } finally { DBUtil.close(conn, pstmt, rs); }
        return 0;
    }

    /**
     * 获取总记录数
     */
    public int getTotalCount(Map<String, Object> params) {
        String sql = "SELECT COUNT(*) FROM complaints WHERE 1=1";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); } finally { DBUtil.close(conn, pstmt, rs); }
        return 0;
    }

    /**
     * 根据类型统计数量
     */
    public int getCountByType(String type) {
        String sql = "SELECT COUNT(*) FROM complaints WHERE complaint_type = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, type);
            rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); } finally { DBUtil.close(conn, pstmt, rs); }
        return 0;
    }

    /**
     * 根据类型统计已解决数量
     */
    public int getResolvedCountByType(String type) {
        String sql = "SELECT COUNT(*) FROM complaints WHERE complaint_type = ? AND complaint_status = 'resolved'";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, type);
            rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); } finally { DBUtil.close(conn, pstmt, rs); }
        return 0;
    }

    /**
     * 获取平均响应时长
     */
    public double getAvgResponseHours() {
        String sql = "SELECT AVG(DATEDIFF(HOUR, submit_time, reply_time)) FROM complaints WHERE reply_time IS NOT NULL";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) return Math.round(rs.getDouble(1) * 100) / 100.0;
        } catch (SQLException e) { e.printStackTrace(); } finally { DBUtil.close(conn, pstmt, rs); }
        return 0;
    }

    /**
     * 业主端：统计业主投诉总数
     */
    public int getOwnerTotalCount(String ownerId) {
        String sql = "SELECT COUNT(*) FROM complaints WHERE owner_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, ownerId);
            rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); } finally { DBUtil.close(conn, pstmt, rs); }
        return 0;
    }

    /**
     * 业主端：统计业主指定状态的投诉数量
     */
    public int getOwnerCountByStatus(String ownerId, String status) {
        String sql = "SELECT COUNT(*) FROM complaints WHERE owner_id = ? AND complaint_status = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, ownerId);
            pstmt.setString(2, status);
            rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); } finally { DBUtil.close(conn, pstmt, rs); }
        return 0;
    }

    /**
     * 业主端：查询最近投诉
     */
    public List<Complaint> findRecentByOwner(String ownerId, int limit) {
        String sql = "SELECT TOP (?) c.*, " +
                "o.owner_name, o.phone AS owner_phone, " +
                "u.real_name AS handler_name " +
                "FROM complaints c " +
                "LEFT JOIN owners o ON c.owner_id = o.owner_id " +
                "LEFT JOIN users u ON c.handler_id = u.user_id " +
                "WHERE c.owner_id = ? " +
                "ORDER BY c.submit_time DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Complaint> list = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            pstmt.setString(2, ownerId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Complaint complaint = new Complaint();
                complaint.setComplaintId(rs.getInt("complaint_id"));
                complaint.setOwnerId(rs.getString("owner_id"));
                complaint.setComplaintType(rs.getString("complaint_type"));
                complaint.setTitle(rs.getString("title"));
                complaint.setContent(rs.getString("content"));
                complaint.setIsAnonymous(rs.getInt("is_anonymous"));
                complaint.setComplaintStatus(rs.getString("complaint_status"));

                Timestamp submitTime = rs.getTimestamp("submit_time");
                if (submitTime != null) complaint.setSubmitTime(submitTime.toLocalDateTime());

                Object handlerIdObj = rs.getObject("handler_id");
                if (handlerIdObj != null) complaint.setHandlerId(((Number) handlerIdObj).intValue());

                complaint.setReply(rs.getString("reply"));
                Timestamp replyTime = rs.getTimestamp("reply_time");
                if (replyTime != null) complaint.setReplyTime(replyTime.toLocalDateTime());

                complaint.setOwnerName(rs.getString("owner_name"));
                complaint.setOwnerPhone(rs.getString("owner_phone"));
                complaint.setHandlerName(rs.getString("handler_name"));

                list.add(complaint);
            }
        } catch (SQLException e) { e.printStackTrace(); } finally { DBUtil.close(conn, pstmt, rs); }
        return list;
    }

    /**
     * 业主端：撤销投诉 (旧方法，保留兼容性)
     */
    public boolean cancelComplaint(Integer complaintId) {
        String sql = "UPDATE complaints SET complaint_status = 'closed' WHERE complaint_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, complaintId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; } finally { DBUtil.close(conn, pstmt, null); }
    }

    /**
     * 业主端：追加投诉内容
     */
    public boolean appendContent(Integer complaintId, String additionalContent) {
        String sql = "UPDATE complaints SET content = content + CHAR(13) + CHAR(10) + " +
                "'【' + CONVERT(VARCHAR, GETDATE(), 120) + ' 追加】' + CHAR(13) + CHAR(10) + ? " +
                "WHERE complaint_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, additionalContent);
            pstmt.setInt(2, complaintId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; } finally { DBUtil.close(conn, pstmt, null); }
    }

    /**
     * 管理员：驳回/取消投诉 (旧方法，保留兼容性)
     */
    public boolean cancelComplaint(int complaintId, String reason) {
        String sql = "UPDATE complaints SET complaint_status = 'closed', reply = ? WHERE complaint_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            String finalReason = "【已驳回/取消】原因：" + reason;
            pstmt.setString(1, finalReason);
            pstmt.setInt(2, complaintId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; } finally { DBUtil.close(conn, pstmt, null); }
    }
}
