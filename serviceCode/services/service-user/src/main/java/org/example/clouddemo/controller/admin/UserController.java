package org.example.clouddemo.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.clouddemo.bean.common.entity.User;
import org.example.clouddemo.service.common.UserService;
import org.example.common.response.ApiResult;
import org.example.common.response.ApiResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 用户表 内部接口
 * </p>
 *
 * @author hzg
 * @since 2025-06-06
 */
@Api(tags = "管理员接口")
@ApiSupport(order = 2)
@RestController
@RequestMapping("/api/admin/user")
public class UserController {

    @Autowired
    public UserService userService;

    @ApiOperation(value = "用户列表")
    @ApiOperationSupport(order = 1)
    @PostMapping("list/{page}/{pageSize}")
    public ApiResult list(@PathVariable("page") int page, @PathVariable("pageSize") int pageSize, @RequestBody User user) {
        Page pageInfo = new Page<>();
        pageInfo.setCurrent(page);
        pageInfo.setSize(pageSize);
        IPage result = userService.selectPageVo(pageInfo, user);
        return ApiResult.success(result);
    }

    @ApiOperation(value = "用户查询")
    @ApiOperationSupport(order = 2)
    @PostMapping("queryOne")
    public ApiResult queryOne(@RequestBody User user) {
        return ApiResult.success(userService.selectOne(user));
    }

    @ApiOperation(value = "用户注销")
    @ApiOperationSupport(order = 3)
    @PostMapping("delete/{id}")
    public ApiResult delete(@PathVariable("id") String id) {
        return ApiResult.success(userService.delete(id));
    }

    @ApiOperation(value = "用户信息更新")
    @ApiOperationSupport(order = 4)
    @PostMapping("update")
    public ApiResult update(@RequestBody User user) {
        if (StringUtils.isEmpty(user.getId())) {
            return ApiResult.res(ApiResultCode.PARAMS_NOT_EXIST);
        }
        return ApiResult.success(userService.update(user));
    }

}
