package org.example.clouddemo.config;


import cn.hutool.core.util.StrUtil;
import lombok.extern.log4j.Log4j2;
import org.example.common.auth.TokenService;
import org.example.common.bean.LoginUser;
import org.example.common.config.IgnoreAuthConfig;
import org.example.common.exception.AppRunTimeException;
import org.example.common.response.ApiResultCode;
import org.example.common.utils.AntPathMatcherUtil;
import org.example.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 拦截器 token验证
 */
@Component
@Log4j2
public class TokenInterceptor implements HandlerInterceptor {


    @Autowired
    private IgnoreAuthConfig ignoreAuthConfig;


    @Autowired
    private TokenService tokenService;

    private List<String> ignoreUrls;


    public List<String> getIgnoreUrls() {
        if (ignoreUrls == null) {
            ignoreUrls = ignoreAuthConfig.getAllUrl();
            log.info("ignoreUrls={}", ignoreUrls);
        }

        return ignoreUrls;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {


        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        boolean isAppUrl = requestURI.startsWith("/api/app/");
        String uuid = request.getHeader(SecurityUtils.INFO_ID_HEADER);
        String token = request.getHeader(SecurityUtils.TOKEN_HEADER);
        String type = isAppUrl ? "app" : "admin";
        log.info("method:{},请求url:{},uuid:{},jwt-token:{}", method, requestURI, uuid, token);

        //检查是否 是放行的路径
        List<String> ignoreUrls = getIgnoreUrls();
        // 使用 Ant 风格匹配替换简单的 contains
        boolean isIgnored = ignoreUrls.stream()
                .anyMatch(pattern -> AntPathMatcherUtil.matchPath(pattern, requestURI));

        if (isIgnored) {
            return true;
        }


        //校验token
    LoginUser loginUser = tokenService.getLoginUser(request, type);
    if (loginUser == null) {

        throw new AppRunTimeException(ApiResultCode.TOKEN_FAIL);
    }

    // 如果请求头中没有uuid，使用从token中解析出的uuid
    if (StrUtil.isEmpty(uuid)) {
        uuid = loginUser.getUuid();
    }

    //在Session  添加 字段
    request.getSession().setAttribute(SecurityUtils.INFO_ID_HEADER, uuid);
    request.getSession().setAttribute(SecurityUtils.USER_TYPE_HEADER, type);

        //如果没过期 则 刷新令牌有效期
        tokenService.verifyToken(loginUser);

        // Token验证通过，继续执行请求
        return true;
    }


}