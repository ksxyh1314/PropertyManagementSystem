package com.property.dao;

import com.property.entity.RepairRecord;
import com.property.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 报修记录DAO
 */
public class RepairRecordDao extends BaseDao {

    // 状态常量
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_PROCESSING = "processing";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_CANCELLED = "cancelled";

    /**
     * 根据报修ID查询
     */
    public RepairRecord findById(Integer repairId) {
        String sql = "SELECT r.*, o.owner_name, o.phone AS owner_phone " +
                "FROM repair_records r " +
                "LEFT JOIN owners o ON r.owner_id = o.owner_id " +
                "WHERE r.repair_id = ?";
        return queryOne(sql, this::mapRepairRecord, repairId);
    }

    /**
     * 查询所有报修记录
     */
    public List<RepairRecord> findAll() {
        String sql = "SELECT r.*, o.owner_name, o.phone AS owner_phone " +
                "FROM repair_records r " +
                "LEFT JOIN owners o ON r.owner_id = o.owner_id " +
                "ORDER BY r.submit_time DESC";
        return query(sql, this::mapRepairRecord);
    }

    /**
     * 分页查询报修记录
     */
    public List<RepairRecord> findByPage(int pageNum, int pageSize, String keyword, String status) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM (");
        sql.append("  SELECT ROW_NUMBER() OVER (ORDER BY r.submit_time DESC) AS row_num, ");
        sql.append("    r.*, o.owner_name, o.phone AS owner_phone ");
        sql.append("  FROM repair_records r ");
        sql.append("  LEFT JOIN owners o ON r.owner_id = o.owner_id ");
        sql.append("  WHERE 1=1 ");

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasStatus = status != null && !status.trim().isEmpty();

        if (hasKeyword) {
            sql.append("  AND (r.owner_id LIKE ? OR o.owner_name LIKE ? OR r.house_id LIKE ?) ");
        }
        if (hasStatus) {
            sql.append("  AND r.repair_status = ? ");
        }

        sql.append(") AS temp ");
        sql.append("WHERE row_num BETWEEN ? AND ?");

        int start = (pageNum - 1) * pageSize + 1;
        int end = pageNum * pageSize;

        List<Object> params = new java.util.ArrayList<>();
        if (hasKeyword) {
            String likeKeyword = "%" + keyword + "%";
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);
        }
        if (hasStatus) {
            params.add(status);
        }
        params.add(start);
        params.add(end);

