package org.example.clouddemo.bean.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@ApiModel("订单实体")
@Data
@TableName("orders")
public class Order {

    @ApiModelProperty("订单id")
    @TableId(value = "order_id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty("产品id")
    @TableField("product_id")
    private Long productId;

    @ApiModelProperty("用户名称")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty("产品名称")
    @TableField("product_name")
    private String productName;

    @ApiModelProperty("产品数量")
    @TableField("product_quantity")
    private Integer quantity;

    @ApiModelProperty("订单金额")
    @TableField("order_price")
    private BigDecimal amount;

    @ApiModelProperty("订单状态：0-未完成，1-已完成")
    @TableField("is_completed")
    private Integer status;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @TableField("update_time")
    private Date updateTime;

}