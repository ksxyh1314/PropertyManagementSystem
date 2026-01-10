package com.property.service;

import com.property.dao.OperationLogDao;
import com.property.entity.OperationLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作日志服务类
 */
public class OperationLogService {
    private static final Logger logger = LoggerFactory.getLogger(OperationLogService.class);
    private final OperationLogDao operationLogDao = new OperationLogDao();

    /**
     * 分页查询操作日志
     */
    public Map<String, Object> findByPage(int pageNum, int pageSize, String keyword,
                                          String operationType, String startDate, String endDate) {
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1) pageSize = 10;

        List<OperationLog> list = operationLogDao.findByPage(pageNum, pageSize, keyword,
                operationType, startDate, endDate);
        long total = operationLogDao.count(keyword, operationType, startDate, endDate);
        int totalPages = (int) Math.ceil((double) total / pageSize);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", totalPages);

        return result;
    }

    /**
     * 根据ID查询日志
     */
    public OperationLog findById(int logId) {
        return operationLogDao.findById(logId);
    }

    /**
     * 查询所有日志（用于导出）
     */
    public List<OperationLog> findAll(String keyword, String operationType,
                                      String startDate, String endDate) {
        return operationLogDao.findAll(keyword, operationType, startDate, endDate);
    }

    /**
     * 获取操作类型统计
     */
    public List<Map<String, Object>> getOperationTypeStats() {
        return operationLogDao.getOperationTypeStats();
    }

    /**
     * 获取用户操作统计
     */
    public List<Map<String, Object>> getUserOperationStats() {
        return operationLogDao.getUserOperationStats();
    }
}
