package com.property.dao;

import com.property.entity.PaymentRecord;
import com.property.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * 缴费记录DAO
 * 对应数据库表：payment_records
 *
 * @author PropertyManagementSystem
 * @version 4.0 - 完全修复字段名一致性问题
 */
public class PaymentRecordDao extends BaseDao {

    private static final Logger logger = LoggerFactory.getLogger(PaymentRecordDao.class);

    private static final String TABLE_NAME = "payment_records";
    private static final String VIEW_PAYMENT_DETAILS = "view_owner_payment_details";

    private static final String STATUS_PAID = "paid";
    private static final String STATUS_UNPAID = "unpaid";
    private static final String STATUS_OVERDUE = "overdue";
    private static final String STATUS_PARTIAL = "partial";

    // ==================== 查询方法 ====================

    public PaymentRecord findById(String recordId) {
        String sql = "SELECT * FROM " + VIEW_PAYMENT_DETAILS + " WHERE record_id = ?";
        return queryOne(sql, this::mapPaymentRecord, recordId);
    }

    public List<PaymentRecord> findAll() {
        String sql = "SELECT * FROM " + VIEW_PAYMENT_DETAILS + " ORDER BY due_date DESC";
        return query(sql, this::mapPaymentRecord);
    }

    public List<PaymentRecord> findByPage(int pageNum, int pageSize, String keyword, String status) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM (");
        sql.append("  SELECT ROW_NUMBER() OVER (ORDER BY due_date DESC) AS row_num, * ");
        sql.append("  FROM ").append(VIEW_PAYMENT_DETAILS).append(" WHERE 1=1 ");

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

        List<Object> params = new ArrayList<>();
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

    public long count(String keyword, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ")
                .append(TABLE_NAME).append(" pr WHERE 1=1 ");

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasStatus = status != null && !status.trim().isEmpty();

        List<Object> params = new ArrayList<>();

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

    public List<PaymentRecord> findByOwnerId(String ownerId) {
        String sql = "SELECT * FROM " + VIEW_PAYMENT_DETAILS +
                " WHERE owner_id = ? ORDER BY due_date DESC";
        return query(sql, this::mapPaymentRecord, ownerId);
    }

    public List<PaymentRecord> findUnpaidByOwnerId(String ownerId) {
        String sql = "SELECT * FROM " + VIEW_PAYMENT_DETAILS +
                " WHERE owner_id = ? AND payment_status IN (?, ?) " +
                " ORDER BY due_date";
        return query(sql, this::mapPaymentRecord, ownerId, STATUS_UNPAID, STATUS_OVERDUE);
    }

    public List<PaymentRecord> findPaidByOwnerId(String ownerId) {
        String sql = "SELECT * FROM " + VIEW_PAYMENT_DETAILS +
                " WHERE owner_id = ? AND payment_status = ? " +
                " ORDER BY payment_date DESC";
        return query(sql, this::mapPaymentRecord, ownerId, STATUS_PAID);
    }

    public List<PaymentRecord> findOverdueRecords() {
        String sql = "SELECT * FROM " + VIEW_PAYMENT_DETAILS +
                " WHERE payment_status = ? " +
                " ORDER BY overdue_days DESC";
        return query(sql, this::mapPaymentRecord, STATUS_OVERDUE);
    }

    // ==================== 增删改方法 ====================

    /**
     * ✅ 修复：移除 record_id，让数据库自动生成
     * ✅ 修复：owner_id 类型匹配问题 (setInt -> setString)
     */
    public String insert(PaymentRecord record) {
        String sql = "INSERT INTO " + TABLE_NAME + " (" +
                "owner_id, house_id, item_id, " +
                "billing_period, amount, late_fee, due_date, " +
                "payment_status, remark, create_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            // 使用 RETURN_GENERATED_KEYS 获取自增ID
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // -------------------------------------------------------
            // 🔴 修复点：ownerId 是 String 类型，必须用 setString
            // -------------------------------------------------------
            pstmt.setString(1, record.getOwnerId());

            pstmt.setString(2, record.getHouseId());
            pstmt.setString(3, record.getItemId());
            pstmt.setString(4, record.getBillingPeriod());
            pstmt.setBigDecimal(5, record.getAmount());
            pstmt.setBigDecimal(6, record.getLateFee() != null ? record.getLateFee() : BigDecimal.ZERO);
            pstmt.setDate(7, new java.sql.Date(record.getDueDate().getTime()));
            pstmt.setString(8, record.getPaymentStatus() != null ? record.getPaymentStatus() : STATUS_UNPAID);
            pstmt.setString(9, record.getRemark());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                // 获取数据库自动生成的 ID
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    String generatedId = rs.getString(1);
                    record.setRecordId(generatedId);
                    logger.info("✅ 插入缴费记录成功: recordId={}", generatedId);
                    return generatedId;
                }
            }

            logger.warn("❌ 插入缴费记录失败");
            return null;

        } catch (Exception e) {
            logger.error("❌ 插入缴费记录异常", e);
            return null;
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }


