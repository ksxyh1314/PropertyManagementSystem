package com.property.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.util.Date;

/**
 * 公告实体类
 * 对应数据库表：announcements
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 公告ID（自增主键）
     */
    private Integer announcementId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 公告类型：notice-通知, payment_reminder-缴费提醒, maintenance-维护, emergency-紧急
     */
    private String announcementType;

    /**
     * 优先级：normal-普通, important-重要, urgent-紧急
     */
    private String priority;

    /**
     * 发布人ID
     */
    private Integer publisherId;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 过期时间
     */
    private Date expiryTime;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 状态：1-发布, 0-撤回
     */
    private Integer status;

    // 关联查询字段（非数据库字段）
    /**
     * 发布人姓名
     */
    private String publisherName;

    /**
     * 获取公告类型显示名称
     */
    public String getAnnouncementTypeDisplay() {
        if (announcementType == null) {
            return "未知";
        }
        switch (announcementType) {
            case "notice":
                return "通知公告";
            case "payment_reminder":
                return "缴费提醒";
            case "maintenance":
                return "维护通知";
            case "emergency":
                return "紧急公告";
            default:
                return "未知";
        }
    }

    /**
     * 获取优先级显示名称
     */
    public String getPriorityDisplay() {
        if (priority == null) {
            return "普通";
        }
        switch (priority) {
            case "normal":
                return "普通";
            case "important":
                return "重要";
            case "urgent":
                return "紧急";
            default:
                return "普通";
        }
    }

    /**
     * 判断是否已发布
     */
    public boolean isPublished() {
        return Integer.valueOf(1).equals(this.status);
    }

    /**
     * 判断是否已过期
     */
    public boolean isExpired() {
        if (expiryTime == null) {
            return false;
        }
        return new Date().after(expiryTime);
    }

    /**
     * 判断是否有效
     */
    public boolean isValid() {
        return isPublished() && !isExpired();
    }
}
