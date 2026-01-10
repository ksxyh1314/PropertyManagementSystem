package com.property.service;

import com.property.dao.ComplaintDao;
import com.property.entity.Complaint;
import com.property.entity.User;
import com.property.util.DBUtil;
import com.property.util.LogUtil;

import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 投诉服务层 (✅ 仅在未使用存储过程的操作中记录日志)
 */
public class ComplaintService {

    private ComplaintDao complaintDao = new ComplaintDao();

    // ==================== 1. 核心业务方法 ====================

    /**
     * 提交投诉（✅ 使用存储过程 sp_submit_complaint，已包含日志记录）
     */
    public Map<String, Object> submitComplaint(Complaint complaint) {
        // 🔥🔥🔥 关键修复1：验证 ownerId 不能为空
        if (complaint.getOwnerId() == null || complaint.getOwnerId().trim().isEmpty()) {
            System.err.println("❌ 提交失败：ownerId 为空");
            return createResult(false, "业主信息缺失，请重新登录");
        }

        // 数据验证
        if (complaint.getTitle() == null || complaint.getTitle().trim().isEmpty()) {
            return createResult(false, "标题不能为空");
        }
        if (complaint.getContent() == null || complaint.getContent().trim().isEmpty()) {
            return createResult(false, "内容不能为空");
        }
        if (complaint.getComplaintType() == null || complaint.getComplaintType().trim().isEmpty()) {
            return createResult(false, "投诉类型不能为空");
        }

        // 🔥🔥🔥 关键修复2：确保 isAnonymous 有默认值
        if (complaint.getIsAnonymous() == null) {
            complaint.setIsAnonymous(0);  // 默认实名
        }

        // 🔥 调试日志
        System.out.println(">>> Service 提交投诉:");
        System.out.println("    业主ID: " + complaint.getOwnerId());
        System.out.println("    是否匿名: " + complaint.getIsAnonymous());
        System.out.println("    标题: " + complaint.getTitle());
        System.out.println("    类型: " + complaint.getComplaintType());

        // ✅ 调用 DAO 层插入（存储过程已记录日志，这里不需要重复记录）
        Map<String, Object> result = complaintDao.submitComplaint(complaint);

        if (result.get("success") == Boolean.TRUE) {
            System.out.println("✅ Service: 投诉提交成功");
        } else {
            System.err.println("❌ Service: 投诉提交失败 - " + result.get("message"));
        }

        return result;
    }
    /**
     * 查询投诉列表 (支持存储过程 sp_get_complaints)
     * 🔥 修复版：正确处理 owner_id 参数类型
     */
    public Map<String, Object> getComplaints(String ownerId, String complaintType,
                                             String complaintStatus, String keyword,
                                             Integer pageNum, Integer pageSize) {

        System.out.println("\n=== ComplaintService.getComplaints 开始执行 ===");
        System.out.println(">>> 接收参数:");
        System.out.println("    ownerId: [" + ownerId + "] (长度: " + (ownerId != null ? ownerId.length() : "null") + ")");
        System.out.println("    complaintType: " + complaintType);
        System.out.println("    complaintStatus: " + complaintStatus);
        System.out.println("    keyword: " + keyword);
        System.out.println("    pageNum: " + pageNum);
        System.out.println("    pageSize: " + pageSize);

        Connection conn = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        Map<String, Object> result = new HashMap<>();
        List<Complaint> list = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();

            // 调用存储过程 sp_get_complaints
            stmt = conn.prepareCall("{CALL sp_get_complaints(?, ?, ?, ?, ?, ?, ?)}");

            // 🔥🔥🔥 关键修复：ownerId 应该作为字符串传递
            // 1. @p_owner_id (CHAR(8))
            if (ownerId == null || ownerId.trim().isEmpty() || "null".equalsIgnoreCase(ownerId)) {
                stmt.setNull(1, Types.CHAR);  // ✅ 修改为 Types.CHAR
                System.out.println(">>> 参数1 (ownerId): NULL");
            } else {
                stmt.setString(1, ownerId);  // ✅ 修改为 setString，保留前导零
                System.out.println(">>> 参数1 (ownerId): [" + ownerId + "]");
            }

            // 2. @p_complaint_type (NVARCHAR(50))
            if (complaintType == null || complaintType.trim().isEmpty()) {
                stmt.setNull(2, Types.NVARCHAR);
            } else {
                stmt.setString(2, complaintType);
            }
            System.out.println(">>> 参数2 (complaintType): " + complaintType);

            // 3. @p_complaint_status (NVARCHAR(20))
            if (complaintStatus == null || complaintStatus.trim().isEmpty()) {
                stmt.setNull(3, Types.NVARCHAR);
            } else {
                stmt.setString(3, complaintStatus);
            }
            System.out.println(">>> 参数3 (complaintStatus): " + complaintStatus);

            // 4. @p_keyword (NVARCHAR(200))
            if (keyword == null || keyword.trim().isEmpty()) {
                stmt.setNull(4, Types.NVARCHAR);
            } else {
                stmt.setString(4, keyword);
            }
            System.out.println(">>> 参数4 (keyword): " + keyword);

            // 5. @p_page_num (INT)
            stmt.setInt(5, pageNum != null ? pageNum : 1);
            System.out.println(">>> 参数5 (pageNum): " + (pageNum != null ? pageNum : 1));

            // 6. @p_page_size (INT)
            stmt.setInt(6, pageSize != null ? pageSize : 10);
            System.out.println(">>> 参数6 (pageSize): " + (pageSize != null ? pageSize : 10));

            // 7. @p_total_count (INT OUTPUT)
            stmt.registerOutParameter(7, Types.INTEGER);

            System.out.println(">>> 开始执行存储过程...");
            rs = stmt.executeQuery();

            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                Complaint complaint = new Complaint();

                // 🔥 注意：存储过程返回的字段名是小写的 complaintid, ownerid 等
                complaint.setComplaintId(rs.getInt("complaintid"));
                complaint.setOwnerId(rs.getString("ownerid"));
                complaint.setOwnerName(rs.getString("ownername"));
                complaint.setOwnerPhone(rs.getString("ownerphone"));
                complaint.setComplaintType(rs.getString("complainttype"));
                complaint.setComplaintTypeName(rs.getString("complainttypename"));
                complaint.setTitle(rs.getString("title"));
                complaint.setContent(rs.getString("content"));
                complaint.setIsAnonymous(rs.getInt("isanonymous"));
                complaint.setComplaintStatus(rs.getString("complaintstatus"));
                complaint.setComplaintStatusName(rs.getString("complaintstatusname"));

                Timestamp submitTime = rs.getTimestamp("submittime");
                if (submitTime != null) {
                    complaint.setSubmitTime(submitTime.toLocalDateTime());
                }

                Object handlerIdObj = rs.getObject("handlerid");
                if (handlerIdObj != null) {
                    complaint.setHandlerId(((Number) handlerIdObj).intValue());
                }

                complaint.setHandlerName(rs.getString("handlername"));
                complaint.setReply(rs.getString("reply"));

                Timestamp replyTime = rs.getTimestamp("replytime");
                if (replyTime != null) {
                    complaint.setReplyTime(replyTime.toLocalDateTime());
                }

                Object responseHoursObj = rs.getObject("responsehours");
                if (responseHoursObj != null) {
                    complaint.setResponseHours(((Number) responseHoursObj).intValue());
                }

                list.add(complaint);

                // 🔥 打印第一条记录的详细信息
                if (rowCount == 1) {
                    System.out.println(">>> 第一条记录:");
                    System.out.println("    complaintId: " + complaint.getComplaintId());
                    System.out.println("    ownerId: [" + complaint.getOwnerId() + "]");
                    System.out.println("    title: " + complaint.getTitle());
                    System.out.println("    status: " + complaint.getComplaintStatus());
                }
            }

