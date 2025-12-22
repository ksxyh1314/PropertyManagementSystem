package com.property.servlet;

import com.property.entity.ChargeItem;
import com.property.service.ChargeItemService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 收费项目管理Servlet
 */
@WebServlet("/admin/chargeItem")
public class ChargeItemServlet extends BaseServlet {
    private ChargeItemService chargeItemService = new ChargeItemService();

    /**
     * 分页查询收费项目列表
     */
    public void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        int pageNum = getIntParameter(req, "pageNum", 1);
        int pageSize = getIntParameter(req, "pageSize", 10);
        String keyword = getStringParameter(req, "keyword");

        try {
            Map<String, Object> result = chargeItemService.findByPage(pageNum, pageSize, keyword);
            writeJson(resp, result);
        } catch (Exception e) {
            logger.error("查询收费项目列表失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询所有收费项目
     */
    public void findAll(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        try {
            List<ChargeItem> items = chargeItemService.findAll();
            writeSuccess(resp, "查询成功", items);
        } catch (Exception e) {
            logger.error("查询收费项目失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询启用的收费项目
     */
    public void findActive(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        try {
            List<ChargeItem> items = chargeItemService.findActive();
            writeSuccess(resp, "查询成功", items);
        } catch (Exception e) {
            logger.error("查询启用的收费项目失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID查询收费项目
     */
    public void findById(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        String itemId = getStringParameter(req, "itemId");
        if (itemId == null || itemId.isEmpty()) {
            writeError(resp, "项目ID不能为空");
            return;
        }

        try {
            ChargeItem item = chargeItemService.findById(itemId);
            if (item != null) {
                writeSuccess(resp, "查询成功", item);
            } else {
                writeError(resp, "收费项目不存在");
            }
        } catch (Exception e) {
            logger.error("查询收费项目失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 添加收费项目
     */
    public void add(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        String itemId = getStringParameter(req, "itemId");
        String itemName = getStringParameter(req, "itemName");
        String chargeCycle = getStringParameter(req, "chargeCycle");
        String description = getStringParameter(req, "description");
        String calculationType = getStringParameter(req, "calculationType");
        String fixedAmountStr = getStringParameter(req, "fixedAmount");
        String formula = getStringParameter(req, "formula");
        Integer gracePeriod = getIntParameter(req, "gracePeriod");
        String lateFeeRateStr = getStringParameter(req, "lateFeeRate");

        ChargeItem item = new ChargeItem();
        item.setItemId(itemId);
        item.setItemName(itemName);
        item.setChargeCycle(chargeCycle);
        item.setDescription(description);
        item.setCalculationType(calculationType);
        item.setFormula(formula);
        item.setGracePeriod(gracePeriod);

        // 解析固定金额
        if (fixedAmountStr != null && !fixedAmountStr.isEmpty()) {
            try {
                item.setFixedAmount(new BigDecimal(fixedAmountStr));
            } catch (NumberFormatException e) {
                writeError(resp, "固定金额格式不正确");
                return;
            }
        }

        // 解析滞纳金比例
        if (lateFeeRateStr != null && !lateFeeRateStr.isEmpty()) {
            try {
                item.setLateFeeRate(new BigDecimal(lateFeeRateStr));
            } catch (NumberFormatException e) {
                writeError(resp, "滞纳金比例格式不正确");
                return;
            }
        }

        try {
            boolean success = chargeItemService.addChargeItem(item);
            if (success) {
                writeSuccess(resp, "添加收费项目成功");
            } else {
                writeError(resp, "添加收费项目失败");
            }
        } catch (IllegalArgumentException e) {
            writeError(resp, e.getMessage());
        } catch (Exception e) {
            logger.error("添加收费项目失败", e);
            writeError(resp, "添加收费项目失败：" + e.getMessage());
        }
    }

    /**
     * 更新收费项目
     */
    public void update(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        String itemId = getStringParameter(req, "itemId");
        if (itemId == null || itemId.isEmpty()) {
            writeError(resp, "项目ID不能为空");
            return;
        }

        String itemName = getStringParameter(req, "itemName");
        String chargeCycle = getStringParameter(req, "chargeCycle");
        String description = getStringParameter(req, "description");
        String calculationType = getStringParameter(req, "calculationType");
        String fixedAmountStr = getStringParameter(req, "fixedAmount");
        String formula = getStringParameter(req, "formula");
        Integer gracePeriod = getIntParameter(req, "gracePeriod");
        String lateFeeRateStr = getStringParameter(req, "lateFeeRate");
        Integer status = getIntParameter(req, "status");

        ChargeItem item = new ChargeItem();
        item.setItemId(itemId);
        item.setItemName(itemName);
        item.setChargeCycle(chargeCycle);
        item.setDescription(description);
        item.setCalculationType(calculationType);
        item.setFormula(formula);
        item.setGracePeriod(gracePeriod);
        item.setStatus(status);

        // 解析固定金额
        if (fixedAmountStr != null && !fixedAmountStr.isEmpty()) {
            try {
                item.setFixedAmount(new BigDecimal(fixedAmountStr));
            } catch (NumberFormatException e) {
                writeError(resp, "固定金额格式不正确");
                return;
            }
        }

        // 解析滞纳金比例
        if (lateFeeRateStr != null && !lateFeeRateStr.isEmpty()) {
            try {
                item.setLateFeeRate(new BigDecimal(lateFeeRateStr));
            } catch (NumberFormatException e) {
                writeError(resp, "滞纳金比例格式不正确");
                return;
            }
        }

        try {
            boolean success = chargeItemService.updateChargeItem(item);
            if (success) {
                writeSuccess(resp, "更新收费项目成功");
            } else {
                writeError(resp, "更新收费项目失败");
            }
        } catch (IllegalArgumentException e) {
            writeError(resp, e.getMessage());
        } catch (Exception e) {
            logger.error("更新收费项目失败", e);
            writeError(resp, "更新收费项目失败：" + e.getMessage());
        }
    }

    /**
     * 删除收费项目
     */
    public void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        String itemId = getStringParameter(req, "itemId");
        if (itemId == null || itemId.isEmpty()) {
            writeError(resp, "项目ID不能为空");
            return;
        }

        try {
            boolean success = chargeItemService.deleteChargeItem(itemId);
            if (success) {
                writeSuccess(resp, "删除收费项目成功");
            } else {
                writeError(resp, "删除收费项目失败");
            }
        } catch (Exception e) {
            logger.error("删除收费项目失败", e);
            writeError(resp, "删除收费项目失败：" + e.getMessage());
        }
    }

    /**
     * 启用/禁用收费项目
     */
    public void updateStatus(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        String itemId = getStringParameter(req, "itemId");
        Integer status = getIntParameter(req, "status");

        if (itemId == null || itemId.isEmpty()) {
            writeError(resp, "项目ID不能为空");
            return;
        }
        if (status == null) {
            writeError(resp, "状态不能为空");
            return;
        }

        try {
            boolean success = chargeItemService.updateStatus(itemId, status);
            if (success) {
                writeSuccess(resp, "更新状态成功");
            } else {
                writeError(resp, "更新状态失败");
            }
        } catch (Exception e) {
            logger.error("更新状态失败", e);
            writeError(resp, "更新状态失败：" + e.getMessage());
        }
    }
}
