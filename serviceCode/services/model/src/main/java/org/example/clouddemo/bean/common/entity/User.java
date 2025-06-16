package org.example.clouddemo.bean.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author hzg
 * @since 2025-06-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_user")
@ApiModel(value = "User对象", description = "用户表")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId("id")
    private Long id;

    @ApiModelProperty(value = "帐号（自动生成，9位数字）")
    @TableField("username")
    private String username;

    @ApiModelProperty(value = "密码（加密存储）")
    @TableField("password")
    private String password;

    @ApiModelProperty(value = "昵称（可修改）")
    @TableField("nickname")
    private String nickname;

    @ApiModelProperty(value = "头像（可修改）")
    @TableField("avatar_url")
    private String avatarUrl;


    @ApiModelProperty(value = "是否启用（1-是，0-否）")
    @TableField("enable_flag")
    private Boolean enableFlag;


    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;


}
