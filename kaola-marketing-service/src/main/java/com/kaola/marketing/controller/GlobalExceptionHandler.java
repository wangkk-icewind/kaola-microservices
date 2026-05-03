package com.kaola.marketing.controller;

import com.kaola.common.core.dto.Result;
import com.kaola.common.core.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result<Void> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.warn("数据完整性异常: {}", e.getMessage());
        String msg = e.getMessage();
        if (msg != null && msg.contains("Duplicate entry")) {
            return Result.error("数据已存在，请勿重复创建");
        }
        if (msg != null && msg.contains("doesn't have a default value")) {
            return Result.error("必填字段不能为空");
        }
        if (msg != null && msg.contains("cannot be null")) {
            return Result.error("必填字段不能为空");
        }
        return Result.error("数据写入失败，请检查必填字段");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("请求参数解析失败: {}", e.getMessage());
        return Result.error("请求参数格式错误，日期格式请使用 yyyy-MM-ddTHH:mm:ss");
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: {}", e.getMessage(), e);
        return Result.error(e.getMessage() != null ? e.getMessage() : "服务内部错误");
    }
}
