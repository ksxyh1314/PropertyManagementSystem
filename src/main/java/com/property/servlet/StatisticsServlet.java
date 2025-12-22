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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 统计Servlet
 */
@WebServlet("/statistics")
public class StatisticsServlet extends BaseServlet {
    private StatisticsService statisticsService = new StatisticsService();

    /**
     * 获取仪表盘统计数据
     */
    public void dashboard(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
            return;
        }

        try {
            Map<String, Object> stats = statisticsService.getDashboardStatistics();
            writeSuccess(resp, "查询成功", stats);
        } catch (Exception e) {
            logger.error("获取仪表盘统计数据失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取物业收费统计
     */
    public void paymentStats(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
    public void buildingStats(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        try {
            List<Map<String, Object>> stats = statisticsService.getBuildingPaymentStatus();
            writeSuccess(resp, "查询成功", stats);
        } catch (Exception e) {
            logger.error("获取楼栋缴费统计失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取收费趋势数据
     */
    public void paymentTrend(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        try {
            List<Map<String, Object>> trend = statisticsService.getPaymentTrend();
            writeSuccess(resp, "查询成功", trend);
        } catch (Exception e) {
            logger.error("获取收费趋势失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 导出财务报表（Excel格式）
     */
    public void exportReport(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "finance")) {
            return;
        }

        String startDateStr = getStringParameter(req, "startDate");
        String endDateStr = getStringParameter(req, "endDate");

        if (startDateStr == null || startDateStr.isEmpty() || endDateStr == null || endDateStr.isEmpty()) {
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

        Row row1 = sheet.createRow(rowNum++);
        row1.createCell(0).setCellValue("总账单数");
        row1.createCell(1).setCellValue(((Long) periodStats.get("totalCount")).doubleValue());

        Row row2 = sheet.createRow(rowNum++);
        row2.createCell(0).setCellValue("已缴费数");
        row2.createCell(1).setCellValue(((Long) periodStats.get("paidCount")).doubleValue());

        Row row3 = sheet.createRow(rowNum++);
        row3.createCell(0).setCellValue("应收总额");
        row3.createCell(1).setCellValue(((BigDecimal) periodStats.get("totalAmount")).doubleValue());

        Row row4 = sheet.createRow(rowNum++);
        row4.createCell(0).setCellValue("实收总额");
        row4.createCell(1).setCellValue(((BigDecimal) periodStats.get("paidAmount")).doubleValue());

        Row row5 = sheet.createRow(rowNum++);
        row5.createCell(0).setCellValue("滞纳金总额");
        row5.createCell(1).setCellValue(((BigDecimal) periodStats.get("totalLateFee")).doubleValue());

        // 自动调整列宽
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
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

        for (Map<String, Object> stat : buildingStats) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((String) stat.get("buildingNo"));
            row.createCell(1).setCellValue(((Integer) stat.get("totalRecords")).doubleValue());
            row.createCell(2).setCellValue(((Integer) stat.get("paidRecords")).doubleValue());
            row.createCell(3).setCellValue(((BigDecimal) stat.get("totalAmount")).doubleValue());
            row.createCell(4).setCellValue(((BigDecimal) stat.get("paidAmount")).doubleValue());
            row.createCell(5).setCellValue(((BigDecimal) stat.get("unpaidAmount")).doubleValue());
            row.createCell(6).setCellValue(((BigDecimal) stat.get("totalLateFee")).doubleValue());
            row.createCell(7).setCellValue(((BigDecimal) stat.get("paymentRate")).doubleValue());
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
