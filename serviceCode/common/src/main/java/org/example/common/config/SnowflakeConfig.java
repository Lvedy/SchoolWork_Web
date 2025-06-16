package org.example.common.config;

import cn.hutool.core.lang.Snowflake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 雪花算法订单号生成
 */
@Configuration
public class SnowflakeConfig {

    @Value("${snowflake.datacenterId}")
    private Long datacenterId;

    @Value("${snowflake.workerId}")
    private Long workerId;

    @Bean
    public Snowflake snowflake() {
        return new Snowflake(workerId, datacenterId);
    }

}
