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
 * 报修管理Servlet
 */
@WebServlet("/repair")
public class RepairServlet extends BaseServlet {
    private RepairService repairService = new RepairService();

    /**
     * 分页查询报修记录列表
     */
    public void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
            return;
        }

        int pageNum = getIntParameter(req, "pageNum", 1);
        int pageSize = getIntParameter(req, "pageSize", 10);
        String keyword = getStringParameter(req, "keyword");
        String status = getStringParameter(req, "status");

        try {
            Map<String, Object> result = repairService.findByPage(pageNum, pageSize, keyword, status);
            writeJson(resp, result);
        } catch (Exception e) {
            logger.error("查询报修记录列表失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询业主的报修记录
     */
    public void findByOwner(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
            return;
        }

        User currentUser = getCurrentUser(req);
        String ownerId = getStringParameter(req, "ownerId");

        // 如果是业主角色，只能查询自己的记录
        if ("owner".equals(currentUser.getUserRole())) {
            ownerId = currentUser.getUsername();
        }

        if (ownerId == null || ownerId.isEmpty()) {
            writeError(resp, "业主ID不能为空");
            return;
        }

        try {
            List<RepairRecord> records = repairService.findByOwnerId(ownerId);
            writeSuccess(resp, "查询成功", records);
        } catch (Exception e) {
            logger.error("查询报修记录失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询待处理报修
     */
    public void findPending(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        try {
            List<RepairRecord> records = repairService.findPendingRepairs();
            writeSuccess(resp, "查询成功", records);
        } catch (Exception e) {
            logger.error("查询待处理报修失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 提交报修
     */
    public void submit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkLogin(req, resp)) {
            return;
        }

        User currentUser = getCurrentUser(req);
        String ownerId = currentUser.getUsername();
        String houseId = getStringParameter(req, "houseId");
        String repairType = getStringParameter(req, "repairType");
        String description = getStringParameter(req, "description");
        String priority = getStringParameter(req, "priority", "normal");

        RepairRecord record = new RepairRecord();
        record.setOwnerId(ownerId);
        record.setHouseId(houseId);
        record.setRepairType(repairType);
        record.setDescription(description);
        record.setPriority(priority);

        try {
            Integer repairId = repairService.submitRepair(record);
            if (repairId != null) {
                writeSuccess(resp, "提交报修成功", repairId);
            } else {
                writeError(resp, "提交报修失败");
            }
        } catch (IllegalArgumentException e) {
            writeError(resp, e.getMessage());
        } catch (Exception e) {
            logger.error("提交报修失败", e);
            writeError(resp, "提交报修失败：" + e.getMessage());
        }
    }

    /**
     * 受理报修
     */
    public void accept(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        Integer repairId = getIntParameter(req, "repairId");
        String handler = getStringParameter(req, "handler");
        String handlerPhone = getStringParameter(req, "handlerPhone");

        if (repairId == null) {
            writeError(resp, "报修ID不能为空");
            return;
        }

        try {
            boolean success = repairService.acceptRepair(repairId, handler, handlerPhone);
            if (success) {
                writeSuccess(resp, "受理报修成功");
            } else {
                writeError(resp, "受理报修失败");
            }
        } catch (IllegalArgumentException e) {
            writeError(resp, e.getMessage());
        } catch (Exception e) {
            logger.error("受理报修失败", e);
            writeError(resp, "受理报修失败：" + e.getMessage());
        }
    }

    /**
     * 完成报修
     */
    public void complete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        Integer repairId = getIntParameter(req, "repairId");
        String repairResult = getStringParameter(req, "repairResult");

        if (repairId == null) {
            writeError(resp, "报修ID不能为空");
            return;
        }

        try {
            boolean success = repairService.completeRepair(repairId, repairResult);
            if (success) {
                writeSuccess(resp, "完成报修成功");
            } else {
                writeError(resp, "完成报修失败");
            }
        } catch (IllegalArgumentException e) {
            writeError(resp, e.getMessage());
        } catch (Exception e) {
            logger.error("完成报修失败", e);
            writeError(resp, "完成报修失败：" + e.getMessage());
        }
    }

    /**
     * 评价报修
     */
    public void rate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "owner")) {
            return;
        }

        Integer repairId = getIntParameter(req, "repairId");
        Integer rating = getIntParameter(req, "rating");
        String feedback = getStringParameter(req, "feedback");

        if (repairId == null) {
            writeError(resp, "报修ID不能为空");
            return;
        }

        try {
            boolean success = repairService.rateRepair(repairId, rating, feedback);
            if (success) {
                writeSuccess(resp, "评价成功");
            } else {
                writeError(resp, "评价失败");
            }
        } catch (IllegalArgumentException e) {
            writeError(resp, e.getMessage());
        } catch (Exception e) {
            logger.error("评价失败", e);
            writeError(resp, "评价失败：" + e.getMessage());
        }
    }

    /**
     * 统计各状态报修数量
     */
    public void countByStatus(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        try {
            Map<String, Long> stats = repairService.countByStatus();
            writeSuccess(resp, "统计成功", stats);
        } catch (Exception e) {
            logger.error("统计报修失败", e);
            writeError(resp, "统计失败：" + e.getMessage());
        }
    }
}
