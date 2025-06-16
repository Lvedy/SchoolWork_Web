package org.example.clouddemo.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.example.clouddemo.bean.common.entity.Product;
import org.example.clouddemo.dto.OrderDTO;
import org.example.clouddemo.dto.ProductListParamDTO;
import org.example.clouddemo.feign.OrderFeignClient;
import org.example.clouddemo.feign.ProductFeignClient;
import org.example.common.response.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 推荐微服务 内部接口
 * </p>
 *
 * @author hzg
 * @since 2025-06-07
 */

@Api(tags = "推荐接口")
@ApiSupport(order = 1)
@RestController("appRecommendController")
@RequestMapping("/api/app/recommend")
@Log4j2
public class RecommendController {

    @Autowired
    private OrderFeignClient orderFeignClient;
    
    @Autowired
    private ProductFeignClient productFeignClient;
    
    @Autowired
    private ObjectMapper objectMapper;

    @ApiOperation(value = "刷新推荐", notes = "需要token")
    @ApiOperationSupport(order = 1)
    @PostMapping("refresh")
    public ApiResult<List<Product>> refreshRecommend(@RequestHeader("token") String token) {
        try {
            log.info("开始刷新推荐，token: {}", token);
            
            // 1. 调用订单服务获取订单列表
            List<OrderDTO> orders = getOrderList(token);
            if (orders == null || orders.isEmpty()) {
                log.warn("未获取到订单数据，返回空推荐列表");
                return ApiResult.success(new ArrayList<>());
            }
            
            // 2. 计算订单单品价格的平均数和标准差
            List<BigDecimal> unitPrices = calculateUnitPrices(orders);
            if (unitPrices.isEmpty()) {
                log.warn("无法计算单品价格，返回空推荐列表");
                return ApiResult.success(new ArrayList<>());
            }
            
            BigDecimal mean = calculateMean(unitPrices);
            BigDecimal standardDeviation = calculateStandardDeviation(unitPrices, mean);
            
            log.info("价格统计 - 平均数: {}, 标准差: {}", mean, standardDeviation);
            
            // 3. 计算价格范围
            int minPrice = mean.subtract(standardDeviation).setScale(0, RoundingMode.DOWN).intValue();
            int maxPrice = mean.add(standardDeviation).setScale(0, RoundingMode.UP).intValue();
            
            // 确保价格范围合理
            minPrice = Math.max(0, minPrice);
            if (maxPrice <= minPrice) {
                maxPrice = minPrice + 100; // 设置最小价格范围
            }
            
            log.info("价格查询范围: {} - {}", minPrice, maxPrice);
            
            // 4. 调用产品服务查询产品
            List<Product> recommendedProducts = getProductsByPriceRange(minPrice, maxPrice, token);
            
            // 5. 随机选择最多4个产品
            List<Product> finalRecommendations = selectRandomProducts(recommendedProducts, 4);
            
            log.info("推荐完成，返回{}个产品", finalRecommendations.size());
            return ApiResult.success(finalRecommendations);
            
        } catch (Exception e) {
            log.error("刷新推荐失败", e);
            return ApiResult.error("推荐服务暂时不可用");
        }
    }
    
