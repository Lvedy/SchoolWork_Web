package org.example.common.config;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: hzg
 * @Date: 2021/8/6 10:16 上午
 */
@Log4j2
@Component
public class CustomIdGenerator implements IdentifierGenerator {

    @Autowired
    private SnowflakeConfig snowflakeConfig;

    @Override
    public Long nextId(Object entity) {
        //可以将当前传入的class全类名来作为bizKey,或者提取参数来生成bizKey进行分布式Id调用生成.
        String bizKey = entity.getClass().getName();
        //根据bizKey调用分布式ID生成
        long id = snowflakeConfig.snowflake().nextId();
        //返回生成的id值即可.
        log.info("自定义id:" + id);
        return id;
    }

}