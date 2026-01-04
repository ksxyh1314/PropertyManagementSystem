package com.property.util;

/**
 * 统一响应结果类
 * 用于封装接口返回的数据
 */
public class Result<T> {

    /**
     * 响应码（200=成功，其他=失败）
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    // ==================== 构造方法 ====================

    public Result() {
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 🔥 成功响应（带数据）
     *
     * @param data 响应数据
     * @return Result 对象
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 🔥 成功响应（自定义消息）
     *
     * @param message 响应消息
     * @param data 响应数据
     * @return Result 对象
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /**
     * 🔥 成功响应（无数据）
     *
     * @param message 响应消息
     * @return Result 对象
     */
    public static <T> Result<T> success(String message) {
        return new Result<>(200, message, null);
    }

    /**
     * 🔥 失败响应（默认 500 错误码）
     *
     * @param message 错误消息
     * @return Result 对象
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    /**
     * 🔥 失败响应（自定义错误码）
     *
     * @param code 错误码
     * @param message 错误消息
     * @return Result 对象
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 🔥 失败响应（带数据）
     *
     * @param code 错误码
     * @param message 错误消息
     * @param data 响应数据
     * @return Result 对象
     */
    public static <T> Result<T> error(int code, String message, T data) {
        return new Result<>(code, message, data);
    }

    // ==================== Getter 和 Setter ====================

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    // ==================== 便捷方法 ====================

    /**
     * 判断是否成功
     *
     * @return true=成功，false=失败
     */
    public boolean isSuccess() {
        return this.code == 200;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
