package com.test.booking.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
@EnableAsync
public class ServiceAsyncConfig implements AsyncConfigurer {

    private static final String THREAD_NAME_PREFIX = "booking-async-";
    private static final Integer CORE_POOL_SIZE = 2;
    private static final Integer MAX_POOL_SIZE = 2;
    private static final Integer QUEUE_CAPACITY = 500;

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        threadPoolTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        threadPoolTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        threadPoolTaskExecutor.setQueueCapacity(QUEUE_CAPACITY);
        threadPoolTaskExecutor.setRejectedExecutionHandler((r, executor) -> log.warn("Task rejected, thread pool is full and queue is also full"));
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

}
