package com.property.servlet.owner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.property.entity.PaymentRecord;
import com.property.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * 业主端 - 缴费管理 Servlet（增加日志记录）
 *
 * ✅ 完全适配数据库下划线命名
 * ✅ 添加详细日志输出
 * ✅ 增加操作日志记录
 */
@WebServlet("/owner/payment")
public class OwnerPaymentServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(OwnerPaymentServlet.class);
    private final PaymentService paymentService = new PaymentService();
    private final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .serializeNulls()
            .create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    /**
     * 🔥 统一请求处理（使用 action 参数）
     */
    private void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        String action = req.getParameter("action");
        String ownerId = req.getParameter("ownerId");

        logger.info("========================================");
        logger.info("📥 收到业主缴费请求");
        logger.info("  Action: {}", action);
        logger.info("  OwnerId: {}", ownerId);
        logger.info("  Method: {}", req.getMethod());
        logger.info("========================================");

        if (action == null || action.trim().isEmpty()) {
            logger.error("❌ 缺少 action 参数");
            writeError(resp, "缺少 action 参数");
            return;
        }

        try {
            switch (action) {
                case "summary":
                    getSummary(req, resp);
                    break;
                case "list":
                    getList(req, resp);
                    break;
                case "history":
                    getHistory(req, resp);
                    break;
                case "detail":
                    getDetail(req, resp);
                    break;
                case "pay":
                    processPay(req, resp);
                    break;
                case "batchPay":
                    processBatchPay(req, resp);
                    break;
                default:
                    logger.error("❌ 无效的操作：{}", action);
                    writeError(resp, "无效的操作：" + action);
            }
        } catch (Exception e) {
            logger.error("❌ 处理请求失败，action={}", action, e);
            writeError(resp, "系统错误：" + e.getMessage());
        }
    }

