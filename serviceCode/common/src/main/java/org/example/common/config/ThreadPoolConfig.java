package org.example.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @description: 自定义线程池
 * @author: alan
 * @time: 2021/12/30 14:52
 */
@Configuration
@EnableAsync//开启异步
public class ThreadPoolConfig {
    //在@SpringBootApplication启动类 添加注解@EnableAsync
    //异步方法使用注解@Async("@Bean的名称") ,返回值为void或者Future
    //切记一点 ,异步方法和调用方法一定要写在不同的类中,如果写在一个类中,是没有效果的

    //    在@Async标注的方法，同时也使用@Transactional进行标注；在其调用数据库操作之时，将无法产生事务管理的控制，原因就在于其是基于异步处理的操作。
    //    那该如何给这些操作添加事务管理呢？
    //    可以将需要事务管理操作的方法放置到异步方法内部，在内部被调用的方法上添加@Transactional
    //    示例：
    //    方法A， 使用了@Async/@Transactional来标注，但是无法产生事务控制的目的。
    //    方法B， 使用了@Async来标注，B中调用了C、D，C/D分别使用@Transactional做了标注，则可实现事务控制的目的

    /**
     * 线程池
     *
     * @return
     */
    @Bean("vodCallbackExecutor")
    public ThreadPoolTaskExecutor vodCallbackExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(10);
        // 设置最大线程数
        executor.setMaxPoolSize(20);
        // 设置队列容量
        executor.setQueueCapacity(200);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        // 设置默认线程名称
        executor.setThreadNamePrefix("vodCallback-");
        // 设置拒绝策略 ThreadPoolExecutor.AbortPolicy策略，是默认的策略,处理程序遭到拒绝将抛出运行时 RejectedExecutionException
        //CallerRunsPolicy 调用者运行策略，线程池中没办法运行，那么就由提交任务的这个线程运行（哪儿来的回哪儿儿去~）。
        // executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }

    /**
     * 线程池
     *
     * @return
     */
    @Bean("vodMergeExecutor")
    public ThreadPoolTaskExecutor vodMergeExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(10);
        // 设置最大线程数
        executor.setMaxPoolSize(40);
        // 设置队列容量
        executor.setQueueCapacity(200);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        // 设置默认线程名称
        executor.setThreadNamePrefix("vodMerge-");
        // 设置拒绝策略 ThreadPoolExecutor.AbortPolicy策略，是默认的策略,处理程序遭到拒绝将抛出运行时 RejectedExecutionException
        //CallerRunsPolicy 调用者运行策略，线程池中没办法运行，那么就由提交任务的这个线程运行（哪儿来的回哪儿儿去~）。
        // executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }

    /**
     * 线程池
     *
     * @return
     */
    @Bean("mqttExecutor")
    public ThreadPoolTaskExecutor mqttExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(12);
        // 设置最大线程数
        executor.setMaxPoolSize(500);
        // 设置队列容量
        executor.setQueueCapacity(5000);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        // 设置默认线程名称
        executor.setThreadNamePrefix("mqttExecutor-");
        // 设置拒绝策略 ThreadPoolExecutor.AbortPolicy策略，是默认的策略,处理程序遭到拒绝将抛出运行时 RejectedExecutionException
        //CallerRunsPolicy 调用者运行策略，线程池中没办法运行，那么就由提交任务的这个线程运行（哪儿来的回哪儿儿去~）。
        // executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }


    /**
     * 线程池
     *
     * @return
     */
    @Bean("consumerExecutor")
    public ThreadPoolTaskExecutor consumerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(12);
        // 设置最大线程数
        executor.setMaxPoolSize(100);
        // 设置队列容量
        executor.setQueueCapacity(1000);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        // 设置默认线程名称
        executor.setThreadNamePrefix("consumerExecutor-");
        // 设置拒绝策略 ThreadPoolExecutor.AbortPolicy策略，是默认的策略,处理程序遭到拒绝将抛出运行时 RejectedExecutionException
        //CallerRunsPolicy 调用者运行策略，线程池中没办法运行，那么就由提交任务的这个线程运行（哪儿来的回哪儿儿去~）。
        // executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }
}
