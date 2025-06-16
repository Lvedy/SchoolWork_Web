package org.example.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * redis缓存配置
 */
@EnableCaching // 使用了CacheManager，别忘了开启它  否则无效
@Configuration
public class CacheConfig extends CachingConfigurerSupport {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    // 配置一个CacheManager 来支持缓存注解

//    @Bean
////    @Primary
//    public RedisTemplate<String, String> stringRedisTemplate() {
//        RedisTemplate<String, String> redisTemplate = new StringRedisTemplate();
//        redisTemplate.setConnectionFactory(redisConnectionFactory);
//
//        //定义key序列化方式
//        //RedisSerializer<String> redisSerializer = new StringRedisSerializer();//Long类型会出现异常信息;需要我们自定义key生成策略，一般没必要
//        //定义value的序列化方式
//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//        ObjectMapper om = new ObjectMapper();
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        jackson2JsonRedisSerializer.setObjectMapper(om);
//
//        // template.setKeySerializer(redisSerializer);
//        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//        redisTemplate.setDefaultSerializer(jackson2JsonRedisSerializer);
//      //  redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
//        redisTemplate.afterPropertiesSet();
//
//        return redisTemplate;
//    }

    @Bean
    public CacheManager cacheManager() {
        // 1.x是这么配置的：仅供参考
        //RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        //cacheManager.setDefaultExpiration(ONE_HOUR * HOURS_IN_ONE_DAY);
        //cacheManager.setUsePrefix(true);

        // --------------2.x的配置方式--------------
        // 方式一：直接create
        //RedisCacheManager redisCacheManager = RedisCacheManager.create(redisConnectionFactory());
        // 方式二：builder方式（推荐）

        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1)) //Duration.ZERO表示永不过期（此值一般建议必须设置）
                //.disableKeyPrefix() // 禁用key的前缀
                .disableCachingNullValues() //禁止缓存null值

                //=== 前缀我个人觉得是非常重要的，建议约定：注解缓存一个统一前缀、RedisTemplate直接操作的缓存一个统一前缀===
                //.prefixKeysWith("baidu:") // 底层其实调用的还是computePrefixWith() 方法，只是它的前缀是固定的（默认前缀是cacheName，此方法是把它固定住，一般不建议使用固定的）
                //.computePrefixWith(CacheKeyPrefix.simple()); // 使用内置的实现
                .computePrefixWith(cacheName -> "caching:" + cacheName) // 自己实现，建议这么使用(cacheName也保留下来了)
                //springboot 的缓存使用 jackson 来做数据的序列化与反序列化，如果默认使用 Object 作为序列化与反序列化的类型，则其只能识别 java 基本类型，遇到复杂类型时，jackson 就会先序列化成 LinkedHashMap ，然后再尝试强转为所需类别，这样大部分情况下会强转失败。此时就需要指定序列化方式为:
                //GenericJackson2JsonRedisSerializer，
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())) //解决  报错java.lang.ClassCastException: xxx cannot be cast to xxx
                ;

        RedisCacheManager redisCacheManager = RedisCacheManager.builder(redisConnectionFactory)
                // .disableCreateOnMissingCache() // 关闭动态创建Cache
                //.initialCacheNames() // 初始化时候就放进去的cacheNames（若关闭了动态创建，这个就是必须的）
                .cacheDefaults(configuration) // 默认配置（强烈建议配置上）。  比如动态创建出来的都会走此默认配置
                //.withInitialCacheConfigurations() // 个性化配置  可以提供一个Map，针对每个Cache都进行个性化的配置（否则是默认配置）
                //.transactionAware() // 支持事务

                .build();


        return redisCacheManager;
    }


}