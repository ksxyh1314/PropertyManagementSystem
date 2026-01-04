package com.property.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.util.Date;

/**
 * 报修记录实体类
 * 对应数据库表：repair_records
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepairRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 报修ID（自增主键）
     */
    private Integer repairId;

    /**
     * 业主ID
     */
    private String ownerId;

    /**
     * 房屋编号
     */
    private String houseId;

    /**
     * 报修类型：plumbing-水暖, electrical-电路, door_window-门窗, public_facility-公共设施, other-其他
     */
    private String repairType;

    /**
     * 问题描述
     */
    private String description;

    /**
     * 报修状态：pending-待处理, processing-处理中, completed-已完成, cancelled-已取消
     */
    private String repairStatus;

    /**
     * 优先级：normal-普通, urgent-紧急, emergency-特急
     */
    private String priority;

    /**
     * 提交时间
     */
    private Date submitTime;

    /**
     * 受理时间
     */
    private Date acceptTime;

    /**
     * 完成时间
     */
    private Date completeTime;

    /**
     * 处理人
     */
    private String handler;

    /**
     * 处理人电话
     */
    private String handlerPhone;

    /**
     * 处理结果
     */
    private String repairResult;

    /**
     * 满意度评分（1-5分）
     * ⚠️ 修改：从 Integer 改为 Short，匹配 SQL Server TINYINT 类型
     */
    private Short satisfactionRating;  // ✅ 改为 Short

    /**
     * 反馈意见
     */
    private String feedback;

    // 关联查询字段（非数据库字段）
    /**
     * 业主姓名
     */
    private String ownerName;

    /**
     * 业主电话
     */
    private String ownerPhone;

    /**
     * 获取报修类型显示名称
     */
    public String getRepairTypeDisplay() {
        if (repairType == null) {
            return "未知";
        }
        switch (repairType) {
            case "plumbing":
                return "水暖";
            case "electrical":
                return "电路";
            case "door_window":
                return "门窗";
            case "public_facility":
                return "公共设施";
            case "other":
                return "其他";
            default:
                return "未知";
        }
    }

    /**
     * 获取报修状态显示名称
     */
    public String getRepairStatusDisplay() {
        if (repairStatus == null) {
            return "未知";
        }
        switch (repairStatus) {
            case "pending":
                return "待处理";
            case "processing":
                return "处理中";
            case "completed":
                return "已完成";
            case "cancelled":
                return "已取消";
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
            case "urgent":
                return "紧急";
            case "emergency":
                return "特急";
            default:
                return "普通";
        }
    }

    /**
     * 判断是否已完成
     */
    public boolean isCompleted() {
        return "completed".equals(this.repairStatus);
    }

    /**
     * 判断是否待处理
     */
    public boolean isPending() {
        return "pending".equals(this.repairStatus);
    }

    /**
     * 计算处理时长（小时）
     */
    public Long getProcessingHours() {
        if (submitTime != null && completeTime != null) {
            long diff = completeTime.getTime() - submitTime.getTime();
            return diff / (1000 * 60 * 60);
        }
        return null;
    }

    /**
     * 获取满意度评分（转为 Integer 供前端使用）
     * 兼容方法：前端可能需要 Integer 类型
     */
    public Integer getSatisfactionRatingAsInt() {
        return satisfactionRating != null ? satisfactionRating.intValue() : null;
    }

    /**
     * 设置满意度评分（接受 Integer 参数）
     * 兼容方法：前端可能传递 Integer 类型
     */
    public void setSatisfactionRatingFromInt(Integer rating) {
        this.satisfactionRating = rating != null ? rating.shortValue() : null;
    }
}
