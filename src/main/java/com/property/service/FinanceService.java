package com.property.service;

import com.property.dao.FinanceDao;
import com.property.dao.PaymentRecordDao;
import com.property.entity.FinanceStatistics;
import com.property.entity.PaymentRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 财务管理业务逻辑层
 *
 * @author PropertyManagementSystem
 * @version 1.0
 */
public class FinanceService {
    private static final Logger logger = LoggerFactory.getLogger(FinanceService.class);
    private final FinanceDao financeDao = new FinanceDao();
    private final PaymentRecordDao paymentRecordDao = new PaymentRecordDao();
    /**
     * 查询欠费业主列表(分页)
     * 🔧 修复:添加关键词搜索参数
     */
    public Map<String, Object> getArrearsOwners(int pageNum, int pageSize,
                                                BigDecimal minAmount, String keyword) {
        logger.info("========================================");
        logger.info("【Service】查询欠费业主列表");
        logger.info("pageNum={}, pageSize={}, minAmount={}, keyword={}",
                pageNum, pageSize, minAmount, keyword);
        logger.info("========================================");

        try {
            // 参数验证
            if (pageNum < 1) {
                pageNum = 1;
            }
            if (pageSize < 1 || pageSize > 100) {
                pageSize = 10;
            }
            if (minAmount == null) {
                minAmount = BigDecimal.ZERO;
            }

            // 🆕 传递关键词参数给 DAO 层
            Map<String, Object> result = financeDao.getArrearsOwners(
                    pageNum, pageSize, minAmount, keyword
            );

            logger.info("✅ 查询成功: total={}", result.get("total"));

            return result;

        } catch (SQLException e) {
            logger.error("❌ 查询欠费业主失败", e);
            throw new RuntimeException("查询欠费业主失败:" + e.getMessage(), e);
        }
    }

    /**
     * 查询所有欠费业主(不分页,用于导出)
     * 🔧 修复:添加关键词搜索参数
     */
    public List<FinanceStatistics> getAllArrearsOwners(BigDecimal minAmount, String keyword) {
        logger.info("========================================");
        logger.info("【Service】查询所有欠费业主(导出)");
        logger.info("minAmount={}, keyword={}", minAmount, keyword);
        logger.info("========================================");

        try {
            if (minAmount == null) {
                minAmount = BigDecimal.ZERO;
            }

            // 🆕 传递关键词参数给 DAO 层
            List<FinanceStatistics> list = financeDao.getAllArrearsOwners(minAmount, keyword);

            logger.info("✅ 查询成功: count={}", list.size());

            return list;

        } catch (SQLException e) {
            logger.error("❌ 查询所有欠费业主失败", e);
            throw new RuntimeException("查询失败:" + e.getMessage(), e);
        }
    }

    /**
     * 查询逾期统计
     */
    public Map<String, Object> getOverdueStatistics() {
        logger.info("========================================");
        logger.info("【Service】查询逾期统计");
        logger.info("========================================");

        try {
            Map<String, Object> result = financeDao.getOverdueStatistics();

            logger.info("✅ 查询成功: {}", result);

            return result;

        } catch (SQLException e) {
            logger.error("❌ 查询逾期统计失败", e);
            throw new RuntimeException("查询逾期统计失败:" + e.getMessage(), e);
        }
    }

    /**
     * 生成催缴通知
     */
    public boolean generatePaymentReminder(String ownerId, int publisherId) {
        logger.info("========================================");
        logger.info("【Service】生成催缴通知");
        logger.info("ownerId={}, publisherId={}", ownerId, publisherId);
        logger.info("========================================");

        try {
            // 参数验证
            if (ownerId == null || ownerId.trim().isEmpty()) {
                throw new IllegalArgumentException("业主ID不能为空");
            }

            boolean success = financeDao.generatePaymentReminder(ownerId, publisherId);

            if (success) {
                logger.info("✅ 生成催缴通知成功");
            } else {
                logger.warn("⚠️ 生成催缴通知失败");
            }

            return success;

        } catch (SQLException e) {
            logger.error("❌ 生成催缴通知失败", e);
            throw new RuntimeException("生成催缴通知失败:" + e.getMessage(), e);
        }
    }

