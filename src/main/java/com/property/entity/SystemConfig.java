package com.property.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.util.Date;

/**
 * 系统配置实体类
 * 对应数据库表：system_config
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 配置ID（自增主键）
     */
    private Integer configId;

    /**
     * 配置键
     */
    private String configKey;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 配置描述
     */
    private String configDesc;

    /**
     * 更新时间
     */
    private Date updateTime;
}
