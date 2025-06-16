package org.example.clouddemo.service.common.mapper;

import org.example.clouddemo.bean.common.entity.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* <p>
* 产品表 Mapper 接口
* </p>
*
* @author hzg
* @since 2025-06-07
*/
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

}
