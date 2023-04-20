package pers.juumii.thread;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class ThreadUtils {

    private final Map<Long, BlockingQueue<Runnable>> userBlockingQueues;
    private final ThreadPoolTaskExecutor globalExecutor;

    public ThreadUtils(
            Map<Long, BlockingQueue<Runnable>> userBlockingQueues,
            ThreadPoolTaskExecutor globalExecutor) {
        this.userBlockingQueues = userBlockingQueues;
        this.globalExecutor = globalExecutor;
    }

    // 任务加到阻塞队列中实现同步执行的同时不影响主线程的流畅性。
    // 使用时直接 getUserBlockingQueue(userId).add(()->{...}) 即可
    public BlockingQueue<Runnable> getUserBlockingQueue(Long userId){
        return userBlockingQueues.computeIfAbsent(userId, key->{
            BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
            globalExecutor.execute(new UserTaskHandler(queue));
            return queue;
        });
    }
}
