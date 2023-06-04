package pers.juumii.thread;

import pers.juumii.utils.SerialTimer;

import java.util.concurrent.BlockingQueue;

public class UserTaskHandler implements Runnable {
    private final BlockingQueue<Runnable> queue;
    private boolean state;

    public UserTaskHandler(BlockingQueue<Runnable> queue) {
        this.queue = queue;
        this.state = true;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    @Override
    public void run() {
        while (state){
            try{
                Runnable task = queue.take();
                SerialTimer timer = SerialTimer.timer();
                task.run();
                timer.setInfo("User Task: " + task);
                timer.logAndRestart();
            }catch (Throwable e){
                e.printStackTrace();
            }
        }
    }
}
