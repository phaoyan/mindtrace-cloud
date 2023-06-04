package pers.juumii.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.service.ClientCacheService;

/**
 * 前端进行一次学习的时候可能会中途离开网站，前端临时数据会消失。
 * 在这里后端为前端提供缓存功能
 */
@RestController
public class ClientCacheController {

    private final ClientCacheService clientCacheService;

    @Autowired
    public ClientCacheController(ClientCacheService clientCacheService) {
        this.clientCacheService = clientCacheService;
    }

    @PostMapping("/user/{userId}/cache")
    public void updateCache(@PathVariable Long userId, @RequestBody String json){
        JSONObject entries = JSONUtil.parseObj(json);
        clientCacheService.updateCache(userId, entries.getStr("key"), JSONUtil.toJsonStr(entries.get("data")));
    }

    @GetMapping("/user/{userId}/cache")
    public String getCache(@PathVariable Long userId, @RequestParam String key){
        return clientCacheService.getCache(userId, key);
    }

    @DeleteMapping("/user/{userId}/cache")
    public void clearCache(@PathVariable Long userId, @RequestParam String key){
        clientCacheService.clearCache(userId, key);
    }


}
