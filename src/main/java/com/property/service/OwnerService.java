package com.property.service;

import com.property.dao.OwnerDao;
import com.property.dao.HouseDao;
import com.property.dao.UserDao;
import com.property.entity.Owner;
import com.property.entity.House;
import com.property.entity.User;
import com.property.util.MD5Util;
import com.property.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 业主服务类（✅ 增加日志记录）
 */
public class OwnerService {
    private static final Logger logger = LoggerFactory.getLogger(OwnerService.class);
    private OwnerDao ownerDao = new OwnerDao();
    private HouseDao houseDao = new HouseDao();
    private UserDao userDao = new UserDao();

    /**
     * 根据ID查询业主
     */
    public Owner findById(String ownerId) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("业主ID不能为空");
        }

        Owner owner = ownerDao.findById(ownerId);

        // 🔥 添加房屋数量
        if (owner != null) {
            try {
                int houseCount = ownerDao.countHousesByOwnerId(ownerId);
                owner.setHouseCount(houseCount);
            } catch (Exception e) {
                logger.warn("查询业主 {} 的房屋数量失败", ownerId, e);
                owner.setHouseCount(0);
            }
        }

        return owner;
    }

    /**
     * 查询所有业主
     */
    public List<Owner> findAll() {
        return ownerDao.findAll();
    }

    /**
     * 分页查询业主
     */
    public Map<String, Object> findByPage(int pageNum, int pageSize, String keyword) {
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1) pageSize = 10;

        logger.info("业主分页查询 - pageNum: {}, pageSize: {}, keyword: {}", pageNum, pageSize, keyword);

        List<Owner> list = ownerDao.findByPage(pageNum, pageSize, keyword);
        int total = ownerDao.count(keyword);
        int totalPages = (int) Math.ceil((double) total / pageSize);

        // 🔥 为每个业主添加房屋数量
        for (Owner owner : list) {
            try {
                int houseCount = ownerDao.countHousesByOwnerId(owner.getOwnerId());
                owner.setHouseCount(houseCount);
            } catch (Exception e) {
                logger.warn("查询业主 {} 的房屋数量失败", owner.getOwnerId(), e);
                owner.setHouseCount(0);
            }
        }

        logger.info("查询结果 - 总记录数: {}, 当前页记录数: {}", total, list.size());

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", totalPages);

        return result;
    }

    /**
     * 根据房屋ID查询业主
     */
    public Owner findByHouseId(String houseId) {
        if (houseId == null || houseId.trim().isEmpty()) {
            throw new IllegalArgumentException("房屋ID不能为空");
        }
        return ownerDao.findByHouseId(houseId);
    }

    /**
     * 添加业主（支持不传 request）
     */
    public boolean addOwner(Owner owner, String password) {
        return addOwner(owner, password, null, null);
    }

    /**
     * ✅ 添加业主（增加日志记录）
     *
     * @param owner 业主信息
     * @param password 登录密码
     * @param operatorId 操作员ID
     * @param request HTTP请求对象（用于记录日志）
     * @return 是否成功
     */
    public boolean addOwner(Owner owner, String password, Integer operatorId, HttpServletRequest request) {
        // ========================================
        // 1. 参数验证
        // ========================================
        validateOwner(owner);

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("登录密码不能为空");
        }
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("密码必须8位以上，且包含字母和数字");
        }

        // ========================================
        // 2. 业务规则校验
        // ========================================
        // 检查身份证号是否已存在
        if (ownerDao.existsByIdCard(owner.getIdCard())) {
            throw new IllegalArgumentException("身份证号已存在：" + owner.getIdCard());
        }

        // 检查手机号是否已存在
        if (ownerDao.existsByPhone(owner.getPhone())) {
            throw new IllegalArgumentException("手机号已存在：" + owner.getPhone());
        }

        // 检查房屋是否存在且未分配
        if (owner.getHouseId() != null && !owner.getHouseId().trim().isEmpty()) {
            House house = houseDao.findById(owner.getHouseId());
            if (house == null) {
                throw new IllegalArgumentException("房屋不存在：" + owner.getHouseId());
            }
            if (house.getOwnerId() != null && !house.getOwnerId().trim().isEmpty()) {
                throw new IllegalArgumentException("房屋已分配给其他业主");
            }
        }

        // ========================================
        // 3. 生成业主ID
        // ========================================
        String ownerId = ownerDao.generateOwnerId(owner.getHouseId());
        owner.setOwnerId(ownerId);

        // 设置登记日期
        if (owner.getRegisterDate() == null) {
            owner.setRegisterDate(new Date());
        }

        // ========================================
        // 4. 插入业主信息
        // ========================================
        int rows = ownerDao.insert(owner);
        if (rows <= 0) {
            throw new RuntimeException("添加业主失败");
        }

        // ========================================
        // 🔥 5. 手动创建用户账号（支持自定义密码）
        // ========================================
        try {
            User user = new User();
            user.setUsername(ownerId);
            user.setPassword(MD5Util.encrypt(password));
            user.setRealName(owner.getOwnerName());
            user.setUserRole("owner");
            user.setPhone(owner.getPhone());
            user.setIdCard(owner.getIdCard());
            user.setStatus(1);

            Integer userId = userDao.insert(user);
            if (userId == null || userId <= 0) {
                // 如果用户创建失败，回滚业主信息
                ownerDao.delete(ownerId);
                throw new RuntimeException("创建用户账号失败");
            }

            logger.info("✅ 创建用户账号成功：{}", ownerId);
        } catch (Exception e) {
            // 如果用户创建失败，回滚业主信息
            logger.error("❌ 创建用户账号失败，回滚业主信息", e);
            ownerDao.delete(ownerId);
            throw new RuntimeException("创建用户账号失败：" + e.getMessage(), e);
        }

        // ========================================
        // 🔥 6. 手动更新房屋的业主信息
        // ========================================
        if (owner.getHouseId() != null && !owner.getHouseId().trim().isEmpty()) {
            try {
                houseDao.assignOwner(owner.getHouseId(), ownerId);
                logger.info("✅ 更新房屋关联成功：{} -> {}", owner.getHouseId(), ownerId);
            } catch (Exception e) {
                logger.error("❌ 更新房屋关联失败", e);
                // 不回滚，因为房屋关联不是必须的
            }
        }

        logger.info("✅ 添加业主成功：{} - {}", ownerId, owner.getOwnerName());

        // ✅ 记录操作日志
        if (operatorId != null && request != null) {
            LogUtil.log(
                    operatorId,
                    "admin_" + operatorId,
                    "owner_add",
                    "添加业主：" + owner.getOwnerName() + "（" + ownerId + "）",
                    LogUtil.getClientIP(request)
            );
        }

        return true;
    }

    /**
     * 更新业主信息（支持不传 request）
     */
    public boolean updateOwner(Owner owner) {
        return updateOwner(owner, null, null);
    }

    /**
     * ✅ 更新业主信息（增加日志记录）
     *
     * @param owner 业主信息
     * @param operatorId 操作员ID
     * @param request HTTP请求对象（用于记录日志）
     * @return 是否成功
     */
    public boolean updateOwner(Owner owner, Integer operatorId, HttpServletRequest request) {
        if (owner.getOwnerId() == null || owner.getOwnerId().trim().isEmpty()) {
            throw new IllegalArgumentException("业主ID不能为空");
        }

        // 检查业主是否存在
        Owner existOwner = ownerDao.findById(owner.getOwnerId());
        if (existOwner == null) {
            throw new IllegalArgumentException("业主不存在");
        }

        // 验证业主信息
        validateOwner(owner);

        // 检查身份证号是否被其他业主使用
        Owner ownerByIdCard = ownerDao.findByIdCard(owner.getIdCard());
        if (ownerByIdCard != null && !ownerByIdCard.getOwnerId().equals(owner.getOwnerId())) {
            throw new IllegalArgumentException("身份证号已被其他业主使用");
        }

        // 检查手机号是否被其他业主使用
        Owner ownerByPhone = ownerDao.findByPhone(owner.getPhone());
        if (ownerByPhone != null && !ownerByPhone.getOwnerId().equals(owner.getOwnerId())) {
            throw new IllegalArgumentException("手机号已被其他业主使用");
        }

        // 如果房屋发生变化，需要更新房屋关联
        if (owner.getHouseId() != null && !owner.getHouseId().equals(existOwner.getHouseId())) {
            // 检查新房屋是否存在且未分配
            House newHouse = houseDao.findById(owner.getHouseId());
            if (newHouse == null) {
                throw new IllegalArgumentException("房屋不存在：" + owner.getHouseId());
            }
            if (newHouse.getOwnerId() != null && !newHouse.getOwnerId().equals(owner.getOwnerId())) {
                throw new IllegalArgumentException("房屋已分配给其他业主");
            }

            // 清除原房屋的业主关联
            if (existOwner.getHouseId() != null) {
                houseDao.assignOwner(existOwner.getHouseId(), null);
            }

            // 设置新房屋的业主关联
            houseDao.assignOwner(owner.getHouseId(), owner.getOwnerId());
        }

        int rows = ownerDao.update(owner);
        if (rows > 0) {
            // 同步更新用户信息
            User user = userDao.findByUsername(owner.getOwnerId());
            if (user != null) {
                user.setRealName(owner.getOwnerName());
                user.setPhone(owner.getPhone());
                user.setIdCard(owner.getIdCard());
                userDao.update(user);
            }

            logger.info("更新业主成功：{}", owner.getOwnerId());

            // ✅ 记录操作日志
            if (operatorId != null && request != null) {
                LogUtil.log(
                        operatorId,
                        "admin_" + operatorId,
                        "owner_update",
                        "更新业主：" + owner.getOwnerName() + "（" + owner.getOwnerId() + "）",
                        LogUtil.getClientIP(request)
                );
            }

            return true;
        }
        return false;
    }

    /**
     * 删除业主（支持不传 request）
     */
    public boolean deleteOwner(String ownerId) {
        return deleteOwner(ownerId, null, null);
    }

    /**
     * ✅ 删除业主（增加日志记录）
     *
     * @param ownerId 业主ID
     * @param operatorId 操作员ID
     * @param request HTTP请求对象（用于记录日志）
     * @return 是否成功
     */
    public boolean deleteOwner(String ownerId, Integer operatorId, HttpServletRequest request) {
        // ========================================
        // 1. 参数验证
        // ========================================
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("业主ID不能为空");
        }

        // ========================================
        // 2. 业主存在性检查
        // ========================================
        Owner owner = ownerDao.findById(ownerId);
        if (owner == null) {
            throw new IllegalArgumentException("业主不存在");
        }

        // ========================================
        // 🔥 3. 执行删除（触发器自动处理所有级联逻辑）
        // ========================================
        try {
            int rows = ownerDao.delete(ownerId);

            if (rows > 0) {
                logger.info("✅ 删除业主成功：{} (触发器已自动处理：检查未缴费、删除用户、清除房屋关联)", ownerId);
                return true;
            } else {
                logger.warn("⚠️ 删除业主失败：{} (未删除任何记录)", ownerId);
                return false;
            }
        } catch (Exception e) {
            // 🔥 捕获触发器抛出的错误
            String errorMsg = e.getMessage();

            if (errorMsg != null && errorMsg.contains("未缴费记录")) {
                // 触发器阻止删除
                logger.warn("⚠️ 删除业主失败：{} - {}", ownerId, errorMsg);
                throw new IllegalArgumentException(errorMsg);
            } else {
                // 其他数据库错误
                logger.error("❌ 删除业主失败：{}", ownerId, e);
                throw new RuntimeException("删除业主失败：" + errorMsg, e);
            }
        }
    }

    /**
     * 查询欠费业主
     */
    public List<Owner> findArrearsOwners() {
        return ownerDao.findArrearsOwners();
    }

    /**
     * 重置业主密码（支持不传 request）
     */
    public boolean resetPassword(String ownerId, String newPassword) {
        return resetPassword(ownerId, newPassword, null, null);
    }

    /**
     * ✅ 重置业主密码（增加日志记录）
     *
     * @param ownerId 业主ID
     * @param newPassword 新密码
     * @param operatorId 操作员ID
     * @param request HTTP请求对象（用于记录日志）
     * @return 是否成功
     */
    public boolean resetPassword(String ownerId, String newPassword, Integer operatorId, HttpServletRequest request) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("业主ID不能为空");
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("新密码不能为空");
        }

        if (!isValidPassword(newPassword)) {
            throw new IllegalArgumentException("密码必须8位以上，且包含字母和数字");
        }

        // 检查业主是否存在
        Owner owner = ownerDao.findById(ownerId);
        if (owner == null) {
            throw new IllegalArgumentException("业主不存在");
        }

        // 更新密码
        String encryptedPassword = MD5Util.encrypt(newPassword);
        boolean success = ownerDao.updatePassword(ownerId, encryptedPassword);

        if (success) {
            logger.info("重置业主密码成功：{}", ownerId);

            // ✅ 记录操作日志
            if (operatorId != null && request != null) {
                LogUtil.log(
                        operatorId,
                        "admin_" + operatorId,
                        "owner_reset_password",
                        "重置业主密码：" + owner.getOwnerName() + "（" + ownerId + "）",
                        LogUtil.getClientIP(request)
                );
            }
        } else {
            logger.warn("重置业主密码失败：{}", ownerId);
        }

        return success;
    }

    /**
     * ✅ 批量删除业主
     */
    public Map<String, Integer> batchDeleteOwners(List<String> ownerIds) {
        int successCount = 0;
        int failCount = 0;
        List<String> failedIds = new ArrayList<>();

        for (String ownerId : ownerIds) {
            try {
                boolean success = deleteOwner(ownerId);
                if (success) {
                    successCount++;
                } else {
                    failCount++;
                    failedIds.add(ownerId);
                }
            } catch (Exception e) {
                logger.error("批量删除业主失败: {}", ownerId, e);
                failCount++;
                failedIds.add(ownerId);
            }
        }

        Map<String, Integer> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);

        logger.info("批量删除业主完成 - 成功: {}, 失败: {}", successCount, failCount);
        if (!failedIds.isEmpty()) {
            logger.warn("删除失败的业主ID: {}", failedIds);
        }

        return result;
    }

    /**
     * ✅ 统计业主信息
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 总业主数
        int totalOwners = ownerDao.count(null);
        stats.put("totalOwners", totalOwners);

        // 欠费业主数
        List<Owner> arrearsOwners = ownerDao.findArrearsOwners();
        stats.put("arrearsOwners", arrearsOwners.size());

        // 正常业主数
        stats.put("normalOwners", totalOwners - arrearsOwners.size());

        // 本月新增业主数
        int monthlyNew = ownerDao.countMonthlyNew();
        stats.put("monthlyNew", monthlyNew);

        // 各楼栋业主数量
        List<Object[]> buildingStats = ownerDao.countByBuilding();
        stats.put("buildingStats", buildingStats);

        logger.info("业主统计信息 - 总数: {}, 欠费: {}, 正常: {}, 本月新增: {}",
                totalOwners, arrearsOwners.size(), totalOwners - arrearsOwners.size(), monthlyNew);

        return stats;
    }

    /**
     * ✅ 根据关键字搜索业主（不分页）
     */
    public List<Owner> searchOwners(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ownerDao.findAll();
        }
        return ownerDao.findByKeyword(keyword);
    }

    /**
     * ✅ 验证业主是否存在
     */
    public boolean existsById(String ownerId) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            return false;
        }
        Owner owner = ownerDao.findById(ownerId);
        return owner != null;
    }

    /**
     * ✅ 验证身份证号是否已被使用
     */
    public boolean existsByIdCard(String idCard) {
        if (idCard == null || idCard.trim().isEmpty()) {
            return false;
        }
        return ownerDao.existsByIdCard(idCard);
    }

    /**
     * ✅ 验证手机号是否已被使用
     */
    public boolean existsByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return ownerDao.existsByPhone(phone);
    }

    /**
     * ✅ 获取业主的房屋信息
     */
    public House getOwnerHouse(String ownerId) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("业主ID不能为空");
        }

        Owner owner = ownerDao.findById(ownerId);
        if (owner == null) {
            throw new IllegalArgumentException("业主不存在");
        }

        if (owner.getHouseId() == null || owner.getHouseId().trim().isEmpty()) {
            return null;
        }

        return houseDao.findById(owner.getHouseId());
    }

    /**
     * 更新业主状态（支持不传 request）
     */
    public boolean updateOwnerStatus(String ownerId, int status) {
        return updateOwnerStatus(ownerId, status, null, null);
    }

    /**
     * ✅ 更新业主状态（增加日志记录）
     *
     * @param ownerId 业主ID
     * @param status 状态（1=启用，0=禁用）
     * @param operatorId 操作员ID
     * @param request HTTP请求对象（用于记录日志）
     * @return 是否成功
     */
    public boolean updateOwnerStatus(String ownerId, int status, Integer operatorId, HttpServletRequest request) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("业主ID不能为空");
        }

        // 检查业主是否存在
        Owner owner = ownerDao.findById(ownerId);
        if (owner == null) {
            throw new IllegalArgumentException("业主不存在");
        }

        // 更新对应用户账号的状态
        User user = userDao.findByUsername(ownerId);
        if (user != null) {
            user.setStatus(status);
            int rows = userDao.update(user);
            if (rows > 0) {
                logger.info("更新业主状态成功：{} - 状态: {}", ownerId, status == 1 ? "启用" : "禁用");

                // ✅ 记录操作日志
                if (operatorId != null && request != null) {
                    LogUtil.log(
                            operatorId,
                            "admin_" + operatorId,
                            "owner_status",
                            (status == 1 ? "启用" : "禁用") + "业主：" + owner.getOwnerName() + "（" + ownerId + "）",
                            LogUtil.getClientIP(request)
                    );
                }

                return true;
            }
        }

        return false;
    }

    /**
     * ✅ 根据楼栋查询业主
     */
    public List<Owner> findByBuilding(String buildingNo) {
        if (buildingNo == null || buildingNo.trim().isEmpty()) {
            throw new IllegalArgumentException("楼栋号不能为空");
        }
        return ownerDao.findByBuilding(buildingNo);
    }

    /**
     * ✅ 批量导入业主
     */
    public Map<String, Object> batchImportOwners(List<Owner> owners, String defaultPassword) {
        int successCount = 0;
        int failCount = 0;
        List<String> errorMessages = new ArrayList<>();

        for (Owner owner : owners) {
            try {
                // 验证业主信息
                validateOwner(owner);

                // 检查是否已存在
                if (ownerDao.existsByIdCard(owner.getIdCard())) {
                    errorMessages.add("身份证号已存在：" + owner.getIdCard());
                    failCount++;
                    continue;
                }

                // 添加业主
                boolean success = addOwner(owner, defaultPassword);
                if (success) {
                    successCount++;
                } else {
                    failCount++;
                    errorMessages.add("添加失败：" + owner.getOwnerName());
                }

            } catch (Exception e) {
                logger.error("批量导入业主失败: {}", owner.getOwnerName(), e);
                failCount++;
                errorMessages.add(owner.getOwnerName() + ": " + e.getMessage());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("errorMessages", errorMessages);

        logger.info("批量导入业主完成 - 成功: {}, 失败: {}", successCount, failCount);

        return result;
    }

    /**
     * 验证业主信息
     */
    private void validateOwner(Owner owner) {
        if (owner == null) {
            throw new IllegalArgumentException("业主信息不能为空");
        }
        if (owner.getOwnerName() == null || owner.getOwnerName().trim().isEmpty()) {
            throw new IllegalArgumentException("业主姓名不能为空");
        }
        if (owner.getPhone() == null || owner.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("联系电话不能为空");
        }
        if (owner.getIdCard() == null || owner.getIdCard().trim().isEmpty()) {
            throw new IllegalArgumentException("身份证号不能为空");
        }
        if (owner.getHouseId() == null || owner.getHouseId().trim().isEmpty()) {
            throw new IllegalArgumentException("房屋编号不能为空");
        }

        // 验证姓名（中文或英文）
        if (!owner.getOwnerName().matches("^[\\u4e00-\\u9fa5a-zA-Z]+$")) {
            throw new IllegalArgumentException("姓名格式不正确");
        }

        // 验证手机号（11位数字，1开头）
        if (!owner.getPhone().matches("^1[3-9]\\d{9}$")) {
            throw new IllegalArgumentException("手机号格式不正确");
        }

        // 验证身份证号（18位，最后一位可以是X）
        if (!owner.getIdCard().matches("^\\d{17}[\\dXx]$")) {
            throw new IllegalArgumentException("身份证号格式不正确");
        }
    }

    /**
     * 验证密码强度（8位以上，含字母和数字）
     */
    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        return hasLetter && hasDigit;
    }

    /**
     * 从房屋编号中提取楼栋号
     */
    private String extractBuildingNo(String houseId) {
        if (houseId == null || houseId.trim().isEmpty()) {
            return "01";
        }

        // 如果是纯数字格式（如：01010101）
        if (houseId.matches("^\\d{8}$")) {
            return houseId.substring(0, 2);
        }

        // 如果包含"栋"字（如：1栋2单元301）
        String[] parts = houseId.split("栋");
        if (parts.length > 0) {
            try {
                int buildingNum = Integer.parseInt(parts[0].trim());
                return String.format("%02d", buildingNum);
            } catch (NumberFormatException e) {
                logger.warn("无法解析楼栋号: {}", houseId);
                return "01";
            }
        }

        return "01";
    }

    /**
     * 获取业主总数
     */
    public int getTotalCount() {
        try {
            return ownerDao.getTotalCount();
        } catch (Exception e) {
            logger.error("获取业主总数失败", e);
            return 0;
        }
    }

    /**
     * 🔥 查询业主拥有的所有房屋
     * @param ownerId 业主ID
     * @return Map 包含业主姓名和房屋列表
     */
    public Map<String, Object> findOwnerHouses(String ownerId) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("业主ID不能为空");
        }

        // 查询业主信息
        Owner owner = ownerDao.findById(ownerId);
        if (owner == null) {
            throw new IllegalArgumentException("业主不存在");
        }

        // 查询业主的所有房屋
        List<Map<String, Object>> houses = ownerDao.findHousesByOwnerId(ownerId);

        // 封装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("ownerName", owner.getOwnerName());
        result.put("houses", houses);

        logger.info("查询业主 {} 的房屋列表，共 {} 套", ownerId, houses.size());

        return result;
    }
}
