package com.property.servlet;

import com.property.entity.ChargeItem;
import com.property.service.ChargeItemService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 收费项目管理Servlet
 */
@WebServlet(urlPatterns = {"/admin/chargeItem", "/finance/chargeItem"})
public class ChargeItemServlet extends BaseServlet {
    private ChargeItemService chargeItemService = new ChargeItemService();

    /**
     * 分页查询收费项目列表
     */
    public void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // ========== 调试日志开始 ==========
        System.out.println("========================================");
        System.out.println("【收费项目列表查询】开始");
        System.out.println("请求时间: " + new java.util.Date());
        System.out.println("请求 URI: " + req.getRequestURI());
        System.out.println("请求方法: " + req.getMethod());
        System.out.println("========================================");

        // 检查权限
        System.out.println("1. 开始检查权限...");
        if (!checkRole(req, resp, "admin", "finance")) {
            System.out.println("❌ 权限检查失败！");
            System.out.println("========================================");
            return;
        }
        System.out.println("✅ 权限检查通过");

        // 获取参数
        int pageNum = getIntParameter(req, "pageNum", 1);
        int pageSize = getIntParameter(req, "pageSize", 10);
        String keyword = getStringParameter(req, "keyword");

        System.out.println("\n2. 请求参数：");
        System.out.println("   pageNum: " + pageNum);
        System.out.println("   pageSize: " + pageSize);
        System.out.println("   keyword: " + (keyword == null ? "null" : "'" + keyword + "'"));

        try {
            System.out.println("\n3. 开始查询数据库...");
            Map<String, Object> result = chargeItemService.findByPage(pageNum, pageSize, keyword);

            System.out.println("✅ 查询成功！");
            System.out.println("   total: " + result.get("total"));
            System.out.println("   pageNum: " + result.get("pageNum"));
            System.out.println("   pageSize: " + result.get("pageSize"));
            System.out.println("   totalPages: " + result.get("totalPages"));

            @SuppressWarnings("unchecked")
            List<ChargeItem> list = (List<ChargeItem>) result.get("list");
            System.out.println("   list.size(): " + (list != null ? list.size() : "null"));

            if (list != null && !list.isEmpty()) {
                System.out.println("\n   数据列表（前3条）：");
                for (int i = 0; i < Math.min(3, list.size()); i++) {
                    ChargeItem item = list.get(i);
                    System.out.println("   [" + (i+1) + "] " + item.getItemId() + " - " + item.getItemName());
                }
            } else {
                System.out.println("   ⚠️ 数据列表为空！");
            }

            System.out.println("\n4. 开始写入响应...");
            writeJson(resp, result);
            System.out.println("✅ 响应写入成功");

        } catch (Exception e) {
            System.err.println("❌ 查询失败！");
            System.err.println("异常类型: " + e.getClass().getName());
            System.err.println("异常消息: " + e.getMessage());
            System.err.println("堆栈跟踪:");
            e.printStackTrace();

            logger.error("查询收费项目列表失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        } finally {
            System.out.println("========================================");
            System.out.println("【收费项目列表查询】结束");
            System.out.println("========================================\n");
        }
    }

