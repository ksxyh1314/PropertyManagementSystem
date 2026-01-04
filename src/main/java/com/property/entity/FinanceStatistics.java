package com.property.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 财务统计实体类
 *
 * @author PropertyManagementSystem
 * @version 1.0
 */
public class FinanceStatistics implements Serializable {
    private static final long serialVersionUID = 1L;

    // ========== 欠费业主信息 ==========
    private String ownerId;              // 业主ID
    private String ownerName;            // 业主姓名
    private String ownerPhone;           // 业主电话
    private String houseId;              // 房屋编号
    private String buildingNo;           // 楼栋号
    private String unitNo;               // 单元号
    private String floor;                // 楼层

    // ========== 欠费统计 ==========
    private Integer unpaidCount;         // 未缴费笔数
    private BigDecimal unpaidAmount;     // 未缴金额
    private BigDecimal totalLateFee;     // 滞纳金总额
    private BigDecimal totalArrears;     // 欠费总额（本金+滞纳金）
    private Date earliestDueDate;        // 最早欠费日期
    private Integer maxOverdueDays;      // 最大逾期天数

    // ========== 逾期统计 ==========
    private Long overdueCount;           // 逾期账单数
    private BigDecimal overdueAmount;    // 逾期金额
    private Double avgOverdueDays;       // 平均逾期天数

    // ========== 收入统计 ==========
    private Long totalRecords;           // 总记录数
    private Long paidCount;              // 已缴费数
    private BigDecimal totalAmount;      // 应收总额
    private BigDecimal paidAmount;       // 实收总额
    private BigDecimal collectedLateFee; // 已收滞纳金
    private BigDecimal collectionRate;   // 收缴率

    // ========== 构造方法 ==========
    public FinanceStatistics() {
    }

    // ========== Getter & Setter ==========

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public String getBuildingNo() {
        return buildingNo;
    }

    public void setBuildingNo(String buildingNo) {
        this.buildingNo = buildingNo;
    }

    public String getUnitNo() {
        return unitNo;
    }

    public void setUnitNo(String unitNo) {
        this.unitNo = unitNo;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public Integer getUnpaidCount() {
        return unpaidCount;
    }

    public void setUnpaidCount(Integer unpaidCount) {
        this.unpaidCount = unpaidCount;
    }

    public BigDecimal getUnpaidAmount() {
        return unpaidAmount;
    }

    public void setUnpaidAmount(BigDecimal unpaidAmount) {
        this.unpaidAmount = unpaidAmount;
    }

    public BigDecimal getTotalLateFee() {
        return totalLateFee;
    }

    public void setTotalLateFee(BigDecimal totalLateFee) {
        this.totalLateFee = totalLateFee;
    }

    public BigDecimal getTotalArrears() {
        return totalArrears;
    }

    public void setTotalArrears(BigDecimal totalArrears) {
        this.totalArrears = totalArrears;
    }

    public Date getEarliestDueDate() {
        return earliestDueDate;
    }

    public void setEarliestDueDate(Date earliestDueDate) {
        this.earliestDueDate = earliestDueDate;
    }

    public Integer getMaxOverdueDays() {
        return maxOverdueDays;
    }

    public void setMaxOverdueDays(Integer maxOverdueDays) {
        this.maxOverdueDays = maxOverdueDays;
    }

    public Long getOverdueCount() {
        return overdueCount;
    }

    public void setOverdueCount(Long overdueCount) {
        this.overdueCount = overdueCount;
    }

    public BigDecimal getOverdueAmount() {
        return overdueAmount;
    }

    public void setOverdueAmount(BigDecimal overdueAmount) {
        this.overdueAmount = overdueAmount;
    }

    public Double getAvgOverdueDays() {
        return avgOverdueDays;
    }

    public void setAvgOverdueDays(Double avgOverdueDays) {
        this.avgOverdueDays = avgOverdueDays;
    }

    public Long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Long getPaidCount() {
        return paidCount;
    }

    public void setPaidCount(Long paidCount) {
        this.paidCount = paidCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getCollectedLateFee() {
        return collectedLateFee;
    }

    public void setCollectedLateFee(BigDecimal collectedLateFee) {
        this.collectedLateFee = collectedLateFee;
    }

    public BigDecimal getCollectionRate() {
        return collectionRate;
    }

    public void setCollectionRate(BigDecimal collectionRate) {
        this.collectionRate = collectionRate;
    }

    @Override
    public String toString() {
        return "FinanceStatistics{" +
                "ownerId='" + ownerId + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", ownerPhone='" + ownerPhone + '\'' +
                ", houseId='" + houseId + '\'' +
                ", unpaidCount=" + unpaidCount +
                ", totalArrears=" + totalArrears +
                ", maxOverdueDays=" + maxOverdueDays +
                '}';
    }
}
