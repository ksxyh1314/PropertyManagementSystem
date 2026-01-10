package com.property.servlet.owner;

import com.property.entity.House;
import com.property.entity.RepairRecord;
import com.property.entity.User;
import com.property.service.HouseService;
import com.property.service.RepairService;
import com.property.servlet.BaseServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 业主端 - 报修管理
 * 功能：列表查询、提交、详情、取消、评价、删除
 *
 * ✅ 日志记录规则：
 * - 提交报修：触发器 trg_after_repair_submit 自动记录（不需要传参）
 * - 取消报修：Service 层记录（需要传 request）
 * - 评价报修：Service 层记录（需要传 request）
 * - 删除报修：Service 层记录（需要传 request）
 */
@WebServlet("/owner/repair")
public class OwnerRepairServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(OwnerRepairServlet.class);
    private final RepairService repairService = new RepairService();
    private final HouseService houseService = new HouseService();

    // 常量定义
    private static final int MAX_DESCRIPTION_LENGTH = 500;
    private static final int MAX_FEEDBACK_LENGTH = 500;
    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;

    /**
     * 查询当前业主的房屋列表（用于报修时选择）
     */
    public void myHouses(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = checkOwnerLoginAndGetUser(req, resp);
        if (currentUser == null) return;

        try {
            String ownerId = currentUser.getUsername();
            List<House> houses = houseService.findByOwnerId(ownerId);

            if (houses == null || houses.isEmpty()) {
                writeError(resp, "您名下暂无房屋，无法提交报修");
                return;
            }

            writeSuccess(resp, "查询成功", houses);
        } catch (Exception e) {
            logger.error("查询业主房屋失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 1. 查询我的报修列表
     */
    public void list(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = checkOwnerLoginAndGetUser(req, resp);
        if (currentUser == null) return;

        int pageNum = getIntParameter(req, "pageNum", 1);
        int pageSize = getIntParameter(req, "pageSize", 10);
        String repairStatus = getStringParameter(req, "repairStatus");
        String ownerId = currentUser.getUsername();

        logger.info(">>> Servlet: 查询报修列表，ownerId={}, status={}, pageNum={}, pageSize={}",
                ownerId, repairStatus, pageNum, pageSize);

        try {
            Map<String, Object> result = repairService.findByPageForOwner(
                    pageNum, pageSize, ownerId, repairStatus
            );

            logger.info("✅ Servlet: 查询成功，total={}, listSize={}",
                    result.get("total"),
                    ((List<?>) result.get("list")).size());

            writeSuccess(resp, "查询成功", result);
        } catch (Exception e) {
            logger.error("查询报修列表失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * ✅ 2. 提交报修（不需要传日志参数，触发器会自动记录）
     */
    public void submit(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = checkOwnerLoginAndGetUser(req, resp);
        if (currentUser == null) return;

        String houseId = getStringParameter(req, "houseId");
        String repairType = getStringParameter(req, "repairType");
        String description = getStringParameter(req, "description");
        String priority = getStringParameter(req, "priority", "normal");

        // 参数校验
        if (houseId == null || repairType == null || description == null) {
            writeError(resp, "请填写完整信息（房屋、类型、描述）");
            return;
        }
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            writeError(resp, "问题描述不能超过" + MAX_DESCRIPTION_LENGTH + "字");
            return;
        }
        if (!isValidPriority(priority)) {
            writeError(resp, "无效的优先级类型");
            return;
        }

        try {
            String ownerId = currentUser.getUsername();

            // 安全检查：验证房屋是否属于当前业主
            House house = houseService.findById(houseId);
            if (house == null) {
                writeError(resp, "房屋不存在");
                return;
            }
            if (!ownerId.equals(house.getOwnerId())) {
                logger.warn("越权提交警告：用户 {} 尝试为房屋 {} 提交报修", ownerId, houseId);
                writeError(resp, 403, "您无权为该房屋提交报修");
                return;
            }

            // 构建报修记录
            RepairRecord record = new RepairRecord();
            record.setOwnerId(ownerId);
            record.setHouseId(houseId);
            record.setRepairType(repairType);
            record.setDescription(description);
            record.setPriority(priority);
            record.setRepairStatus("pending");

            // ✅ 不需要传日志参数，触发器 trg_after_repair_submit 会自动记录
            Integer repairId = repairService.submitRepair(record);

            if (repairId != null && repairId > 0) {
                writeSuccess(resp, "报修提交成功，我们会尽快处理");
            } else {
                writeError(resp, "提交失败，请稍后重试");
            }
        } catch (Exception e) {
            logger.error("提交报修失败", e);
            writeError(resp, "提交失败：" + e.getMessage());
        }
    }

    /**
     * 3. 查询报修详情
     */
    public void detail(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = checkOwnerLoginAndGetUser(req, resp);
        if (currentUser == null) return;

        Integer repairId = getIntParameter(req, "repairId");
        if (repairId == null) {
            writeError(resp, "报修ID不能为空");
            return;
        }

        try {
            RepairRecord record = checkOwnerAuth(repairId, currentUser.getUsername());
            if (record == null) {
                writeError(resp, 403, "报修记录不存在或无权访问");
                return;
            }

            writeSuccess(resp, "查询成功", record);
        } catch (Exception e) {
            logger.error("查询详情失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * ✅ 4. 取消报修（需要记录日志）
     */
    public void cancel(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = checkOwnerLoginAndGetUser(req, resp);
        if (currentUser == null) return;

        Integer repairId = getIntParameter(req, "repairId");
        String cancelReason = getStringParameter(req, "cancelReason");

        if (repairId == null) {
            writeError(resp, "报修ID不能为空");
            return;
        }
        if (cancelReason == null || cancelReason.trim().isEmpty()) {
            writeError(resp, "请输入取消原因");
            return;
        }

        try {
            // 1. 权限检查
            RepairRecord record = checkOwnerAuth(repairId, currentUser.getUsername());
            if (record == null) {
                writeError(resp, "无权操作此记录");
                return;
            }

            // 2. 状态检查
            if (!"pending".equals(record.getRepairStatus())) {
                writeError(resp, "当前状态不可取消（只有待处理状态可以取消）");
                return;
            }

            // ✅ 3. 执行取消（传递 request 用于记录日志）
            boolean success = repairService.cancelRepairByOwner(
                    repairId,
                    currentUser.getUsername(),
                    cancelReason.trim(),
                    req  // ✅ 传递请求对象，用于记录日志
            );

            if (success) {
                writeSuccess(resp, "取消成功");
            } else {
                writeError(resp, "取消失败");
            }
        } catch (Exception e) {
            logger.error("取消报修失败", e);
            writeError(resp, "操作失败：" + e.getMessage());
        }
    }

    /**
     * ✅ 5. 评价报修（需要记录日志）
     */
    public void rate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = checkOwnerLoginAndGetUser(req, resp);
        if (currentUser == null) return;

        Integer repairId = getIntParameter(req, "repairId");
        Integer rating = getIntParameter(req, "rating");
        String feedback = getStringParameter(req, "feedback");

        // 参数校验
        if (repairId == null || rating == null) {
            writeError(resp, "参数不完整");
            return;
        }
        if (rating < MIN_RATING || rating > MAX_RATING) {
            writeError(resp, "评分必须在 1-5 之间");
            return;
        }
        if (feedback != null && feedback.length() > MAX_FEEDBACK_LENGTH) {
            writeError(resp, "评价内容过长");
            return;
        }

        try {
            // 1. 权限检查
            RepairRecord record = checkOwnerAuth(repairId, currentUser.getUsername());
            if (record == null) {
                writeError(resp, "无权操作此记录");
                return;
            }

            // 2. 状态检查
            if (!"completed".equals(record.getRepairStatus())) {
                writeError(resp, "只有已完成的工单可以评价");
                return;
            }

            // 3. 检查是否已评价
            if (record.getSatisfactionRating() != null) {
                writeError(resp, "该工单已经评价过了");
                return;
            }

            // ✅ 4. 执行评价（传递 request 用于记录日志）
            boolean success = repairService.rateRepairByOwner(
                    repairId,
                    currentUser.getUsername(),
                    rating.shortValue(),
                    feedback,
                    req  // ✅ 传递请求对象，用于记录日志
            );

            if (success) {
                writeSuccess(resp, "评价提交成功");
            } else {
                writeError(resp, "评价提交失败");
            }
        } catch (Exception e) {
            logger.error("评价失败", e);
            writeError(resp, "评价失败：" + e.getMessage());
        }
    }

    /**
     * 6. 首页查询最近报修
     */
    public void recent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = checkOwnerLoginAndGetUser(req, resp);
        if (currentUser == null) return;

        int limit = getIntParameter(req, "limit", 5);
        String ownerId = currentUser.getUsername();

        try {
            List<RepairRecord> list = repairService.findRecentByOwner(ownerId, limit);
            writeSuccess(resp, "查询成功", list);
        } catch (Exception e) {
            logger.error("查询最近报修失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * ✅ 7. 删除报修记录（需要记录日志）
     */
    public void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = checkOwnerLoginAndGetUser(req, resp);
        if (currentUser == null) return;

        Integer repairId = getIntParameter(req, "repairId");
        if (repairId == null) {
            writeError(resp, "报修ID不能为空");
            return;
        }

        try {
            // 1. 权限检查
            RepairRecord record = checkOwnerAuth(repairId, currentUser.getUsername());
            if (record == null) {
                writeError(resp, 403, "无权操作此记录");
                return;
            }

            // 2. 状态检查：只能删除已取消的记录
            if (!"cancelled".equals(record.getRepairStatus())) {
                writeError(resp, "只能删除已取消的报修记录");
                return;
            }

            // ✅ 3. 执行删除（需要记录日志）
            // 注意：业主端删除需要使用带日志记录的方法
            Integer userId = currentUser.getUserId();
            if (userId == null) userId = 0;

            boolean success = repairService.deleteRepair(
                    repairId,
                    userId,  // ✅ 操作员ID
                    req      // ✅ 请求对象
            );

            if (success) {
                logger.info("业主 {} 删除了报修记录 {}", currentUser.getUsername(), repairId);
                writeSuccess(resp, "删除成功");
            } else {
                writeError(resp, "删除失败");
            }
        } catch (Exception e) {
            logger.error("删除报修记录失败", e);
            writeError(resp, "删除失败：" + e.getMessage());
        }
    }

    /**
     * ✅ 8. 追加报修说明（需要记录日志）
     */
    public void appendDescription(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = checkOwnerLoginAndGetUser(req, resp);
        if (currentUser == null) return;

        Integer repairId = getIntParameter(req, "repairId");
        String additionalDesc = getStringParameter(req, "additionalDesc");

        if (repairId == null) {
            writeError(resp, "报修ID不能为空");
            return;
        }
        if (additionalDesc == null || additionalDesc.trim().isEmpty()) {
            writeError(resp, "请输入追加说明");
            return;
        }
        if (additionalDesc.length() > MAX_DESCRIPTION_LENGTH) {
            writeError(resp, "追加说明不能超过" + MAX_DESCRIPTION_LENGTH + "字");
            return;
        }

        try {
            // 1. 权限检查
            RepairRecord record = checkOwnerAuth(repairId, currentUser.getUsername());
            if (record == null) {
                writeError(resp, 403, "无权操作此记录");
                return;
            }

            // 2. 状态检查
            String status = record.getRepairStatus();
            if (!"pending".equals(status) && !"processing".equals(status)) {
                writeError(resp, "该报修已完成或取消，无法追加说明");
                return;
            }

            // ✅ 3. 执行追加（传递 request 用于记录日志）
            boolean success = repairService.appendDescription(
                    repairId,
                    currentUser.getUsername(),
                    additionalDesc.trim(),
                    req  // ✅ 传递请求对象，用于记录日志
            );

            if (success) {
                writeSuccess(resp, "追加说明成功");
            } else {
                writeError(resp, "追加说明失败");
            }
        } catch (Exception e) {
            logger.error("追加报修说明失败", e);
            writeError(resp, "操作失败：" + e.getMessage());
        }
    }

    /**
     * 9. 查询可评价的报修
     */
    public void ratable(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = checkOwnerLoginAndGetUser(req, resp);
        if (currentUser == null) return;

        try {
            String ownerId = currentUser.getUsername();
            List<RepairRecord> list = repairService.findRatableRepairs(ownerId);
            writeSuccess(resp, "查询成功", list);
        } catch (Exception e) {
            logger.error("查询可评价报修失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 10. 统计我的报修数量
     */
    public void summary(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = checkOwnerLoginAndGetUser(req, resp);
        if (currentUser == null) return;

        try {
            String ownerId = currentUser.getUsername();
            Map<String, Object> summary = repairService.getOwnerRepairSummary(ownerId);
            writeSuccess(resp, "统计成功", summary);
        } catch (Exception e) {
            logger.error("统计报修数量失败", e);
            writeError(resp, "统计失败：" + e.getMessage());
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 核心安全检查：验证报修单是否存在，且属于当前登录业主
     */
    private RepairRecord checkOwnerAuth(Integer repairId, String currentOwnerId) {
        RepairRecord record = repairService.findById(repairId);
        if (record == null) {
            return null;
        }
        if (!currentOwnerId.equals(record.getOwnerId())) {
            logger.warn("越权访问警告：用户 {} 尝试访问报修单 {}", currentOwnerId, repairId);
            return null;
        }
        return record;
    }

    /**
     * 验证优先级参数
     */
    private boolean isValidPriority(String priority) {
        return "normal".equals(priority) || "urgent".equals(priority) || "emergency".equals(priority);
    }
}
