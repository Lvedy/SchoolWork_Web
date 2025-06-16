package org.example.common.bean;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author:hzg
 * @Date:Created in 13:45 2022/4/18
 * @Description:
 */
@ApiModel("通用的登录用户")
@Data
public class LoginUser implements Serializable {
    public static final String USER_TYPE_ADMIN = "admin";
    public static final String USER_TYPE_APP = "app";

    public static final String LOGIN_TYPE_PWD = "pwd";
    public static final String LOGIN_TYPE_CODE = "code";

    @ApiModelProperty("用户id")
    private Long id;

    @ApiModelProperty("用户名称")
    private String userName;

    @ApiModelProperty("用户类型: app :app用户 admin:后台用户 ")
    private String userType;

    @ApiModelProperty("登录方式: pwd :密码登录 code:验证码登录 wx:微信登录 ")
    private String loginType;


    @ApiModelProperty("唯一标识,用作存放在redis的标识")
    private String uuid;

}
