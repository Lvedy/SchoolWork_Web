package org.example.clouddemo.bean.common.param;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@ApiModel("创建产品参数")
@Data
public class CreateProductParam {

    @ApiModelProperty(value = "产品名称", required = true)
    @NotEmpty(message = "不能为空")
    private String productName;

    @ApiModelProperty(value = "图片", required = false)
    private String productImg;

    @ApiModelProperty(value = "价格", required = true)
    @NotNull(message = "不能为空")
    private BigDecimal price;

    @ApiModelProperty(value = "描述", required = true)
    @NotEmpty(message = "不能为空")
    private String productDesc;

    @ApiModelProperty(value = "排序")
    private Integer sortIndex = 0;

    @ApiModelProperty(value = "是否启用（1-是，0-否）")
    @TableField("enable_flag")
    private Boolean enableFlag = true;
}
