package pers.juumii.utils;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.function.Function;
import java.util.function.Supplier;

@Data
public class SerialTimer {

    private Long curMilli;
    private Long counter;
    private String info;
    private Logger logger;

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
        logger.info(info + "T" + counter + " --- " + restart());
    }

    public static SerialTimer timer(Logger logger){
        SerialTimer res = new SerialTimer();
        res.setCounter(0L);
        res.setInfo("    ");
        res.setLogger(logger);
        res.start();
        return res;
    }

    public static SerialTimer timer(Logger logger, String info){
        SerialTimer res = new SerialTimer();
        res.setCounter(0L);
        res.setInfo(info);
        res.setLogger(logger);
        res.start();
        return res;
    }
}
