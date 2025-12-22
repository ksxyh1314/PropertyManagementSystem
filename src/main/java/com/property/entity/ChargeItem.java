package com.property.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 收费项目实体类
 * 对应数据库表：charge_items
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargeItem implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 项目编号（2位数字）
     */
    private String itemId;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 收费周期：monthly-月度, quarterly-季度, yearly-年度
     */
    private String chargeCycle;

    /**
     * 收费标准说明
     */
    private String description;

    /**
     * 计算类型：area_based-按面积, fixed-固定金额
     */
    private String calculationType;

    /**
     * 固定金额
     */
    private BigDecimal fixedAmount;

    /**
     * 计算公式
     */
    private String formula;

    /**
     * 宽限期（天数）
     */
    private Integer gracePeriod;

    /**
     * 滞纳金比例（日）
     */
    private BigDecimal lateFeeRate;

    /**
     * 状态：1-启用, 0-停用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 获取收费周期显示名称
     */
    public String getChargeCycleDisplay() {
        if (chargeCycle == null) {
            return "未知";
        }
        switch (chargeCycle) {
            case "monthly":
                return "月度";
            case "quarterly":
                return "季度";
            case "yearly":
                return "年度";
            default:
                return "未知";
        }
    }

    /**
     * 获取计算类型显示名称
     */
    public String getCalculationTypeDisplay() {
        if (calculationType == null) {
            return "未知";
        }
        switch (calculationType) {
            case "area_based":
                return "按面积计算";
            case "fixed":
                return "固定金额";
            default:
                return "未知";
        }
    }

    /**
     * 判断是否启用
     */
    public boolean isActive() {
        return Integer.valueOf(1).equals(this.status);
    }

    /**
     * 判断是否按面积计算
     */
    public boolean isAreaBased() {
        return "area_based".equals(this.calculationType);
    }

    /**
     * 判断是否固定金额
     */
    public boolean isFixedAmount() {
        return "fixed".equals(this.calculationType);
    }
}
