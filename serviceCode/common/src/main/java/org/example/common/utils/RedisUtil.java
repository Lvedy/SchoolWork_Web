package org.example.common.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import org.example.common.exception.AppRunTimeException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author:hzg
 * @Date:Created in 9:53 2022/4/15
 * @Description: redis 工具类
 */
public class RedisUtil {


    private static StringRedisTemplate stringRedisTemplate = SpringContextUtils.getBean("stringRedisTemplate", StringRedisTemplate.class);

    //一天多少s
    public static long TIME_DAYS_S = 24 * 60 * 60;
    //1小时多少s
    public static long TIME_HOUR_S = 60 * 60;
    //=============================common============================


    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public static void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                stringRedisTemplate.delete(key[0]);
            } else {

                stringRedisTemplate.delete(Arrays.stream(key).collect(Collectors.toList()));
            }
        }
    }

    //============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        return key == null ? null : (T) stringRedisTemplate.opsForValue().get(key);
    }


    public static boolean set(String key, String value) {
        return set(key, value, -1);
    }


    /**
     * 普通缓存放入并设置时间  单位秒
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public static boolean set(String key, String value, long time) {
        try {
            if (time > 0) {
                stringRedisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                stringRedisTemplate.opsForValue().set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增 此时value值必须为int类型 否则报错
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public static Long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递增 此时value值必须为int类型 否则报错
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @param time  过期时间 s
     * @return
     */
    public static Long incr(String key, long delta, long time) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        long i = stringRedisTemplate.opsForValue().increment(key, delta);
        stringRedisTemplate.expire(key, time, TimeUnit.SECONDS);
        return i;
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public static Long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须小于0");
        }
        return stringRedisTemplate.opsForValue().increment(key, -delta);
    }

    public static boolean setLongValue(String key, Long value, long time) {
        try {
            if (time > 0) {
                stringRedisTemplate.opsForValue().set(key, String.valueOf(value), time, TimeUnit.SECONDS);
            } else {
                stringRedisTemplate.opsForValue().set(key, String.valueOf(value));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public static Long getLongValue(String key) {
        if (key == null) {
            return null;
        }
        String result = stringRedisTemplate.opsForValue().get(key);
        if (result == null) {
            return null;
        }
        return Long.valueOf(result);
    }

    /**
     * 比较和删除标记，原子性
     *
     * @return 是否成功
     */
    public static boolean cad(String key, String value) {

        if (key.contains(StrUtil.SPACE) || value.contains(StrUtil.SPACE)) {
            throw new AppRunTimeException("network.busy");
        }

        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

        //通过lure脚本原子验证令牌和删除令牌
        Long result = stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                Collections.singletonList(key),
                value);

        return !Objects.equals(result, 0L);
    }


    /**
     * lpush
     *
     * @param key
     * @param value
     */
    public static Long leftPush(final String key, final String value) {
        return stringRedisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * lpush
     *
     * @param key
     * @param
     */
    public static String rightPop(final String key) {
        return stringRedisTemplate.opsForList().rightPop(key);
    }

    public static String listGetItem(final String key, long index) {
        return stringRedisTemplate.opsForList().index(key, index);
    }

    public static Long listRemoveItem(final String key, String value) {
        System.out.println("listRemoveItem:" + key + "," + value);
        return stringRedisTemplate.opsForList().remove(key, 0, value);

    }

    /**
     * lpush
     *
     * @param key
     * @param value
     */
    public static Long leftPushObject(final String key, final Object value) {
        return stringRedisTemplate.opsForList().leftPush(key, JSON.toJSONString(value));
    }

    /**
     * rightPop
     *
     * @param key
     * @param clazz
     */
    public static <T> T rightPopObject(final String key, Class<T> clazz) {
        String json = stringRedisTemplate.opsForList().rightPop(key);
        if (StrUtil.isEmpty(json)) {
            return null;
        }
        return JSON.parseObject(json, clazz);
    }

    /**
     * lpush
     *
     * @param key
     * @param value
     */
    public static Long leftPushObject(final String key, final Object value, int maxLength) {
        //超过长度,先移除
        if (listSize(key) >= maxLength) {
            stringRedisTemplate.opsForList().rightPop(key);
        }
        //再添加
        return stringRedisTemplate.opsForList().leftPush(key, JSON.toJSONString(value));
    }


    /**
     * 获取列表长度
     *
     * @param key
     * @return
     */
    public static long listSize(final String key) {
        return stringRedisTemplate.opsForList().size(key);
    }


    /**
     * 是否包含key
     *
     * @param key
     * @return
     */
    public static Boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }


    /**
     * 从stringRedisTemplate 获取数据
     *
     * @param key
     * @param clazz
     */
    public static <T> T getObject(final String key, Class<T> clazz) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isEmpty(json)) {
            return null;
        }

        return JSON.parseObject(json, clazz);
    }

    /**
     * 使用stringRedisTemplate 保存数据
     *
     * @param key
     * @param value
     * @param time  时间s
     */
    public static void setObject(final String key, Object value, long time) {
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(value), time, TimeUnit.SECONDS);
    }


    /**
     * 设置key的过期时间
     *
     * @param key
     * @param time
     */
    public static void expireKey(String key, long time) {
        stringRedisTemplate.expire(key, time, TimeUnit.SECONDS);

    }
}