// ==================== 📊 查询相关 ====================

    private void getSummary(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String ownerId = req.getParameter("ownerId");

        logger.info("📊 查询欠费汇总，ownerId={}", ownerId);

        if (ownerId == null || ownerId.trim().isEmpty()) {
            logger.error("❌ 业主ID不能为空");
            writeError(resp, "业主ID不能为空");
            return;
        }

        try {
            Map<String, Object> summary = paymentService.getUnpaidSummary(ownerId);

            logger.info("✅ 查询成功，原始数据: {}", summary);

            Map<String, Object> result = new HashMap<>();
            result.put("unpaid_count", summary.get("unpaidCount"));
            result.put("unpaid_amount", summary.get("unpaidAmount"));
            result.put("overdue_count", summary.get("overdueCount"));
            result.put("overdue_amount", summary.get("overdueAmount"));
            result.put("total_count", summary.get("totalCount"));
            result.put("total_amount", summary.get("totalAmount"));

            logger.info("📤 返回数据: {}", result);
            logger.info("  未逾期: {}笔, ¥{}", result.get("unpaid_count"), result.get("unpaid_amount"));
            logger.info("  已逾期: {}笔, ¥{}", result.get("overdue_count"), result.get("overdue_amount"));
            logger.info("  总欠费: {}笔, ¥{}", result.get("total_count"), result.get("total_amount"));

            writeSuccess(resp, "查询成功", result);

        } catch (Exception e) {
            logger.error("❌ 查询欠费汇总失败，ownerId={}", ownerId, e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询待缴费列表
     */
    private void getList(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        int pageNum = getIntParameter(req, "pageNum", 1);
        int pageSize = getIntParameter(req, "pageSize", 10);
        String ownerId = req.getParameter("ownerId");

        logger.info("📋 查询待缴费列表");
        logger.info("  ownerId={}", ownerId);
        logger.info("  pageNum={}", pageNum);
        logger.info("  pageSize={}", pageSize);

        if (ownerId == null || ownerId.trim().isEmpty()) {
            logger.error("❌ 业主ID不能为空");
            writeError(resp, "业主ID不能为空");
            return;
        }

        String paymentStatus = req.getParameter("paymentStatus");
        String itemId = req.getParameter("itemId");
        String keyword = req.getParameter("keyword");

        logger.info("  paymentStatus={}", paymentStatus);
        logger.info("  itemId={}", itemId);
        logger.info("  keyword={}", keyword);

        Map<String, Object> params = new HashMap<>();
        params.put("ownerId", ownerId);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        params.put("sortBy", "due_date");
        params.put("sortOrder", "desc");

        if (paymentStatus != null && !paymentStatus.trim().isEmpty()) {
            params.put("statusList", Arrays.asList(paymentStatus.split(",")));
        }

        if (itemId != null && !itemId.trim().isEmpty()) {
            params.put("itemId", itemId);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            params.put("keyword", keyword.trim());
        }

        try {
            Map<String, Object> result = paymentService.findByPageWithSearch(params);

            logger.info("✅ 查询成功");
            logger.info("  total={}", result.get("total"));
            logger.info("  list.size={}", result.get("list") != null ? ((List<?>)result.get("list")).size() : 0);

            if (result.get("list") != null) {
                List<?> list = (List<?>) result.get("list");
                if (!list.isEmpty()) {
                    logger.info("  第一条数据: {}", list.get(0));
                }
            }

            writeSuccess(resp, "查询成功", result);

        } catch (Exception e) {
            logger.error("❌ 查询缴费记录失败，ownerId={}", ownerId, e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询缴费历史
     */
    private void getHistory(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        int pageNum = getIntParameter(req, "pageNum", 1);
        int pageSize = getIntParameter(req, "pageSize", 10);
        String ownerId = req.getParameter("ownerId");
        String keyword = req.getParameter("keyword");
        String itemId = req.getParameter("itemId");

        logger.info("📜 查询缴费历史");
        logger.info("  ownerId={}", ownerId);
        logger.info("  pageNum={}", pageNum);
        logger.info("  keyword={}", keyword);
        logger.info("  itemId={}", itemId);

        if (ownerId == null || ownerId.trim().isEmpty()) {
            logger.error("❌ 业主ID不能为空");
            writeError(resp, "业主ID不能为空");
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("ownerId", ownerId);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        params.put("statusList", Arrays.asList("paid"));
        params.put("sortBy", "payment_date");
        params.put("sortOrder", "desc");

        if (keyword != null && !keyword.trim().isEmpty()) {
            params.put("keyword", keyword.trim());
        }

        if (itemId != null && !itemId.trim().isEmpty()) {
            params.put("itemId", itemId);
        }

        try {
            Map<String, Object> result = paymentService.findByPageWithSearch(params);

            logger.info("✅ 查询历史成功");
            logger.info("  total={}", result.get("total"));
            logger.info("  list.size={}", result.get("list") != null ? ((List<?>)result.get("list")).size() : 0);

            writeSuccess(resp, "查询成功", result);

        } catch (Exception e) {
            logger.error("❌ 查询缴费历史失败，ownerId={}", ownerId, e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询账单详情
     */
    private void getDetail(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Integer recordId = getIntParameter(req, "recordId");
        String ownerId = req.getParameter("ownerId");

        logger.info("🔍 查询账单详情");
        logger.info("  recordId={}", recordId);
        logger.info("  ownerId={}", ownerId);

        if (recordId == null) {
            logger.error("❌ 缴费记录ID不能为空");
            writeError(resp, "缴费记录ID不能为空");
            return;
        }
        if (ownerId == null || ownerId.trim().isEmpty()) {
            logger.error("❌ 业主ID不能为空");
            writeError(resp, "业主ID不能为空");
            return;
        }

        try {
            PaymentRecord record = paymentService.getDetailByIdForOwner(recordId, ownerId);

            if (record == null) {
                logger.warn("❌ 记录不存在或无权访问: recordId={}, ownerId={}", recordId, ownerId);
                writeError(resp, "记录不存在或无权查看");
                return;
            }

            Map<String, Object> detail = paymentService.getPaymentDetailWithChargeItem(String.valueOf(recordId));

            logger.info("✅ 查询详情成功");
            logger.info("  paymentStatus={}", detail.get("payment_status"));
            logger.info("  amount={}", detail.get("amount"));
            logger.info("  lateFee={}", detail.get("late_fee"));

            String paymentStatus = (String) detail.get("payment_status");
            if (!"paid".equals(paymentStatus)) {
                Map<String, Object> calculation = paymentService.calculateLateFee(String.valueOf(recordId));

                Map<String, Object> result = new HashMap<>();
                result.put("record", detail);
                result.put("calculation", calculation);

                logger.info("  calculation={}", calculation);

                writeSuccess(resp, "查询成功", result);
            } else {
                writeSuccess(resp, "查询成功", detail);
            }

        } catch (Exception e) {
            logger.error("❌ 查询缴费详情失败，recordId={}", recordId, e);
            writeError(resp, "查询失败：" + e.getMessage());
        }
    }

    // ==================== 💰 缴费相关 ====================

    /**
     * ✅ 处理缴费（增加日志记录）
     */
    private void processPay(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Integer recordId = getIntParameter(req, "recordId");
        String ownerId = req.getParameter("ownerId");
        String paymentMethod = req.getParameter("paymentMethod");
        Integer operatorId = getIntParameter(req, "operatorId", 1);

        logger.info("💰 处理缴费");
        logger.info("  recordId={}", recordId);
        logger.info("  ownerId={}", ownerId);
        logger.info("  paymentMethod={}", paymentMethod);
        logger.info("  operatorId={}", operatorId);

        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            paymentMethod = "online";
        }

        if (recordId == null) {
            logger.error("❌ 缴费记录ID不能为空");
            writeError(resp, "缴费记录ID不能为空");
            return;
        }
        if (ownerId == null || ownerId.trim().isEmpty()) {
            logger.error("❌ 业主ID不能为空");
            writeError(resp, "业主ID不能为空");
            return;
        }

        try {
            PaymentRecord record = paymentService.findById(String.valueOf(recordId));
            if (record == null) {
                logger.error("❌ 缴费记录不存在，recordId={}", recordId);
                writeError(resp, "缴费记录不存在");
                return;
            }
            if (!ownerId.equals(record.getOwnerId())) {
                logger.warn("⚠️ 业主 {} 尝试缴费不属于自己的记录 {}", ownerId, recordId);
                writeError(resp, "无权操作此记录");
                return;
            }

            // ✅ 传递 request 用于记录日志
            Map<String, Object> result = paymentService.processPayment(
                    recordId,
                    paymentMethod,
                    operatorId,
                    req  // ✅ 传递请求对象
            );

            logger.info("✅ 缴费处理完成");
            logger.info("  success={}", result.get("success"));
            logger.info("  message={}", result.get("message"));
            logger.info("  totalAmount={}", result.get("totalAmount"));
            logger.info("  receiptNo={}", result.get("receiptNo"));

            if ((Boolean) result.get("success")) {
                writeSuccess(resp, (String) result.get("message"), result);
            } else {
                writeError(resp, (String) result.get("message"));
            }

        } catch (Exception e) {
            logger.error("❌ 缴费处理失败，recordId={}", recordId, e);
            writeError(resp, "缴费失败：" + e.getMessage());
        }
    }

    /**
     * ✅ 批量缴费（增加日志记录）
     */
    private void processBatchPay(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String recordIds = req.getParameter("recordIds");
        String ownerId = req.getParameter("ownerId");
        String paymentMethod = req.getParameter("paymentMethod");
        Integer operatorId = getIntParameter(req, "operatorId", 1);

        logger.info("💰 批量缴费");
        logger.info("  recordIds={}", recordIds);
        logger.info("  ownerId={}", ownerId);
        logger.info("  paymentMethod={}", paymentMethod);

        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            paymentMethod = "online";
        }

        if (recordIds == null || recordIds.trim().isEmpty()) {
            logger.error("❌ 缴费记录ID不能为空");
            writeError(resp, "缴费记录ID不能为空");
            return;
        }
        if (ownerId == null || ownerId.trim().isEmpty()) {
            logger.error("❌ 业主ID不能为空");
            writeError(resp, "业主ID不能为空");
            return;
        }

        try {
            List<Integer> idList = parseRecordIds(recordIds);
            if (idList.isEmpty()) {
                logger.error("❌ 没有有效的缴费记录ID");
                writeError(resp, "没有有效的缴费记录ID");
                return;
            }

            logger.info("  解析后的ID列表: {}", idList);

            // 验证权限
            for (Integer recordId : idList) {
                PaymentRecord record = paymentService.findById(String.valueOf(recordId));
                if (record == null) {
                    logger.error("❌ 缴费记录 {} 不存在", recordId);
                    writeError(resp, "缴费记录 " + recordId + " 不存在");
                    return;
                }
                if (!ownerId.equals(record.getOwnerId())) {
                    logger.warn("⚠️ 业主 {} 尝试批量缴费不属于自己的记录 {}", ownerId, recordId);
                    writeError(resp, "无权操作记录 " + recordId);
                    return;
                }
            }

            // ✅ 传递 request 用于记录日志
            Map<String, Object> result = executeBatchPayment(idList, paymentMethod, operatorId, req);

            int successCount = (int) result.get("successCount");
            int failCount = (int) result.get("failCount");

            logger.info("✅ 批量缴费完成");
            logger.info("  successCount={}", successCount);
            logger.info("  failCount={}", failCount);
            logger.info("  totalAmount={}", result.get("totalAmount"));

            if (failCount == 0) {
                writeSuccess(resp, "批量缴费成功，共缴费 " + successCount + " 笔", result);
            } else if (successCount == 0) {
                writeError(resp, "批量缴费全部失败");
            } else {
                writeSuccess(resp, "批量缴费部分成功：成功 " + successCount + " 笔，失败 " + failCount + " 笔", result);
            }

        } catch (Exception e) {
            logger.error("❌ 批量缴费处理失败，recordIds={}", recordIds, e);
            writeError(resp, "批量缴费失败：" + e.getMessage());
        }
    }

    // ==================== 🛠️ 辅助方法 ====================

    private List<Integer> parseRecordIds(String recordIds) {
        List<Integer> idList = new ArrayList<>();
        String[] idArray = recordIds.split(",");

        for (String id : idArray) {
            try {
                idList.add(Integer.parseInt(id.trim()));
            } catch (NumberFormatException e) {
                logger.warn("⚠️ 无效的记录ID: {}", id);
            }
        }

        return idList;
    }

    /**
     * ✅ 执行批量缴费（增加日志记录）
     */
    private Map<String, Object> executeBatchPayment(List<Integer> idList, String paymentMethod,
                                                    Integer operatorId, HttpServletRequest req) {
        int successCount = 0;
        int failCount = 0;
        double totalAmount = 0.0;
        List<String> errors = new ArrayList<>();

        for (Integer recordId : idList) {
            try {
                // ✅ 传递 request 用于记录日志
                Map<String, Object> result = paymentService.processPayment(
                        recordId,
                        paymentMethod,
                        operatorId,
                        req  // ✅ 传递请求对象
                );

                if ((Boolean) result.get("success")) {
                    successCount++;
                    Object amountObj = result.get("totalAmount");
                    if (amountObj != null) {
                        totalAmount += Double.parseDouble(amountObj.toString());
                    }
                } else {
                    failCount++;
                    errors.add("记录 " + recordId + ": " + result.get("message"));
                }
            } catch (Exception e) {
                failCount++;
                errors.add("记录 " + recordId + ": " + e.getMessage());
                logger.error("❌ 记录 {} 缴费失败", recordId, e);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("totalAmount", totalAmount);
        result.put("errors", errors);

        return result;
    }

    private int getIntParameter(HttpServletRequest req, String name, int defaultValue) {
        String value = req.getParameter(name);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("⚠️ 参数 {} 格式错误: {}", name, value);
            return defaultValue;
        }
    }

    private Integer getIntParameter(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("⚠️ 参数 {} 格式错误: {}", name, value);
            return null;
        }
    }

    /**
     * 🔥 返回成功响应
     */
    private void writeSuccess(HttpServletResponse resp, String message, Object data) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", message);
        result.put("data", data);

        String json = gson.toJson(result);

        logger.info("📤 返回成功响应");
        logger.info("  message={}", message);
        logger.info("  data={}", data != null ? data.getClass().getSimpleName() : "null");

        PrintWriter out = resp.getWriter();
        out.print(json);
        out.flush();
    }

    /**
     * 🔥 返回错误响应
     */
    private void writeError(HttpServletResponse resp, String message) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("msg", message);
        result.put("data", null);

        String json = gson.toJson(result);

        logger.error("📤 返回错误响应: {}", message);

        PrintWriter out = resp.getWriter();
        out.print(json);
        out.flush();
    }
}
