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
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID（自增主键）
     */
    private Integer recordId;

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
     * 缴费期限（如"2025年第1季度"）
     */
    private String billingPeriod;

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
     * 缴费期限
     */
    private Date dueDate;

    /**
     * 缴费日期
     */
    private Date paymentDate;

    /**
     * 缴费方式：cash-现金, wechat-微信, alipay-支付宝, bank_transfer-银行转账, online-在线支付
     */
    private String paymentMethod;

    /**
     * 缴费状态：unpaid-未缴费, paid-已缴费, overdue-逾期, partial-部分缴费
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
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    // 关联查询字段（非数据库字段）
    /**
     * 业主姓名
     */
    private String ownerName;

    /**
     * 楼栋号
     */
    private String buildingNo;

    /**
     * 单元号
     */
    private String unitNo;

    /**
     * 楼层
     */
    private String floor;

    /**
     * 收费项目名称
     */
    private String itemName;

    /**
     * 逾期天数
     */
    private Integer overdueDays;

    /**
     * 获取缴费状态显示名称
     */
    public String getPaymentStatusDisplay() {
        if (paymentStatus == null) {
            return "未知";
        }
        switch (paymentStatus) {
            case "unpaid":
                return "未缴费";
            case "paid":
                return "已缴费";
            case "overdue":
                return "逾期";
            case "partial":
                return "部分缴费";
            default:
                return "未知";
        }
    }

    /**
     * 获取缴费方式显示名称
     */
    public String getPaymentMethodDisplay() {
        if (paymentMethod == null) {
            return "未支付";
        }
        switch (paymentMethod) {
            case "cash":
                return "现金";
            case "wechat":
                return "微信";
            case "alipay":
                return "支付宝";
            case "bank_transfer":
                return "银行转账";
            case "online":
                return "在线支付";
            default:
                return "未知";
        }
    }

    /**
     * 判断是否已缴费
     */
    public boolean isPaid() {
        return "paid".equals(this.paymentStatus);
    }

    /**
     * 判断是否逾期
     */
    public boolean isOverdue() {
        return "overdue".equals(this.paymentStatus);
    }

    /**
     * 判断是否未缴费
     */
    public boolean isUnpaid() {
        return "unpaid".equals(this.paymentStatus);
    }

    /**
     * 计算逾期天数
     */
    public int calculateOverdueDays() {
        if (dueDate == null || isPaid()) {
            return 0;
        }
        Date now = new Date();
        if (now.after(dueDate)) {
            long diff = now.getTime() - dueDate.getTime();
            return (int) (diff / (1000 * 60 * 60 * 24));
        }
        return 0;
    }

    /**
     * 判断是否在宽限期内
     */
    public boolean isWithinGracePeriod(int gracePeriod) {
        int days = calculateOverdueDays();
        return days > 0 && days <= gracePeriod;
    }
}
