package com.property.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 缴费记录实体类
 * 对应数据库表：payment_records
 *
 * @author PropertyManagementSystem
 * @version 3.0 - 完全匹配数据库字段名
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID（主键）
     * 数据库类型: int，但业务层使用 String
     */
    private String recordId;

    /**
     * 业主ID
     */
    private String ownerId;

    /**
     * 房屋编号
     */
    private String houseId;

    /**
     * 收费项目编号
     */
    private String itemId;

    /**
     * 账期（格式: YYYY-MM，如"2025-12"）
     * ✅ 对应数据库字段: billing_period
     */
    private String billingPeriod;  // ✅ 修改：billingMonth → billingPeriod

    /**
     * 应缴金额
     */
    private BigDecimal amount;

    /**
     * 滞纳金
     */
    private BigDecimal lateFee;

    /**
     * 总金额（应缴金额 + 滞纳金）
     */
    private BigDecimal totalAmount;

    /**
     * 缴费截止日期
     */
    private Date dueDate;

    /**
     * 实际缴费日期
     */
    private Date paymentDate;

    /**
     * 缴费方式
     */
    private String paymentMethod;

    /**
     * 缴费状态
     */
    private String paymentStatus;

    /**
     * 收据号
     */
    private String receiptNo;

    /**
     * 操作员ID
     */
    private Integer operatorId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     * ✅ 对应数据库字段: create_time
     */
    private Date createTime;  // ✅ 修改：createdAt → createTime

    /**
     * 更新时间
     * ✅ 对应数据库字段: update_time
     */
    private Date updateTime;  // ✅ 修改：updatedAt → updateTime

    // ==================== 关联查询字段（非数据库字段）====================

    private String ownerName;
    private String ownerPhone;
    private String buildingNo;
    private String unitNo;
    private String floor;
    private String roomNo;
    private String fullHouseNo;
    private String itemName;
    private BigDecimal itemPrice;
    private String calculationType;
    private BigDecimal houseArea;
    private Integer overdueDays;

    // ==================== 业务方法 ====================

    public String getPaymentStatusDisplay() {
        if (paymentStatus == null) return "未知";
        switch (paymentStatus) {
            case "unpaid": return "未缴费";
            case "paid": return "已缴费";
            case "overdue": return "逾期";
            case "partial": return "部分缴费";
            default: return "未知";
        }
    }

    public String getPaymentStatusClass() {
        if (paymentStatus == null) return "status-unknown";
        switch (paymentStatus) {
            case "unpaid": return "status-unpaid";
            case "paid": return "status-paid";
            case "overdue": return "status-overdue";
            case "partial": return "status-partial";
            default: return "status-unknown";
        }
    }

    public String getPaymentMethodDisplay() {
        if (paymentMethod == null) return "未支付";
        switch (paymentMethod) {
            case "cash": return "现金";
            case "wechat": return "微信支付";
            case "alipay": return "支付宝";
            case "bank_transfer": return "银行转账";
            case "online": return "在线支付";
            default: return "其他";
        }
    }

    public boolean isPaid() {
        return "paid".equals(this.paymentStatus);
    }

    public boolean isOverdue() {
        if (isPaid()) return false;
        return "overdue".equals(this.paymentStatus) || calculateOverdueDays() > 0;
    }

    public boolean isUnpaid() {
        return "unpaid".equals(this.paymentStatus);
    }

    public int calculateOverdueDays() {
        if (dueDate == null || isPaid()) return 0;
        Date now = new Date();
        if (now.after(dueDate)) {
            long diff = now.getTime() - dueDate.getTime();
            return (int) (diff / (1000 * 60 * 60 * 24));
        }
        return 0;
    }

    public boolean isWithinGracePeriod(int gracePeriod) {
        int days = calculateOverdueDays();
        return days > 0 && days <= gracePeriod;
    }

    public BigDecimal calculateTotalAmount() {
        BigDecimal amt = amount != null ? amount : BigDecimal.ZERO;
        BigDecimal late = lateFee != null ? lateFee : BigDecimal.ZERO;
        return amt.add(late);
    }

    /**
     * 获取账期显示格式（如：2025年12月）
     * ✅ 方法名改为 getBillingPeriodDisplay
     */
    public String getBillingPeriodDisplay() {  // ✅ 修改方法名
        if (billingPeriod == null || billingPeriod.length() < 7) {
            return billingPeriod;
        }
        try {
            String[] parts = billingPeriod.split("-");
            return parts[0] + "年" + Integer.parseInt(parts[1]) + "月";
        } catch (Exception e) {
            return billingPeriod;
        }
    }

    /**
     * ✅ 兼容旧方法名（如果 JSP 中使用了 billingMonth）
     */
    @Deprecated
    public String getBillingMonth() {
        return this.billingPeriod;
    }

    @Deprecated
    public void setBillingMonth(String billingMonth) {
        this.billingPeriod = billingMonth;
    }

    @Deprecated
    public String getBillingMonthDisplay() {
        return getBillingPeriodDisplay();
    }

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (buildingNo != null) sb.append(buildingNo).append("栋");
        if (unitNo != null) sb.append(unitNo).append("单元");
        if (floor != null) sb.append(floor).append("层");
        if (roomNo != null) sb.append(roomNo).append("室");
        return sb.length() > 0 ? sb.toString() : houseId;
    }

    public boolean shouldCalculateLateFee(int gracePeriod) {
        return !isPaid() && calculateOverdueDays() > gracePeriod;
    }

    public int getRemainingDays() {
        if (dueDate == null || isPaid()) return 0;
        Date now = new Date();
        long diff = dueDate.getTime() - now.getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    public boolean isDueSoon() {
        if (isPaid()) return false;
        int remaining = getRemainingDays();
        return remaining > 0 && remaining <= 7;
    }
}
