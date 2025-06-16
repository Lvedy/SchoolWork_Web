package org.example.clouddemo.feign;

import org.example.clouddemo.bean.common.entity.Product;
import org.example.common.response.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "service-product", path = "/api/app/product")
public interface ProductFeignClient {

    @PostMapping("queryOne")
    ApiResult<Product> getProductById(@RequestBody Product param);

    @PostMapping("queryOne")
    Product getProductById2(@RequestBody Product param);
}