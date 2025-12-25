package com.property.service;

import com.property.dao.PaymentRecordDao;
import com.property.dao.HouseDao;
import com.property.dao.OwnerDao;
import com.property.dao.RepairRecordDao;
import com.property.entity.Owner;
import com.property.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * 统计服务类
 */
public class StatisticsService {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsService.class);
    private PaymentRecordDao paymentRecordDao = new PaymentRecordDao();
    private HouseDao houseDao = new HouseDao();
    private OwnerDao ownerDao = new OwnerDao();
    private RepairRecordDao repairRecordDao = new RepairRecordDao();

    /**
     * 获取首页统计数据（仪表盘）
     */
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();

        try {
            // 1. 房屋统计
            long totalHouses = houseDao.count(null, null);
            Map<String, Long> houseStatus = houseDao.countByStatus();
            stats.put("totalHouses", totalHouses);
            stats.put("occupiedHouses", houseStatus.getOrDefault("occupied", 0L));
            stats.put("vacantHouses", houseStatus.getOrDefault("vacant", 0L));

            // 2. 业主统计
            long totalOwners = ownerDao.count(null);
            stats.put("totalOwners", totalOwners);

            // 3. 缴费统计（当月）
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            Date startDate = cal.getTime();

            cal.add(Calendar.MONTH, 1);
            cal.add(Calendar.DAY_OF_MONTH, -1);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            Date endDate = cal.getTime();

            Map<String, Object> paymentStats = paymentRecordDao.statisticsByPeriod(startDate, endDate);

            // 提取当月收入（已缴费金额）
            BigDecimal monthlyIncome = (BigDecimal) paymentStats.getOrDefault("paidAmount", BigDecimal.ZERO);
            stats.put("monthlyIncome", monthlyIncome);
            stats.put("monthlyPaymentStats", paymentStats);

            // 4. 计算收缴率
            Long totalCount = (Long) paymentStats.getOrDefault("totalCount", 0L);
            Long paidCount = (Long) paymentStats.getOrDefault("paidCount", 0L);
            if (totalCount != null && totalCount > 0) {
                BigDecimal rate = new BigDecimal(paidCount)
                        .divide(new BigDecimal(totalCount), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
                stats.put("paymentRate", rate.setScale(2, RoundingMode.HALF_UP));
            } else {
                stats.put("paymentRate", BigDecimal.ZERO);
            }

            // 5. 未缴费账单统计
            long unpaidBills = getUnpaidBillsCount();
            stats.put("unpaidBills", unpaidBills);

            // 6. 缴费状态分布
            Map<String, Long> paymentStatusCount = getPaymentStatusCount();
            stats.put("paidCount", paymentStatusCount.getOrDefault("paid", 0L));
            stats.put("unpaidCount", paymentStatusCount.getOrDefault("unpaid", 0L));
            stats.put("overdueCount", paymentStatusCount.getOrDefault("overdue", 0L));

            // 7. 报修统计
            Map<String, Long> repairStatus = repairRecordDao.countByStatus();
            stats.put("pendingRepairs", repairStatus.getOrDefault("pending", 0L));
            stats.put("processingRepairs", repairStatus.getOrDefault("processing", 0L));
            stats.put("completedRepairs", repairStatus.getOrDefault("completed", 0L));

            logger.info("获取仪表盘统计数据成功: " + stats);
        } catch (Exception e) {
            logger.error("获取仪表盘统计数据失败", e);
            throw new RuntimeException("获取统计数据失败", e);
        }

        return stats;
    }

    /**
     * 获取未缴费账单数量
     */
    private long getUnpaidBillsCount() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM payment_records WHERE payment_status = 'unpaid'";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (Exception e) {
            logger.error("查询未缴费账单数量失败", e);
            return 0;
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }

    /**
     * 获取缴费状态分布
     */
    private Map<String, Long> getPaymentStatusCount() {
        Map<String, Long> statusCount = new HashMap<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql =
                    "SELECT " +
                            "    payment_status, " +
                            "    COUNT(*) as count " +
                            "FROM payment_records " +
                            "GROUP BY payment_status";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String status = rs.getString("payment_status");
                long count = rs.getLong("count");
                statusCount.put(status, count);
            }
        } catch (Exception e) {
            logger.error("查询缴费状态分布失败", e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return statusCount;
    }

    /**
     * 获取物业收费统计（按月度、季度）
     */
    public List<Map<String, Object>> getPaymentStatistics(String startMonth, String endMonth) {
        if (startMonth == null || startMonth.trim().isEmpty()) {
            throw new IllegalArgumentException("开始月份不能为空");
        }
        if (endMonth == null || endMonth.trim().isEmpty()) {
            throw new IllegalArgumentException("结束月份不能为空");
        }

        return paymentRecordDao.getPaymentStatistics(startMonth, endMonth);
    }

    /**
     * 获取各楼栋缴费情况
     */
    public List<Map<String, Object>> getBuildingPaymentStatus() {
        return paymentRecordDao.getBuildingPaymentStatus();
    }

    /**
     * 获取欠费业主列表
     */
    public List<Owner> getArrearsOwners() {
        return ownerDao.findArrearsOwners();
    }

    /**
     * 生成财务报表数据
     */
    public Map<String, Object> generateFinancialReport(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("开始日期和结束日期不能为空");
        }
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }

        Map<String, Object> report = new HashMap<>();

        try {
            // 时间段内的收费统计
            Map<String, Object> periodStats = paymentRecordDao.statisticsByPeriod(startDate, endDate);
            report.put("periodStats", periodStats);

            // 各楼栋缴费情况
            List<Map<String, Object>> buildingStats = paymentRecordDao.getBuildingPaymentStatus();
            report.put("buildingStats", buildingStats);

            // 时间范围
            report.put("startDate", startDate);
            report.put("endDate", endDate);
            report.put("generateTime", new Date());

            logger.info("生成财务报表成功：{} 至 {}", startDate, endDate);
        } catch (Exception e) {
            logger.error("生成财务报表失败", e);
            throw new RuntimeException("生成财务报表失败", e);
        }

        return report;
    }

    /**
     * 获取收费趋势数据（最近6个月）
     */
    public List<Map<String, Object>> getPaymentTrend() {
        List<Map<String, Object>> trendData = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            // 使用 CTE 生成最近6个月的数据
            String sql =
                    "WITH Months AS ( " +
                            "    SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 " +
                            "    UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 " +
                            ") " +
                            "SELECT " +
                            "    FORMAT(DATEADD(MONTH, -n, GETDATE()), 'yyyy-MM') as month, " +
                            "    ISNULL(SUM(pr.amount + pr.late_fee), 0) as totalAmount, " +
                            "    ISNULL(SUM(CASE WHEN pr.payment_status = 'paid' THEN pr.amount + pr.late_fee ELSE 0 END), 0) as paidAmount " +
                            "FROM Months " +
                            "LEFT JOIN payment_records pr ON " +
                            "    FORMAT(pr.due_date, 'yyyy-MM') = FORMAT(DATEADD(MONTH, -n, GETDATE()), 'yyyy-MM') " +
                            "GROUP BY FORMAT(DATEADD(MONTH, -n, GETDATE()), 'yyyy-MM'), n " +
                            "ORDER BY n DESC";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> monthData = new HashMap<>();
                monthData.put("month", rs.getString("month"));
                monthData.put("totalAmount", rs.getBigDecimal("totalAmount"));
                monthData.put("paidAmount", rs.getBigDecimal("paidAmount"));
                trendData.add(monthData);
            }

            logger.info("获取收费趋势数据成功，共 " + trendData.size() + " 个月");
        } catch (Exception e) {
            logger.error("获取收费趋势数据失败", e);

            // 如果查询失败，返回空数据
            Calendar cal = Calendar.getInstance();
            for (int i = 5; i >= 0; i--) {
                cal.add(Calendar.MONTH, -i);
                String month = String.format("%04d-%02d",
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH) + 1);

                Map<String, Object> monthData = new HashMap<>();
                monthData.put("month", month);
                monthData.put("totalAmount", BigDecimal.ZERO);
                monthData.put("paidAmount", BigDecimal.ZERO);
                trendData.add(monthData);

                cal = Calendar.getInstance(); // 重置
            }
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return trendData;
    }

    /**
     * 获取待处理报修列表（前5条）
     */
    public List<Map<String, Object>> getPendingRepairs() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Map<String, Object>> repairs = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql =
                    "SELECT TOP 5 " +
                            "    r.repair_id, " +
                            "    o.owner_name, " +
                            "    r.house_id, " +
                            "    r.repair_type, " +
                            "    r.description, " +
                            "    r.submit_time, " +
                            "    r.priority, " +
                            "    r.status " +
                            "FROM repair_records r " +
                            "LEFT JOIN owners o ON r.owner_id = o.owner_id " +
                            "WHERE r.status = 'pending' " +
                            "ORDER BY " +
                            "    CASE r.priority " +
                            "        WHEN 'urgent' THEN 1 " +
                            "        WHEN 'high' THEN 2 " +
                            "        WHEN 'normal' THEN 3 " +
                            "        WHEN 'low' THEN 4 " +
                            "        ELSE 5 " +
                            "    END, " +
                            "    r.submit_time DESC";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> repair = new HashMap<>();
                repair.put("repairId", rs.getInt("repair_id"));
                repair.put("ownerName", rs.getString("owner_name"));
                repair.put("houseId", rs.getString("house_id"));
                repair.put("repairType", rs.getString("repair_type"));
                repair.put("description", rs.getString("description"));
                repair.put("submitTime", rs.getTimestamp("submit_time"));
                repair.put("priority", rs.getString("priority"));
                repair.put("status", rs.getString("status"));
                repairs.add(repair);
            }

            logger.info("获取待处理报修列表成功，共 " + repairs.size() + " 条");
        } catch (Exception e) {
            logger.error("获取待处理报修列表失败", e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return repairs;
    }

    /**
     * 通用查询方法（用于执行自定义SQL）
     */
    private List<Map<String, Object>> queryForList(String sql) {
        List<Map<String, Object>> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                list.add(row);
            }
        } catch (Exception e) {
            logger.error("执行查询失败: " + sql, e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return list;
    }

    /**
     * 查询单个值
     */
    private Object queryForObject(String sql) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getObject(1);
            }
        } catch (Exception e) {
            logger.error("执行查询失败: " + sql, e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return null;
    }
}
