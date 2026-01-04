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
 * 功能：列表查询、提交、详情、取消、评价
 */
@WebServlet("/owner/repair")
public class OwnerRepairServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(OwnerRepairServlet.class);
    private final RepairService repairService = new RepairService();
    private final HouseService houseService = new HouseService(); // 🔥 新增

    // 常量定义
    private static final int MAX_DESCRIPTION_LENGTH = 500;
    private static final int MAX_FEEDBACK_LENGTH = 500;
    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;

    /**
     * 🔥 新增：查询当前业主的房屋列表（用于报修时选择）
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
     * 🔥 1. 查询我的报修列表（最终修复版）
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
            // 🔥🔥🔥 修复：参数顺序改为 (pageNum, pageSize, ownerId, repairStatus)
            Map<String, Object> result = repairService.findByPageForOwner(
                    pageNum, pageSize, ownerId, repairStatus  // ✅ 正确顺序
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
     * 🔥 2. 提交报修（修复版：自动识别业主房屋）
     */
    public void submit(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = checkOwnerLoginAndGetUser(req, resp);
        if (currentUser == null) return;

        String houseId = getStringParameter(req, "houseId");
        String repairType = getStringParameter(req, "repairType");
        String description = getStringParameter(req, "description");
        String priority = getStringParameter(req, "priority", "normal"); // 默认为普通

        // --- 参数校验 ---
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

            // 🔥🔥🔥 关键安全检查：验证房屋是否属于当前业主
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
            record.setOwnerId(ownerId); // 绑定当前用户
            record.setHouseId(houseId);
            record.setRepairType(repairType);
            record.setDescription(description);
            record.setPriority(priority);
            record.setRepairStatus("pending"); // 初始状态

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
     * 🔥 3. 查询报修详情
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
            // 权限检查提取为通用方法
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
     * 🔥 4. 取消报修
     * 只有 "pending" (待处理) 状态的可以取消
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

            // 3. 执行取消
            boolean success = repairService.cancelRepair(repairId, cancelReason);
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
     * 🔥 5. 评价报修
     * 只有 "completed" (已完成) 状态的可以评价
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

            // 3. 执行评价
            boolean success = repairService.rateRepair(repairId, rating.shortValue(), feedback);
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
     * 🔥 6. 首页查询最近报修 (Limit 5)
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

    // ==================== 私有辅助方法 ====================

    /**
     * 核心安全检查：验证报修单是否存在，且属于当前登录业主
     * @return 如果验证通过返回 Record 对象，否则返回 null
     */
    private RepairRecord checkOwnerAuth(Integer repairId, String currentOwnerId) {
        RepairRecord record = repairService.findById(repairId);
        if (record == null) {
            return null;
        }
        // 关键：比较数据库中的 ownerId 和 Session 中的 username
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
    /**
     * 🔥 6. 删除报修记录（新增）
     * 只有 "cancelled" (已取消) 状态的可以删除
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

            // 3. 执行删除
            boolean success = repairService.deleteById(repairId);
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

}
