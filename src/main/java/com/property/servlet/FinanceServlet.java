package com.property.servlet;

import com.property.entity.FinanceStatistics;
import com.property.entity.User;
import com.property.service.FinanceService;
import com.property.util.ExcelExportUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 财务管理Servlet
 *
 * @author PropertyManagementSystem
 * @version 1.0
 */
@WebServlet("/finance")
public class FinanceServlet extends BaseServlet {
    private final FinanceService financeService = new FinanceService();

    // ========================================
    // 🆕 新增:工作台统计方法
    // ========================================

    /**
     * 获取本月收入
     * 🔥 修复：从数据库真实查询
     */
    public void getMonthlyIncome(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        logger.info("========================================");
        logger.info("【获取本月收入】");
        logger.info("========================================");

        try {
            // 🔥 从数据库查询真实数据
            Map<String, Object> data = financeService.getMonthlyIncomeStatistics();

            logger.info("✅ 本月收入: ¥{}", data.get("income"));
            logger.info("   上月收入: ¥{}", data.get("lastMonthIncome"));
            logger.info("   增长率: {}%", data.get("changeRate"));

            writeSuccess(resp, "获取成功", data);

        } catch (Exception e) {
            logger.error("❌ 获取本月收入失败", e);
            writeError(resp, "获取失败:" + e.getMessage());
        }
    }

    /**
     * 获取今日缴费
     * 🔥 修复：从数据库真实查询
     */
    public void getTodayPayment(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        logger.info("========================================");
        logger.info("【获取今日缴费】");
        logger.info("========================================");

        try {
            // 🔥 从数据库查询真实数据
            Map<String, Object> data = financeService.getTodayPaymentStatistics();

            logger.info("✅ 今日缴费: {}笔, ¥{}", data.get("count"), data.get("amount"));

            writeSuccess(resp, "获取成功", data);

        } catch (Exception e) {
            logger.error("❌ 获取今日缴费失败", e);
            writeError(resp, "获取失败:" + e.getMessage());
        }
    }

    /**
     * 获取最近动态
     * 🔥 修复：从数据库真实查询
     */
    public void getRecentActivities(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int limit = getIntParameter(req, "limit", 10);

        logger.info("========================================");
        logger.info("【获取最近动态】limit: {}", limit);
        logger.info("========================================");

        try {
            // 🔥 从数据库查询真实数据
            List<Map<String, Object>> activities = financeService.getRecentActivities(limit);

            logger.info("✅ 返回 {} 条动态", activities.size());
            writeSuccess(resp, "获取成功", activities);

        } catch (Exception e) {
            logger.error("❌ 获取最近动态失败", e);
            writeError(resp, "获取失败:" + e.getMessage());
        }
    }


    /**
     * 查询欠费业主列表(分页)
     * 🔧 修复:添加关键词搜索功能
     */
    public void getArrearsOwners(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        int pageNum = getIntParameter(req, "pageNum", 1);
        int pageSize = getIntParameter(req, "pageSize", 10);
        String minAmountStr = getStringParameter(req, "minAmount");
        String keyword = getStringParameter(req, "keyword"); // 🆕 新增关键词参数

        logger.info("========================================");
        logger.info("【查询欠费业主】");
        logger.info("pageNum={}, pageSize={}, minAmount={}, keyword={}",
                pageNum, pageSize, minAmountStr, keyword);
        logger.info("========================================");

        try {
            BigDecimal minAmount = BigDecimal.ZERO;
            if (minAmountStr != null && !minAmountStr.isEmpty()) {
                minAmount = new BigDecimal(minAmountStr);
            }

            Map<String, Object> result = financeService.getArrearsOwners(
                    pageNum, pageSize, minAmount, keyword // 🆕 传递关键词
            );

            logger.info("✅ 查询成功: total={}", result.get("total"));
            writeSuccess(resp, "查询成功", result);

        } catch (Exception e) {
            logger.error("❌ 查询欠费业主失败", e);
            writeError(resp, "查询失败:" + e.getMessage());
        }
    }

