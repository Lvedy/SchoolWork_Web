package org.example.clouddemo.feign.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.example.clouddemo.UserApplication;
import org.example.common.exception.AppRunTimeException;
import org.example.common.response.ApiResult;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * 自定义解码器
 */
@Log4j2
public class CustomFeignDecoder implements Decoder {
    private final ObjectMapper objectMapper;

    public CustomFeignDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(CustomFeignDecoder.class);

    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {

        log.debug("开始解码响应，目标类型: {}", type.getTypeName());

        // 1. 读取响应体
        String responseBody = readResponseBody(response);

        // 2. 检查响应状态码
        checkResponseStatus(response);

        // 3. 判断目标类型是否为 ApiResult<?>
        if (isApiResultType(type)) {
            // 直接解析为 ApiResult<T>
            return deserializeAsApiResult(responseBody, type);
        } else {
            // 从 ApiResult 中提取 data 字段并解析为目标类型
            return extractDataFromApiResult(responseBody, type);
        }
    }

    private String readResponseBody(Response response) throws IOException {
        try (InputStream inputStream = response.body().asInputStream()) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }
    }

    private void checkResponseStatus(Response response) {

    }

    private boolean isApiResultType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return parameterizedType.getRawType() == ApiResult.class;
        }
        return type == ApiResult.class;
    }

    private Object deserializeAsApiResult(String responseBody, Type type) throws IOException {
        // 使用 TypeFactory 构造带泛型信息的 JavaType
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        JavaType javaType = typeFactory.constructType(type);

        // 反序列化为带泛型的 ApiResult<T>
        ApiResult<?> apiResult = objectMapper.readValue(responseBody, javaType);

        // 检查业务状态码
        if (!apiResult.isSuccess()) {
            throw new AppRunTimeException("服务端返回错误：" + apiResult.getMsg());
        }

        return apiResult;
    }

    private Object extractDataFromApiResult(String responseBody, Type targetType) throws IOException {
        // 先解析为 ApiResult<JsonNode>
        ApiResult<?> apiResult = objectMapper.readValue(responseBody,
                new TypeReference<ApiResult<?>>() {
                });

        // 检查业务状态码
        if (!apiResult.isSuccess()) {
            throw new AppRunTimeException("服务端返回错误：" + apiResult.getMsg());
        }

        // 获取 data 字段
        Object data = apiResult.getData();

        if (data == null) {
            log.warn("响应中的 data 字段为空，目标类型: {}", targetType.getTypeName());
            return null;
        }

        // 将 data 转换为目标类型
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        JavaType javaType = typeFactory.constructType(targetType);

        return objectMapper.convertValue(data, javaType);
    }
}