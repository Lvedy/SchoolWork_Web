package org.example.clouddemo.service.common.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.clouddemo.bean.common.entity.Product;
import org.example.clouddemo.bean.common.param.ProductListParam;
import org.example.clouddemo.service.common.mapper.ProductMapper;
import org.example.clouddemo.service.common.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
* <p>
* 产品表 服务实现类
* </p>
*
* @author hzg
* @since 2025-06-07
*/
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public IPage<Product> selectPageVo(Page<Product> page, ProductListParam param) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        
        // 只对非空字段进行查询
        if (param.getId() != null) {
            queryWrapper.eq("id", param.getId());
        }
        if (param.getProductName() != null && !param.getProductName().trim().isEmpty()) {
            queryWrapper.like("product_name", param.getProductName());
        }
        if (param.getProductImg() != null && !param.getProductImg().trim().isEmpty()) {
            queryWrapper.eq("product_img", param.getProductImg());
        }
        
        // 价格范围查询：支持起始价格和终止价格
        if (param.getStartPrice() != null) {
            queryWrapper.ge("price", param.getStartPrice());
        }
        if (param.getEndPrice() != null) {
            queryWrapper.le("price", param.getEndPrice());
        }
        
        if (param.getProductDesc() != null && !param.getProductDesc().trim().isEmpty()) {
            queryWrapper.like("product_desc", param.getProductDesc());
        }
        if (param.getSortIndex() != null) {
            queryWrapper.eq("sort_index", param.getSortIndex());
        }
        if (param.getEnableFlag() != null) {
            queryWrapper.eq("enable_flag", param.getEnableFlag());
        }
        
        queryWrapper.orderByDesc("create_time");
        return productMapper.selectPage(page, queryWrapper);
    }


    @Override
    public Product selectOne(Product product) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>(product);
        return productMapper.selectOne(queryWrapper);
    }

    @Override
    public int create(Product product) {
        return productMapper.insert(product);
    }

    @Override
    public int update(Product product) {
        return productMapper.updateById(product);
    }

    @Override
    public int delete(String id) {
        return productMapper.deleteById(id);
    }
}
