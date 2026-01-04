package com.property.servlet.owner;

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
import java.util.Map;

/**
 * 业主端 - 个人信息管理
 */
@WebServlet("/owner/info")
public class OwnerInfoServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(OwnerInfoServlet.class);
    private OwnerService ownerService = new OwnerService();
    private UserService userService = new UserService();

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
     * 获取业主详细信息
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
            Owner owner = ownerService.findById(ownerId);
            if (owner != null) {
                // 转换为前端需要的格式
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

                logger.info("✅ 查询成功: {}", owner.getOwnerName());
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
     * 更新业主基本信息
     */
    public void updateInfo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        String ownerId = (String) session.getAttribute("username");

        if (ownerId == null || ownerId.trim().isEmpty()) {
            writeError(resp, "未登录或登录已过期");
            return;
        }

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
            Owner owner = new Owner();
            owner.setOwnerId(ownerId);
            owner.setPhone(phone);
            owner.setEmail(email);

            boolean success = ownerService.updateOwner(owner);
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
     * 修改密码
     */
    public void updatePassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute("username");

        if (username == null || username.trim().isEmpty()) {
            writeError(resp, "未登录或登录已过期");
            return;
        }

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

            // 更新密码
            boolean success = userService.updatePassword(username, newPassword);
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
