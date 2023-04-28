package pers.juumii.thread;

import java.util.concurrent.BlockingQueue;

public class UserTaskHandler implements Runnable {
    private final BlockingQueue<Runnable> queue;

    public UserTaskHandler(BlockingQueue<Runnable> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true)
                queue.take().run();
        } catch (InterruptedException e) {
            // 线程被中断，可以在这里处理清理逻辑
            e.printStackTrace();
        }
    }
}
