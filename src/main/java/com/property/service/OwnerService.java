package com.property.service;

import com.property.dao.OwnerDao;
import com.property.dao.HouseDao;
import com.property.dao.UserDao;
import com.property.entity.Owner;
import com.property.entity.House;
import com.property.entity.User;
import com.property.util.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业主服务类
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
        return ownerDao.findById(ownerId);
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

        List<Owner> list = ownerDao.findByPage(pageNum, pageSize, keyword);
        long total = ownerDao.count(keyword);
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
     * 根据房屋ID查询业主
     */
    public Owner findByHouseId(String houseId) {
        if (houseId == null || houseId.trim().isEmpty()) {
            throw new IllegalArgumentException("房屋ID不能为空");
        }
        return ownerDao.findByHouseId(houseId);
    }

    /**
     * 添加业主（同时创建用户账号）
     */
    public boolean addOwner(Owner owner, String password) {
        // 参数验证
        validateOwner(owner);

        // 验证密码
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("登录密码不能为空");
        }
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("密码必须8位以上，且包含字母和数字");
        }

        // 检查身份证号是否已存在
        if (ownerDao.existsByIdCard(owner.getIdCard())) {
            throw new IllegalArgumentException("身份证号已存在：" + owner.getIdCard());
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

        // 生成业主ID（根据房屋楼栋号）
        String buildingNo = extractBuildingNo(owner.getHouseId());
        String ownerId = ownerDao.generateOwnerId(buildingNo);
        owner.setOwnerId(ownerId);

        // 设置登记日期
        if (owner.getRegisterDate() == null) {
            owner.setRegisterDate(new Date());
        }

        // 插入业主信息
        int rows = ownerDao.insert(owner);
        if (rows <= 0) {
            throw new RuntimeException("添加业主失败");
        }

        // 更新房屋的业主信息
        if (owner.getHouseId() != null && !owner.getHouseId().trim().isEmpty()) {
            houseDao.assignOwner(owner.getHouseId(), ownerId);
        }

        // 创建用户账号（用户名为业主ID）
        User user = new User();
        user.setUsername(ownerId);
        user.setPassword(MD5Util.encrypt(password));
        user.setRealName(owner.getOwnerName());
        user.setUserRole("owner");
        user.setPhone(owner.getPhone());
        user.setIdCard(owner.getIdCard());
        user.setStatus(1);

        userDao.insert(user);

        logger.info("添加业主成功：{} - {}", ownerId, owner.getOwnerName());
        return true;
    }

    /**
     * 更新业主信息
     */
    public boolean updateOwner(Owner owner) {
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

        // 如果房屋发生变化，需要更新房屋关联
        if (!owner.getHouseId().equals(existOwner.getHouseId())) {
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
            return true;
        }
        return false;
    }

    /**
     * 删除业主
     */
    public boolean deleteOwner(String ownerId) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("业主ID不能为空");
        }

        // 检查业主是否存在
        Owner owner = ownerDao.findById(ownerId);
        if (owner == null) {
            throw new IllegalArgumentException("业主不存在");
        }

        // 清除房屋的业主关联
        if (owner.getHouseId() != null) {
            houseDao.assignOwner(owner.getHouseId(), null);
        }

        // 删除业主
        int rows = ownerDao.delete(ownerId);
        if (rows > 0) {
            // 删除对应的用户账号
            User user = userDao.findByUsername(ownerId);
            if (user != null) {
                userDao.delete(user.getUserId());
            }

            logger.info("删除业主成功：{}", ownerId);
            return true;
        }
        return false;
    }

    /**
     * 查询欠费业主
     */
    public List<Owner> findArrearsOwners() {
        return ownerDao.findArrearsOwners();
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

        // 验证姓名（中文）
        if (!owner.getOwnerName().matches("^[\\u4e00-\\u9fa5]+$")) {
            throw new IllegalArgumentException("姓名必须为中文");
        }

        // 验证手机号（11位数字）
        if (!owner.getPhone().matches("^1[3-9]\\d{9}$")) {
            throw new IllegalArgumentException("手机号格式不正确，必须为11位数字");
        }

        // 验证身份证号（18位，含X）
        if (!owner.getIdCard().matches("^\\d{17}[\\dXx]$")) {
            throw new IllegalArgumentException("身份证号格式不正确，必须为18位（含X）");
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
     * 例如："1栋2单元301" -> "01"
     */
    private String extractBuildingNo(String houseId) {
        if (houseId == null || houseId.trim().isEmpty()) {
            return "01";
        }
        // 提取数字部分
        String[] parts = houseId.split("栋");
        if (parts.length > 0) {
            try {
                int buildingNum = Integer.parseInt(parts[0].trim());
                return String.format("%02d", buildingNum);
            } catch (NumberFormatException e) {
                return "01";
            }
        }
        return "01";
    }
}
