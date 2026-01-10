package com.property.servlet;

import com.property.entity.House;
import com.property.entity.Owner;
import com.property.entity.User;
import com.property.service.HouseService;
import com.property.service.OwnerService;
import com.property.util.ExcelExportUtil;
import com.property.util.LogUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 业主管理Servlet（✅ 增加日志记录 + 自动更新房屋状态）
 */
@WebServlet("/admin/owner")
public class OwnerServlet extends BaseServlet {
    private OwnerService ownerService = new OwnerService();

    /**
     * 分页查询业主列表
     */
    public void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        int pageNum = getIntParameter(req, "pageNum", 1);
        int pageSize = getIntParameter(req, "pageSize", 10);
        String keyword = getStringParameter(req, "keyword");

        logger.info("业主列表查询 - pageNum: {}, pageSize: {}, keyword: {}", pageNum, pageSize, keyword);

        try {
            Map<String, Object> result = ownerService.findByPage(pageNum, pageSize, keyword);

            // ✅ 确保返回格式正确
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", result);

            logger.info("业主列表查询成功，返回 {} 条记录",
                    ((List<?>) result.get("list")).size());

            writeJson(resp, response);
        } catch (Exception e) {
            logger.error("查询业主列表失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询所有业主
     */
    public void findAll(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        try {
            List<Owner> owners = ownerService.findAll();
            logger.info("查询所有业主成功，共 {} 条", owners.size());
            writeSuccess(resp, "查询成功", owners);
        } catch (Exception e) {
            logger.error("查询业主失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID查询业主
     */
    public void findById(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        String ownerId = getStringParameter(req, "ownerId");
        if (ownerId == null || ownerId.isEmpty()) {
            writeError(resp, "业主ID不能为空");
            return;
        }

        try {
            Owner owner = ownerService.findById(ownerId);
            if (owner != null) {
                logger.info("查询业主成功: {}", ownerId);
                writeSuccess(resp, "查询成功", owner);
            } else {
                logger.warn("业主不存在: {}", ownerId);
                writeError(resp, "业主不存在");
            }
        } catch (Exception e) {
            logger.error("查询业主失败: {}", ownerId, e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }
    /**
     * ✅ 添加业主（增加日志记录 + 自动更新房屋状态）
     */
    public void add(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        // 获取当前用户
        User currentUser = getCurrentUser(req);

        String ownerName = getStringParameter(req, "ownerName");
        String phone = getStringParameter(req, "phone");
        String idCard = getStringParameter(req, "idCard");
        String houseId = getStringParameter(req, "houseId");
        String email = getStringParameter(req, "email");
        Integer memberCount = getIntParameter(req, "memberCount");
        String registerDateStr = getStringParameter(req, "registerDate");
        String remark = getStringParameter(req, "remark");
        String password = getStringParameter(req, "password");
        String ownerType = getStringParameter(req, "ownerType"); // 🔥 业主类型（owner/tenant）

        // ✅ 参数验证
        if (ownerName == null || ownerName.trim().isEmpty()) {
            writeError(resp, "业主姓名不能为空");
            return;
        }
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            writeError(resp, "请输入正确的手机号码");
            return;
        }
        if (idCard == null || !idCard.matches("^\\d{17}[\\dXx]$")) {
            writeError(resp, "请输入正确的身份证号");
            return;
        }
        if (houseId == null || houseId.trim().isEmpty()) {
            writeError(resp, "房屋编号不能为空");
            return;
        }
        if (password == null || password.length() < 8) {
            writeError(resp, "密码必须至少8位");
            return;
        }

        // 🔥 验证业主类型（默认为业主）
        if (ownerType == null || (!ownerType.equals("owner") && !ownerType.equals("tenant"))) {
            ownerType = "owner";
        }

        Owner owner = new Owner();
        owner.setOwnerName(ownerName.trim());
        owner.setPhone(phone);
        owner.setIdCard(idCard);
        owner.setHouseId(houseId);
        owner.setEmail(email);
        owner.setMemberCount(memberCount != null ? memberCount : 1);

        // 🔥 在备注中标记类型（可选，方便后续查看）
        String typeLabel = ownerType.equals("tenant") ? "[租户]" : "[业主]";
        if (remark != null && !remark.isEmpty()) {
            owner.setRemark(typeLabel + " " + remark);
        } else {
            owner.setRemark(typeLabel);
        }

        // 解析日期
        if (registerDateStr != null && !registerDateStr.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                owner.setRegisterDate(sdf.parse(registerDateStr));
            } catch (Exception e) {
                logger.warn("日期解析失败，使用当前日期: {}", registerDateStr);
                owner.setRegisterDate(new Date());
            }
        } else {
            owner.setRegisterDate(new Date());
        }

        try {
            // ✅ 传入 operatorId 和 request 记录日志
            boolean success = ownerService.addOwner(owner, password, currentUser.getUserId(), req);
            if (success) {
                // 🔥 根据业主类型自动更新房屋状态
                // ✅ 修复：直接传递 String 类型的 userId
                boolean houseUpdated = updateHouseStatusByOwnerType(
                        houseId,
                        ownerType,
                        owner.getOwnerId(),
                        currentUser.getUserId(),  // ✅ String 类型
                        req
                );

                String statusInfo = "";
                if (houseUpdated) {
                    if ("owner".equals(ownerType)) {
                        statusInfo = "，房屋已标记为【已售+已入住】";
                    } else if ("tenant".equals(ownerType)) {
                        statusInfo = "，房屋已标记为【已租+出租中】";
                    }
                }

                logger.info("添加业主成功: {} - {} (类型: {}){}",
                        owner.getOwnerId(), ownerName,
                        ownerType.equals("tenant") ? "租户" : "业主",
                        statusInfo);

                writeSuccess(resp, "添加业主成功" + statusInfo, owner.getOwnerId());
            } else {
                writeError(resp, "添加业主失败");
            }
        } catch (IllegalArgumentException e) {
            logger.warn("添加业主参数错误: {}", e.getMessage());
            writeError(resp, e.getMessage());
        } catch (Exception e) {
            logger.error("添加业主失败", e);
            writeError(resp, "添加业主失败：" + e.getMessage());
        }
    }

    /**
     * 🔥 根据业主类型自动更新房屋状态（不需要修改数据库表）
     * @param houseId 房屋ID
     * @param ownerType 业主类型（owner/tenant）
     * @param ownerId 业主ID
     * @param operatorId 操作员ID（String类型，会在方法内转换为Integer）
     * @param req HTTP请求对象
     * @return 是否更新成功
     */
    /**
     * 🔥 根据业主类型自动更新房屋状态（不需要修改数据库表）
     * @param houseId 房屋ID
     * @param ownerType 业主类型（owner/tenant）
     * @param ownerId 业主ID
     * @param operatorId 操作员ID（Integer类型）
     * @param req HTTP请求对象
     * @return 是否更新成功
     */
    private boolean updateHouseStatusByOwnerType(String houseId, String ownerType, String ownerId,
                                                 Integer operatorId, HttpServletRequest req) {
        try {
            HouseService houseService = new HouseService();
            House house = houseService.findById(houseId);

            if (house == null) {
                logger.warn("房屋不存在: {}", houseId);
                return false;
            }

            String oldSaleStatus = house.getSaleStatus();
            String oldHouseStatus = house.getHouseStatus();

            // 🔥 根据业主类型设置房屋状态
            if ("owner".equals(ownerType)) {
                // 业主 → 已售 + 已入住
                house.setSaleStatus("sold");
                house.setHouseStatus("occupied");
                logger.info("房屋 {} 标记为已售+已入住（业主: {}）", houseId, ownerId);
            } else if ("tenant".equals(ownerType)) {
                // 租户 → 已租 + 出租中
                house.setSaleStatus("leased");
                house.setHouseStatus("rented");
                logger.info("房屋 {} 标记为已租+出租中（租户: {}）", houseId, ownerId);
            } else {
                // 未知类型，不更新
                logger.warn("未知的业主类型: {}", ownerType);
                return false;
            }

            // 设置业主ID
            house.setOwnerId(ownerId);

            // 🔥 直接使用 Integer 类型的 operatorId
            boolean success = houseService.updateHouse(house, operatorId, req);

            if (success) {
                logger.info("房屋状态更新成功: {} → sale_status: {} → {}, house_status: {} → {}",
                        houseId,
                        oldSaleStatus, house.getSaleStatus(),
                        oldHouseStatus, house.getHouseStatus());

                // 🔥 记录房屋状态变更日志（使用 LogUtil）
                if (operatorId != null) {
                    String typeLabel = "owner".equals(ownerType) ? "业主" : "租户";
                    String logContent = String.format(
                            "添加%s【%s】后自动更新房屋状态：%s → sale_status: %s → %s, house_status: %s → %s",
                            typeLabel,
                            ownerId,
                            houseId,
                            oldSaleStatus, house.getSaleStatus(),
                            oldHouseStatus, house.getHouseStatus()
                    );

                    LogUtil.log(
                            operatorId,
                            "admin_" + operatorId,
                            "house_status_update",
                            logContent,
                            LogUtil.getClientIP(req)
                    );
                }

                return true;
            } else {
                logger.error("房屋状态更新失败: {}", houseId);
                return false;
            }

        } catch (Exception e) {
            logger.error("更新房屋状态失败: {}", houseId, e);
            return false;
        }
    }

    /**
     * ✅ 更新业主（增加日志记录）
     */
    public void update(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        // 获取当前用户
        User currentUser = getCurrentUser(req);

        String ownerId = getStringParameter(req, "ownerId");
        if (ownerId == null || ownerId.isEmpty()) {
            writeError(resp, "业主ID不能为空");
            return;
        }

        String ownerName = getStringParameter(req, "ownerName");
        String phone = getStringParameter(req, "phone");
        String idCard = getStringParameter(req, "idCard");
        String houseId = getStringParameter(req, "houseId");
        String email = getStringParameter(req, "email");
        Integer memberCount = getIntParameter(req, "memberCount");
        String remark = getStringParameter(req, "remark");

        // ✅ 参数验证
        if (ownerName == null || ownerName.trim().isEmpty()) {
            writeError(resp, "业主姓名不能为空");
            return;
        }
        if (phone != null && !phone.matches("^1[3-9]\\d{9}$")) {
            writeError(resp, "请输入正确的手机号码");
            return;
        }
        if (idCard != null && !idCard.matches("^\\d{17}[\\dXx]$")) {
            writeError(resp, "请输入正确的身份证号");
            return;
        }

        Owner owner = new Owner();
        owner.setOwnerId(ownerId);
        owner.setOwnerName(ownerName.trim());
        owner.setPhone(phone);
        owner.setIdCard(idCard);
        owner.setHouseId(houseId);
        owner.setEmail(email);
        owner.setMemberCount(memberCount);
        owner.setRemark(remark);

        try {
            // ✅ 传入 operatorId 和 request 记录日志
            boolean success = ownerService.updateOwner(owner, currentUser.getUserId(), req);
            if (success) {
                logger.info("更新业主成功: {} - {}", ownerId, ownerName);
                writeSuccess(resp, "更新业主成功");
            } else {
                writeError(resp, "更新业主失败");
            }
        } catch (IllegalArgumentException e) {
            logger.warn("更新业主参数错误: {}", e.getMessage());
            writeError(resp, e.getMessage());
        } catch (Exception e) {
            logger.error("更新业主失败: {}", ownerId, e);
            writeError(resp, "更新业主失败：" + e.getMessage());
        }
    }

    /**
     * ✅ 删除业主（增加日志记录）
     */
    public void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        // 获取当前用户
        User currentUser = getCurrentUser(req);

        String ownerId = getStringParameter(req, "ownerId");
        if (ownerId == null || ownerId.isEmpty()) {
            writeError(resp, "业主ID不能为空");
            return;
        }

        try {
            // ✅ 传入 operatorId 和 request 记录日志
            boolean success = ownerService.deleteOwner(ownerId, currentUser.getUserId(), req);
            if (success) {
                logger.info("删除业主成功: {}", ownerId);
                writeSuccess(resp, "删除业主成功");
            } else {
                writeError(resp, "删除业主失败");
            }
        } catch (Exception e) {
            logger.error("删除业主失败: {}", ownerId, e);
            writeError(resp, "删除业主失败：" + e.getMessage());
        }
    }

    /**
     * 查询欠费业主
     */
    public void findArrears(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        try {
            List<Owner> owners = ownerService.findArrearsOwners();
            logger.info("查询欠费业主成功，共 {} 条", owners.size());
            writeSuccess(resp, "查询成功", owners);
        } catch (Exception e) {
            logger.error("查询欠费业主失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * ✅ 导出所有业主数据
     */
    public void export(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        String keyword = getStringParameter(req, "keyword");

        try {
            logger.info("开始导出业主数据，关键字: {}", keyword);

            // 查询数据
            List<Owner> owners;
            if (keyword != null && !keyword.trim().isEmpty()) {
                // 如果有搜索条件，导出搜索结果
                Map<String, Object> result = ownerService.findByPage(1, Integer.MAX_VALUE, keyword);
                owners = (List<Owner>) result.get("list");
            } else {
                // 否则导出全部
                owners = ownerService.findAll();
            }

            if (owners == null || owners.isEmpty()) {
                writeError(resp, "没有可导出的数据");
                return;
            }

            // 设置响应头
            String fileName = "业主数据_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx";
            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            resp.setHeader("Content-Disposition", "attachment; filename=" +
                    new String(fileName.getBytes("UTF-8"), "ISO-8859-1"));

            // 导出Excel
            OutputStream out = resp.getOutputStream();
            ExcelExportUtil.exportOwnerList(owners, out);
            out.flush();

            logger.info("导出业主数据成功，共 {} 条", owners.size());

        } catch (Exception e) {
            logger.error("导出业主数据失败", e);
            writeError(resp, "导出失败：" + e.getMessage());
        }
    }

    /**
     * ✅ 导出选中的业主数据
     */
    public void exportSelected(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        String idsParam = getStringParameter(req, "ids");
        if (idsParam == null || idsParam.trim().isEmpty()) {
            writeError(resp, "请选择要导出的数据");
            return;
        }

        try {
            logger.info("开始导出选中业主数据，IDs: {}", idsParam);

            // 解析ID列表
            String[] idArray = idsParam.split(",");
            List<Owner> owners = new ArrayList<>();

            for (String id : idArray) {
                Owner owner = ownerService.findById(id.trim());
                if (owner != null) {
                    owners.add(owner);
                }
            }

            if (owners.isEmpty()) {
                writeError(resp, "没有找到要导出的数据");
                return;
            }

            // 设置响应头
            String fileName = "业主数据_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx";
            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            resp.setHeader("Content-Disposition", "attachment; filename=" +
                    new String(fileName.getBytes("UTF-8"), "ISO-8859-1"));

            // 导出Excel
            OutputStream out = resp.getOutputStream();
            ExcelExportUtil.exportOwnerList(owners, out);
            out.flush();

            logger.info("导出选中业主数据成功，共 {} 条", owners.size());

        } catch (Exception e) {
            logger.error("导出选中业主数据失败", e);
            writeError(resp, "导出失败：" + e.getMessage());
        }
    }

    /**
     * ✅ 批量删除业主（增加日志记录）
     */
    public void batchDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        // 获取当前用户
        User currentUser = getCurrentUser(req);

        String idsParam = getStringParameter(req, "ids");
        if (idsParam == null || idsParam.trim().isEmpty()) {
            writeError(resp, "请选择要删除的数据");
            return;
        }

        try {
            logger.info("开始批量删除业主，IDs: {}", idsParam);

            String[] idArray = idsParam.split(",");
            int successCount = 0;
            int failCount = 0;

            for (String id : idArray) {
                try {
                    // ✅ 传入 operatorId 和 request 记录日志
                    boolean success = ownerService.deleteOwner(id.trim(), currentUser.getUserId(), req);
                    if (success) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch (Exception e) {
                    logger.error("删除业主失败: {}", id, e);
                    failCount++;
                }
            }

            String message = String.format("成功删除 %d 条记录", successCount);
            if (failCount > 0) {
                message += String.format("，失败 %d 条", failCount);
            }

            logger.info("批量删除业主完成: {}", message);
            writeSuccess(resp, message);

        } catch (Exception e) {
            logger.error("批量删除业主失败", e);
            writeError(resp, "批量删除失败：" + e.getMessage());
        }
    }

    /**
     * ✅ 统计业主信息
     */
    public void statistics(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        try {
            Map<String, Object> stats = new HashMap<>();

            // 总业主数
            List<Owner> allOwners = ownerService.findAll();
            stats.put("totalOwners", allOwners.size());

            // 欠费业主数
            List<Owner> arrearsOwners = ownerService.findArrearsOwners();
            stats.put("arrearsOwners", arrearsOwners.size());

            // 正常业主数
            stats.put("normalOwners", allOwners.size() - arrearsOwners.size());

            logger.info("查询业主统计信息成功");
            writeSuccess(resp, "查询成功", stats);

        } catch (Exception e) {
            logger.error("查询业主统计信息失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * ✅ 重置业主密码（增加日志记录）
     */
    public void resetPassword(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        // 获取当前用户
        User currentUser = getCurrentUser(req);

        String ownerId = getStringParameter(req, "ownerId");
        String newPassword = getStringParameter(req, "newPassword");

        if (ownerId == null || ownerId.isEmpty()) {
            writeError(resp, "业主ID不能为空");
            return;
        }

        if (newPassword == null || newPassword.length() < 8) {
            writeError(resp, "新密码必须至少8位");
            return;
        }

        try {
            // ✅ 传入 operatorId 和 request 记录日志
            boolean success = ownerService.resetPassword(ownerId, newPassword, currentUser.getUserId(), req);
            if (success) {
                logger.info("重置业主密码成功: {}", ownerId);
                writeSuccess(resp, "重置密码成功");
            } else {
                writeError(resp, "重置密码失败");
            }
        } catch (Exception e) {
            logger.error("重置业主密码失败: {}", ownerId, e);
            writeError(resp, "重置密码失败：" + e.getMessage());
        }
    }

    /**
     * 🔥 查询业主的所有房屋
     */
    public void findHouses(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        String ownerId = getStringParameter(req, "ownerId");
        if (ownerId == null || ownerId.isEmpty()) {
            writeError(resp, "业主ID不能为空");
            return;
        }

        try {
            Map<String, Object> result = ownerService.findOwnerHouses(ownerId);
            logger.info("查询业主 {} 的房屋列表成功，共 {} 套",
                    ownerId,
                    ((List<?>) result.get("houses")).size());
            writeSuccess(resp, "查询成功", result);
        } catch (IllegalArgumentException e) {
            logger.warn("查询业主房屋参数错误: {}", e.getMessage());
            writeError(resp, e.getMessage());
        } catch (Exception e) {
            logger.error("查询业主房屋失败: {}", ownerId, e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }
}
