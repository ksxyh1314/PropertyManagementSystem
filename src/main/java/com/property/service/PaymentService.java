package com.property.service;

import com.property.dao.ChargeItemDao;
import com.property.dao.HouseDao;
import com.property.dao.PaymentRecordDao;
import com.property.entity.ChargeItem;
import com.property.entity.House;
import com.property.entity.PaymentRecord;
import com.property.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 缴费服务类（支持宽限期和滞纳金计算 + 日志记录）
 */
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private PaymentRecordDao PaymentRecordDao = new PaymentRecordDao();
    private ChargeItemDao chargeItemDao = new ChargeItemDao();
    private HouseDao houseDao = new HouseDao();

    // ==========================================
    // 🔥 新增：适配业主端的方法 (Start)
    // ==========================================

    /**
     * 🔥 获取业主欠费汇总信息（修复版：包含滞纳金）
     */
    public Map<String, Object> getUnpaidSummary(String ownerId) {
        logger.info("📊 查询业主欠费汇总: ownerId={}", ownerId);

        Map<String, Object> summary = new HashMap<>();

        try {
            // 1️⃣ 获取该业主所有未缴费记录（从视图查询，视图已包含动态计算的滞纳金）
            List<PaymentRecord> unpaidRecords = PaymentRecordDao.findUnpaidByOwnerId(ownerId);

            // 2️⃣ 初始化统计变量
            int unpaidCount = 0;              // 未逾期数量
            BigDecimal unpaidAmount = BigDecimal.ZERO;   // 未逾期总额（本金+滞纳金）
            int overdueCount = 0;             // 逾期数量
            BigDecimal overdueAmount = BigDecimal.ZERO;  // 逾期总额（本金+滞纳金）
            BigDecimal totalAmount = BigDecimal.ZERO;    // 总欠费（本金+滞纳金）

            // 3️⃣ 遍历记录进行统计
            for (PaymentRecord record : unpaidRecords) {
                // 🔥 获取本金和滞纳金
                BigDecimal amount = record.getAmount() != null ? record.getAmount() : BigDecimal.ZERO;
                BigDecimal lateFee = record.getLateFee() != null ? record.getLateFee() : BigDecimal.ZERO;

                // 🔥 计算单条记录的总额 = 本金 + 滞纳金
                BigDecimal recordTotal = amount.add(lateFee);

                // 累加总欠费
                totalAmount = totalAmount.add(recordTotal);

                // 🔥 根据 payment_status 字段判断是否逾期
                // 视图中已经动态计算了状态：如果过了宽限期，状态会是 'overdue'
                String status = record.getPaymentStatus();

                if ("overdue".equals(status)) {
                    // 已逾期
                    overdueCount++;
                    overdueAmount = overdueAmount.add(recordTotal);

                    logger.debug("  逾期记录: recordId={}, 本金={}, 滞纳金={}, 小计={}",
                            record.getRecordId(), amount, lateFee, recordTotal);
                } else {
                    // 未逾期（状态为 'unpaid' 或其他）
                    unpaidCount++;
                    unpaidAmount = unpaidAmount.add(recordTotal);

                    logger.debug("  未逾期记录: recordId={}, 本金={}, 滞纳金={}, 小计={}",
                            record.getRecordId(), amount, lateFee, recordTotal);
                }
            }

            // 4️⃣ 组装返回数据
            summary.put("unpaidCount", unpaidCount);           // 未逾期数量
            summary.put("unpaidAmount", unpaidAmount);         // 未逾期金额（含滞纳金）
            summary.put("overdueCount", overdueCount);         // 逾期数量
            summary.put("overdueAmount", overdueAmount);       // 逾期金额（含滞纳金）
            summary.put("totalCount", unpaidCount + overdueCount);  // 总欠费数量
            summary.put("totalAmount", totalAmount);           // 总欠费金额（含滞纳金）

            logger.info("✅ 统计完成:");
            logger.info("  未逾期: {}笔, ¥{}", unpaidCount, unpaidAmount);
            logger.info("  已逾期: {}笔, ¥{}", overdueCount, overdueAmount);
            logger.info("  总欠费: {}笔, ¥{}", unpaidCount + overdueCount, totalAmount);

        } catch (Exception e) {
            logger.error("❌ 查询欠费汇总失败: ownerId={}", ownerId, e);

            // 返回默认值，避免前端报错
            summary.put("unpaidCount", 0);
            summary.put("unpaidAmount", BigDecimal.ZERO);
            summary.put("overdueCount", 0);
            summary.put("overdueAmount", BigDecimal.ZERO);
            summary.put("totalCount", 0);
            summary.put("totalAmount", BigDecimal.ZERO);
        }

        return summary;
    }

    /**
     * 🔥 分页查询（适配业主端：支持 ownerId 和 多状态列表）
     */
    public Map<String, Object> findByPage(int pageNum, int pageSize, String ownerId, List<String> statusList, String keyword) {
        // 1. 获取业主所有记录
        List<PaymentRecord> allRecords = PaymentRecordDao.findByOwnerId(ownerId);

        // 2. 内存过滤
        List<PaymentRecord> filteredList = allRecords.stream()
                .filter(record -> {
                    // 状态过滤
                    boolean statusMatch = true;
                    if (statusList != null && !statusList.isEmpty()) {
                        String recordStatus = record.getPaymentStatus();
                        if (statusList.contains("overdue") && "unpaid".equals(recordStatus)) {
                            if (record.getDueDate() != null && new Date().after(record.getDueDate())) {
                                return true;
                            }
                        }
                        statusMatch = statusList.contains(recordStatus);
                    }

                    // 关键字过滤
                    boolean keywordMatch = true;
                    if (keyword != null && !keyword.trim().isEmpty()) {
                        String k = keyword.toLowerCase();
                        String remark = record.getRemark() != null ? record.getRemark().toLowerCase() : "";
                        keywordMatch = remark.contains(k);
                    }

                    return statusMatch && keywordMatch;
                })
                .sorted((r1, r2) -> {
                    Date d1 = r1.getCreateTime() != null ? r1.getCreateTime() : new Date(0);
                    Date d2 = r2.getCreateTime() != null ? r2.getCreateTime() : new Date(0);
                    return d2.compareTo(d1);
                })
                .collect(Collectors.toList());

        // 3. 内存分页
        int total = filteredList.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);

        List<PaymentRecord> pageList = new ArrayList<>();
        if (start < total) {
            pageList = filteredList.subList(start, end);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageList);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", totalPages);

        return result;
    }

    /**
     * 🔥 重载 processPayment 支持 Integer ID (适配 Servlet)
     */
    public Map<String, Object> processPayment(Integer recordId, String paymentMethod, Integer operatorId) {
        return processPayment(recordId, paymentMethod, operatorId, null);
    }

    /**
     * ✅ 处理缴费（增加日志记录）
     */
    public Map<String, Object> processPayment(Integer recordId, String paymentMethod, Integer operatorId, HttpServletRequest request) {
        if (recordId == null) {
            throw new IllegalArgumentException("记录ID不能为空");
        }
        return processPayment(String.valueOf(recordId), paymentMethod, operatorId, request);
    }

    // ==========================================
    // 核心方法
    // ==========================================

    /**
     * 根据ID查询缴费记录
     */
    public PaymentRecord findById(String recordId) {
        if (recordId == null || recordId.trim().isEmpty()) {
            throw new IllegalArgumentException("记录ID不能为空");
        }
        return PaymentRecordDao.findById(recordId);
    }

    /**
     * ✅ 获取缴费记录详情（包含收费项目完整信息）
     */
    public Map<String, Object> getPaymentDetailWithChargeItem(String recordId) {
        logger.info("========================================");
        logger.info("【查询缴费详情】记录ID: {}", recordId);
        logger.info("========================================");

        try {
            PaymentRecord record = PaymentRecordDao.findById(recordId);
            if (record == null) {
                logger.warn("❌ 缴费记录不存在");
                return null;
            }

            ChargeItem chargeItem = chargeItemDao.findById(record.getItemId());
            if (chargeItem == null) {
                logger.warn("❌ 收费项目不存在");
                return null;
            }

            Map<String, Object> detail = new HashMap<>();

            // 缴费记录基本信息
            detail.put("recordId", record.getRecordId());
            detail.put("ownerId", record.getOwnerId());
            detail.put("ownerName", record.getOwnerName());
            detail.put("houseId", record.getHouseId());
            detail.put("itemId", record.getItemId());
            detail.put("itemName", record.getItemName());
            detail.put("billingPeriod", record.getBillingPeriod());
            detail.put("amount", record.getAmount());
            detail.put("lateFee", record.getLateFee());
            detail.put("totalAmount", record.getAmount().add(record.getLateFee()));
            detail.put("dueDate", record.getDueDate());
            detail.put("paymentStatus", record.getPaymentStatus());
            detail.put("paymentMethod", record.getPaymentMethod());
            detail.put("paymentDate", record.getPaymentDate());
            detail.put("receiptNo", record.getReceiptNo());
            detail.put("remark", record.getRemark());
            detail.put("createTime", record.getCreateTime());
            detail.put("updateTime", record.getUpdateTime());

            // 收费项目详细信息
            detail.put("chargeCycle", chargeItem.getChargeCycle());
            detail.put("calculationType", chargeItem.getCalculationType());
            detail.put("fixedAmount", chargeItem.getFixedAmount());
            detail.put("gracePeriod", chargeItem.getGracePeriod());
            detail.put("lateFeeRate", chargeItem.getLateFeeRate());

            // 计算宽限期结束日期和逾期天数
            Date dueDate = record.getDueDate();
            Integer gracePeriod = chargeItem.getGracePeriod() != null ? chargeItem.getGracePeriod() : 0;

            if (dueDate != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dueDate);
                cal.add(Calendar.DAY_OF_MONTH, gracePeriod);
                Date graceDueDate = cal.getTime();
                detail.put("graceDueDate", graceDueDate);

                Date today = new Date();
                if (today.after(graceDueDate)) {
                    long diffInMillies = today.getTime() - graceDueDate.getTime();
                    long overdueDays = diffInMillies / (1000 * 60 * 60 * 24);
                    detail.put("overdueDays", (int) overdueDays);
                } else {
                    detail.put("overdueDays", 0);

                    if (today.after(dueDate)) {
                        long diffInMillies = graceDueDate.getTime() - today.getTime();
                        long remainingGraceDays = diffInMillies / (1000 * 60 * 60 * 24);
                        detail.put("remainingGraceDays", (int) remainingGraceDays);
                    }
                }
            }

            // 查询房屋信息
            try {
                House house = houseDao.findById(record.getHouseId());
                if (house != null) {
                    detail.put("houseArea", house.getArea());
                    detail.put("buildingNo", house.getBuildingNo());
                    detail.put("unitNo", house.getUnitNo());
                    detail.put("floor", house.getFloor());
                    detail.put("layout", house.getLayout());
                    detail.put("houseStatus", house.getHouseStatus());
                    detail.put("houseStatusDisplay", house.getHouseStatusDisplay());
                    detail.put("fullAddress", house.getFullAddress());
                    detail.put("pricePerSqm", house.getPricePerSqm());
                }
            } catch (Exception e) {
                logger.warn("查询房屋信息失败: {}", e.getMessage());
            }

            logger.info("✅ 查询缴费详情成功");
            return detail;

        } catch (Exception e) {
            logger.error("❌ 查询缴费详情失败", e);
            throw new RuntimeException("查询缴费详情失败: " + e.getMessage(), e);
        }
    }

    /**
     * 查询所有缴费记录
     */
    public List<PaymentRecord> findAll() {
        return PaymentRecordDao.findAll();
    }

    /**
     * 查询所有符合条件的记录（不分页，用于导出）
     */
    public List<PaymentRecord> findAll(String keyword, String status) {
        try {
            if ((keyword == null || keyword.trim().isEmpty()) &&
                    (status == null || status.trim().isEmpty())) {
                return PaymentRecordDao.findAll();
            } else {
                return PaymentRecordDao.findByPage(1, 100000, keyword, status);
            }
        } catch (Exception e) {
            logger.error("查询所有记录失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 分页查询缴费记录 (管理员端使用)
     */
    public Map<String, Object> findByPage(int pageNum, int pageSize, String keyword, String status) {
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1) pageSize = 10;

        List<PaymentRecord> list = PaymentRecordDao.findByPage(pageNum, pageSize, keyword, status);
        long total = PaymentRecordDao.count(keyword, status);
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
     * 获取统计数据
     */
    public Map<String, Object> getStatistics(String keyword, String status) {
        try {
            Map<String, Object> statistics = PaymentRecordDao.getStatistics(keyword, status);
            List<Map<String, Object>> monthlyStats = PaymentRecordDao.getMonthlyStatistics();
            statistics.put("monthlyStats", monthlyStats);

            List<Map<String, Object>> feeTypeStats = PaymentRecordDao.getFeeTypeStatistics(keyword, status);
            statistics.put("feeTypeStats", feeTypeStats);

            Integer totalCount = (Integer) statistics.getOrDefault("totalCount", 0);
            Integer paidCount = (Integer) statistics.getOrDefault("paidCount", 0);
            double paymentRate = totalCount > 0 ? (paidCount * 100.0 / totalCount) : 0;
            statistics.put("paymentRate", String.format("%.2f", paymentRate));

            return statistics;
        } catch (Exception e) {
            logger.error("获取统计数据失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("totalCount", 0);
            result.put("totalAmount", BigDecimal.ZERO);
            result.put("paidCount", 0);
            result.put("paidAmount", BigDecimal.ZERO);
            result.put("unpaidCount", 0);
            result.put("unpaidAmount", BigDecimal.ZERO);
            result.put("overdueCount", 0);
            result.put("overdueAmount", BigDecimal.ZERO);
            result.put("paymentRate", "0.00");
            result.put("monthlyStats", new ArrayList<>());
            result.put("feeTypeStats", new ArrayList<>());
            return result;
        }
    }

    /**
     * 获取收入统计
     */
    public Map<String, Object> getIncomeStatistics(String startDate, String endDate) {
        try {
            return PaymentRecordDao.getIncomeStatistics(startDate, endDate);
        } catch (Exception e) {
            logger.error("获取收入统计失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("totalCount", 0);
            result.put("totalAmount", BigDecimal.ZERO);
            result.put("totalLateFee", BigDecimal.ZERO);
            result.put("totalIncome", BigDecimal.ZERO);
            return result;
        }
    }

    /**
     * 获取月度统计
     */
    public List<Map<String, Object>> getMonthlyStatistics() {
        try {
            return PaymentRecordDao.getMonthlyStatistics();
        } catch (Exception e) {
            logger.error("获取月度统计失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取费用类型统计
     */
    public List<Map<String, Object>> getFeeTypeStatistics(String keyword, String status) {
        try {
            return PaymentRecordDao.getFeeTypeStatistics(keyword, status);
        } catch (Exception e) {
            logger.error("获取费用类型统计失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据业主ID查询缴费记录
     */
    public List<PaymentRecord> findByOwnerId(String ownerId) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("业主ID不能为空");
        }
        return PaymentRecordDao.findByOwnerId(ownerId);
    }

    /**
     * 查询业主未缴费记录
     */
    public List<PaymentRecord> findUnpaidByOwnerId(String ownerId) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("业主ID不能为空");
        }
        return PaymentRecordDao.findUnpaidByOwnerId(ownerId);
    }

    /**
     * 查询业主已缴费记录
     */
    public List<PaymentRecord> findPaidByOwnerId(String ownerId) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("业主ID不能为空");
        }
        return PaymentRecordDao.findPaidByOwnerId(ownerId);
    }

    /**
     * 查询逾期记录
     */
    public List<PaymentRecord> findOverdueRecords() {
        return PaymentRecordDao.findOverdueRecords();
    }

    /**
     * ✅ 添加缴费记录（增加日志记录）
     */
    public boolean addPaymentRecord(PaymentRecord record) {
        return addPaymentRecord(record, null);
    }

    /**
     * ✅ 添加缴费记录（增加日志记录）
     */
    public boolean addPaymentRecord(PaymentRecord record, HttpServletRequest request) {
        validatePaymentRecord(record);

        if (record.getPaymentStatus() == null || record.getPaymentStatus().trim().isEmpty()) {
            record.setPaymentStatus("unpaid");
        }
        if (record.getLateFee() == null) {
            record.setLateFee(BigDecimal.ZERO);
        }

        String recordId = PaymentRecordDao.insert(record);
        if (recordId != null && !recordId.trim().isEmpty()) {
            logger.info("添加缴费记录成功：业主={}, 项目={}, 金额={}",
                    record.getOwnerId(), record.getItemId(), record.getAmount());

            // ✅ 记录操作日志
            if (request != null) {
                LogUtil.log(
                        getUserId(request),
                        getUsername(request),
                        "payment_add",
                        "添加缴费记录：业主" + record.getOwnerId() + "，项目" + record.getItemId() +
                                "，金额" + record.getAmount() + "元",
                        LogUtil.getClientIP(request)
                );
            }

            return true;
        }
        return false;
    }

    /**
     * ✅ 更新缴费记录（增加日志记录）
     */
    public boolean updatePaymentRecord(PaymentRecord record) {
        return updatePaymentRecord(record, null);
    }

    /**
     * ✅ 更新缴费记录（增加日志记录）
     */
    public boolean updatePaymentRecord(PaymentRecord record, HttpServletRequest request) {
        if (record.getRecordId() == null || record.getRecordId().trim().isEmpty()) {
            throw new IllegalArgumentException("记录ID不能为空");
        }

        PaymentRecord existRecord = PaymentRecordDao.findById(record.getRecordId());
        if (existRecord == null) {
            throw new IllegalArgumentException("缴费记录不存在");
        }

        validatePaymentRecord(record);

        int rows = PaymentRecordDao.update(record);
        if (rows > 0) {
            logger.info("更新缴费记录成功：记录ID={}", record.getRecordId());

            // ✅ 记录操作日志
            if (request != null) {
                LogUtil.log(
                        getUserId(request),
                        getUsername(request),
                        "payment_update",
                        "修改缴费记录：记录ID" + record.getRecordId(),
                        LogUtil.getClientIP(request)
                );
            }

            return true;
        }
        return false;
    }

    /**
     * ✅ 处理缴费（调用存储过程，自动计算滞纳金，增加日志记录）
     */
    public Map<String, Object> processPayment(String recordId, String paymentMethod, Integer operatorId, HttpServletRequest request) {
        if (recordId == null || recordId.trim().isEmpty()) {
            throw new IllegalArgumentException("记录ID不能为空");
        }
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("缴费方式不能为空");
        }
        if (operatorId == null) {
            throw new IllegalArgumentException("操作员ID不能为空");
        }

        if (!paymentMethod.matches("^(cash|wechat|alipay|bank_transfer|online)$")) {
            throw new IllegalArgumentException("缴费方式无效");
        }

        PaymentRecord record = PaymentRecordDao.findById(recordId);
        if (record == null) {
            throw new IllegalArgumentException("缴费记录不存在");
        }

        if ("paid".equals(record.getPaymentStatus())) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "该账单已缴费");
            return result;
        }

        logger.info("========== 开始处理缴费 ==========");
        logger.info("记录ID: {}", recordId);

        try {
            // 1. 先计算滞纳金
            Map<String, Object> calculation = calculateLateFee(recordId);
            Boolean calcSuccess = (Boolean) calculation.get("success");

            if (calcSuccess == null || !calcSuccess) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "计算滞纳金失败：" + calculation.get("message"));
                return result;
            }

            BigDecimal lateFee = (BigDecimal) calculation.get("lateFee");
            BigDecimal totalAmount = (BigDecimal) calculation.get("totalAmount");
            Integer overdueDays = (Integer) calculation.get("overdueDays");

            logger.info("应缴金额: {}", record.getAmount());
            logger.info("滞纳金: {} (逾期{}天)", lateFee, overdueDays);
            logger.info("总金额: {}", totalAmount);

            // 2. 调用存储过程处理缴费
            Map<String, Object> procedureResult = PaymentRecordDao.processPayment(recordId, paymentMethod, operatorId);

            Map<String, Object> result = new HashMap<>();
            String message = (String) procedureResult.get("message");

            if (message != null && message.contains("成功")) {
                result.put("success", true);
                result.put("message", message);
                result.put("receiptNo", procedureResult.get("receiptNo"));
                result.put("lateFee", lateFee);
                result.put("totalAmount", totalAmount);
                result.put("overdueDays", overdueDays);

                logger.info("✅ 缴费成功！收据号：{}", procedureResult.get("receiptNo"));

                // ✅ 记录缴费日志
                if (request != null) {
                    LogUtil.log(
                            operatorId,
                            getUsername(request),
                            "payment_process",
                            "处理缴费：业主" + record.getOwnerId() + "，记录ID" + recordId +
                                    "，金额" + totalAmount + "元（含滞纳金" + lateFee + "元），收据号" + procedureResult.get("receiptNo"),
                            LogUtil.getClientIP(request)
                    );
                }
            } else {
                result.put("success", false);
                result.put("message", message);
                logger.warn("❌ 缴费失败：{}", message);
            }

            logger.info("========================================");
            return result;

        } catch (Exception e) {
            logger.error("处理缴费异常", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "处理缴费失败：" + e.getMessage());
            return result;
        }
    }

    /**
     * 计算滞纳金（缴费前预览）
     */
    public Map<String, Object> calculateLateFee(String recordId) {
        logger.info("========== 开始计算滞纳金 ==========");
        logger.info("记录ID: {}", recordId);

        Map<String, Object> result = new HashMap<>();

        try {
            PaymentRecord record = PaymentRecordDao.findById(recordId);
            if (record == null) {
                result.put("success", false);
                result.put("message", "缴费记录不存在");
                return result;
            }

            if ("paid".equals(record.getPaymentStatus())) {
                result.put("success", false);
                result.put("message", "该账单已缴费");
                return result;
            }

            ChargeItem chargeItem = chargeItemDao.findById(record.getItemId());
            if (chargeItem == null) {
                result.put("success", false);
                result.put("message", "收费项目不存在");
                return result;
            }

            Date dueDate = record.getDueDate();
            Integer gracePeriod = chargeItem.getGracePeriod() != null ? chargeItem.getGracePeriod() : 30;
            BigDecimal lateFeeRate = chargeItem.getLateFeeRate() != null ?
                    chargeItem.getLateFeeRate() : new BigDecimal("0.0005");

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dueDate);
            calendar.add(Calendar.DAY_OF_MONTH, gracePeriod);
            Date graceEndDate = calendar.getTime();

            Date currentDate = new Date();

            BigDecimal lateFee = BigDecimal.ZERO;
            int overdueDays = 0;
            String status = "normal";

            if (currentDate.after(graceEndDate)) {
                status = "overdue";
                long diffInMillies = currentDate.getTime() - graceEndDate.getTime();
                overdueDays = (int) (diffInMillies / (1000 * 60 * 60 * 24));

                lateFee = record.getAmount()
                        .multiply(lateFeeRate)
                        .multiply(new BigDecimal(overdueDays))
                        .setScale(2, RoundingMode.HALF_UP);

            } else if (currentDate.after(dueDate)) {
                status = "grace";
                long diffInMillies = currentDate.getTime() - dueDate.getTime();
                int graceDaysUsed = (int) (diffInMillies / (1000 * 60 * 60 * 24));
                int graceDaysRemaining = gracePeriod - graceDaysUsed;

                result.put("graceDaysUsed", graceDaysUsed);
                result.put("graceDaysRemaining", graceDaysRemaining);
            }

            BigDecimal totalAmount = record.getAmount().add(lateFee);

            result.put("success", true);
            result.put("status", status);
            result.put("amount", record.getAmount());
            result.put("lateFee", lateFee);
            result.put("totalAmount", totalAmount);
            result.put("overdueDays", overdueDays);
            result.put("dueDate", dueDate);
            result.put("graceEndDate", graceEndDate);
            result.put("gracePeriod", gracePeriod);
            result.put("lateFeeRate", lateFeeRate);

            String message;
            if ("overdue".equals(status)) {
                message = String.format("逾期%d天，应缴金额: %.2f元，滞纳金: %.2f元，合计: %.2f元",
                        overdueDays, record.getAmount(), lateFee, totalAmount);
            } else if ("grace".equals(status)) {
                message = String.format("在宽限期内，还剩%d天，应缴金额: %.2f元（无滞纳金）",
                        result.get("graceDaysRemaining"), record.getAmount());
            } else {
                message = String.format("未逾期，应缴金额: %.2f元", record.getAmount());
            }

            result.put("message", message);
            return result;

        } catch (Exception e) {
            logger.error("❌ 计算滞纳金失败", e);
            result.put("success", false);
            result.put("message", "计算失败：" + e.getMessage());
            return result;
        }
    }

    /**
     * 根据收费项目和房屋信息计算费用
     */
    private BigDecimal calculateAmount(ChargeItem chargeItem, House house) {
        String calculationType = chargeItem.getCalculationType();

        try {
            if ("fixed".equals(calculationType)) {
                BigDecimal amount = chargeItem.getFixedAmount();
                if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                    return null;
                }
                return amount;

            } else if ("area_based".equals(calculationType)) {
                BigDecimal area = house.getArea();
                BigDecimal unitPrice = chargeItem.getFixedAmount();

                if (area == null || area.compareTo(BigDecimal.ZERO) <= 0) {
                    return null;
                }
                if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
                    return null;
                }

                return area.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP);
            } else {
                return null;
            }

        } catch (Exception e) {
            logger.error("计算费用失败", e);
            return null;
        }
    }

    /**
     * ✅ 生成账单（增加日志记录）
     */
    public Map<String, Object> generateBillByChargeItem(String itemId, String billingPeriod, Date dueDate, String buildingId, String houseIds) {
        return generateBillByChargeItem(itemId, billingPeriod, dueDate, buildingId, houseIds, null);
    }

    /**
     * ✅ 生成账单（增加日志记录）
     */
    public Map<String, Object> generateBillByChargeItem(String itemId, String billingPeriod, Date dueDate, String buildingId, String houseIds, HttpServletRequest request) {
        logger.info("========================================");
        logger.info("【生成账单】开始");
        logger.info("收费项目ID: {}", itemId);
        logger.info("账期: {}", billingPeriod);
        logger.info("楼栋ID: {}", buildingId);
        logger.info("自定义房屋: {}", houseIds);
        logger.info("========================================");

        Map<String, Object> result = new HashMap<>();

        try {
            ChargeItem chargeItem = chargeItemDao.findById(itemId);
            if (chargeItem == null || chargeItem.getStatus() != 1) {
                result.put("success", false);
                result.put("message", "收费项目不存在或未启用");
                return result;
            }

            List<House> houses = new ArrayList<>();

            if (houseIds != null && !houseIds.trim().isEmpty()) {
                String[] ids = houseIds.split("[,\\n\\s]+");
                for (String id : ids) {
                    if (!id.trim().isEmpty()) {
                        House h = houseDao.findById(id.trim());
                        if (h != null) {
                            houses.add(h);
                        }
                    }
                }
            } else if (buildingId != null && !buildingId.trim().isEmpty()) {
                houses = houseDao.findByBuildingId(buildingId);
            } else {
                houses = houseDao.findOccupiedHouses();
            }

            if (houses.isEmpty()) {
                result.put("success", false);
                result.put("message", "没有找到符合条件的房屋");
                result.put("totalCount", 0);
                result.put("successCount", 0);
                result.put("failCount", 0);
                return result;
            }

            int totalCount = 0;
            int successCount = 0;
            int failCount = 0;
            List<String> errorMessages = new ArrayList<>();
            List<Map<String, Object>> successDetails = new ArrayList<>();

            for (House house : houses) {
                totalCount++;

                try {
                    if (house.getOwnerId() == null || house.getOwnerId().trim().isEmpty()) {
                        failCount++;
                        errorMessages.add(house.getHouseId() + ": 房屋空置（无业主）");
                        continue;
                    }

                    if (PaymentRecordDao.existsBill(house.getOwnerId(), house.getHouseId(), itemId, billingPeriod)) {
                        failCount++;
                        errorMessages.add(house.getHouseId() + ": 该账期已有账单");
                        continue;
                    }

                    BigDecimal amount = calculateAmount(chargeItem, house);

                    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                        failCount++;
                        errorMessages.add(house.getHouseId() + ": 计算金额无效");
                        continue;
                    }

                    PaymentRecord record = new PaymentRecord();
                    record.setOwnerId(house.getOwnerId());
                    record.setHouseId(house.getHouseId());
                    record.setItemId(itemId);
                    record.setBillingPeriod(billingPeriod);
                    record.setAmount(amount);
                    record.setDueDate(dueDate);
                    record.setPaymentStatus("unpaid");
                    record.setLateFee(BigDecimal.ZERO);

                    String remark = String.format("系统自动生成 - %s - %s",
                            chargeItem.getItemName(), billingPeriod);
                    record.setRemark(remark);

                    String recordId = PaymentRecordDao.insert(record);

                    if (recordId != null && !recordId.trim().isEmpty()) {
                        successCount++;

                        Map<String, Object> detail = new HashMap<>();
                        detail.put("houseId", house.getHouseId());
                        detail.put("ownerId", house.getOwnerId());
                        detail.put("amount", amount);
                        detail.put("recordId", recordId);
                        successDetails.add(detail);
                    } else {
                        failCount++;
                        errorMessages.add(house.getHouseId() + ": 数据库插入失败");
                    }

                } catch (Exception e) {
                    failCount++;
                    errorMessages.add(house.getHouseId() + ": " + e.getMessage());
                }
            }

            logger.info("【生成完成】总数: {}, 成功: {}, 失败: {}", totalCount, successCount, failCount);

            result.put("success", successCount > 0);
            result.put("message", String.format("生成完成！总数: %d, 成功: %d, 失败/跳过: %d",
                    totalCount, successCount, failCount));
            result.put("totalCount", totalCount);
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("errorMessages", errorMessages);
            result.put("successDetails", successDetails);

            // ✅ 记录生成账单日志
            if (request != null && successCount > 0) {
                LogUtil.log(
                        getUserId(request),
                        getUsername(request),
                        "payment_generate",
                        "生成账单：项目" + chargeItem.getItemName() + "，账期" + billingPeriod +
                                "，成功" + successCount + "条",
                        LogUtil.getClientIP(request)
                );
            }

            return result;

        } catch (Exception e) {
            logger.error("❌ 生成账单失败", e);
            result.put("success", false);
            result.put("message", "生成账单失败：" + e.getMessage());
            result.put("totalCount", 0);
            result.put("successCount", 0);
            result.put("failCount", 0);
            return result;
        }
    }

    /**
     * 生成物业费账单（调用存储过程）
     */
    public boolean generatePropertyBill(String billingMonth, Date dueDate, String itemId) {
        if (billingMonth == null || billingMonth.trim().isEmpty()) {
            throw new IllegalArgumentException("账期不能为空");
        }
        if (dueDate == null) {
            throw new IllegalArgumentException("截止日期不能为空");
        }
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new IllegalArgumentException("收费项目ID不能为空");
        }

        try {
            PaymentRecordDao.generatePropertyBill(billingMonth, dueDate, itemId);
            logger.info("生成物业费账单成功：期限={}, 项目={}", billingMonth, itemId);
            return true;
        } catch (Exception e) {
            logger.error("生成物业费账单失败", e);
            throw new RuntimeException("生成账单失败：" + e.getMessage());
        }
    }

    /**
     * ✅ 删除缴费记录（增加日志记录）
     */
    public boolean deletePaymentRecord(String recordId) {
        return deletePaymentRecord(recordId, null);
    }

    /**
     * ✅ 删除缴费记录（增加日志记录）
     */
    public boolean deletePaymentRecord(String recordId, HttpServletRequest request) {
        if (recordId == null || recordId.trim().isEmpty()) {
            throw new IllegalArgumentException("记录ID不能为空");
        }

        PaymentRecord record = PaymentRecordDao.findById(recordId);
        if (record != null && "paid".equals(record.getPaymentStatus())) {
            throw new IllegalArgumentException("已缴费的记录不能删除");
        }

        int rows = PaymentRecordDao.delete(recordId);
        if (rows > 0) {
            logger.info("删除缴费记录成功：记录ID={}", recordId);

            // ✅ 记录删除日志
            if (request != null && record != null) {
                LogUtil.log(
                        getUserId(request),
                        getUsername(request),
                        "payment_delete",
                        "删除缴费记录：记录ID" + recordId + "，业主" + record.getOwnerId(),
                        LogUtil.getClientIP(request)
                );
            }

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
        return PaymentRecordDao.sumUnpaidAmount(ownerId);
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

        return PaymentRecordDao.statisticsByPeriod(startDate, endDate);
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

        if (!startMonth.matches("^\\d{4}-\\d{2}$") || !endMonth.matches("^\\d{4}-\\d{2}$")) {
            throw new IllegalArgumentException("月份格式不正确，应为：yyyy-MM");
        }

        return PaymentRecordDao.getPaymentStatistics(startMonth, endMonth);
    }

    /**
     * 查询各楼栋缴费情况（调用视图）
     */
    public List<Map<String, Object>> getBuildingPaymentStatus() {
        return PaymentRecordDao.getBuildingPaymentStatus();
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
            throw new IllegalArgumentException("账期不能为空");
        }
        if (record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("应缴金额必须大于0");
        }
        if (record.getDueDate() == null) {
            throw new IllegalArgumentException("截止日期不能为空");
        }
    }

    /**
     * 业主端：获取缴费汇总
     */
    public Map<String, Object> getOwnerPaymentSummary(String ownerId) {
        logger.info("查询业主缴费汇总：ownerId={}", ownerId);

        Map<String, Object> summary = new HashMap<>();

        try {
            int unpaidCount = PaymentRecordDao.countByOwnerAndStatus(ownerId, "unpaid");
            int overdueCount = PaymentRecordDao.countByOwnerAndStatus(ownerId, "overdue");
            int paidCount = PaymentRecordDao.countByOwnerAndStatus(ownerId, "paid");

            BigDecimal totalAmount = BigDecimal.ZERO;
            BigDecimal totalLateFee = BigDecimal.ZERO;

            List<PaymentRecord> unpaidList = PaymentRecordDao.findByOwnerAndStatus(ownerId, "unpaid");
            for (PaymentRecord record : unpaidList) {
                if (record.getAmount() != null) {
                    totalAmount = totalAmount.add(record.getAmount());
                }
                if (record.getLateFee() != null) {
                    totalLateFee = totalLateFee.add(record.getLateFee());
                }
            }

            List<PaymentRecord> overdueList = PaymentRecordDao.findByOwnerAndStatus(ownerId, "overdue");
            for (PaymentRecord record : overdueList) {
                if (record.getAmount() != null) {
                    totalAmount = totalAmount.add(record.getAmount());
                }
                if (record.getLateFee() != null) {
                    totalLateFee = totalLateFee.add(record.getLateFee());
                }
            }

            BigDecimal grandTotal = totalAmount.add(totalLateFee);

            summary.put("unpaidCount", unpaidCount);
            summary.put("overdueCount", overdueCount);
            summary.put("paidCount", paidCount);
            summary.put("totalAmount", totalAmount);
            summary.put("totalLateFee", totalLateFee);
            summary.put("grandTotal", grandTotal);

        } catch (Exception e) {
            logger.error("查询缴费汇总失败", e);
            summary.put("unpaidCount", 0);
            summary.put("overdueCount", 0);
            summary.put("paidCount", 0);
            summary.put("totalAmount", BigDecimal.ZERO);
            summary.put("totalLateFee", BigDecimal.ZERO);
            summary.put("grandTotal", BigDecimal.ZERO);
        }

        return summary;
    }

    /**
     * 支持按收费项目筛选的分页查询（方法重载）
     */
    public Map<String, Object> findByPage(int pageNum, int pageSize, String keyword, String status, String itemId) {
        Map<String, Object> result = new HashMap<>();

        try {
            List<PaymentRecord> list = PaymentRecordDao.findByPage(pageNum, pageSize, keyword, status, itemId);
            long total = PaymentRecordDao.count(keyword, status, itemId);

            result.put("list", list);
            result.put("total", total);
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);
            result.put("pages", (int) Math.ceil((double) total / pageSize));

        } catch (Exception e) {
            logger.error("按项目筛选分页查询失败", e);
            throw new RuntimeException("查询失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 支持按收费项目筛选的统计数据（方法重载）
     */
    public Map<String, Object> getStatistics(String keyword, String status, String itemId) {
        try {
            return PaymentRecordDao.getStatistics(keyword, status, itemId);
        } catch (Exception e) {
            logger.error("获取按项目筛选的统计数据失败", e);
            throw new RuntimeException("获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询缴费记录（支持搜索）
     */
    public Map<String, Object> findByPageWithSearch(Map<String, Object> params) throws SQLException {
        String ownerId = (String) params.get("ownerId");
        int pageNum = (int) params.getOrDefault("pageNum", 1);
        int pageSize = (int) params.getOrDefault("pageSize", 10);

        @SuppressWarnings("unchecked")
        List<String> statusList = (List<String>) params.get("statusList");
        String itemId = (String) params.get("itemId");
        String keyword = (String) params.get("keyword");
        String startDate = (String) params.get("startDate");
        String endDate = (String) params.get("endDate");
        String sortBy = (String) params.getOrDefault("sortBy", "due_date");
        String sortOrder = (String) params.getOrDefault("sortOrder", "desc");

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM view_owner_payment_details WHERE 1=1");

        List<Object> sqlParams = new ArrayList<>();

        if (ownerId != null && !ownerId.trim().isEmpty()) {
            sql.append(" AND owner_id = ?");
            sqlParams.add(ownerId);
        }

        if (statusList != null && !statusList.isEmpty()) {
            sql.append(" AND payment_status IN (");
            for (int i = 0; i < statusList.size(); i++) {
                sql.append(i == 0 ? "?" : ",?");
                sqlParams.add(statusList.get(i));
            }
            sql.append(")");
        }

        if (itemId != null && !itemId.trim().isEmpty()) {
            sql.append(" AND item_id = ?");
            sqlParams.add(itemId);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (item_name LIKE ? OR billing_period LIKE ? OR remark LIKE ?)");
            String likeKeyword = "%" + keyword.trim() + "%";
            sqlParams.add(likeKeyword);
            sqlParams.add(likeKeyword);
            sqlParams.add(likeKeyword);
        }

        if (startDate != null && !startDate.trim().isEmpty()) {
            sql.append(" AND due_date >= ?");
            sqlParams.add(startDate);
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            sql.append(" AND due_date <= ?");
            sqlParams.add(endDate);
        }

        sql.append(" ORDER BY ").append(sortBy).append(" ").append(sortOrder);

        int offset = (pageNum - 1) * pageSize;
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        sqlParams.add(offset);
        sqlParams.add(pageSize);

        List<PaymentRecord> list = PaymentRecordDao.executeQuery(sql.toString(), sqlParams.toArray());

        int total = countBySearch(params);

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
     * 统计符合条件的记录数
     */
    private int countBySearch(Map<String, Object> params) throws SQLException {
        String ownerId = (String) params.get("ownerId");

        @SuppressWarnings("unchecked")
        List<String> statusList = (List<String>) params.get("statusList");
        String itemId = (String) params.get("itemId");
        String keyword = (String) params.get("keyword");
        String startDate = (String) params.get("startDate");
        String endDate = (String) params.get("endDate");

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM view_owner_payment_details WHERE 1=1");

        List<Object> sqlParams = new ArrayList<>();

        if (ownerId != null && !ownerId.trim().isEmpty()) {
            sql.append(" AND owner_id = ?");
            sqlParams.add(ownerId);
        }

        if (statusList != null && !statusList.isEmpty()) {
            sql.append(" AND payment_status IN (");
            for (int i = 0; i < statusList.size(); i++) {
                sql.append(i == 0 ? "?" : ",?");
                sqlParams.add(statusList.get(i));
            }
            sql.append(")");
        }

        if (itemId != null && !itemId.trim().isEmpty()) {
            sql.append(" AND item_id = ?");
            sqlParams.add(itemId);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (item_name LIKE ? OR billing_period LIKE ? OR remark LIKE ?)");
            String likeKeyword = "%" + keyword.trim() + "%";
            sqlParams.add(likeKeyword);
            sqlParams.add(likeKeyword);
            sqlParams.add(likeKeyword);
        }

        if (startDate != null && !startDate.trim().isEmpty()) {
            sql.append(" AND due_date >= ?");
            sqlParams.add(startDate);
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            sql.append(" AND due_date <= ?");
            sqlParams.add(endDate);
        }

        return PaymentRecordDao.executeCount(sql.toString(), sqlParams.toArray());
    }

    /**
     * 查询所有符合条件的记录（支持项目筛选，不分页，用于导出）
     */
    public List<PaymentRecord> findAll(String keyword, String status, String itemId) {
        try {
            if (itemId != null && !itemId.trim().isEmpty()) {
                return PaymentRecordDao.findByPage(1, 100000, keyword, status, itemId);
            } else {
                return findAll(keyword, status);
            }
        } catch (Exception e) {
            logger.error("查询所有记录失败（含项目筛选）", e);
            return new ArrayList<>();
        }
    }

    /**
     * ✅ 批量删除缴费记录（增加日志记录）
     */
    public Map<String, Object> batchDeletePaymentRecords(String[] recordIds) {
        return batchDeletePaymentRecords(recordIds, null);
    }

    /**
     * ✅ 批量删除缴费记录（增加日志记录）
     */
    public Map<String, Object> batchDeletePaymentRecords(String[] recordIds, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        List<String> errorMessages = new ArrayList<>();

        for (String recordId : recordIds) {
            try {
                if (deletePaymentRecord(recordId, request)) {
                    successCount++;
                } else {
                    failCount++;
                    errorMessages.add(recordId + ": 删除失败");
                }
            } catch (Exception e) {
                failCount++;
                errorMessages.add(recordId + ": " + e.getMessage());
            }
        }

        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("errorMessages", errorMessages);

        // ✅ 记录批量删除日志
        if (request != null && successCount > 0) {
            LogUtil.log(
                    getUserId(request),
                    getUsername(request),
                    "payment_batch_delete",
                    "批量删除缴费记录：成功" + successCount + "条，失败" + failCount + "条",
                    LogUtil.getClientIP(request)
            );
        }

        return result;
    }

    /**
     * ✅ 批量删除未缴费记录（增加日志记录）
     */
    public Map<String, Object> batchDeleteUnpaidRecords(String recordIds) {
        return batchDeleteUnpaidRecords(recordIds, null);
    }

    /**
     * ✅ 批量删除未缴费记录（增加日志记录）
     */
    public Map<String, Object> batchDeleteUnpaidRecords(String recordIds, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        if (recordIds == null || recordIds.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "请选择要删除的记录");
            result.put("successCount", 0);
            result.put("failCount", 0);
            return result;
        }

        String[] ids = recordIds.split(",");
        List<String> idList = new ArrayList<>();
        for (String id : ids) {
            String trimmedId = id.trim();
            if (!trimmedId.isEmpty()) {
                idList.add(trimmedId);
            }
        }

        if (idList.isEmpty()) {
            result.put("success", false);
            result.put("message", "没有有效的记录ID");
            result.put("successCount", 0);
            result.put("failCount", 0);
            return result;
        }

        try {
            List<String> paidIds = PaymentRecordDao.findPaidRecordIds(idList);            if (!paidIds.isEmpty()) {
                logger.warn("⚠️ 发现 {} 条已缴费记录，不能删除", paidIds.size());
                result.put("success", false);
                result.put("message", "选中的记录中包含已缴费记录，不能删除");
                result.put("successCount", 0);
                result.put("failCount", idList.size());
                result.put("paidIds", paidIds);
                return result;
            }

            int deletedCount = PaymentRecordDao.batchDeleteUnpaid(idList);

            logger.info("✅ 批量删除完成：成功 {} 条", deletedCount);

            result.put("success", deletedCount > 0);
            result.put("message", String.format("删除成功 %d 条记录", deletedCount));
            result.put("successCount", deletedCount);
            result.put("failCount", idList.size() - deletedCount);

            // ✅ 记录批量删除日志
            if (request != null && deletedCount > 0) {
                LogUtil.log(
                        getUserId(request),
                        getUsername(request),
                        "payment_batch_delete",
                        "批量删除未缴费记录：成功" + deletedCount + "条",
                        LogUtil.getClientIP(request)
                );
            }

        } catch (Exception e) {
            logger.error("❌ 批量删除失败", e);
            result.put("success", false);
            result.put("message", "删除失败：" + e.getMessage());
            result.put("successCount", 0);
            result.put("failCount", idList.size());
        }

        return result;
    }

    /**
     * 根据ID列表查询记录（用于导出选中记录）
     */
    public List<PaymentRecord> findByIds(String recordIds) {
        if (recordIds == null || recordIds.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String[] ids = recordIds.split(",");
        List<String> idList = new ArrayList<>();
        for (String id : ids) {
            String trimmedId = id.trim();
            if (!trimmedId.isEmpty()) {
                idList.add(trimmedId);
            }
        }

        if (idList.isEmpty()) {
            return new ArrayList<>();
        }

        logger.info("📥 根据ID列表查询记录，共 {} 个ID", idList.size());

        try {
            List<PaymentRecord> records = PaymentRecordDao.findByIds(idList);
            logger.info("✅ 查询成功，找到 {} 条记录", records.size());
            return records;
        } catch (Exception e) {
            logger.error("❌ 根据ID列表查询失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 业主端：根据ID查询详情
     */
    public PaymentRecord getDetailByIdForOwner(Integer recordId, String ownerId) {
        return PaymentRecordDao.getDetailByIdForOwner(String.valueOf(recordId), ownerId);
    }

    // ==========================================
    // 🔧 辅助方法：从 Session 获取用户信息
    // ==========================================

    /**
     * 从 Session 获取当前用户ID
     */
    private Integer getUserId(HttpServletRequest request) {
        if (request == null) return 0;
        Object userId = request.getSession().getAttribute("userId");
        return userId != null ? (Integer) userId : 0;
    }

    /**
     * 从 Session 获取当前用户名
     */
    private String getUsername(HttpServletRequest request) {
        if (request == null) return "system";
        Object username = request.getSession().getAttribute("username");
        return username != null ? username.toString() : "system";
    }
}

