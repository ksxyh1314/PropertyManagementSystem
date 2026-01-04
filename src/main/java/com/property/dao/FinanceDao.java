package com.property.dao;

import com.property.entity.FinanceStatistics;
import com.property.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 财务管理数据访问层
 *
 * @author PropertyManagementSystem
 * @version 1.0
 */
public class FinanceDao extends BaseDao {
    private static final Logger logger = LoggerFactory.getLogger(FinanceDao.class);

    /**
     * 查询欠费业主列表(分页)
     * 🔧 修复:添加关键词搜索功能
     */
    public Map<String, Object> getArrearsOwners(int pageNum, int pageSize,
                                                BigDecimal minAmount, String keyword)
            throws SQLException {

        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement countStmt = null;
        ResultSet rs = null;
        ResultSet countRs = null;

        try {
            conn = DBUtil.getConnection();

            // 🆕 构建查询条件
            StringBuilder whereClause = new StringBuilder("WHERE total_arrears >= ?");
            List<Object> params = new ArrayList<>();
            params.add(minAmount);

            // 🆕 添加关键词搜索条件
            if (keyword != null && !keyword.trim().isEmpty()) {
                whereClause.append(" AND (owner_name LIKE ? OR phone LIKE ?)");
                String likeKeyword = "%" + keyword.trim() + "%";
                params.add(likeKeyword);
                params.add(likeKeyword);
            }

            // 1️⃣ 查询总记录数
            String countSql = "SELECT COUNT(*) AS total FROM view_owner_arrears_summary " +
                    whereClause.toString();

            countStmt = conn.prepareStatement(countSql);
            for (int i = 0; i < params.size(); i++) {
                countStmt.setObject(i + 1, params.get(i));
            }
            countRs = countStmt.executeQuery();

            int totalCount = 0;
            if (countRs.next()) {
                totalCount = countRs.getInt("total");
            }

            // 2️⃣ 分页查询数据
            String sql = "SELECT * FROM view_owner_arrears_summary " +
                    whereClause.toString() + " " +
                    "ORDER BY total_arrears DESC " +
                    "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

            pstmt = conn.prepareStatement(sql);

            // 设置查询参数
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            pstmt.setInt(params.size() + 1, (pageNum - 1) * pageSize);  // OFFSET
            pstmt.setInt(params.size() + 2, pageSize);                   // FETCH NEXT

            rs = pstmt.executeQuery();

            List<FinanceStatistics> list = new ArrayList<>();

            while (rs.next()) {
                FinanceStatistics stats = new FinanceStatistics();
                stats.setOwnerId(rs.getString("owner_id"));
                stats.setOwnerName(rs.getString("owner_name"));
                stats.setOwnerPhone(rs.getString("phone"));
                stats.setHouseId(rs.getString("house_id"));
                stats.setBuildingNo(rs.getString("building_no"));
                stats.setUnitNo(rs.getString("unit_no"));
                stats.setFloor(rs.getString("floor"));
                stats.setUnpaidCount(rs.getInt("unpaid_count"));

                // ✅ 修复:处理可能为 NULL 的字段
                BigDecimal unpaidAmount = rs.getBigDecimal("unpaid_amount");
                stats.setUnpaidAmount(unpaidAmount != null ? unpaidAmount : BigDecimal.ZERO);

                BigDecimal totalLateFee = rs.getBigDecimal("total_late_fee");
                stats.setTotalLateFee(totalLateFee != null ? totalLateFee : BigDecimal.ZERO);

                BigDecimal totalArrears = rs.getBigDecimal("total_arrears");
                stats.setTotalArrears(totalArrears != null ? totalArrears : BigDecimal.ZERO);

                stats.setEarliestDueDate(rs.getDate("earliest_due_date"));
                stats.setMaxOverdueDays(rs.getInt("max_overdue_days"));

                list.add(stats);
            }

            // 3️⃣ 计算总页数
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);

            // 4️⃣ 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("list", list);
            result.put("total", totalCount);
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);
            result.put("pages", totalPages);

