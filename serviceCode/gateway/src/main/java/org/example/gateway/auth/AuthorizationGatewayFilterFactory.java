package org.example.gateway.auth;


import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.example.common.auth.JWTUtils;
import org.example.common.response.ApiResultCode;
import org.example.common.utils.SecurityUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Slf4j
@Component
public class AuthorizationGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthorizationGatewayFilterFactory.Config> {
    private static final String JWT_TOKEN = "token";
    private static final String USER_INFO_HEADER = "X-User-Info";

    private static final String NAME_KEY = "excludedPaths";

    // 关键修改：禁用快捷参数，强制使用 args 方式
    public List<String> shortcutFieldOrder() {
        return Collections.emptyList();
    }


    public AuthorizationGatewayFilterFactory() {
        super(Config.class);
    }


    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 每次请求时重新加载配置，确保获取到当前路由的excludedPaths
            List<String> pathList = splitExcludedPaths(config.getExcludedPaths());

            // 转换为正则表达式列表
            List<String> regexPatterns = pathList.stream()
                    .map(this::convertToRegex)
                    .collect(Collectors.toList());

            // 编译正则表达式
            List<Pattern> currentExcludePatterns = regexPatterns.stream()
                    .map(Pattern::compile)
                    .collect(Collectors.toList());
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();


            //判断是否需要过滤
            String path = request.getURI().getPath();

            boolean isApp = path.startsWith("/api/app");

            HttpMethod httpMethod = request.getMethod();
            String methodName = httpMethod.name();

            //是否放行
            boolean isSkip = shouldSkip(path, currentExcludePatterns);
            if (isSkip) {
                //直接通过，传输到下一级
                return chain.filter(exchange);
            }


            //判断是否存在JWT
            String jwt = "";
            List<String> headers = request.getHeaders().get(SecurityUtils.TOKEN_HEADER);
            if (headers != null && headers.size() > 0) {
                jwt = headers.get(0);
            }
            if (StringUtils.isEmpty(jwt)) {
                log.info("jwt null:path={}", path);
                //返回未授权错误
                return error(response, ApiResultCode.TOKEN_FAIL);
            }

            boolean flag = JWTUtils.checkJWT(jwt);
            if (!flag) {
                log.info("jwt error:{}", jwt);
                return error(response, ApiResultCode.TOKEN_FAIL);
            }

            // 将用户信息id 放在 header,后续可以通过redis获取信息
            Claims claims = JWTUtils.infoJWT(jwt);
            String uuid = claims.getId();

            String type = isApp ? "app" : "admin";


            //将uuid添加到请求头中
            ServerHttpRequest.Builder reqBuilder = request.mutate();
            // 添加自定义请求头
            reqBuilder.header(SecurityUtils.INFO_ID_HEADER, uuid);
            reqBuilder.header(SecurityUtils.USER_TYPE_HEADER, type);

            // 构建新的请求
            ServerHttpRequest newRequest = reqBuilder.build();

            // 继续过滤链，传递新的请求
            return chain.filter(exchange.mutate().request(newRequest).build());
        };
    }


    private boolean shouldSkip(String path, List<Pattern> excludePatterns) {
        if (excludePatterns == null || excludePatterns.isEmpty()) {
            return false;
        }
        
        for (Pattern pattern : excludePatterns) {
            if (pattern.matcher(path).matches()) {
                return true;
            }
        }
        
        return false;
    }


    // 将路径模式转换为正则表达式
    private String convertToRegex(String pathPattern) {
        // 处理常见的路径通配符
        String regex = pathPattern
                .replace("/**/", "/[^/]+/")    // 匹配中间任意层级路径
                .replace("/*/", "/[^/]+/")     // 匹配单层级路径
                .replace("/*", "/[^/]*")       // 匹配路径结尾
                .replace("*", "[^/]*");        // 匹配任意字符（不含斜杠）

        // 确保正则表达式锚定路径首尾
        if (!regex.startsWith("^")) {
            regex = "^" + regex;
        }
        if (!regex.endsWith("$")) {
            regex = regex + "$";
        }

        return regex;
    }


    // 新增方法：将逗号分隔的字符串拆分为列表
    private List<String> splitExcludedPaths(List<String> configPaths) {
        if (configPaths == null || configPaths.isEmpty()) {
            return Collections.emptyList();
        }

        // 处理可能包含逗号的多个路径
        return configPaths.stream()
                .flatMap(path -> Arrays.stream(path.split(",")))
                .map(String::trim)
                .filter(p -> !p.isEmpty())
                .collect(Collectors.toList());
    }

    private Mono<Void> error(ServerHttpResponse response, ApiResultCode apiResult) {
        //返回错误
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        response.setStatusCode(HttpStatus.OK);
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", apiResult.getCode());
        resp.put("data", "{}");
        resp.put("message", apiResult.getMsg());
        DataBuffer buffer = response.bufferFactory().wrap(JSON.toJSONString(resp).getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }


    public static class Config implements Serializable {

        private List<String> excludedPaths = Collections.emptyList();

        public List<String> getExcludedPaths() {
            return excludedPaths;
        }

        public Config setExcludedPaths(List<String> excludedPaths) {
            this.excludedPaths = excludedPaths;
            return this;
        }
    }
}
