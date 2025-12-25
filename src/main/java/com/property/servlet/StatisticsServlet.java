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

/**
 * 统计Servlet
 */
@WebServlet("/admin/statistics")
public class StatisticsServlet extends BaseServlet {
    private StatisticsService statisticsService = new StatisticsService();

    /**
     * 获取仪表盘数据（首页调用）
     */
    public void dashboard(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
            return;
        }

        try {
            Map<String, Object> stats = statisticsService.getDashboardStatistics();

            logger.info("仪表盘数据查询成功: " + stats);
            writeSuccess(resp, "查询成功", stats);
        } catch (Exception e) {
            logger.error("获取仪表盘数据失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取概览数据（与 dashboard 相同）
     */
    public void overview(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        dashboard(req, resp);
    }

    /**
     * 获取收费趋势数据（首页图表调用）
     */
    public void trend(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
            return;
        }

        try {
            // 获取最近6个月的数据
            List<Map<String, Object>> trendData = statisticsService.getPaymentTrend();

            // 转换为前端需要的格式
            List<String> months = new ArrayList<>();
            List<Double> receivables = new ArrayList<>();
            List<Double> received = new ArrayList<>();

            for (Map<String, Object> data : trendData) {
                months.add((String) data.get("month"));

                // 处理 BigDecimal 类型
                Object totalAmountObj = data.get("totalAmount");
                Object paidAmountObj = data.get("paidAmount");

                double totalAmount = 0.0;
                double paidAmount = 0.0;

                if (totalAmountObj instanceof BigDecimal) {
                    totalAmount = ((BigDecimal) totalAmountObj).doubleValue();
                } else if (totalAmountObj instanceof Number) {
                    totalAmount = ((Number) totalAmountObj).doubleValue();
                }

                if (paidAmountObj instanceof BigDecimal) {
                    paidAmount = ((BigDecimal) paidAmountObj).doubleValue();
                } else if (paidAmountObj instanceof Number) {
                    paidAmount = ((Number) paidAmountObj).doubleValue();
                }

                receivables.add(totalAmount);
                received.add(paidAmount);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("months", months);
            result.put("receivable", receivables);  // 应收
            result.put("received", received);        // 实收

            logger.info("趋势数据查询成功: " + result);
            writeSuccess(resp, "查询成功", result);
        } catch (Exception e) {
            logger.error("获取趋势数据失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取待处理报修列表（首页调用）
     */
    public void findPending(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
            return;
        }

        try {
            // 调用 RepairService 获取待处理报修
            List<Map<String, Object>> pendingRepairs = statisticsService.getPendingRepairs();

            logger.info("待处理报修查询成功，数量: " + pendingRepairs.size());
            writeSuccess(resp, "查询成功", pendingRepairs);
        } catch (Exception e) {
            logger.error("获取待处理报修失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取月度统计数据
     */
    public void monthly(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        trend(req, resp);  // 复用 trend 方法
    }

    /**
     * 获取缴费状态分布
     */
    public void status(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
            return;
        }

        try {
            Map<String, Object> stats = statisticsService.getDashboardStatistics();

            // 提取状态分布数据
            Map<String, Object> result = new HashMap<>();
            result.put("paid", stats.getOrDefault("paidCount", 0));
            result.put("unpaid", stats.getOrDefault("unpaidCount", 0));
            result.put("overdue", stats.getOrDefault("overdueCount", 0));

            writeSuccess(resp, "查询成功", result);
        } catch (Exception e) {
            logger.error("获取状态分布失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取楼栋统计数据
     */
    public void building(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
            return;
        }

        try {
            List<Map<String, Object>> buildingStats = statisticsService.getBuildingPaymentStatus();

            // 转换为前端需要的格式
            List<String> buildings = new ArrayList<>();
            List<Double> rates = new ArrayList<>();

            for (Map<String, Object> data : buildingStats) {
                buildings.add(data.get("buildingNo") + "栋");

                // 获取缴费率，处理不同类型
                Object rateObj = data.get("paymentRate");
                double rate = 0.0;

                if (rateObj instanceof BigDecimal) {
                    rate = ((BigDecimal) rateObj).doubleValue();
                } else if (rateObj instanceof Number) {
                    rate = ((Number) rateObj).doubleValue();
                }

                rates.add(rate);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("buildings", buildings);
            result.put("rates", rates);

            writeSuccess(resp, "查询成功", result);
        } catch (Exception e) {
            logger.error("获取楼栋统计失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取物业收费统计
     */
    public void paymentStats(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
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
        } catch (IllegalArgumentException e) {
            writeError(resp, e.getMessage());
        } catch (Exception e) {
            logger.error("获取收费统计失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取各楼栋缴费情况
     */
    public void buildingStats(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        building(req, resp);
    }

    /**
     * 获取收费趋势数据
     */
    public void paymentTrend(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        trend(req, resp);
    }

    /**
     * 导出财务报表（Excel格式）
     */
    public void exportReport(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
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
            // 生成财务报表数据
            Map<String, Object> reportData = statisticsService.generateFinancialReport(startDate, endDate);

            // 创建Excel工作簿
            Workbook workbook = createFinancialReportExcel(reportData);

            // 设置响应头
            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            resp.setHeader("Content-Disposition",
                    "attachment; filename=financial_report_" +
                            new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx");

            // 输出Excel文件
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

        // 创建样式
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

        // 创建汇总表
        Sheet summarySheet = workbook.createSheet("收费汇总");
        createSummarySheet(summarySheet, reportData, headerStyle, dataStyle);

        // 创建楼栋统计表
        Sheet buildingSheet = workbook.createSheet("楼栋统计");
        createBuildingSheet(buildingSheet, reportData, headerStyle, dataStyle);

        return workbook;
    }

    /**
     * 创建汇总表
     */
    private void createSummarySheet(Sheet sheet, Map<String, Object> reportData,
                                    CellStyle headerStyle, CellStyle dataStyle) {
        int rowNum = 0;

        // 标题
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("物业费收缴汇总报表");
        titleCell.setCellStyle(headerStyle);

        // 时间范围
        rowNum++;
        Row dateRow = sheet.createRow(rowNum++);
        dateRow.createCell(0).setCellValue("统计时间：");
        dateRow.createCell(1).setCellValue(
                new SimpleDateFormat("yyyy-MM-dd").format(reportData.get("startDate")) +
                        " 至 " +
                        new SimpleDateFormat("yyyy-MM-dd").format(reportData.get("endDate"))
        );

        // 汇总数据
        @SuppressWarnings("unchecked")
        Map<String, Object> periodStats = (Map<String, Object>) reportData.get("periodStats");

        rowNum++;
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("统计项");
        headerRow.createCell(1).setCellValue("数值");
        headerRow.getCell(0).setCellStyle(headerStyle);
        headerRow.getCell(1).setCellStyle(headerStyle);

        // 数据行
        createDataRow(sheet, rowNum++, "总账单数", periodStats.get("totalCount"));
        createDataRow(sheet, rowNum++, "已缴费数", periodStats.get("paidCount"));
        createDataRow(sheet, rowNum++, "应收总额", periodStats.get("totalAmount"));
        createDataRow(sheet, rowNum++, "实收总额", periodStats.get("paidAmount"));
        createDataRow(sheet, rowNum++, "滞纳金总额", periodStats.get("totalLateFee"));

        // 自动调整列宽
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    /**
     * 创建数据行
     */
    private void createDataRow(Sheet sheet, int rowNum, String label, Object value) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(label);

        if (value instanceof Number) {
            row.createCell(1).setCellValue(((Number) value).doubleValue());
        } else {
            row.createCell(1).setCellValue(String.valueOf(value));
        }
    }

    /**
     * 创建楼栋统计表
     */
    private void createBuildingSheet(Sheet sheet, Map<String, Object> reportData,
                                     CellStyle headerStyle, CellStyle dataStyle) {
        int rowNum = 0;

        // 表头
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"楼栋号", "总账单数", "已缴数", "应收总额", "实收总额", "欠费总额", "滞纳金", "收缴率(%)"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 数据行
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> buildingStats = (List<Map<String, Object>>) reportData.get("buildingStats");

        if (buildingStats != null) {
            for (Map<String, Object> stat : buildingStats) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(String.valueOf(stat.get("buildingNo")));

                // 处理数字类型
                setCellNumericValue(row, 1, stat.get("totalRecords"));
                setCellNumericValue(row, 2, stat.get("paidRecords"));
                setCellNumericValue(row, 3, stat.get("totalAmount"));
                setCellNumericValue(row, 4, stat.get("paidAmount"));
                setCellNumericValue(row, 5, stat.get("unpaidAmount"));
                setCellNumericValue(row, 6, stat.get("totalLateFee"));
                setCellNumericValue(row, 7, stat.get("paymentRate"));
            }
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * 设置单元格数值
     */
    private void setCellNumericValue(Row row, int cellIndex, Object value) {
        if (value instanceof Number) {
            row.createCell(cellIndex).setCellValue(((Number) value).doubleValue());
        } else {
            row.createCell(cellIndex).setCellValue(0.0);
        }
    }
}
