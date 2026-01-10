package com.property.servlet.owner;

import com.property.dao.HouseDao;
import com.property.entity.House;
import com.property.entity.Owner;
import com.property.entity.User;
import com.property.service.OwnerService;
import com.property.service.UserService;
import com.property.servlet.BaseServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业主端 - 个人信息管理（✅ 增加房屋详情查询）
 */
@WebServlet("/owner/info")
public class OwnerInfoServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(OwnerInfoServlet.class);
    private OwnerService ownerService = new OwnerService();
    private UserService userService = new UserService();
    private HouseDao houseDao = new HouseDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        logger.info("========================================");
        logger.info("📥 业主信息管理请求");
        logger.info("Action: {}", action);
        logger.info("========================================");

        if (action == null) {
            writeError(resp, "缺少 action 参数");
            return;
        }

        switch (action) {
            case "detail":
                detail(req, resp);
                break;
            case "houseDetail":  // 🔥 新增
                houseDetail(req, resp);
                break;
            default:
                writeError(resp, "未知操作: " + action);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        logger.info("========================================");
        logger.info("📝 业主信息修改请求");
        logger.info("Action: {}", action);
        logger.info("========================================");

        if (action == null) {
            writeError(resp, "缺少 action 参数");
            return;
        }

        switch (action) {
            case "updateInfo":
                updateInfo(req, resp);
                break;
            case "updatePassword":
                updatePassword(req, resp);
                break;
            default:
                writeError(resp, "未知操作: " + action);
        }
    }

    /**
     * 获取业主详细信息（✅ 增加房屋数量统计）
     */
    public void detail(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        String ownerId = (String) session.getAttribute("username");

        if (ownerId == null || ownerId.trim().isEmpty()) {
            writeError(resp, "未登录或登录已过期");
            return;
        }

        logger.info("🔍 查询业主信息: ownerId={}", ownerId);

        try {
            // 1. 查询业主基本信息
            Owner owner = ownerService.findById(ownerId);
            if (owner != null) {
                // 2. 🔥 查询业主名下的房屋列表
                List<House> houses = houseDao.findByOwnerId(ownerId);
                int houseCount = houses != null ? houses.size() : 0;

                logger.info("📊 业主 {} 名下房屋数量: {}", owner.getOwnerName(), houseCount);

                // 3. 转换为前端需要的格式
                Map<String, Object> result = new HashMap<>();
                result.put("ownerId", owner.getOwnerId());
                result.put("ownerName", owner.getOwnerName());
                result.put("phone", owner.getPhone());
                result.put("idCard", owner.getIdCard());
                result.put("houseId", owner.getHouseId());
                result.put("email", owner.getEmail());
                result.put("memberCount", owner.getMemberCount());
                result.put("registerDate", owner.getRegisterDate());
                result.put("remark", owner.getRemark());

                // 🔥 新增：房屋数量和房屋列表
                result.put("houseCount", houseCount);
                result.put("houses", houses);

                logger.info("✅ 查询成功: {} (房屋数量: {})", owner.getOwnerName(), houseCount);
                writeSuccess(resp, "查询成功", result);
            } else {
                logger.warn("⚠️ 业主不存在: {}", ownerId);
                writeError(resp, "业主信息不存在");
            }
        } catch (Exception e) {
            logger.error("❌ 查询业主信息失败", e);
            writeError(resp, "查询失败: " + e.getMessage());
        }
    }

    /**
     * 🏠 查询房屋详情（新增）
     */
    public void houseDetail(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        String currentOwnerId = (String) session.getAttribute("username");

        if (currentOwnerId == null || currentOwnerId.trim().isEmpty()) {
            writeError(resp, "未登录或登录已过期");
            return;
        }

        String houseId = getStringParameter(req, "houseId");

        logger.info("========================================");
        logger.info("🏠 查询房屋详情");
        logger.info("  houseId: {}", houseId);
        logger.info("  currentOwnerId: {}", currentOwnerId);
        logger.info("========================================");

        if (houseId == null || houseId.trim().isEmpty()) {
            logger.warn("⚠️ 房屋编号为空");
            writeError(resp, "房屋编号不能为空");
            return;
        }

        try {
            // 1. 查询房屋信息
            House house = houseDao.findById(houseId);

            if (house == null) {
                logger.warn("⚠️ 房屋不存在: houseId={}", houseId);
                writeError(resp, "房屋不存在");
                return;
            }

            // 2. 验证房屋是否属于当前业主
            if (!currentOwnerId.equals(house.getOwnerId())) {
                logger.warn("⚠️ 无权查看此房屋");
                logger.warn("  当前业主: {}", currentOwnerId);
                logger.warn("  房屋业主: {}", house.getOwnerId());
                writeError(resp, "无权查看此房屋信息");
                return;
            }

            // 3. 查询业主信息（补充房屋的业主姓名和电话）
            Owner owner = ownerService.findById(house.getOwnerId());
            if (owner != null) {
                house.setOwnerName(owner.getOwnerName());
                house.setOwnerPhone(owner.getPhone());
            }

            logger.info("✅ 查询成功");
            logger.info("  房屋编号: {}", house.getHouseId());
            logger.info("  楼栋: {}栋 {}单元 {}层",
                    house.getBuildingNo(), house.getUnitNo(), house.getFloor());
            logger.info("  面积: {} m²", house.getArea());
            logger.info("  户型: {}", house.getLayout());
            logger.info("========================================");

            writeSuccess(resp, "查询成功", house);

        } catch (Exception e) {
            logger.error("❌ 查询房屋详情失败", e);
            writeError(resp, "查询失败: " + e.getMessage());
        }
    }

    /**
     * ✅ 更新业主基本信息（增加日志记录）
     */
    public void updateInfo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        String ownerId = (String) session.getAttribute("username");

        if (ownerId == null || ownerId.trim().isEmpty()) {
            writeError(resp, "未登录或登录已过期");
            return;
        }

        // 🔥 获取当前用户（业主）
        User currentUser = getCurrentUser(req);

        String phone = getStringParameter(req, "phone");
        String email = getStringParameter(req, "email");

        logger.info("📝 更新业主信息: ownerId={}, phone={}, email={}", ownerId, phone, email);

        // 验证手机号格式
        if (phone != null && !phone.isEmpty() && !phone.matches("^1[3-9]\\d{9}$")) {
            writeError(resp, "手机号格式不正确");
            return;
        }

        // 验证邮箱格式
        if (email != null && !email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            writeError(resp, "邮箱格式不正确");
            return;
        }

        try {
            // 🔥 先查询完整的业主信息（因为 updateOwner 需要必填字段）
            Owner existOwner = ownerService.findById(ownerId);
            if (existOwner == null) {
                writeError(resp, "业主信息不存在");
                return;
            }

            // 🔥 只更新允许修改的字段
            existOwner.setPhone(phone);
            existOwner.setEmail(email);

            // ✅ 传入 operatorId 和 request 记录日志
            boolean success = ownerService.updateOwner(existOwner, currentUser.getUserId(), req);
            if (success) {
                logger.info("✅ 更新成功");
                writeSuccess(resp, "更新成功", null);
            } else {
                logger.warn("⚠️ 更新失败");
                writeError(resp, "更新失败");
            }
        } catch (Exception e) {
            logger.error("❌ 更新业主信息失败", e);
            writeError(resp, "更新失败: " + e.getMessage());
        }
    }

    /**
     * ✅ 修改密码（增加日志记录）
     */
    public void updatePassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute("username");

        if (username == null || username.trim().isEmpty()) {
            writeError(resp, "未登录或登录已过期");
            return;
        }

        // 🔥 获取当前用户（业主）
        User currentUser = getCurrentUser(req);

        String oldPassword = getStringParameter(req, "oldPassword");
        String newPassword = getStringParameter(req, "newPassword");
        String confirmPassword = getStringParameter(req, "confirmPassword");

        logger.info("🔐 修改密码请求: username={}", username);

        // 1. 参数验证
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            writeError(resp, "请输入原密码");
            return;
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            writeError(resp, "请输入新密码");
            return;
        }

        // 2. 密码长度验证
        if (newPassword.length() < 8) {
            writeError(resp, "新密码长度不能少于8位");
            return;
        }

        // 3. 🔥 密码强度验证(必须包含字母和数字)
        if (!isValidPassword(newPassword)) {
            writeError(resp, "新密码必须同时包含字母和数字");
            return;
        }

        // 4. 确认密码验证
        if (!newPassword.equals(confirmPassword)) {
            writeError(resp, "两次输入的新密码不一致");
            return;
        }

        // 5. 新旧密码不能相同
        if (oldPassword.equals(newPassword)) {
            writeError(resp, "新密码不能与原密码相同");
            return;
        }

        try {
            // 验证原密码
            User user = userService.login(username, oldPassword, "owner");
            if (user == null) {
                logger.warn("⚠️ 原密码错误");
                writeError(resp, "原密码错误");
                return;
            }

            // ✅ 更新密码（传入 operatorId 和 request 记录日志）
            boolean success = userService.updatePassword(username, newPassword, currentUser.getUserId(), req);
            if (success) {
                logger.info("✅ 密码修改成功");

                // 清除 session,要求重新登录
                session.invalidate();

                writeSuccess(resp, "密码修改成功,请重新登录", null);
            } else {
                logger.warn("⚠️ 密码修改失败");
                writeError(resp, "密码修改失败");
            }
        } catch (Exception e) {
            logger.error("❌ 修改密码失败", e);
            writeError(resp, "修改密码失败: " + e.getMessage());
        }
    }

    /**
     * 🔥 验证密码强度
     * 规则: 8位以上,必须包含字母和数字
     */
    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        // 检查是否包含字母
        boolean hasLetter = password.matches(".*[a-zA-Z].*");

        // 检查是否包含数字
        boolean hasNumber = password.matches(".*[0-9].*");

        return hasLetter && hasNumber;
    }
}
