package org.example.clouddemo.controller.app;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.example.clouddemo.bean.common.entity.Product;
import org.example.clouddemo.bean.common.param.CreateProductParam;
import org.example.clouddemo.bean.common.param.GetDetailParam;
import org.example.clouddemo.bean.common.param.ProductListParam;
import org.example.clouddemo.bean.common.param.UpdateProductParam;
import org.example.clouddemo.service.common.ProductService;
import org.example.common.annotation.IgnoreAuth;
import org.example.common.response.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 产品表 内部接口
 * </p>
 *
 * @author hzg
 * @since 2025-06-07
 */

@Api(tags = "产品接口")
@ApiSupport(order = 1)
@RestController("appProductController")
@RequestMapping("/api/app/product")
@Log4j2
public class ProductController {

    @Autowired
    public ProductService productService;

    @ApiOperation(value = "产品列表", notes = "需要token")
    @ApiOperationSupport(order = 1)
    @PostMapping("list/{pageNum}/{pageSize}")
    public ApiResult<IPage<Product>> list(@PathVariable("pageNum") Integer pageNum,
                                          @PathVariable("pageSize") Integer pageSize,
                                          @RequestBody ProductListParam param) {
        log.info("接收到产品列表查询请求 - pageNum: {}, pageSize: {}", pageNum, pageSize);
        log.info("查询参数: {}", param);
        log.info("价格范围: startPrice={}, endPrice={}", param.getStartPrice(), param.getEndPrice());
        
        Page<Product> page = new Page<>(pageNum, pageSize);
        IPage<Product> pageVo = productService.selectPageVo(page, param);
        
        log.info("查询结果: 总记录数={}, 当前页记录数={}", pageVo.getTotal(), pageVo.getRecords().size());
        if (!pageVo.getRecords().isEmpty()) {
            log.info("第一个产品: id={}, name={}, price={}", 
                pageVo.getRecords().get(0).getId(),
                pageVo.getRecords().get(0).getProductName(),
                pageVo.getRecords().get(0).getPrice());
        }
        
        return ApiResult.success(pageVo);
    }

    @ApiOperation(value = "产品详情", notes = "不需要token")
    @ApiOperationSupport(order = 2)
    @PostMapping("queryOne")
    @IgnoreAuth
    public ApiResult queryOne(@Validated @RequestBody GetDetailParam param) {
        Product product = new Product();
        product.setId(param.getId());

        return ApiResult.success(productService.selectOne(product));
    }

    @ApiOperation(value = "创建产品", notes = "需要token")
    @ApiOperationSupport(order = 3)
    @PostMapping("create")
    public ApiResult create(@Validated @RequestBody CreateProductParam param) {
        Product product = BeanUtil.copyProperties(param, Product.class);
        return ApiResult.success(productService.create(product));
    }

    @ApiOperation(value = "删除产品", notes = "需要token")
    @ApiOperationSupport(order = 3)
    @PostMapping("delete/{id}")
    public ApiResult delete(@PathVariable("id") String id) {
        return ApiResult.success(productService.delete(id));
    }

    @ApiOperation(value = "更新产品", notes = "需要token")
    @ApiOperationSupport(order = 4)
    @PostMapping("update")
    public ApiResult update(@Validated @RequestBody UpdateProductParam param) {
        Product product = BeanUtil.copyProperties(param, Product.class);
        return ApiResult.success(productService.update(product));
    }


}
