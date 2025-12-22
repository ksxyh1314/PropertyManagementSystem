package com.property.dao;

import com.property.entity.ChargeItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 收费项目DAO
 */
public class ChargeItemDao extends BaseDao {

    /**
     * 根据项目ID查询
     */
    public ChargeItem findById(String itemId) {
        String sql = "SELECT * FROM charge_items WHERE item_id = ?";
        return queryOne(sql, this::mapChargeItem, itemId);
    }

    /**
     * 查询所有收费项目
     */
    public List<ChargeItem> findAll() {
        String sql = "SELECT * FROM charge_items ORDER BY item_id";
        return query(sql, this::mapChargeItem);
    }

    /**
     * 查询启用的收费项目
     */
    public List<ChargeItem> findActive() {
        String sql = "SELECT * FROM charge_items WHERE status = 1 ORDER BY item_id";
        return query(sql, this::mapChargeItem);
    }

    /**
     * 分页查询收费项目
     */
    public List<ChargeItem> findByPage(int pageNum, int pageSize, String keyword) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM (");
        sql.append("  SELECT ROW_NUMBER() OVER (ORDER BY item_id) AS row_num, * ");
        sql.append("  FROM charge_items WHERE 1=1 ");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("  AND (item_id LIKE ? OR item_name LIKE ? OR description LIKE ?) ");
        }

        sql.append(") AS temp ");
        sql.append("WHERE row_num BETWEEN ? AND ?");

        int start = (pageNum - 1) * pageSize + 1;
        int end = pageNum * pageSize;

        if (keyword != null && !keyword.trim().isEmpty()) {
            String likeKeyword = "%" + keyword + "%";
            return query(sql.toString(), this::mapChargeItem,
                    likeKeyword, likeKeyword, likeKeyword, start, end);
        } else {
            return query(sql.toString(), this::mapChargeItem, start, end);
        }
    }

    /**
     * 统计收费项目总数
     */
    public long count(String keyword) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM charge_items WHERE 1=1 ");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (item_id LIKE ? OR item_name LIKE ? OR description LIKE ?)");
            String likeKeyword = "%" + keyword + "%";
            return queryForLong(sql.toString(), likeKeyword, likeKeyword, likeKeyword);
        }

        return queryForLong(sql.toString());
    }

    /**
     * 添加收费项目
     */
    public int insert(ChargeItem item) {
        String sql = "INSERT INTO charge_items (item_id, item_name, charge_cycle, description, " +
                "calculation_type, fixed_amount, formula, grace_period, late_fee_rate, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return update(sql,
                item.getItemId(),
                item.getItemName(),
                item.getChargeCycle(),
                item.getDescription(),
                item.getCalculationType(),
                item.getFixedAmount(),
                item.getFormula(),
                item.getGracePeriod() != null ? item.getGracePeriod() : 30,
                item.getLateFeeRate() != null ? item.getLateFeeRate() : 0.0005,
                item.getStatus() != null ? item.getStatus() : 1
        );
    }

    /**
     * 更新收费项目
     */
    public int update(ChargeItem item) {
        String sql = "UPDATE charge_items SET item_name = ?, charge_cycle = ?, description = ?, " +
                "calculation_type = ?, fixed_amount = ?, formula = ?, grace_period = ?, " +
                "late_fee_rate = ?, status = ? " +
                "WHERE item_id = ?";
        return update(sql,
                item.getItemName(),
                item.getChargeCycle(),
                item.getDescription(),
                item.getCalculationType(),
                item.getFixedAmount(),
                item.getFormula(),
                item.getGracePeriod(),
                item.getLateFeeRate(),
                item.getStatus(),
                item.getItemId()
        );
    }

    /**
     * 删除收费项目
     */
    public int delete(String itemId) {
        String sql = "DELETE FROM charge_items WHERE item_id = ?";
        return update(sql, itemId);
    }

    /**
     * 启用/禁用收费项目
     */
    public int updateStatus(String itemId, Integer status) {
        String sql = "UPDATE charge_items SET status = ? WHERE item_id = ?";
        return update(sql, status, itemId);
    }

    /**
     * 检查项目ID是否存在
     */
    public boolean existsById(String itemId) {
        String sql = "SELECT COUNT(*) FROM charge_items WHERE item_id = ?";
        return queryForLong(sql, itemId) > 0;
    }

    /**
     * 映射结果集到ChargeItem对象
     */
    private ChargeItem mapChargeItem(ResultSet rs) throws SQLException {
        ChargeItem item = new ChargeItem();
        item.setItemId(rs.getString("item_id"));
        item.setItemName(rs.getString("item_name"));
        item.setChargeCycle(rs.getString("charge_cycle"));
        item.setDescription(rs.getString("description"));
        item.setCalculationType(rs.getString("calculation_type"));
        item.setFixedAmount(rs.getBigDecimal("fixed_amount"));
        item.setFormula(rs.getString("formula"));
        item.setGracePeriod(rs.getInt("grace_period"));
        item.setLateFeeRate(rs.getBigDecimal("late_fee_rate"));
        item.setStatus(rs.getInt("status"));
        item.setCreateTime(rs.getTimestamp("create_time"));
        return item;
    }
}
