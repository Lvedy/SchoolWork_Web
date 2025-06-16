package org.example.common.exception;


import lombok.Data;
import org.example.common.response.ApiResultCode;

/**
 * @Author:hzg
 * @Date:Created in 9:34 2022/4/15
 * @Description: 业务逻辑 异常
 */
@Data
public class AppRunTimeException extends RuntimeException {
    private Integer code;
    private String msg;

    public AppRunTimeException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public AppRunTimeException(String msg) {
        super(msg);
        this.code = ApiResultCode.FAIL.getCode();
        this.msg = msg;
    }

    public AppRunTimeException(ApiResultCode responseCode) {
        super(responseCode.getMsg());
        this.code = responseCode.getCode();
        this.msg = responseCode.getMsg();
    }
}