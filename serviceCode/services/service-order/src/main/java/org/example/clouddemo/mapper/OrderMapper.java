package org.example.clouddemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.clouddemo.bean.entity.Order;

/**
 * <p>
 * 订单 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2025-06-13
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}