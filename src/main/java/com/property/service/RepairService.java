package com.property.service;

import com.property.dao.RepairRecordDao;
import com.property.entity.RepairRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepairService {
    private static final Logger logger = LoggerFactory.getLogger(RepairService.class);
    private RepairRecordDao repairRecordDao = new RepairRecordDao();

    // ==================== 原有方法（保持不变）====================

    /**
     * 根据ID查询
     */
    public RepairRecord findById(Integer repairId) {
        return repairRecordDao.findById(repairId);
    }

    /**
     * 分页查询
     */
    public Map<String, Object> findByPage(int pageNum, int pageSize, String keyword, String status) {
        List<RepairRecord> list = repairRecordDao.findByPage(pageNum, pageSize, keyword, status);
        long total = repairRecordDao.count(keyword, status);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);

        return result;
    }

    /**
     * 根据业主ID查询
     */
    public List<RepairRecord> findByOwnerId(String ownerId) {
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
        // 可以添加业务验证
        return repairRecordDao.insert(record);
    }

    /**
     * 受理报修
     */
    public boolean acceptRepair(Integer repairId, String handler, String handlerPhone) {
        // 验证状态
        RepairRecord record = repairRecordDao.findById(repairId);
        if (record == null) {
            throw new IllegalArgumentException("报修记录不存在");
        }
        if (!"pending".equals(record.getRepairStatus())) {
            throw new IllegalArgumentException("只能受理待处理状态的报修");
        }

        int result = repairRecordDao.acceptRepair(repairId, handler, handlerPhone);
        return result > 0;
    }

    /**
     * 完成报修
     */
    public boolean completeRepair(Integer repairId, String repairResult) {
        RepairRecord record = repairRecordDao.findById(repairId);
        if (record == null) {
            throw new IllegalArgumentException("报修记录不存在");
        }
        if (!"processing".equals(record.getRepairStatus())) {
            throw new IllegalArgumentException("只能完成处理中状态的报修");
        }

        int result = repairRecordDao.completeRepair(repairId, repairResult);
        return result > 0;
    }

    /**
     * 取消报修（无原因）
     */
    public boolean cancelRepair(Integer repairId) {
        RepairRecord record = repairRecordDao.findById(repairId);
        if (record == null) {
            throw new IllegalArgumentException("报修记录不存在");
        }

        String status = record.getRepairStatus();
        if (!"pending".equals(status) && !"processing".equals(status)) {
            throw new IllegalArgumentException("只能取消待处理或处理中的报修");
        }

        int result = repairRecordDao.cancelRepair(repairId);
        return result > 0;
    }

    /**
     * 取消报修（带原因）- 新增方法
     */
    public boolean cancelRepair(Integer repairId, String cancelReason) {
        RepairRecord record = repairRecordDao.findById(repairId);
        if (record == null) {
            throw new IllegalArgumentException("报修记录不存在");
        }

        String status = record.getRepairStatus();
        if (!"pending".equals(status) && !"processing".equals(status)) {
            throw new IllegalArgumentException("只能取消待处理或处理中的报修");
        }

        int result = repairRecordDao.cancelRepair(repairId, cancelReason);
        return result > 0;
    }

    /**
     * 评价报修
     */
    public boolean rateRepair(Integer repairId, Short rating, String feedback) {
        RepairRecord record = repairRecordDao.findById(repairId);
        if (record == null) {
            throw new IllegalArgumentException("报修记录不存在");
        }
        if (!"completed".equals(record.getRepairStatus())) {
            throw new IllegalArgumentException("只能评价已完成的报修");
        }
        if (record.getSatisfactionRating() != null) {
            throw new IllegalArgumentException("该报修已经评价过了");
        }

        int result = repairRecordDao.rateRepair(repairId, rating, feedback);
        return result > 0;
    }

    /**
     * 删除报修
     */
    public boolean deleteRepair(Integer repairId) {
        int result = repairRecordDao.delete(repairId);
        return result > 0;
    }

    /**
     * 统计各状态数量
     */
    public Map<String, Long> countByStatus() {
        return repairRecordDao.countByStatus();
    }

    /**
     * 根据状态获取报修数量
     * 🔥 修复：使用实例方法调用，而不是静态方法
     */
    public int getCountByStatus(String status) {
        try {
            return repairRecordDao.getCountByStatus(status);  // ✅ 使用实例对象调用
        } catch (Exception e) {
            logger.error("获取报修数量失败：status={}", status, e);
            return 0;
        }
    }

    // ==================== 🔥 新增：业主端方法 ====================

    /**
     * 🔥 业主端：分页查询我的报修记录（最终修复版）
     */
    public Map<String, Object> findByPageForOwner(int pageNum, int pageSize, String ownerId, String status) {
        // 🔥 修复：日志参数顺序
        logger.info("业主端查询报修记录：ownerId={}, pageNum={}, pageSize={}, status={}",
                ownerId, pageNum, pageSize, status);

        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("业主ID不能为空");
        }

        try {
            // 🔥 修复：DAO 调用参数顺序 (pageNum, pageSize, ownerId, status)
            List<RepairRecord> list = repairRecordDao.findByPageForOwner(pageNum, pageSize, ownerId, status);

            // 🔥 修复：统计方法参数顺序 (ownerId, status)
            long total = repairRecordDao.countByOwner(ownerId, status);

            Map<String, Object> result = new HashMap<>();
            result.put("list", list);
            result.put("total", total);
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);

            logger.info("查询成功：total={}, listSize={}", total, list.size());
            return result;

        } catch (Exception e) {
            logger.error("业主端查询报修记录失败", e);
            throw new RuntimeException("查询失败：" + e.getMessage());
        }
    }

    /**
     * 🔥 业主端：统计我的报修数量
     */
    public Map<String, Object> getOwnerRepairSummary(String ownerId) {
        logger.info("统计业主报修数据：ownerId={}", ownerId);

        Map<String, Object> summary = new HashMap<>();

        try {
            int totalCount = repairRecordDao.countByOwner(ownerId, null);
            int pendingCount = repairRecordDao.countByOwnerAndStatus(ownerId, "pending");
            int processingCount = repairRecordDao.countByOwnerAndStatus(ownerId, "processing");
            int completedCount = repairRecordDao.countByOwnerAndStatus(ownerId, "completed");
            int cancelledCount = repairRecordDao.countByOwnerAndStatus(ownerId, "cancelled");

            summary.put("totalCount", totalCount);
            summary.put("pendingCount", pendingCount);
            summary.put("processingCount", processingCount);
            summary.put("completedCount", completedCount);
            summary.put("cancelledCount", cancelledCount);

            logger.info("统计成功：total={}, pending={}, processing={}, completed={}",
                    totalCount, pendingCount, processingCount, completedCount);

        } catch (Exception e) {
            logger.error("统计业主报修数据失败", e);
            summary.put("totalCount", 0);
            summary.put("pendingCount", 0);
            summary.put("processingCount", 0);
            summary.put("completedCount", 0);
            summary.put("cancelledCount", 0);
        }

        return summary;
    }

    /**
     * 🔥 业主端：查询最近报修记录
     */
    public List<RepairRecord> findRecentByOwner(String ownerId, int limit) {
        logger.info("查询业主最近报修：ownerId={}, limit={}", ownerId, limit);

        try {
            return repairRecordDao.findRecentByOwner(ownerId, limit);
        } catch (Exception e) {
            logger.error("查询最近报修失败", e);
            return new java.util.ArrayList<>();
        }
    }

    /**
     * 🔥 业主端：业主取消报修（带权限验证）
     */
    public boolean cancelRepairByOwner(Integer repairId, String ownerId, String cancelReason) {
        logger.info("业主取消报修：repairId={}, ownerId={}", repairId, ownerId);

        try {
            // 验证报修记录是否存在
            RepairRecord record = repairRecordDao.findById(repairId);
            if (record == null) {
                throw new IllegalArgumentException("报修记录不存在");
            }

            // 验证是否是本人的报修
            if (!ownerId.equals(record.getOwnerId())) {
                throw new IllegalArgumentException("无权操作此报修记录");
            }

            // 验证状态
            String status = record.getRepairStatus();
            if (!"pending".equals(status)) {
                throw new IllegalArgumentException("只能取消待处理状态的报修");
            }

            // 执行取消
            int result = repairRecordDao.cancelRepair(repairId, "业主", cancelReason);

            logger.info("取消成功：repairId={}", repairId);
            return result > 0;

        } catch (Exception e) {
            logger.error("业主取消报修失败", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 🔥 业主端：追加报修说明
     */
    public boolean appendDescription(Integer repairId, String ownerId, String additionalDesc) {
        logger.info("追加报修说明：repairId={}, ownerId={}", repairId, ownerId);

        try {
            // 验证报修记录是否存在
            RepairRecord record = repairRecordDao.findById(repairId);
            if (record == null) {
                throw new IllegalArgumentException("报修记录不存在");
            }

            // 验证是否是本人的报修
            if (!ownerId.equals(record.getOwnerId())) {
                throw new IllegalArgumentException("无权操作此报修记录");
            }

            // 验证状态（只能在待处理或处理中时追加）
            String status = record.getRepairStatus();
            if (!"pending".equals(status) && !"processing".equals(status)) {
                throw new IllegalArgumentException("该报修已完成或取消，无法追加说明");
            }

            // 执行追加
            int result = repairRecordDao.appendDescription(repairId, additionalDesc);

            logger.info("追加成功：repairId={}", repairId);
            return result > 0;

        } catch (Exception e) {
            logger.error("追加报修说明失败", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 🔥 业主端：查询可评价的报修（已完成且未评价）
     */
    public List<RepairRecord> findRatableRepairs(String ownerId) {
        logger.info("查询可评价报修：ownerId={}", ownerId);

        try {
            return repairRecordDao.findRatableByOwner(ownerId);
        } catch (Exception e) {
            logger.error("查询可评价报修失败", e);
            return new java.util.ArrayList<>();
        }
    }

    /**
     * 🔥 业主端：业主评价报修（带权限验证）
     */
    public boolean rateRepairByOwner(Integer repairId, String ownerId, Short rating, String feedback) {
        logger.info("业主评价报修：repairId={}, ownerId={}, rating={}", repairId, ownerId, rating);

        try {
            // 验证报修记录是否存在
            RepairRecord record = repairRecordDao.findById(repairId);
            if (record == null) {
                throw new IllegalArgumentException("报修记录不存在");
            }

            // 验证是否是本人的报修
            if (!ownerId.equals(record.getOwnerId())) {
                throw new IllegalArgumentException("无权操作此报修记录");
            }

            // 验证状态
            if (!"completed".equals(record.getRepairStatus())) {
                throw new IllegalArgumentException("只能评价已完成的报修");
            }

            // 验证是否已评价
            if (record.getSatisfactionRating() != null) {
                throw new IllegalArgumentException("该报修已经评价过了");
            }

            // 验证评分范围
            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("评分必须在1-5之间");
            }

            // 执行评价
            int result = repairRecordDao.rateRepair(repairId, rating, feedback);

            logger.info("评价成功：repairId={}, rating={}", repairId, rating);
            return result > 0;

        } catch (Exception e) {
            logger.error("业主评价报修失败", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 🔥 业主端：获取报修详情（带权限验证）
     */
    public RepairRecord getRepairDetailForOwner(Integer repairId, String ownerId) {
        logger.info("业主查询报修详情：repairId={}, ownerId={}", repairId, ownerId);

        try {
            RepairRecord record = repairRecordDao.findById(repairId);

            if (record == null) {
                throw new IllegalArgumentException("报修记录不存在");
            }

            // 验证是否是本人的报修
            if (!ownerId.equals(record.getOwnerId())) {
                throw new IllegalArgumentException("无权查看此报修记录");
            }

            return record;

        } catch (Exception e) {
            logger.error("查询报修详情失败", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 🔥 获取报修统计数据（用于首页）
     */
    public Map<String, Object> getRepairStatistics() {
        logger.info("获取报修统计数据");

        Map<String, Object> result = new HashMap<>();

        try {
            // 调用现有的 countByStatus() 方法
            Map<String, Long> statusMap = repairRecordDao.countByStatus();

            // 转换为前端需要的格式
            result.put("pendingCount", statusMap.getOrDefault("pending", 0L));
            result.put("processingCount", statusMap.getOrDefault("processing", 0L));
            result.put("completedCount", statusMap.getOrDefault("completed", 0L));
            result.put("cancelledCount", statusMap.getOrDefault("cancelled", 0L));

            // 计算总数
            long total = statusMap.values().stream().mapToLong(Long::longValue).sum();
            result.put("totalCount", total);

            logger.info("统计成功：total={}, pending={}, processing={}, completed={}",
                    total,
                    statusMap.getOrDefault("pending", 0L),
                    statusMap.getOrDefault("processing", 0L),
                    statusMap.getOrDefault("completed", 0L));

        } catch (Exception e) {
            logger.error("获取报修统计失败", e);
            result.put("pendingCount", 0);
            result.put("processingCount", 0);
            result.put("completedCount", 0);
            result.put("cancelledCount", 0);
            result.put("totalCount", 0);
        }

        return result;
    }

    /**
     * 🔥 查询待处理报修（用于首页，限制数量）
     */
    public List<RepairRecord> findPendingRepairs(int limit) {
        logger.info("查询待处理报修：limit={}", limit);

        try {
            // 调用现有的 findPendingRepairs() 方法
            List<RepairRecord> allPending = repairRecordDao.findPendingRepairs();

            // 限制返回数量
            if (allPending.size() > limit) {
                return allPending.subList(0, limit);
            }

            return allPending;

        } catch (Exception e) {
            logger.error("查询待处理报修失败", e);
            return new java.util.ArrayList<>();
        }
    }
    /**
     * 根据ID删除报修记录
     */
    public boolean deleteById(Integer repairId) {
        if (repairId == null) {
            return false;
        }
        try {
            return repairRecordDao.delete(repairId) > 0;
        } catch (Exception e) {
            logger.error("删除报修记录失败", e);
            return false;
        }
    }


}
