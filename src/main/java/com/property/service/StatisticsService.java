package com.property.service;

import com.property.dao.PaymentRecordDao;
import com.property.dao.HouseDao;
import com.property.dao.OwnerDao;
import com.property.dao.RepairRecordDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

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
            // 房屋统计
            long totalHouses = houseDao.count(null, null);
            Map<String, Long> houseStatus = houseDao.countByStatus();
            stats.put("totalHouses", totalHouses);
            stats.put("occupiedHouses", houseStatus.getOrDefault("occupied", 0L));
            stats.put("vacantHouses", houseStatus.getOrDefault("vacant", 0L));

            // 业主统计
            long totalOwners = ownerDao.count(null);
            stats.put("totalOwners", totalOwners);

            // 缴费统计（当月）
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            Date startDate = cal.getTime();

            cal.add(Calendar.MONTH, 1);
            cal.add(Calendar.DAY_OF_MONTH, -1);
            Date endDate = cal.getTime();

            Map<String, Object> paymentStats = paymentRecordDao.statisticsByPeriod(startDate, endDate);
            stats.put("monthlyPaymentStats", paymentStats);

            // 计算收缴率
            Long totalCount = (Long) paymentStats.get("totalCount");
            Long paidCount = (Long) paymentStats.get("paidCount");
            if (totalCount != null && totalCount > 0) {
                BigDecimal rate = new BigDecimal(paidCount).divide(new BigDecimal(totalCount), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
                stats.put("paymentRate", rate.setScale(2, RoundingMode.HALF_UP));
            } else {
                stats.put("paymentRate", BigDecimal.ZERO);
            }

            // 报修统计
            Map<String, Long> repairStatus = repairRecordDao.countByStatus();
            stats.put("pendingRepairs", repairStatus.getOrDefault("pending", 0L));
            stats.put("processingRepairs", repairStatus.getOrDefault("processing", 0L));
            stats.put("completedRepairs", repairStatus.getOrDefault("completed", 0L));

            logger.info("获取仪表盘统计数据成功");
        } catch (Exception e) {
            logger.error("获取仪表盘统计数据失败", e);
            throw new RuntimeException("获取统计数据失败", e);
        }

        return stats;
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
    public List<com.property.entity.Owner> getArrearsOwners() {
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
     * 获取收费趋势数据（按月统计最近12个月）
     */
    public List<Map<String, Object>> getPaymentTrend() {
        List<Map<String, Object>> trendData = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -11); // 往前推11个月

        for (int i = 0; i < 12; i++) {
            String month = String.format("%04d-%02d",
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1);

            List<Map<String, Object>> monthStats = paymentRecordDao.getPaymentStatistics(month, month);

            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", month);

            if (!monthStats.isEmpty()) {
                BigDecimal totalAmount = BigDecimal.ZERO;
                BigDecimal paidAmount = BigDecimal.ZERO;

                for (Map<String, Object> stat : monthStats) {
                    totalAmount = totalAmount.add((BigDecimal) stat.get("totalAmount"));
                    paidAmount = paidAmount.add((BigDecimal) stat.get("paidAmount"));
                }

                monthData.put("totalAmount", totalAmount);
                monthData.put("paidAmount", paidAmount);
            } else {
                monthData.put("totalAmount", BigDecimal.ZERO);
                monthData.put("paidAmount", BigDecimal.ZERO);
            }

            trendData.add(monthData);
            cal.add(Calendar.MONTH, 1);
        }

        return trendData;
    }
}
