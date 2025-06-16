package org.example.clouddemo.bean.common.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@ApiModel("创建产品参数")
@Data
public class UpdateProductParam {

    @ApiModelProperty(value = "id", required = true)
    @NotNull(message = "不能为空")
    private Long id;

    @ApiModelProperty(value = "产品名称", required = false)
    private String productName;

    @ApiModelProperty(value = "图片", required = false)
    private String productImg;

    @ApiModelProperty(value = "价格", required = false)

    private BigDecimal price;

    @ApiModelProperty(value = "描述", required = false)
    private String productDesc;

    @ApiModelProperty(value = "排序", required = false)
    private Integer sortIndex;

    @ApiModelProperty(value = "是否启用（1-是，0-否）", required = false)
    private Boolean enableFlag;
}
