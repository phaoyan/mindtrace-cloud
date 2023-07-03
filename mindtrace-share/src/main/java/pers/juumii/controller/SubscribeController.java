package pers.juumii.controller;

import cn.hutool.core.convert.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.service.SubscribeService;

import java.util.List;

@RestController
public class SubscribeController {

    private final SubscribeService subscribeService;

    @Autowired
    public SubscribeController(SubscribeService subscribeService) {
        this.subscribeService = subscribeService;
    }

    @GetMapping("/knode/{knodeId}/subscribe/user")
    public List<String> getUserSubscribes(@PathVariable Long knodeId){
        return subscribeService.getUserSubscribes(knodeId).stream().map(Convert::toStr).toList();
    }

    @GetMapping("/knode/{knodeId}/subscribe/knode")
    public List<String> getKnodeSubscribes(@PathVariable Long knodeId){
        return subscribeService.getKnodeSubscribes(knodeId).stream().map(Convert::toStr).toList();
    }

    @GetMapping("/knode/{knodeId}/subscribe/enhancer")
    public List<String> getEnhancerSubscribes(@PathVariable Long knodeId){
        return subscribeService.getEnhancerSubscribes(knodeId).stream().map(Convert::toStr).toList();
    }

    @PostMapping("/knode/{knodeId}/subscribe/user/{targetId}")
    public void subscribeUser(@PathVariable Long knodeId, @PathVariable Long targetId){
        subscribeService.subscribeUser(knodeId, targetId);
    }

    @PostMapping("/knode/{knodeId}/subscribe/knode/{targetId}")
    public void subscribeKnode(@PathVariable Long knodeId, @PathVariable Long targetId){
        subscribeService.subscribeKnode(knodeId, targetId);
    }

    @PostMapping("/knode/{knodeId}/subscribe/enhancer/{targetId}")
    public void subscribeEnhancer(@PathVariable Long knodeId, @PathVariable Long targetId){
        subscribeService.subscribeEnhancer(knodeId, targetId);
    }

    @DeleteMapping("/knode/{knodeId}/subscribe/user/{targetId}")
    public void removeUserSubscribe(@PathVariable Long knodeId, @PathVariable Long targetId){
        subscribeService.removeUserSubscribe(knodeId, targetId);
    }

    @DeleteMapping("/knode/{knodeId}/subscribe/knode/{targetId}")
    public void removeKnodeSubscribe(@PathVariable Long knodeId, @PathVariable Long targetId){
        subscribeService.removeKnodeSubscribe(knodeId, targetId);
    }

    @DeleteMapping("/knode/{knodeId}/subscribe/enhancer/{targetId}")
    public void removeEnhancerSubscribe(@PathVariable Long knodeId, @PathVariable Long targetId){
        subscribeService.removeEnhancerSubscribe(knodeId, targetId);
    }
}
