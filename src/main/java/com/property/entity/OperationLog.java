package com.property.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志实体类
 * 对应数据库表：operation_logs
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLog implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 日志ID（自增主键）
     */
    private Long logId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 操作描述
     */
    private String operationDesc;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 操作时间
     */
    private Date operationTime;
}
