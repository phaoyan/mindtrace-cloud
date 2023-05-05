package pers.juumii.thread;

import pers.juumii.utils.SerialTimer;

import java.util.concurrent.BlockingQueue;

public class UserTaskHandler implements Runnable {
    private final BlockingQueue<Runnable> queue;

    public UserTaskHandler(BlockingQueue<Runnable> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true){
                Runnable task = queue.take();
                SerialTimer timer = SerialTimer.timer();
                timer.setInfo("User Task: " + task);
                task.run();
                timer.logAndRestart();
            }
        } catch (Throwable e) {
            // 线程被中断，可以在这里处理清理逻辑
            e.printStackTrace();
        }
    }
}
