package com.property.servlet;

import com.property.dao.HouseDao;
import com.property.entity.House;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 房屋数据导出 Servlet
 */
@WebServlet("/export/house")  // ✅ 改成 /export/house
public class HouseExportServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(HouseExportServlet.class);
    private final HouseDao houseDao = new HouseDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String method = request.getParameter("method");

        logger.info("=== 房屋导出请求 ===");
        logger.info("method: {}", method);

        if ("export".equals(method)) {
            exportHouses(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "未知的方法: " + method);
        }
    }

    /**
     * 导出房屋数据
     */
    private void exportHouses(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // 1. 获取参数
            String exportType = request.getParameter("exportType");
            String keyword = request.getParameter("keyword");
            String status = request.getParameter("status");
            String selectedIds = request.getParameter("selectedIds");

            logger.info("导出类型: {}", exportType);
            logger.info("关键词: {}", keyword);
            logger.info("状态: {}", status);
            logger.info("选中的ID: {}", selectedIds);

            // 2. 查询数据
            List<House> houses;
            if ("selected".equals(exportType) && selectedIds != null && !selectedIds.trim().isEmpty()) {
                // 导出选中的数据
                List<String> ids = Arrays.stream(selectedIds.split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());
                houses = houseDao.findByIds(ids);
                logger.info("导出选中数据，共 {} 条", houses.size());
            } else {
                // 导出所有数据（根据筛选条件）
                houses = houseDao.findByCondition(keyword, status);
                logger.info("导出所有数据，共 {} 条", houses.size());
            }

            // 3. 生成 Excel
            Workbook workbook = createExcelWorkbook(houses);

            // 4. 设置响应头
            String fileName = "房屋信息_" + System.currentTimeMillis() + ".xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment; filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8"));

            // 5. 输出文件
            try (OutputStream out = response.getOutputStream()) {
                workbook.write(out);
                out.flush();
            }

            workbook.close();
            logger.info("导出成功: {}", fileName);

        } catch (Exception e) {
            logger.error("导出失败", e);
            throw new ServletException("导出失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建 Excel 工作簿
     */
    private Workbook createExcelWorkbook(List<House> houses) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("房屋信息");

        // 创建标题行
        Row headerRow = sheet.createRow(0);
        String[] headers = {"房屋编号", "楼栋号", "单元号", "楼层", "户型", "面积(㎡)",
                "单价(元/㎡)", "房屋状态", "销售状态", "业主姓名", "业主电话"};

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 4000);
        }

        // 填充数据
        int rowNum = 1;
        for (House house : houses) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(house.getHouseId());
            row.createCell(1).setCellValue(house.getBuildingNo());
            row.createCell(2).setCellValue(house.getUnitNo());
            row.createCell(3).setCellValue(house.getFloor());
            row.createCell(4).setCellValue(house.getLayout());
            row.createCell(5).setCellValue(house.getArea() != null ? house.getArea().doubleValue() : 0);
            row.createCell(6).setCellValue(house.getPricePerSqm() != null ? house.getPricePerSqm().doubleValue() : 0);
            row.createCell(7).setCellValue(getStatusText(house.getHouseStatus()));
            row.createCell(8).setCellValue(getSaleStatusText(house.getSaleStatus()));
            row.createCell(9).setCellValue(house.getOwnerName() != null ? house.getOwnerName() : "");
            row.createCell(10).setCellValue(house.getOwnerPhone() != null ? house.getOwnerPhone() : "");
        }

        return workbook;
    }

    /**
     * 获取房屋状态文本
     */
    private String getStatusText(String status) {
        if (status == null) return "";
        switch (status) {
            case "vacant": return "空置";
            case "occupied": return "已入住";
            case "rented": return "出租中";
            default: return status;
        }
    }

    /**
     * 获取销售状态文本
     */
    private String getSaleStatusText(String status) {
        if (status == null) return "";
        switch (status) {
            case "for_sale": return "待售";
            case "sold": return "已售";
            case "reserved": return "预定";
            default: return status;
        }
    }
}
