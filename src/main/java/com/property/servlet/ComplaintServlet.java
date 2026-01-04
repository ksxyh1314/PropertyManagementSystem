package com.property.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.property.entity.Complaint;
import com.property.entity.User;
import com.property.service.ComplaintService;
import com.property.util.LocalDateTimeAdapter;
import com.property.util.ResponseUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 投诉管理 Servlet
 * 修改记录：移除了单独的 close 接口，保留 cancel 接口用于处理驳回/取消
 */
@WebServlet(
        urlPatterns = "/admin/complaint/*",
        loadOnStartup = 1
)
public class ComplaintServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ComplaintService complaintService = new ComplaintService();
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @Override
    public void init() throws ServletException {
        super.init();
        System.out.println("\n=================================================");
        System.out.println("✅ ComplaintServlet 初始化成功");
        System.out.println("Servlet 名称: " + getServletName());
        System.out.println("Servlet 路径: /admin/complaint/*");
        System.out.println("初始化时间: " + new Date());
        System.out.println("=================================================\n");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("\n=== ComplaintServlet doGet 被调用 ===");
        System.out.println("请求 URI: " + request.getRequestURI());
        System.out.println("Path Info: " + request.getPathInfo());

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
            System.out.println(">>> 调用 getComplaintList");
            getComplaintList(request, response);
        } else if (pathInfo.startsWith("/detail/")) {
            System.out.println(">>> 调用 getComplaintDetail");
            getComplaintDetail(request, response, pathInfo);
        } else if (pathInfo.equals("/statistics")) {
            System.out.println(">>> 调用 getComplaintStatistics");
            getComplaintStatistics(request, response);
        } else {
            System.out.println(">>> 无效路径: " + pathInfo);
            ResponseUtil.writeJson(response, ResponseUtil.error("无效的请求路径"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("\n=================================================");
        System.out.println("=== ComplaintServlet doPost 被调用 ===");
        System.out.println("请求时间: " + new Date());
        System.out.println("请求 URI: " + request.getRequestURI());
        System.out.println("Path Info: " + request.getPathInfo());
        System.out.println("Content-Type: " + request.getContentType());
        System.out.println("=================================================\n");

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            ResponseUtil.writeJson(response, ResponseUtil.error("无效的请求路径"));
        } else if (pathInfo.equals("/submit")) {
            System.out.println(">>> 调用 submitComplaint");
            submitComplaint(request, response);
        } else if (pathInfo.equals("/accept")) {
            System.out.println(">>> 调用 acceptComplaint");
            acceptComplaint(request, response);
        } else if (pathInfo.equals("/reply")) {
            System.out.println(">>> 调用 replyComplaint");
            replyComplaint(request, response);
        }
        // ✅ 只保留 cancel 路由（用于驳回/取消）
        else if (pathInfo.equals("/cancel")) {
            System.out.println(">>> 调用 cancelComplaint");
            cancelComplaint(request, response);
        }
        else {
            System.out.println(">>> 无效路径: " + pathInfo);
            ResponseUtil.writeJson(response, ResponseUtil.error("无效的请求路径"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("\n=================================================");
        System.out.println("=== ComplaintServlet doDelete 被调用 ===");
        System.out.println("请求时间: " + new Date());
        System.out.println("请求 URI: " + request.getRequestURI());
        System.out.println("Path Info: " + request.getPathInfo());
        System.out.println("=================================================\n");

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        String pathInfo = request.getPathInfo();

        if (pathInfo != null && pathInfo.startsWith("/delete/")) {
            System.out.println(">>> 调用 deleteComplaint");
            deleteComplaint(request, response, pathInfo);
        } else {
            System.out.println(">>> 无效路径: " + pathInfo);
            ResponseUtil.writeJson(response, ResponseUtil.error("无效的请求路径"));
        }
    }

    /**
     * 提交投诉
     */
    private void submitComplaint(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        System.out.println("=== submitComplaint 开始执行 ===");

        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            String jsonBody = sb.toString();
            System.out.println("请求体: " + jsonBody);

            Complaint complaint = gson.fromJson(jsonBody, Complaint.class);
            System.out.println("解析后的投诉对象: " + complaint);

            Map<String, Object> result = complaintService.submitComplaint(complaint);
            System.out.println("服务层返回结果: " + result);

            ResponseUtil.writeJson(response, result);

        } catch (Exception e) {
            System.err.println("❌ submitComplaint 异常: " + e.getMessage());
            e.printStackTrace();
            ResponseUtil.writeJson(response, ResponseUtil.error("提交失败: " + e.getMessage()));
        }
    }

    /**
     * 查询投诉列表
     */
    private void getComplaintList(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        System.out.println("=== getComplaintList 开始执行 ===");

        String ownerId = request.getParameter("ownerId");
        String complaintType = request.getParameter("complaintType");
        String complaintStatus = request.getParameter("complaintStatus");
        String keyword = request.getParameter("keyword");

        Integer pageNum = 1;
        Integer pageSize = 10;

        try {
            if (request.getParameter("pageNum") != null) {
                pageNum = Integer.parseInt(request.getParameter("pageNum"));
            }
            if (request.getParameter("pageSize") != null) {
                pageSize = Integer.parseInt(request.getParameter("pageSize"));
            }
        } catch (NumberFormatException e) {
            System.err.println("❌ 页码格式错误: " + e.getMessage());
            ResponseUtil.writeJson(response, ResponseUtil.error("页码格式错误"));
            return;
        }

        System.out.println("查询参数:");
        System.out.println("  ownerId: " + ownerId);
        System.out.println("  complaintType: " + complaintType);
        System.out.println("  complaintStatus: " + complaintStatus);
        System.out.println("  keyword: " + keyword);
        System.out.println("  pageNum: " + pageNum);
        System.out.println("  pageSize: " + pageSize);

        try {
            Map<String, Object> result = complaintService.getComplaints(
                    ownerId, complaintType, complaintStatus, keyword, pageNum, pageSize);

            System.out.println("服务层返回结果: " + result);
            System.out.println("返回记录数: " + (result.get("list") != null ?
                    ((java.util.List<?>) result.get("list")).size() : 0));

            ResponseUtil.writeJson(response, result);

        } catch (Exception e) {
            System.err.println("❌ getComplaintList 异常: " + e.getMessage());
            e.printStackTrace();
            ResponseUtil.writeJson(response, ResponseUtil.error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 查询投诉详情
     */
    private void getComplaintDetail(HttpServletRequest request, HttpServletResponse response,
                                    String pathInfo) throws IOException {

        System.out.println("=== getComplaintDetail 开始执行 ===");
        System.out.println("pathInfo: " + pathInfo);

        try {
            String idStr = pathInfo.substring("/detail/".length());
            System.out.println("投诉ID字符串: " + idStr);

            Integer complaintId = Integer.parseInt(idStr);
            System.out.println("投诉ID: " + complaintId);

            Complaint complaint = complaintService.getComplaintDetail(complaintId);
            System.out.println("查询结果: " + (complaint != null ? "找到记录" : "未找到记录"));

            if (complaint != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("data", complaint);
                ResponseUtil.writeJson(response, result);
            } else {
                ResponseUtil.writeJson(response, ResponseUtil.error("投诉记录不存在"));
            }

        } catch (NumberFormatException e) {
            System.err.println("❌ 投诉ID格式错误: " + e.getMessage());
            ResponseUtil.writeJson(response, ResponseUtil.error("投诉ID格式错误"));
        } catch (Exception e) {
            System.err.println("❌ getComplaintDetail 异常: " + e.getMessage());
            e.printStackTrace();
            ResponseUtil.writeJson(response, ResponseUtil.error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 受理投诉
     */
    private void acceptComplaint(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        System.out.println("=== acceptComplaint 开始执行 ===");

        try {
            String complaintIdStr = request.getParameter("complaintId");
            String handlerIdStr = request.getParameter("handlerId");

            Integer complaintId = Integer.parseInt(complaintIdStr);
            Integer handlerId = Integer.parseInt(handlerIdStr);

            Map<String, Object> result = complaintService.acceptComplaint(complaintId, handlerId);
            System.out.println("服务层返回结果: " + result);

            ResponseUtil.writeJson(response, result);

        } catch (NumberFormatException e) {
            System.err.println("❌ 参数格式错误: " + e.getMessage());
            ResponseUtil.writeJson(response, ResponseUtil.error("参数格式错误"));
        } catch (Exception e) {
            System.err.println("❌ acceptComplaint 异常: " + e.getMessage());
            e.printStackTrace();
            ResponseUtil.writeJson(response, ResponseUtil.error("受理失败: " + e.getMessage()));
        }
    }

    /**
     * 回复投诉
     */
    private void replyComplaint(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        System.out.println("=== replyComplaint 开始执行 ===");

        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            String jsonBody = sb.toString();
            System.out.println("请求体: " + jsonBody);

            @SuppressWarnings("unchecked")
            Map<String, Object> params = gson.fromJson(jsonBody, Map.class);

            Integer complaintId = ((Double) params.get("complaintId")).intValue();
            Integer handlerId = ((Double) params.get("handlerId")).intValue();
            String reply = (String) params.get("reply");
            String newStatus = (String) params.getOrDefault("newStatus", "resolved");

            Map<String, Object> result = complaintService.replyComplaint(
                    complaintId, handlerId, reply, newStatus);
            System.out.println("服务层返回结果: " + result);

            ResponseUtil.writeJson(response, result);

        } catch (Exception e) {
            System.err.println("❌ replyComplaint 异常: " + e.getMessage());
            e.printStackTrace();
            ResponseUtil.writeJson(response, ResponseUtil.error("回复失败: " + e.getMessage()));
        }
    }

    /**
     * ✅ 取消/驳回投诉
     * 现实逻辑：
     * 1. 业主只能取消 "待处理" 的投诉。
     * 2. 管理员可以驳回 "待处理" 或 "处理中" 的投诉。
     */
    private void cancelComplaint(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("=== cancelComplaint 开始执行 ===");

        try {
            // 1. 获取参数 (ID 和 原因)
            String complaintIdStr = request.getParameter("complaintId");
            String reason = request.getParameter("reason");

            System.out.println("complaintId: " + complaintIdStr);
            System.out.println("reason: " + reason);

            if (complaintIdStr == null || reason == null || reason.trim().isEmpty()) {
                ResponseUtil.writeJson(response, ResponseUtil.error("必须填写取消/驳回原因"));
                return;
            }

            int complaintId = Integer.parseInt(complaintIdStr);

            // 2. 获取当前用户 (用于判断是业主取消还是管理员驳回)
            HttpSession session = request.getSession();
            User currentUser = (User) session.getAttribute("user");

            if (currentUser == null) {
                ResponseUtil.writeJson(response, ResponseUtil.error("用户未登录"));
                return;
            }

            // 3. 简单的业务处理：如果是管理员，自动在原因前加个标记
            if ("admin".equals(currentUser.getUserRole())) {
                reason = "[管理员驳回] " + reason;
            }

            // 4. 调用 Service
            // 注意：Service 层需要有 cancelComplaint(int, String, User) 方法
            Map<String, Object> result = complaintService.cancelComplaint(complaintId, reason, currentUser);

            System.out.println("服务层返回结果: " + result);
            ResponseUtil.writeJson(response, result);

        } catch (NumberFormatException e) {
            System.err.println("❌ ID格式错误: " + e.getMessage());
            ResponseUtil.writeJson(response, ResponseUtil.error("ID格式错误"));
        } catch (Exception e) {
            System.err.println("❌ cancelComplaint 异常: " + e.getMessage());
            e.printStackTrace();
            ResponseUtil.writeJson(response, ResponseUtil.error("操作失败: " + e.getMessage()));
        }
    }

    /**
     * 删除投诉
     */
    private void deleteComplaint(HttpServletRequest request, HttpServletResponse response,
                                 String pathInfo) throws IOException {

        System.out.println("=== deleteComplaint 开始执行 ===");
        System.out.println("pathInfo: " + pathInfo);

        try {
            if (pathInfo == null || pathInfo.equals("/delete") || pathInfo.equals("/delete/")) {
                ResponseUtil.writeJson(response, ResponseUtil.error("投诉ID不能为空"));
                return;
            }

            String complaintIdStr = pathInfo.substring(pathInfo.lastIndexOf("/") + 1);
            int complaintId = Integer.parseInt(complaintIdStr);

            HttpSession session = request.getSession();
            User currentUser = (User) session.getAttribute("user");

            if (currentUser == null) {
                ResponseUtil.writeJson(response, ResponseUtil.error("用户未登录"));
                return;
            }

            int operatorId = currentUser.getUserId();
            Map<String, Object> result = complaintService.deleteComplaint(complaintId, operatorId);
            System.out.println("服务层返回结果: " + result);

            ResponseUtil.writeJson(response, result);

        } catch (NumberFormatException e) {
            System.err.println("❌ 投诉ID格式错误: " + e.getMessage());
            ResponseUtil.writeJson(response, ResponseUtil.error("投诉ID格式错误"));
        } catch (Exception e) {
            System.err.println("❌ deleteComplaint 异常: " + e.getMessage());
            e.printStackTrace();
            ResponseUtil.writeJson(response, ResponseUtil.error("删除失败：" + e.getMessage()));
        }
    }

    /**
     * 投诉统计
     */
    private void getComplaintStatistics(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        System.out.println("=== getComplaintStatistics 开始执行 ===");

        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");

        try {
            Map<String, Object> result = complaintService.getComplaintStatistics(startDate, endDate);
            System.out.println("服务层返回结果: " + result);
            ResponseUtil.writeJson(response, result);
        } catch (Exception e) {
            System.err.println("❌ getComplaintStatistics 异常: " + e.getMessage());
            e.printStackTrace();
            ResponseUtil.writeJson(response, ResponseUtil.error("统计失败: " + e.getMessage()));
        }
    }

    @Override
    public void destroy() {
        System.out.println("\n=================================================");
        System.out.println("❌ ComplaintServlet 销毁");
        System.out.println("销毁时间: " + new Date());
        System.out.println("=================================================\n");
        super.destroy();
    }
}
