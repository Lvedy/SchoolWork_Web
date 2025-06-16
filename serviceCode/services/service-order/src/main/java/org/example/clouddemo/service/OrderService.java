package org.example.clouddemo.service;

import org.example.clouddemo.bean.entity.Order;
import org.example.clouddemo.bean.param.CompleteOrderParam;
import org.example.clouddemo.bean.param.CreateOrderParam;
import org.example.clouddemo.bean.param.DeleteOrderParam;
import org.example.common.response.ApiResult;

import java.util.List;

/**
 * <p>
 * 订单服务接口
 * </p>
 *
 * @author system
 * @since 2025-06-13
 */
public interface OrderService {

    /**
     * 创建订单
     *
     * @param param 创建订单参数
     * @return 订单ID
     */
    ApiResult<Long> createOrder(CreateOrderParam param, String token);

    /**
     * 删除订单
     *
     * @param param 删除订单参数
     * @param token 用户token
     * @return 操作结果
     */
    ApiResult<Boolean> deleteOrder(DeleteOrderParam param, String token);

    /**
     * 完成订单
     *
     * @param param 完成订单参数
     * @param token 用户token
     * @return 操作结果
     */
    ApiResult<Boolean> completeOrder(CompleteOrderParam param, String token);

    /**
     * 查看用户订单列表
     *
     * @param token 用户token
     * @return 订单列表
     */
    ApiResult<List<Order>> getOrderList(String token);

}