package org.example.clouddemo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 产品列表查询参数DTO
 * 用于推荐服务调用产品服务时传递查询参数
 *
 * @author system
 * @since 2025-06-14
 */
@ApiModel("产品列表查询参数DTO")
@Data
public class ProductListParamDTO {

    @ApiModelProperty("产品id")
    private Long id;

    @ApiModelProperty("产品名称")
    private String productName;

    @ApiModelProperty("产品图片")
    private String productImg;

    @ApiModelProperty("起始价格")
    private BigDecimal startPrice;

    @ApiModelProperty("终止价格")
    private BigDecimal endPrice;

    @ApiModelProperty("产品描述")
    private String productDesc;

    @ApiModelProperty("排序索引")
    private Integer sortIndex;

    @ApiModelProperty("启用标志：0-禁用，1-启用")
    private Integer enableFlag;
}