package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.MessageListener;
import pers.juumii.service.EventService;

import java.util.List;
import java.util.Map;

/**
 * 本服务的使用：
 * 1. 其他服务通过Feign客户端调用来使用本服务
 * 2. 若要监听消息，则添加一个listener，其event选中要监听的事件；同时为listener命名，同名的listener为等价实例
 * 3. 若要发布消息，则emit一个event，mindtrace-mq向所有该事件的listener发送http请求消费该消息
 */
@RestController
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PutMapping("/event")
    public void emit(
            @RequestParam String event,
            @RequestParam String data){
        eventService.emit(event, data);
    }

    @PutMapping("/listener")
    public void addListener(
            @RequestParam String event,
            @RequestParam String callback){
        eventService.addListener(event, callback);
    }

    @DeleteMapping("/listener")
    public void removeListener(@RequestParam Long id){
        eventService.removeListener(id);
    }

    @GetMapping("/listener")
    public Map<String, List<MessageListener>> getListeners(){
        return eventService.getEventMap();
    }


}
