package org.example.common.response;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;

/**
 * @Author:hzg
 * @Date:Created in 9:30 2022/4/15
 * @Description: 返回结果状态码
 */
@ApiModel(value = "返回状态码", description = "返回状态码")
public enum ApiResultCode {
    SUCCESS(1, "success"),
    FAIL(-1, "fail"),
    LOGIN_FAIL(201, "login fail"),
    TOKEN_FAIL(203, "invalid token ,please login "),
    ROLE_NULL(204, "not set role"),

    PERMISSION_DENIED(500, "permission denied"),

    PARAMS_NOT_EXIST(1000, "param missing"),
    SQL_CONSTRAINTVIOLATION_ERROR(1001, "The constraint is violated, and the operation fails"),
    SQL_UNIQUE_CONSTRAINT_ERROR(1003, "The constraint is violated, and the operation fails"),
    SQL_SYNTAXERROR_ERROR(1002, "The SQL statement is incorrect");


    private int code;
    private String msg;

    ApiResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    @JsonValue
    public String showInfo() {
        return this.name() + "(" + this.code + "," + this.msg + ")";
    }

}
