package org.example.common.config;


import lombok.extern.log4j.Log4j2;
import org.example.common.exception.AppRunTimeException;
import org.example.common.response.ApiResult;
import org.example.common.response.ApiResultCode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/**
 * @Author:hzg
 * @Date:Created in 9:37 2022/4/15
 * @Description: 异常处理
 */
@Controller
@RestControllerAdvice
@Log4j2
public class DefaultExceptionHandlerConfig {


    @ExceptionHandler(AppRunTimeException.class)
    public ApiResult unauthorizedExceptionHandler(AppRunTimeException e) {
        e.printStackTrace();
        log.error("自定义抛出异常:" + e.getMsg());
        return ApiResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResult ex(Exception e) {
        //e.printStackTrace();
        log.error("异常:", e);
        //违反数据库约束
        if (e instanceof DataIntegrityViolationException) {

            Throwable rootCause = getRootCause(e);
            // 唯一约束冲突
            if (rootCause instanceof SQLIntegrityConstraintViolationException) {
                String message = rootCause.getMessage();
                if (message != null) {
                    // 唯一索引冲突 (通常包含 "Duplicate entry" 或 "unique constraint" 关键字)
                    if (message.contains("Duplicate entry") || message.contains("unique constraint")) {
                        // 提取唯一键名称和冲突值
                        String constraintName = extractConstraintName(message);
                        String duplicateValue = extractDuplicateValue(message);

                        return ApiResult.res(
                                ApiResultCode.SQL_UNIQUE_CONSTRAINT_ERROR
                        );
                    }
                }
            }


            return ApiResult.res(ApiResultCode.SQL_CONSTRAINTVIOLATION_ERROR);
        }
        //sql语句异常
        if (e instanceof BadSqlGrammarException) {
            return ApiResult.res(ApiResultCode.SQL_SYNTAXERROR_ERROR);
        }
        return ApiResult.error(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult methodParamEx(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<FieldError> list = ex.getBindingResult().getFieldErrors();
        StringBuilder builder = new StringBuilder();
        list.forEach(objectError -> {
            builder.append(objectError.getField() + ":" + objectError.getDefaultMessage());
        });
        return ApiResult.error(builder.toString());
    }


    /**
     * 获取根本异常原因
     */
    private Throwable getRootCause(Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }

    /**
     * 从异常消息中提取约束名称 (例如: uk1)
     */
    private String extractConstraintName(String message) {
        // 示例: "key 't_service_config.uk1'" -> "uk1"
        int startIndex = message.indexOf("key '");
        if (startIndex != -1) {
            startIndex += 5; // 跳过 "key '"
            int endIndex = message.indexOf("'", startIndex);
            if (endIndex != -1) {
                String fullKey = message.substring(startIndex, endIndex);
                int dotIndex = fullKey.lastIndexOf('.');
                if (dotIndex != -1) {
                    return fullKey.substring(dotIndex + 1); // 表名.约束名 -> 约束名
                }
                return fullKey;
            }
        }
        return "未知约束";
    }

    /**
     * 从异常消息中提取重复值
     */
    private String extractDuplicateValue(String message) {
        // 示例: "Duplicate entry 'iFlytek-中国-国内节点'" -> "iFlytek-中国-国内节点"
        int startIndex = message.indexOf("Duplicate entry '");
        if (startIndex != -1) {
            startIndex += 16; // 跳过 "Duplicate entry '"
            int endIndex = message.indexOf("'", startIndex);
            if (endIndex != -1) {
                return message.substring(startIndex, endIndex);
            }
        }
        return "未知值";
    }
}