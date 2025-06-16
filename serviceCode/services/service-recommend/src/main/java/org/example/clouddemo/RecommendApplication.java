package org.example.clouddemo;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication()
@ComponentScan(value = {"org.example.*"})
@EnableCaching
@EnableScheduling
@EnableDiscoveryClient
@EnableFeignClients
@Log4j2
public class RecommendApplication implements ApplicationRunner {


    public static void main(String[] args) {

        try {
            ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(RecommendApplication.class, args);
            Environment environment = configurableApplicationContext.getBean(Environment.class);
            log.info("============> 系统启动成功！后台地址：http://localhost:{}", environment.getProperty("server.port"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("推荐微服务启动完成");
    }
}
