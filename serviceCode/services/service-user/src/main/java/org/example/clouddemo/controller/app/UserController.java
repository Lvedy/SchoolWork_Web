package org.example.clouddemo.controller.app;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.clouddemo.bean.common.param.LoginParam;
import org.example.clouddemo.bean.common.param.RegisterParam;
import org.example.clouddemo.bean.common.vo.LoginResultVo;
import org.example.clouddemo.bean.common.vo.UserAppVo;
import org.example.clouddemo.service.common.UserService;
import org.example.common.annotation.IgnoreAuth;
import org.example.common.auth.TokenService;
import org.example.common.bean.LoginUser;
import org.example.common.response.ApiResult;
import org.example.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 用户表 内部接口
 * </p>
 *
 * @author hzg
 * @since 2025-06-06
 */

@Api(tags = "用户接口")
@ApiSupport(order = 1)
@RestController("appUserController")
@RequestMapping("/api/app/user")
public class UserController {

    @Autowired
    public UserService userService;

    @Autowired
    private TokenService tokenService;

    @ApiOperation(value = "用户注册", notes = "不需要token")
    @ApiOperationSupport(order = 1)
    @PostMapping("register")
    @IgnoreAuth
    public ApiResult<Long> register(@Validated @RequestBody RegisterParam param) {
        return ApiResult.success(userService.register(param));
    }


    @ApiOperation(value = "用户登录", notes = "不需要token")
    @ApiOperationSupport(order = 2)
    @PostMapping("login")
    @IgnoreAuth
    public ApiResult<LoginResultVo> login(@Validated @RequestBody LoginParam param) {

        return ApiResult.success(userService.login(param));
    }


    @ApiOperation(value = "获取用户信息", notes = "需要token")
    @ApiOperationSupport(order = 3)
    @PostMapping("getInfo")
    public ApiResult<UserAppVo> getInfo() {

        LoginUser loginUser = SecurityUtils.getUserInfo();
        Long userId = loginUser.getId();

        //获取用户信息
        UserAppVo userAppVo = userService.getInfo(userId);

        return ApiResult.success(userAppVo);
    }


    @ApiOperation("退出登录")
    @ApiOperationSupport(order = 4)
    @PostMapping(value = "logout")
    public ApiResult logout() {
        LoginUser loginUser = SecurityUtils.getUserInfo();
        tokenService.delLoginUser(String.valueOf(loginUser.getId()), loginUser.getUuid(), LoginUser.USER_TYPE_APP);

        return ApiResult.success();
    }

}
