package  org.example.clouddemo.bean.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* <p>
* 产品表
* </p>
*
* @author hzg
* @since 2025-06-07
*/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_product")
@ApiModel(value="Product对象", description="产品表")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId("id")
    private Long id;

    @ApiModelProperty(value = "产品名称")
    @TableField("product_name")
    private String productName;

    @ApiModelProperty(value = "图片")
    @TableField("product_img")
    private String productImg;

    @ApiModelProperty(value = "价格")
    @TableField("price")
    private BigDecimal price;

    @ApiModelProperty(value = "描述")
    @TableField("product_desc")
    private String productDesc;

    @ApiModelProperty(value = "排序")
    @TableField("sort_index")
    private Integer sortIndex;

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