    /**
     * 导出欠费业主列表到Excel
     * 🔧 修复:添加关键词搜索功能,修复滞纳金为空问题
     */
    public void exportArrearsOwners(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        OutputStream outputStream = null;

        try {
            String minAmountStr = getStringParameter(req, "minAmount");
            String keyword = getStringParameter(req, "keyword"); // 🆕 新增关键词参数

            BigDecimal minAmount = BigDecimal.ZERO;
            if (minAmountStr != null && !minAmountStr.isEmpty()) {
                minAmount = new BigDecimal(minAmountStr);
            }

            logger.info("📥 开始导出欠费业主列表");
            logger.info("最低欠费金额: {}", minAmount);
            logger.info("关键词: {}", keyword);

            // 查询所有欠费业主(不分页)
            List<FinanceStatistics> arrearsOwners =
                    financeService.getAllArrearsOwners(minAmount, keyword); // 🆕 传递关键词

            if (arrearsOwners == null || arrearsOwners.isEmpty()) {
                logger.warn("⚠️ 没有找到欠费业主");
                resp.setContentType("text/html;charset=UTF-8");
                resp.getWriter().write(
                        "<script>alert('没有可导出的欠费数据');history.back();</script>"
                );
                return;
            }

            // 限制导出数量
            final int MAX_EXPORT_SIZE = 50000;
            if (arrearsOwners.size() > MAX_EXPORT_SIZE) {
                resp.setContentType("text/html;charset=UTF-8");
                resp.getWriter().write(
                        "<script>alert('导出数据过多(超过" + MAX_EXPORT_SIZE +
                                "条),请提高最低欠费金额筛选');history.back();</script>"
                );
                return;
            }

            // 生成文件名
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = "欠费业主列表_" + sdf.format(new Date()) + ".xlsx";

            // 设置响应头
            setExportResponseHeaders(resp, fileName);

            // 导出Excel
            outputStream = resp.getOutputStream();
            ExcelExportUtil.exportArrearsOwnerList(arrearsOwners, outputStream);

            outputStream.flush();

            logger.info("✅ 导出成功:{} ({} 条记录)", fileName, arrearsOwners.size());

        } catch (Exception e) {
            logger.error("❌ 导出失败", e);
            handleExportError(resp, outputStream, e);
        }
    }

    /**
     * 查询逾期统计
     */
    public void getOverdueStatistics(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        logger.info("========================================");
        logger.info("【查询逾期统计】");
        logger.info("========================================");

        try {
            Map<String, Object> result = financeService.getOverdueStatistics();

            logger.info("逾期统计结果: {}", result);
            logger.info("========================================");

            writeSuccess(resp, "查询成功", result);

        } catch (Exception e) {
            logger.error("❌ 查询逾期统计失败", e);
            writeError(resp, "查询失败:" + e.getMessage());
        }
    }

