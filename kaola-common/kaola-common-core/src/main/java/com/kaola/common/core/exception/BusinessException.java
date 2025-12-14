package com.kaola.common.core.exception;

import lombok.Getter;

/**
 * 业务异常
 *
 * @author Kaola Team
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误信息
     */
    private final String message;

    public BusinessException(String message) {
        super(message);
        this.code = -1;
        this.message = message;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = -1;
        this.message = message;
    }

    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    /**
     * 快捷创建业务异常
     */
    public static BusinessException of(String message) {
        return new BusinessException(message);
    }

    /**
     * 快捷创建业务异常（带错误码）
     */
    public static BusinessException of(Integer code, String message) {
        return new BusinessException(code, message);
    }
}