    /**
     * 调用订单服务获取订单列表
     */
    private List<OrderDTO> getOrderList(String token) {
        try {
            log.info("通过Feign调用订单服务获取订单列表，token: {}", token);
            
            ApiResult<List<OrderDTO>> result = orderFeignClient.getOrderList(token);
            
            if (result != null && result.isSuccess()) {
                log.info("成功获取订单列表，数量: {}", result.getData() != null ? result.getData().size() : 0);
                return result.getData();
            }
            
            log.warn("获取订单列表失败: {}", result != null ? result.getMsg() : "返回结果为空");
            return new ArrayList<>();
            
        } catch (Exception e) {
            log.error("调用订单服务失败", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 计算订单的单品价格
     */
    private List<BigDecimal> calculateUnitPrices(List<OrderDTO> orders) {
        return orders.stream()
            .filter(order -> order.getAmount() != null && order.getQuantity() != null && order.getQuantity() > 0)
            .map(order -> order.getAmount().divide(new BigDecimal(order.getQuantity()), 2, RoundingMode.HALF_UP))
            .collect(Collectors.toList());
    }
    
    /**
     * 计算平均数
     */
    private BigDecimal calculateMean(List<BigDecimal> prices) {
        BigDecimal sum = prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(new BigDecimal(prices.size()), 2, RoundingMode.HALF_UP);
    }
    
    /**
     * 计算标准差
     */
    private BigDecimal calculateStandardDeviation(List<BigDecimal> prices, BigDecimal mean) {
        BigDecimal variance = prices.stream()
            .map(price -> price.subtract(mean).pow(2))
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(new BigDecimal(prices.size()), 2, RoundingMode.HALF_UP);
        
        // 计算标准差（方差的平方根）
        double varianceDouble = variance.doubleValue();
        double stdDev = Math.sqrt(varianceDouble);
        return new BigDecimal(stdDev).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * 根据价格范围查询产品
     */
    private List<Product> getProductsByPriceRange(int minPrice, int maxPrice, String token) {
        try {
            // 构造查询参数，使用价格范围
            ProductListParamDTO queryParam = new ProductListParamDTO();
            queryParam.setStartPrice(new BigDecimal(minPrice));
            queryParam.setEndPrice(new BigDecimal(maxPrice));
            
            log.info("通过Feign调用产品服务查询产品，价格范围: {}-{}", minPrice, maxPrice);
            log.info("请求参数: {}", queryParam);
            
            String responseBody = productFeignClient.getProductList(1, 100, queryParam, token);
            
            log.info("产品查询响应内容: {}", responseBody);
            
            if (responseBody != null && !responseBody.isEmpty()) {
                try {
                    JsonNode rootNode = objectMapper.readTree(responseBody);
                    boolean success = rootNode.path("success").asBoolean();
                    log.info("产品查询结果 - success: {}", success);
                    
                    if (success) {
                        JsonNode dataNode = rootNode.path("data");
                        if (!dataNode.isMissingNode() && !dataNode.isNull()) {
                            JsonNode recordsNode = dataNode.path("records");
                            if (!recordsNode.isMissingNode() && recordsNode.isArray()) {
                                List<Product> products = objectMapper.convertValue(
                                    recordsNode, 
                                    new TypeReference<List<Product>>() {}
                                );
                                log.info("成功获取产品数量: {}", products.size());
                                if (!products.isEmpty()) {
                                    log.info("第一个产品信息: id={}, name={}, price={}", 
                                        products.get(0).getId(), 
                                        products.get(0).getProductName(), 
                                        products.get(0).getPrice());
                                }
                                return products;
                            } else {
                                log.warn("响应中没有找到records数组");
                            }
                        } else {
                            log.warn("响应中data字段为空或缺失");
                        }
                    } else {
                        String message = rootNode.path("msg").asText();
                        log.warn("产品查询失败 - message: {}", message);
                    }
                } catch (Exception e) {
                    log.error("解析产品查询响应失败", e);
                }
            } else {
                log.warn("产品查询返回空响应");
            }
            
            log.warn("产品查询返回空结果，价格范围：{}-{}", minPrice, maxPrice);
            return new ArrayList<>();
            
        } catch (Exception e) {
            log.error("根据价格范围查询产品失败，价格范围：{}-{}", minPrice, maxPrice, e);
            return new ArrayList<>();
        }
    }
    

    
    /**
     * 随机选择指定数量的产品
     */
    private List<Product> selectRandomProducts(List<Product> products, int maxCount) {
        if (products.isEmpty()) {
            return new ArrayList<>();
        }
        
        if (products.size() <= maxCount) {
            return new ArrayList<>(products);
        }
        
        // 随机打乱并选择前maxCount个
        List<Product> shuffled = new ArrayList<>(products);
        Collections.shuffle(shuffled);
        return shuffled.subList(0, maxCount);
    }
}
