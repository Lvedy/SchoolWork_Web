package org.example.clouddemo.bean.common.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.example.clouddemo.bean.common.entity.User;

@ApiModel("用户信息")
@Data
public class UserAppVo extends User {

    //可以加其它的组装字段


    //隐藏字段
    @ApiModelProperty(value = "密码（加密存储）")
    @JsonIgnore
    private String password;


}