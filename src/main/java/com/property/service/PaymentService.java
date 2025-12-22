package com.property.service;

import com.property.dao.PaymentRecordDao;
import com.property.entity.PaymentRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 缴费服务类
 */
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private PaymentRecordDao paymentRecordDao = new PaymentRecordDao();

    /**
     * 根据ID查询缴费记录
     */
    public PaymentRecord findById(Integer recordId) {
        if (recordId == null) {
            throw new IllegalArgumentException("记录ID不能为空");
        }
        return paymentRecordDao.findById(recordId);
    }

    /**
     * 查询所有缴费记录
     */
    public List<PaymentRecord> findAll() {
        return paymentRecordDao.findAll();
    }

    /**
     * 分页查询缴费记录
     */
    public Map<String, Object> findByPage(int pageNum, int pageSize, String keyword, String status) {
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1) pageSize = 10;

        List<PaymentRecord> list = paymentRecordDao.findByPage(pageNum, pageSize, keyword, status);
        long total = paymentRecordDao.count(keyword, status);
        int totalPages = (int) Math.ceil((double) total / pageSize);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", totalPages);

        return result;
    }

    /**
     * 根据业主ID查询缴费记录
     */
    public List<PaymentRecord> findByOwnerId(String ownerId) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("业主ID不能为空");
        }
        return paymentRecordDao.findByOwnerId(ownerId);
    }

    /**
     * 查询业主未缴费记录
     */
    public List<PaymentRecord> findUnpaidByOwnerId(String ownerId) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("业主ID不能为空");
        }
        return paymentRecordDao.findUnpaidByOwnerId(ownerId);
    }

    /**
     * 查询业主已缴费记录
     */
    public List<PaymentRecord> findPaidByOwnerId(String ownerId) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("业主ID不能为空");
        }
        return paymentRecordDao.findPaidByOwnerId(ownerId);
    }

    /**
     * 查询逾期记录
     */
    public List<PaymentRecord> findOverdueRecords() {
        return paymentRecordDao.findOverdueRecords();
    }

    /**
     * 添加缴费记录
     */
    public Integer addPaymentRecord(PaymentRecord record) {
        // 参数验证
        validatePaymentRecord(record);

        // 设置默认状态
        if (record.getPaymentStatus() == null || record.getPaymentStatus().trim().isEmpty()) {
            record.setPaymentStatus("unpaid");
        }
        if (record.getLateFee() == null) {
            record.setLateFee(BigDecimal.ZERO);
        }

        Integer recordId = paymentRecordDao.insert(record);
        if (recordId != null) {
            logger.info("添加缴费记录成功：业主={}, 项目={}, 金额={}",
                    record.getOwnerId(), record.getItemId(), record.getAmount());
        }
        return recordId;
    }

    /**
     * 更新缴费记录
     */
    public boolean updatePaymentRecord(PaymentRecord record) {
        if (record.getRecordId() == null) {
            throw new IllegalArgumentException("记录ID不能为空");
        }

        // 检查记录是否存在
        PaymentRecord existRecord = paymentRecordDao.findById(record.getRecordId());
        if (existRecord == null) {
            throw new IllegalArgumentException("缴费记录不存在");
        }

        // 验证记录信息
        validatePaymentRecord(record);

        int rows = paymentRecordDao.update(record);
        if (rows > 0) {
            logger.info("更新缴费记录成功：记录ID={}", record.getRecordId());
            return true;
        }
        return false;
    }

    /**
     * 处理缴费（调用存储过程）
     */
    public Map<String, Object> processPayment(Integer recordId, String paymentMethod, Integer operatorId) {
        if (recordId == null) {
            throw new IllegalArgumentException("记录ID不能为空");
        }
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("缴费方式不能为空");
        }
        if (operatorId == null) {
            throw new IllegalArgumentException("操作员ID不能为空");
        }

        // 验证缴费方式
        if (!paymentMethod.matches("^(cash|wechat|alipay|bank_transfer|online)$")) {
            throw new IllegalArgumentException("缴费方式无效");
        }

        // 检查记录是否存在
        PaymentRecord record = paymentRecordDao.findById(recordId);
        if (record == null) {
            throw new IllegalArgumentException("缴费记录不存在");
        }

        // 检查是否已缴费
        if ("paid".equals(record.getPaymentStatus())) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "该账单已缴费");
            return result;
        }

        // 调用存储过程处理缴费
        Map<String, Object> procedureResult = paymentRecordDao.processPayment(recordId, paymentMethod, operatorId);

        Map<String, Object> result = new HashMap<>();
        String message = (String) procedureResult.get("message");

        if (message != null && message.contains("成功")) {
            result.put("success", true);
            result.put("message", message);
            result.put("receiptNo", procedureResult.get("receiptNo"));
            logger.info("缴费成功：记录ID={}, 收据号={}", recordId, procedureResult.get("receiptNo"));
        } else {
            result.put("success", false);
            result.put("message", message);
            logger.warn("缴费失败：记录ID={}, 原因={}", recordId, message);
        }

        return result;
    }

    /**
     * 生成物业费账单（调用存储过程）
     */
    public boolean generatePropertyBill(String billingPeriod, Date dueDate, String itemId) {
        if (billingPeriod == null || billingPeriod.trim().isEmpty()) {
            throw new IllegalArgumentException("缴费期限不能为空");
        }
        if (dueDate == null) {
            throw new IllegalArgumentException("截止日期不能为空");
        }
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new IllegalArgumentException("收费项目ID不能为空");
        }

        try {
            paymentRecordDao.generatePropertyBill(billingPeriod, dueDate, itemId);
            logger.info("生成物业费账单成功：期限={}, 项目={}", billingPeriod, itemId);
            return true;
        } catch (Exception e) {
            logger.error("生成物业费账单失败", e);
            throw new RuntimeException("生成账单失败：" + e.getMessage());
        }
    }

    /**
     * 删除缴费记录
     */
    public boolean deletePaymentRecord(Integer recordId) {
        if (recordId == null) {
            throw new IllegalArgumentException("记录ID不能为空");
        }

        // 检查是否已缴费
        PaymentRecord record = paymentRecordDao.findById(recordId);
        if (record != null && "paid".equals(record.getPaymentStatus())) {
            throw new IllegalArgumentException("已缴费的记录不能删除");
        }

        int rows = paymentRecordDao.delete(recordId);
        if (rows > 0) {
            logger.info("删除缴费记录成功：记录ID={}", recordId);
            return true;
        }
        return false;
    }

    /**
     * 统计业主欠费总额
     */
    public BigDecimal sumUnpaidAmount(String ownerId) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("业主ID不能为空");
        }
        return paymentRecordDao.sumUnpaidAmount(ownerId);
    }

    /**
     * 统计某时间段内的收费情况
     */
    public Map<String, Object> statisticsByPeriod(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("开始日期和结束日期不能为空");
        }
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }

        return paymentRecordDao.statisticsByPeriod(startDate, endDate);
    }

    /**
     * 查询物业收费统计（调用视图）
     */
    public List<Map<String, Object>> getPaymentStatistics(String startMonth, String endMonth) {
        if (startMonth == null || startMonth.trim().isEmpty()) {
            throw new IllegalArgumentException("开始月份不能为空");
        }
        if (endMonth == null || endMonth.trim().isEmpty()) {
            throw new IllegalArgumentException("结束月份不能为空");
        }

        // 验证月份格式（yyyy-MM）
        if (!startMonth.matches("^\\d{4}-\\d{2}$") || !endMonth.matches("^\\d{4}-\\d{2}$")) {
            throw new IllegalArgumentException("月份格式不正确，应为：yyyy-MM");
        }

        return paymentRecordDao.getPaymentStatistics(startMonth, endMonth);
    }

    /**
     * 查询各楼栋缴费情况（调用视图）
     */
    public List<Map<String, Object>> getBuildingPaymentStatus() {
        return paymentRecordDao.getBuildingPaymentStatus();
    }

    /**
     * 验证缴费记录信息
     */
    private void validatePaymentRecord(PaymentRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("缴费记录信息不能为空");
        }
        if (record.getOwnerId() == null || record.getOwnerId().trim().isEmpty()) {
            throw new IllegalArgumentException("业主ID不能为空");
        }
        if (record.getHouseId() == null || record.getHouseId().trim().isEmpty()) {
            throw new IllegalArgumentException("房屋ID不能为空");
        }
        if (record.getItemId() == null || record.getItemId().trim().isEmpty()) {
            throw new IllegalArgumentException("收费项目ID不能为空");
        }
        if (record.getBillingPeriod() == null || record.getBillingPeriod().trim().isEmpty()) {
            throw new IllegalArgumentException("缴费期限不能为空");
        }
        if (record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("应缴金额必须大于0");
        }
        if (record.getDueDate() == null) {
            throw new IllegalArgumentException("截止日期不能为空");
        }
    }
}
