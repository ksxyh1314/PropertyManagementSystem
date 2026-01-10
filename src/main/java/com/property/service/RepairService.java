package com.property.service;

import com.property.dao.RepairRecordDao;
import com.property.entity.RepairRecord;
import com.property.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报修服务类（✅ 保留触发器版本）
 *
 * 日志记录规则：
 * - 提交报修：触发器 trg_after_repair_submit 自动记录
 * - 完成报修：触发器 trg_after_repair_complete 自动记录
 * - 其他操作：Java 代码记录
 */
public class RepairService {
    private static final Logger logger = LoggerFactory.getLogger(RepairService.class);
    private RepairRecordDao repairRecordDao = new RepairRecordDao();

    // ==================== 原有方法 ====================

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
     * ✅ 提交报修（不记录日志，触发器会自动记录）
     */
    public Integer submitRepair(RepairRecord record) {
        Integer repairId = repairRecordDao.insert(record);

        if (repairId != null && repairId > 0) {
            logger.info("✅ 提交报修成功：repairId={}, ownerId={} (触发器已自动记录日志)",
                    repairId, record.getOwnerId());
            // ✅ 不记录日志，触发器 trg_after_repair_submit 会自动记录
        }

        return repairId;
    }

    /**
     * ✅ 受理报修（增加日志记录）
     */
    public boolean acceptRepair(Integer repairId, String handler, String handlerPhone,
                                Integer operatorId, HttpServletRequest request) {
        // 验证状态
        RepairRecord record = repairRecordDao.findById(repairId);
        if (record == null) {
            throw new IllegalArgumentException("报修记录不存在");
        }
        if (!"pending".equals(record.getRepairStatus())) {
            throw new IllegalArgumentException("只能受理待处理状态的报修");
        }

        int result = repairRecordDao.acceptRepair(repairId, handler, handlerPhone);

        if (result > 0) {
            logger.info("✅ 受理报修成功：repairId={}, handler={}", repairId, handler);

            // ✅ 记录操作日志
            if (operatorId != null && request != null) {
                LogUtil.log(
                        operatorId,
                        getUsername(request),
                        "repair_accept",
                        "受理报修：单号" + repairId + "，处理人：" + handler +
                                "，业主：" + record.getOwnerName() + "（" + record.getOwnerId() + "）",
                        LogUtil.getClientIP(request)
                );
            }

            return true;
        }

        return false;
    }

    /**
     * ✅ 完成报修（不记录日志，触发器会自动记录）
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

        if (result > 0) {
            logger.info("✅ 完成报修成功：repairId={} (触发器已自动记录日志)", repairId);
            // ✅ 不记录日志，触发器 trg_after_repair_complete 会自动记录
            return true;
        }

        return false;
    }

    /**
     * ✅ 取消报修（无原因，增加日志记录）
     */
    public boolean cancelRepair(Integer repairId, Integer operatorId, HttpServletRequest request) {
        RepairRecord record = repairRecordDao.findById(repairId);
        if (record == null) {
            throw new IllegalArgumentException("报修记录不存在");
        }

        String status = record.getRepairStatus();
        if (!"pending".equals(status) && !"processing".equals(status)) {
            throw new IllegalArgumentException("只能取消待处理或处理中的报修");
        }

        int result = repairRecordDao.cancelRepair(repairId);

        if (result > 0) {
            logger.info("✅ 取消报修成功：repairId={}", repairId);

            // ✅ 记录操作日志
            if (operatorId != null && request != null) {
                LogUtil.log(
                        operatorId,
                        getUsername(request),
                        "repair_cancel",
                        "取消报修：单号" + repairId +
                                "，业主：" + record.getOwnerName() + "（" + record.getOwnerId() + "）",
                        LogUtil.getClientIP(request)
                );
            }

            return true;
        }

        return false;
    }

