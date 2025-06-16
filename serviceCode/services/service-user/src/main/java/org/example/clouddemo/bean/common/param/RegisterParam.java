package org.example.clouddemo.bean.common.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@ApiModel("注册参数")
@Data
public class RegisterParam {

    @ApiModelProperty("帐号名")
    @NotEmpty(message = "不能为空")
    private String username;

    @ApiModelProperty("密码,md5后传输")
    @NotEmpty(message = "不能为空")
    private String password;

}
