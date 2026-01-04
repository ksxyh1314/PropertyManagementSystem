package com.property.servlet;

import com.property.entity.PaymentRecord;
import com.property.service.PaymentService;
import com.property.util.ExcelExportUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet("/payment/export")
public class ExcelExportServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ExcelExportServlet.class);
    private static final int MAX_EXPORT_SIZE = 50000;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 在方法内创建 Service 实例，确保线程安全
        PaymentService paymentService = new PaymentService();
        OutputStream outputStream = null;

        try {
            // 获取查询参数
            String keyword = request.getParameter("keyword");
            String status = request.getParameter("status");
            String itemId = request.getParameter("itemId");

            logger.info("📥 开始导出缴费记录 - 关键字: {}, 状态: {}, 项目ID: {}",
                    keyword, status, itemId);

            // 查询所有符合条件的数据
            List<PaymentRecord> records;

            if (itemId != null && !itemId.trim().isEmpty()) {
                records = paymentService.findAll(keyword, status, itemId);
            } else {
                records = paymentService.findAll(keyword, status);
            }

            // 检查数据是否为空
            if (records == null || records.isEmpty()) {
                logger.warn("⚠️ 没有找到符合条件的记录");
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write(
                        "<script>alert('没有可导出的数据');history.back();</script>"
                );
                return;
            }

            logger.info("✅ 查询到 {} 条记录", records.size());

            // 限制导出数量
            if (records.size() > MAX_EXPORT_SIZE) {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write(
                        "<script>alert('导出数据过多（超过" + MAX_EXPORT_SIZE +
                                "条），请缩小查询范围');history.back();</script>"
                );
                return;
            }

            // 生成文件名
            String fileName = generateFileName(status);

            // 设置响应头
            setResponseHeaders(response, fileName);

            // 导出Excel
            outputStream = response.getOutputStream();
            ExcelExportUtil.exportPaymentRecordList(records, outputStream);

            outputStream.flush();

            logger.info("✅ 导出成功：{} ({} 条记录)", fileName, records.size());

        } catch (Exception e) {
            logger.error("❌ 导出失败", e);
            handleExportError(response, outputStream, e);
        }
    }

    /**
     * 生成导出文件名
     */
    private String generateFileName(String status) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());

        String prefix = "缴费记录";
        if ("paid".equals(status)) {
            prefix = "已缴费记录";
        } else if ("unpaid".equals(status)) {
            prefix = "未缴费记录";
        } else if ("overdue".equals(status)) {
            prefix = "逾期记录";
        }

        return prefix + "_" + timestamp + ".xlsx";
    }

    /**
     * 设置响应头
     */
    private void setResponseHeaders(HttpServletResponse response, String fileName)
            throws IOException {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");

        String encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8")
                .replaceAll("\\+", "%20");

        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + encodedFileName +
                        "\"; filename*=UTF-8''" + encodedFileName);

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    /**
     * 处理导出错误
     */
    private void handleExportError(HttpServletResponse response,
                                   OutputStream outputStream,
                                   Exception e) throws IOException {

        if (outputStream != null && response.isCommitted()) {
            logger.error("❌ 响应已提交，无法发送错误信息");
            return;
        }

        response.reset();
        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        String errorMsg = "导出失败：" + e.getMessage();
        response.getWriter().write(
                "<script>alert('" + errorMsg.replace("'", "\\'") +
                        "');history.back();</script>"
        );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}