    /**
     * ✅ 取消报修（带原因，增加日志记录）
     */
    public boolean cancelRepair(Integer repairId, String cancelReason,
                                Integer operatorId, HttpServletRequest request) {
        RepairRecord record = repairRecordDao.findById(repairId);
        if (record == null) {
            throw new IllegalArgumentException("报修记录不存在");
        }

        String status = record.getRepairStatus();
        if (!"pending".equals(status) && !"processing".equals(status)) {
            throw new IllegalArgumentException("只能取消待处理或处理中的报修");
        }

        int result = repairRecordDao.cancelRepair(repairId, cancelReason);

        if (result > 0) {
            logger.info("✅ 取消报修成功：repairId={}, reason={}", repairId, cancelReason);

            // ✅ 记录操作日志
            if (operatorId != null && request != null) {
                LogUtil.log(
                        operatorId,
                        getUsername(request),
                        "repair_cancel",
                        "取消报修：单号" + repairId + "，原因：" + (cancelReason != null ? cancelReason : "无") +
                                "，业主：" + record.getOwnerName() + "（" + record.getOwnerId() + "）",
                        LogUtil.getClientIP(request)
                );
            }

            return true;
        }

        return false;
    }

    /**
     * ✅ 评价报修（增加日志记录）
     */
    public boolean rateRepair(Integer repairId, Short rating, String feedback,
                              Integer operatorId, HttpServletRequest request) {
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

        if (result > 0) {
            logger.info("✅ 评价报修成功：repairId={}, rating={}", repairId, rating);

            // ✅ 记录操作日志
            if (operatorId != null && request != null) {
                LogUtil.log(
                        operatorId,
                        record.getOwnerId(),
                        "repair_rate",
                        "评价报修：单号" + repairId + "，评分：" + rating + "分" +
                                "，业主：" + record.getOwnerName() + "（" + record.getOwnerId() + "）",
                        LogUtil.getClientIP(request)
                );
            }

            return true;
        }

        return false;
    }

    /**
     * ✅ 删除报修（增加日志记录）
     */
    public boolean deleteRepair(Integer repairId, Integer operatorId, HttpServletRequest request) {
        RepairRecord record = repairRecordDao.findById(repairId);
        if (record == null) {
            throw new IllegalArgumentException("报修记录不存在");
        }

        int result = repairRecordDao.delete(repairId);

        if (result > 0) {
            logger.info("✅ 删除报修成功：repairId={}", repairId);

            // ✅ 记录操作日志
            if (operatorId != null && request != null) {
                LogUtil.log(
                        operatorId,
                        getUsername(request),
                        "repair_delete",
                        "删除报修：单号" + repairId + "，类型：" + getRepairTypeText(record.getRepairType()) +
                                "，业主：" + record.getOwnerName() + "（" + record.getOwnerId() + "）",
                        LogUtil.getClientIP(request)
                );
            }

            return true;
        }

        return false;
    }

    /**
     * 统计各状态数量
     */
    public Map<String, Long> countByStatus() {
        return repairRecordDao.countByStatus();
    }

    /**
     * 根据状态获取报修数量
     */
    public int getCountByStatus(String status) {
        try {
            return repairRecordDao.getCountByStatus(status);
        } catch (Exception e) {
            logger.error("获取报修数量失败：status={}", status, e);
            return 0;
        }
    }

    // ==================== 业主端方法 ====================

    /**
     * 业主端：分页查询我的报修记录
     */
    public Map<String, Object> findByPageForOwner(int pageNum, int pageSize, String ownerId, String status) {
        logger.info("业主端查询报修记录：ownerId={}, pageNum={}, pageSize={}, status={}",
                ownerId, pageNum, pageSize, status);

        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("业主ID不能为空");
        }