    /**
     * 查询楼栋收缴率统计
     */
    public List<Map<String, Object>> getBuildingPaymentStatistics() {
        logger.info("========================================");
        logger.info("【Service】查询楼栋收缴率统计");
        logger.info("========================================");

        try {
            List<Map<String, Object>> list = financeDao.getBuildingPaymentStatistics();

            logger.info("✅ 查询成功: count={}", list.size());

            return list;

        } catch (SQLException e) {
            logger.error("❌ 查询楼栋统计失败", e);
            throw new RuntimeException("查询楼栋统计失败:" + e.getMessage(), e);
        }
    }

    /**
     * 查询月度收缴率统计
     */
    public List<Map<String, Object>> getMonthlyPaymentStatistics(int year, int month) {
        logger.info("========================================");
        logger.info("【Service】查询月度收缴率统计");
        logger.info("year={}, month={}", year, month);
        logger.info("========================================");

        try {
            // 参数验证
            if (year < 2000 || year > 2100) {
                throw new IllegalArgumentException("年份不合法");
            }
            if (month < 1 || month > 12) {
                throw new IllegalArgumentException("月份不合法");
            }

            List<Map<String, Object>> list = financeDao.getMonthlyPaymentStatistics(year, month);

            logger.info("✅ 查询成功: count={}", list.size());

            return list;

        } catch (SQLException e) {
            logger.error("❌ 查询月度统计失败", e);
            throw new RuntimeException("查询月度统计失败:" + e.getMessage(), e);
        }
    }

    /**
     * 🆕 查询时间段收缴率统计
     * 🔥 核心优化：
     * 1. 完善参数验证
     * 2. 优化异常处理
     * 3. 添加汇总行计算
     *
     * @param cycle 统计周期：monthly(月度)、quarterly(季度)、yearly(年度)
     * @param year 年份
     * @param month 月份（月度统计时必填）
     * @param quarter 季度（季度统计时必填）
     * @return 统计结果列表（包含汇总行）
     */
    public List<Map<String, Object>> getPeriodPaymentStatistics(
            String cycle, Integer year, Integer month, Integer quarter) {

        logger.info("========================================");
        logger.info("【Service】查询时间段统计");
        logger.info("cycle={}, year={}, month={}, quarter={}", cycle, year, month, quarter);
        logger.info("========================================");

        try {
            // 🔥 参数验证
            validatePeriodParameters(cycle, year, month, quarter);

            // 调用 DAO 层查询
            List<Map<String, Object>> list = financeDao.getPeriodPaymentStatistics(
                    cycle, year, month, quarter
            );

            // 🔥 如果有数据，添加汇总行
            if (!list.isEmpty()) {
                Map<String, Object> summary = calculateSummary(list);
                list.add(summary);
                logger.info("✅ 已添加汇总行");
            }

            logger.info("✅ 查询成功: count={} (含汇总行)", list.size());

            return list;

        } catch (IllegalArgumentException e) {
            // 参数验证异常
            logger.error("❌ 参数验证失败: {}", e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            // DAO 层业务异常
            logger.error("❌ 查询时间段统计失败", e);
            throw new RuntimeException("查询时间段统计失败: " + e.getMessage(), e);
        } catch (Exception e) {
            // 其他未知异常
            logger.error("❌ 系统异常", e);
            throw new RuntimeException("系统异常: " + e.getMessage(), e);
        }
    }

    /**
     * 🔥 参数验证（提取为独立方法，提高代码可读性）
     */
    private void validatePeriodParameters(String cycle, Integer year, Integer month, Integer quarter) {
        // 验证周期类型
        if (cycle == null || cycle.trim().isEmpty()) {
            throw new IllegalArgumentException("统计周期不能为空");
        }

        if (!"monthly".equals(cycle) && !"quarterly".equals(cycle) && !"yearly".equals(cycle)) {
            throw new IllegalArgumentException("统计周期类型不合法，必须是 monthly、quarterly 或 yearly");
        }

        // 验证年份
        if (year == null) {
            throw new IllegalArgumentException("年份不能为空");
        }
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("年份不合法，必须在 2000-2100 之间");
        }

        // 验证月份（月度统计时）
        if ("monthly".equals(cycle)) {
            if (month == null) {
                throw new IllegalArgumentException("月度统计时，月份不能为空");
            }
            if (month < 1 || month > 12) {
                throw new IllegalArgumentException("月份不合法，必须在 1-12 之间");
            }
        }

        // 验证季度（季度统计时）
        if ("quarterly".equals(cycle)) {
            if (quarter == null) {
                throw new IllegalArgumentException("季度统计时，季度不能为空");
            }
            if (quarter < 1 || quarter > 4) {
                throw new IllegalArgumentException("季度不合法，必须在 1-4 之间");
            }
        }

        logger.debug("✅ 参数验证通过");
    }

