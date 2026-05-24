package com.ts.exception;

import com.ts.common.ApiResponse;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusiness(BusinessException ex) {
        return ApiResponse.fail(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ApiResponse<Void> handleValidation(Exception ex) {
        if (ex instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            String message = methodArgumentNotValidException.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            return ApiResponse.fail(400, message.isBlank() ? "参数校验失败" : message);
        }
        if (ex instanceof BindException bindException) {
            String message = bindException.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            return ApiResponse.fail(400, message.isBlank() ? "参数校验失败" : message);
        }
        return ApiResponse.fail(400, "参数校验失败");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<Void> handleBadRequest(HttpMessageNotReadableException ex) {
        return ApiResponse.fail(400, "请求体格式不正确");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ApiResponse<Void> handleAuthentication(AuthenticationException ex) {
        return ApiResponse.fail(401, "登录状态已失效，请重新登录");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<Void> handleAccessDenied(AccessDeniedException ex) {
        return ApiResponse.fail(403, "无权访问当前资源");
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleOther(Exception ex) {
        return ApiResponse.fail(500, "服务器内部错误");
    }
}