        try {
            List<RepairRecord> list = repairRecordDao.findByPageForOwner(pageNum, pageSize, ownerId, status);
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
     * 业主端：统计我的报修数量
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
     * 业主端：查询最近报修记录
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
     * ✅ 业主端：业主取消报修（增加日志记录）
     */
    public boolean cancelRepairByOwner(Integer repairId, String ownerId, String cancelReason,
                                       HttpServletRequest request) {
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

            if (result > 0) {
                logger.info("✅ 取消成功：repairId={}", repairId);

                // ✅ 记录操作日志
                if (request != null) {
                    Integer userId = (Integer) request.getSession().getAttribute("userId");
                    if (userId == null) userId = 0;

                    LogUtil.log(
                            userId,
                            ownerId,
                            "repair_cancel",
                            "业主取消报修：单号" + repairId + "，原因：" + (cancelReason != null ? cancelReason : "无"),
                            LogUtil.getClientIP(request)
                    );
                }

                return true;
            }

            return false;

        } catch (Exception e) {
            logger.error("业主取消报修失败", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * ✅ 业主端：追加报修说明（增加日志记录）
     */
    public boolean appendDescription(Integer repairId, String ownerId, String additionalDesc,
                                     HttpServletRequest request) {
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

            if (result > 0) {
                logger.info("✅ 追加成功：repairId={}", repairId);

                // ✅ 记录操作日志
                if (request != null) {
                    Integer userId = (Integer) request.getSession().getAttribute("userId");
                    if (userId == null) userId = 0;

                    LogUtil.log(
                            userId,
                            ownerId,
                            "repair_append",
                            "追加报修说明：单号" + repairId,
                            LogUtil.getClientIP(request)
                    );
                }

                return true;
            }

            return false;

        } catch (Exception e) {
            logger.error("追加报修说明失败", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 业主端：查询可评价的报修（已完成且未评价）
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
     * ✅ 业主端：业主评价报修（增加日志记录）
     */
    public boolean rateRepairByOwner(Integer repairId, String ownerId, Short rating, String feedback,
                                     HttpServletRequest request) {
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

            if (result > 0) {
                logger.info("✅ 评价成功：repairId={}, rating={}", repairId, rating);

                // ✅ 记录操作日志
                if (request != null) {
                    Integer userId = (Integer) request.getSession().getAttribute("userId");
                    if (userId == null) userId = 0;

                    LogUtil.log(
                            userId,
                            ownerId,
                            "repair_rate",
                            "业主评价报修：单号" + repairId + "，评分：" + rating + "分",
                            LogUtil.getClientIP(request)
                    );
                }

                return true;
            }

            return false;

        } catch (Exception e) {
            logger.error("业主评价报修失败", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 业主端：获取报修详情（带权限验证）
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
     * 获取报修统计数据（用于首页）
     */
    public Map<String, Object> getRepairStatistics() {
        logger.info("获取报修统计数据");

        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, Long> statusMap = repairRecordDao.countByStatus();

            result.put("pendingCount", statusMap.getOrDefault("pending", 0L));
            result.put("processingCount", statusMap.getOrDefault("processing", 0L));
            result.put("completedCount", statusMap.getOrDefault("completed", 0L));
            result.put("cancelledCount", statusMap.getOrDefault("cancelled", 0L));

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
     * 查询待处理报修（用于首页，限制数量）
     */
    public List<RepairRecord> findPendingRepairs(int limit) {
        logger.info("查询待处理报修：limit={}", limit);

        try {
            List<RepairRecord> allPending = repairRecordDao.findPendingRepairs();

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

    // ==================== 工具方法 ====================

    /**
     * 从请求中获取用户名
     */
    private String getUsername(HttpServletRequest request) {
        if (request == null) return "unknown";

        Object username = request.getSession().getAttribute("username");
        if (username != null) {
            return username.toString();
        }

        return "unknown";
    }

    /**
     * 获取报修类型文本
     */
    private String getRepairTypeText(String repairType) {
        if (repairType == null) return "未知";
        switch (repairType) {
            case "plumbing": return "水暖";
            case "electrical": return "电路";
            case "door_window": return "门窗";
            case "public_facility": return "公共设施";
            default: return "其他";
        }
    }

    /**
     * 获取优先级文本
     */
    private String getPriorityText(String priority) {
        if (priority == null) return "普通";
        switch (priority) {
            case "emergency": return "紧急";
            case "urgent": return "加急";
            default: return "普通";
        }
    }
}
