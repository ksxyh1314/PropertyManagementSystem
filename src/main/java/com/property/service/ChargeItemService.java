package com.property.service;

import com.property.dao.ChargeItemDao;
import com.property.entity.ChargeItem;
import com.property.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收费项目服务类（✅ 增加日志记录）
 */
public class ChargeItemService {
    private static final Logger logger = LoggerFactory.getLogger(ChargeItemService.class);
    private ChargeItemDao chargeItemDao = new ChargeItemDao();

    /**
     * 根据ID查询收费项目
     */
    public ChargeItem findById(String itemId) {
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new IllegalArgumentException("项目ID不能为空");
        }
        return chargeItemDao.findById(itemId);
    }

    /**
     * 查询所有收费项目
     */
    public List<ChargeItem> findAll() {
        return chargeItemDao.findAll();
    }

    /**
     * 查询启用的收费项目
     */
    public List<ChargeItem> findActive() {
        return chargeItemDao.findActive();
    }

    /**
     * 分页查询收费项目
     */
    public Map<String, Object> findByPage(int pageNum, int pageSize, String keyword) {
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1) pageSize = 10;

        List<ChargeItem> list = chargeItemDao.findByPage(pageNum, pageSize, keyword);
        long total = chargeItemDao.count(keyword);
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
     * 添加收费项目（支持不传 request）
     */
    public boolean addChargeItem(ChargeItem item) {
        return addChargeItem(item, null);
    }

    /**
     * 添加收费项目（✅ 增加日志记录）
     */
    public boolean addChargeItem(ChargeItem item, HttpServletRequest request) {
        // 参数验证
        validateChargeItem(item);

        // 检查项目ID是否已存在
        if (chargeItemDao.existsById(item.getItemId())) {
            throw new IllegalArgumentException("项目编号已存在：" + item.getItemId());
        }

        // 设置默认值
        if (item.getGracePeriod() == null) {
            item.setGracePeriod(30);
        }
        if (item.getLateFeeRate() == null) {
            item.setLateFeeRate(new BigDecimal("0.0005")); // 默认万分之五
        }
        if (item.getStatus() == null) {
            item.setStatus(1);
        }

        int rows = chargeItemDao.insert(item);
        if (rows > 0) {
            logger.info("添加收费项目成功：{} - {}", item.getItemId(), item.getItemName());

            // ✅ 记录日志
            if (request != null) {
                LogUtil.log(
                        getUserId(request),
                        getUsername(request),
                        "charge_item_add",
                        "添加收费项目：" + item.getItemName() + "（" + item.getItemId() + "）",
                        LogUtil.getClientIP(request)
                );
            }

            return true;
        }
        return false;
    }

    /**
     * 更新收费项目（支持不传 request）
     */
    public boolean updateChargeItem(ChargeItem item) {
        return updateChargeItem(item, null);
    }

    /**
     * 更新收费项目（✅ 增加日志记录）
     */
    public boolean updateChargeItem(ChargeItem item, HttpServletRequest request) {
        if (item.getItemId() == null || item.getItemId().trim().isEmpty()) {
            throw new IllegalArgumentException("项目ID不能为空");
        }

        // 检查项目是否存在
        ChargeItem existItem = chargeItemDao.findById(item.getItemId());
        if (existItem == null) {
            throw new IllegalArgumentException("收费项目不存在");
        }

        // 验证项目信息
        validateChargeItem(item);

        int rows = chargeItemDao.update(item);
        if (rows > 0) {
            logger.info("更新收费项目成功：{}", item.getItemId());

            // ✅ 记录日志
            if (request != null) {
                LogUtil.log(
                        getUserId(request),
                        getUsername(request),
                        "charge_item_update",
                        "修改收费项目：" + item.getItemName() + "（" + item.getItemId() + "）",
                        LogUtil.getClientIP(request)
                );
            }

            return true;
        }
        return false;
    }

    /**
     * 删除收费项目（支持不传 request）
     */
    public boolean deleteChargeItem(String itemId) {
        return deleteChargeItem(itemId, null);
    }

    /**
     * 删除收费项目（✅ 增加日志记录）
     */
    public boolean deleteChargeItem(String itemId, HttpServletRequest request) {
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new IllegalArgumentException("项目ID不能为空");
        }

        // 查询项目信息（用于日志）
        ChargeItem item = chargeItemDao.findById(itemId);
        String itemName = item != null ? item.getItemName() : "未知";

        int rows = chargeItemDao.delete(itemId);
        if (rows > 0) {
            logger.info("删除收费项目成功：{}", itemId);

            // ✅ 记录日志
            if (request != null) {
                LogUtil.log(
                        getUserId(request),
                        getUsername(request),
                        "charge_item_delete",
                        "删除收费项目：" + itemName + "（" + itemId + "）",
                        LogUtil.getClientIP(request)
                );
            }

            return true;
        }
        return false;
    }

    /**
     * 启用/禁用收费项目（支持不传 request）
     */
    public boolean updateStatus(String itemId, Integer status) {
        return updateStatus(itemId, status, null);
    }

    /**
     * 启用/禁用收费项目（✅ 增加日志记录）
     */
    public boolean updateStatus(String itemId, Integer status, HttpServletRequest request) {
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new IllegalArgumentException("项目ID不能为空");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new IllegalArgumentException("状态值无效");
        }

        // 查询项目信息（用于日志）
        ChargeItem item = chargeItemDao.findById(itemId);
        String itemName = item != null ? item.getItemName() : "未知";

        int rows = chargeItemDao.updateStatus(itemId, status);
        if (rows > 0) {
            logger.info("更新收费项目状态成功：项目ID={}, 状态={}", itemId, status);

            // ✅ 记录日志
            if (request != null) {
                String statusDesc = status == 1 ? "启用" : "禁用";
                LogUtil.log(
                        getUserId(request),
                        getUsername(request),
                        "charge_item_status",
                        statusDesc + "收费项目：" + itemName + "（" + itemId + "）",
                        LogUtil.getClientIP(request)
                );
            }

            return true;
        }
        return false;
    }

    /**
     * 🔧 验证收费项目信息（完整修复版）
     */
    private void validateChargeItem(ChargeItem item) {
        if (item == null) {
            throw new IllegalArgumentException("收费项目信息不能为空");
        }

        // ========== 基本字段验证 ==========
        if (item.getItemId() == null || item.getItemId().trim().isEmpty()) {
            throw new IllegalArgumentException("项目编号不能为空");
        }
        if (item.getItemName() == null || item.getItemName().trim().isEmpty()) {
            throw new IllegalArgumentException("项目名称不能为空");
        }
        if (item.getChargeCycle() == null || item.getChargeCycle().trim().isEmpty()) {
            throw new IllegalArgumentException("收费周期不能为空");
        }
        if (item.getCalculationType() == null || item.getCalculationType().trim().isEmpty()) {
            throw new IllegalArgumentException("计算类型不能为空");
        }

        // ========== 项目编号验证 ==========
        // 允许2-4位数字或字母数字组合
        if (!item.getItemId().matches("^[A-Z0-9]{2,4}$")) {
            throw new IllegalArgumentException("项目编号必须为2-4位大写字母或数字");
        }

        // ========== 收费周期验证 ==========
        if (!item.getChargeCycle().matches("^(monthly|quarterly|yearly|once)$")) {
            throw new IllegalArgumentException("收费周期无效，必须为：monthly（月）、quarterly（季）、yearly（年）、once（一次性）");
        }

        // ========== 计算类型验证 ==========
        if (!item.getCalculationType().matches("^(area_based|fixed)$")) {
            throw new IllegalArgumentException("计算类型无效，必须为：area_based（按面积）、fixed（固定金额）");
        }

        // ========== 固定金额验证 ==========
        if ("fixed".equals(item.getCalculationType())) {
            if (item.getFixedAmount() == null || item.getFixedAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("固定金额必须大于0");
            }
            // 验证金额范围（0.01 - 999999.99）
            if (item.getFixedAmount().compareTo(new BigDecimal("999999.99")) > 0) {
                throw new IllegalArgumentException("固定金额不能超过999999.99元");
            }
        }

        // ========== 按面积计算验证 ==========
        if ("area_based".equals(item.getCalculationType())) {
            if (item.getFormula() == null || item.getFormula().trim().isEmpty()) {
                throw new IllegalArgumentException("按面积计算时，计算公式不能为空");
            }
        }

        // ========== 🔧 滞纳金比例验证（关键修复） ==========
        if (item.getLateFeeRate() != null) {
            // 允许 0 到 1 之间的任意小数（包括 0.0005）
            if (item.getLateFeeRate().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("滞纳金比例不能为负数");
            }
            if (item.getLateFeeRate().compareTo(BigDecimal.ONE) > 0) {
                throw new IllegalArgumentException("滞纳金比例不能超过1（即100%）");
            }

            // 可选：验证精度（最多6位小数）
            if (item.getLateFeeRate().scale() > 6) {
                throw new IllegalArgumentException("滞纳金比例最多支持6位小数");
            }

            logger.info("滞纳金比例验证通过：{}", item.getLateFeeRate());
        }

        // ========== 宽限期验证 ==========
        if (item.getGracePeriod() != null) {
            if (item.getGracePeriod() < 0) {
                throw new IllegalArgumentException("宽限期不能为负数");
            }
            if (item.getGracePeriod() > 365) {
                throw new IllegalArgumentException("宽限期不能超过365天");
            }
        }

        // ========== 状态验证 ==========
        if (item.getStatus() != null && item.getStatus() != 0 && item.getStatus() != 1) {
            throw new IllegalArgumentException("状态值无效，必须为：0（禁用）或 1（启用）");
        }

        logger.info("收费项目验证通过：{} - {}", item.getItemId(), item.getItemName());
    }

    // ========== 辅助方法 ==========

    /**
     * 从 Session 获取当前用户ID
     */
    private Integer getUserId(HttpServletRequest request) {
        if (request == null) return 0;
        try {
            Object userId = request.getSession(false) != null ?
                    request.getSession(false).getAttribute("userId") : null;
            return userId != null ? (Integer) userId : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 从 Session 获取当前用户名
     */
    private String getUsername(HttpServletRequest request) {
        if (request == null) return "system";
        try {
            Object username = request.getSession(false) != null ?
                    request.getSession(false).getAttribute("username") : null;
            return username != null ? username.toString() : "system";
        } catch (Exception e) {
            return "system";
        }
    }
}
