package org.example.common.utils;


import cn.hutool.core.util.StrUtil;
import org.example.common.bean.LoginUser;
import org.example.common.constants.RedisKey;
import org.example.common.exception.AppRunTimeException;
import org.example.common.response.ApiResultCode;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author:hzg 2
 * @Date:Created in 13:56 2022/4/18
 * @Description:
 */
public class SecurityUtils {
    public static final String TOKEN_HEADER = "token";
    public static final String INFO_ID_HEADER = "X-Info-Id";
    public static final String USER_TYPE_HEADER = "X-User-Type";

    /**
     * 获取用户信息
     **/
    public static Long getUserId() {

        try {
            LoginUser loginUser = getUserInfo();

            return loginUser.getId();
        } catch (Exception e) {
            throw new AppRunTimeException("获取用户信息失败");
        }
    }


    /**
     * 获取用户信息
     **/
    public static LoginUser getUserInfo() {

        try {
            HttpServletRequest request = getCurrentRequest();
//            String uuid = request.getHeader(INFO_ID_HEADER);
//            String type = request.getHeader(USER_TYPE_HEADER);


            String uuid = String.valueOf(request.getSession().getAttribute(INFO_ID_HEADER));
            String type = String.valueOf(request.getSession().getAttribute(USER_TYPE_HEADER));

            if (StrUtil.isEmpty(uuid)) {
                throw new AppRunTimeException(ApiResultCode.TOKEN_FAIL);
            }

            type = "admin".equals(type) ? "admin" : "app";

            LoginUser loginUser = RedisUtil.getObject(RedisKey.TOKEN(type, uuid), LoginUser.class);

            if (loginUser == null) {
                throw new AppRunTimeException(ApiResultCode.TOKEN_FAIL);
            }

            return loginUser;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppRunTimeException("获取用户信息失败");
        }
    }


    /**
     * 获取当前请求
     */
    public static HttpServletRequest getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes == null) {
            throw new AppRunTimeException("无法获取当前请求");
        }

        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

}