    /**
     * ✅ 修复：使用 update_time
     */
    public int update(PaymentRecord record) {
        String sql = "UPDATE " + TABLE_NAME + " SET " +
                "amount = ?, late_fee = ?, due_date = ?, " +
                "payment_status = ?, remark = ?, update_time = GETDATE() " +  // ✅ update_time
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
     * ✅ 修复：使用 update_time
     */
    public boolean updatePaymentStatus(String recordId, String status, String paymentMethod,
                                       String receiptNo, Integer operatorId) {
        String sql = "UPDATE " + TABLE_NAME + " SET " +
                "payment_status = ?, " +
                "payment_method = ?, " +
                "receipt_no = ?, " +
                "operator_id = ?, " +
                "payment_date = GETDATE(), " +
                "update_time = GETDATE() " +  // ✅ update_time
                "WHERE record_id = ?";

        try {
            int rows = update(sql, status, paymentMethod, receiptNo, operatorId, recordId);
            return rows > 0;
        } catch (Exception e) {
            logger.error("❌ 更新缴费状态失败: recordId={}", recordId, e);
            return false;
        }
    }

    public Map<String, Object> processPayment(String recordId, String paymentMethod, Integer operatorId) {
        String sql = "{CALL sp_process_payment(?, ?, ?, ?, ?)}";
        Map<String, Object> result = new HashMap<>();

        try {
            callProcedure(sql, cstmt -> {
                cstmt.setString(1, recordId);
                cstmt.setString(2, paymentMethod);
                cstmt.setInt(3, operatorId);
                cstmt.registerOutParameter(4, Types.NVARCHAR);
                cstmt.registerOutParameter(5, Types.NVARCHAR);

                cstmt.execute();

                result.put("receiptNo", cstmt.getString(4));
                result.put("message", cstmt.getString(5));
            });
        } catch (Exception e) {
            logger.error("❌ 处理缴费失败: recordId={}", recordId, e);
            result.put("success", false);
            result.put("message", "缴费处理失败: " + e.getMessage());
        }

        return result;
    }

    public void generatePropertyBill(String billingPeriod, Date dueDate, String itemId) {
        String sql = "{CALL sp_generate_property_bill(?, ?, ?)}";

        try {
            callProcedure(sql, cstmt -> {
                cstmt.setString(1, billingPeriod);  // ✅ billingPeriod
                cstmt.setDate(2, new java.sql.Date(dueDate.getTime()));
                cstmt.setString(3, itemId);
                cstmt.execute();
            });
            logger.info("✅ 生成账单成功: 账期={}, 项目ID={}", billingPeriod, itemId);
        } catch (Exception e) {
            logger.error("❌ 生成账单失败: 账期={}, 项目ID={}", billingPeriod, itemId, e);
            throw new RuntimeException("生成账单失败", e);
        }
    }


    public int delete(String recordId) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE record_id = ?";
        return update(sql, recordId);
    }

    public int batchDelete(List<String> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            return 0;
        }

        StringBuilder sql = new StringBuilder("DELETE FROM ")
                .append(TABLE_NAME)
                .append(" WHERE record_id IN (");

        for (int i = 0; i < recordIds.size(); i++) {
            sql.append(i == 0 ? "?" : ",?");
        }
        sql.append(")");

