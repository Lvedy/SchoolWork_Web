package org.example.clouddemo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单数据传输对象
 * 用于接收订单服务返回的订单数据
 *
 * @author system
 * @since 2025-06-14
 */
@ApiModel("订单DTO")
@Data
public class OrderDTO {

    @ApiModelProperty("订单id")
    private Long id;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("产品id")
    private Long productId;

    @ApiModelProperty("用户名称")
    private String userName;

    @ApiModelProperty("产品名称")
    private String productName;

    @ApiModelProperty("产品数量")
    private Integer quantity;

    @ApiModelProperty("订单金额")
    private BigDecimal amount;

    @ApiModelProperty("订单状态：0-未完成，1-已完成")
    private Integer status;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}