            logger.info("✅ 查询欠费业主成功: total={}, pages={}, keyword={}",
                    totalCount, totalPages, keyword);

            return result;

        } finally {
            DBUtil.close(countRs, countStmt, null);
            DBUtil.close(rs, pstmt, conn);
        }
    }

    /**
     * 查询所有欠费业主(不分页,用于导出)
     * 🔧 修复:添加关键词搜索功能
     */
    public List<FinanceStatistics> getAllArrearsOwners(BigDecimal minAmount, String keyword)
            throws SQLException {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            // 🆕 构建查询条件
            StringBuilder whereClause = new StringBuilder("WHERE total_arrears >= ?");
            List<Object> params = new ArrayList<>();
            params.add(minAmount);

            // 🆕 添加关键词搜索条件
            if (keyword != null && !keyword.trim().isEmpty()) {
                whereClause.append(" AND (owner_name LIKE ? OR phone LIKE ?)");
                String likeKeyword = "%" + keyword.trim() + "%";
                params.add(likeKeyword);
                params.add(likeKeyword);
            }

            // 使用视图查询
            String sql = "SELECT * FROM view_owner_arrears_summary " +
                    whereClause.toString() + " " +
                    "ORDER BY total_arrears DESC";

            pstmt = conn.prepareStatement(sql);

            // 设置查询参数
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();

            List<FinanceStatistics> list = new ArrayList<>();

            while (rs.next()) {
                FinanceStatistics stats = new FinanceStatistics();
                stats.setOwnerId(rs.getString("owner_id"));
                stats.setOwnerName(rs.getString("owner_name"));
                stats.setOwnerPhone(rs.getString("phone"));
                stats.setHouseId(rs.getString("house_id"));
                stats.setBuildingNo(rs.getString("building_no"));
                stats.setUnitNo(rs.getString("unit_no"));
                stats.setFloor(rs.getString("floor"));
                stats.setUnpaidCount(rs.getInt("unpaid_count"));

                // ✅ 修复:处理可能为 NULL 的字段
                BigDecimal unpaidAmount = rs.getBigDecimal("unpaid_amount");
                stats.setUnpaidAmount(unpaidAmount != null ? unpaidAmount : BigDecimal.ZERO);

                BigDecimal totalLateFee = rs.getBigDecimal("total_late_fee");
                stats.setTotalLateFee(totalLateFee != null ? totalLateFee : BigDecimal.ZERO);

                BigDecimal totalArrears = rs.getBigDecimal("total_arrears");
                stats.setTotalArrears(totalArrears != null ? totalArrears : BigDecimal.ZERO);

                stats.setEarliestDueDate(rs.getDate("earliest_due_date"));
                stats.setMaxOverdueDays(rs.getInt("max_overdue_days"));

                list.add(stats);
            }

            logger.info("✅ 查询所有欠费业主成功: count={}, keyword={}", list.size(), keyword);

            return list;

        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }

    /**
     * 查询逾期统计
     * 调用存储过程:sp_get_overdue_statistics
     */
    public Map<String, Object> getOverdueStatistics() throws SQLException {
        Connection conn = null;
        CallableStatement cstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            // 调用存储过程
            String sql = "{CALL sp_get_overdue_statistics()}";
            cstmt = conn.prepareCall(sql);

            // 执行存储过程
            boolean hasResultSet = cstmt.execute();

            Map<String, Object> result = new HashMap<>();

            if (hasResultSet) {
                rs = cstmt.getResultSet();
                if (rs.next()) {
                    result.put("overdueCount", rs.getLong("overdue_count"));
                    result.put("overdueAmount", rs.getBigDecimal("overdue_amount"));
                    result.put("totalLateFee", rs.getBigDecimal("total_late_fee"));
                    result.put("avgOverdueDays", rs.getDouble("avg_overdue_days"));
                }
            }

            logger.info("✅ 查询逾期统计成功: {}", result);

            return result;

        } finally {
            DBUtil.close(rs, cstmt, conn);
        }
    }

    /**
     * 生成催缴通知
     */
    public boolean generatePaymentReminder(String ownerId, int publisherId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            // 1. 查询业主欠费信息
            String querySql = "SELECT o.owner_name, COUNT(*) AS unpaid_count, " +
                    "SUM(pr.total_amount) AS total_arrears " +
                    "FROM payment_records pr " +
                    "INNER JOIN owners o ON pr.owner_id = o.owner_id " +
                    "WHERE pr.owner_id = ? " +
                    "AND pr.payment_status IN ('unpaid', 'overdue') " +
                    "GROUP BY o.owner_name";

            pstmt = conn.prepareStatement(querySql);
            pstmt.setString(1, ownerId);

            rs = pstmt.executeQuery();

            if (!rs.next()) {
                logger.warn("⚠️ 业主 {} 没有欠费记录", ownerId);
                return false;
            }

            String ownerName = rs.getString("owner_name");
            int unpaidCount = rs.getInt("unpaid_count");
            BigDecimal totalArrears = rs.getBigDecimal("total_arrears");

            DBUtil.close(rs, pstmt, null);

            // 2. 生成催缴通知内容
            String title = "缴费提醒:" + ownerName;
            String content = String.format(
                    "尊敬的%s业主:\n\n" +
                            "您有%d笔未缴费账单,欠费总额为%.2f元,请尽快缴纳。\n\n" +
                            "逾期将产生滞纳金,请您及时处理。\n\n" +
                            "如有疑问,请联系物业服务中心。\n\n" +
                            "物业服务中心\n" +
                            "%tF",
                    ownerName, unpaidCount, totalArrears, new java.util.Date()
            );

            // 3. 插入公告表
            String insertSql = "INSERT INTO announcements " +
                    "(title, content, announcement_type, priority, publisher_id, " +
                    "publish_time, status) " +
                    "VALUES (?, ?, 'payment_reminder', 'important', ?, GETDATE(), 1)";

            pstmt = conn.prepareStatement(insertSql);
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setInt(3, publisherId);

            int rows = pstmt.executeUpdate();

            logger.info("✅ 生成催缴通知成功: ownerId={}, ownerName={}", ownerId, ownerName);

            return rows > 0;

        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }

    /**
     * 查询楼栋收缴率统计
     * 调用存储过程:sp_building_payment_statistics
     */
    public List<Map<String, Object>> getBuildingPaymentStatistics() throws SQLException {
        Connection conn = null;
        CallableStatement cstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            // 调用存储过程
            String sql = "{CALL sp_building_payment_statistics()}";
            cstmt = conn.prepareCall(sql);

            // 执行存储过程
            boolean hasResultSet = cstmt.execute();

            List<Map<String, Object>> list = new ArrayList<>();

            if (hasResultSet) {
                rs = cstmt.getResultSet();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("buildingNo", rs.getString("building_no"));
                    row.put("buildingName", rs.getString("building_name"));
                    row.put("houseCount", rs.getInt("house_count"));
                    row.put("occupiedCount", rs.getInt("occupied_count"));
                    row.put("totalBills", rs.getInt("total_bills"));
                    row.put("paidBills", rs.getInt("paid_bills"));
                    row.put("unpaidBills", rs.getInt("unpaid_bills"));
                    row.put("overdueBills", rs.getInt("overdue_bills"));
                    row.put("totalAmount", rs.getBigDecimal("total_amount"));
                    row.put("collectedAmount", rs.getBigDecimal("collected_amount"));
                    row.put("uncollectedAmount", rs.getBigDecimal("uncollected_amount"));
                    row.put("collectionRate", rs.getBigDecimal("collection_rate"));

                    list.add(row);
                }
            }

            logger.info("✅ 查询楼栋统计成功: count={}", list.size());

            return list;

        } finally {
            DBUtil.close(rs, cstmt, conn);
        }
    }

    /**
     * 查询月度收缴率统计
     * 调用存储过程:sp_monthly_payment_statistics
     */
    public List<Map<String, Object>> getMonthlyPaymentStatistics(int year, int month)
            throws SQLException {

        Connection conn = null;
        CallableStatement cstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            // 调用存储过程
            String sql = "{CALL sp_monthly_payment_statistics(?, ?)}";
            cstmt = conn.prepareCall(sql);

            cstmt.setInt(1, year);
            cstmt.setInt(2, month);

            // 执行存储过程
            boolean hasResultSet = cstmt.execute();

            List<Map<String, Object>> list = new ArrayList<>();

            if (hasResultSet) {
                rs = cstmt.getResultSet();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("itemId", rs.getString("item_id"));
                    row.put("itemName", rs.getString("item_name"));
                    row.put("chargeCycle", rs.getString("charge_cycle"));
                    row.put("totalBills", rs.getInt("total_bills"));
                    row.put("paidBills", rs.getInt("paid_bills"));
                    row.put("unpaidBills", rs.getInt("unpaid_bills"));
                    row.put("overdueBills", rs.getInt("overdue_bills"));
                    row.put("totalAmount", rs.getBigDecimal("total_amount"));
                    row.put("collectedAmount", rs.getBigDecimal("collected_amount"));
                    row.put("collectedLateFee", rs.getBigDecimal("collected_late_fee"));
                    row.put("collectionRate", rs.getBigDecimal("collection_rate"));

                    list.add(row);
                }
            }

            logger.info("✅ 查询月度统计成功: year={}, month={}, count={}",
                    year, month, list.size());

            return list;

        } finally {
            DBUtil.close(rs, cstmt, conn);
        }
    }

    /**
     * 🆕 查询时间段收缴率统计
     * 🔥 核心逻辑：
     * 1. 按 due_date 确定账单归属时间段（不受宽限期影响）
     * 2. 宽限期只影响逾期判断，不影响统计归属
     *
     * @param cycle 统计周期：monthly(月度)、quarterly(季度)、yearly(年度)
     * @param year 年份
     * @param month 月份（月度统计时必填）
     * @param quarter 季度（季度统计时必填）
     * @return 统计结果列表
     */
    /**
     * 查询时间段统计
     */
    public List<Map<String, Object>> getPeriodPaymentStatistics(String cycle, Integer year, Integer month, Integer quarter) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("    ci.item_id, ");
        sql.append("    ci.item_name, ");
        sql.append("    ci.charge_cycle, ");
        sql.append("    COUNT(*) AS total_bills, ");
        sql.append("    SUM(CASE WHEN pr.payment_status = 'paid' THEN 1 ELSE 0 END) AS paid_bills, ");
        sql.append("    SUM(CASE WHEN pr.payment_status = 'unpaid' THEN 1 ELSE 0 END) AS unpaid_bills, ");
        sql.append("    SUM(CASE ");
        sql.append("        WHEN pr.payment_status = 'overdue' THEN 1 ");
        sql.append("        WHEN pr.payment_status = 'unpaid' AND DATEADD(DAY, ISNULL(ci.grace_period, 30), pr.due_date) < GETDATE() THEN 1 ");
        sql.append("        ELSE 0 ");
        sql.append("    END) AS overdue_bills, ");
        sql.append("    ISNULL(SUM(pr.amount), 0) AS total_amount, ");
        sql.append("    ISNULL(SUM(CASE WHEN pr.payment_status = 'paid' THEN pr.amount ELSE 0 END), 0) AS collected_amount, ");
        sql.append("    ISNULL(SUM(CASE WHEN pr.payment_status = 'paid' THEN ISNULL(pr.late_fee, 0) ELSE 0 END), 0) AS collected_late_fee, ");
        sql.append("    CASE ");
        sql.append("        WHEN SUM(pr.amount) = 0 OR SUM(pr.amount) IS NULL THEN 0 ");
        sql.append("        ELSE CAST(SUM(CASE WHEN pr.payment_status = 'paid' THEN pr.amount ELSE 0 END) * 100.0 / SUM(pr.amount) AS DECIMAL(10,2)) ");
        sql.append("    END AS collection_rate ");
        sql.append("FROM payment_records pr ");
        sql.append("INNER JOIN charge_items ci ON pr.item_id = ci.item_id ");
        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        // 根据周期类型添加条件
        if ("monthly".equals(cycle) && year != null && month != null) {
            sql.append("AND YEAR(pr.due_date) = ? ");
            sql.append("AND MONTH(pr.due_date) = ? ");
            params.add(year);
            params.add(month);
            logger.info("📅 查询范围: {}年{}月", year, month);
        } else if ("quarterly".equals(cycle) && year != null && quarter != null) {
            sql.append("AND YEAR(pr.due_date) = ? ");
            sql.append("AND DATEPART(QUARTER, pr.due_date) = ? ");
            params.add(year);
            params.add(quarter);
            logger.info("📅 查询范围: {}年第{}季度", year, quarter);
        } else if ("yearly".equals(cycle) && year != null) {
            sql.append("AND YEAR(pr.due_date) = ? ");
            params.add(year);
            logger.info("📅 查询范围: {}年", year);
        }

        sql.append("GROUP BY ci.item_id, ci.item_name, ci.charge_cycle ");
        sql.append("ORDER BY ci.item_id");

        logger.info("SQL: {}", sql.toString());
        logger.info("参数: cycle={}, year={}, month={}, quarter={}", cycle, year, month, quarter);
        logger.info("========================================");

        try {
            // ✅ 使用 RowMapper 将 ResultSet 转换为 Map
            return query(sql.toString(), rs -> {
                Map<String, Object> row = new HashMap<>();
                row.put("itemId", rs.getString("item_id"));
                row.put("itemName", rs.getString("item_name"));
                row.put("chargeCycle", rs.getString("charge_cycle"));
                row.put("totalBills", rs.getInt("total_bills"));
                row.put("paidBills", rs.getInt("paid_bills"));
                row.put("unpaidBills", rs.getInt("unpaid_bills"));
                row.put("overdueBills", rs.getInt("overdue_bills"));
                row.put("totalAmount", rs.getBigDecimal("total_amount"));
                row.put("collectedAmount", rs.getBigDecimal("collected_amount"));
                row.put("collectedLateFee", rs.getBigDecimal("collected_late_fee"));
                row.put("collectionRate", rs.getBigDecimal("collection_rate"));
                return row;
            }, params.toArray());

        } catch (Exception e) {
            logger.error("❌ 查询时间段统计失败", e);
            throw new RuntimeException("查询时间段统计失败: " + e.getMessage(), e);
        }
    }

    /**
     * 🆕 获取可用的统计年份列表（用于前端下拉框）
     */
    public List<Integer> getAvailableYears() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Integer> years = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();

            // 🔥 从 due_date 中提取年份
            String sql = "SELECT DISTINCT YEAR(due_date) AS year " +
                    "FROM payment_records " +
                    "WHERE due_date IS NOT NULL " +
                    "ORDER BY year DESC";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                years.add(rs.getInt("year"));
            }

            // 如果没有数据，至少返回当前年份
            if (years.isEmpty()) {
                years.add(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR));
            }

            logger.info("✅ 获取可用年份: {}", years);
            return years;

        } catch (SQLException e) {
            logger.error("❌ 获取可用年份失败", e);
            // 返回默认年份列表
            int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
            for (int i = 0; i < 5; i++) {
                years.add(currentYear - i);
            }
            return years;
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }
}
