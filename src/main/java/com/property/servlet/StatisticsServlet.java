package com.property.servlet;

import com.property.service.StatisticsService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

/**
 * 统计Servlet
 */
@WebServlet("/admin/statistics")
public class StatisticsServlet extends BaseServlet {
    private StatisticsService statisticsService = new StatisticsService();

    /**
     * ✅ 获取概览数据（支持按收费项目筛选）
     */
    public void overview(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
            return;
        }

        try {
            // ✅ 获取筛选参数
            String keyword = getStringParameter(req, "keyword");
            String status = getStringParameter(req, "status");
            String itemId = getStringParameter(req, "itemId");  // ✅ 新增：收费项目筛选

            Map<String, Object> stats;

            // ✅ 如果有筛选条件，使用筛选统计
            if ((keyword != null && !keyword.isEmpty()) ||
                    (status != null && !status.isEmpty()) ||
                    (itemId != null && !itemId.isEmpty())) {

                logger.info("📊 使用筛选条件统计 - keyword: " + keyword + ", status: " + status + ", itemId: " + itemId);
                stats = statisticsService.getFilteredStatistics(keyword, status, itemId);
            } else {
                // 无筛选条件，使用全局统计
                logger.info("📊 使用全局统计（无筛选条件）");
                stats = statisticsService.getDashboardStatistics();
            }

            // 确保返回的数据格式正确
            Map<String, Object> result = new HashMap<>();

            // 原有数据（统计面板使用）
            result.put("totalHouses", getIntValue(stats.get("totalHouses")));
            result.put("totalOwners", getIntValue(stats.get("totalOwners")));
            result.put("unpaidCount", getIntValue(stats.get("unpaidCount")));
            result.put("currentMonthIncome", getDoubleValue(stats.get("monthlyIncome")));

            // 新增数据（首页仪表盘使用）
            result.put("occupiedHouses", getIntValue(stats.get("occupiedHouses")));
            result.put("vacantHouses", getIntValue(stats.get("vacantHouses")));
            result.put("monthlyIncome", getDoubleValue(stats.get("monthlyIncome")));
            result.put("paidCount", getIntValue(stats.get("paidCount")));
            result.put("paymentRate", getDoubleValue(stats.get("paymentRate")));

            // 报修数据
            result.put("pendingRepairs", getIntValue(stats.get("pendingRepairs")));
            result.put("processingRepairs", getIntValue(stats.get("processingRepairs")));
            result.put("completedRepairs", getIntValue(stats.get("completedRepairs")));
            result.put("cancelledRepairs", getIntValue(stats.get("cancelledRepairs")));
            result.put("avgRating", getDoubleValue(stats.get("avgRating")));

            // 投诉数据
            result.put("totalComplaints", getIntValue(stats.get("totalComplaints")));
            result.put("pendingComplaints", getIntValue(stats.get("pendingComplaints")));
            result.put("processingComplaints", getIntValue(stats.get("processingComplaints")));
            result.put("resolvedComplaints", getIntValue(stats.get("resolvedComplaints")));
            result.put("closedComplaints", getIntValue(stats.get("closedComplaints")));

            // ✅ 筛选统计特有数据
            result.put("totalRecords", getIntValue(stats.get("totalRecords")));
            result.put("totalCount", getIntValue(stats.get("totalCount")));
            result.put("overdueCount", getIntValue(stats.get("overdueCount")));
            result.put("totalAmount", getDoubleValue(stats.get("totalAmount")));
            result.put("paidAmount", getDoubleValue(stats.get("paidAmount")));
            result.put("unpaidAmount", getDoubleValue(stats.get("unpaidAmount")));
            result.put("overdueAmount", getDoubleValue(stats.get("overdueAmount")));
            result.put("totalLateFee", getDoubleValue(stats.get("totalLateFee")));

            // ✅ 费用类型统计
            if (stats.containsKey("feeTypeStats")) {
                result.put("feeTypeStats", stats.get("feeTypeStats"));
            }

            logger.info("✅ 概览数据查询成功");
            writeSuccess(resp, "查询成功", result);
        } catch (Exception e) {
            logger.error("❌ 获取概览数据失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 🔥 获取月度图表数据（修复版）
     */
    public void monthly(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
            return;
        }

        try {
            // 获取最近6个月的数据
            List<Map<String, Object>> trendData = statisticsService.getPaymentTrend();

            List<String> months = new ArrayList<>();
            List<Double> totalAmounts = new ArrayList<>();
            List<Double> paidAmounts = new ArrayList<>();

            if (trendData == null || trendData.isEmpty()) {
                // 如果没有数据，生成默认的6个月数据
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

                for (int i = 5; i >= 0; i--) {
                    cal.add(Calendar.MONTH, -i);
                    months.add(sdf.format(cal.getTime()));
                    totalAmounts.add(0.0);
                    paidAmounts.add(0.0);
                    cal = Calendar.getInstance(); // 重置
                }
            } else {
                for (Map<String, Object> data : trendData) {
                    months.add(String.valueOf(data.get("month")));
                    totalAmounts.add(getDoubleValue(data.get("totalAmount")));
                    paidAmounts.add(getDoubleValue(data.get("paidAmount")));
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("months", months);
            result.put("totalAmounts", totalAmounts);
            result.put("paidAmounts", paidAmounts);

            logger.info("✅ 月度数据查询成功，共 " + months.size() + " 个月");
            writeSuccess(resp, "查询成功", result);
        } catch (Exception e) {
            logger.error("❌ 获取月度数据失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 🔥 获取缴费状态分布（修复版）
     */
    public void status(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
            return;
        }

        try {
            Map<String, Object> stats = statisticsService.getDashboardStatistics();

            Map<String, Object> result = new HashMap<>();
            result.put("paid", getIntValue(stats.get("paidCount")));
            result.put("unpaid", getIntValue(stats.get("unpaidCount")));
            result.put("overdue", getIntValue(stats.get("overdueCount")));

            logger.info("✅ 状态分布查询成功: " + result);
            writeSuccess(resp, "查询成功", result);
        } catch (Exception e) {
            logger.error("❌ 获取状态分布失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 🔥 获取楼栋缴费率（修复版）
     */
    public void building(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
            return;
        }

        try {
            List<Map<String, Object>> buildingStats = statisticsService.getBuildingPaymentStatus();

            List<String> buildings = new ArrayList<>();
            List<Double> rates = new ArrayList<>();

            if (buildingStats == null || buildingStats.isEmpty()) {
                // 如果没有数据，返回空数组
                logger.warn("⚠️ 没有楼栋统计数据");
            } else {
                for (Map<String, Object> data : buildingStats) {
                    String buildingNo = String.valueOf(data.get("buildingNo"));
                    buildings.add(buildingNo + "栋");

                    double rate = getDoubleValue(data.get("paymentRate"));
                    rates.add(Math.round(rate * 100.0) / 100.0); // 保留2位小数
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("buildings", buildings);
            result.put("rates", rates);

            logger.info("✅ 楼栋数据查询成功，共 " + buildings.size() + " 栋");
            writeSuccess(resp, "查询成功", result);
        } catch (Exception e) {
            logger.error("❌ 获取楼栋统计失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取仪表盘数据（首页调用）
     */
    public void dashboard(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        overview(req, resp);
    }

    /**
     * 获取收费趋势数据（首页图表调用）
     */
    public void paymentTrend(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        monthly(req, resp);
    }

    /**
     * 获取物业收费统计
     */
    public void paymentStats(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        String startMonth = getStringParameter(req, "startMonth");
        String endMonth = getStringParameter(req, "endMonth");

        if (startMonth == null || startMonth.isEmpty()) {
            writeError(resp, "开始月份不能为空");
            return;
        }
        if (endMonth == null || endMonth.isEmpty()) {
            writeError(resp, "结束月份不能为空");
            return;
        }

        try {
            List<Map<String, Object>> stats = statisticsService.getPaymentStatistics(startMonth, endMonth);
            writeSuccess(resp, "查询成功", stats);
        } catch (Exception e) {
            logger.error("获取收费统计失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 导出财务报表（Excel格式）
     */
    public void exportReport(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!checkRole(req, resp, "finance")) {
            return;
        }

        String startDateStr = getStringParameter(req, "startDate");
        String endDateStr = getStringParameter(req, "endDate");

        if (startDateStr == null || startDateStr.isEmpty() ||
                endDateStr == null || endDateStr.isEmpty()) {
            writeError(resp, "开始日期和结束日期不能为空");
            return;
        }

        Date startDate, endDate;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            startDate = sdf.parse(startDateStr);
            endDate = sdf.parse(endDateStr);
        } catch (Exception e) {
            writeError(resp, "日期格式不正确");
            return;
        }

        try {
            Map<String, Object> reportData = statisticsService.generateFinancialReport(startDate, endDate);
            Workbook workbook = createFinancialReportExcel(reportData);

            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            resp.setHeader("Content-Disposition",
                    "attachment; filename=financial_report_" +
                            new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx");

            ServletOutputStream out = resp.getOutputStream();
            workbook.write(out);
            workbook.close();
            out.flush();
            out.close();

            logger.info("导出财务报表成功");
        } catch (Exception e) {
            logger.error("导出财务报表失败", e);
            writeError(resp, "导出失败：" + e.getMessage());
        }
    }

    /**
     * 创建财务报表Excel
     */
    private Workbook createFinancialReportExcel(Map<String, Object> reportData) {
        Workbook workbook = new XSSFWorkbook();

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.LEFT);

        Sheet summarySheet = workbook.createSheet("收费汇总");
        createSummarySheet(summarySheet, reportData, headerStyle, dataStyle);

        Sheet buildingSheet = workbook.createSheet("楼栋统计");
        createBuildingSheet(buildingSheet, reportData, headerStyle, dataStyle);

        return workbook;
    }

    private void createSummarySheet(Sheet sheet, Map<String, Object> reportData,
                                    CellStyle headerStyle, CellStyle dataStyle) {
        int rowNum = 0;

        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("物业费收缴汇总报表");
        titleCell.setCellStyle(headerStyle);

        rowNum++;
        Row dateRow = sheet.createRow(rowNum++);
        dateRow.createCell(0).setCellValue("统计时间：");
        dateRow.createCell(1).setCellValue(
                new SimpleDateFormat("yyyy-MM-dd").format(reportData.get("startDate")) +
                        " 至 " +
                        new SimpleDateFormat("yyyy-MM-dd").format(reportData.get("endDate"))
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> periodStats = (Map<String, Object>) reportData.get("periodStats");

        rowNum++;
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("统计项");
        headerRow.createCell(1).setCellValue("数值");
        headerRow.getCell(0).setCellStyle(headerStyle);
        headerRow.getCell(1).setCellStyle(headerStyle);

        createDataRow(sheet, rowNum++, "总账单数", periodStats.get("totalCount"));
        createDataRow(sheet, rowNum++, "已缴费数", periodStats.get("paidCount"));
        createDataRow(sheet, rowNum++, "应收总额", periodStats.get("totalAmount"));
        createDataRow(sheet, rowNum++, "实收总额", periodStats.get("paidAmount"));
        createDataRow(sheet, rowNum++, "滞纳金总额", periodStats.get("totalLateFee"));

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createDataRow(Sheet sheet, int rowNum, String label, Object value) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(label);

        if (value instanceof Number) {
            row.createCell(1).setCellValue(((Number) value).doubleValue());
        } else {
            row.createCell(1).setCellValue(String.valueOf(value));
        }
    }

    private void createBuildingSheet(Sheet sheet, Map<String, Object> reportData,
                                     CellStyle headerStyle, CellStyle dataStyle) {
        int rowNum = 0;

        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"楼栋号", "总账单数", "已缴数", "应收总额", "实收总额", "欠费总额", "滞纳金", "收缴率(%)"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> buildingStats = (List<Map<String, Object>>) reportData.get("buildingStats");

        if (buildingStats != null) {
            for (Map<String, Object> stat : buildingStats) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(String.valueOf(stat.get("buildingNo")));

                setCellNumericValue(row, 1, stat.get("totalRecords"));
                setCellNumericValue(row, 2, stat.get("paidRecords"));
                setCellNumericValue(row, 3, stat.get("totalAmount"));
                setCellNumericValue(row, 4, stat.get("paidAmount"));
                setCellNumericValue(row, 5, stat.get("unpaidAmount"));
                setCellNumericValue(row, 6, stat.get("totalLateFee"));
                setCellNumericValue(row, 7, stat.get("paymentRate"));
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void setCellNumericValue(Row row, int cellIndex, Object value) {
        if (value instanceof Number) {
            row.createCell(cellIndex).setCellValue(((Number) value).doubleValue());
        } else {
            row.createCell(cellIndex).setCellValue(0.0);
        }
    }

    /**
     * 🔧 辅助方法：从 Object 中提取 int 值
     */
    private int getIntValue(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 🔧 辅助方法：从 Object 中提取 double 值
     */
    private double getDoubleValue(Object value) {
        if (value == null) {
            return 0.0;
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).doubleValue();
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }
}