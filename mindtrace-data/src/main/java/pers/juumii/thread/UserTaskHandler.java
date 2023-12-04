package pers.juumii.thread;

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
                task.run();
            }catch (Throwable e){
                e.printStackTrace();
            }
        }
    }
}
