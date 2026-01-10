package com.property.util;

import com.property.entity.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Excel 导出工具类
 * ✅ 已修复缴费记录导出的并发问题
 */
public class ExcelExportUtil {

    /**
     * 导出房屋列表到 Excel
     */
    public static byte[] exportHouseList(List<House> houseList) throws IOException {
        // 创建工作簿
        Workbook workbook = new XSSFWorkbook();

        try {
            Sheet sheet = workbook.createSheet("房屋信息");

            // 创建样式
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            // 创建表头
            String[] headers = {
                    "房屋编号", "楼栋号", "单元号", "楼层", "户型",
                    "建筑面积(㎡)", "物业费单价(元/㎡)", "房屋状态", "销售状态",
                    "业主编号", "业主姓名", "业主电话"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 填充数据
            int rowNum = 1;
            for (House house : houseList) {
                Row row = sheet.createRow(rowNum++);

                // 房屋编号
                createCell(row, 0, house.getHouseId(), dataStyle);
                // 楼栋号
                createCell(row, 1, house.getBuildingNo(), dataStyle);
                // 单元号
                createCell(row, 2, house.getUnitNo(), dataStyle);
                // 楼层
                createCell(row, 3, house.getFloor(), dataStyle);
                // 户型
                createCell(row, 4, house.getLayout(), dataStyle);
                // 建筑面积
                createCell(row, 5, formatBigDecimal(house.getArea()), dataStyle);
                // 物业费单价
                createCell(row, 6, formatBigDecimal(house.getPricePerSqm()), dataStyle);
                // 房屋状态
                createCell(row, 7, formatHouseStatus(house.getHouseStatus()), dataStyle);
                // 销售状态
                createCell(row, 8, formatSaleStatus(house.getSaleStatus()), dataStyle);
                // 业主编号
                createCell(row, 9, house.getOwnerId(), dataStyle);
                // 业主姓名
                createCell(row, 10, house.getOwnerName(), dataStyle);
                // 业主电话
                createCell(row, 11, house.getOwnerPhone(), dataStyle);
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // 额外增加一些宽度
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
            }

            // 转换为字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            return outputStream.toByteArray();

        } finally {
            workbook.close();
        }
    }

    /**
     * 导出业主列表到 Excel（直接写入 OutputStream）
     */
    public static void exportOwnerList(List<Owner> ownerList, OutputStream outputStream) throws IOException {
        // ✅ 每次创建新的 Workbook 实例，避免并发问题
        Workbook workbook = new XSSFWorkbook();

        try {
            Sheet sheet = workbook.createSheet("业主信息");

            // 创建样式
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            // 创建表头
            String[] headers = {
                    "业主编号", "业主姓名", "联系电话", "身份证号",
                    "房屋编号", "电子邮箱", "家庭人数", "登记日期", "备注"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 填充数据
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            int rowNum = 1;
            for (Owner owner : ownerList) {
                Row row = sheet.createRow(rowNum++);

                // 业主编号
                createCell(row, 0, owner.getOwnerId(), dataStyle);
                // 业主姓名
                createCell(row, 1, owner.getOwnerName(), dataStyle);
                // 联系电话
                createCell(row, 2, owner.getPhone(), dataStyle);
                // 身份证号
                createCell(row, 3, owner.getIdCard(), dataStyle);
                // 房屋编号
                createCell(row, 4, owner.getHouseId(), dataStyle);
                // 电子邮箱
                createCell(row, 5, owner.getEmail(), dataStyle);
                // 家庭人数
                createCell(row, 6, owner.getMemberCount() != null ? owner.getMemberCount().toString() : "", dataStyle);
                // 登记日期
                createCell(row, 7, owner.getRegisterDate() != null ? dateFormat.format(owner.getRegisterDate()) : "", dataStyle);
                // 备注
                createCell(row, 8, owner.getRemark(), dataStyle);
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // 额外增加一些宽度
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
            }

            // ✅ 写入输出流（不关闭 outputStream，由调用方管理）
            workbook.write(outputStream);
            outputStream.flush();

        } finally {
            // ✅ 只关闭 workbook，不关闭 outputStream
            workbook.close();
        }
    }

    /**
     * 导出业主列表到 Excel（返回字节数组）
     */
    public static byte[] exportOwnerListToBytes(List<Owner> ownerList) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            exportOwnerList(ownerList, outputStream);
            return outputStream.toByteArray();
        } finally {
            outputStream.close();
        }
    }

