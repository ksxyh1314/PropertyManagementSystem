package com.property.dao;

import com.property.entity.PaymentRecord;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 缴费记录DAO
 */
public class PaymentRecordDao extends BaseDao {

    /**
     * 根据记录ID查询
     */
    public PaymentRecord findById(Integer recordId) {
        String sql = "SELECT * FROM view_owner_payment_details WHERE record_id = ?";
        return queryOne(sql, this::mapPaymentRecord, recordId);
    }

    /**
     * 查询所有缴费记录
     */
    public List<PaymentRecord> findAll() {
        String sql = "SELECT * FROM view_owner_payment_details ORDER BY due_date DESC";
        return query(sql, this::mapPaymentRecord);
    }

    /**
     * 分页查询缴费记录
     */
    public List<PaymentRecord> findByPage(int pageNum, int pageSize, String keyword, String status) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM (");
        sql.append("  SELECT ROW_NUMBER() OVER (ORDER BY due_date DESC) AS row_num, * ");
        sql.append("  FROM view_owner_payment_details WHERE 1=1 ");

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasStatus = status != null && !status.trim().isEmpty();

        if (hasKeyword) {
            sql.append("  AND (owner_id LIKE ? OR owner_name LIKE ? OR house_id LIKE ?) ");
        }
        if (hasStatus) {
            sql.append("  AND payment_status = ? ");
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

        return query(sql.toString(), this::mapPaymentRecord, params.toArray());
    }

    /**
     * 统计缴费记录总数
     */
    public long count(String keyword, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM payment_records pr WHERE 1=1 ");

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasStatus = status != null && !status.trim().isEmpty();

        List<Object> params = new java.util.ArrayList<>();

        if (hasKeyword) {
            sql.append("AND (pr.owner_id LIKE ? OR pr.house_id LIKE ?) ");
            String likeKeyword = "%" + keyword + "%";
            params.add(likeKeyword);
            params.add(likeKeyword);
        }
        if (hasStatus) {
            sql.append("AND pr.payment_status = ? ");
            params.add(status);
        }

        return queryForLong(sql.toString(), params.toArray());
    }

    /**
     * 根据业主ID查询缴费记录
     */
    public List<PaymentRecord> findByOwnerId(String ownerId) {
        String sql = "SELECT * FROM view_owner_payment_details WHERE owner_id = ? ORDER BY due_date DESC";
        return query(sql, this::mapPaymentRecord, ownerId);
    }

    /**
     * 查询业主未缴费记录
     */
    public List<PaymentRecord> findUnpaidByOwnerId(String ownerId) {
        String sql = "SELECT * FROM view_owner_payment_details " +
                "WHERE owner_id = ? AND payment_status IN ('unpaid', 'overdue') " +
                "ORDER BY due_date";
        return query(sql, this::mapPaymentRecord, ownerId);
    }

    /**
     * 查询业主已缴费记录
     */
    public List<PaymentRecord> findPaidByOwnerId(String ownerId) {
        String sql = "SELECT * FROM view_owner_payment_details " +
                "WHERE owner_id = ? AND payment_status = 'paid' " +
                "ORDER BY payment_date DESC";
        return query(sql, this::mapPaymentRecord, ownerId);
    }

    /**
     * 查询逾期记录
     */
    public List<PaymentRecord> findOverdueRecords() {
        String sql = "SELECT * FROM view_owner_payment_details " +
                "WHERE payment_status = 'overdue' " +
                "ORDER BY overdue_days DESC";
        return query(sql, this::mapPaymentRecord);
    }

    /**
     * 添加缴费记录
     */
    public Integer insert(PaymentRecord record) {
        String sql = "INSERT INTO payment_records (owner_id, house_id, item_id, billing_period, " +
                "amount, late_fee, due_date, payment_status, remark) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return insertAndGetKey(sql,
                record.getOwnerId(),
                record.getHouseId(),
                record.getItemId(),
                record.getBillingPeriod(),
                record.getAmount(),
                record.getLateFee() != null ? record.getLateFee() : 0,
                record.getDueDate(),
                record.getPaymentStatus() != null ? record.getPaymentStatus() : "unpaid",
                record.getRemark()
        );
    }