    /**
     * 查询所有收费项目
     */
    public void findAll(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("========== 查询所有收费项目 ==========");

        if (!checkRole(req, resp, "admin", "finance")) {
            System.out.println("权限检查失败");
            return;
        }

        try {
            List<ChargeItem> items = chargeItemService.findAll();
            System.out.println("查询到 " + items.size() + " 条记录");
            writeSuccess(resp, "查询成功", items);
        } catch (Exception e) {
            System.err.println("查询失败: " + e.getMessage());
            e.printStackTrace();
            logger.error("查询收费项目失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询启用的收费项目
     * 🔥 允许 admin 和 finance 访问
     */
    public void findActive(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        System.out.println("========================================");
        System.out.println("【查询启用的收费项目】");
        System.out.println("请求时间: " + new java.util.Date());
        System.out.println("请求 URI: " + req.getRequestURI());
        System.out.println("请求方法: findActive");
        System.out.println("========================================");

        // 🔥 只需要这一个权限检查
        if (!checkRole(req, resp, "admin", "finance")) {
            System.out.println("❌ 权限检查失败");
            System.out.println("========================================");
            return;
        }

        System.out.println("✅ 权限检查通过");

        try {
            System.out.println("开始查询启用的收费项目...");
            List<ChargeItem> items = chargeItemService.findActive();

            System.out.println("✅ 查询成功，共 " + items.size() + " 条记录");

            // 打印前3条数据
            if (items.size() > 0) {
                System.out.println("\n📋 收费项目列表（前3条）:");
                for (int i = 0; i < Math.min(3, items.size()); i++) {
                    ChargeItem item = items.get(i);
                    System.out.println("  " + (i+1) + ". ID=" + item.getItemId() +
                            ", Name=" + item.getItemName());
                }
            }

            System.out.println("========================================");
            writeSuccess(resp, "查询成功", items);

        } catch (Exception e) {
            System.err.println("❌ 查询失败: " + e.getMessage());
            e.printStackTrace();
            System.out.println("========================================");
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
        System.out.println("========== 根据ID查询: " + itemId + " ==========");

        if (itemId == null || itemId.isEmpty()) {
            writeError(resp, "项目ID不能为空");
            return;
        }

        try {
            ChargeItem item = chargeItemService.findById(itemId);
            if (item != null) {
                System.out.println("查询成功: " + item.getItemName());
                writeSuccess(resp, "查询成功", item);
            } else {
                System.out.println("未找到记录");
                writeError(resp, "收费项目不存在");
            }
        } catch (Exception e) {
            System.err.println("查询失败: " + e.getMessage());
            e.printStackTrace();
            logger.error("查询收费项目失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 添加收费项目
     */
    public void add(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("========================================");
        System.out.println("【添加收费项目】开始");
        System.out.println("========================================");

        if (!checkRole(req, resp, "admin")) {
            System.out.println("❌ 权限检查失败");
            return;
        }

        // 获取参数
        String itemId = getStringParameter(req, "itemId");
        String itemName = getStringParameter(req, "itemName");
        String chargeCycle = getStringParameter(req, "chargeCycle");
        String description = getStringParameter(req, "description");
        String calculationType = getStringParameter(req, "calculationType");
        String fixedAmountStr = getStringParameter(req, "fixedAmount");
        String formula = getStringParameter(req, "formula");
        Integer gracePeriod = getIntParameter(req, "gracePeriod");
        String lateFeeRateStr = getStringParameter(req, "lateFeeRate");

        System.out.println("\n接收到的参数：");
        System.out.println("  项目ID: " + itemId);
        System.out.println("  项目名称: " + itemName);
        System.out.println("  收费周期: " + chargeCycle);
        System.out.println("  描述: " + description);
        System.out.println("  计算类型: " + calculationType);
        System.out.println("  固定金额: " + fixedAmountStr);
        System.out.println("  计算公式: " + formula);
        System.out.println("  宽限期: " + gracePeriod);
        System.out.println("  滞纳金比例: " + lateFeeRateStr);

        // 创建实体对象
        ChargeItem item = new ChargeItem();
        item.setItemId(itemId);
        item.setItemName(itemName);
        item.setChargeCycle(chargeCycle);
        item.setDescription(description);
        item.setCalculationType(calculationType);
        item.setFormula(formula);
        item.setGracePeriod(gracePeriod);

        // 🔧 解析固定金额
        if (fixedAmountStr != null && !fixedAmountStr.trim().isEmpty()) {
            try {
                BigDecimal fixedAmount = new BigDecimal(fixedAmountStr);

                // 验证金额必须大于0
                if (fixedAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    System.err.println("❌ 固定金额必须大于0: " + fixedAmount);
                    writeError(resp, "固定金额必须大于0");
                    return;
                }

                // 验证金额不能超过上限
                if (fixedAmount.compareTo(new BigDecimal("999999.99")) > 0) {
                    System.err.println("❌ 固定金额超过上限: " + fixedAmount);
                    writeError(resp, "固定金额不能超过999999.99元");
                    return;
                }

                item.setFixedAmount(fixedAmount);
                System.out.println("✅ 固定金额解析成功: " + fixedAmount);

            } catch (NumberFormatException e) {
                System.err.println("❌ 固定金额格式错误: " + fixedAmountStr);
                writeError(resp, "固定金额格式不正确，请输入有效的数字（如：100.50）");
                return;
            }
        }

        // 🔧 解析滞纳金比例（支持小数，如 0.0005）
        if (lateFeeRateStr != null && !lateFeeRateStr.trim().isEmpty()) {
            try {
                BigDecimal lateFeeRate = new BigDecimal(lateFeeRateStr);

                // 验证不能为负数
                if (lateFeeRate.compareTo(BigDecimal.ZERO) < 0) {
                    System.err.println("❌ 滞纳金比例不能为负数: " + lateFeeRate);
                    writeError(resp, "滞纳金比例不能为负数");
                    return;
                }

                // 验证不能超过100%
                if (lateFeeRate.compareTo(BigDecimal.ONE) > 0) {
                    System.err.println("❌ 滞纳金比例超过100%: " + lateFeeRate);
                    writeError(resp, "滞纳金比例不能超过1（即100%）");
                    return;
                }

                // 验证精度（最多6位小数）
                if (lateFeeRate.scale() > 6) {
                    System.err.println("❌ 滞纳金比例精度过高: " + lateFeeRate);
                    writeError(resp, "滞纳金比例最多支持6位小数");
                    return;
                }

                item.setLateFeeRate(lateFeeRate);
                System.out.println("✅ 滞纳金比例解析成功: " + lateFeeRate);

            } catch (NumberFormatException e) {
                System.err.println("❌ 滞纳金比例格式错误: " + lateFeeRateStr);
                writeError(resp, "滞纳金比例格式不正确，请输入有效的小数（如：0.0005 表示万分之五）");
                return;
            }
        }

        try {
            System.out.println("\n开始添加收费项目...");
            boolean success = chargeItemService.addChargeItem(item);

            if (success) {
                System.out.println("✅ 添加成功");
                System.out.println("========================================");
                writeSuccess(resp, "添加收费项目成功");
            } else {
                System.out.println("❌ 添加失败（数据库操作返回false）");
                System.out.println("========================================");
                writeError(resp, "添加收费项目失败");
            }

        } catch (IllegalArgumentException e) {
            System.err.println("❌ 参数验证失败: " + e.getMessage());
            System.out.println("========================================");
            writeError(resp, e.getMessage());

        } catch (Exception e) {
            System.err.println("❌ 系统错误: " + e.getMessage());
            e.printStackTrace();
            System.out.println("========================================");
            logger.error("添加收费项目失败", e);
            writeError(resp, "添加收费项目失败：" + e.getMessage());
        }
    }

    /**
     * 更新收费项目
     */
    public void update(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("========================================");
        System.out.println("【更新收费项目】开始");
        System.out.println("========================================");

        if (!checkRole(req, resp, "admin")) {
            System.out.println("❌ 权限检查失败");
            return;
        }

        String itemId = getStringParameter(req, "itemId");
        if (itemId == null || itemId.isEmpty()) {
            writeError(resp, "项目ID不能为空");
            return;
        }

        System.out.println("更新项目ID: " + itemId);

        // 获取参数
        String itemName = getStringParameter(req, "itemName");
        String chargeCycle = getStringParameter(req, "chargeCycle");
        String description = getStringParameter(req, "description");
        String calculationType = getStringParameter(req, "calculationType");
        String fixedAmountStr = getStringParameter(req, "fixedAmount");
        String formula = getStringParameter(req, "formula");
        Integer gracePeriod = getIntParameter(req, "gracePeriod");
        String lateFeeRateStr = getStringParameter(req, "lateFeeRate");
        Integer status = getIntParameter(req, "status");

        System.out.println("\n接收到的参数：");
        System.out.println("  项目名称: " + itemName);
        System.out.println("  收费周期: " + chargeCycle);
        System.out.println("  计算类型: " + calculationType);
        System.out.println("  固定金额: " + fixedAmountStr);
        System.out.println("  滞纳金比例: " + lateFeeRateStr);
        System.out.println("  状态: " + status);

        // 创建实体对象
        ChargeItem item = new ChargeItem();
        item.setItemId(itemId);
        item.setItemName(itemName);
        item.setChargeCycle(chargeCycle);
        item.setDescription(description);
        item.setCalculationType(calculationType);
        item.setFormula(formula);
        item.setGracePeriod(gracePeriod);
        item.setStatus(status);

        // 🔧 解析固定金额
        if (fixedAmountStr != null && !fixedAmountStr.trim().isEmpty()) {
            try {
                BigDecimal fixedAmount = new BigDecimal(fixedAmountStr);

                if (fixedAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    System.err.println("❌ 固定金额必须大于0: " + fixedAmount);
                    writeError(resp, "固定金额必须大于0");
                    return;
                }

                if (fixedAmount.compareTo(new BigDecimal("999999.99")) > 0) {
                    System.err.println("❌ 固定金额超过上限: " + fixedAmount);
                    writeError(resp, "固定金额不能超过999999.99元");
                    return;
                }

                item.setFixedAmount(fixedAmount);
                System.out.println("✅ 固定金额解析成功: " + fixedAmount);

            } catch (NumberFormatException e) {
                System.err.println("❌ 固定金额格式错误: " + fixedAmountStr);
                writeError(resp, "固定金额格式不正确，请输入有效的数字（如：100.50）");
                return;
            }
        }

        // 🔧 解析滞纳金比例
        if (lateFeeRateStr != null && !lateFeeRateStr.trim().isEmpty()) {
            try {
                BigDecimal lateFeeRate = new BigDecimal(lateFeeRateStr);

                if (lateFeeRate.compareTo(BigDecimal.ZERO) < 0) {
                    System.err.println("❌ 滞纳金比例不能为负数: " + lateFeeRate);
                    writeError(resp, "滞纳金比例不能为负数");
                    return;
                }

                if (lateFeeRate.compareTo(BigDecimal.ONE) > 0) {
                    System.err.println("❌ 滞纳金比例超过100%: " + lateFeeRate);
                    writeError(resp, "滞纳金比例不能超过1（即100%）");
                    return;
                }

                if (lateFeeRate.scale() > 6) {
                    System.err.println("❌ 滞纳金比例精度过高: " + lateFeeRate);
                    writeError(resp, "滞纳金比例最多支持6位小数");
                    return;
                }

                item.setLateFeeRate(lateFeeRate);
                System.out.println("✅ 滞纳金比例解析成功: " + lateFeeRate);

            } catch (NumberFormatException e) {
                System.err.println("❌ 滞纳金比例格式错误: " + lateFeeRateStr);
                writeError(resp, "滞纳金比例格式不正确，请输入有效的小数（如：0.0005）");
                return;
            }
        }

        try {
            System.out.println("\n开始更新收费项目...");
            boolean success = chargeItemService.updateChargeItem(item);

            if (success) {
                System.out.println("✅ 更新成功");
                System.out.println("========================================");
                writeSuccess(resp, "更新收费项目成功");
            } else {
                System.out.println("❌ 更新失败（数据库操作返回false）");
                System.out.println("========================================");
                writeError(resp, "更新收费项目失败");
            }

        } catch (IllegalArgumentException e) {
            System.err.println("❌ 参数验证失败: " + e.getMessage());
            System.out.println("========================================");
            writeError(resp, e.getMessage());

        } catch (Exception e) {
            System.err.println("❌ 系统错误: " + e.getMessage());
            e.printStackTrace();
            System.out.println("========================================");
            logger.error("更新收费项目失败", e);
            writeError(resp, "更新收费项目失败：" + e.getMessage());
        }
    }

    /**
     * 删除收费项目
     */
    public void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("========================================");
        System.out.println("【删除收费项目】开始");
        System.out.println("========================================");

        if (!checkRole(req, resp, "admin")) {
            System.out.println("❌ 权限检查失败");
            return;
        }

        String itemId = getStringParameter(req, "itemId");
        System.out.println("删除项目ID: " + itemId);

        if (itemId == null || itemId.isEmpty()) {
            writeError(resp, "项目ID不能为空");
            return;
        }

        try {
            boolean success = chargeItemService.deleteChargeItem(itemId);

            if (success) {
                System.out.println("✅ 删除成功");
                System.out.println("========================================");
                writeSuccess(resp, "删除收费项目成功");
            } else {
                System.out.println("❌ 删除失败");
                System.out.println("========================================");
                writeError(resp, "删除收费项目失败");
            }

        } catch (Exception e) {
            System.err.println("❌ 删除失败: " + e.getMessage());
            e.printStackTrace();
            System.out.println("========================================");
            logger.error("删除收费项目失败", e);
            writeError(resp, "删除收费项目失败：" + e.getMessage());
        }
    }

    /**
     * 启用/禁用收费项目
     */
    public void updateStatus(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("========================================");
        System.out.println("【更新收费项目状态】开始");
        System.out.println("========================================");

        if (!checkRole(req, resp, "admin")) {
            System.out.println("❌ 权限检查失败");
            return;
        }

        String itemId = getStringParameter(req, "itemId");
        Integer status = getIntParameter(req, "status");

        System.out.println("项目ID: " + itemId);
        System.out.println("状态: " + status);

        if (itemId == null || itemId.isEmpty()) {
            writeError(resp, "项目ID不能为空");
            return;
        }
        if (status == null) {
            writeError(resp, "状态不能为空");
            return;
        }
        if (status != 0 && status != 1) {
            writeError(resp, "状态值无效，必须为0（禁用）或1（启用）");
            return;
        }

        try {
            boolean success = chargeItemService.updateStatus(itemId, status);

            if (success) {
                System.out.println("✅ 状态更新成功");
                System.out.println("========================================");
                writeSuccess(resp, "更新状态成功");
            } else {
                System.out.println("❌ 状态更新失败");
                System.out.println("========================================");
                writeError(resp, "更新状态失败");
            }

        } catch (Exception e) {
            System.err.println("❌ 状态更新失败: " + e.getMessage());
            e.printStackTrace();
            System.out.println("========================================");
            logger.error("更新状态失败", e);
            writeError(resp, "更新状态失败：" + e.getMessage());
        }
    }
}
