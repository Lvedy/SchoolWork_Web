package ${package.ServiceImpl};

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import ${package.Service}.${table.serviceName};
import ${superServiceImplClassPackage};
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
* <p>
* ${table.comment!} 服务实现类
* </p>
*
* @author ${author}
* @since ${date}
*/
@Service
<#if kotlin>
    open class ${table.serviceImplName} : ${superServiceImplClass}<${table.mapperName}, ${entity}>(), ${table.serviceName} {

    }
<#else>
public class ${table.serviceImplName} extends ${superServiceImplClass}<${table.mapperName}, ${entity}> implements ${table.serviceName} {

    @Autowired
    private ${table.mapperName} ${table.entityPath}Mapper;

    @Override
    public IPage<${entity}> selectPageVo(Page<${entity}> page, ${entity} ${table.entityPath}) {
        QueryWrapper<${entity}> queryWrapper = new QueryWrapper<>(${table.entityPath});
        queryWrapper.orderByDesc("create_time");
        return ${table.entityPath}Mapper.selectPage(page,queryWrapper);
    }


    @Override
    public ${entity} selectOne(${entity} ${table.entityPath}) {
        QueryWrapper<${entity}> queryWrapper = new QueryWrapper<>(${table.entityPath});
        return ${table.entityPath}Mapper.selectOne(queryWrapper);
    }

    @Override
    public int create(${entity} ${table.entityPath}) {
        return ${table.entityPath}Mapper.insert(${table.entityPath});
    }

    @Override
    public int update(${entity} ${table.entityPath}) {
        return ${table.entityPath}Mapper.updateById(${table.entityPath});
    }

    @Override
    public int delete(String id) {
        return ${table.entityPath}Mapper.deleteById(id);
    }
}
</#if>