        return query(sql.toString(), this::mapRepairRecord, params.toArray());
    }

    /**
     * 统计报修记录总数
     */
    public long count(String keyword, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM repair_records r WHERE 1=1 ");

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasStatus = status != null && !status.trim().isEmpty();

        List<Object> params = new java.util.ArrayList<>();

        if (hasKeyword) {
            sql.append("AND (r.owner_id LIKE ? OR r.house_id LIKE ?) ");
            String likeKeyword = "%" + keyword + "%";
            params.add(likeKeyword);
            params.add(likeKeyword);
        }
        if (hasStatus) {
            sql.append("AND r.repair_status = ? ");
            params.add(status);
        }

        return queryForLong(sql.toString(), params.toArray());
    }

    /**
     * 根据业主ID查询报修记录
     */
    public List<RepairRecord> findByOwnerId(String ownerId) {
        String sql = "SELECT r.*, o.owner_name, o.phone AS owner_phone " +
                "FROM repair_records r " +
                "LEFT JOIN owners o ON r.owner_id = o.owner_id " +
                "WHERE r.owner_id = ? " +
                "ORDER BY r.submit_time DESC";
        return query(sql, this::mapRepairRecord, ownerId);
    }

    /**
     * 查询待处理报修
     */
    public List<RepairRecord> findPendingRepairs() {
        String sql = "SELECT r.*, o.owner_name, o.phone AS owner_phone " +
                "FROM repair_records r " +
                "LEFT JOIN owners o ON r.owner_id = o.owner_id " +
                "WHERE r.repair_status = 'pending' " +
                "ORDER BY r.priority DESC, r.submit_time";
        return query(sql, this::mapRepairRecord);
    }

    /**
     * 添加报修记录
     */
    public Integer insert(RepairRecord record) {
        String sql = "INSERT INTO repair_records (owner_id, house_id, repair_type, description, " +
                "repair_status, priority) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        return insertAndGetKey(sql,
                record.getOwnerId(),
                record.getHouseId(),
                record.getRepairType(),
                record.getDescription(),
                record.getRepairStatus() != null ? record.getRepairStatus() : STATUS_PENDING,
                record.getPriority() != null ? record.getPriority() : "normal"
        );
    }

    /**
     * 更新报修记录
     */
    public int update(RepairRecord record) {
        String sql = "UPDATE repair_records SET repair_type = ?, description = ?, " +
                "repair_status = ?, priority = ?, handler = ?, handler_phone = ?, " +
                "repair_result = ? " +
                "WHERE repair_id = ?";
        return update(sql,
                record.getRepairType(),
                record.getDescription(),
                record.getRepairStatus(),
                record.getPriority(),
                record.getHandler(),
                record.getHandlerPhone(),
                record.getRepairResult(),
                record.getRepairId()
        );
    }

    /**
     * 受理报修
     */
    public int acceptRepair(Integer repairId, String handler, String handlerPhone) {
        String sql = "UPDATE repair_records SET repair_status = ?, " +
                "accept_time = GETDATE(), handler = ?, handler_phone = ? " +
                "WHERE repair_id = ?";
        return update(sql, STATUS_PROCESSING, handler, handlerPhone, repairId);
    }

    /**
     * 完成报修
     */
    public int completeRepair(Integer repairId, String repairResult) {
        String sql = "UPDATE repair_records SET repair_status = ?, " +
                "complete_time = GETDATE(), repair_result = ? " +
                "WHERE repair_id = ?";
        return update(sql, STATUS_COMPLETED, repairResult, repairId);
    }

    /**
     * 取消报修
     */
    public int cancelRepair(Integer repairId) {
        String sql = "UPDATE repair_records SET repair_status = ?, " +
                "complete_time = GETDATE() " +
                "WHERE repair_id = ? AND repair_status IN (?, ?)";
        return update(sql, STATUS_CANCELLED, repairId, STATUS_PENDING, STATUS_PROCESSING);
    }

    /**
     * 取消报修（带原因）
     */
    public int cancelRepair(Integer repairId, String cancelReason) {
        String sql = "UPDATE repair_records SET repair_status = ?, " +
                "complete_time = GETDATE(), repair_result = ? " +
                "WHERE repair_id = ? AND repair_status IN (?, ?)";
        return update(sql, STATUS_CANCELLED, "取消原因: " + cancelReason, repairId,
                STATUS_PENDING, STATUS_PROCESSING);
    }

    /**
     * 取消报修（带操作人和原因）
     */
    public int cancelRepair(Integer repairId, String cancelledBy, String cancelReason) {
        String sql = "UPDATE repair_records SET repair_status = ?, " +
                "complete_time = GETDATE(), handler = ?, repair_result = ? " +
                "WHERE repair_id = ? AND repair_status IN (?, ?)";
        return update(sql, STATUS_CANCELLED, cancelledBy, "取消原因: " + cancelReason,
                repairId, STATUS_PENDING, STATUS_PROCESSING);
    }

    /**
     * 评价报修
     */
    public int rateRepair(Integer repairId, Short rating, String feedback) {
        String sql = "UPDATE repair_records SET satisfaction_rating = ?, feedback = ? " +
                "WHERE repair_id = ?";
        return update(sql, rating, feedback, repairId);
    }

    /**
     * 删除报修记录
     */
    public int delete(Integer repairId) {
        String sql = "DELETE FROM repair_records WHERE repair_id = ?";
        return update(sql, repairId);
    }

    /**
     * 统计各状态报修数量
     */
    public java.util.Map<String, Long> countByStatus() {
        String sql = "SELECT repair_status, COUNT(*) AS cnt FROM repair_records GROUP BY repair_status";
        java.util.Map<String, Long> map = new java.util.HashMap<>();

        query(sql, rs -> {
            map.put(rs.getString("repair_status"), rs.getLong("cnt"));
            return null;
        });

        return map;
    }

    /**
     * 统计指定状态的报修数量
     */
    public long countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM repair_records WHERE repair_status = ?";
        return queryForLong(sql, status);
    }

    /**
     * 查询指定状态的报修记录
     */
    public List<RepairRecord> findByStatus(String status) {
        String sql = "SELECT r.*, o.owner_name, o.phone AS owner_phone " +
                "FROM repair_records r " +
                "LEFT JOIN owners o ON r.owner_id = o.owner_id " +
                "WHERE r.repair_status = ? " +
                "ORDER BY r.submit_time DESC";
        return query(sql, this::mapRepairRecord, status);
    }

    /**
     * 检查报修是否可以取消
     */
    public boolean canCancel(Integer repairId) {
        String sql = "SELECT repair_status FROM repair_records WHERE repair_id = ?";
        RepairRecord record = queryOne(sql, rs -> {
            RepairRecord r = new RepairRecord();
            r.setRepairStatus(rs.getString("repair_status"));
            return r;
        }, repairId);

        if (record == null) {
            return false;
        }

        String status = record.getRepairStatus();
        return STATUS_PENDING.equals(status) || STATUS_PROCESSING.equals(status);
    }

    /**
     * 映射结果集到RepairRecord对象
     */
    private RepairRecord mapRepairRecord(ResultSet rs) throws SQLException {
        RepairRecord record = new RepairRecord();
        record.setRepairId(rs.getInt("repair_id"));
        record.setOwnerId(rs.getString("owner_id"));
        record.setHouseId(rs.getString("house_id"));
        record.setRepairType(rs.getString("repair_type"));
        record.setDescription(rs.getString("description"));
        record.setRepairStatus(rs.getString("repair_status"));
        record.setPriority(rs.getString("priority"));
        record.setSubmitTime(rs.getTimestamp("submit_time"));
        record.setAcceptTime(rs.getTimestamp("accept_time"));
        record.setCompleteTime(rs.getTimestamp("complete_time"));
        record.setHandler(rs.getString("handler"));
        record.setHandlerPhone(rs.getString("handler_phone"));
        record.setRepairResult(rs.getString("repair_result"));

        // 使用 getShort() 读取 TINYINT 类型
        Short rating = rs.getShort("satisfaction_rating");
        if (!rs.wasNull()) {
            record.setSatisfactionRating(rating);
        }

        record.setFeedback(rs.getString("feedback"));

        // 关联字段
        try {
            record.setOwnerName(rs.getString("owner_name"));
            record.setOwnerPhone(rs.getString("owner_phone"));
        } catch (SQLException e) {
            // 某些查询可能不包含这些字段
        }

        return record;
    }

    /**
     * 根据状态获取报修数量
     */
    public int getCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM repair_records WHERE repair_status = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            logger.error("获取报修数量失败：status={}", status, e);
        }
        return 0;
    }

    // ==================== 🔥 业主端方法 ====================

    /**
     * 🔥 业主端：分页查询业主的报修记录
     */
    public List<RepairRecord> findByPageForOwner(int pageNum, int pageSize, String ownerId, String status) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM (");
        sql.append("  SELECT ROW_NUMBER() OVER (ORDER BY r.submit_time DESC) AS row_num, ");
        sql.append("    r.*, o.owner_name, o.phone AS owner_phone ");
        sql.append("  FROM repair_records r ");
        sql.append("  LEFT JOIN owners o ON r.owner_id = o.owner_id ");
        sql.append("  WHERE r.owner_id = ? ");

        List<Object> params = new java.util.ArrayList<>();
        params.add(ownerId);

        if (status != null && !status.trim().isEmpty()) {
            sql.append("  AND r.repair_status = ? ");
            params.add(status);
        }

        sql.append(") AS temp ");
        sql.append("WHERE row_num BETWEEN ? AND ?");

        int start = (pageNum - 1) * pageSize + 1;
        int end = pageNum * pageSize;
        params.add(start);
        params.add(end);

        return query(sql.toString(), this::mapRepairRecord, params.toArray());
    }

    /**
     * 🔥 业主端：统计业主的报修总数
     */
    public int countByOwner(String ownerId, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM repair_records WHERE owner_id = ? ");

        List<Object> params = new java.util.ArrayList<>();
        params.add(ownerId);

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND repair_status = ? ");
            params.add(status);
        }

        // ✅ 修复：安全转换 Long 为 int
        Long count = queryForLong(sql.toString(), params.toArray());
        return count != null ? count.intValue() : 0;
    }

    /**
     * 🔥 业主端：统计业主指定状态的报修数量
     */
    public int countByOwnerAndStatus(String ownerId, String status) {
        String sql = "SELECT COUNT(*) FROM repair_records WHERE owner_id = ? AND repair_status = ?";

        // ✅ 修复：安全转换 Long 为 int
        Long count = queryForLong(sql, ownerId, status);
        return count != null ? count.intValue() : 0;
    }

    /**
     * 🔥 业主端：查询业主最近的报修记录
     */
    public List<RepairRecord> findRecentByOwner(String ownerId, int limit) {
        String sql = "SELECT TOP (?) r.*, o.owner_name, o.phone AS owner_phone " +
                "FROM repair_records r " +
                "LEFT JOIN owners o ON r.owner_id = o.owner_id " +
                "WHERE r.owner_id = ? " +
                "ORDER BY r.submit_time DESC";
        return query(sql, this::mapRepairRecord, limit, ownerId);
    }

    /**
     * 🔥 业主端：追加报修说明
     */
    public int appendDescription(Integer repairId, String additionalDesc) {
        String sql = "UPDATE repair_records SET description = description + CHAR(13) + CHAR(10) + " +
                "'【' + CONVERT(VARCHAR, GETDATE(), 120) + ' 追加】' + CHAR(13) + CHAR(10) + ? " +
                "WHERE repair_id = ?";
        return update(sql, additionalDesc, repairId);
    }

    /**
     * 🔥 业主端：查询可评价的报修（已完成且未评价）
     */
    public List<RepairRecord> findRatableByOwner(String ownerId) {
        String sql = "SELECT r.*, o.owner_name, o.phone AS owner_phone " +
                "FROM repair_records r " +
                "LEFT JOIN owners o ON r.owner_id = o.owner_id " +
                "WHERE r.owner_id = ? " +
                "AND r.repair_status = 'completed' " +
                "AND r.satisfaction_rating IS NULL " +
                "ORDER BY r.complete_time DESC";
        return query(sql, this::mapRepairRecord, ownerId);
    }

    /**
     * 🔥 业主端：查询业主的报修统计（按类型）
     */
    public Map<String, Integer> countByOwnerGroupByType(String ownerId) {
        String sql = "SELECT repair_type, COUNT(*) AS cnt " +
                "FROM repair_records " +
                "WHERE owner_id = ? " +
                "GROUP BY repair_type";

        Map<String, Integer> map = new java.util.HashMap<>();

        query(sql, rs -> {
            map.put(rs.getString("repair_type"), rs.getInt("cnt"));
            return null;
        }, ownerId);

        return map;
    }

    /**
     * 🔥 业主端：查询业主的平均满意度
     */
    public Double getAverageSatisfactionByOwner(String ownerId) {
        String sql = "SELECT AVG(CAST(satisfaction_rating AS FLOAT)) " +
                "FROM repair_records " +
                "WHERE owner_id = ? AND satisfaction_rating IS NOT NULL";

        try {
            // ✅ 修复：安全转换 Long 为 Double
            Long result = queryForLong(sql, ownerId);
            return result != null ? result.doubleValue() : 0.0;
        } catch (Exception e) {
            logger.error("查询平均满意度失败", e);
            return 0.0;
        }
    }
    /**
     * 🔥 获取平均满意度（所有已评价的报修）
     */
    public Double getAverageSatisfaction() {
        String sql = "SELECT AVG(CAST(satisfaction_rating AS FLOAT)) " +
                "FROM repair_records " +
                "WHERE satisfaction_rating IS NOT NULL";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                double avg = rs.getDouble(1);
                if (!rs.wasNull()) {
                    return Math.round(avg * 10.0) / 10.0; // 保留1位小数
                }
            }
        } catch (Exception e) {
            logger.error("获取平均满意度失败", e);
        }

        return 0.0;
    }
    public int countByHouseId(String houseId) {
        String sql = "SELECT COUNT(*) FROM repair_records WHERE house_id = ?";
        Long count = queryForLong(sql, houseId);
        return count != null ? count.intValue() : 0;
    }
}