    /**
     * ✅ 修复：导出缴费记录到 Excel（直接写入 OutputStream）
     * 核心修复：
     * 1. 每次创建新的 Workbook 实例（避免并发冲突）
     * 2. 正确管理资源关闭顺序
     * 3. 不关闭外部传入的 OutputStream
     */
    public static void exportPaymentRecordList(List<PaymentRecord> recordList, OutputStream outputStream) throws IOException {
        // ✅ 关键修复1：每次创建新的 Workbook 实例，避免并发问题
        Workbook workbook = new XSSFWorkbook();

        try {
            Sheet sheet = workbook.createSheet("缴费记录");

            // 创建样式
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);

            // 创建表头
            String[] headers = {
                    "记录ID", "业主ID", "业主姓名", "房屋编号", "楼栋", "单元", "楼层",
                    "收费项目", "账期", "应缴金额", "滞纳金", "总金额",
                    "到期日期", "缴费日期", "缴费方式", "缴费状态", "收据号"
            };

            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(25); // 设置表头行高

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 填充数据
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            int rowNum = 1;

            for (PaymentRecord record : recordList) {
                Row row = sheet.createRow(rowNum++);
                row.setHeightInPoints(20); // 设置数据行高

                int colNum = 0;

                // 记录ID
                createCell(row, colNum++, record.getRecordId() != null ? record.getRecordId().toString() : "", dataStyle);
                // 业主ID
                createCell(row, colNum++, record.getOwnerId(), dataStyle);
                // 业主姓名
                createCell(row, colNum++, record.getOwnerName(), dataStyle);
                // 房屋编号
                createCell(row, colNum++, record.getHouseId(), dataStyle);
                // 楼栋
                createCell(row, colNum++, record.getBuildingNo(), dataStyle);
                // 单元
                createCell(row, colNum++, record.getUnitNo(), dataStyle);
                // 楼层
                createCell(row, colNum++, record.getFloor() != null ? record.getFloor().toString() : "", dataStyle);
                // 收费项目
                createCell(row, colNum++, record.getItemName(), dataStyle);
                // 账期（使用 getBillingPeriod）
                createCell(row, colNum++, record.getBillingPeriod(), dataStyle);
                // 应缴金额
                createCell(row, colNum++, formatBigDecimal(record.getAmount()), numberStyle);
                // 滞纳金
                createCell(row, colNum++, formatBigDecimal(record.getLateFee()), numberStyle);
                // 总金额
                createCell(row, colNum++, formatBigDecimal(record.getTotalAmount()), numberStyle);
                // 到期日期
                createCell(row, colNum++, record.getDueDate() != null ? dateFormat.format(record.getDueDate()) : "", dataStyle);
                // 缴费日期
                createCell(row, colNum++, record.getPaymentDate() != null ? dateFormat.format(record.getPaymentDate()) : "", dataStyle);
                // 缴费方式
                createCell(row, colNum++, formatPaymentMethod(record.getPaymentMethod()), dataStyle);
                // 缴费状态
                createCell(row, colNum++, formatPaymentStatus(record.getPaymentStatus()), dataStyle);
                // 收据号
                createCell(row, colNum++, record.getReceiptNo(), dataStyle);
            }

            // 设置列宽
            sheet.setColumnWidth(0, 3000);  // 记录ID
            sheet.setColumnWidth(1, 3500);  // 业主ID
            sheet.setColumnWidth(2, 4000);  // 业主姓名
            sheet.setColumnWidth(3, 4000);  // 房屋编号
            sheet.setColumnWidth(4, 3000);  // 楼栋
            sheet.setColumnWidth(5, 3000);  // 单元
            sheet.setColumnWidth(6, 3000);  // 楼层
            sheet.setColumnWidth(7, 4000);  // 收费项目
            sheet.setColumnWidth(8, 4000);  // 账期
            sheet.setColumnWidth(9, 4000);  // 应缴金额
            sheet.setColumnWidth(10, 3500); // 滞纳金
            sheet.setColumnWidth(11, 4000); // 总金额
            sheet.setColumnWidth(12, 4000); // 到期日期
            sheet.setColumnWidth(13, 4000); // 缴费日期
            sheet.setColumnWidth(14, 3500); // 缴费方式
            sheet.setColumnWidth(15, 3500); // 缴费状态
            sheet.setColumnWidth(16, 5000); // 收据号

            // ✅ 关键修复2：写入输出流并刷新
            workbook.write(outputStream);
            outputStream.flush();

        } finally {
            // ✅ 关键修复3：只关闭 workbook，不关闭外部传入的 outputStream
            // outputStream 的关闭由调用方（Servlet）负责
            workbook.close();
        }
    }

    /**
     * 导出缴费记录到 Excel（返回字节数组）
     */
    public static byte[] exportPaymentRecordListToBytes(List<PaymentRecord> recordList) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            exportPaymentRecordList(recordList, outputStream);
            return outputStream.toByteArray();
        } finally {
            outputStream.close();
        }
    }

    /**
     * 创建表头样式
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        // 设置背景色
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 设置边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        // 设置对齐
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        // 设置字体
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);

        return style;
    }

    /**
     * 创建数据样式
     */
    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        // 设置边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        // 设置对齐
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

    /**
     * 创建数字样式（右对齐）
     */
    private static CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        // 设置边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        // 设置对齐（数字右对齐）
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

    /**
     * 创建单元格并设置值
     */
    private static void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }

    /**
     * 格式化 BigDecimal
     */
    private static String formatBigDecimal(BigDecimal value) {
        if (value == null) {
            return "0.00";
        }
        return String.format("%.2f", value);
    }

    /**
     * 格式化日期
     */
    private static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    /**
     * 格式化房屋状态
     */
    private static String formatHouseStatus(String status) {
        if (status == null) return "";
        switch (status) {
            case "vacant": return "空置";
            case "occupied": return "已入住";
            case "rented": return "已出租";
            default: return status;
        }
    }

    /**
     * 格式化销售状态
     */
    private static String formatSaleStatus(String status) {
        if (status == null) return "";
        switch (status) {
            case "for_sale": return "待售";
            case "sold": return "已售";
            case "reserved": return "已预订";
            default: return status;
        }
    }

    /**
     * 格式化缴费方式
     */
    private static String formatPaymentMethod(String method) {
        if (method == null) return "";
        switch (method) {
            case "cash": return "现金";
            case "wechat": return "微信";
            case "alipay": return "支付宝";
            case "bank_transfer": return "银行转账";
            case "online": return "在线支付";
            default: return method;
        }
    }

    /**
     * 格式化缴费状态
     */
    private static String formatPaymentStatus(String status) {
        if (status == null) return "";
        switch (status) {
            case "paid": return "已缴费";
            case "unpaid": return "未缴费";
            case "overdue": return "已逾期";
            case "partial": return "部分缴费";
            default: return status;
        }
    }
    /**
     * 导出欠费业主列表到Excel
     */
    public static void exportArrearsOwnerList(List<FinanceStatistics> list, OutputStream outputStream)
            throws IOException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("欠费业主列表");

        // 创建标题样式
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 12);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setBorderTop(BorderStyle.THIN);
        titleStyle.setBorderBottom(BorderStyle.THIN);
        titleStyle.setBorderLeft(BorderStyle.THIN);
        titleStyle.setBorderRight(BorderStyle.THIN);

        // 创建数据样式
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.LEFT);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        // 创建金额样式
        CellStyle moneyStyle = workbook.createCellStyle();
        moneyStyle.cloneStyleFrom(dataStyle);
        moneyStyle.setDataFormat(workbook.createDataFormat().getFormat("¥#,##0.00"));

        // 创建日期样式
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.cloneStyleFrom(dataStyle);
        dateStyle.setDataFormat(workbook.createDataFormat().getFormat("yyyy-MM-dd"));

        // 创建标题行
        Row titleRow = sheet.createRow(0);
        titleRow.setHeight((short) 500);

        String[] headers = {
                "业主ID", "业主姓名", "联系电话", "房屋编号",
                "楼栋", "单元", "楼层", "未缴笔数",
                "欠费金额", "滞纳金", "欠费总额",
                "最早欠费日期", "最大逾期天数"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = titleRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(titleStyle);
        }

        // 填充数据
        int rowIndex = 1;
        for (FinanceStatistics stats : list) {
            Row row = sheet.createRow(rowIndex++);

            // 业主ID
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(stats.getOwnerId());
            cell0.setCellStyle(dataStyle);

            // 业主姓名
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(stats.getOwnerName());
            cell1.setCellStyle(dataStyle);

            // 联系电话
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(stats.getOwnerPhone());
            cell2.setCellStyle(dataStyle);

            // 房屋编号
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(stats.getHouseId());
            cell3.setCellStyle(dataStyle);

            // 楼栋
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(stats.getBuildingNo() + "栋");
            cell4.setCellStyle(dataStyle);

            // 单元
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(stats.getUnitNo() + "单元");
            cell5.setCellStyle(dataStyle);

            // 楼层
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(stats.getFloor() + "层");
            cell6.setCellStyle(dataStyle);

            // 未缴笔数
            Cell cell7 = row.createCell(7);
            cell7.setCellValue(stats.getUnpaidCount());
            cell7.setCellStyle(dataStyle);

            // 欠费金额
            Cell cell8 = row.createCell(8);
            if (stats.getUnpaidAmount() != null) {
                cell8.setCellValue(stats.getUnpaidAmount().doubleValue());
            }
            cell8.setCellStyle(moneyStyle);

            // 滞纳金
            Cell cell9 = row.createCell(9);
            if (stats.getTotalLateFee() != null) {
                cell9.setCellValue(stats.getTotalLateFee().doubleValue());
            }
            cell9.setCellStyle(moneyStyle);

            // 欠费总额
            Cell cell10 = row.createCell(10);
            if (stats.getTotalArrears() != null) {
                cell10.setCellValue(stats.getTotalArrears().doubleValue());
            }
            cell10.setCellStyle(moneyStyle);

            // 最早欠费日期
            Cell cell11 = row.createCell(11);
            if (stats.getEarliestDueDate() != null) {
                cell11.setCellValue(stats.getEarliestDueDate());
                cell11.setCellStyle(dateStyle);
            }

            // 最大逾期天数
            Cell cell12 = row.createCell(12);
            if (stats.getMaxOverdueDays() != null) {
                cell12.setCellValue(stats.getMaxOverdueDays() + "天");
            }
            cell12.setCellStyle(dataStyle);
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
        }

        // 写入输出流
        workbook.write(outputStream);
        workbook.close();
    }
    /**
     * 导出操作日志列表到Excel
     *
     * @param logs 操作日志列表
     * @param outputStream 输出流
     * @throws IOException IO异常
     */
    public static void exportOperationLogList(List<OperationLog> logs, OutputStream outputStream)
            throws IOException {

        // 创建新的工作簿
        Workbook workbook = new XSSFWorkbook();

        try {
            Sheet sheet = workbook.createSheet("操作日志");

            // 创建样式
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);

            // 创建表头
            String[] headers = {
                    "日志ID", "用户ID", "用户名", "操作类型",
                    "操作描述", "IP地址", "操作时间"
            };

            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(25);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 填充数据
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            int rowNum = 1;

            for (OperationLog log : logs) {
                Row row = sheet.createRow(rowNum++);
                row.setHeightInPoints(20);

                int colNum = 0;

                // 日志ID
                createCell(row, colNum++,
                        log.getLogId() != null ? log.getLogId().toString() : "",
                        dataStyle);

                // 用户ID
                createCell(row, colNum++,
                        log.getUserId() != null ? log.getUserId().toString() : "",
                        dataStyle);

                // 用户名
                createCell(row, colNum++, log.getUsername(), dataStyle);

                // 操作类型（转换为中文）
                createCell(row, colNum++,
                        formatOperationType(log.getOperationType()),
                        dataStyle);

                // 操作描述
                createCell(row, colNum++, log.getOperationDesc(), dataStyle);

                // IP地址
                createCell(row, colNum++, log.getIpAddress(), dataStyle);

                // 操作时间
                createCell(row, colNum++,
                        log.getOperationTime() != null ?
                                dateFormat.format(log.getOperationTime()) : "",
                        dataStyle);
            }

            // 设置列宽
            sheet.setColumnWidth(0, 3000);  // 日志ID
            sheet.setColumnWidth(1, 3000);  // 用户ID
            sheet.setColumnWidth(2, 4000);  // 用户名
            sheet.setColumnWidth(3, 4000);  // 操作类型
            sheet.setColumnWidth(4, 8000);  // 操作描述
            sheet.setColumnWidth(5, 5000);  // IP地址
            sheet.setColumnWidth(6, 6000);  // 操作时间

            // 写入输出流
            workbook.write(outputStream);
            outputStream.flush();

        } finally {
            workbook.close();
        }
    }

    /**
     * 创建日期样式
     */
    private static CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        // 设置边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        // 设置对齐
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        // 设置日期格式
        style.setDataFormat(workbook.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));

        return style;
    }

    /**
     * 格式化操作类型（转换为中文）
     */
    private static String formatOperationType(String operationType) {
        if (operationType == null || operationType.trim().isEmpty()) {
            return "";
        }

        switch (operationType) {
            // 用户管理
            case "login":
                return "登录";
            case "logout":
                return "登出";
            case "register":
                return "注册";
            case "change_password":
                return "修改密码";

            // 业主管理
            case "owner_add":
                return "添加业主";
            case "owner_update":
                return "修改业主";
            case "owner_delete":
                return "删除业主";

            // 房屋管理
            case "house_add":
                return "添加房屋";
            case "house_update":
                return "修改房屋";
            case "house_delete":
                return "删除房屋";
            case "house_assign":
                return "分配业主";

            // 缴费管理
            case "payment_add":
                return "添加缴费记录";
            case "payment_update":
                return "修改缴费记录";
            case "payment_delete":
                return "删除缴费记录";
            case "payment_process":
                return "处理缴费";
            case "payment_generate":
                return "生成账单";
            case "payment_batch_delete":
                return "批量删除";

            // 报修管理
            case "repair_submit":
                return "提交报修";
            case "repair_accept":
                return "受理报修";
            case "repair_complete":
                return "完成报修";

            // 公告管理
            case "announcement_publish":
                return "发布公告";
            case "announcement_update":
                return "修改公告";
            case "announcement_delete":
                return "删除公告";

            // 投诉管理
            case "complaint_submit":
                return "提交投诉";
            case "complaint_handle":
                return "处理投诉";
            case "complaint_close":
                return "关闭投诉";

            // 收费项目管理
            case "charge_item_add":
                return "添加收费项目";
            case "charge_item_update":
                return "修改收费项目";
            case "charge_item_delete":
                return "删除收费项目";

            // 系统管理
            case "system_config":
                return "系统配置";
            case "data_export":
                return "数据导出";
            case "data_import":
                return "数据导入";

            default:
                return operationType;
        }
    }

}
