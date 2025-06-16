package org.example.common.response;

import java.io.Serializable;

/**
 * @Author: hzg
 * @Date: 2025/06/06 2:17 下午
 */

public class ApiResult<T> implements Serializable {
    public Integer code;
    public T data;
    public String msg;

    public ApiResult() {
    }

    public ApiResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ApiResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ApiResult(ApiResultCode res, T data) {
        this.code = res.getCode();
        this.msg = res.getMsg();
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }


    public boolean isSuccess() {
        return this.code == Integer.valueOf(ApiResultCode.SUCCESS.getCode());
    }

    public static <T> ApiResult<T> success() {
        return ApiResult.res(ApiResultCode.SUCCESS);
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<T>(ApiResultCode.SUCCESS, data);
    }

    public static <T> ApiResult<T> error() {
        return ApiResult.res(ApiResultCode.FAIL);
    }

    public static <T> ApiResult<T> error(T data) {
        return new ApiResult<T>(ApiResultCode.FAIL, data);
    }

    public static <T> ApiResult<T> error(String msg) {
        return new ApiResult<T>(ApiResultCode.FAIL.getCode(), msg);
    }

    public static <T> ApiResult<T> res(int code, String msg) {
        return new ApiResult<T>(code, msg);
    }

    public static <T> ApiResult<T> error(int code, String msg) {
        return new ApiResult<T>(code, msg);
    }


    public static <T> ApiResult<T> res(ApiResultCode res) {
        return new ApiResult<T>(res.getCode(), res.getMsg());
    }


    public static ApiResult error(int code, String msg, Object data) {
        return new ApiResult(code, msg, data);
    }
}
