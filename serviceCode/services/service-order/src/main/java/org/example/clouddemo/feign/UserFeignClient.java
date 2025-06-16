package org.example.clouddemo.feign;

import org.example.clouddemo.bean.common.vo.UserAppVo;
import org.example.common.response.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "service-user", path = "/api/app/user")
public interface UserFeignClient {

    @PostMapping("getInfo")
    ApiResult<UserAppVo> getInfo(@RequestHeader("token") String token);

}