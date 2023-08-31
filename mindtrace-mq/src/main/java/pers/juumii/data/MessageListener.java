package pers.juumii.data;

import cn.hutool.core.util.IdUtil;
import lombok.Data;

@Data
public class MessageListener {

    private Long id;
    private String name;
    private String event;
    private String callback;

    public static MessageListener prototype(String event, String listener, String callback) {
        MessageListener res = new MessageListener();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setName(listener);
        res.setEvent(event);
        res.setCallback(callback);
        return res;
    }
}