    /**
     * 🔥 计算汇总行
     *
     * @param list 统计数据列表
     * @return 汇总行数据
     */
    private Map<String, Object> calculateSummary(List<Map<String, Object>> list) {
        Map<String, Object> summary = new HashMap<>();

        int totalBills = 0;
        int paidBills = 0;
        int unpaidBills = 0;
        int overdueBills = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal collectedAmount = BigDecimal.ZERO;
        BigDecimal collectedLateFee = BigDecimal.ZERO;

        // 遍历所有记录，累加统计值
        for (Map<String, Object> row : list) {
            totalBills += getIntValue(row, "totalBills");
            paidBills += getIntValue(row, "paidBills");
            unpaidBills += getIntValue(row, "unpaidBills");
            overdueBills += getIntValue(row, "overdueBills");
            totalAmount = totalAmount.add(getBigDecimalValue(row, "totalAmount"));
            collectedAmount = collectedAmount.add(getBigDecimalValue(row, "collectedAmount"));
            collectedLateFee = collectedLateFee.add(getBigDecimalValue(row, "collectedLateFee"));
        }

        // 计算总收缴率
        BigDecimal collectionRate = BigDecimal.ZERO;
        if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
            collectionRate = collectedAmount
                    .multiply(new BigDecimal("100"))
                    .divide(totalAmount, 2, RoundingMode.HALF_UP);
        }

        // 构建汇总行
        summary.put("itemId", "");
        summary.put("itemName", "【汇总】");
        summary.put("chargeCycle", "");
        summary.put("totalBills", totalBills);
        summary.put("paidBills", paidBills);
        summary.put("unpaidBills", unpaidBills);
        summary.put("overdueBills", overdueBills);
        summary.put("totalAmount", totalAmount);
        summary.put("collectedAmount", collectedAmount);
        summary.put("collectedLateFee", collectedLateFee);
        summary.put("collectionRate", collectionRate);
        summary.put("isSummary", true);  // 标记为汇总行

        logger.debug("✅ 汇总计算完成: totalBills={}, collectionRate={}%", totalBills, collectionRate);

