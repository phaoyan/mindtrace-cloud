package pers.juumii.utils;

import lombok.Data;

import java.util.Timer;

@Data
public class SerialTimer {

    private Long curMilli;
    private Long counter;
    private String info;

    public void start(){
        setCurMilli(System.currentTimeMillis());
        setCounter(counter + 1);
    }

    // 返回距离上次start的毫秒间隔并重新开始
    public Long restart(){
        long cur = System.currentTimeMillis();
        Long interval = cur - curMilli;
        start();
        return interval;
    }

    public void logAndRestart(){
        System.out.println(info + "T" + counter + " --- " + restart());
    }

    public static SerialTimer timer(){
        SerialTimer res = new SerialTimer();
        res.setCounter(0L);
        res.setInfo("    ");
        res.start();
        return res;
    }

    public static SerialTimer timer(String info){
        SerialTimer res = new SerialTimer();
        res.setCounter(0L);
        res.setInfo(info);
        res.start();
        return res;
    }
}
