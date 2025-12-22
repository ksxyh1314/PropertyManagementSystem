package com.property.service;

import com.property.dao.ChargeItemDao;
import com.property.entity.ChargeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收费项目服务类
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
     * 添加收费项目
     */
    public boolean addChargeItem(ChargeItem item) {
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
            item.setLateFeeRate(new BigDecimal("0.0005"));
        }
        if (item.getStatus() == null) {
            item.setStatus(1);
        }

        int rows = chargeItemDao.insert(item);
        if (rows > 0) {
            logger.info("添加收费项目成功：{} - {}", item.getItemId(), item.getItemName());
            return true;
        }
        return false;
    }

    /**
     * 更新收费项目
     */
    public boolean updateChargeItem(ChargeItem item) {
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
            return true;
        }
        return false;
    }

    /**
     * 删除收费项目
     */
    public boolean deleteChargeItem(String itemId) {
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new IllegalArgumentException("项目ID不能为空");
        }

        int rows = chargeItemDao.delete(itemId);
        if (rows > 0) {
            logger.info("删除收费项目成功：{}", itemId);
            return true;
        }
        return false;
    }

    /**
     * 启用/禁用收费项目
     */
    public boolean updateStatus(String itemId, Integer status) {
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new IllegalArgumentException("项目ID不能为空");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new IllegalArgumentException("状态值无效");
        }

        int rows = chargeItemDao.updateStatus(itemId, status);
        if (rows > 0) {
            logger.info("更新收费项目状态成功：项目ID={}, 状态={}", itemId, status);
            return true;
        }
        return false;
    }

    /**
     * 验证收费项目信息
     */
    private void validateChargeItem(ChargeItem item) {
        if (item == null) {
            throw new IllegalArgumentException("收费项目信息不能为空");
        }
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

        // 验证项目编号（2位数字）
        if (!item.getItemId().matches("^\\d{2}$")) {
            throw new IllegalArgumentException("项目编号必须为2位数字");
        }

        // 验证收费周期
        if (!item.getChargeCycle().matches("^(monthly|quarterly|yearly)$")) {
            throw new IllegalArgumentException("收费周期无效，必须为：monthly、quarterly、yearly");
        }

        // 验证计算类型
        if (!item.getCalculationType().matches("^(area_based|fixed)$")) {
            throw new IllegalArgumentException("计算类型无效，必须为：area_based、fixed");
        }

        // 如果是固定金额，必须提供固定金额值
        if ("fixed".equals(item.getCalculationType())) {
            if (item.getFixedAmount() == null || item.getFixedAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("固定金额必须大于0");
            }
        }
    }
}
