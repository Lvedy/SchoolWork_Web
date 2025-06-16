package org.example.clouddemo.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.log4j.Log4j2;
import org.example.clouddemo.bean.common.entity.Product;
import org.example.clouddemo.bean.common.vo.UserAppVo;
import org.example.clouddemo.bean.entity.Order;
import org.example.clouddemo.bean.param.CompleteOrderParam;
import org.example.clouddemo.bean.param.CreateOrderParam;
import org.example.clouddemo.bean.param.DeleteOrderParam;
import org.example.clouddemo.feign.ProductFeignClient;
import org.example.clouddemo.feign.UserFeignClient;
import org.example.clouddemo.mapper.OrderMapper;
import org.example.clouddemo.service.OrderService;
import org.example.common.response.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 订单服务实现类
 * </p>
 *
 * @author system
 * @since 2025-06-13
 */
@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserFeignClient userFeignClient;
    
    @Autowired
    private ProductFeignClient productFeignClient;
    
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public ApiResult<Long> createOrder(CreateOrderParam param, String token) {
        log.info("创建订单，参数：{}, token: {}", param, token);
        
        try {
            // 1. 通过token获取用户信息
        ApiResult<UserAppVo> userResult = userFeignClient.getInfo(token);
            if (!userResult.isSuccess() || userResult.getData() == null) {
                log.error("获取用户信息失败: {}", userResult.getMsg());
                return ApiResult.error("获取用户信息失败");
            }
            UserAppVo user = userResult.getData();
            log.info("获取到用户信息: {}", user.getUsername());
            
            // 2. 通过产品ID获取产品详情
            Product productParam = new Product();
            productParam.setId(param.getProductId());
            ApiResult<Product> productResult = productFeignClient.getProductById(productParam);
            if (!productResult.isSuccess() || productResult.getData() == null) {
                log.error("获取产品信息失败: {}", productResult.getMsg());
                return ApiResult.error("获取产品信息失败");
            }
            Product product = productResult.getData();
            log.info("获取到产品信息: {}, 价格: {}", product.getProductName(), product.getPrice());
            
            // 3. 计算订单金额
            BigDecimal orderAmount = product.getPrice().multiply(new BigDecimal(param.getQuantity()));
            
            // 4. 创建订单对象
            Order order = new Order();
            order.setUserId(user.getId());
            order.setProductId(product.getId());
            order.setUserName(user.getUsername());
            order.setProductName(product.getProductName());
            order.setQuantity(param.getQuantity());
            order.setAmount(orderAmount);
            order.setStatus(0); // 0-未完成
            order.setCreateTime(new Date());
            order.setUpdateTime(new Date());
            
            // 5. 保存订单到数据库
            int result = orderMapper.insert(order);
            if (result > 0) {
                log.info("订单创建成功，订单ID: {}", order.getId());
                return ApiResult.success(order.getId());
            } else {
                log.error("订单保存失败");
                return ApiResult.error("订单创建失败");
            }
            
        } catch (Exception e) {
            log.error("创建订单异常: ", e);
            return ApiResult.error("创建订单异常: " + e.getMessage());
        }
    }

    @Override
    public ApiResult<Boolean> deleteOrder(DeleteOrderParam param, String token) {
        try {
            log.info("删除订单，参数：{}, token: {}", param, token);
            
            // 1. 通过token获取用户信息
            ApiResult<UserAppVo> userResult = userFeignClient.getInfo(token);
            if (!userResult.isSuccess() || userResult.getData() == null) {
                log.error("获取用户信息失败: {}", userResult.getMsg());
                return ApiResult.error("获取用户信息失败");
            }
            
            UserAppVo user = userResult.getData();
            Long userId = user.getId();
            Long orderId = param.getOrderId();
            log.info("用户ID: {}, 订单ID: {}", userId, orderId);
            
            // 2. 查询订单是否存在且属于当前用户
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("order_id", orderId);
            queryWrapper.eq("user_id", userId);
            
            Order existingOrder = orderMapper.selectOne(queryWrapper);
            if (existingOrder == null) {
                log.warn("订单不存在或无权限删除，用户ID: {}, 订单ID: {}", userId, orderId);
                return ApiResult.error("订单不存在或无权限删除");
            }
            
            // 3. 删除订单
            int result = orderMapper.delete(queryWrapper);
            if (result > 0) {
                log.info("订单删除成功，用户ID: {}, 订单ID: {}", userId, orderId);
                return ApiResult.success(true);
            } else {
                log.error("订单删除失败，用户ID: {}, 订单ID: {}", userId, orderId);
                return ApiResult.error("订单删除失败");
            }
            
        } catch (Exception e) {
            log.error("删除订单异常: ", e);
            return ApiResult.error("删除订单异常: " + e.getMessage());
        }
    }

    @Override
    public ApiResult<Boolean> completeOrder(CompleteOrderParam param, String token) {
        try {
            log.info("完成订单，参数：{}, token: {}", param, token);
            
            // 1. 通过token获取用户信息
            ApiResult<UserAppVo> userResult = userFeignClient.getInfo(token);
            if (!userResult.isSuccess() || userResult.getData() == null) {
                log.error("获取用户信息失败: {}", userResult.getMsg());
                return ApiResult.error("获取用户信息失败");
            }
            
            UserAppVo user = userResult.getData();
            Long userId = user.getId();
            Long orderId = param.getOrderId();
            log.info("用户ID: {}, 订单ID: {}", userId, orderId);
            
            // 2. 查询订单是否存在且属于当前用户
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("order_id", orderId);
            queryWrapper.eq("user_id", userId);
            
            Order existingOrder = orderMapper.selectOne(queryWrapper);
            if (existingOrder == null) {
                log.warn("订单不存在或无权限操作，用户ID: {}, 订单ID: {}", userId, orderId);
                return ApiResult.error("订单不存在或无权限操作");
            }
            
            // 3. 检查订单状态
            if (existingOrder.getStatus() == 1) {
                log.info("订单已经是完成状态，用户ID: {}, 订单ID: {}", userId, orderId);
                return ApiResult.success(true);
            }
            
            // 4. 更新订单状态为已完成
            Order updateOrder = new Order();
            updateOrder.setStatus(1); // 1表示已完成
            updateOrder.setUpdateTime(new Date());
            
            QueryWrapper<Order> updateWrapper = new QueryWrapper<>();
            updateWrapper.eq("order_id", orderId);
            updateWrapper.eq("user_id", userId);
            
            int result = orderMapper.update(updateOrder, updateWrapper);
            if (result > 0) {
                log.info("订单完成成功，用户ID: {}, 订单ID: {}", userId, orderId);
                return ApiResult.success(true);
            } else {
                log.error("订单完成失败，用户ID: {}, 订单ID: {}", userId, orderId);
                return ApiResult.error("订单完成失败");
            }
            
        } catch (Exception e) {
            log.error("完成订单异常: ", e);
            return ApiResult.error("完成订单异常: " + e.getMessage());
        }
    }

    @Override
    public ApiResult<List<Order>> getOrderList(String token) {
        try {
            log.info("查看订单列表，token: {}", token);
            
            // 1. 通过token获取用户信息
            ApiResult<UserAppVo> userResult = userFeignClient.getInfo(token);
            if (!userResult.isSuccess() || userResult.getData() == null) {
                log.error("获取用户信息失败: {}", userResult.getMsg());
                return ApiResult.error("获取用户信息失败");
            }
            
            UserAppVo user = userResult.getData();
            Long userId = user.getId();
            log.info("获取到用户ID: {}", userId);
            
            // 2. 根据用户ID查询订单列表
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            queryWrapper.orderByDesc("create_time"); // 按创建时间倒序排列
            
            List<Order> orderList = orderMapper.selectList(queryWrapper);
            log.info("查询到用户{}的订单数量: {}", userId, orderList.size());
            
            return ApiResult.success(orderList);
            
        } catch (Exception e) {
            log.error("查看订单列表异常: ", e);
            return ApiResult.error("查看订单列表异常: " + e.getMessage());
        }
    }
}