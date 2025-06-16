package ${package.Service};

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ${package.Entity}.${entity};
import ${superServiceClassPackage};
/**
* <p>
* ${table.comment!} 服务类
* </p>
*
* @author ${author}
* @since ${date}
*/

<#if kotlin>
    interface ${table.serviceName} : ${superServiceClass}<${entity}>
<#else>
public interface ${table.serviceName} extends ${superServiceClass}<${entity}> {
    IPage selectPageVo(Page<${entity}> page,${entity} ${table.entityPath});

    ${entity} selectOne(${entity} ${table.entityPath});

    int create(${entity} ${table.entityPath});

    int update(${entity} ${table.entityPath});

    int delete(String id);
}
</#if>