    /**
     * 生成催缴通知
     */
    public void generatePaymentReminder(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        User currentUser = getCurrentUser(req);
        String ownerIds = getStringParameter(req, "ownerIds"); // 逗号分隔的业主ID列表

        logger.info("========================================");
        logger.info("【生成催缴通知】");
        logger.info("业主列表: {}", ownerIds);
        logger.info("========================================");

        if (ownerIds == null || ownerIds.trim().isEmpty()) {
            writeError(resp, "请选择要催缴的业主");
            return;
        }

        try {
            String[] ids = ownerIds.split(",");
            int successCount = 0;
            int failCount = 0;
            List<String> errorMessages = new ArrayList<>();

            for (String ownerId : ids) {
                String trimmedId = ownerId.trim();
                if (trimmedId.isEmpty()) {
                    continue;
                }

                try {
                    boolean success = financeService.generatePaymentReminder(
                            trimmedId,
                            currentUser.getUserId()
                    );

                    if (success) {
                        successCount++;
                        logger.info("✅ 生成催缴通知成功: {}", trimmedId);
                    } else {
                        failCount++;
                        errorMessages.add(trimmedId + ": 该业主无欠费记录");
                        logger.warn("⚠️ 生成催缴通知失败: {}", trimmedId);
                    }

                } catch (Exception e) {
                    failCount++;
                    errorMessages.add(trimmedId + ": " + e.getMessage());
                    logger.error("❌ 生成催缴通知异常: {}", trimmedId, e);
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("errorMessages", errorMessages);

            logger.info("========================================");
            logger.info("【生成催缴通知】完成");
            logger.info("成功: {}, 失败: {}", successCount, failCount);
            logger.info("========================================");

            if (successCount > 0) {
                String message = String.format("生成完成!成功: %d 条, 失败: %d 条",
                        successCount, failCount);
                writeSuccess(resp, message, result);
            } else {
                writeError(resp, "生成失败,请检查选中的业主");
            }

        } catch (Exception e) {
            logger.error("❌ 生成催缴通知失败", e);
            writeError(resp, "生成失败:" + e.getMessage());
        }
    }

    /**
     * 查询楼栋收缴率统计
     */
    public void getBuildingPaymentStatistics(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        logger.info("========================================");
        logger.info("【查询楼栋收缴率统计】");
        logger.info("========================================");

        try {
            List<Map<String, Object>> list = financeService.getBuildingPaymentStatistics();

            logger.info("✅ 查询成功: count={}", list.size());
            logger.info("========================================");

            writeSuccess(resp, "查询成功", list);

        } catch (Exception e) {
            logger.error("❌ 查询楼栋统计失败", e);
            writeError(resp, "查询失败:" + e.getMessage());
        }
    }

    /**
     * 查询月度收缴率统计
     */
    public void getMonthlyPaymentStatistics(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        int year = getIntParameter(req, "year", Calendar.getInstance().get(Calendar.YEAR));
        int month = getIntParameter(req, "month", Calendar.getInstance().get(Calendar.MONTH) + 1);

        logger.info("========================================");
        logger.info("【查询月度收缴率统计】");
        logger.info("year={}, month={}", year, month);
        logger.info("========================================");

        try {
            List<Map<String, Object>> list = financeService.getMonthlyPaymentStatistics(year, month);

            logger.info("✅ 查询成功: count={}", list.size());
            logger.info("========================================");

            writeSuccess(resp, "查询成功", list);

        } catch (IllegalArgumentException e) {
            logger.warn("⚠️ 参数错误: {}", e.getMessage());
            writeError(resp, e.getMessage());

        } catch (Exception e) {
            logger.error("❌ 查询月度统计失败", e);
            writeError(resp, "查询失败:" + e.getMessage());
        }
    }

    /**
     * 🆕 查询时间段收缴率统计
     * 🔥 核心修复：
     * 1. 完善参数验证和处理
     * 2. 优化异常处理逻辑
     * 3. 支持月度、季度、年度统计
     * 4. 自动添加汇总行
     */
    public void getPeriodPaymentStatistics(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        // 🔥 获取参数
        String cycle = getStringParameter(req, "cycle"); // monthly/quarterly/yearly
        Integer year = null;
        Integer month = null;
        Integer quarter = null;

        // 🔥 处理年份参数
        String yearStr = getStringParameter(req, "year");
        if (yearStr != null && !yearStr.trim().isEmpty()) {
            try {
                year = Integer.parseInt(yearStr);
            } catch (NumberFormatException e) {
                logger.warn("⚠️ 年份参数格式错误: {}", yearStr);
                writeError(resp, "年份参数格式错误");
                return;
            }
        } else {
            // 默认使用当前年份
            year = Calendar.getInstance().get(Calendar.YEAR);
        }

        // 🔥 根据周期类型处理月份或季度参数
        if ("monthly".equals(cycle)) {
            String monthStr = getStringParameter(req, "month");
            if (monthStr != null && !monthStr.trim().isEmpty()) {
                try {
                    month = Integer.parseInt(monthStr);
                } catch (NumberFormatException e) {
                    logger.warn("⚠️ 月份参数格式错误: {}", monthStr);
                    writeError(resp, "月份参数格式错误");
                    return;
                }
            } else {
                // 默认使用当前月份
                month = Calendar.getInstance().get(Calendar.MONTH) + 1;
            }
        } else if ("quarterly".equals(cycle)) {
            String quarterStr = getStringParameter(req, "quarter");
            if (quarterStr != null && !quarterStr.trim().isEmpty()) {
                try {
                    quarter = Integer.parseInt(quarterStr);
                } catch (NumberFormatException e) {
                    logger.warn("⚠️ 季度参数格式错误: {}", quarterStr);
                    writeError(resp, "季度参数格式错误");
                    return;
                }
            } else {
                // 默认使用第1季度
                quarter = 1;
            }
        }

        logger.info("========================================");
        logger.info("【查询时间段统计】");
        logger.info("cycle={}, year={}, month={}, quarter={}", cycle, year, month, quarter);
        logger.info("========================================");

        try {
            // 🔥 调用 Service 层（参数验证在 Service 层完成）
            List<Map<String, Object>> list = financeService.getPeriodPaymentStatistics(
                    cycle, year, month, quarter
            );

            logger.info("✅ 查询成功: count={}", list.size());
            logger.info("========================================");

            writeSuccess(resp, "查询成功", list);

        } catch (IllegalArgumentException e) {
            // 🔥 参数验证异常
            logger.warn("⚠️ 参数错误: {}", e.getMessage());
            writeError(resp, e.getMessage());

        } catch (Exception e) {
            // 🔥 其他异常
            logger.error("❌ 查询时间段统计失败", e);
            writeError(resp, "查询失败: " + e.getMessage());
        }
    }

    /**
     * 🆕 获取可用的统计年份列表
     */
    public void getAvailableYears(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        logger.info("========================================");
        logger.info("【获取可用统计年份】");
        logger.info("========================================");

        try {
            List<Integer> years = financeService.getAvailableYears();

            logger.info("✅ 获取成功: years={}", years);
            logger.info("========================================");

            writeSuccess(resp, "获取成功", years);

        } catch (Exception e) {
            logger.error("❌ 获取可用年份失败", e);
            writeError(resp, "获取失败: " + e.getMessage());
        }
    }

    // ========================================
    // 工具方法
    // ========================================

    /**
     * 设置导出响应头
     */
    private void setExportResponseHeaders(HttpServletResponse response, String fileName)
            throws IOException {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");

        // 处理中文文件名
        String encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8")
                .replaceAll("\\+", "%20");

        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + encodedFileName +
                        "\"; filename*=UTF-8''" + encodedFileName);

        // 防止缓存
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    /**
     * 处理导出错误
     */
    private void handleExportError(HttpServletResponse response,
                                   OutputStream outputStream,
                                   Exception e) throws IOException {

        if (outputStream != null && response.isCommitted()) {
            logger.error("❌ 响应已提交,无法发送错误信息");
            return;
        }

        response.reset();
        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        String errorMsg = "导出失败:" + e.getMessage();
        response.getWriter().write(
                "<script>alert('" + errorMsg.replace("'", "\\'") +
                        "');history.back();</script>"
        );
    }
}
