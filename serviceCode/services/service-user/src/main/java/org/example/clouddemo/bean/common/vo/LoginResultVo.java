package org.example.clouddemo.bean.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("登录结果")
@Data
public class LoginResultVo {

    @ApiModelProperty("token")
    private String token;


}
