package com.property.servlet;

import com.property.entity.PaymentRecord;
import com.property.entity.User;
import com.property.service.PaymentService;
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
 * 缴费管理Servlet
 *
 * @author PropertyManagementSystem
 * @version 2.2 - 修复分页总数统计不一致问题
 */
@WebServlet("/payment")
public class PaymentServlet extends BaseServlet {
    private PaymentService paymentService = new PaymentService();

    /**
     * ✅ 根据ID查询缴费记录（关联收费项目信息）
     */
    public void findById(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
            return;
        }

        String recordId = getStringParameter(req, "recordId");
        if (recordId == null || recordId.trim().isEmpty()) {
            writeError(resp, "记录ID不能为空");
            return;
        }

        try {
            PaymentRecord record = paymentService.findById(recordId);
            if (record != null) {
                // ✅ 关键修复：获取收费项目的详细信息
                Map<String, Object> detailInfo = paymentService.getPaymentDetailWithChargeItem(recordId);

                if (detailInfo != null) {
                    writeSuccess(resp, "查询成功", detailInfo);
                } else {
                    writeSuccess(resp, "查询成功", record);
                }
            } else {
                writeError(resp, "记录不存在");
            }
        } catch (Exception e) {
            logger.error("查询缴费记录失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询缴费记录列表
     * ✅ 已修复：当 total 为 0 但实际有数据时，从统计接口获取正确总数，解决分页消失问题
     * ✅ 新增：支持按收费项目 itemId 筛选
     */
    public void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
            return;
        }

        int pageNum = getIntParameter(req, "pageNum", 1);
        int pageSize = getIntParameter(req, "pageSize", 10);
        String keyword = getStringParameter(req, "keyword");
        String status = getStringParameter(req, "status");
        String itemId = getStringParameter(req, "itemId");  // ✅ 新增：收费项目ID

        logger.info("📥 收到查询请求: pageNum={}, pageSize={}, keyword={}, status={}, itemId={}",
                pageNum, pageSize, keyword, status, itemId);

        try {
            Map<String, Object> result;
            Map<String, Object> stats;

            // ✅ 根据是否有 itemId 参数，调用不同的重载方法
            if (itemId != null && !itemId.trim().isEmpty()) {
                // 调用支持 itemId 的重载方法
                result = paymentService.findByPage(pageNum, pageSize, keyword, status, itemId);
                stats = paymentService.getStatistics(keyword, null, itemId);  // 传入 null 获取全局统计
                logger.info("✅ 使用按项目筛选查询: itemId={}", itemId);
            } else {
                // 调用原有方法（保持兼容）
                result = paymentService.findByPage(pageNum, pageSize, keyword, status);
                stats = paymentService.getStatistics(keyword, null);  // 传入 null 获取全局统计
                logger.info("✅ 使用常规查询（无项目筛选）");
            }

            // ========== 🔥 核心修正逻辑：修正 total 值 ==========
            if (stats != null) {
                long realTotal = 0;
                long currentTotal = Long.parseLong(String.valueOf(result.getOrDefault("total", 0)));

                // 根据当前筛选的状态，从统计数据中取正确的值
                if ("overdue".equals(status)) {
                    realTotal = Long.parseLong(String.valueOf(stats.getOrDefault("overdueCount", 0)));
                } else if ("unpaid".equals(status)) {
                    realTotal = Long.parseLong(String.valueOf(stats.getOrDefault("unpaidCount", 0)));
                } else if ("paid".equals(status)) {
                    realTotal = Long.parseLong(String.valueOf(stats.getOrDefault("paidCount", 0)));
                } else {
                    // 没有状态筛选，取总数
                    Object totalRecords = stats.get("totalRecords");
                    if (totalRecords == null) {
                        totalRecords = stats.get("totalCount");  // 兼容不同字段名
                    }
                    realTotal = Long.parseLong(String.valueOf(totalRecords != null ? totalRecords : 0));
                }

                // 🔥 核心修复逻辑：如果列表返回 total=0，但统计显示有数据，则强制覆盖
                if (currentTotal == 0 && realTotal > 0) {
                    result.put("total", realTotal);
                    // 重新计算总页数
                    int totalPages = (int) Math.ceil((double) realTotal / pageSize);
                    result.put("totalPages", totalPages);
                    result.put("pages", totalPages);  // 兼容不同字段名
                    logger.info("✅ 已修正分页数据: status={}, itemId={}, 原total={}, 修正后total={}",
                            status, itemId, currentTotal, realTotal);
                }
            }

            // 添加统计数据到结果
            result.put("statistics", stats);

            logger.info("✅ 查询成功: total={}, pages={}", result.get("total"), result.get("pages"));
            writeJson(resp, result);

        } catch (Exception e) {
            logger.error("❌ 查询缴费记录列表失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }


    /**
     * 查询业主的缴费记录
     */
    public void findByOwner(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
            return;
        }

        User currentUser = getCurrentUser(req);
        String ownerId = getStringParameter(req, "ownerId");

        // 如果是业主角色，只能查询自己的记录
        if ("owner".equals(currentUser.getUserRole())) {
            ownerId = currentUser.getUsername();
        }

        if (ownerId == null || ownerId.isEmpty()) {
            writeError(resp, "业主ID不能为空");
            return;
        }

        try {
            List<PaymentRecord> records = paymentService.findByOwnerId(ownerId);
            writeSuccess(resp, "查询成功", records);
        } catch (Exception e) {
            logger.error("查询缴费记录失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询业主未缴费记录
     */
    public void findUnpaid(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
            return;
        }

        User currentUser = getCurrentUser(req);
        String ownerId = getStringParameter(req, "ownerId");

        if ("owner".equals(currentUser.getUserRole())) {
            ownerId = currentUser.getUsername();
        }

        if (ownerId == null || ownerId.isEmpty()) {
            writeError(resp, "业主ID不能为空");
            return;
        }

        try {
            List<PaymentRecord> records = paymentService.findUnpaidByOwnerId(ownerId);
            writeSuccess(resp, "查询成功", records);
        } catch (Exception e) {
            logger.error("查询未缴费记录失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询业主已缴费记录
     */
    public void findPaid(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
            return;
        }

        User currentUser = getCurrentUser(req);
        String ownerId = getStringParameter(req, "ownerId");

        if ("owner".equals(currentUser.getUserRole())) {
            ownerId = currentUser.getUsername();
        }

        if (ownerId == null || ownerId.isEmpty()) {
            writeError(resp, "业主ID不能为空");
            return;
        }

        try {
            List<PaymentRecord> records = paymentService.findPaidByOwnerId(ownerId);
            writeSuccess(resp, "查询成功", records);
        } catch (Exception e) {
            logger.error("查询已缴费记录失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询逾期记录
     */
    public void findOverdue(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        try {
            List<PaymentRecord> records = paymentService.findOverdueRecords();
            writeSuccess(resp, "查询成功", records);
        } catch (Exception e) {
            logger.error("查询逾期记录失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 添加缴费记录
     */
    public void add(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        String ownerId = getStringParameter(req, "ownerId");
        String houseId = getStringParameter(req, "houseId");
        String itemId = getStringParameter(req, "itemId");
        String billingPeriod = getStringParameter(req, "billingPeriod");
        String amountStr = getStringParameter(req, "amount");
        String dueDateStr = getStringParameter(req, "dueDate");
        String remark = getStringParameter(req, "remark");

        // 参数验证
        if (ownerId == null || ownerId.trim().isEmpty()) {
            writeError(resp, "业主ID不能为空");
            return;
        }
        if (houseId == null || houseId.trim().isEmpty()) {
            writeError(resp, "房屋编号不能为空");
            return;
        }
        if (itemId == null || itemId.trim().isEmpty()) {
            writeError(resp, "收费项目不能为空");
            return;
        }
        if (billingPeriod == null || billingPeriod.trim().isEmpty()) {
            writeError(resp, "账期不能为空");
            return;
        }

        PaymentRecord record = new PaymentRecord();
        record.setOwnerId(ownerId);
        record.setHouseId(houseId);
        record.setItemId(itemId);
        record.setBillingPeriod(billingPeriod);
        record.setRemark(remark);

        // 解析金额
        if (amountStr != null && !amountStr.isEmpty()) {
            try {
                record.setAmount(new BigDecimal(amountStr));
            } catch (NumberFormatException e) {
                writeError(resp, "金额格式不正确");
                return;
            }
        } else {
            writeError(resp, "金额不能为空");
            return;
        }

        // 解析日期
        if (dueDateStr != null && !dueDateStr.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                record.setDueDate(sdf.parse(dueDateStr));
            } catch (Exception e) {
                writeError(resp, "日期格式不正确,请使用 yyyy-MM-dd 格式");
                return;
            }
        } else {
            writeError(resp, "截止日期不能为空");
            return;
        }

        try {
            boolean success = paymentService.addPaymentRecord(record);
            if (success) {
                writeSuccess(resp, "添加缴费记录成功", record.getRecordId());
            } else {
                writeError(resp, "添加缴费记录失败");
            }
        } catch (IllegalArgumentException e) {
            writeError(resp, e.getMessage());
        } catch (Exception e) {
            logger.error("添加缴费记录失败", e);
            writeError(resp, "添加缴费记录失败：" + e.getMessage());
        }
    }

    /**
     * 计算滞纳金（缴费前预览）
     */
    public void calculateLateFee(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
            return;
        }

        String recordId = getStringParameter(req, "recordId");
        if (recordId == null || recordId.trim().isEmpty()) {
            writeError(resp, "记录ID不能为空");
            return;
        }

        logger.info("========================================");
        logger.info("【计算滞纳金】请求");
        logger.info("记录ID: {}", recordId);
        logger.info("========================================");

        try {
            Map<String, Object> result = paymentService.calculateLateFee(recordId);

            Boolean success = (Boolean) result.get("success");
            String message = (String) result.get("message");

            if (success != null && success) {
                writeSuccess(resp, message, result);
            } else {
                writeError(resp, message);
            }

        } catch (Exception e) {
            logger.error("❌ 计算滞纳金失败", e);
            writeError(resp, "计算失败：" + e.getMessage());
        }
    }

    /**
     * 处理缴费（调用存储过程,自动计算滞纳金）
     */
    public void pay(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
            return;
        }

        String recordId = getStringParameter(req, "recordId");
        String paymentMethod = getStringParameter(req, "paymentMethod");
        User currentUser = getCurrentUser(req);

        if (recordId == null || recordId.trim().isEmpty()) {
            writeError(resp, "记录ID不能为空");
            return;
        }
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            writeError(resp, "缴费方式不能为空");
            return;
        }

        logger.info("========================================");
        logger.info("【处理缴费】开始");
        logger.info("记录ID: {}", recordId);
        logger.info("缴费方式: {}", paymentMethod);
        logger.info("操作员: {} (ID: {})", currentUser.getUsername(), currentUser.getUserId());
        logger.info("========================================");

        try {
            Map<String, Object> result = paymentService.processPayment(
                    recordId,
                    paymentMethod,
                    currentUser.getUserId()
            );

            Boolean success = (Boolean) result.get("success");
            String message = (String) result.get("message");

            logger.info("缴费结果: {}", message);
            logger.info("========================================");

            if (success != null && success) {
                writeSuccess(resp, message, result);
            } else {
                writeError(resp, message);
            }

        } catch (IllegalArgumentException e) {
            logger.warn("参数错误: {}", e.getMessage());
            logger.info("========================================");
            writeError(resp, e.getMessage());

        } catch (Exception e) {
            logger.error("处理缴费失败", e);
            logger.info("========================================");
            writeError(resp, "处理缴费失败：" + e.getMessage());
        }
    }

    /**
     * ✅ 生成账单（已修复：增加 buildingId 和 houseIds 参数处理）
     */
    public void generateBill(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("========================================");
        logger.info("【生成账单】开始");
        logger.info("========================================");

        if (!checkRole(req, resp, "admin", "finance")) {
            logger.warn("❌ 权限检查失败");
            return;
        }

        String itemId = getStringParameter(req, "itemId");
        String billingPeriod = getStringParameter(req, "billingPeriod");
        String dueDateStr = getStringParameter(req, "dueDate");

        // ✅ 新增：获取楼栋ID和房屋ID列表
        String buildingId = getStringParameter(req, "buildingId");
        String houseIds = getStringParameter(req, "houseIds");

        // ✅ 关键修复：将空字符串转换为 null
        // 前端传 "scope=all" 时 buildingId 为 ""，如果不转 null，Service层可能会查不到数据
        if (buildingId != null && buildingId.trim().isEmpty()) {
            buildingId = null;
        }
        if (houseIds != null && houseIds.trim().isEmpty()) {
            houseIds = null;
        }

        logger.info("接收到的参数：");
        logger.info("  收费项目ID: {}", itemId);
        logger.info("  账期: {}", billingPeriod);
        logger.info("  截止日期: {}", dueDateStr);
        logger.info("  楼栋ID: {}", buildingId);
        logger.info("  自定义房屋: {}", houseIds);

        // 参数验证
        if (itemId == null || itemId.trim().isEmpty()) {
            writeError(resp, "收费项目不能为空");
            return;
        }
        if (billingPeriod == null || billingPeriod.trim().isEmpty()) {
            writeError(resp, "账期不能为空");
            return;
        }
        if (dueDateStr == null || dueDateStr.trim().isEmpty()) {
            writeError(resp, "截止日期不能为空");
            return;
        }

        // 解析截止日期
        Date dueDate;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dueDate = sdf.parse(dueDateStr);
            logger.info("✅ 截止日期解析成功: {}", sdf.format(dueDate));
        } catch (Exception e) {
            logger.error("❌ 日期格式错误: {}", dueDateStr);
            writeError(resp, "日期格式不正确，请使用 yyyy-MM-dd 格式");
            return;
        }

        try {
            logger.info("\n开始生成账单...");

            // ✅ 修复：调用包含 buildingId 和 houseIds 的 Service 方法
            Map<String, Object> result = paymentService.generateBillByChargeItem(
                    itemId,
                    billingPeriod,
                    dueDate,
                    buildingId, // 传入楼栋ID
                    houseIds    // 传入房屋ID列表
            );

            Boolean success = (Boolean) result.get("success");
            String message = (String) result.get("message");
            Integer totalCount = (Integer) result.get("totalCount");
            Integer successCount = (Integer) result.get("successCount");
            Integer failCount = (Integer) result.get("failCount");

            logger.info("\n生成结果：");
            logger.info("  总数: {}", totalCount);
            logger.info("  成功: {}", successCount);
            logger.info("  失败: {}", failCount);

            if (success != null && success) {
                logger.info("✅ 生成账单成功");
                logger.info("========================================");
                writeSuccess(resp, message, result);
            } else {
                logger.warn("❌ 生成账单失败");
                logger.info("========================================");
                writeError(resp, message);
            }

        } catch (IllegalArgumentException e) {
            logger.error("❌ 参数错误: {}", e.getMessage());
            logger.info("========================================");
            writeError(resp, e.getMessage());

        } catch (Exception e) {
            logger.error("❌ 系统错误", e);
            logger.info("========================================");
            writeError(resp, "生成账单失败：" + e.getMessage());
        }
    }

    /**
     * 统计业主欠费总额
     */
    public void sumUnpaid(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
            return;
        }

        User currentUser = getCurrentUser(req);
        String ownerId = getStringParameter(req, "ownerId");

        if ("owner".equals(currentUser.getUserRole())) {
            ownerId = currentUser.getUsername();
        }

        if (ownerId == null || ownerId.isEmpty()) {
            writeError(resp, "业主ID不能为空");
            return;
        }

        try {
            BigDecimal amount = paymentService.sumUnpaidAmount(ownerId);
            writeSuccess(resp, "查询成功", amount);
        } catch (Exception e) {
            logger.error("统计欠费失败", e);
            writeError(resp, "统计失败：" + e.getMessage());
        }
    }

    /**
     * ✅ 统计分析（支持按收费项目筛选）
     */
    public void statistics(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!checkLogin(req, resp)) {
            return;
        }

        try {
            String keyword = req.getParameter("keyword");
            String status = req.getParameter("status");
            String itemId = req.getParameter("itemId");  // ✅ 新增

            logger.info("=== 统计分析请求 ===");
            logger.info("关键词: {}", keyword);
            logger.info("状态: {}", status);
            logger.info("项目ID: {}", itemId);  // ✅ 新增日志

            // ✅ 调用支持 itemId 的统计方法
            Map<String, Object> statisticsData;
            if (itemId != null && !itemId.trim().isEmpty()) {
                statisticsData = paymentService.getStatistics(keyword, status, itemId);
                logger.info("📊 使用按项目筛选统计: itemId={}", itemId);
            } else {
                statisticsData = paymentService.getStatistics(keyword, status);
                logger.info("📊 使用全局统计（无筛选条件）");
            }

            logger.info("统计数据: {}", statisticsData);

            writeSuccess(resp, "查询成功", statisticsData);

        } catch (Exception e) {
            logger.error("统计分析失败", e);
            writeError(resp, "统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取收入统计
     */
    public void getIncomeStatistics(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!checkLogin(req, resp)) {
            return;
        }

        try {
            String startDate = req.getParameter("startDate");
            String endDate = req.getParameter("endDate");

            logger.info("=== 获取收入统计 ===");
            logger.info("开始日期: {}", startDate);
            logger.info("结束日期: {}", endDate);

            Map<String, Object> result = paymentService.getIncomeStatistics(startDate, endDate);

            logger.info("收入统计结果: {}", result);

            writeSuccess(resp, "查询成功", result);

        } catch (Exception e) {
            logger.error("获取收入统计失败", e);
            writeError(resp, "获取收入统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取月度统计
     */
    public void getMonthlyStatistics(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!checkLogin(req, resp)) {
            return;
        }

        try {
            logger.info("=== 获取月度统计 ===");

            List<Map<String, Object>> result = paymentService.getMonthlyStatistics();

            logger.info("月度统计结果: {}", result);

            writeSuccess(resp, "查询成功", result);

        } catch (Exception e) {
            logger.error("获取月度统计失败", e);
            writeError(resp, "获取月度统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取费用类型统计
     */
    public void getFeeTypeStatistics(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!checkLogin(req, resp)) {
            return;
        }

        try {
            String keyword = req.getParameter("keyword");
            String status = req.getParameter("status");

            logger.info("=== 获取费用类型统计 ===");
            logger.info("关键词: {}", keyword);
            logger.info("状态: {}", status);

            List<Map<String, Object>> result = paymentService.getFeeTypeStatistics(keyword, status);

            logger.info("费用类型统计结果: {}", result);

            writeSuccess(resp, "查询成功", result);

        } catch (Exception e) {
            logger.error("获取费用类型统计失败", e);
            writeError(resp, "获取费用类型统计失败: " + e.getMessage());
        }
    }

    /**
     * 删除缴费记录
     */
    public void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        String recordId = getStringParameter(req, "recordId");
        if (recordId == null || recordId.trim().isEmpty()) {
            writeError(resp, "记录ID不能为空");
            return;
        }

        try {
            boolean success = paymentService.deletePaymentRecord(recordId);
            if (success) {
                writeSuccess(resp, "删除成功", null);
            } else {
                writeError(resp, "删除失败,记录不存在");
            }
        } catch (Exception e) {
            logger.error("删除缴费记录失败", e);
            writeError(resp, "删除失败：" + e.getMessage());
        }
    }

    /**
     * 更新缴费记录
     */
    public void update(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        String recordId = getStringParameter(req, "recordId");
        if (recordId == null || recordId.trim().isEmpty()) {
            writeError(resp, "记录ID不能为空");
            return;
        }

        String amountStr = getStringParameter(req, "amount");
        String lateFeeStr = getStringParameter(req, "lateFee");
        String dueDateStr = getStringParameter(req, "dueDate");
        String status = getStringParameter(req, "status");
        String remark = getStringParameter(req, "remark");

        PaymentRecord record = new PaymentRecord();
        record.setRecordId(recordId);
        record.setPaymentStatus(status);
        record.setRemark(remark);

        // 解析金额
        if (amountStr != null && !amountStr.isEmpty()) {
            try {
                record.setAmount(new BigDecimal(amountStr));
            } catch (NumberFormatException e) {
                writeError(resp, "金额格式不正确");
                return;
            }
        }

        // 解析滞纳金
        if (lateFeeStr != null && !lateFeeStr.isEmpty()) {
            try {
                record.setLateFee(new BigDecimal(lateFeeStr));
            } catch (NumberFormatException e) {
                writeError(resp, "滞纳金格式不正确");
                return;
            }
        }

        // 解析日期
        if (dueDateStr != null && !dueDateStr.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                record.setDueDate(sdf.parse(dueDateStr));
            } catch (Exception e) {
                writeError(resp, "日期格式不正确");
                return;
            }
        }

        try {
            boolean success = paymentService.updatePaymentRecord(record);
            if (success) {
                writeSuccess(resp, "更新成功", null);
            } else {
                writeError(resp, "更新失败");
            }
        } catch (Exception e) {
            logger.error("更新缴费记录失败", e);
            writeError(resp, "更新失败：" + e.getMessage());
        }
    }
    /**
     * ✅ 导出缴费记录到Excel（支持导出选中记录）
     */
    public void export(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!checkLogin(req, resp)) {
            return;
        }

        OutputStream outputStream = null;

        try {
            // 获取查询参数
            String keyword = getStringParameter(req, "keyword");
            String status = getStringParameter(req, "status");
            String itemId = getStringParameter(req, "itemId");

            // ✅ 新增：获取选中的记录ID列表
            String recordIds = getStringParameter(req, "recordIds");

            logger.info("📥 开始导出缴费记录");
            logger.info("关键字: {}, 状态: {}, 项目ID: {}, 选中记录: {}",
                    keyword, status, itemId, recordIds);

            List<PaymentRecord> records;

            // ✅ 判断是导出选中记录还是导出筛选结果
            if (recordIds != null && !recordIds.trim().isEmpty()) {
                // 导出选中的记录
                logger.info("📋 导出模式：选中记录");
                records = new ArrayList<>();
                String[] ids = recordIds.split(",");

                for (String id : ids) {
                    String trimmedId = id.trim();
                    if (!trimmedId.isEmpty()) {
                        PaymentRecord record = paymentService.findById(trimmedId);
                        if (record != null) {
                            records.add(record);
                        }
                    }
                }

                logger.info("✅ 查询到 {} 条选中记录", records.size());

            } else {
                // 导出筛选结果
                logger.info("📋 导出模式：筛选结果");

                if (itemId != null && !itemId.trim().isEmpty()) {
                    records = paymentService.findAll(keyword, status, itemId);
                } else {
                    records = paymentService.findAll(keyword, status);
                }

                logger.info("✅ 查询到 {} 条筛选记录", records.size());
            }

            // 检查数据是否为空
            if (records == null || records.isEmpty()) {
                logger.warn("⚠️ 没有找到符合条件的记录");
                resp.setContentType("text/html;charset=UTF-8");
                resp.getWriter().write(
                        "<script>alert('没有可导出的数据');history.back();</script>"
                );
                return;
            }

            // 限制导出数量
            final int MAX_EXPORT_SIZE = 50000;
            if (records.size() > MAX_EXPORT_SIZE) {
                resp.setContentType("text/html;charset=UTF-8");
                resp.getWriter().write(
                        "<script>alert('导出数据过多（超过" + MAX_EXPORT_SIZE +
                                "条），请缩小查询范围');history.back();</script>"
                );
                return;
            }

            // 生成文件名
            String fileName;
            if (recordIds != null && !recordIds.trim().isEmpty()) {
                fileName = "选中缴费记录_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx";
            } else {
                fileName = generateExportFileName(status);
            }

            // 设置响应头
            setExportResponseHeaders(resp, fileName);

            // 导出Excel
            outputStream = resp.getOutputStream();
            ExcelExportUtil.exportPaymentRecordList(records, outputStream);

            outputStream.flush();

            logger.info("✅ 导出成功：{} ({} 条记录)", fileName, records.size());

        } catch (Exception e) {
            logger.error("❌ 导出失败", e);
            handleExportError(resp, outputStream, e);
        }
    }


    /**
     * 生成导出文件名
     */
    private String generateExportFileName(String status) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());

        String prefix = "缴费记录";
        if ("paid".equals(status)) {
            prefix = "已缴费记录";
        } else if ("unpaid".equals(status)) {
            prefix = "未缴费记录";
        } else if ("overdue".equals(status)) {
            prefix = "逾期记录";
        }

        return prefix + "_" + timestamp + ".xlsx";
    }

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
            logger.error("❌ 响应已提交，无法发送错误信息");
            return;
        }

        response.reset();
        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        String errorMsg = "导出失败：" + e.getMessage();
        response.getWriter().write(
                "<script>alert('" + errorMsg.replace("'", "\\'") +
                        "');history.back();</script>"
        );
    }
    /**
     * ✅ 批量删除未缴费记录
     */
    public void batchDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        String recordIds = getStringParameter(req, "recordIds");
        if (recordIds == null || recordIds.trim().isEmpty()) {
            writeError(resp, "请选择要删除的记录");
            return;
        }

        logger.info("========================================");
        logger.info("【批量删除】开始");
        logger.info("记录ID列表: {}", recordIds);
        logger.info("========================================");

        try {
            String[] ids = recordIds.split(",");
            int successCount = 0;
            int failCount = 0;
            List<String> errorMessages = new ArrayList<>();

            for (String id : ids) {
                String trimmedId = id.trim();
                if (trimmedId.isEmpty()) {
                    continue;
                }

                try {
                    // 检查记录是否存在
                    PaymentRecord record = paymentService.findById(trimmedId);
                    if (record == null) {
                        failCount++;
                        errorMessages.add(trimmedId + ": 记录不存在");
                        logger.warn("记录不存在: {}", trimmedId);
                        continue;
                    }

                    // 检查是否已缴费
                    if ("paid".equals(record.getPaymentStatus())) {
                        failCount++;
                        errorMessages.add(trimmedId + ": 已缴费记录不能删除");
                        logger.warn("已缴费记录不能删除: {}", trimmedId);
                        continue;
                    }

                    // 执行删除
                    if (paymentService.deletePaymentRecord(trimmedId)) {
                        successCount++;
                        logger.info("✅ 删除成功: {}", trimmedId);
                    } else {
                        failCount++;
                        errorMessages.add(trimmedId + ": 删除失败");
                        logger.error("❌ 删除失败: {}", trimmedId);
                    }

                } catch (Exception e) {
                    failCount++;
                    String errorMsg = trimmedId + ": " + e.getMessage();
                    errorMessages.add(errorMsg);
                    logger.error("❌ 删除异常: {}", trimmedId, e);
                }
            }

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("errorMessages", errorMessages);

            logger.info("========================================");
            logger.info("【批量删除】完成");
            logger.info("成功: {}, 失败: {}", successCount, failCount);
            logger.info("========================================");

            if (successCount > 0) {
                String message = String.format("删除完成！成功: %d 条, 失败: %d 条",
                        successCount, failCount);
                writeSuccess(resp, message, result);
            } else {
                writeError(resp, "删除失败，请检查选中的记录");
            }

        } catch (Exception e) {
            logger.error("❌ 批量删除失败", e);
            writeError(resp, "批量删除失败：" + e.getMessage());
        }
    }

}
