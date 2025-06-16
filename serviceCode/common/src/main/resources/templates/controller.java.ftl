package ${package.Controller};

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import ${package.Entity}.${entity};
import ${package.Service}.${table.serviceName};

import org.example.common.response.ApiResult;
import org.example.common.response.ApiResultCode;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

<#if restControllerStyle>
import org.springframework.web.bind.annotation.RestController;
<#else>
import org.springframework.stereotype.Controller;
</#if>
<#if superControllerClassPackage??>
import ${superControllerClassPackage};
</#if>


/**
* <p>
* ${table.comment!} 内部接口
* </p>
*
* @author ${author}
* @since ${date}
*/
<#if restControllerStyle>
@RestController
<#else>
@Controller
</#if>
@RequestMapping("<#if package.ModuleName??>/${package.ModuleName}</#if><#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>")
<#if kotlin>
    class ${table.controllerName}<#if superControllerClass??> : ${superControllerClass}()</#if>
<#else>
    <#if superControllerClass??>
public class ${table.controllerName} extends ${superControllerClass} {
    <#else>
public class ${table.controllerName} {
    </#if>

    @Autowired
    public ${table.serviceName} ${table.entityPath}Service;

    @PostMapping("list/{page}/{pageSize}")
    public ApiResult list(@PathVariable("page") int page ,@PathVariable("pageSize") int pageSize, @RequestBody ${entity} ${table.entityPath}){
        Page pageInfo = new Page<>();
        pageInfo.setCurrent(page);
        pageInfo.setSize(pageSize);
        IPage result = ${table.entityPath}Service.selectPageVo(pageInfo, ${table.entityPath});
        return ApiResult.success(result);
    }

    @PostMapping("queryOne")
    public ApiResult queryOne(@RequestBody ${entity}  ${table.entityPath}){
        return ApiResult.success(${table.entityPath}Service.selectOne(${table.entityPath}));
    }


    @PostMapping("delete/{id}")
    public ApiResult delete(@PathVariable("id") String id){
        return ApiResult.success(${table.entityPath}Service.delete(id));
    }

    @PostMapping("update")
    public ApiResult update(@RequestBody ${entity} ${table.entityPath}) {
        if(StringUtils.isEmpty(${table.entityPath}.getId())){
            return ApiResult.res(ApiResultCode.PARAMS_NOT_EXIST);
        }
        return ApiResult.success(${table.entityPath}Service.update(${table.entityPath}));
    }

    @PostMapping("create")
    public ApiResult create(@RequestBody ${entity} ${table.entityPath}){
        return ApiResult.success(${table.entityPath}Service.create(${table.entityPath}));
    }
}
</#if>