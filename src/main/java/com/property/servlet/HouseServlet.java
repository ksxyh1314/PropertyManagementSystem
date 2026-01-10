package com.property.servlet;

import java.util.Arrays;
import com.property.entity.House;
import com.property.entity.Owner;
import com.property.entity.User;
import com.property.service.HouseService;
import com.property.service.OwnerService;
import com.property.util.ExcelExportUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 房屋管理Servlet（✅ 增加日志记录）
 */
@WebServlet("/admin/house")
public class HouseServlet extends BaseServlet {
    private HouseService houseService = new HouseService();
    private OwnerService ownerService = new OwnerService();

    /**
     * 分页查询房屋列表
     */
    public void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        int pageNum = getIntParameter(req, "pageNum", 1);
        int pageSize = getIntParameter(req, "pageSize", 10);
        String keyword = getStringParameter(req, "keyword");
        String status = getStringParameter(req, "status");

        try {
            Map<String, Object> result = houseService.findByPage(pageNum, pageSize, keyword, status);
            writeSuccess(resp, "查询成功", result);
        } catch (Exception e) {
            logger.error("查询房屋列表失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询所有房屋
     */
    public void findAll(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        try {
            List<House> houses = houseService.findAll();
            writeSuccess(resp, "查询成功", houses);
        } catch (Exception e) {
            logger.error("查询房屋失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID查询房屋
     */
    public void findById(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        String houseId = getStringParameter(req, "houseId");
        if (houseId == null || houseId.isEmpty()) {
            writeError(resp, "房屋ID不能为空");
            return;
        }

        try {
            House house = houseService.findById(houseId);
            if (house != null) {
                writeSuccess(resp, "查询成功", house);
            } else {
                writeError(resp, "房屋不存在");
            }
        } catch (Exception e) {
            logger.error("查询房屋失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取房屋详情（别名方法，兼容前端 detail 调用）
     */
    public void detail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        findById(req, resp);
    }

    /**
     * 查询空置房屋
     */
    public void findVacant(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        try {
            List<House> houses = houseService.findVacantHouses();
            writeSuccess(resp, "查询成功", houses);
        } catch (Exception e) {
            logger.error("查询空置房屋失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有业主列表（供前端下拉框使用）
     */
    public void owners(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        try {
            List<Owner> owners = ownerService.findAll();
            writeSuccess(resp, "查询成功", owners);
        } catch (Exception e) {
            logger.error("查询业主列表失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * ✅ 添加房屋（增加日志记录）
     */
    public void add(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        // 获取当前用户
        User currentUser = getCurrentUser(req);

        String houseId = getStringParameter(req, "houseId");
        String buildingNo = getStringParameter(req, "buildingNo");
        String unitNo = getStringParameter(req, "unitNo");
        String floor = getStringParameter(req, "floor");
        String layout = getStringParameter(req, "layout");
        String areaStr = getStringParameter(req, "area");
        String pricePerSqmStr = getStringParameter(req, "pricePerSqm");
        String houseStatus = getStringParameter(req, "houseStatus");
        String saleStatus = getStringParameter(req, "saleStatus");
        String ownerId = getStringParameter(req, "ownerId");

        House house = new House();
        house.setHouseId(houseId);
        house.setBuildingNo(buildingNo);
        house.setUnitNo(unitNo);
        house.setFloor(floor);
        house.setLayout(layout);
        house.setHouseStatus(houseStatus);
        house.setSaleStatus(saleStatus);

        // 解析面积
        if (areaStr != null && !areaStr.isEmpty()) {
            try {
                house.setArea(new BigDecimal(areaStr));
            } catch (NumberFormatException e) {
                writeError(resp, "建筑面积格式不正确");
                return;
            }
        }

        // 解析物业费单价
        if (pricePerSqmStr != null && !pricePerSqmStr.isEmpty()) {
            try {
                house.setPricePerSqm(new BigDecimal(pricePerSqmStr));
            } catch (NumberFormatException e) {
                writeError(resp, "物业费单价格式不正确");
                return;
            }
        }

        // 设置业主ID
        if (ownerId != null && !ownerId.isEmpty()) {
            house.setOwnerId(ownerId);
        }

        try {
            // ✅ 传入 operatorId 和 request 记录日志
            boolean success = houseService.addHouse(house, currentUser.getUserId(), req);
            if (success) {
                writeSuccess(resp, "添加房屋成功");
            } else {
                writeError(resp, "添加房屋失败");
            }
        } catch (IllegalArgumentException e) {
            writeError(resp, e.getMessage());
        } catch (Exception e) {
            logger.error("添加房屋失败", e);
            writeError(resp, "添加房屋失败：" + e.getMessage());
        }
    }

    /**
     * ✅ 更新房屋（增加日志记录）
     */
    public void update(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        // 获取当前用户
        User currentUser = getCurrentUser(req);

        String houseId = getStringParameter(req, "houseId");
        if (houseId == null || houseId.isEmpty()) {
            writeError(resp, "房屋ID不能为空");
            return;
        }

        String buildingNo = getStringParameter(req, "buildingNo");
        String unitNo = getStringParameter(req, "unitNo");
        String floor = getStringParameter(req, "floor");
        String layout = getStringParameter(req, "layout");
        String areaStr = getStringParameter(req, "area");
        String pricePerSqmStr = getStringParameter(req, "pricePerSqm");
        String houseStatus = getStringParameter(req, "houseStatus");
        String saleStatus = getStringParameter(req, "saleStatus");
        String ownerId = getStringParameter(req, "ownerId");

        House house = new House();
        house.setHouseId(houseId);
        house.setBuildingNo(buildingNo);
        house.setUnitNo(unitNo);
        house.setFloor(floor);
        house.setLayout(layout);
        house.setHouseStatus(houseStatus);
        house.setSaleStatus(saleStatus);

        // 解析面积
        if (areaStr != null && !areaStr.isEmpty()) {
            try {
                house.setArea(new BigDecimal(areaStr));
            } catch (NumberFormatException e) {
                writeError(resp, "建筑面积格式不正确");
                return;
            }
        }

        // 解析物业费单价
        if (pricePerSqmStr != null && !pricePerSqmStr.isEmpty()) {
            try {
                house.setPricePerSqm(new BigDecimal(pricePerSqmStr));
            } catch (NumberFormatException e) {
                writeError(resp, "物业费单价格式不正确");
                return;
            }
        }

        // 设置业主ID
        if (ownerId != null && !ownerId.isEmpty()) {
            house.setOwnerId(ownerId);
        }

        try {
            // ✅ 传入 operatorId 和 request 记录日志
            boolean success = houseService.updateHouse(house, currentUser.getUserId(), req);
            if (success) {
                writeSuccess(resp, "更新房屋成功");
            } else {
                writeError(resp, "更新房屋失败");
            }
        } catch (IllegalArgumentException e) {
            writeError(resp, e.getMessage());
        } catch (Exception e) {
            logger.error("更新房屋失败", e);
            writeError(resp, "更新房屋失败：" + e.getMessage());
        }
    }

    /**
     * ✅ 删除房屋（增加日志记录）
     */
    public void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        // 获取当前用户
        User currentUser = getCurrentUser(req);

        String houseId = getStringParameter(req, "houseId");
        if (houseId == null || houseId.isEmpty()) {
            writeError(resp, "房屋ID不能为空");
            return;
        }

        try {
            // ✅ 传入 operatorId 和 request 记录日志
            boolean success = houseService.deleteHouse(houseId, currentUser.getUserId(), req);
            if (success) {
                writeSuccess(resp, "删除房屋成功");
            } else {
                writeError(resp, "删除房屋失败");
            }
        } catch (IllegalArgumentException e) {
            writeError(resp, e.getMessage());
        } catch (Exception e) {
            logger.error("删除房屋失败", e);
            writeError(resp, "删除房屋失败：" + e.getMessage());
        }
    }

    /**
     * ✅ 分配业主（增加日志记录）
     */
    public void assignOwner(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        // 获取当前用户
        User currentUser = getCurrentUser(req);

        String houseId = getStringParameter(req, "houseId");
        String ownerId = getStringParameter(req, "ownerId");

        if (houseId == null || houseId.isEmpty()) {
            writeError(resp, "房屋ID不能为空");
            return;
        }

        if (ownerId == null || ownerId.isEmpty()) {
            writeError(resp, "业主ID不能为空");
            return;
        }

        try {
            // 获取房屋信息
            House house = houseService.findById(houseId);
            if (house == null) {
                writeError(resp, "房屋不存在");
                return;
            }

            // 更新房屋的业主ID和状态
            house.setOwnerId(ownerId);
            house.setHouseStatus("occupied"); // 设置为已入住

            // ✅ 传入 operatorId 和 request 记录日志
            boolean success = houseService.updateHouse(house, currentUser.getUserId(), req);
            if (success) {
                // ✅ 额外记录分配业主的日志
                houseService.assignOwner(houseId, ownerId, currentUser.getUserId(), req);
                writeSuccess(resp, "分配业主成功");
            } else {
                writeError(resp, "分配业主失败");
            }
        } catch (Exception e) {
            logger.error("分配业主失败", e);
            writeError(resp, "分配失败：" + e.getMessage());
        }
    }

    /**
     * 统计各状态房屋数量
     */
    public void countByStatus(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        try {
            Map<String, Long> stats = houseService.countByStatus();
            writeSuccess(resp, "统计成功", stats);
        } catch (Exception e) {
            logger.error("统计房屋失败", e);
            writeError(resp, "统计失败：" + e.getMessage());
        }
    }

    /**
     * 导出房屋列表到 Excel
     */
    public void export(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        String keyword = getStringParameter(req, "keyword");
        String status = getStringParameter(req, "status");

        try {
            // 查询所有符合条件的房屋（不分页）
            List<House> houseList;

            if ((keyword != null && !keyword.isEmpty()) || (status != null && !status.isEmpty())) {
                // 如果有筛选条件，查询符合条件的数据
                houseList = houseService.findByCondition(keyword, status);
            } else {
                // 否则导出所有数据
                houseList = houseService.findAll();
            }

            if (houseList == null || houseList.isEmpty()) {
                writeError(resp, "没有数据可导出");
                return;
            }

            // 生成 Excel 文件
            byte[] excelData = ExcelExportUtil.exportHouseList(houseList);

            // 设置响应头
            String fileName = "房屋信息_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx";
            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" +
                    new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + "\"");
            resp.setContentLength(excelData.length);

            // 输出文件
            resp.getOutputStream().write(excelData);
            resp.getOutputStream().flush();

            logger.info("导出房屋数据成功，共 {} 条记录", houseList.size());

        } catch (Exception e) {
            logger.error("导出房屋数据失败", e);
            writeError(resp, "导出失败：" + e.getMessage());
        }
    }

    /**
     * 导出选中的房屋数据
     */
    public void exportSelected(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin", "finance")) {
            return;
        }

        String idsParam = getStringParameter(req, "ids");
        if (idsParam == null || idsParam.isEmpty()) {
            writeError(resp, "请选择要导出的数据");
            return;
        }

        try {
            // 解析房屋ID列表
            String[] idsArray = idsParam.split(",");
            List<String> ids = Arrays.asList(idsArray);

            List<House> houseList = houseService.findByIds(ids);

            if (houseList == null || houseList.isEmpty()) {
                writeError(resp, "没有找到对应的数据");
                return;
            }

            // 生成 Excel 文件
            byte[] excelData = ExcelExportUtil.exportHouseList(houseList);

            // 设置响应头
            String fileName = "房屋信息_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx";
            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" +
                    new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + "\"");
            resp.setContentLength(excelData.length);

            // 输出文件
            resp.getOutputStream().write(excelData);
            resp.getOutputStream().flush();

            logger.info("导出选中房屋数据成功，共 {} 条记录", houseList.size());

        } catch (Exception e) {
            logger.error("导出选中房屋数据失败", e);
            writeError(resp, "导出失败：" + e.getMessage());
        }
    }

    /**
     * 获取楼栋列表
     */
    public void listBuildings(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!checkLogin(req, resp)) {
            return;
        }

        try {
            List<Map<String, Object>> buildings = houseService.listBuildings();
            writeSuccess(resp, "查询成功", buildings);
        } catch (Exception e) {
            logger.error("获取楼栋列表失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 统计已入住房屋数量
     */
    public void countOccupied(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!checkLogin(req, resp)) {
            return;
        }

        String buildingId = getStringParameter(req, "buildingId");

        try {
            int count = houseService.countOccupied(buildingId);
            writeSuccess(resp, "查询成功", count);
        } catch (Exception e) {
            logger.error("统计房屋数量失败", e);
            writeError(resp, "统计失败：" + e.getMessage());
        }
    }
    /**
     * ✅ 标记房屋为已售（简化版，不记录金额）
     */
    public void markAsSold(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        // 获取当前用户
        User currentUser = getCurrentUser(req);

        String houseId = getStringParameter(req, "houseId");
        String ownerId = getStringParameter(req, "ownerId");

        if (houseId == null || houseId.isEmpty()) {
            writeError(resp, "房屋ID不能为空");
            return;
        }

        if (ownerId == null || ownerId.isEmpty()) {
            writeError(resp, "业主ID不能为空");
            return;
        }

        try {
            // 获取房屋信息
            House house = houseService.findById(houseId);
            if (house == null) {
                writeError(resp, "房屋不存在");
                return;
            }

            // 检查房屋状态
            if ("sold".equals(house.getSaleStatus())) {
                writeError(resp, "该房屋已售出，无法重复操作");
                return;
            }

            // 检查业主是否存在
            Owner owner = ownerService.findById(ownerId);
            if (owner == null) {
                writeError(resp, "业主不存在");
                return;
            }

            // 更新房屋状态
            house.setSaleStatus("sold");        // 标记为已售
            house.setHouseStatus("occupied");   // 标记为已入住
            house.setOwnerId(ownerId);          // 设置业主

            // ✅ 传入 operatorId 和 request 记录日志
            boolean success = houseService.updateHouse(house, currentUser.getUserId(), req);

            if (success) {
                // ✅ 记录出售操作日志
                houseService.logHouseSale(houseId, ownerId, currentUser.getUserId(), req);
                writeSuccess(resp, "房屋出售成功");
            } else {
                writeError(resp, "房屋出售失败");
            }
        } catch (Exception e) {
            logger.error("房屋出售失败", e);
            writeError(resp, "出售失败：" + e.getMessage());
        }
    }

    /**
     * ✅ 标记房屋为待售（取消已售状态）
     */
    public void markAsForSale(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        // 获取当前用户
        User currentUser = getCurrentUser(req);

        String houseId = getStringParameter(req, "houseId");

        if (houseId == null || houseId.isEmpty()) {
            writeError(resp, "房屋ID不能为空");
            return;
        }

        try {
            // 获取房屋信息
            House house = houseService.findById(houseId);
            if (house == null) {
                writeError(resp, "房屋不存在");
                return;
            }

            // 保存原业主ID用于日志
            String oldOwnerId = house.getOwnerId();

            // 更新房屋状态
            house.setSaleStatus("for_sale");    // 标记为待售
            house.setHouseStatus("vacant");     // 标记为空置
            house.setOwnerId(null);             // 清空业主

            // ✅ 传入 operatorId 和 request 记录日志
            boolean success = houseService.updateHouse(house, currentUser.getUserId(), req);

            if (success) {
                // ✅ 记录取消出售日志
                houseService.logCancelSale(houseId, oldOwnerId, currentUser.getUserId(), req);
                writeSuccess(resp, "房屋已标记为待售");
            } else {
                writeError(resp, "操作失败");
            }
        } catch (Exception e) {
            logger.error("标记待售失败", e);
            writeError(resp, "操作失败：" + e.getMessage());
        }
    }

    /**
     * ✅ 标记房屋为已租（简化版）
     */
    public void markAsLeased(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        // 获取当前用户
        User currentUser = getCurrentUser(req);

        String houseId = getStringParameter(req, "houseId");
        String ownerId = getStringParameter(req, "ownerId");

        if (houseId == null || houseId.isEmpty()) {
            writeError(resp, "房屋ID不能为空");
            return;
        }

        if (ownerId == null || ownerId.isEmpty()) {
            writeError(resp, "租户ID不能为空");
            return;
        }

        try {
            // 获取房屋信息
            House house = houseService.findById(houseId);
            if (house == null) {
                writeError(resp, "房屋不存在");
                return;
            }

            // 检查房屋状态
            if ("leased".equals(house.getSaleStatus())) {
                writeError(resp, "该房屋已出租，无法重复操作");
                return;
            }

            // 检查租户是否存在
            Owner owner = ownerService.findById(ownerId);
            if (owner == null) {
                writeError(resp, "租户不存在");
                return;
            }

            // 更新房屋状态
            house.setSaleStatus("leased");      // 标记为已租
            house.setHouseStatus("rented");     // 标记为出租中
            house.setOwnerId(ownerId);          // 设置租户

            // ✅ 传入 operatorId 和 request 记录日志
            boolean success = houseService.updateHouse(house, currentUser.getUserId(), req);

            if (success) {
                // ✅ 记录出租操作日志
                houseService.logHouseLease(houseId, ownerId, currentUser.getUserId(), req);
                writeSuccess(resp, "房屋出租成功");
            } else {
                writeError(resp, "房屋出租失败");
            }
        } catch (Exception e) {
            logger.error("房屋出租失败", e);
            writeError(resp, "出租失败：" + e.getMessage());
        }
    }

}
