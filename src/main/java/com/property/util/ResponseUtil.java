package com.property.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP 响应工具类
 * 用于统一处理 JSON 响应
 */
public class ResponseUtil {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    /**
     * 发送成功响应
     * @param response HttpServletResponse
     * @param data 响应数据
     */
    public static void writeJson(HttpServletResponse response, Map<String, Object> data)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String json = gson.toJson(data);
        System.out.println(">>> 返回 JSON: " + json);

        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }

    /**
     * 发送成功响应（简化版）
     * @param message 成功消息
     * @return 响应数据 Map
     */
    public static Map<String, Object> success(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", message);
        return result;
    }

    /**
     * 发送成功响应（带数据）
     * @param message 成功消息
     * @param data 响应数据
     * @return 响应数据 Map
     */
    public static Map<String, Object> success(String message, Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", message);
        result.put("data", data);
        return result;
    }

    /**
     * 发送错误响应
     * @param message 错误消息
     * @return 响应数据 Map
     */
    public static Map<String, Object> error(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        return result;
    }

    /**
     * 发送错误响应（带错误码）
     * @param code 错误码
     * @param message 错误消息
     * @return 响应数据 Map
     */
    public static Map<String, Object> error(int code, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("code", code);
        result.put("message", message);
        return result;
    }
}
