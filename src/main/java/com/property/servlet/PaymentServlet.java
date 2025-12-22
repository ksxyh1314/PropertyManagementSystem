package com.property.servlet;

import com.property.entity.PaymentRecord;
import com.property.entity.User;
import com.property.service.PaymentService;

import javax.servlet.ServletException;
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
 * 缴费管理Servlet
 */
@WebServlet("/payment")
public class PaymentServlet extends BaseServlet {
    private PaymentService paymentService = new PaymentService();

    /**
     * 分页查询缴费记录列表
     */
    public void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
            return;
        }

        int pageNum = getIntParameter(req, "pageNum", 1);
        int pageSize = getIntParameter(req, "pageSize", 10);
        String keyword = getStringParameter(req, "keyword");
        String status = getStringParameter(req, "status");

        try {
            Map<String, Object> result = paymentService.findByPage(pageNum, pageSize, keyword, status);
            writeJson(resp, result);
        } catch (Exception e) {
            logger.error("查询缴费记录列表失败", e);
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
            ownerId = currentUser.getUsername(); // 业主的用户名就是业主ID
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

        // 如果是业主角色，只能查询自己的记录
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

        // 如果是业主角色，只能查询自己的记录
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
            Integer recordId = paymentService.addPaymentRecord(record);
            if (recordId != null) {
                writeSuccess(resp, "添加缴费记录成功", recordId);
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
     * 处理缴费
     */
    public void pay(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
            return;
        }

        Integer recordId = getIntParameter(req, "recordId");
        String paymentMethod = getStringParameter(req, "paymentMethod");
        User currentUser = getCurrentUser(req);

        if (recordId == null) {
            writeError(resp, "记录ID不能为空");
            return;
        }
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            writeError(resp, "缴费方式不能为空");
            return;
        }

        try {
            Map<String, Object> result = paymentService.processPayment(recordId, paymentMethod, currentUser.getUserId());
            writeJson(resp, result);
        } catch (IllegalArgumentException e) {
            writeError(resp, e.getMessage());
        } catch (Exception e) {
            logger.error("处理缴费失败", e);
            writeError(resp, "处理缴费失败：" + e.getMessage());
        }
    }

    /**
     * 生成物业费账单
     */
    public void generateBill(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        String billingPeriod = getStringParameter(req, "billingPeriod");
        String dueDateStr = getStringParameter(req, "dueDate");
        String itemId = getStringParameter(req, "itemId");

        if (billingPeriod == null || billingPeriod.isEmpty()) {
            writeError(resp, "缴费期限不能为空");
            return;
        }
        if (dueDateStr == null || dueDateStr.isEmpty()) {
            writeError(resp, "截止日期不能为空");
            return;
        }
        if (itemId == null || itemId.isEmpty()) {
            writeError(resp, "收费项目不能为空");
            return;
        }

        Date dueDate;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dueDate = sdf.parse(dueDateStr);
        } catch (Exception e) {
            writeError(resp, "日期格式不正确");
            return;
        }

        try {
            boolean success = paymentService.generatePropertyBill(billingPeriod, dueDate, itemId);
            if (success) {
                writeSuccess(resp, "生成账单成功");
            } else {
                writeError(resp, "生成账单失败");
            }
        } catch (Exception e) {
            logger.error("生成账单失败", e);
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

        // 如果是业主角色，只能查询自己的欠费
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
}
