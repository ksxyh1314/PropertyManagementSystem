package com.property.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 房屋实体类
 * 对应数据库表：houses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class House implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 房屋编号（如"1栋2单元301"）
     */
    private String houseId;

    /**
     * 楼栋号（2位数字）
     */
    private String buildingNo;

    /**
     * 单元号（1位数字）
     */
    private String unitNo;

    /**
     * 楼层（2位数字）
     */
    private String floor;

    /**
     * 户型（如"两室一厅"）
     */
    private String layout;

    /**
     * 建筑面积（大于0，保留1位小数）
     */
    private BigDecimal area;

    /**
     * 物业费单价（按平方米计算）
     */
    private BigDecimal pricePerSqm;

    /**
     * 房屋状态：vacant-空置, occupied-已入住, rented-出租
     */
    private String houseStatus;

    /**
     * 销售状态：for_sale-待售, sold-已售, leased-已租
     */
    private String saleStatus;

    /**
     * 业主ID
     */
    private String ownerId;

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
     * 业主信息（关联查询）
     */
    private Owner owner;

    /**
     * 获取房屋状态显示名称
     */
    public String getHouseStatusDisplay() {
        if (houseStatus == null) {
            return "未知";
        }
        switch (houseStatus) {
            case "vacant":
                return "空置";
            case "occupied":
                return "已入住";
            case "rented":
                return "出租";
            default:
                return "未知";
        }
    }

    /**
     * 获取销售状态显示名称
     */
    public String getSaleStatusDisplay() {
        if (saleStatus == null) {
            return "未知";
        }
        switch (saleStatus) {
            case "for_sale":
                return "待售";
            case "sold":
                return "已售";
            case "leased":
                return "已租";
            default:
                return "未知";
        }
    }

    /**
     * 计算季度物业费（面积 × 单价 × 3个月）
     */
    public BigDecimal calculateQuarterlyFee() {
        if (area != null && pricePerSqm != null) {
            return area.multiply(pricePerSqm).multiply(new BigDecimal("3"));
        }
        return BigDecimal.ZERO;
    }

    /**
     * 计算月度物业费
     */
    public BigDecimal calculateMonthlyFee() {
        if (area != null && pricePerSqm != null) {
            return area.multiply(pricePerSqm);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 计算年度物业费
     */
    public BigDecimal calculateYearlyFee() {
        if (area != null && pricePerSqm != null) {
            return area.multiply(pricePerSqm).multiply(new BigDecimal("12"));
        }
        return BigDecimal.ZERO;
    }

    /**
     * 判断是否已分配业主
     */
    public boolean hasOwner() {
        return ownerId != null && !ownerId.trim().isEmpty();
    }
}
