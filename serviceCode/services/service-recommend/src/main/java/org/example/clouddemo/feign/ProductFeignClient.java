package org.example.clouddemo.feign;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.clouddemo.bean.common.entity.Product;
import org.example.clouddemo.dto.ProductListParamDTO;
import org.example.common.response.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 产品服务Feign客户端
 * 用于远程调用产品微服务
 *
 * @author system
 * @since 2025-06-14
 */
@FeignClient(name = "service-product", path = "/api/app/product")
public interface ProductFeignClient {

    /**
     * 分页查询产品列表
     *
     * @param current 当前页
     * @param size 每页大小
     * @param productListParamDTO 查询参数
     * @param token 用户token
     * @return 产品分页数据（返回String避免IPage反序列化问题）
     */
    @PostMapping("/list/{current}/{size}")
    String getProductList(
            @PathVariable("current") Integer current,
            @PathVariable("size") Integer size,
            @RequestBody ProductListParamDTO productListParamDTO,
            @RequestHeader("token") String token
    );
}