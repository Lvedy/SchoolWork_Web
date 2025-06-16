package org.example.clouddemo.feign;

import org.example.clouddemo.dto.OrderDTO;
import org.example.common.response.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * 订单服务Feign客户端
 * 用于远程调用订单微服务
 *
 * @author system
 * @since 2025-06-14
 */
@FeignClient(name = "service-order", path = "/api/app/order")
public interface OrderFeignClient {

    /**
     * 获取订单列表
     *
     * @param token 用户token
     * @return 订单列表
     */
    @PostMapping("/getOrderList")
    ApiResult<List<OrderDTO>> getOrderList(@RequestHeader("token") String token);
}