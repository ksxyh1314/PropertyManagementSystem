package com.property.servlet;

import com.property.entity.RepairRecord;
import com.property.entity.User;
import com.property.service.RepairService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 报修管理Servlet (管理员端核心逻辑)
 */
@WebServlet("/admin/repair")
public class RepairServlet extends BaseServlet {
    private RepairService repairService = new RepairService();

    // 常量定义
    private static final int MAX_DESCRIPTION_LENGTH = 500;
    private static final int MAX_FEEDBACK_LENGTH = 500;
    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;
    private static final int MAX_PAGE_SIZE = 100;

    /**
     * 1. 分页查询报修记录列表
     */
    public void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkLogin(req, resp)) return;

        int pageNum = getIntParameter(req, "pageNum", 1);
        int pageSize = getIntParameter(req, "pageSize", 10);
        String keyword = getStringParameter(req, "keyword");
        String status = getStringParameter(req, "status");

        if (pageNum < 1) {
            writeError(resp, "页码必须大于0");
            return;
        }
        if (pageSize > MAX_PAGE_SIZE) pageSize = MAX_PAGE_SIZE; // 自动修正最大值

        try {
            Map<String, Object> result = repairService.findByPage(pageNum, pageSize, keyword, status);
            writeJson(resp, result);
        } catch (Exception e) {
            logger.error("查询报修记录列表失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 2. 根据ID查询报修记录
     */
    public void findById(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkLogin(req, resp)) return;

        User currentUser = getCurrentUser(req);
        Integer repairId = getIntParameter(req, "repairId");

        if (repairId == null) {
            writeError(resp, "报修ID不能为空");
            return;
        }

        try {
            RepairRecord record = repairService.findById(repairId);
            if (record == null) {
                writeError(resp, "报修记录不存在");
                return;
            }

            // 权限控制：如果是业主，只能看自己的
            if ("owner".equals(currentUser.getUserRole())
                    && !currentUser.getUsername().equals(record.getOwnerId())) {
                writeError(resp, "无权查看他人的报修记录");
                return;
            }

            writeSuccess(resp, "查询成功", record);
        } catch (Exception e) {
            logger.error("查询报修详情失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 3. 提交报修 (管理员代提交)
     */
    public void submit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkLogin(req, resp)) return;

        // 获取参数
        String ownerId = getStringParameter(req, "ownerId");
        String houseId = getStringParameter(req, "houseId");
        String repairType = getStringParameter(req, "repairType");
        String description = getStringParameter(req, "description");
        String priority = getStringParameter(req, "priority", "normal");

        // 基础校验
        if (ownerId == null || houseId == null || repairType == null || description == null) {
            writeError(resp, "请填写完整信息（业主、房屋、类型、描述）");
            return;
        }

        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            writeError(resp, "问题描述不能超过" + MAX_DESCRIPTION_LENGTH + "字");
            return;
        }

        if (!isValidPriority(priority)) {
            writeError(resp, "优先级参数无效");
            return;
        }

        RepairRecord record = new RepairRecord();
        record.setOwnerId(ownerId.trim());
        record.setHouseId(houseId.trim());
        record.setRepairType(repairType.trim());
        record.setDescription(description.trim());
        record.setPriority(priority);
        record.setRepairStatus("pending"); // 默认为待处理

        try {
            Integer repairId = repairService.submitRepair(record);
            if (repairId != null) {
                writeSuccess(resp, "提交报修成功", repairId);
            } else {
                writeError(resp, "提交报修失败");
            }
        } catch (Exception e) {
            logger.error("提交报修失败", e);
            writeError(resp, "提交报修失败：" + e.getMessage());
        }
    }

    /**
     * 4. 受理报修 (管理员专属)
     */
    public void accept(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) return; // 仅管理员

        Integer repairId = getIntParameter(req, "repairId");
        String handler = getStringParameter(req, "handler");
        String handlerPhone = getStringParameter(req, "handlerPhone");

        if (repairId == null || handler == null || handlerPhone == null) {
            writeError(resp, "参数不完整（ID、处理人、电话）");
            return;
        }

        if (!isValidPhone(handlerPhone)) {
            writeError(resp, "处理人电话格式不正确");
            return;
        }

        try {
            // 检查状态：只有 pending 状态才能受理
            RepairRecord record = repairService.findById(repairId);
            if (record == null) {
                writeError(resp, "记录不存在");
                return;
            }
            if (!"pending".equals(record.getRepairStatus())) {
                writeError(resp, "该工单已被受理或已结束，无法重复受理");
                return;
            }

            boolean success = repairService.acceptRepair(repairId, handler.trim(), handlerPhone.trim());
            if (success) {
                writeSuccess(resp, "受理报修成功");
            } else {
                writeError(resp, "受理报修失败");
            }
        } catch (Exception e) {
            logger.error("受理报修失败", e);
            writeError(resp, "受理失败：" + e.getMessage());
        }
    }

    /**
     * 5. 完成报修 (管理员专属)
     */
    public void complete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) return;

        Integer repairId = getIntParameter(req, "repairId");
        String repairResult = getStringParameter(req, "repairResult");

        if (repairId == null || repairResult == null) {
            writeError(resp, "参数不完整");
            return;
        }

        if (repairResult.length() > MAX_DESCRIPTION_LENGTH) {
            writeError(resp, "维修结果字数过多");
            return;
        }

        try {
            // 检查状态：只有 processing 状态才能完成
            RepairRecord record = repairService.findById(repairId);
            if (record == null || !"processing".equals(record.getRepairStatus())) {
                writeError(resp, "只有【处理中】的工单才能点击完成");
                return;
            }

            boolean success = repairService.completeRepair(repairId, repairResult.trim());
            if (success) {
                writeSuccess(resp, "完成报修成功");
            } else {
                writeError(resp, "操作失败");
            }
        } catch (Exception e) {
            logger.error("完成报修失败", e);
            writeError(resp, "操作失败：" + e.getMessage());
        }
    }

    /**
     * 🔥 6. 取消报修 (核心业务逻辑升级)
     * - 业主：只能取消 Pending
     * - 管理员：可以取消 Pending 和 Processing (驳回)
     */
    public void cancel(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkLogin(req, resp)) return;

        User currentUser = getCurrentUser(req);
        Integer repairId = getIntParameter(req, "repairId");
        String cancelReason = getStringParameter(req, "cancelReason");

        if (repairId == null || cancelReason == null || cancelReason.trim().isEmpty()) {
            writeError(resp, "必须填写取消/驳回原因");
            return;
        }

        try {
            RepairRecord record = repairService.findById(repairId);
            if (record == null) {
                writeError(resp, "报修记录不存在");
                return;
            }

            // --- 权限与状态检查 ---
            if ("owner".equals(currentUser.getUserRole())) {
                // 🛑 业主逻辑
                if (!currentUser.getUsername().equals(record.getOwnerId())) {
                    writeError(resp, "无权操作他人的工单");
                    return;
                }
                if (!"pending".equals(record.getRepairStatus())) {
                    writeError(resp, "物业已受理或已完成，无法取消，请联系物业前台");
                    return;
                }
            } else if ("admin".equals(currentUser.getUserRole())) {
                // 🛑 管理员逻辑 (权限更大)
                if ("completed".equals(record.getRepairStatus())) {
                    writeError(resp, "已完成的工单无法取消/驳回");
                    return;
                }
                if ("cancelled".equals(record.getRepairStatus())) {
                    writeError(resp, "该工单已经是取消状态");
                    return;
                }
                // 管理员操作时，自动添加前缀，体现业务含义
                cancelReason = "[管理员驳回] " + cancelReason;
            }

            // 执行取消
            boolean success = repairService.cancelRepair(repairId, cancelReason.trim());
            if (success) {
                writeSuccess(resp, "操作成功");
            } else {
                writeError(resp, "操作失败");
            }
        } catch (Exception e) {
            logger.error("取消报修失败", e);
            writeError(resp, "操作失败：" + e.getMessage());
        }
    }

    /**
     * 🔥 7. 删除报修记录 (安全升级)
     * - 仅允许删除 "已取消" 或 "已完成" 的记录
     * - 防止误删 "处理中" 的记录
     */
    public void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) return;

        Integer repairId = getIntParameter(req, "repairId");
        if (repairId == null) {
            writeError(resp, "ID不能为空");
            return;
        }

        try {
            RepairRecord record = repairService.findById(repairId);
            if (record == null) {
                writeError(resp, "记录不存在");
                return;
            }

            // 🛑 安全检查：禁止删除正在进行的任务
            if ("processing".equals(record.getRepairStatus())) {
                writeError(resp, "该工单正在维修中，禁止删除！请先取消或完成后再删除。");
                return;
            }

            // 🛑 安全检查：禁止删除待处理的任务（防止误操作导致业主以为提交了但没人理）
            // 这一步看业务需求，通常建议只能删除 Cancelled 或 Completed
            if ("pending".equals(record.getRepairStatus())) {
                writeError(resp, "待处理的工单建议先【取消】后再删除，以保留操作痕迹。");
                return;
            }

            boolean success = repairService.deleteRepair(repairId);
            if (success) {
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
     * 8. 评价报修 (通常只有业主能评价，管理员只能查看)
     */
    public void rate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkLogin(req, resp)) return;

        User currentUser = getCurrentUser(req);
        Integer repairId = getIntParameter(req, "repairId");
        Integer rating = getIntParameter(req, "rating");
        String feedback = getStringParameter(req, "feedback");

        if (repairId == null || rating == null) {
            writeError(resp, "参数不完整");
            return;
        }

        try {
            RepairRecord record = repairService.findById(repairId);
            if (record == null) {
                writeError(resp, "记录不存在");
                return;
            }

            // 🛑 业务限制：管理员不能给自己刷好评
            if ("admin".equals(currentUser.getUserRole())) {
                writeError(resp, "管理员无法评价工单，请由业主进行评价");
                return;
            }

            // 业主只能评价自己的
            if (!currentUser.getUsername().equals(record.getOwnerId())) {
                writeError(resp, "无权评价他人记录");
                return;
            }

            if (!"completed".equals(record.getRepairStatus())) {
                writeError(resp, "只有已完成的工单可以评价");
                return;
            }

            boolean success = repairService.rateRepair(repairId, rating.shortValue(), feedback);
            if (success) {
                writeSuccess(resp, "评价成功");
            } else {
                writeError(resp, "评价失败");
            }
        } catch (Exception e) {
            logger.error("评价失败", e);
            writeError(resp, "评价失败：" + e.getMessage());
        }
    }

    // ==================== 辅助方法 ====================

    public void detail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        findById(req, resp);
    }

    private boolean isValidPriority(String priority) {
        return "normal".equals(priority) || "urgent".equals(priority) || "emergency".equals(priority);
    }

    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^1[3-9]\\d{9}$");
    }
    /**
     * 🔥 9. 查询待处理报修（用于首页统计）
     */
    public void findPending(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkLogin(req, resp)) return;

        try {
            int limit = getIntParameter(req, "limit", 5);
            if (limit > 50) limit = 50;

            List<RepairRecord> list = repairService.findPendingRepairs(limit);
            writeSuccess(resp, "查询成功", list);
        } catch (Exception e) {
            logger.error("查询待处理报修失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 🔥 10. 按状态统计报修数量（用于首页卡片）
     */
    public void countByStatus(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkLogin(req, resp)) return;

        try {
            Map<String, Object> statistics = repairService.getRepairStatistics();
            writeSuccess(resp, "统计成功", statistics);
        } catch (Exception e) {
            logger.error("统计报修数量失败", e);
            writeError(resp, "统计失败：" + e.getMessage());
        }
    }

}