    /**
     * 更新缴费记录
     */
    public int update(PaymentRecord record) {
        String sql = "UPDATE payment_records SET amount = ?, late_fee = ?, due_date = ?, " +
                "payment_status = ?, remark = ?, update_time = GETDATE() " +
                "WHERE record_id = ?";
        return update(sql,
                record.getAmount(),
                record.getLateFee(),
                record.getDueDate(),
                record.getPaymentStatus(),
                record.getRemark(),
                record.getRecordId()
        );
    }

    /**
     * 处理缴费（调用存储过程）
     */
    public Map<String, Object> processPayment(Integer recordId, String paymentMethod, Integer operatorId) {
        String sql = "{CALL sp_process_payment(?, ?, ?, ?, ?)}";
        Map<String, Object> result = new HashMap<>();

        callProcedure(sql, cstmt -> {
            cstmt.setInt(1, recordId);
            cstmt.setString(2, paymentMethod);
            cstmt.setInt(3, operatorId);
            cstmt.registerOutParameter(4, Types.NVARCHAR);
            cstmt.registerOutParameter(5, Types.NVARCHAR);

            cstmt.execute();

            result.put("receiptNo", cstmt.getString(4));
            result.put("message", cstmt.getString(5));
        });

        return result;
    }

    /**
     * 生成物业费账单（调用存储过程）
     */
    public void generatePropertyBill(String billingPeriod, Date dueDate, String itemId) {
        String sql = "{CALL sp_generate_property_bill(?, ?, ?)}";

        callProcedure(sql, cstmt -> {
            cstmt.setString(1, billingPeriod);
            cstmt.setDate(2, new java.sql.Date(dueDate.getTime()));
            cstmt.setString(3, itemId);
            cstmt.execute();
        });
    }

    /**
     * 删除缴费记录
     */
    public int delete(Integer recordId) {
        String sql = "DELETE FROM payment_records WHERE record_id = ?";
        return update(sql, recordId);
    }

    /**
     * 统计业主欠费总额
     */
    public java.math.BigDecimal sumUnpaidAmount(String ownerId) {
        String sql = "SELECT ISNULL(SUM(total_amount), 0) FROM payment_records " +
                "WHERE owner_id = ? AND payment_status IN ('unpaid', 'overdue')";
        Long amount = queryForLong(sql, ownerId);
        return new java.math.BigDecimal(amount != null ? amount : 0);
    }

    /**
     * 统计某时间段内的收费情况
     */
    public Map<String, Object> statisticsByPeriod(Date startDate, Date endDate) {
        String sql = "SELECT " +
                "  COUNT(*) AS total_count, " +
                "  SUM(CASE WHEN payment_status = 'paid' THEN 1 ELSE 0 END) AS paid_count, " +
                "  SUM(amount) AS total_amount, " +
                "  SUM(CASE WHEN payment_status = 'paid' THEN amount ELSE 0 END) AS paid_amount, " +
                "  SUM(late_fee) AS total_late_fee " +
                "FROM payment_records " +
                "WHERE due_date BETWEEN ? AND ?";

        Map<String, Object> stats = new HashMap<>();

        List<Map<String, Object>> result = query(sql, rs -> {
            Map<String, Object> map = new HashMap<>();
            map.put("totalCount", rs.getLong("total_count"));
            map.put("paidCount", rs.getLong("paid_count"));
            map.put("totalAmount", rs.getBigDecimal("total_amount"));
            map.put("paidAmount", rs.getBigDecimal("paid_amount"));
            map.put("totalLateFee", rs.getBigDecimal("total_late_fee"));
            return map;
        }, startDate, endDate);

        return result.isEmpty() ? stats : result.get(0);
    }

