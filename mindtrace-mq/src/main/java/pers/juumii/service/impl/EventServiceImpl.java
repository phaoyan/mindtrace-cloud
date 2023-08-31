package pers.juumii.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pers.juumii.data.MessageListener;
import pers.juumii.service.EventService;
import pers.juumii.utils.DataUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventServiceImpl implements EventService {

    public static final String LISTENERS_REDIS_KEY = "mindtrace::mq::listeners";

    private final StringRedisTemplate redis;
    private final RestTemplate rest;

    @Autowired
    public EventServiceImpl(StringRedisTemplate redis, RestTemplate rest) {
        this.redis = redis;
        this.rest = rest;
    }

    @Override
    public void emit(String event, String data) {
        Map<String, List<MessageListener>> eventMap = getEventMap();
        List<MessageListener> listenerList = eventMap.get(event);
        if(listenerList == null || listenerList.isEmpty()) return;
        MessageListener listener = DataUtils.randomPick(listenerList);
        try {
            rest.postForEntity(listener.getCallback(), data, Void.class);
            System.out.println("EMIT: " + event);
        }catch (Exception e){
            removeListener(listener.getId());
        }
    }

    @Override
    public void addListener(String event, String listener, String callback) {
        Map<String, List<MessageListener>> eventMap = getEventMap();
        List<MessageListener> listenerList = eventMap.getOrDefault(event, new ArrayList<>());
        if(DataUtils.ifAny(listenerList, l->l.getName().equals(listener) && l.getCallback().equals(callback)))
            return;
        listenerList.add(MessageListener.prototype(event, listener, callback));
        eventMap.put(event, listenerList);
        redis.opsForValue().set(LISTENERS_REDIS_KEY, JSONUtil.toJsonStr(eventMap));
        System.out.println("ADD LISTENER : " + listener + " " + event + " " + callback);
    }

    @Override
    public void removeListener(Long id) {
        Map<String, List<MessageListener>> eventMap = getEventMap();
        for(List<MessageListener> listenerList: eventMap.values())
            listenerList.removeIf(listener->listener.getId().equals(id));
        redis.opsForValue().set(LISTENERS_REDIS_KEY, JSONUtil.toJsonStr(eventMap));
        System.out.println("REMOVE LISTENER : " + id);
    }

    @Override
    public Map<String, List<MessageListener>> getEventMap() {
        JSONObject eventMap = JSONUtil.parseObj(redis.opsForValue().get(LISTENERS_REDIS_KEY));
        HashMap<String, List<MessageListener>> res = new HashMap<>();
        for(String event: eventMap.keySet())
            res.put(event, eventMap.getBeanList(event, MessageListener.class));
        return res;
    }
}
