package com.property.servlet;

import com.property.entity.House;
import com.property.service.HouseService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 房屋管理Servlet
 */
@WebServlet("/admin/house")
public class HouseServlet extends BaseServlet {
    private HouseService houseService = new HouseService();

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
            writeJson(resp, result);
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
     * 添加房屋
     */
    public void add(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        String houseId = getStringParameter(req, "houseId");
        String buildingNo = getStringParameter(req, "buildingNo");
        String unitNo = getStringParameter(req, "unitNo");
        String floor = getStringParameter(req, "floor");
        String layout = getStringParameter(req, "layout");
        String areaStr = getStringParameter(req, "area");
        String pricePerSqmStr = getStringParameter(req, "pricePerSqm");
        String houseStatus = getStringParameter(req, "houseStatus");
        String saleStatus = getStringParameter(req, "saleStatus");

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

        try {
            boolean success = houseService.addHouse(house);
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
     * 更新房屋
     */
    public void update(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

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

        try {
            boolean success = houseService.updateHouse(house);
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
     * 删除房屋
     */
    public void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        String houseId = getStringParameter(req, "houseId");
        if (houseId == null || houseId.isEmpty()) {
            writeError(resp, "房屋ID不能为空");
            return;
        }

        try {
            boolean success = houseService.deleteHouse(houseId);
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
}
