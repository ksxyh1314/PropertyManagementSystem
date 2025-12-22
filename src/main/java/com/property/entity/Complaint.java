package com.property.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.util.Date;

/**
 * 投诉建议实体类
 * 对应数据库表：complaints
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Complaint implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 投诉ID（自增主键）
     */
    private Integer complaintId;

    /**
     * 业主ID
     */
    private String ownerId;

    /**
     * 投诉类型：service-服务, environment-环境, facility-设施, fee-收费, other-其他
     */
    private String complaintType;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 是否匿名：0-否, 1-是
     */
    private Integer isAnonymous;

    /**
     * 投诉状态：pending-待处理, processing-处理中, resolved-已解决, closed-已关闭
     */
    private String complaintStatus;

    /**
     * 提交时间
     */
    private Date submitTime;

    /**
     * 处理人ID
     */
    private Integer handlerId;

    /**
     * 回复内容
     */
    private String reply;

    /**
     * 回复时间
     */
    private Date replyTime;

    // 关联查询字段（非数据库字段）
    /**
     * 业主姓名
     */
    private String ownerName;

    /**
     * 处理人姓名
     */
    private String handlerName;

    /**
     * 获取投诉类型显示名称
     */
    public String getComplaintTypeDisplay() {
        if (complaintType == null) {
            return "未知";
        }
        switch (complaintType) {
            case "service":
                return "服务投诉";
            case "environment":
                return "环境问题";
            case "facility":
                return "设施问题";
            case "fee":
                return "收费问题";
            case "other":
                return "其他";
            default:
                return "未知";
        }
    }

    /**
     * 获取投诉状态显示名称
     */
    public String getComplaintStatusDisplay() {
        if (complaintStatus == null) {
            return "未知";
        }
        switch (complaintStatus) {
            case "pending":
                return "待处理";
            case "processing":
                return "处理中";
            case "resolved":
                return "已解决";
            case "closed":
                return "已关闭";
            default:
                return "未知";
        }
    }

    /**
     * 判断是否匿名
     */
    public boolean isAnonymousComplaint() {
        return Integer.valueOf(1).equals(this.isAnonymous);
    }

    /**
     * 判断是否已处理
     */
    public boolean isResolved() {
        return "resolved".equals(this.complaintStatus);
    }

    /**
     * 判断是否待处理
     */
    public boolean isPending() {
        return "pending".equals(this.complaintStatus);
    }

    /**
     * 计算处理时长（小时）
     */
    public Long getProcessingHours() {
        if (submitTime != null && replyTime != null) {
            long diff = replyTime.getTime() - submitTime.getTime();
            return diff / (1000 * 60 * 60);
        }
        return null;
    }
}
