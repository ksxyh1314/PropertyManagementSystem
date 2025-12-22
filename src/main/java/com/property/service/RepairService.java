package com.property.service;

import com.property.dao.RepairRecordDao;
import com.property.entity.RepairRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报修服务类
 */
public class RepairService {
    private static final Logger logger = LoggerFactory.getLogger(RepairService.class);
    private RepairRecordDao repairRecordDao = new RepairRecordDao();

    /**
     * 根据ID查询报修记录
     */
    public RepairRecord findById(Integer repairId) {
        if (repairId == null) {
            throw new IllegalArgumentException("报修ID不能为空");
        }
        return repairRecordDao.findById(repairId);
    }

    /**
     * 查询所有报修记录
     */
    public List<RepairRecord> findAll() {
        return repairRecordDao.findAll();
    }

    /**
     * 分页查询报修记录
     */
    public Map<String, Object> findByPage(int pageNum, int pageSize, String keyword, String status) {
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1) pageSize = 10;

        List<RepairRecord> list = repairRecordDao.findByPage(pageNum, pageSize, keyword, status);
        long total = repairRecordDao.count(keyword, status);
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
     * 根据业主ID查询报修记录
     */
    public List<RepairRecord> findByOwnerId(String ownerId) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("业主ID不能为空");
        }
        return repairRecordDao.findByOwnerId(ownerId);
    }

    /**
     * 查询待处理报修
     */
    public List<RepairRecord> findPendingRepairs() {
        return repairRecordDao.findPendingRepairs();
    }

    /**
     * 提交报修
     */
    public Integer submitRepair(RepairRecord record) {
        // 参数验证
        validateRepairRecord(record);

        // 设置默认值
        if (record.getRepairStatus() == null || record.getRepairStatus().trim().isEmpty()) {
            record.setRepairStatus("pending");
        }
        if (record.getPriority() == null || record.getPriority().trim().isEmpty()) {
            record.setPriority("normal");
        }

        Integer repairId = repairRecordDao.insert(record);
        if (repairId != null) {
            logger.info("提交报修成功：业主={}, 类型={}", record.getOwnerId(), record.getRepairType());
        }
        return repairId;
    }

    /**
     * 更新报修记录
     */
    public boolean updateRepair(RepairRecord record) {
        if (record.getRepairId() == null) {
            throw new IllegalArgumentException("报修ID不能为空");
        }

        // 检查记录是否存在
        RepairRecord existRecord = repairRecordDao.findById(record.getRepairId());
        if (existRecord == null) {
            throw new IllegalArgumentException("报修记录不存在");
        }

        int rows = repairRecordDao.update(record);
        if (rows > 0) {
            logger.info("更新报修记录成功：报修ID={}", record.getRepairId());
            return true;
        }
        return false;
    }

    /**
     * 受理报修
     */
    public boolean acceptRepair(Integer repairId, String handler, String handlerPhone) {
        if (repairId == null) {
            throw new IllegalArgumentException("报修ID不能为空");
        }
        if (handler == null || handler.trim().isEmpty()) {
            throw new IllegalArgumentException("处理人不能为空");
        }
        if (handlerPhone == null || handlerPhone.trim().isEmpty()) {
            throw new IllegalArgumentException("处理人电话不能为空");
        }

        // 验证手机号
        if (!handlerPhone.matches("^1[3-9]\\d{9}$")) {
            throw new IllegalArgumentException("手机号格式不正确");
        }

        int rows = repairRecordDao.acceptRepair(repairId, handler, handlerPhone);
        if (rows > 0) {
            logger.info("受理报修成功：报修ID={}, 处理人={}", repairId, handler);
            return true;
        }
        return false;
    }

    /**
     * 完成报修
     */
    public boolean completeRepair(Integer repairId, String repairResult) {
        if (repairId == null) {
            throw new IllegalArgumentException("报修ID不能为空");
        }
        if (repairResult == null || repairResult.trim().isEmpty()) {
            throw new IllegalArgumentException("处理结果不能为空");
        }

        int rows = repairRecordDao.completeRepair(repairId, repairResult);
        if (rows > 0) {
            logger.info("完成报修成功：报修ID={}", repairId);
            return true;
        }
        return false;
    }

    /**
     * 评价报修
     */
    public boolean rateRepair(Integer repairId, Integer rating, String feedback) {
        if (repairId == null) {
            throw new IllegalArgumentException("报修ID不能为空");
        }
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("评分必须在1-5之间");
        }

        int rows = repairRecordDao.rateRepair(repairId, rating, feedback);
        if (rows > 0) {
            logger.info("评价报修成功：报修ID={}, 评分={}", repairId, rating);
            return true;
        }
        return false;
    }

    /**
     * 删除报修记录
     */
    public boolean deleteRepair(Integer repairId) {
        if (repairId == null) {
            throw new IllegalArgumentException("报修ID不能为空");
        }

        int rows = repairRecordDao.delete(repairId);
        if (rows > 0) {
            logger.info("删除报修记录成功：报修ID={}", repairId);
            return true;
        }
        return false;
    }

    /**
     * 统计各状态报修数量
     */
    public Map<String, Long> countByStatus() {
        return repairRecordDao.countByStatus();
    }

    /**
     * 验证报修记录信息
     */
    private void validateRepairRecord(RepairRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("报修记录信息不能为空");
        }
        if (record.getOwnerId() == null || record.getOwnerId().trim().isEmpty()) {
            throw new IllegalArgumentException("业主ID不能为空");
        }
        if (record.getHouseId() == null || record.getHouseId().trim().isEmpty()) {
            throw new IllegalArgumentException("房屋ID不能为空");
        }
        if (record.getRepairType() == null || record.getRepairType().trim().isEmpty()) {
            throw new IllegalArgumentException("报修类型不能为空");
        }
        if (record.getDescription() == null || record.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("问题描述不能为空");
        }

        // 验证报修类型
        if (!record.getRepairType().matches("^(plumbing|electrical|door_window|public_facility|other)$")) {
            throw new IllegalArgumentException("报修类型无效");
        }

        // 验证优先级
        if (record.getPriority() != null && !record.getPriority().trim().isEmpty()) {
            if (!record.getPriority().matches("^(normal|urgent|emergency)$")) {
                throw new IllegalArgumentException("优先级无效");
            }
        }
    }
}