    /**
     * 查询物业收费统计（调用视图）
     */
    public List<Map<String, Object>> getPaymentStatistics(String startMonth, String endMonth) {
        String sql = "SELECT * FROM view_payment_statistics " +
                "WHERE stat_month BETWEEN ? AND ? " +
                "ORDER BY stat_month DESC, item_id";

        return query(sql, rs -> {
            Map<String, Object> map = new HashMap<>();
            map.put("statMonth", rs.getString("stat_month"));
            map.put("itemId", rs.getString("item_id"));
            map.put("itemName", rs.getString("item_name"));
            map.put("totalRecords", rs.getInt("total_records"));
            map.put("paidCount", rs.getInt("paid_count"));
            map.put("unpaidCount", rs.getInt("unpaid_count"));
            map.put("overdueCount", rs.getInt("overdue_count"));
            map.put("totalAmount", rs.getBigDecimal("total_amount"));
            map.put("paidAmount", rs.getBigDecimal("paid_amount"));
            map.put("unpaidAmount", rs.getBigDecimal("unpaid_amount"));
            map.put("totalLateFee", rs.getBigDecimal("total_late_fee"));
            map.put("paymentRate", rs.getBigDecimal("payment_rate"));
            return map;
        }, startMonth, endMonth);
    }

    /**
     * 查询各楼栋缴费情况（调用视图）
     */
    public List<Map<String, Object>> getBuildingPaymentStatus() {
        String sql = "SELECT * FROM view_building_payment_status ORDER BY building_no";

        return query(sql, rs -> {
            Map<String, Object> map = new HashMap<>();
            map.put("buildingNo", rs.getString("building_no"));
            map.put("totalRecords", rs.getInt("total_records"));
            map.put("paidRecords", rs.getInt("paid_records"));
            map.put("totalAmount", rs.getBigDecimal("total_amount"));
            map.put("paidAmount", rs.getBigDecimal("paid_amount"));
            map.put("unpaidAmount", rs.getBigDecimal("unpaid_amount"));
            map.put("totalLateFee", rs.getBigDecimal("total_late_fee"));
            map.put("paymentRate", rs.getBigDecimal("payment_rate"));
            return map;
        });
    }

    /**
     * 映射结果集到PaymentRecord对象
     */
    private PaymentRecord mapPaymentRecord(ResultSet rs) throws SQLException {
        PaymentRecord record = new PaymentRecord();
        record.setRecordId(rs.getInt("record_id"));
        record.setOwnerId(rs.getString("owner_id"));
        record.setHouseId(rs.getString("house_id"));
        record.setItemId(rs.getString("item_id"));
        record.setBillingPeriod(rs.getString("billing_period"));
        record.setAmount(rs.getBigDecimal("amount"));
        record.setLateFee(rs.getBigDecimal("late_fee"));
        record.setTotalAmount(rs.getBigDecimal("total_amount"));
        record.setDueDate(rs.getDate("due_date"));
        record.setPaymentDate(rs.getTimestamp("payment_date"));
        record.setPaymentMethod(rs.getString("payment_method"));
        record.setPaymentStatus(rs.getString("payment_status"));
        record.setReceiptNo(rs.getString("receipt_no"));
        record.setOperatorId((Integer) rs.getObject("operator_id"));
        record.setRemark(rs.getString("remark"));
        record.setCreateTime(rs.getTimestamp("create_time"));
        record.setUpdateTime(rs.getTimestamp("update_time"));

        // 关联字段
        try {
            record.setOwnerName(rs.getString("owner_name"));
            record.setBuildingNo(rs.getString("building_no"));
            record.setUnitNo(rs.getString("unit_no"));
            record.setFloor(rs.getString("floor"));
            record.setItemName(rs.getString("item_name"));
            record.setOverdueDays(rs.getInt("overdue_days"));
        } catch (SQLException e) {
            // 某些查询可能不包含这些字段
        }

        return record;
    }
}
