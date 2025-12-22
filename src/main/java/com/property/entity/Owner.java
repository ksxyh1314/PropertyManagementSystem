package com.property.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 业主实体类
 * 对应数据库表：owners
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Owner implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 业主ID（8位数字，前4位为楼栋号）
     */
    private String ownerId;

    /**
     * 业主姓名
     */
    private String ownerName;

    /**
     * 手机号码（11位）
     */
    private String phone;

    /**
     * 身份证号（18位，含X）
     */
    private String idCard;

    /**
     * 房屋编号
     */
    private String houseId;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 家庭成员数量
     */
    private Integer memberCount;

    /**
     * 登记日期
     */
    private Date registerDate;

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
     * 房屋信息（关联查询）
     */
    private House house;

    /**
     * 未缴费账单数量
     */
    private Integer unpaidCount;

    /**
     * 欠费总金额
     */
    private BigDecimal totalArrears;

    /**
     * 最早欠费日期
     */
    private Date earliestDueDate;

    /**
     * 脱敏手机号
     */
    public String getMaskedPhone() {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 脱敏身份证号
     */
    public String getMaskedIdCard() {
        if (idCard == null || idCard.length() != 18) {
            return idCard;
        }
        return idCard.substring(0, 6) + "********" + idCard.substring(14);
    }

    /**
     * 获取楼栋号（从业主ID前4位提取）
     */
    public String getBuildingNo() {
        if (ownerId != null && ownerId.length() >= 4) {
            return ownerId.substring(0, 4);
        }
        return null;
    }
}