        return update(sql.toString(), recordIds.toArray());
    }

    // ==================== 统计方法 ====================

    public BigDecimal sumUnpaidAmount(String ownerId) {
        String sql = "SELECT ISNULL(SUM(amount + ISNULL(late_fee, 0)), 0) FROM " + TABLE_NAME +
                " WHERE owner_id = ? AND payment_status IN (?, ?)";

        Long amount = queryForLong(sql, ownerId, STATUS_UNPAID, STATUS_OVERDUE);
        return new BigDecimal(amount != null ? amount : 0);
    }

    public Map<String, Object> statisticsByPeriod(Date startDate, Date endDate) {
        String sql = "SELECT " +
                "  COUNT(*) AS total_count, " +
                "  SUM(CASE WHEN payment_status = ? THEN 1 ELSE 0 END) AS paid_count, " +
                "  ISNULL(SUM(amount), 0) AS total_amount, " +
                "  ISNULL(SUM(CASE WHEN payment_status = ? THEN amount ELSE 0 END), 0) AS paid_amount, " +
                "  ISNULL(SUM(late_fee), 0) AS total_late_fee " +
                "FROM " + TABLE_NAME + " " +
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
        }, STATUS_PAID, STATUS_PAID, startDate, endDate);

        return result.isEmpty() ? stats : result.get(0);
    }

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

    public Map<String, Object> getStatistics(String keyword, String status) {
        Map<String, Object> result = new HashMap<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("  COUNT(*) AS total_count, ");
        sql.append("  ISNULL(SUM(amount), 0) AS total_amount, ");
        sql.append("  SUM(CASE WHEN payment_status = ? THEN 1 ELSE 0 END) AS paid_count, ");
        sql.append("  ISNULL(SUM(CASE WHEN payment_status = ? THEN amount ELSE 0 END), 0) AS paid_amount, ");
        sql.append("  SUM(CASE WHEN payment_status = ? THEN 1 ELSE 0 END) AS unpaid_count, ");
        sql.append("  ISNULL(SUM(CASE WHEN payment_status = ? THEN amount ELSE 0 END), 0) AS unpaid_amount, ");
        sql.append("  SUM(CASE WHEN payment_status = ? THEN 1 ELSE 0 END) AS overdue_count, ");
        sql.append("  ISNULL(SUM(CASE WHEN payment_status = ? THEN amount ELSE 0 END), 0) AS overdue_amount ");
        sql.append("FROM ").append(VIEW_PAYMENT_DETAILS).append(" WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        params.add(STATUS_PAID);
        params.add(STATUS_PAID);
        params.add(STATUS_UNPAID);
        params.add(STATUS_UNPAID);
        params.add(STATUS_OVERDUE);
        params.add(STATUS_OVERDUE);

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (owner_id LIKE ? OR owner_name LIKE ? OR house_id LIKE ?) ");
            String likeKeyword = "%" + keyword.trim() + "%";
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);
        }

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND payment_status = ? ");
            params.add(status);
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result.put("totalCount", rs.getInt("total_count"));
                result.put("totalAmount", rs.getBigDecimal("total_amount"));
                result.put("paidCount", rs.getInt("paid_count"));
                result.put("paidAmount", rs.getBigDecimal("paid_amount"));
                result.put("unpaidCount", rs.getInt("unpaid_count"));
                result.put("unpaidAmount", rs.getBigDecimal("unpaid_amount"));
                result.put("overdueCount", rs.getInt("overdue_count"));
                result.put("overdueAmount", rs.getBigDecimal("overdue_amount"));
            }

        } catch (SQLException e) {
            logger.error("❌ 获取统计数据失败", e);
        }

        return result;
    }

    public List<Map<String, Object>> getMonthlyStatistics() {
        List<Map<String, Object>> result = new ArrayList<>();

        String sql = "SELECT " +
                "  FORMAT(due_date, 'yyyy-MM') AS month, " +
                "  COUNT(*) AS count, " +
                "  ISNULL(SUM(amount), 0) AS amount, " +
                "  ISNULL(SUM(CASE WHEN payment_status = ? THEN amount ELSE 0 END), 0) AS paid_amount " +
                "FROM " + VIEW_PAYMENT_DETAILS + " " +
                "WHERE due_date >= DATEADD(MONTH, -12, GETDATE()) " +
                "GROUP BY FORMAT(due_date, 'yyyy-MM') " +
                "ORDER BY month DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, STATUS_PAID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("month", rs.getString("month"));
                row.put("count", rs.getInt("count"));
                row.put("amount", rs.getBigDecimal("amount"));
                row.put("paidAmount", rs.getBigDecimal("paid_amount"));
                result.add(row);
            }

        } catch (SQLException e) {
            logger.error("❌ 获取月度统计数据失败", e);
        }

        return result;
    }

    public List<Map<String, Object>> getFeeTypeStatistics(String keyword, String status) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("  v.item_id, ");
        sql.append("  ISNULL(v.item_name, '未分类') AS item_name, ");
        sql.append("  COUNT(*) AS total_count, ");
        sql.append("  ISNULL(SUM(v.amount), 0) AS total_amount, ");
        sql.append("  ISNULL(SUM(CASE WHEN v.payment_status = ? THEN v.amount ELSE 0 END), 0) AS paid_amount, ");
        sql.append("  ISNULL(SUM(v.late_fee), 0) AS late_fee ");
        sql.append("FROM ").append(VIEW_PAYMENT_DETAILS).append(" v ");
        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        params.add(STATUS_PAID);

        appendSearchConditions(sql, params, keyword, status);

        sql.append("GROUP BY v.item_id, v.item_name ");
        sql.append("ORDER BY total_amount DESC");

        logger.info("执行费用类型统计SQL: {}", sql.toString());
        logger.info("参数: {}", params);

        List<Map<String, Object>> result = query(sql.toString(), rs -> {
            Map<String, Object> row = new HashMap<>();

            String itemId = rs.getString("item_id");
            String itemName = rs.getString("item_name");
            int totalCount = rs.getInt("total_count");
            BigDecimal totalAmount = rs.getBigDecimal("total_amount");
            BigDecimal paidAmount = rs.getBigDecimal("paid_amount");
            BigDecimal lateFee = rs.getBigDecimal("late_fee");

            logger.info("费用项目: itemId={}, itemName={}, count={}, total={}, paid={}",
                    itemId, itemName, totalCount, totalAmount, paidAmount);

            row.put("itemId", itemId);
            row.put("itemName", itemName != null && !itemName.isEmpty() ? itemName : "未分类");
            row.put("totalCount", totalCount);
            row.put("totalAmount", totalAmount);
            row.put("paidAmount", paidAmount);
            row.put("lateFee", lateFee);

            if (totalAmount != null && totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal rate = paidAmount.divide(totalAmount, 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(new BigDecimal("100"));
                row.put("paymentRate", rate);
            } else {
                row.put("paymentRate", BigDecimal.ZERO);
            }

            return row;
        }, params.toArray());

        logger.info("费用类型统计结果数量: {}", result.size());

        return result;
    }

    private void appendSearchConditions(StringBuilder sql, List<Object> params,
                                        String keyword, String status) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (v.owner_id LIKE ? OR v.owner_name LIKE ? OR v.house_id LIKE ?) ");
            String likeKeyword = "%" + keyword.trim() + "%";
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);
        }

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND v.payment_status = ? ");
            params.add(status);
        }
    }

    public Map<String, Object> getIncomeStatistics(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("  COUNT(*) AS total_count, ");
        sql.append("  ISNULL(SUM(amount), 0) AS total_amount, ");
        sql.append("  ISNULL(SUM(late_fee), 0) AS total_late_fee, ");
        sql.append("  ISNULL(SUM(amount + ISNULL(late_fee, 0)), 0) AS total_income ");
        sql.append("FROM ").append(TABLE_NAME).append(" ");
        sql.append("WHERE payment_status = ? ");

        List<Object> params = new ArrayList<>();
        params.add(STATUS_PAID);

        if (startDate != null && !startDate.trim().isEmpty()) {
            sql.append("AND payment_date >= ? ");
            params.add(startDate);
        }

        if (endDate != null && !endDate.trim().isEmpty()) {
            sql.append("AND payment_date <= ? ");
            params.add(endDate + " 23:59:59");
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result.put("totalCount", rs.getInt("total_count"));
                result.put("totalAmount", rs.getBigDecimal("total_amount"));
                result.put("totalLateFee", rs.getBigDecimal("total_late_fee"));
                result.put("totalIncome", rs.getBigDecimal("total_income"));
            } else {
                result.put("totalCount", 0);
                result.put("totalAmount", BigDecimal.ZERO);
                result.put("totalLateFee", BigDecimal.ZERO);
                result.put("totalIncome", BigDecimal.ZERO);
            }

        } catch (SQLException e) {
            logger.error("❌ 获取收入统计失败", e);
            result.put("totalCount", 0);
            result.put("totalAmount", BigDecimal.ZERO);
            result.put("totalLateFee", BigDecimal.ZERO);
            result.put("totalIncome", BigDecimal.ZERO);
        }

        return result;
    }

    /**
     * ✅ 修复：使用 billing_period
     */
    public boolean existsBill(String ownerId, String houseId, String itemId, String billingPeriod) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " " +
                "WHERE owner_id = ? AND house_id = ? AND item_id = ? AND billing_period = ?";  // ✅ billing_period

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ownerId);
            pstmt.setString(2, houseId);
            pstmt.setString(3, itemId);
            pstmt.setString(4, billingPeriod);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                boolean exists = rs.getInt(1) > 0;
                logger.debug("账单存在性检查: ownerId={}, houseId={}, itemId={}, billingPeriod={}, exists={}",
                        ownerId, houseId, itemId, billingPeriod, exists);
                return exists;
            }
            return false;

        } catch (SQLException e) {
            logger.error("❌ 检查账单是否存在失败: ownerId={}, houseId={}, itemId={}, billingPeriod={}",
                    ownerId, houseId, itemId, billingPeriod, e);
            throw new RuntimeException("检查账单是否存在失败", e);
        }
    }

    /**
     * ✅ 修复：使用 billing_period
     */
    public int countByCondition(String ownerId, String houseId, String itemId, String billingPeriod) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME +
                " WHERE owner_id = ? AND house_id = ? AND item_id = ? AND billing_period = ?";  // ✅ billing_period

        Long count = queryForLong(sql, ownerId, houseId, itemId, billingPeriod);
        return count != null ? count.intValue() : 0;
    }

    // ==================== 映射方法 ====================

    /**
     * ✅ 终极修复版 - 使用 billing_period, create_time, update_time
     */
    private PaymentRecord mapPaymentRecord(ResultSet rs) throws SQLException {
        PaymentRecord record = new PaymentRecord();

        logger.debug("========== 开始映射 PaymentRecord ==========");

        try {
            // 基础字段
            record.setRecordId(rs.getString("record_id"));
            record.setOwnerId(rs.getString("owner_id"));
            record.setHouseId(rs.getString("house_id"));
            record.setItemId(rs.getString("item_id"));

            // ✅ 使用 billing_period
            record.setBillingPeriod(rs.getString("billing_period"));
            logger.debug("✅ billing_period: {}", record.getBillingPeriod());

            // 金额信息
            record.setAmount(rs.getBigDecimal("amount"));
            record.setLateFee(rs.getBigDecimal("late_fee"));

            BigDecimal amount = record.getAmount() != null ? record.getAmount() : BigDecimal.ZERO;
            BigDecimal lateFee = record.getLateFee() != null ? record.getLateFee() : BigDecimal.ZERO;
            record.setTotalAmount(amount.add(lateFee));

            // 日期信息
            record.setDueDate(rs.getDate("due_date"));
            record.setPaymentDate(rs.getTimestamp("payment_date"));

            // ✅ 使用 create_time 和 update_time
            record.setCreateTime(rs.getTimestamp("create_time"));
            record.setUpdateTime(rs.getTimestamp("update_time"));
            logger.debug("✅ create_time: {}, update_time: {}",
                    record.getCreateTime(), record.getUpdateTime());

            // 状态信息
            record.setPaymentStatus(rs.getString("payment_status"));
            record.setPaymentMethod(rs.getString("payment_method"));
            record.setReceiptNo(rs.getString("receipt_no"));

            try {
                int operatorId = rs.getInt("operator_id");
                if (!rs.wasNull()) {
                    record.setOperatorId(operatorId);
                }
            } catch (SQLException e) {
                logger.debug("operator_id 字段不存在或为 NULL");
            }

            record.setRemark(rs.getString("remark"));

            // 扩展信息
            try {
                record.setOwnerName(rs.getString("owner_name"));
                record.setOwnerPhone(rs.getString("owner_phone"));
                record.setItemName(rs.getString("item_name"));
                record.setBuildingNo(rs.getString("building_no"));
                record.setUnitNo(rs.getString("unit_no"));
                record.setFloor(rs.getString("floor"));
                record.setRoomNo(rs.getString("room_no"));

                try {
                    record.setOverdueDays(rs.getInt("overdue_days"));
                } catch (SQLException e) {
                    // ignore
                }

                logger.debug("✅ 扩展信息加载成功");
            } catch (SQLException e) {
                logger.debug("扩展信息字段不存在（可能是从表查询）");
            }

            logger.debug("========== PaymentRecord 映射完成 ==========");

        } catch (SQLException e) {
            logger.error("❌ 映射 PaymentRecord 失败", e);
            throw e;
        }

        return record;
    }

    // ==================== 业务辅助方法 ====================

    /**
     * ✅ 修复：使用 update_time
     */
    public int calculateAndUpdateLateFee(BigDecimal lateFeeRate, int gracePeriod) {
        String sql = "UPDATE " + TABLE_NAME + " SET " +
                "late_fee = amount * ? * DATEDIFF(DAY, due_date, GETDATE()), " +
                "update_time = GETDATE() " +  // ✅ update_time
                "WHERE payment_status IN (?, ?) " +
                "AND DATEDIFF(DAY, due_date, GETDATE()) > ?";

        return update(sql, lateFeeRate, STATUS_UNPAID, STATUS_OVERDUE, gracePeriod);
    }

    public int updateOverdueStatus() {
        String sql = "UPDATE " + TABLE_NAME + " SET " +
                "payment_status = ? " +
                "WHERE payment_status = ? " +
                "AND due_date < GETDATE() " +
                "AND payment_date IS NULL";

        return update(sql, STATUS_OVERDUE, STATUS_UNPAID);
    }

    public List<PaymentRecord> findDueSoonRecords() {
        String sql = "SELECT * FROM " + VIEW_PAYMENT_DETAILS +
                " WHERE payment_status = ? " +
                " AND due_date BETWEEN GETDATE() AND DATEADD(DAY, 7, GETDATE()) " +
                " ORDER BY due_date";

        return query(sql, this::mapPaymentRecord, STATUS_UNPAID);
    }
    /**
     * 🔥 根据业主ID和状态查询记录
     */
    public List<PaymentRecord> findByOwnerAndStatus(String ownerId, String paymentStatus) {
        String sql = "SELECT * FROM payment_records WHERE owner_id = ? AND payment_status = ?";
        return query(sql, this::mapPaymentRecord, ownerId, paymentStatus);
    }

    /**
     * 🔥 统计业主指定状态的缴费数量
     */
    public int countByOwnerAndStatus(String ownerId, String paymentStatus) {
        String sql = "SELECT COUNT(*) FROM payment_records WHERE owner_id = ? AND payment_status = ?";
        Long count = queryForLong(sql, ownerId, paymentStatus);
        return count != null ? count.intValue() : 0;
    }
    public int countByHouseId(String houseId) {
        String sql = "SELECT COUNT(*) FROM payment_records WHERE house_id = ?";
        Long count = queryForLong(sql, houseId);
        return count != null ? count.intValue() : 0;
    }
    // ==================== 🆕 按收费项目筛选的方法（方法重载） ====================

    /**
     * ✅ 支持按收费项目筛选的分页查询（方法重载）
     * @param itemId 收费项目ID（可选）
     */
    public List<PaymentRecord> findByPage(int pageNum, int pageSize, String keyword, String status, String itemId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM (");
        sql.append("  SELECT ROW_NUMBER() OVER (ORDER BY due_date DESC) AS row_num, * ");
        sql.append("  FROM ").append(VIEW_PAYMENT_DETAILS).append(" WHERE 1=1 ");

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasStatus = status != null && !status.trim().isEmpty();
        boolean hasItemId = itemId != null && !itemId.trim().isEmpty();

        if (hasKeyword) {
            sql.append("  AND (owner_id LIKE ? OR owner_name LIKE ? OR house_id LIKE ?) ");
        }
        if (hasStatus) {
            sql.append("  AND payment_status = ? ");
        }
        if (hasItemId) {
            sql.append("  AND item_id = ? ");
        }

        sql.append(") AS temp ");
        sql.append("WHERE row_num BETWEEN ? AND ?");

        int start = (pageNum - 1) * pageSize + 1;
        int end = pageNum * pageSize;

        List<Object> params = new ArrayList<>();
        if (hasKeyword) {
            String likeKeyword = "%" + keyword + "%";
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);
        }
        if (hasStatus) {
            params.add(status);
        }
        if (hasItemId) {
            params.add(itemId);
        }
        params.add(start);
        params.add(end);

        logger.debug("🔍 按项目筛选分页查询: itemId={}", itemId);
        return query(sql.toString(), this::mapPaymentRecord, params.toArray());
    }

    /**
     * ✅ 支持按收费项目筛选的总数统计（方法重载）
     */
    public long count(String keyword, String status, String itemId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ")
                .append(TABLE_NAME).append(" pr WHERE 1=1 ");

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasStatus = status != null && !status.trim().isEmpty();
        boolean hasItemId = itemId != null && !itemId.trim().isEmpty();

        List<Object> params = new ArrayList<>();

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
        if (hasItemId) {
            sql.append("AND pr.item_id = ? ");
            params.add(itemId);
        }

        logger.debug("📊 按项目筛选统计: itemId={}", itemId);
        return queryForLong(sql.toString(), params.toArray());
    }

    /**
     * ✅ 支持按收费项目筛选的统计数据（方法重载）
     */
    public Map<String, Object> getStatistics(String keyword, String status, String itemId) {
        Map<String, Object> result = new HashMap<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("  COUNT(*) AS total_count, ");
        sql.append("  ISNULL(SUM(amount), 0) AS total_amount, ");
        sql.append("  SUM(CASE WHEN payment_status = ? THEN 1 ELSE 0 END) AS paid_count, ");
        sql.append("  ISNULL(SUM(CASE WHEN payment_status = ? THEN amount ELSE 0 END), 0) AS paid_amount, ");
        sql.append("  SUM(CASE WHEN payment_status = ? THEN 1 ELSE 0 END) AS unpaid_count, ");
        sql.append("  ISNULL(SUM(CASE WHEN payment_status = ? THEN amount ELSE 0 END), 0) AS unpaid_amount, ");
        sql.append("  SUM(CASE WHEN payment_status = ? THEN 1 ELSE 0 END) AS overdue_count, ");
        sql.append("  ISNULL(SUM(CASE WHEN payment_status = ? THEN amount ELSE 0 END), 0) AS overdue_amount ");
        sql.append("FROM ").append(VIEW_PAYMENT_DETAILS).append(" WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        params.add(STATUS_PAID);
        params.add(STATUS_PAID);
        params.add(STATUS_UNPAID);
        params.add(STATUS_UNPAID);
        params.add(STATUS_OVERDUE);
        params.add(STATUS_OVERDUE);

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (owner_id LIKE ? OR owner_name LIKE ? OR house_id LIKE ?) ");
            String likeKeyword = "%" + keyword.trim() + "%";
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);
        }

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND payment_status = ? ");
            params.add(status);
        }

        if (itemId != null && !itemId.trim().isEmpty()) {
            sql.append("AND item_id = ? ");
            params.add(itemId);
        }

        logger.debug("📊 按项目筛选统计查询: itemId={}", itemId);

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result.put("totalCount", rs.getInt("total_count"));
                result.put("totalAmount", rs.getBigDecimal("total_amount"));
                result.put("paidCount", rs.getInt("paid_count"));
                result.put("paidAmount", rs.getBigDecimal("paid_amount"));
                result.put("unpaidCount", rs.getInt("unpaid_count"));
                result.put("unpaidAmount", rs.getBigDecimal("unpaid_amount"));
                result.put("overdueCount", rs.getInt("overdue_count"));
                result.put("overdueAmount", rs.getBigDecimal("overdue_amount"));
            }

        } catch (SQLException e) {
            logger.error("❌ 获取按项目筛选的统计数据失败", e);
        }

        return result;
    }
    /**
     * 🔥 执行查询（返回 List）
     *
     * @param sql SQL 语句
     * @param params 参数数组
     * @return List<PaymentRecord>
     */
    public List<PaymentRecord> executeQuery(String sql, Object[] params) throws SQLException {
        List<PaymentRecord> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);

            // 设置参数
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                PaymentRecord record = new PaymentRecord();

                // 🔥 修复1: recordId 从 int 转换为 String
                record.setRecordId(String.valueOf(rs.getInt("record_id")));

                record.setOwnerId(rs.getString("owner_id"));
                record.setOwnerName(rs.getString("owner_name"));
                record.setHouseId(rs.getString("house_id"));
                record.setItemId(rs.getString("item_id"));
                record.setItemName(rs.getString("item_name"));
                record.setBillingPeriod(rs.getString("billing_period"));

                // 🔥 修复2: amount 从 double 转换为 BigDecimal
                record.setAmount(rs.getBigDecimal("amount"));

                // 🔥 修复3: lateFee 从 double 转换为 BigDecimal
                BigDecimal lateFee = rs.getBigDecimal("late_fee");
                record.setLateFee(lateFee != null ? lateFee : BigDecimal.ZERO);

                // 🔥 修复4: totalAmount 从 double 转换为 BigDecimal
                BigDecimal totalAmount = rs.getBigDecimal("total_amount");
                record.setTotalAmount(totalAmount != null ? totalAmount : BigDecimal.ZERO);

                record.setDueDate(rs.getDate("due_date"));
                record.setPaymentStatus(rs.getString("payment_status"));
                record.setPaymentDate(rs.getTimestamp("payment_date"));
                record.setPaymentMethod(rs.getString("payment_method"));
                record.setReceiptNo(rs.getString("receipt_no"));
                record.setRemark(rs.getString("remark"));

                // 🔥 修复5: overdueDays 可能为 null，需要处理
                int overdueDays = rs.getInt("overdue_days");
                record.setOverdueDays(rs.wasNull() ? 0 : overdueDays);

                list.add(record);
            }

        } catch (SQLException e) {
            logger.error("执行查询失败，sql={}", sql, e);
            throw e;
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return list;
    }
    /**
     * 🔥 执行统计查询（返回 count）
     *
     * @param sql SQL 语句
     * @param params 参数数组
     * @return 记录数
     */
    public int executeCount(String sql, Object[] params) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);

            // 设置参数
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;

        } catch (SQLException e) {
            logger.error("执行统计查询失败，sql={}", sql, e);
            throw e;
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }
    // ==================== 🆕 批量操作支持方法（新增，不影响现有功能） ====================

    /**
     * ✅ 根据ID列表批量查询记录
     * 用于导出选中记录
     */
    public List<PaymentRecord> findByIds(List<String> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ").append(VIEW_PAYMENT_DETAILS);
        sql.append(" WHERE record_id IN (");

        for (int i = 0; i < recordIds.size(); i++) {
            sql.append(i == 0 ? "?" : ",?");
        }
        sql.append(") ORDER BY due_date DESC");

        return query(sql.toString(), this::mapPaymentRecord, recordIds.toArray());
    }

    /**
     * ✅ 批量检查记录状态
     * 返回已缴费的记录ID列表（用于批量删除前的验证）
     */
    public List<String> findPaidRecordIds(List<String> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT record_id FROM ").append(TABLE_NAME);
        sql.append(" WHERE payment_status = ? AND record_id IN (");

        List<Object> params = new ArrayList<>();
        params.add(STATUS_PAID);

        for (int i = 0; i < recordIds.size(); i++) {
            sql.append(i == 0 ? "?" : ",?");
            params.add(recordIds.get(i));
        }
        sql.append(")");

        List<String> paidIds = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                paidIds.add(rs.getString("record_id"));
            }

        } catch (SQLException e) {
            logger.error("❌ 查询已缴费记录失败", e);
        }

        return paidIds;
    }

    /**
     * ✅ 批量删除未缴费记录
     * 只删除未缴费和逾期状态的记录
     */
    public int batchDeleteUnpaid(List<String> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            return 0;
        }

        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(TABLE_NAME);
        sql.append(" WHERE payment_status IN (?, ?) AND record_id IN (");

        List<Object> params = new ArrayList<>();
        params.add(STATUS_UNPAID);
        params.add(STATUS_OVERDUE);

        for (int i = 0; i < recordIds.size(); i++) {
            sql.append(i == 0 ? "?" : ",?");
            params.add(recordIds.get(i));
        }
        sql.append(")");

        return update(sql.toString(), params.toArray());
    }
    /**
     * 💰 查询今日缴费统计
     * 🔥 修复：SQL Server 使用 CAST 而不是 DATE()
     */
    public Map<String, Object> getTodayPaymentStatistics() {
        String sql = "SELECT " +
                "COUNT(*) as count, " +
                "COALESCE(SUM(amount + COALESCE(late_fee, 0)), 0) as amount " +
                "FROM payment_records " +
                "WHERE payment_status = 'paid' " +
                "AND CAST(payment_date AS DATE) = CAST(GETDATE() AS DATE)";  // 🔥 修复

        List<Map<String, Object>> result = query(sql, rs -> {
            Map<String, Object> map = new HashMap<>();
            map.put("count", rs.getInt("count"));
            map.put("amount", rs.getBigDecimal("amount"));
            return map;
        });

        if (result.isEmpty()) {
            Map<String, Object> map = new HashMap<>();
            map.put("count", 0);
            map.put("amount", BigDecimal.ZERO);
            return map;
        }

        return result.get(0);
    }

    /**
     * 📋 查询最近的缴费记录（包含已缴费和逾期）
     * 🔥 修复：SQL Server 使用 TOP 而不是 LIMIT
     */
    public List<PaymentRecord> getRecentPayments(int limit) {
        String sql = "SELECT TOP (?) * FROM view_owner_payment_details " +  // 🔥 修复
                "WHERE payment_status IN ('paid', 'overdue') " +
                "ORDER BY " +
                "CASE " +
                "  WHEN payment_status = 'paid' THEN payment_date " +
                "  ELSE due_date " +
                "END DESC";

        return query(sql, this::mapPaymentRecord, limit);
    }

    /**
     * 📈 查询月度收入
     */
    public BigDecimal getMonthlyIncome(Date startDate, Date endDate) {
        String sql = "SELECT COALESCE(SUM(amount + COALESCE(late_fee, 0)), 0) " +
                "FROM payment_records " +
                "WHERE payment_status = 'paid' " +
                "AND payment_date >= ? AND payment_date <= ?";

        List<BigDecimal> result = query(sql, rs -> rs.getBigDecimal(1), startDate, endDate);

        return result.isEmpty() ? BigDecimal.ZERO : result.get(0);
    }
    /**
     * 🔍 根据ID查询缴费详情（业主端专用，带权限验证）
     *
     * @param recordId 记录ID
     * @param ownerId 业主ID（用于权限验证）
     * @return 缴费记录详情，如果不存在或无权限则返回null
     */
    public PaymentRecord getDetailByIdForOwner(String recordId, String ownerId) {
        logger.info("========================================");
        logger.info("【查询缴费详情】业主端");
        logger.info("记录ID: {}, 业主ID: {}", recordId, ownerId);
        logger.info("========================================");

        String sql = "SELECT * FROM view_owner_payment_details " +
                "WHERE record_id = ? AND owner_id = ?";

        List<PaymentRecord> result = query(sql, this::mapPaymentRecord, recordId, ownerId);

        if (result.isEmpty()) {
            logger.warn("⚠️ 未找到记录或无权限访问: recordId={}, ownerId={}", recordId, ownerId);
            return null;
        }

        PaymentRecord record = result.get(0);

        logger.info("✅ 查询成功");
        logger.info("  业主: {}", record.getOwnerName());
        logger.info("  费用: {}", record.getItemName());
        logger.info("  金额: ¥{}", record.getAmount());
        logger.info("  状态: {}", record.getPaymentStatus());
        logger.info("========================================");

        return record;
    }

    /**
     * 🔍 根据ID查询缴费详情（管理端，无权限限制）
     *
     * @param recordId 记录ID
     * @return 缴费记录详情，如果不存在则返回null
     */
    public PaymentRecord getDetailById(String recordId) {
        logger.info("========================================");
        logger.info("【查询缴费详情】管理端");
        logger.info("记录ID: {}", recordId);
        logger.info("========================================");

        String sql = "SELECT * FROM view_owner_payment_details WHERE record_id = ?";

        List<PaymentRecord> result = query(sql, this::mapPaymentRecord, recordId);

        if (result.isEmpty()) {
            logger.warn("⚠️ 未找到记录: recordId={}", recordId);
            return null;
        }

        PaymentRecord record = result.get(0);

        logger.info("✅ 查询成功");
        logger.info("  业主: {}", record.getOwnerName());
        logger.info("  费用: {}", record.getItemName());
        logger.info("  金额: ¥{}", record.getAmount());
        logger.info("  状态: {}", record.getPaymentStatus());
        logger.info("========================================");

        return record;
    }

}
