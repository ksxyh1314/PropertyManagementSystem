package com.property.servlet;

import com.property.entity.Owner;
import com.property.service.OwnerService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 业主管理Servlet
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

        try {
            Map<String, Object> result = ownerService.findByPage(pageNum, pageSize, keyword);
            writeJson(resp, result);
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
                writeSuccess(resp, "查询成功", owner);
            } else {
                writeError(resp, "业主不存在");
            }
        } catch (Exception e) {
            logger.error("查询业主失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 添加业主
     */
    public void add(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        String ownerName = getStringParameter(req, "ownerName");
        String phone = getStringParameter(req, "phone");
        String idCard = getStringParameter(req, "idCard");
        String houseId = getStringParameter(req, "houseId");
        String email = getStringParameter(req, "email");
        Integer memberCount = getIntParameter(req, "memberCount");
        String registerDateStr = getStringParameter(req, "registerDate");
        String remark = getStringParameter(req, "remark");
        String password = getStringParameter(req, "password");

        Owner owner = new Owner();
        owner.setOwnerName(ownerName);
        owner.setPhone(phone);
        owner.setIdCard(idCard);
        owner.setHouseId(houseId);
        owner.setEmail(email);
        owner.setMemberCount(memberCount);
        owner.setRemark(remark);

        // 解析日期
        if (registerDateStr != null && !registerDateStr.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                owner.setRegisterDate(sdf.parse(registerDateStr));
            } catch (Exception e) {
                owner.setRegisterDate(new Date());
            }
        } else {
            owner.setRegisterDate(new Date());
        }

        try {
            boolean success = ownerService.addOwner(owner, password);
            if (success) {
                writeSuccess(resp, "添加业主成功", owner.getOwnerId());
            } else {
                writeError(resp, "添加业主失败");
            }
        } catch (IllegalArgumentException e) {
            writeError(resp, e.getMessage());
        } catch (Exception e) {
            logger.error("添加业主失败", e);
            writeError(resp, "添加业主失败：" + e.getMessage());
        }
    }

    /**
     * 更新业主
     */
    public void update(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

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

        Owner owner = new Owner();
        owner.setOwnerId(ownerId);
        owner.setOwnerName(ownerName);
        owner.setPhone(phone);
        owner.setIdCard(idCard);
        owner.setHouseId(houseId);
        owner.setEmail(email);
        owner.setMemberCount(memberCount);
        owner.setRemark(remark);

        try {
            boolean success = ownerService.updateOwner(owner);
            if (success) {
                writeSuccess(resp, "更新业主成功");
            } else {
                writeError(resp, "更新业主失败");
            }
        } catch (IllegalArgumentException e) {
            writeError(resp, e.getMessage());
        } catch (Exception e) {
            logger.error("更新业主失败", e);
            writeError(resp, "更新业主失败：" + e.getMessage());
        }
    }

    /**
     * 删除业主
     */
    public void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        String ownerId = getStringParameter(req, "ownerId");
        if (ownerId == null || ownerId.isEmpty()) {
            writeError(resp, "业主ID不能为空");
            return;
        }

        try {
            boolean success = ownerService.deleteOwner(ownerId);
            if (success) {
                writeSuccess(resp, "删除业主成功");
            } else {
                writeError(resp, "删除业主失败");
            }
        } catch (Exception e) {
            logger.error("删除业主失败", e);
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
            writeSuccess(resp, "查询成功", owners);
        } catch (Exception e) {
            logger.error("查询欠费业主失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }
}
