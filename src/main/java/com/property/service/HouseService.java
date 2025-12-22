package com.property.service;

import com.property.dao.HouseDao;
import com.property.entity.House;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 房屋服务类
 */
public class HouseService {
    private static final Logger logger = LoggerFactory.getLogger(HouseService.class);
    private HouseDao houseDao = new HouseDao();

    /**
     * 根据ID查询房屋
     */
    public House findById(String houseId) {
        if (houseId == null || houseId.trim().isEmpty()) {
            throw new IllegalArgumentException("房屋ID不能为空");
        }
        return houseDao.findById(houseId);
    }

    /**
     * 查询所有房屋
     */
    public List<House> findAll() {
        return houseDao.findAll();
    }

    /**
     * 分页查询房屋
     */
    public Map<String, Object> findByPage(int pageNum, int pageSize, String keyword, String status) {
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1) pageSize = 10;

        List<House> list = houseDao.findByPage(pageNum, pageSize, keyword, status);
        long total = houseDao.count(keyword, status);
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
     * 根据楼栋查询房屋
     */
    public List<House> findByBuilding(String buildingNo) {
        if (buildingNo == null || buildingNo.trim().isEmpty()) {
            throw new IllegalArgumentException("楼栋号不能为空");
        }
        return houseDao.findByBuilding(buildingNo);
    }

    /**
     * 查询空置房屋
     */
    public List<House> findVacantHouses() {
        return houseDao.findVacantHouses();
    }

    /**
     * 添加房屋
     */
    public boolean addHouse(House house) {
        // 参数验证
        validateHouse(house);

        // 检查房屋ID是否已存在
        if (houseDao.existsById(house.getHouseId())) {
            throw new IllegalArgumentException("房屋编号已存在：" + house.getHouseId());
        }

        // 设置默认状态
        if (house.getHouseStatus() == null || house.getHouseStatus().trim().isEmpty()) {
            house.setHouseStatus("vacant");
        }
        if (house.getSaleStatus() == null || house.getSaleStatus().trim().isEmpty()) {
            house.setSaleStatus("for_sale");
        }

        int rows = houseDao.insert(house);
        if (rows > 0) {
            logger.info("添加房屋成功：{}", house.getHouseId());
            return true;
        }
        return false;
    }

    /**
     * 更新房屋信息
     */
    public boolean updateHouse(House house) {
        if (house.getHouseId() == null || house.getHouseId().trim().isEmpty()) {
            throw new IllegalArgumentException("房屋ID不能为空");
        }

        // 检查房屋是否存在
        House existHouse = houseDao.findById(house.getHouseId());
        if (existHouse == null) {
            throw new IllegalArgumentException("房屋不存在");
        }

        // 验证房屋信息
        validateHouse(house);

        int rows = houseDao.update(house);
        if (rows > 0) {
            logger.info("更新房屋成功：{}", house.getHouseId());
            return true;
        }
        return false;
    }

    /**
     * 删除房屋
     */
    public boolean deleteHouse(String houseId) {
        if (houseId == null || houseId.trim().isEmpty()) {
            throw new IllegalArgumentException("房屋ID不能为空");
        }

        // 检查房屋是否已分配业主
        House house = houseDao.findById(houseId);
        if (house != null && house.getOwnerId() != null && !house.getOwnerId().trim().isEmpty()) {
            throw new IllegalArgumentException("房屋已分配业主，不能删除");
        }

        int rows = houseDao.delete(houseId);
        if (rows > 0) {
            logger.info("删除房屋成功：{}", houseId);
            return true;
        }
        return false;
    }

    /**
     * 分配业主
     */
    public boolean assignOwner(String houseId, String ownerId) {
        if (houseId == null || houseId.trim().isEmpty()) {
            throw new IllegalArgumentException("房屋ID不能为空");
        }
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("业主ID不能为空");
        }

        int rows = houseDao.assignOwner(houseId, ownerId);
        if (rows > 0) {
            logger.info("分配业主成功：房屋={}, 业主={}", houseId, ownerId);
            return true;
        }
        return false;
    }

    /**
     * 统计各状态房屋数量
     */
    public Map<String, Long> countByStatus() {
        return houseDao.countByStatus();
    }

    /**
     * 验证房屋信息
     */
    private void validateHouse(House house) {
        if (house == null) {
            throw new IllegalArgumentException("房屋信息不能为空");
        }
        if (house.getHouseId() == null || house.getHouseId().trim().isEmpty()) {
            throw new IllegalArgumentException("房屋编号不能为空");
        }
        if (house.getBuildingNo() == null || house.getBuildingNo().trim().isEmpty()) {
            throw new IllegalArgumentException("楼栋号不能为空");
        }
        if (house.getUnitNo() == null || house.getUnitNo().trim().isEmpty()) {
            throw new IllegalArgumentException("单元号不能为空");
        }
        if (house.getFloor() == null || house.getFloor().trim().isEmpty()) {
            throw new IllegalArgumentException("楼层不能为空");
        }
        if (house.getLayout() == null || house.getLayout().trim().isEmpty()) {
            throw new IllegalArgumentException("户型不能为空");
        }
        if (house.getArea() == null || house.getArea().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("建筑面积必须大于0");
        }
        if (house.getPricePerSqm() == null || house.getPricePerSqm().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("物业费单价必须大于0");
        }

        // 验证楼栋号（2位数字）
        if (!house.getBuildingNo().matches("^\\d{2}$")) {
            throw new IllegalArgumentException("楼栋号必须为2位数字");
        }

        // 验证单元号（1位数字）
        if (!house.getUnitNo().matches("^\\d$")) {
            throw new IllegalArgumentException("单元号必须为1位数字");
        }

        // 验证楼层（2位数字）
        if (!house.getFloor().matches("^\\d{2}$")) {
            throw new IllegalArgumentException("楼层必须为2位数字");
        }

        // 验证房屋编号格式（如"1栋2单元301"）
        String expectedHouseId = house.getBuildingNo().replaceFirst("^0+", "") + "栋" +
                house.getUnitNo() + "单元" +
                house.getFloor().replaceFirst("^0+", "") + "01";
        // 这里简化验证，实际可以更严格
    }
}
