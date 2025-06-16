package org.example.clouddemo.feign.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.example.clouddemo.UserApplication;
import org.example.common.exception.AppRunTimeException;
import org.example.common.utils.SecurityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import javax.servlet.http.HttpServletRequest;


@Configuration
@Log4j2
public class FeignConfig {
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(FeignConfig.class);

    @Bean
    public Decoder feignDecoder(Jackson2ObjectMapperBuilder jacksonBuilder) {
        ObjectMapper objectMapper = jacksonBuilder.build();
        return new CustomFeignDecoder(objectMapper); // 注入自定义解码器
    }


    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }


    // 请求拦截器：记录请求信息
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                log.info("Feign 请求:{} {} {}", template.feignTarget(),template.method(), template.path() + template.url());

                // 从当前请求中获取 token 并传递给 Feign 请求
                HttpServletRequest request = SecurityUtils.getCurrentRequest();


                String token = request.getHeader(SecurityUtils.TOKEN_HEADER);
                String uuid = request.getHeader(SecurityUtils.INFO_ID_HEADER);

                template.header(SecurityUtils.TOKEN_HEADER, token);
                template.header(SecurityUtils.INFO_ID_HEADER, uuid);
            }
        };
    }


    /**
     * 自定义 Feign 错误解码器
     */
    public static class CustomErrorDecoder implements ErrorDecoder {
        @Override
        public Exception decode(String methodKey, Response response) {
            // 1. 处理 HTTP 状态码异常（4xx/5xx）
            if (response.status() >= 400) {
                log.error("Feign 请求失败: {}, 状态码: {}", methodKey, response.status());
                return new AppRunTimeException("服务调用失败，状态码: " + response.status());
            }

            // 2. 默认处理其他异常（包括 RetryableException）
            return feign.FeignException.errorStatus(methodKey, response);
        }
    }
}