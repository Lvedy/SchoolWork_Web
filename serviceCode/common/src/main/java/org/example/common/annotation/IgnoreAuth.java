package org.example.common.annotation;

import java.lang.annotation.*;

/**
 * @Author: 杨雄杰
 * @Date: 2021/8/18 10:17 上午
 * 注解:不需要进行权限验证
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreAuth {

    String value() default "";
}
