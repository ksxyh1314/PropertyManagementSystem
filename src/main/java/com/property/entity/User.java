package com.property.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类
 * 对应数据库表：users
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（自增主键）
     */
    private Integer userId;

    /**
     * 用户名（唯一）
     */
    private String username;

    /**
     * 密码（MD5加密）
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 用户角色：admin-管理员, owner-业主, finance-财务
     */
    private String userRole;

    /**
     * 手机号码（11位）
     */
    private String phone;

    /**
     * 身份证号（18位）
     */
    private String idCard;

    /**
     * 状态：1-启用, 0-禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后登录时间
     */
    private Date lastLogin;

    /**
     * 判断是否为管理员
     */
    public boolean isAdmin() {
        return "admin".equals(this.userRole);
    }

    /**
     * 判断是否为业主
     */
    public boolean isOwner() {
        return "owner".equals(this.userRole);
    }

    /**
     * 判断是否为财务人员
     */
    public boolean isFinance() {
        return "finance".equals(this.userRole);
    }

    /**
     * 判断账号是否启用
     */
    public boolean isActive() {
        return Integer.valueOf(1).equals(this.status);
    }

    /**
     * 获取角色显示名称
     */
    public String getRoleDisplayName() {
        if (userRole == null) {
            return "未知";
        }
        switch (userRole) {
            case "admin":
                return "管理员";
            case "owner":
                return "业主";
            case "finance":
                return "财务";
            default:
                return "未知";
        }
    }

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
}
