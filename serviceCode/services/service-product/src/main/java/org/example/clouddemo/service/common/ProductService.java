package org.example.clouddemo.service.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.clouddemo.bean.common.entity.Product;
import org.example.clouddemo.bean.common.param.ProductListParam;
import com.baomidou.mybatisplus.extension.service.IService;
/**
* <p>
* 产品表 服务类
* </p>
*
* @author hzg
* @since 2025-06-07
*/

public interface ProductService extends IService<Product> {
    IPage selectPageVo(Page<Product> page, ProductListParam param);

    Product selectOne(Product product);

    int create(Product product);

    int update(Product product);

    int delete(String id);
}
