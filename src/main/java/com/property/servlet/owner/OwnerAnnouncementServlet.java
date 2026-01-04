package com.property.servlet.owner;

import com.google.gson.Gson;
import com.property.entity.Announcement;
import com.property.service.AnnouncementService;
import com.property.util.Result;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/owner/announcement")
public class OwnerAnnouncementServlet extends HttpServlet {

    private final AnnouncementService announcementService = new AnnouncementService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 🔥 设置编码
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        try {
            // 🔥 详细日志
            System.out.println("========== OwnerAnnouncementServlet 被访问 ==========");
            System.out.println("请求 URI: " + request.getRequestURI());
            System.out.println("Query String: " + request.getQueryString());

            String method = request.getParameter("method");
            System.out.println("Method: " + method);

            if (method == null || method.isEmpty()) {
                System.err.println("❌ method 参数为空");
                writeJson(response, Result.error(400, "缺少 method 参数"));
                return;
            }

            switch (method) {
                case "list":
                    handleList(request, response);
                    break;
                case "detail":
                    handleDetail(request, response);
                    break;
                default:
                    System.err.println("❌ 未知的 method: " + method);
                    writeJson(response, Result.error(400, "未知的操作: " + method));
            }

        } catch (Exception e) {
            System.err.println("========== 发生异常 ==========");
            e.printStackTrace();
            writeJson(response, Result.error(500, "服务器错误: " + e.getMessage()));
        }
    }
    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            System.out.println(">>> 处理 list 请求");

            // 获取参数
            int pageNum = getIntParameter(request, "pageNum", 1);
            int pageSize = getIntParameter(request, "pageSize", 10);
            String announcementType = request.getParameter("announcementType");
            String keyword = request.getParameter("keyword");  // 🔥 新增

            System.out.println("pageNum: " + pageNum);
            System.out.println("pageSize: " + pageSize);
            System.out.println("announcementType: " + announcementType);
            System.out.println("keyword: " + keyword);  // 🔥 新增

            // 查询数据（调用带搜索的方法）
            List<Announcement> list = announcementService.getPublishedAnnouncements(
                    announcementType, keyword, pageNum, pageSize);  // 🔥 传递 keyword
            int totalCount = announcementService.getPublishedAnnouncementCount(
                    announcementType, keyword);  // 🔥 传递 keyword

            System.out.println("查询结果: " + list.size() + " 条");
            System.out.println("总数: " + totalCount);

            // 返回结果
            Map<String, Object> data = new HashMap<>();
            data.put("list", list);
            data.put("totalCount", totalCount);

            writeJson(response, Result.success(data));

        } catch (Exception e) {
            System.err.println("❌ handleList 异常");
            e.printStackTrace();
            writeJson(response, Result.error("查询失败: " + e.getMessage()));
        }
    }


    private void handleDetail(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            System.out.println(">>> 处理 detail 请求");

            int id = getIntParameter(request, "id", 0);
            System.out.println("公告 ID: " + id);

            if (id <= 0) {
                writeJson(response, Result.error("无效的公告ID"));
                return;
            }

            // 查询详情并增加浏览次数
            Announcement announcement = announcementService.getAnnouncementDetailAndIncreaseView(id);

            if (announcement == null) {
                writeJson(response, Result.error("公告不存在"));
                return;
            }

            System.out.println("查询成功: " + announcement.getTitle());
            writeJson(response, Result.success(announcement));

        } catch (Exception e) {
            System.err.println("❌ handleDetail 异常");
            e.printStackTrace();
            writeJson(response, Result.error("查询失败: " + e.getMessage()));
        }
    }

    private int getIntParameter(HttpServletRequest request, String name, int defaultValue) {
        String value = request.getParameter(name);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void writeJson(HttpServletResponse response, Result<?> result) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String json = gson.toJson(result);
        System.out.println(">>> 返回 JSON: " + json);

        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }
}
