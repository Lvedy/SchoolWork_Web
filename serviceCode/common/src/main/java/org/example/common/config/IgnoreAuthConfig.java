package org.example.common.config;


import lombok.extern.log4j.Log4j2;
import org.example.common.annotation.IgnoreAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: hzg
 * @Date: 2021/9/1 2:47 下午
 * 忽略权限验证注解
 */
@Log4j2
@Component
public class IgnoreAuthConfig {
    @Autowired
    ApplicationContext applicationContext;

    public List<String> getAllUrl() {
        log.info("getAllUrl");
        List<String> urls = new ArrayList<>();
        String[] list = applicationContext.getBeanNamesForAnnotation(RequestMapping.class);

        for (String name : list) {
            Object bean = applicationContext.getBean(name);

            RequestMapping apiRoot = AnnotationUtils.findAnnotation(bean.getClass(), RequestMapping.class);
            if (apiRoot != null) {
                String[] vals = apiRoot.value();
//                log.info(bean.getClass());

                Class<?> clazz = bean.getClass();
//                Type superType = bean.getClass().getGenericSuperclass();
//                try {
//                    clazz = Class.forName(superType.getTypeName());
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
                //    log.info("controller name:" + name);
                Method[] methods = clazz.getDeclaredMethods();
                IgnoreAuth ignoreAuthObj = AnnotationUtils.findAnnotation(bean.getClass(), IgnoreAuth.class);
                if (ignoreAuthObj != null) {
                    addUrl(urls, vals, methods, true);
                } else {
                    addUrl(urls, vals, methods, false);
                }

            }
        }
        return urls;
    }

    private void addUrl(List<String> urls, String[] vals, Method[] methods, boolean isAll) {
        for (Method method : methods) {
//            log.info(method.getName());
            IgnoreAuth ignoreAuth = AnnotationUtils.findAnnotation(method, IgnoreAuth.class);
            if (ignoreAuth == null && !isAll) {
                continue;
            }
            List<String> vals2 = new ArrayList<>();
            if (vals2.size() == 0) {
                PostMapping apiMethod = AnnotationUtils.findAnnotation(method, PostMapping.class);
                if (apiMethod != null)
                    vals2 = Arrays.asList(apiMethod.value());
            }
            if (vals2.size() == 0) {
                GetMapping apiMethod = AnnotationUtils.findAnnotation(method, GetMapping.class);
                if (apiMethod != null)
                    vals2 = Arrays.asList(apiMethod.value());
            }
            if (vals2.size() == 0) {
                RequestMapping apiMethod = AnnotationUtils.findAnnotation(method, RequestMapping.class);
                if (apiMethod != null)
                    vals2 = Arrays.asList(apiMethod.value());
            }
            if (vals2.size() == 0) {
                DeleteMapping apiMethod = AnnotationUtils.findAnnotation(method, DeleteMapping.class);
                if (apiMethod != null)
                    vals2 = Arrays.asList(apiMethod.value());
            }
            if (vals2.size() == 0) {
                PutMapping apiMethod = AnnotationUtils.findAnnotation(method, PutMapping.class);
                if (apiMethod != null)
                    vals2 = Arrays.asList(apiMethod.value());
            }

            if (vals2.size() > 0) {
                if (vals.length > 0) {
                    for (String v : vals) {
                        for (String v2 : vals2) {
                            if (!v.startsWith("/")) v = "/" + v;
                            if (!v2.startsWith("/")) v2 = "/" + v2;

                            urls.add(v + v2);
                        }
                    }
                } else {
                    for (String v2 : vals2) {
                        if (!v2.startsWith("/")) v2 = "/" + v2;
                        urls.add(v2);
                    }
                }
            }
        }
    }

}
