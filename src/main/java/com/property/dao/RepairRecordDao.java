package com.property.dao;

import com.property.entity.RepairRecord;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 报修记录DAO
 */
public class RepairRecordDao extends BaseDao {

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
                record.getRepairStatus() != null ? record.getRepairStatus() : "pending",
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
        String sql = "UPDATE repair_records SET repair_status = 'processing', " +
                "accept_time = GETDATE(), handler = ?, handler_phone = ? " +
                "WHERE repair_id = ?";
        return update(sql, handler, handlerPhone, repairId);
    }

    /**
     * 完成报修
     */
    public int completeRepair(Integer repairId, String repairResult) {
        String sql = "UPDATE repair_records SET repair_status = 'completed', " +
                "complete_time = GETDATE(), repair_result = ? " +
                "WHERE repair_id = ?";
        return update(sql, repairResult, repairId);
    }

    /**
     * 评价报修
     */
    public int rateRepair(Integer repairId, Integer rating, String feedback) {
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
        record.setSatisfactionRating((Integer) rs.getObject("satisfaction_rating"));
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
}
