package com.property.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.property.entity.Announcement;
import com.property.service.AnnouncementService;
import com.property.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 管理员端公告 Servlet
 * 路径: /announcement
 * 功能：公告的增删改查、发布管理
 */
@WebServlet("/announcement")
public class AnnouncementServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AnnouncementServlet.class);
    private final AnnouncementService announcementService = new AnnouncementService();
    private final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 🔥 验证登录和权限
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            logger.warn("❌ 未登录用户尝试访问公告管理");
            writeJson(response, Result.error(401, "请先登录"));
            return;
        }

        String userRole = (String) session.getAttribute("role");
        logger.info("✅ 当前用户角色：[{}]", userRole);
        if (!"admin".equals(userRole) && !"finance".equals(userRole)) {
            logger.warn("❌ 无权限用户尝试访问：role={}", userRole);
            writeJson(response, Result.error(403, "无权限访问"));
            return;
        }

        String method = request.getParameter("method");
        logger.info("========== 管理员端公告请求 ==========");
        logger.info("Method: {}", method);
        logger.info("User: {} ({})", session.getAttribute("username"), userRole);

        try {
            switch (method) {
                case "list":
                    // 查询公告列表（分页）
                    getAnnouncementList(request, response);
                    break;
                case "detail":
                    // 查询公告详情
                    getAnnouncementDetail(request, response);
                    break;
                case "statistics":
                    // 统计各类型公告数量
                    getStatistics(request, response);
                    break;
                default:
                    writeJson(response, Result.error("无效的请求方法"));
            }
        } catch (Exception e) {
            logger.error("❌ 管理员端公告请求异常", e);
            writeJson(response, Result.error("服务器错误：" + e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        // 🔥 验证登录和权限
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            logger.warn("❌ 未登录用户尝试访问公告管理");
            writeJson(response, Result.error(401, "请先登录"));
            return;
        }

        String userRole = (String) session.getAttribute("role");
        if (!"admin".equals(userRole) && !"finance".equals(userRole)) {
            logger.warn("❌ 无权限用户尝试访问：role={}", userRole);
            writeJson(response, Result.error(403, "无权限访问"));
            return;
        }

        String method = request.getParameter("method");
        logger.info("========== 管理员端公告操作 ==========");
        logger.info("Method: {}", method);
        logger.info("User: {} ({})", session.getAttribute("username"), userRole);

        try {
            switch (method) {
                case "add":
                    // 添加公告
                    addAnnouncement(request, response, session);
                    break;
                case "update":
                    // 更新公告
                    updateAnnouncement(request, response);
                    break;
                case "delete":
                    // 删除公告
                    deleteAnnouncement(request, response);
                    break;
                case "batchDelete":
                    // 批量删除公告
                    batchDeleteAnnouncement(request, response);
                    break;
                case "updateStatus":
                    // 更新公告状态（发布/取消发布）
                    updateStatus(request, response);
                    break;
                case "batchUpdateStatus":
                    // 批量更新状态
                    batchUpdateStatus(request, response);
                    break;
                default:
                    writeJson(response, Result.error("无效的请求方法"));
            }
        } catch (Exception e) {
            logger.error("❌ 管理员端公告操作异常", e);
            writeJson(response, Result.error("服务器错误：" + e.getMessage()));
        }
    }

    // ==================== GET 请求处理方法 ====================

    /**
     * ✅ 查询公告列表（分页 + 筛选）
     */
    private void getAnnouncementList(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int pageNum = getIntParam(request, "pageNum", 1);
        int pageSize = getIntParam(request, "pageSize", 10);
        String keyword = request.getParameter("keyword");

        // ✅ 新增筛选参数
        String announcementType = request.getParameter("announcementType");
        String priority = request.getParameter("priority");
        String statusStr = request.getParameter("status");
        Integer status = null;
        if (statusStr != null && !statusStr.trim().isEmpty()) {
            try {
                status = Integer.parseInt(statusStr);
            } catch (NumberFormatException e) {
                logger.warn("⚠️ 状态参数格式错误: {}", statusStr);
            }
        }

        logger.info(">>> 查询公告列表");
        logger.info("参数: pageNum={}, pageSize={}, keyword={}, type={}, priority={}, status={}",
                pageNum, pageSize, keyword, announcementType, priority, status);

        try {
            // 🔥 调用 Service 的 getAnnouncements 方法（需要传递筛选参数）
            Map<String, Object> result = announcementService.getAnnouncements(
                    keyword, announcementType, priority, status, pageNum, pageSize
            );

            logger.info("✅ 查询成功，共 {} 条记录", result.get("total"));
            writeJson(response, Result.success(result));

        } catch (Exception e) {
            logger.error("❌ 查询公告列表失败", e);
            writeJson(response, Result.error("查询失败：" + e.getMessage()));
        }
    }

    /**
     * 查询公告详情
     */
    private void getAnnouncementDetail(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int id = getIntParam(request, "id", 0);

        if (id <= 0) {
            writeJson(response, Result.error("公告ID无效"));
            return;
        }

        logger.info(">>> 查询公告详情，ID: {}", id);

        try {
            // 🔥 调用 Service 的 getAnnouncementById 方法
            Announcement announcement = announcementService.getAnnouncementById(id);

            if (announcement == null) {
                logger.warn("⚠️ 公告不存在，ID: {}", id);
                writeJson(response, Result.error("公告不存在"));
                return;
            }

            logger.info("✅ 查询成功");
            writeJson(response, Result.success(announcement));

        } catch (Exception e) {
            logger.error("❌ 查询公告详情失败", e);
            writeJson(response, Result.error("查询失败：" + e.getMessage()));
        }
    }

    /**
     * 统计各类型公告数量
     */
    private void getStatistics(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        logger.info(">>> 统计各类型公告数量");

        try {
            // 🔥 调用 Service 的 getStatistics 方法
            Map<String, Object> statistics = announcementService.getStatistics();

            logger.info("✅ 统计成功：{}", statistics);
            writeJson(response, Result.success(statistics));

        } catch (Exception e) {
            logger.error("❌ 统计公告数量失败", e);
            writeJson(response, Result.error("统计失败：" + e.getMessage()));
        }
    }

    // ==================== POST 请求处理方法 ====================

    /**
     * 添加公告
     */
    private void addAnnouncement(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException {

        logger.info(">>> 添加公告");

        try {
            // 🔥 读取 JSON 请求体
            String jsonStr = readRequestBody(request);
            logger.info("请求体: {}", jsonStr);

            Announcement announcement = gson.fromJson(jsonStr, Announcement.class);

            // 🔥 参数校验
            if (announcement.getTitle() == null || announcement.getTitle().trim().isEmpty()) {
                writeJson(response, Result.error("公告标题不能为空"));
                return;
            }

            if (announcement.getContent() == null || announcement.getContent().trim().isEmpty()) {
                writeJson(response, Result.error("公告内容不能为空"));
                return;
            }

            if (announcement.getAnnouncementType() == null || announcement.getAnnouncementType().trim().isEmpty()) {
                writeJson(response, Result.error("公告类型不能为空"));
                return;
            }

            // 🔥 设置发布者信息
            Integer userId = (Integer) session.getAttribute("userId");
            announcement.setPublisherId(userId);

            // 🔥 设置发布时间
            if (announcement.getPublishTime() == null) {
                announcement.setPublishTime(new Timestamp(System.currentTimeMillis()));
            }

            // 🔥 设置默认状态（如果未设置）
            if (announcement.getStatus() == null) {
                announcement.setStatus(1); // 默认已发布
            }

            // 🔥 调用 Service 添加
            boolean success = announcementService.addAnnouncement(announcement);

            if (success) {
                logger.info("✅ 公告添加成功");
                writeJson(response, Result.success("添加成功"));
            } else {
                logger.error("❌ 公告添加失败");
                writeJson(response, Result.error("添加失败"));
            }

        } catch (Exception e) {
            logger.error("❌ 添加公告失败", e);
            writeJson(response, Result.error("添加失败：" + e.getMessage()));
        }
    }

    /**
     * 更新公告
     */
    private void updateAnnouncement(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        logger.info(">>> 更新公告");

        try {
            // 🔥 读取 JSON 请求体
            String jsonStr = readRequestBody(request);
            logger.info("请求体: {}", jsonStr);

            Announcement announcement = gson.fromJson(jsonStr, Announcement.class);

            // 🔥 参数校验
            if (announcement.getAnnouncementId() == null || announcement.getAnnouncementId() <= 0) {
                writeJson(response, Result.error("公告ID无效"));
                return;
            }

            if (announcement.getTitle() == null || announcement.getTitle().trim().isEmpty()) {
                writeJson(response, Result.error("公告标题不能为空"));
                return;
            }

            if (announcement.getContent() == null || announcement.getContent().trim().isEmpty()) {
                writeJson(response, Result.error("公告内容不能为空"));
                return;
            }

            // 🔥 调用 Service 更新
            boolean success = announcementService.updateAnnouncement(announcement);

            if (success) {
                logger.info("✅ 公告更新成功");
                writeJson(response, Result.success("更新成功"));
            } else {
                logger.error("❌ 公告更新失败");
                writeJson(response, Result.error("更新失败"));
            }

        } catch (Exception e) {
            logger.error("❌ 更新公告失败", e);
            writeJson(response, Result.error("更新失败：" + e.getMessage()));
        }
    }

    /**
     * 删除公告
     */
    private void deleteAnnouncement(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int id = getIntParam(request, "id", 0);

        if (id <= 0) {
            writeJson(response, Result.error("公告ID无效"));
            return;
        }

        logger.info(">>> 删除公告，ID: {}", id);

        try {
            // 🔥 调用 Service 的 deleteAnnouncement 方法
            boolean success = announcementService.deleteAnnouncement(id);

            if (success) {
                logger.info("✅ 公告删除成功");
                writeJson(response, Result.success("删除成功"));
            } else {
                logger.error("❌ 公告删除失败");
                writeJson(response, Result.error("删除失败"));
            }

        } catch (Exception e) {
            logger.error("❌ 删除公告失败", e);
            writeJson(response, Result.error("删除失败：" + e.getMessage()));
        }
    }

    /**
     * 批量删除公告
     */
    private void batchDeleteAnnouncement(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        logger.info(">>> 批量删除公告");

        try {
            // 🔥 读取 JSON 请求体
            String jsonStr = readRequestBody(request);
            logger.info("请求体: {}", jsonStr);

            // 🔥 解析 ID 数组
            Integer[] idsArray = gson.fromJson(jsonStr, Integer[].class);
            List<Integer> ids = Arrays.asList(idsArray);

            if (ids.isEmpty()) {
                writeJson(response, Result.error("请选择要删除的公告"));
                return;
            }

            logger.info("删除ID列表: {}", ids);

            // 🔥 调用 Service 批量删除
            boolean success = announcementService.batchDelete(ids);

            if (success) {
                logger.info("✅ 批量删除完成");
                writeJson(response, Result.success("批量删除成功"));
            } else {
                logger.error("❌ 批量删除失败");
                writeJson(response, Result.error("批量删除失败"));
            }

        } catch (Exception e) {
            logger.error("❌ 批量删除公告失败", e);
            writeJson(response, Result.error("批量删除失败：" + e.getMessage()));
        }
    }

    /**
     * 更新公告状态（发布/取消发布）
     */
    private void updateStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int id = getIntParam(request, "id", 0);
        int status = getIntParam(request, "status", 1);

        if (id <= 0) {
            writeJson(response, Result.error("公告ID无效"));
            return;
        }

        logger.info(">>> 更新公告状态，ID: {}, status: {}", id, status);

        try {
            // 🔥 调用 Service 的 updateAnnouncementStatus 方法
            boolean success = announcementService.updateAnnouncementStatus(id, status);

            if (success) {
                String statusText = (status == 1) ? "发布" : "取消发布";
                logger.info("✅ 公告{}成功", statusText);
                writeJson(response, Result.success(statusText + "成功"));
            } else {
                logger.error("❌ 更新公告状态失败");
                writeJson(response, Result.error("操作失败"));
            }

        } catch (Exception e) {
            logger.error("❌ 更新公告状态失败", e);
            writeJson(response, Result.error("操作失败：" + e.getMessage()));
        }
    }

    /**
     * 批量更新状态
     */
    private void batchUpdateStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        logger.info(">>> 批量更新公告状态");

        try {
            // 🔥 读取 JSON 请求体
            String jsonStr = readRequestBody(request);
            logger.info("请求体: {}", jsonStr);

            // 🔥 解析请求参数
            Map<String, Object> params = gson.fromJson(jsonStr, Map.class);
            List<Double> idsDouble = (List<Double>) params.get("ids");
            Double statusDouble = (Double) params.get("status");

            // 🔥 转换类型
            List<Integer> ids = new java.util.ArrayList<>();
            for (Double d : idsDouble) {
                ids.add(d.intValue());
            }
            Integer status = statusDouble.intValue();

            if (ids.isEmpty()) {
                writeJson(response, Result.error("请选择要操作的公告"));
                return;
            }

            logger.info("ID列表: {}, status: {}", ids, status);

            // 🔥 调用 Service 批量更新
            boolean success = announcementService.batchUpdateStatus(ids, status);

            if (success) {
                String statusText = (status == 1) ? "发布" : "取消发布";
                logger.info("✅ 批量{}完成", statusText);
                writeJson(response, Result.success("批量" + statusText + "成功"));
            } else {
                logger.error("❌ 批量更新状态失败");
                writeJson(response, Result.error("批量操作失败"));
            }

        } catch (Exception e) {
            logger.error("❌ 批量更新公告状态失败", e);
            writeJson(response, Result.error("批量操作失败：" + e.getMessage()));
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 获取整数参数
     */
    private int getIntParam(HttpServletRequest request, String name, int defaultValue) {
        String value = request.getParameter(name);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 读取请求体
     */
    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    /**
     * 写入 JSON 响应
     */
    private void writeJson(HttpServletResponse response, Result<?> result) throws IOException {
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(result));
        out.flush();
    }
}
