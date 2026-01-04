package com.property.service;

import com.property.dao.HouseDao;
import com.property.dao.PaymentRecordDao; // ✅ Added
import com.property.dao.RepairRecordDao;  // ✅ Added
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

    // ✅ 新增：引入其他DAO用于检查关联数据
    // 请确保这两个 DAO 类中已经添加了 countByHouseId(String houseId) 方法
    private PaymentRecordDao PaymentRecordDao = new PaymentRecordDao();
    private RepairRecordDao repairRecordDao = new RepairRecordDao();

    // ==================== 🔥 新增：业主端专用方法 ====================

    /**
     * 根据业主ID查询房屋列表（业主端使用）
     * @param ownerId 业主ID
     * @return 房屋列表
     */
    public List<House> findByOwnerId(String ownerId) {
        logger.info(">>> Service: 查询业主房屋，ownerId: {}", ownerId);

        if (ownerId == null || ownerId.trim().isEmpty()) {
            logger.warn("业主ID为空");
            throw new IllegalArgumentException("业主ID不能为空");
        }

        try {
            List<House> houses = houseDao.findByOwnerId(ownerId);
            logger.info("✅ Service: 查询到 {} 套房屋", houses.size());
            return houses;
        } catch (Exception e) {
            logger.error("❌ Service 查询业主房屋失败", e);
            throw new RuntimeException("查询房屋失败：" + e.getMessage(), e);
        }
    }

    // ==================== 🔥 统计方法（管理员端使用） ====================

    /**
     * 获取房屋总数
     */
    public int getTotalCount() {
        try {
            return houseDao.getTotalCount();
        } catch (Exception e) {
            logger.error("获取房屋总数失败", e);
            return 0;
        }
    }

    /**
     * 获取已入住房屋数量
     */
    public int getOccupiedCount() {
        try {
            return houseDao.getOccupiedCount();
        } catch (Exception e) {
            logger.error("获取已入住房屋数量失败", e);
            return 0;
        }
    }

    /**
     * 获取空置房屋数量
     */
    public int getVacantCount() {
        try {
            return houseDao.getVacantCount();
        } catch (Exception e) {
            logger.error("获取空置房屋数量失败", e);
            return 0;
        }
    }

    /**
     * 获取房屋入住率（百分比）
     */
    public double getOccupancyRate() {
        try {
            int total = getTotalCount();
            if (total == 0) {
                return 0.0;
            }
            int occupied = getOccupiedCount();
            return (occupied * 100.0) / total;
        } catch (Exception e) {
            logger.error("计算房屋入住率失败", e);
            return 0.0;
        }
    }

    // ==================== ✅ 管理员端原有方法 ====================

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
     * 根据条件查询房屋（用于导出）
     */
    public List<House> findByCondition(String keyword, String status) {
        return houseDao.findByCondition(keyword, status);
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
     * ✅ 删除房屋 (已修复逻辑)
     * 增加了对 业主、缴费记录、报修记录 的前置检查
     */
    public boolean deleteHouse(String houseId) {
        if (houseId == null || houseId.trim().isEmpty()) {
            throw new IllegalArgumentException("房屋ID不能为空");
        }

        // 1. 检查房屋是否存在
        House house = houseDao.findById(houseId);
        if (house == null) {
            throw new IllegalArgumentException("房屋不存在");
        }

        // 2. 检查房屋是否已分配业主
        if (house.getOwnerId() != null && !house.getOwnerId().trim().isEmpty()) {
            throw new IllegalArgumentException("该房屋已分配业主（" + house.getOwnerName() + "），请先在业主管理中解绑！");
        }

        // 3. ✅ 检查是否有历史缴费记录
        try {
            // 注意：PaymentRecordDao 需要实现 countByHouseId 方法
            int paymentCount = PaymentRecordDao.countByHouseId(houseId);
            if (paymentCount > 0) {
                throw new IllegalArgumentException("该房屋存在 " + paymentCount + " 条历史缴费记录，禁止删除！");
            }
        } catch (Exception e) {
            // 如果是 IllegalArgumentException 说明是我们自己抛出的，直接向上抛
            if (e instanceof IllegalArgumentException) throw e;
            // 其他异常（如数据库错误）记录日志
            logger.error("检查缴费记录失败", e);
        }

        // 4. ✅ 检查是否有报修记录
        try {
            // 注意：RepairRecordDao 需要实现 countByHouseId 方法
            int repairCount = repairRecordDao.countByHouseId(houseId);
            if (repairCount > 0) {
                throw new IllegalArgumentException("该房屋存在 " + repairCount + " 条报修记录，禁止删除！");
            }
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) throw e;
            logger.error("检查报修记录失败", e);
        }

        // 5. 执行删除
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
     * 根据ID列表查询房屋（用于导出选中数据）
     */
    public List<House> findByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("房屋ID列表不能为空");
        }
        return houseDao.findByIds(ids);
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
    }

    public List<Map<String, Object>> listBuildings() throws Exception {
        return houseDao.listBuildings();
    }

    public int countOccupied(String buildingId) throws Exception {
        return houseDao.countOccupied(buildingId);
    }
}
