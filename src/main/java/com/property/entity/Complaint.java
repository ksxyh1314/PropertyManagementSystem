package com.property.entity;

import java.time.LocalDateTime;

/**
 * 投诉实体类
 */
public class Complaint {

    /** 投诉ID */
    private Integer complaintId;

    /** 业主ID */
    private String ownerId;

    /** 业主姓名 */
    private String ownerName;

    /** 业主电话 */
    private String ownerPhone;

    /** 房屋ID */
    private String houseId;

    /** 楼栋号 */
    private String buildingNo;

    /** 单元号 */
    private String unitNo;

    /** 楼层 */
    private String floor;

    /** 投诉类型（service=服务, environment=环境, facility=设施, fee=费用, other=其他） */
    private String complaintType;

    /** 投诉类型名称 */
    private String complaintTypeName;

    /** 标题 */
    private String title;

    /** 内容 */
    private String content;

    /** 是否匿名（0=否，1=是） */
    private Integer isAnonymous;

    /** 投诉状态（pending=待处理, processing=处理中, resolved=已解决, closed=已关闭） */
    private String complaintStatus;

    /** 投诉状态名称 */
    private String complaintStatusName;

    /** 提交时间 */
    private LocalDateTime submitTime;

    /** 处理人ID */
    private Integer handlerId;

    /** 处理人姓名 */
    private String handlerName;

    /** 处理人电话 */
    private String handlerPhone;

    /** 回复内容 */
    private String reply;

    /** 回复时间 */
    private LocalDateTime replyTime;

    /** 响应时长（小时） */
    private Integer responseHours;

    // 构造方法
    public Complaint() {}

    // Getter 和 Setter 方法
    public Integer getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Integer complaintId) {
        this.complaintId = complaintId;
    }

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

    public String getComplaintType() {
        return complaintType;
    }

    public void setComplaintType(String complaintType) {
        this.complaintType = complaintType;
    }

    public String getComplaintTypeName() {
        return complaintTypeName;
    }

    public void setComplaintTypeName(String complaintTypeName) {
        this.complaintTypeName = complaintTypeName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(Integer isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public String getComplaintStatus() {
        return complaintStatus;
    }

    public void setComplaintStatus(String complaintStatus) {
        this.complaintStatus = complaintStatus;
    }

    public String getComplaintStatusName() {
        return complaintStatusName;
    }

    public void setComplaintStatusName(String complaintStatusName) {
        this.complaintStatusName = complaintStatusName;
    }

    public LocalDateTime getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(LocalDateTime submitTime) {
        this.submitTime = submitTime;
    }

    public Integer getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(Integer handlerId) {
        this.handlerId = handlerId;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public String getHandlerPhone() {
        return handlerPhone;
    }

    public void setHandlerPhone(String handlerPhone) {
        this.handlerPhone = handlerPhone;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public LocalDateTime getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(LocalDateTime replyTime) {
        this.replyTime = replyTime;
    }

    public Integer getResponseHours() {
        return responseHours;
    }

    public void setResponseHours(Integer responseHours) {
        this.responseHours = responseHours;
    }

    @Override
    public String toString() {
        return "Complaint{" +
                "complaintId=" + complaintId +
                ", ownerId='" + ownerId + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", title='" + title + '\'' +
                ", complaintType='" + complaintType + '\'' +
                ", complaintStatus='" + complaintStatus + '\'' +
                ", submitTime=" + submitTime +
                '}';
    }
}
