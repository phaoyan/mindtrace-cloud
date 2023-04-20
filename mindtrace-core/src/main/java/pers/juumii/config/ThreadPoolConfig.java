package pers.juumii.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {

    @Bean(name = "globalExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // 核心线程数
        executor.setMaxPoolSize(10); // 最大线程数
        executor.setQueueCapacity(25); // 任务队列大小
        executor.setThreadNamePrefix("Global-"); // 线程名前缀
        executor.setKeepAliveSeconds(60); // 空闲线程等待新任务的最长时间（秒）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 任务拒绝处理策略
        executor.initialize(); // 初始化线程池
        return executor;
    }

    @Bean(name = "userBlockingQueues")
    public Map<Long, BlockingQueue<Runnable>> userBlockingQueues(){
        return new ConcurrentHashMap<>();
    }

}
