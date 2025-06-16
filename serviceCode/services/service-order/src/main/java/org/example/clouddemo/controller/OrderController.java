package org.example.clouddemo.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.example.clouddemo.bean.common.entity.Product;
import org.example.clouddemo.bean.entity.Order;
import org.example.clouddemo.bean.param.CompleteOrderParam;
import org.example.clouddemo.bean.param.CreateOrderParam;
import org.example.clouddemo.bean.param.DeleteOrderParam;
import org.example.clouddemo.feign.ProductFeignClient;
import org.example.clouddemo.service.OrderService;
import org.example.common.exception.AppRunTimeException;
import org.example.common.response.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 订单接口
 * </p>
 *
 * @author system
 * @since 2025-06-13
 */

@Api(tags = "订单接口")
@ApiSupport(order = 1)
@RestController
@RequestMapping("/api/app/order")
@Log4j2
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductFeignClient productFeignClient;

    @ApiOperation(value = "创建订单", notes = "返回结果是订单id")
    @ApiOperationSupport(order = 1)
    @PostMapping("createOrder")
    public ApiResult<Long> createOrder(@Validated @RequestBody CreateOrderParam param,
                                       @RequestHeader("token") String token) {
        log.info("创建订单请求参数：{}, token: {}", param, token);
        return orderService.createOrder(param, token);
    }

    @ApiOperation(value = "删除订单", notes = "返回操作结果")
    @ApiOperationSupport(order = 2)
    @PostMapping("deleteOrder")
    public ApiResult<Boolean> deleteOrder(@Validated @RequestBody DeleteOrderParam param,
                                          @RequestHeader("token") String token) {
        log.info("删除订单请求参数：{}, token: {}", param, token);
        return orderService.deleteOrder(param, token);
    }

    @ApiOperation(value = "完成订单", notes = "返回操作结果")
    @ApiOperationSupport(order = 3)
    @PostMapping("completeOrder")
    public ApiResult<Boolean> completeOrder(@Validated @RequestBody CompleteOrderParam param,
                                            @RequestHeader("token") String token) {
        log.info("完成订单请求参数：{}, token: {}", param, token);
        return orderService.completeOrder(param, token);
    }

    @ApiOperation(value = "查看订单列表", notes = "返回用户的所有订单")
    @ApiOperationSupport(order = 4)
    @PostMapping("getOrderList")
    public ApiResult<List<Order>> getOrderList(@RequestHeader("token") String token) {
        log.info("查看订单列表，token: {}", token);
        return orderService.getOrderList(token);
    }
}