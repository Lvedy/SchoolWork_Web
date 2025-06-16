package org.example.common.auth;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.example.common.bean.LoginUser;
import org.example.common.constants.RedisKey;
import org.example.common.exception.AppRunTimeException;
import org.example.common.response.ApiResultCode;
import org.example.common.utils.RedisUtil;
import org.example.common.utils.SecurityUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * redis  token  服务
 *
 * @Author:hzg 2
 * @Date:Created in 14:01 2022/4/18
 * @Description:
 */
@Component
@Slf4j
public class TokenService {
    // 令牌自定义标识
    private String header = "token";

    // 令牌秘钥
    private String secret = "xxx";

    // app令牌有效期（7天）
    private long expireTime = SECONDS_HOUR * 24 * 7;

    // admin令牌有效期（1天）
    private long expireTimeAdmin = SECONDS_HOUR * 24 * 1;


    protected static final long SECONDS_MINUTE = 60;
    protected static final long SECONDS_HOUR = SECONDS_MINUTE * 60;

    //如果token有前缀
    private String TOKEN_PREFIX = "Bearer ";

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser(HttpServletRequest request, String type) {
        // 获取请求携带的令牌
        String token = getToken(request);

        if (StrUtil.isNotEmpty(token)) {
            String userKey = getTokenKey(token, type);

            LoginUser user = JSON.parseObject(RedisUtil.get(userKey), LoginUser.class);
            return user;
        }
        return null;
    }


    /**
     * 删除用户身份信息
     */
    public void delLoginUser(String userId, String token, String type) {
        if (StrUtil.isNotEmpty(token)) {
            String userKey = getTokenKey(token, type);
            RedisUtil.del(userKey);
        }

        if (StrUtil.isNotEmpty(userId)) {
            String key = getUserTokenListKey(userId, type);
            RedisUtil.listRemoveItem(key, token);
        }
    }

    /**
     * 删除用户的所有token
     */
    public void clearUserToken(String userId, String type) {

        String key = getUserTokenListKey(userId, type);
        boolean isOk = true;
        while (isOk) {
            String token = RedisUtil.rightPop(key);
            if (StrUtil.isNotEmpty(token)) {
                String userKey = getTokenKey(token, type);
                RedisUtil.del(userKey);
            } else {
                isOk = false;
            }
        }

    }

    /**
     * 创建令牌
     *
     * @param loginUser 用户信息
     * @return 令牌
     */
    public String createToken(LoginUser loginUser) {
        String key = getUserTokenListKey(String.valueOf(loginUser.getId()), loginUser.getUserType());

        String token = "";

        if (LoginUser.USER_TYPE_ADMIN.equals(loginUser.getUserType())) {
            //admin 登录,每次登录创建新的token

        } else {
            //app登录,使用旧token
        }

        boolean needFresh = false;

        //没有token,生成新的token
        if (StrUtil.isEmpty(token)) {
            token = IdUtil.fastUUID();
            //加入用户token列表
            RedisUtil.leftPush(key, token);

            needFresh = true;

        }

        loginUser.setUuid(token);
        refreshToken(loginUser);


        if (needFresh) {
            //检测用户token列表,移除过期的token
            refreshUserTokenList(key, loginUser.getUserType());
        }
        return token;
    }

    /**
     * 刷新用户可用的token列表,如果token过期就删除
     *
     * @param key      token列表键
     * @param userType 用户类型
     */
    public void refreshUserTokenList(String key, String userType) {
        long size = RedisUtil.listSize(key);


        int index = 0;
        boolean finish = 0 >= size;

        while (!finish) {
            String token = RedisUtil.listGetItem(key, index);
            String tokenKey = getTokenKey(token, userType);

            //检测token是否已过期
            boolean has = RedisUtil.hasKey(tokenKey);
            if (!has) {
                //删除
                RedisUtil.listRemoveItem(key, token);
            } else {
                index++;
            }

            finish = index >= RedisUtil.listSize(key);
        }

    }


    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     *
     * @param loginUser 登录用户
     * @return 令牌
     */
    public void verifyToken(LoginUser loginUser) {
        refreshToken(loginUser);
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUser loginUser) {
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getUuid(), loginUser.getUserType());
        RedisUtil.set(userKey, JSON.toJSONString(loginUser), getExpireTime(loginUser.getUserType()));

    }

    public long getExpireTime(String userType) {
        if (LoginUser.USER_TYPE_ADMIN.equals(userType)) {
            return expireTimeAdmin;
        }
        return expireTime;
    }


    /**
     * 获取请求token
     *
     * @param request
     * @return token
     */
    public String getToken(HttpServletRequest request) {


        //header 中 有 X-Info-Id 字段
        String uuid = request.getHeader(SecurityUtils.INFO_ID_HEADER);
        if (StrUtil.isNotEmpty(uuid)) {
            log.info("从{}获取uuid={}", SecurityUtils.INFO_ID_HEADER, uuid);
            return uuid;
        }


        //header 有 token
        String token = request.getHeader(header);
        if (StrUtil.isNotEmpty(token)) {
            if (token.startsWith(TOKEN_PREFIX)) {
                token = token.replace(TOKEN_PREFIX, "");
            }

            // token 是 jwt token , 校验
            boolean flag = JWTUtils.checkJWT(token);
            if (!flag) {
                throw new AppRunTimeException(ApiResultCode.TOKEN_FAIL);
            }
            Claims claims = JWTUtils.infoJWT(token);
            uuid = claims.getId();

            log.info("从{}的jwt获取uuid={}", SecurityUtils.TOKEN_HEADER, uuid);
            return uuid;
        }


        throw new AppRunTimeException(ApiResultCode.TOKEN_FAIL);


    }

    private String getTokenKey(String uuid, String userType) {
        return RedisKey.TOKEN(userType, uuid);
    }

    private String getUserTokenListKey(String userId, String userType) {
        return RedisKey.USER_TOKEN_LIST(userType, userId);

    }

}

