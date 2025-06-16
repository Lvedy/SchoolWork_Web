package org.example.clouddemo.bean.common.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@ApiModel("获取详情参数")
@Data
public class GetDetailParam {

    @ApiModelProperty("id")
    @NotNull(message = "不能为空")
    private Long id;

}
