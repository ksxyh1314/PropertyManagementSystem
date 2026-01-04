package com.property.servlet.owner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业主端 - 投诉建议 Servlet (完整版：含撤销、追加、删除)
 */
@WebServlet(
        urlPatterns = "/owner/complaint/*",
        loadOnStartup = 1
)
public class OwnerComplaintServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ComplaintService complaintService = new ComplaintService();
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @Override
    public void init() throws ServletException {
        super.init();
        System.out.println("\n=================================================");
        System.out.println("✅ OwnerComplaintServlet 初始化成功");
        System.out.println("Servlet 路径: /owner/complaint/*");
        System.out.println("=================================================\n");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        // 🔥 1. 权限校验
        HttpSession session = request.getSession();
        String ownerId = (String) session.getAttribute("username");
        String role = (String) session.getAttribute("role");

        if (ownerId == null || !"owner".equals(role)) {
            ResponseUtil.writeJson(response, ResponseUtil.error("请先登录"));
            return;
        }

        String pathInfo = request.getPathInfo();
        System.out.println(">>> GET 请求: " + pathInfo + " | 用户: " + ownerId);

        // 🔥 2. 路由分发
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
            getMyComplaintList(request, response, ownerId);
        } else if (pathInfo.startsWith("/detail/")) {
            getComplaintDetail(request, response, pathInfo, ownerId);
        } else if (pathInfo.equals("/statistics")) {
            getMyStatistics(request, response, ownerId);
        } else {
            ResponseUtil.writeJson(response, ResponseUtil.error("无效的请求路径"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        // 🔥 1. 权限校验
        HttpSession session = request.getSession();
        String ownerId = (String) session.getAttribute("username");
        String role = (String) session.getAttribute("role");

        if (ownerId == null || !"owner".equals(role)) {
            ResponseUtil.writeJson(response, ResponseUtil.error("请先登录"));
            return;
        }

        String pathInfo = request.getPathInfo();
        System.out.println(">>> POST 请求: " + pathInfo + " | 用户: " + ownerId);

        // 🔥 2. 路由分发
        if (pathInfo == null || pathInfo.equals("/")) {
            ResponseUtil.writeJson(response, ResponseUtil.error("无效的请求路径"));
        } else if (pathInfo.equals("/submit")) {
            submitComplaint(request, response, ownerId);
        } else if (pathInfo.equals("/cancel")) {
            cancelComplaint(request, response, ownerId);
        } else if (pathInfo.equals("/append")) {
            appendContent(request, response, ownerId);
        } else if (pathInfo.equals("/delete")) {
            deleteComplaint(request, response, ownerId);
        } else {
            ResponseUtil.writeJson(response, ResponseUtil.error("无效的请求路径"));
        }
    }

    // ============================================================
    // 🟢 核心业务方法
    // ============================================================

    /**
     * 🔥 1. 查询我的投诉列表
     */
    private void getMyComplaintList(HttpServletRequest request, HttpServletResponse response,
                                    String ownerId) throws IOException {
        String complaintType = request.getParameter("complaintType");
        String complaintStatus = request.getParameter("complaintStatus");
        String keyword = request.getParameter("keyword");
        Integer pageNum = 1;
        Integer pageSize = 10;

        try {
            if (request.getParameter("pageNum") != null) pageNum = Integer.parseInt(request.getParameter("pageNum"));
            if (request.getParameter("pageSize") != null) pageSize = Integer.parseInt(request.getParameter("pageSize"));

            Map<String, Object> serviceResult = complaintService.getComplaints(
                    ownerId, complaintType, complaintStatus, keyword, pageNum, pageSize);

            @SuppressWarnings("unchecked")
            List<Complaint> list = (List<Complaint>) serviceResult.get("list");
            Integer totalCount = (Integer) serviceResult.get("totalCount");

            Map<String, Object> data = new HashMap<>();
            data.put("list", list != null ? list : new java.util.ArrayList<>());
            data.put("total", totalCount != null ? totalCount : 0);
            data.put("pageNum", pageNum);
            data.put("pageSize", pageSize);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", data);

            ResponseUtil.writeJson(response, result);

        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtil.writeJson(response, ResponseUtil.error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 🔥 2. 查询投诉详情
     */
    private void getComplaintDetail(HttpServletRequest request, HttpServletResponse response,
                                    String pathInfo, String ownerId) throws IOException {
        try {
            String idStr = pathInfo.substring("/detail/".length());
            Integer complaintId = Integer.parseInt(idStr);

            Complaint complaint = complaintService.getComplaintDetail(complaintId);

            if (complaint != null) {
                // 🔒 安全校验：只能看自己的
                if (!ownerId.equals(complaint.getOwnerId())) {
                    ResponseUtil.writeJson(response, ResponseUtil.error("无权查看此投诉"));
                    return;
                }
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("data", complaint);
                ResponseUtil.writeJson(response, result);
            } else {
                ResponseUtil.writeJson(response, ResponseUtil.error("投诉记录不存在"));
            }
        } catch (Exception e) {
            ResponseUtil.writeJson(response, ResponseUtil.error("查询详情失败"));
        }
    }

    /**
     * 🔥 3. 提交投诉（✅ 修复版：匿名投诉也保存 owner_id）
     */
    private void submitComplaint(HttpServletRequest request, HttpServletResponse response,
                                 String ownerId) throws IOException {
        try {
            BufferedReader reader = request.getReader();
            Complaint complaint = gson.fromJson(reader, Complaint.class);

            if (complaint == null) {
                ResponseUtil.writeJson(response, ResponseUtil.error("数据为空"));
                return;
            }
            if (complaint.getTitle() == null || complaint.getTitle().trim().isEmpty()) {
                ResponseUtil.writeJson(response, ResponseUtil.error("标题不能为空"));
                return;
            }
            if (complaint.getContent() == null || complaint.getContent().trim().isEmpty()) {
                ResponseUtil.writeJson(response, ResponseUtil.error("内容不能为空"));
                return;
            }

            // 🔥🔥🔥 关键修复：无论是否匿名，都强制绑定当前业主ID
            complaint.setOwnerId(ownerId);  // ✅ 必须保存（即使匿名）

            // 🔥 如果前端没传 isAnonymous，默认为 0（实名）
            if (complaint.getIsAnonymous() == null) {
                complaint.setIsAnonymous(0);
            }

            // 🔥 调试日志
            System.out.println(">>> 提交投诉:");
            System.out.println("    业主ID: " + complaint.getOwnerId());
            System.out.println("    是否匿名: " + complaint.getIsAnonymous());
            System.out.println("    标题: " + complaint.getTitle());

            Map<String, Object> result = complaintService.submitComplaint(complaint);
            ResponseUtil.writeJson(response, result);

        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtil.writeJson(response, ResponseUtil.error("提交失败: " + e.getMessage()));
        }
    }

    /**
     * 🔥 4. 撤销投诉
     */
    private void cancelComplaint(HttpServletRequest request, HttpServletResponse response,
                                 String ownerId) throws IOException {
        System.out.println("=== 正在执行撤销投诉 ===");
        try {
            BufferedReader reader = request.getReader();
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> params = gson.fromJson(reader, type);

            if (params == null || params.get("complaintId") == null) {
                ResponseUtil.writeJson(response, ResponseUtil.error("参数缺失：complaintId"));
                return;
            }

            Integer complaintId = ((Number) params.get("complaintId")).intValue();
            String reason = (String) params.get("reason");

            User currentUser = new User();
            currentUser.setUsername(ownerId);
            currentUser.setUserRole("owner");

            Map<String, Object> result = complaintService.cancelComplaint(complaintId, reason, currentUser);

            System.out.println("撤销结果: " + result);
            ResponseUtil.writeJson(response, result);

        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtil.writeJson(response, ResponseUtil.error("撤销失败: " + e.getMessage()));
        }
    }
    /**
     * 🔥 5. 追加说明（修复版）
     */
    private void appendContent(HttpServletRequest request, HttpServletResponse response,
                               String ownerId) throws IOException {
        System.out.println("=== 正在执行追加说明 ===");
        try {
            // 🔥 读取请求体
            BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String requestBody = sb.toString();
            System.out.println(">>> 请求体: " + requestBody);

            // 🔥 解析 JSON
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> params = gson.fromJson(requestBody, type);

            System.out.println(">>> 解析后的参数: " + params);

            if (params == null) {
                System.err.println("❌ 参数解析失败：params 为 null");
                ResponseUtil.writeJson(response, ResponseUtil.error("参数解析失败"));
                return;
            }

            // 🔥 获取参数（兼容两种参数名）
            Object complaintIdObj = params.get("complaintId");
            Object contentObj = params.get("additionalContent");  // 🔥 修改这里
            if (contentObj == null) {
                contentObj = params.get("content");  // 🔥 兼容旧版本
            }

            System.out.println(">>> complaintId: " + complaintIdObj);
            System.out.println(">>> content: " + contentObj);

            if (complaintIdObj == null || contentObj == null) {
                System.err.println("❌ 参数缺失");
                ResponseUtil.writeJson(response, ResponseUtil.error("参数缺失"));
                return;
            }

            // 🔥 转换参数类型
            Integer complaintId = null;
            if (complaintIdObj instanceof Number) {
                complaintId = ((Number) complaintIdObj).intValue();
            } else if (complaintIdObj instanceof String) {
                try {
                    complaintId = Integer.parseInt((String) complaintIdObj);
                } catch (NumberFormatException e) {
                    System.err.println("❌ complaintId 格式错误: " + complaintIdObj);
                    ResponseUtil.writeJson(response, ResponseUtil.error("投诉ID格式错误"));
                    return;
                }
            }

            String content = contentObj.toString().trim();

            if (complaintId == null || complaintId <= 0) {
                System.err.println("❌ 无效的 complaintId: " + complaintId);
                ResponseUtil.writeJson(response, ResponseUtil.error("无效的投诉ID"));
                return;
            }

            if (content.isEmpty()) {
                System.err.println("❌ 追加内容为空");
                ResponseUtil.writeJson(response, ResponseUtil.error("追加内容不能为空"));
                return;
            }

            System.out.println(">>> 准备追加说明:");
            System.out.println("    投诉ID: " + complaintId);
            System.out.println("    追加内容: " + content);
            System.out.println("    业主ID: " + ownerId);

            // 🔥 1. 先查询投诉记录，验证权限和状态
            Complaint complaint = complaintService.getComplaintDetail(complaintId);

            if (complaint == null) {
                System.err.println("❌ 投诉记录不存在");
                ResponseUtil.writeJson(response, ResponseUtil.error("投诉记录不存在"));
                return;
            }

            // 🔥 2. 权限校验
            if (!ownerId.equals(complaint.getOwnerId())) {
                System.err.println(">>> 权限校验失败: 当前用户=" + ownerId + ", 投诉归属=" + complaint.getOwnerId());
                ResponseUtil.writeJson(response, ResponseUtil.error("无权操作此投诉记录"));
                return;
            }

            // 🔥 3. 状态校验：只能在待处理和处理中追加
            String status = complaint.getComplaintStatus();
            if (!"pending".equals(status) && !"processing".equals(status)) {
                System.err.println(">>> 状态校验失败: status=" + status);
                ResponseUtil.writeJson(response, ResponseUtil.error("只能对待处理或处理中的投诉追加说明"));
                return;
            }

            // 🔥 4. 执行追加
            System.out.println(">>> 开始追加说明");
            Map<String, Object> result = complaintService.appendContent(complaintId, ownerId, content);

            System.out.println("✅ 追加结果: " + result);
            ResponseUtil.writeJson(response, result);

        } catch (Exception e) {
            System.err.println("❌ 追加说明异常: " + e.getMessage());
            e.printStackTrace();
            ResponseUtil.writeJson(response, ResponseUtil.error("追加失败: " + e.getMessage()));
        }
    }

    /**
     * 🔥 删除投诉记录（修复版）
     * 允许删除：已关闭、已解决、已撤销的投诉
     */
    private void deleteComplaint(HttpServletRequest request, HttpServletResponse response,
                                 String ownerId) throws IOException {
        System.out.println("=== 正在执行删除投诉 ===");
        try {
            BufferedReader reader = request.getReader();
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> params = gson.fromJson(reader, type);

            if (params == null || params.get("complaintId") == null) {
                ResponseUtil.writeJson(response, ResponseUtil.error("参数缺失：complaintId"));
                return;
            }

            Integer complaintId = ((Number) params.get("complaintId")).intValue();

            // 🔥 1. 先查询投诉记录，验证权限和状态
            System.out.println("🔍 查询投诉详情，ID: " + complaintId);
            Complaint complaint = complaintService.getComplaintDetail(complaintId);

            if (complaint == null) {
                System.out.println("❌ 投诉记录不存在");
                ResponseUtil.writeJson(response, ResponseUtil.error("投诉记录不存在"));
                return;
            }

            // 🔥 2. 权限校验：只能删除自己的投诉
            if (!ownerId.equals(complaint.getOwnerId())) {
                System.out.println(">>> 权限校验失败: 当前用户=" + ownerId + ", 投诉归属=" + complaint.getOwnerId());
                ResponseUtil.writeJson(response, ResponseUtil.error("无权删除此投诉记录"));
                return;
            }

            // 🔥 3. 状态校验：允许删除已关闭、已解决、已撤销的投诉
            String status = complaint.getComplaintStatus();
            String reply = complaint.getReply();

            System.out.println("🔍 投诉状态: " + status);
            System.out.println("🔍 回复内容: " + (reply != null ? reply.substring(0, Math.min(50, reply.length())) + "..." : "无"));

            // 判断是否为已撤销状态（closed + 包含撤销标记）
            boolean isCancelled = "closed".equals(status) && reply != null &&
                    (reply.contains("【业主主动撤销】") || reply.contains("【管理员驳回】"));

            // 🔥 修改：允许删除 closed、resolved 或已撤销的投诉
            boolean canDelete = "closed".equals(status) || "resolved".equals(status) || isCancelled;

            System.out.println("🔍 是否可删除: " + canDelete + " (已撤销: " + isCancelled + ")");

            if (!canDelete) {
                System.out.println(">>> 状态校验失败: status=" + status);
                ResponseUtil.writeJson(response, ResponseUtil.error("只能删除已关闭、已解决或已撤销的投诉记录"));
                return;
            }

            // 🔥 4. 执行删除
            System.out.println(">>> 开始删除投诉，ID: " + complaintId);
            boolean success = complaintService.deleteComplaint(complaintId);

            if (success) {
                System.out.println("✅ 删除成功: complaintId=" + complaintId + ", ownerId=" + ownerId);
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "删除成功");
                ResponseUtil.writeJson(response, result);
            } else {
                System.out.println("❌ 删除失败");
                ResponseUtil.writeJson(response, ResponseUtil.error("删除失败"));
            }

        } catch (Exception e) {
            System.out.println("❌ 删除投诉异常: " + e.getMessage());
            e.printStackTrace();
            ResponseUtil.writeJson(response, ResponseUtil.error("删除失败: " + e.getMessage()));
        }
    }

    /**
     * 🔥 7. 获取统计信息
     */
    private void getMyStatistics(HttpServletRequest request, HttpServletResponse response,
                                 String ownerId) throws IOException {
        try {
            Map<String, Object> summary = complaintService.getOwnerComplaintSummary(ownerId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", summary);

            ResponseUtil.writeJson(response, result);
        } catch (Exception e) {
            ResponseUtil.writeJson(response, ResponseUtil.error("统计获取失败"));
        }
    }
}
