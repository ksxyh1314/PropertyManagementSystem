package com.property.servlet;

import com.property.entity.OperationLog;
import com.property.service.OperationLogService;
import com.property.util.ExcelExportUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作日志 Servlet
 */
@WebServlet("/operationLog")
public class OperationLogServlet extends BaseServlet {
    private final OperationLogService operationLogService = new OperationLogService();

    /**
     * 分页查询日志列表
     */
    public void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 仅管理员可查看
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        int pageNum = getIntParameter(req, "pageNum", 1);
        int pageSize = getIntParameter(req, "pageSize", 10);
        String keyword = getStringParameter(req, "keyword");

        // 🔥 同时支持 module 和 operationType 参数
        String module = getStringParameter(req, "module");
        String operationType = getStringParameter(req, "operationType");

        // 优先使用 module 参数
        if (module != null && !module.isEmpty()) {
            operationType = module;
        }

        String startDate = getStringParameter(req, "startDate");
        String endDate = getStringParameter(req, "endDate");

        logger.info("📥 查询操作日志: pageNum={}, keyword={}, module={}, operationType={}, startDate={}, endDate={}",
                pageNum, keyword, module, operationType, startDate, endDate);

        try {
            Map<String, Object> result = operationLogService.findByPage(
                    pageNum, pageSize, keyword, operationType, startDate, endDate);

            logger.info("✅ 查询成功: total={}", result.get("total"));
            writeJson(resp, result);

        } catch (Exception e) {
            logger.error("❌ 查询操作日志失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID查询日志详情
     */
    public void findById(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        Integer logId = getIntParameter(req, "logId");
        if (logId == null) {
            writeError(resp, "日志ID不能为空");
            return;
        }

        try {
            OperationLog log = operationLogService.findById(logId);
            if (log != null) {
                writeSuccess(resp, "查询成功", log);
            } else {
                writeError(resp, "日志不存在");
            }
        } catch (Exception e) {
            logger.error("查询日志详情失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取统计数据
     */
    public void statistics(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        try {
            List<Map<String, Object>> typeStats = operationLogService.getOperationTypeStats();
            List<Map<String, Object>> userStats = operationLogService.getUserOperationStats();

            Map<String, Object> result = new HashMap<>();
            result.put("typeStats", typeStats);
            result.put("userStats", userStats);

            writeSuccess(resp, "查询成功", result);

        } catch (Exception e) {
            logger.error("获取统计数据失败", e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 导出日志到Excel
     */
    public void export(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!checkRole(req, resp, "admin")) {
            return;
        }

        String keyword = getStringParameter(req, "keyword");

        // 🔥 同时支持 module 和 operationType 参数
        String module = getStringParameter(req, "module");
        String operationType = getStringParameter(req, "operationType");

        // 优先使用 module 参数
        if (module != null && !module.isEmpty()) {
            operationType = module;
        }

        String startDate = getStringParameter(req, "startDate");
        String endDate = getStringParameter(req, "endDate");

        logger.info("📥 导出操作日志: keyword={}, module={}, operationType={}, startDate={}, endDate={}",
                keyword, module, operationType, startDate, endDate);

        OutputStream outputStream = null;

        try {
            List<OperationLog> logs = operationLogService.findAll(
                    keyword, operationType, startDate, endDate);

            if (logs == null || logs.isEmpty()) {
                resp.setContentType("text/html;charset=UTF-8");
                resp.getWriter().write("<script>alert('没有可导出的数据');history.back();</script>");
                return;
            }

            String fileName = "操作日志_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx";

            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            resp.setCharacterEncoding("UTF-8");
            String encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");

            outputStream = resp.getOutputStream();
            ExcelExportUtil.exportOperationLogList(logs, outputStream);

            outputStream.flush();

            logger.info("✅ 导出成功：{} ({} 条记录)", fileName, logs.size());

        } catch (Exception e) {
            logger.error("❌ 导出失败", e);
            if (outputStream == null && !resp.isCommitted()) {
                resp.setContentType("text/html;charset=UTF-8");
                resp.getWriter().write("<script>alert('导出失败：" + e.getMessage() + "');history.back();</script>");
            }
        }
    }
}