            int totalCount = stmt.getInt(7);

            System.out.println(">>> 查询结果:");
            System.out.println("    当前页记录数: " + rowCount);
            System.out.println("    总记录数: " + totalCount);
            System.out.println("=================================================\n");

            result.put("list", list);
            result.put("totalCount", totalCount);

        } catch (SQLException e) {
            System.err.println("❌ 查询投诉列表异常: " + e.getMessage());
            System.err.println("❌ SQL State: " + e.getSQLState());
            System.err.println("❌ Error Code: " + e.getErrorCode());
            e.printStackTrace();
            result.put("list", new ArrayList<>());
            result.put("totalCount", 0);
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return result;
    }

    /**
     * 查询投诉详情（使用存储过程 sp_get_complaint_detail）
     */
    public Complaint getComplaintDetail(Integer complaintId) {
        // 优先使用简单查询，失败则尝试存储过程
        Complaint complaint = complaintDao.getComplaintById(complaintId);
        if (complaint == null) {
            complaint = complaintDao.getComplaintDetail(complaintId);
        }
        return complaint;
    }

    // ==================== 2. 管理员操作方法（✅ 使用存储过程，已包含日志记录）====================

    /**
     * 受理投诉（✅ 使用存储过程 sp_accept_complaint，已包含日志记录）
     */
    public Map<String, Object> acceptComplaint(Integer complaintId, Integer handlerId) {
        return complaintDao.acceptComplaint(complaintId, handlerId);
    }

    /**
     * 回复投诉（✅ 使用存储过程 sp_reply_complaint，已包含日志记录）
     */
    public Map<String, Object> replyComplaint(Integer complaintId, Integer handlerId,
                                              String reply, String newStatus) {
        if (reply == null || reply.trim().isEmpty()) {
            return createResult(false, "回复内容不能为空");
        }
        return complaintDao.replyComplaint(complaintId, handlerId, reply, newStatus);
    }

    /**
     * 关闭投诉（✅ 使用存储过程 sp_close_complaint，已包含日志记录）
     */
    public Map<String, Object> closeComplaint(Integer complaintId, Integer handlerId) {
        return complaintDao.closeComplaint(complaintId, handlerId);
    }

    /**
     * 删除投诉（✅ 使用存储过程 sp_delete_complaint，已包含日志记录）
     */
    public Map<String, Object> deleteComplaint(Integer complaintId, Integer operatorId) {
        return complaintDao.deleteComplaint(complaintId, operatorId);
    }

    // ==================== 3. 🔥 重点修复：取消/驳回逻辑（✅ 需要手动记录日志）====================

    /**
     * 取消/驳回投诉（支持不传 request）
     */
    public Map<String, Object> cancelComplaint(Integer complaintId, String reason, User currentUser) {
        return cancelComplaint(complaintId, reason, currentUser, null);
    }

    /**
     * 🔥 取消/驳回投诉（✅ 增加日志记录 - 因为没有使用存储过程）
     */
    public Map<String, Object> cancelComplaint(Integer complaintId, String reason, User currentUser, HttpServletRequest request) {
        System.out.println("=== 开始执行撤销投诉 ===");
        System.out.println("✅ Service: 取消/驳回投诉，ID: " + complaintId + ", 操作人: " + currentUser.getUsername());

        try {
            // 1. 查询当前投诉状态
            System.out.println("✅ DAO: 查询投诉详情，ID: " + complaintId);
            Complaint complaint = complaintDao.getComplaintById(complaintId);

            if (complaint == null) {
                return createResult(false, "投诉记录不存在");
            }

            // 2. 权限与状态校验
            String role = currentUser.getUserRole(); // "owner" 或 "admin"
            String currentStatus = complaint.getComplaintStatus();
            String finalReplyContent = "";
            Integer handlerId = null;

            if ("owner".equals(role)) {
                // --- 🅰️ 业主逻辑 ---
                // 只能操作自己的
                if (!complaint.getOwnerId().equals(currentUser.getUsername())) {
                    return createResult(false, "您无权操作他人的投诉");
                }
                // 只能取消 "pending"
                if (!"pending".equals(currentStatus)) {
                    return createResult(false, "物业已受理或已处理，无法直接取消，请联系管理员");
                }

                // 构造业主撤销文案
                String reasonText = (reason != null && !reason.trim().isEmpty()) ? reason : "无详细原因";
                finalReplyContent = "【业主主动撤销】原因：" + reasonText;

                // 业主撤销不记录 handlerId
                handlerId = null;

            } else {
                // --- 🅱️ 管理员逻辑 ---
                // 不能取消已完成的
                if ("resolved".equals(currentStatus) || "closed".equals(currentStatus)) {
                    return createResult(false, "已结案的投诉无法驳回");
                }

                // 构造管理员驳回文案
                String reasonText = (reason != null && !reason.trim().isEmpty()) ? reason : "不符合受理条件";
                finalReplyContent = "【管理员驳回】原因：" + reasonText;

                // 记录管理员ID
                handlerId = currentUser.getUserId();
            }

            // 3. 🔥 关键修复：使用 'closed' 状态代替 'cancelled'
            // 通过 reply 内容区分是撤销还是驳回
            boolean success = complaintDao.updateStatus(complaintId, "closed", finalReplyContent, handlerId);

            // ✅ 记录日志（因为没有使用存储过程）
            if (success) {
                String operationType = "owner".equals(role) ? "complaint_cancel" : "complaint_reject";
                String operationDesc = "owner".equals(role) ?
                        "撤销投诉：" + complaint.getTitle() + "（ID:" + complaintId + "）" :
                        "驳回投诉：" + complaint.getTitle() + "（ID:" + complaintId + "）";

                if (request != null) {
                    LogUtil.log(
                            currentUser.getUserId() != null ? currentUser.getUserId() : 0,
                            currentUser.getUsername(),
                            operationType,
                            operationDesc,
                            LogUtil.getClientIP(request)
                    );
                } else {
                    // 如果没有 request，使用默认 IP
                    LogUtil.log(
                            currentUser.getUserId() != null ? currentUser.getUserId() : 0,
                            currentUser.getUsername(),
                            operationType,
                            operationDesc,
                            "0.0.0.0"
                    );
                }
            }

            return createResult(success, success ? "操作成功" : "操作失败");

        } catch (Exception e) {
            System.err.println("❌ 取消失败: " + e.getMessage());
            e.printStackTrace();
            return createResult(false, "系统错误: " + e.getMessage());
        }
    }

    // ==================== 4. 🔥 统计相关方法（使用存储过程 sp_get_complaint_statistics）====================

    /**
     * 🔥 投诉统计（修复版：返回正确的数据结构）
     */
    public Map<String, Object> getComplaintStatistics(String startDate, String endDate) {
        System.out.println("=== getComplaintStatistics 开始执行 ===");
        System.out.println("参数 - startDate: " + startDate + ", endDate: " + endDate);

        try {
            // ✅ 优先使用手动统计
            Map<String, Object> stats = getManualStatistics();

            // 🔥 构建符合前端期望的返回格式
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "统计成功");

            // 🔥 将 overall 数据提升到 data 层级
            if (stats.containsKey("overall")) {
                Map<String, Object> overall = (Map<String, Object>) stats.get("overall");

                // 🔥 转换数据格式（字符串 -> 数字）
                Map<String, Object> data = new HashMap<>();

                // 数量统计（整数）
                data.put("pending_count", getIntValue(overall.get("pendingCount")));
                data.put("processing_count", getIntValue(overall.get("processingCount")));
                data.put("resolved_count", getIntValue(overall.get("resolvedCount")));
                data.put("closed_count", getIntValue(overall.get("closedCount")));
                data.put("total_count", getIntValue(overall.get("totalCount")));

                // 🔥 兼容驼峰命名
                data.put("pendingCount", getIntValue(overall.get("pendingCount")));
                data.put("processingCount", getIntValue(overall.get("processingCount")));
                data.put("resolvedCount", getIntValue(overall.get("resolvedCount")));
                data.put("closedCount", getIntValue(overall.get("closedCount")));
                data.put("totalCount", getIntValue(overall.get("totalCount")));

                // 🔥 解决率（数字格式）
                double resolveRate = getDoubleValue(overall.get("resolveRate"));
                data.put("resolve_rate", resolveRate);
                data.put("resolveRate", resolveRate);

                // 平均响应时长
                double avgResponseHours = getDoubleValue(overall.get("avgResponseHours"));
                data.put("avg_response_hours", avgResponseHours);
                data.put("avgResponseHours", avgResponseHours);

                result.put("data", data);

                System.out.println("✅ 投诉统计结果:");
                System.out.println("  待处理: " + data.get("pendingCount"));
                System.out.println("  处理中: " + data.get("processingCount"));
                System.out.println("  已解决: " + data.get("resolvedCount"));
                System.out.println("  已关闭: " + data.get("closedCount"));
                System.out.println("  总数: " + data.get("totalCount"));
                System.out.println("  解决率: " + resolveRate + "%");

            } else {
                // 如果没有 overall，返回空数据
                Map<String, Object> emptyData = new HashMap<>();
                emptyData.put("pending_count", 0);
                emptyData.put("processing_count", 0);
                emptyData.put("resolved_count", 0);
                emptyData.put("closed_count", 0);
                emptyData.put("total_count", 0);
                emptyData.put("resolve_rate", 0.0);
                emptyData.put("resolveRate", 0.0);
                result.put("data", emptyData);
            }

            // 🔥 保留其他统计数据
            if (stats.containsKey("byType")) {
                result.put("byType", stats.get("byType"));
            }
            if (stats.containsKey("byStatus")) {
                result.put("byStatus", stats.get("byStatus"));
            }

            return result;

        } catch (Exception e) {
            System.err.println("❌ 统计异常: " + e.getMessage());
            e.printStackTrace();
            return createResult(false, "统计失败: " + e.getMessage());
        }
    }

    /**
     * 🔥 手动统计（修复版：返回数字类型）
     */
    private Map<String, Object> getManualStatistics() {
        System.out.println(">>> 执行手动统计");

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> overall = new HashMap<>();

        try {
            int totalCount = complaintDao.getTotalCount(new HashMap<>());
            int pendingCount = complaintDao.getCountByStatus("pending");
            int processingCount = complaintDao.getCountByStatus("processing");
            int resolvedCount = complaintDao.getCountByStatus("resolved");
            int closedCount = complaintDao.getCountByStatus("closed");

            // 🔥 计算解决率（数字类型）
            double resolveRate = 0.0;
            if (totalCount > 0) {
                resolveRate = (resolvedCount * 100.0) / totalCount;
            }

            double avgResponseHours = complaintDao.getAvgResponseHours();

            // 🔥 存储为数字类型，不是字符串
            overall.put("totalCount", totalCount);
            overall.put("pendingCount", pendingCount);
            overall.put("processingCount", processingCount);
            overall.put("resolvedCount", resolvedCount);
            overall.put("closedCount", closedCount);
            overall.put("resolveRate", resolveRate);  // ✅ 数字类型
            overall.put("avgResponseHours", avgResponseHours);

            result.put("overall", overall);

            // 按类型统计
            result.put("byType", countByType());

            // 按状态统计
            List<Map<String, Object>> byStatus = new ArrayList<>();
            String[] statuses = {"pending", "processing", "resolved", "closed"};
            String[] statusNames = {"待处理", "处理中", "已解决", "已关闭"};

            for (int i = 0; i < statuses.length; i++) {
                int count = complaintDao.getCountByStatus(statuses[i]);
                if (count > 0) {
                    double percentage = totalCount > 0 ? (count * 100.0 / totalCount) : 0;
                    Map<String, Object> statusStats = new HashMap<>();
                    statusStats.put("complaintStatus", statuses[i]);
                    statusStats.put("complaintStatusName", statusNames[i]);
                    statusStats.put("count", count);
                    statusStats.put("percentage", percentage);  // ✅ 数字类型
                    byStatus.add(statusStats);
                }
            }
            result.put("byStatus", byStatus);
            result.put("success", true);

            System.out.println("✅ 手动统计完成");

        } catch (Exception e) {
            System.err.println("❌ 手动统计失败: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 🔥 按类型统计（包含各状态详细数量）
     */
    public List<Map<String, Object>> countByType() {
        System.out.println(">>> 按类型统计投诉");

        List<Map<String, Object>> result = new ArrayList<>();

        String sql = "SELECT " +
                "complaint_type, " +
                "COUNT(*) as count, " +
                "SUM(CASE WHEN complaint_status = 'pending' THEN 1 ELSE 0 END) as pending_count, " +
                "SUM(CASE WHEN complaint_status = 'processing' THEN 1 ELSE 0 END) as processing_count, " +
                "SUM(CASE WHEN complaint_status = 'resolved' THEN 1 ELSE 0 END) as resolved_count, " +
                "SUM(CASE WHEN complaint_status = 'closed' THEN 1 ELSE 0 END) as closed_count " +
                "FROM complaints " +
                "GROUP BY complaint_type " +
                "ORDER BY count DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                String type = rs.getString("complaint_type");
                int count = rs.getInt("count");
                int resolvedCount = rs.getInt("resolved_count");

                item.put("complaintType", type);
                item.put("complaintTypeName", getTypeName(type));
                item.put("count", count);
                item.put("pendingCount", rs.getInt("pending_count"));
                item.put("processingCount", rs.getInt("processing_count"));
                item.put("resolvedCount", resolvedCount);
                item.put("closedCount", rs.getInt("closed_count"));

                // 解决率
                double resolveRate = count > 0 ? (resolvedCount * 100.0 / count) : 0;
                item.put("resolveRate", String.format("%.1f", resolveRate));

                result.add(item);
            }
            System.out.println("✅ 按类型统计成功，共 " + result.size() + " 种类型");

        } catch (SQLException e) {
            System.err.println("❌ 按类型统计失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }

        return result;
    }

    // ==================== 5. 业主端专用方法 ====================

    /**
     * 业主端：查询我的投诉列表（分页）
     */
    public Map<String, Object> findByPage(int pageNum, int pageSize, String ownerId,
                                          String complaintType, String complaintStatus, String keyword) {
        return getComplaints(ownerId, complaintType, complaintStatus, keyword, pageNum, pageSize);
    }

    /**
     * 业主端：统计我的投诉数量
     */
    public Map<String, Object> getOwnerComplaintSummary(String ownerId) {
        Map<String, Object> summary = new HashMap<>();
        try {
            summary.put("totalCount", complaintDao.getOwnerTotalCount(ownerId));
            summary.put("pendingCount", complaintDao.getOwnerCountByStatus(ownerId, "pending"));
            summary.put("processingCount", complaintDao.getOwnerCountByStatus(ownerId, "processing"));
            summary.put("resolvedCount", complaintDao.getOwnerCountByStatus(ownerId, "resolved"));
            summary.put("closedCount", complaintDao.getOwnerCountByStatus(ownerId, "closed"));
        } catch (Exception e) {
            e.printStackTrace();
            summary.put("totalCount", 0);
        }
        return summary;
    }

    /**
     * 业主端：查询最近投诉
     */
    public List<Complaint> findRecentByOwner(String ownerId, int limit) {
        try {
            return complaintDao.findRecentByOwner(ownerId, limit);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 业主端：追加说明（支持不传 request）
     */
    public Map<String, Object> appendContent(Integer complaintId, String ownerId, String additionalContent) {
        return appendContent(complaintId, ownerId, additionalContent, null);
    }

    /**
     * 业主端：追加说明（✅ 增加日志记录 - 因为没有使用存储过程）
     */
    public Map<String, Object> appendContent(Integer complaintId, String ownerId, String additionalContent, HttpServletRequest request) {
        if (additionalContent == null || additionalContent.trim().isEmpty()) {
            return createResult(false, "追加内容不能为空");
        }
        try {
            Complaint complaint = complaintDao.getComplaintById(complaintId);
            if (complaint == null || !complaint.getOwnerId().equals(ownerId)) {
                return createResult(false, "无权操作");
            }
            if ("resolved".equals(complaint.getComplaintStatus()) || "closed".equals(complaint.getComplaintStatus())) {
                return createResult(false, "已结案的投诉无法追加");
            }
            boolean success = complaintDao.appendContent(complaintId, additionalContent);

            // ✅ 记录日志（因为没有使用存储过程）
            if (success) {
                if (request != null) {
                    LogUtil.log(
                            0,
                            ownerId,
                            "complaint_append",
                            "追加投诉内容：" + complaint.getTitle() + "（ID:" + complaintId + "）",
                            LogUtil.getClientIP(request)
                    );
                } else {
                    // 如果没有 request，使用默认 IP
                    LogUtil.log(
                            0,
                            ownerId,
                            "complaint_append",
                            "追加投诉内容：" + complaint.getTitle() + "（ID:" + complaintId + "）",
                            "0.0.0.0"
                    );
                }
            }

            return createResult(success, success ? "追加成功" : "追加失败");
        } catch (Exception e) {
            e.printStackTrace();
            return createResult(false, "系统错误");
        }
    }

    /**
     * 🔥 业主端：删除投诉记录（仅限已撤销的记录）
     * ✅ 使用存储过程 sp_delete_complaint，已包含日志记录
     */
    public boolean deleteComplaint(Integer complaintId) {
        System.out.println(">>> Service: 删除投诉记录，ID: " + complaintId);

        try {
            Map<String, Object> result = complaintDao.deleteComplaint(complaintId, 0);
            boolean success = (Boolean) result.get("success");

            if (success) {
                System.out.println("✅ Service: 删除成功");
            } else {
                System.err.println("❌ Service: 删除失败 - " + result.get("message"));
            }

            return success;

        } catch (Exception e) {
            System.err.println("❌ Service: 删除投诉异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ==================== 6. 🔥 辅助方法 ====================

    private String getTypeName(String type) {
        if (type == null) return "未知";
        switch (type) {
            case "service": return "服务";
            case "environment": return "环境";
            case "facility": return "设施";
            case "fee": return "费用";
            case "other": return "其他";
            default: return type;
        }
    }

    private Map<String, Object> createResult(boolean success, String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", success);
        map.put("message", message);
        return map;
    }

    /**
     * 🔧 辅助方法：安全获取整数值
     */
    private int getIntValue(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 🔧 辅助方法：安全获取浮点数值
     */
    private double getDoubleValue(Object value) {
        if (value == null) {
            return 0.0;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }
}
