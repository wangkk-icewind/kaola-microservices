package com.kaola.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果类
 *
 * @author Kaola Team
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码 (0-成功 其他-失败)
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 请求ID（用于追踪）
     */
    private String requestId;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功（无数据）
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMessage("success");
        return result;
    }

    /**
     * 成功（带数据）
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    /**
     * 成功（带消息和数据）
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 失败（仅消息）
     */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(-1);
        result.setMessage(message);
        return result;
    }

    /**
     * 失败（带错误码）
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 失败（带错误码和数据）
     */
    public static <T> Result<T> error(Integer code, String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 参数错误
     */
    public static <T> Result<T> badRequest(String message) {
        return error(400, message);
    }

    /**
     * 未授权
     */
    public static <T> Result<T> unauthorized(String message) {
        return error(401, message);
    }

    /**
     * 未授权（默认消息）
     */
    public static <T> Result<T> unauthorized() {
        return error(401, "请先登录");
    }

    /**
     * 禁止访问
     */
    public static <T> Result<T> forbidden(String message) {
        return error(403, message);
    }

    /**
     * 资源不存在
     */
    public static <T> Result<T> notFound(String message) {
        return error(404, message);
    }

    /**
     * 服务器内部错误
     */
    public static <T> Result<T> serverError(String message) {
        return error(500, message);
    }

    /**
     * 服务器内部错误（默认消息）
     */
    public static <T> Result<T> serverError() {
        return error(500, "系统繁忙，请稍后重试");
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return this.code != null && this.code == 0;
    }

    /**
     * 判断是否失败
     */
    public boolean isError() {
        return !isSuccess();
    }

    /**
     * 设置请求ID
     */
    public Result<T> requestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    /**
     * 创建一个新的Result
     */
    public static <T> Result<T> of(Integer code, String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
}