        return summary;
    }

    /**
     * 🔥 安全获取整数值
     */
    private int getIntValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            logger.warn("⚠️ 无法转换为整数: key={}, value={}", key, value);
            return 0;
        }
    }

    /**
     * 🔥 安全获取 BigDecimal 值
     */
    private BigDecimal getBigDecimalValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            logger.warn("⚠️ 无法转换为 BigDecimal: key={}, value={}", key, value);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 🆕 获取可用的统计年份列表
     */
    public List<Integer> getAvailableYears() {
        logger.info("========================================");
        logger.info("【Service】获取可用统计年份");
        logger.info("========================================");

        try {
            List<Integer> years = financeDao.getAvailableYears();

            logger.info("✅ 获取成功: years={}", years);

            return years;

        } catch (Exception e) {
            logger.error("❌ 获取可用年份失败", e);
            // 返回默认年份列表
            List<Integer> defaultYears = new ArrayList<>();
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            for (int i = 0; i < 5; i++) {
                defaultYears.add(currentYear - i);
            }
            logger.warn("⚠️ 返回默认年份列表: {}", defaultYears);
            return defaultYears;
        }
    }
    /**
     * 📈 获取本月收入统计
     */
    public Map<String, Object> getMonthlyIncomeStatistics() {
        logger.info("查询本月收入统计");

        Map<String, Object> result = new HashMap<>();

        try {
            // 🔥 修复：使用实例调用，不是静态调用
            BigDecimal currentMonthIncome = paymentRecordDao.getMonthlyIncome(
                    getCurrentMonthStart(), getCurrentMonthEnd()
            );

            // 🔥 修复：使用实例调用
            BigDecimal lastMonthIncome = paymentRecordDao.getMonthlyIncome(
                    getLastMonthStart(), getLastMonthEnd()
            );

            // 计算增长率
            double changeRate = 0.0;
            if (lastMonthIncome.compareTo(BigDecimal.ZERO) > 0) {
                changeRate = currentMonthIncome.subtract(lastMonthIncome)
                        .divide(lastMonthIncome, 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(new BigDecimal("100"))
                        .doubleValue();
            }

            result.put("income", currentMonthIncome);
            result.put("lastMonthIncome", lastMonthIncome);
            result.put("changeRate", String.format("%.1f", changeRate));

            logger.info("本月收入: ¥{}, 上月: ¥{}, 增长: {}%",
                    currentMonthIncome, lastMonthIncome, changeRate);

        } catch (Exception e) {
            logger.error("查询本月收入失败", e);
            result.put("income", BigDecimal.ZERO);
            result.put("lastMonthIncome", BigDecimal.ZERO);
            result.put("changeRate", "0.0");
        }

        return result;
    }

    /**
     * 💰 获取今日缴费统计
     */
    public Map<String, Object> getTodayPaymentStatistics() {
        logger.info("查询今日缴费统计");

        try {
            // 🔥 修复：使用实例调用
            Map<String, Object> statistics = paymentRecordDao.getTodayPaymentStatistics();

            logger.info("今日缴费: {}笔, ¥{}",
                    statistics.get("count"), statistics.get("amount"));

            return statistics;

        } catch (Exception e) {
            logger.error("查询今日缴费失败", e);

            Map<String, Object> result = new HashMap<>();
            result.put("count", 0);
            result.put("amount", BigDecimal.ZERO);
            return result;
        }
    }

    /**
     * 📋 获取最近动态
     */
    public List<Map<String, Object>> getRecentActivities(int limit) {
        logger.info("查询最近动态, limit={}", limit);

        List<Map<String, Object>> activities = new ArrayList<>();

        try {
            // 🔥 修复：使用实例调用
            List<PaymentRecord> recentRecords = paymentRecordDao.getRecentPayments(limit);

            for (PaymentRecord record : recentRecords) {
                Map<String, Object> activity = new HashMap<>();

                if ("paid".equals(record.getPaymentStatus())) {
                    // 缴费成功
                    activity.put("type", "payment");
                    activity.put("title", "缴费成功");
                    activity.put("content", String.format("业主 %s 缴纳了 %s，金额 ¥%.2f",
                            record.getOwnerName() != null ? record.getOwnerName() : "未知业主",
                            record.getItemName() != null ? record.getItemName() : "费用",
                            record.getAmount() != null ? record.getAmount() : BigDecimal.ZERO));
                    activity.put("time", record.getPaymentDate() != null
                            ? formatTime(record.getPaymentDate())
                            : "未知时间");

                } else if ("overdue".equals(record.getPaymentStatus())) {
                    // 逾期提醒
                    activity.put("type", "overdue");
                    activity.put("title", "账单逾期");
                    activity.put("content", String.format("业主 %s 的 %s 已逾期，金额 ¥%.2f",
                            record.getOwnerName() != null ? record.getOwnerName() : "未知业主",
                            record.getItemName() != null ? record.getItemName() : "费用",
                            record.getAmount() != null ? record.getAmount() : BigDecimal.ZERO));
                    activity.put("time", record.getDueDate() != null
                            ? formatTime(record.getDueDate())
                            : "未知时间");
                }

                activities.add(activity);
            }

            logger.info("查询到 {} 条动态", activities.size());

        } catch (Exception e) {
            logger.error("查询最近动态失败", e);
        }

        return activities;
    }

// ==================== 辅助方法 ====================

    /**
     * 格式化时间显示（相对时间）
     */
    private String formatTime(Date date) {
        if (date == null) return "未知时间";

        long diff = System.currentTimeMillis() - date.getTime();
        long minutes = diff / (60 * 1000);
        long hours = diff / (60 * 60 * 1000);
        long days = diff / (24 * 60 * 60 * 1000);

        if (minutes < 1) {
            return "刚刚";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (hours < 24) {
            return hours + "小时前";
        } else if (days == 1) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return "昨天 " + sdf.format(date);
        } else if (days < 7) {
            return days + "天前";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return sdf.format(date);
        }
    }

    private Date getCurrentMonthStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date getCurrentMonthEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    private Date getLastMonthStart() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    private Date getLastMonthEnd() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

}
