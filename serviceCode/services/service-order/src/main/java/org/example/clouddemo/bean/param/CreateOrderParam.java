package org.example.clouddemo.bean.param;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel("创建订单参数")
@Data
public class CreateOrderParam {

    @ApiModelProperty("产品id")
    @NotNull(message = "不能为空")
    private Long productId;

    @ApiModelProperty("用户id")
    @JsonIgnore
    private Long userId;

    @ApiModelProperty("下单数量")
    @NotNull(message = "下单数量不能为空")
    private Integer quantity;

}