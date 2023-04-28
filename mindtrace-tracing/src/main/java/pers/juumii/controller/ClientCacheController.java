package pers.juumii.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前端进行一次学习的时候可能会中途离开网站，前端临时数据会消失。
 * 在这里后端为前端提供缓存功能
 */
@RestController
@RequestMapping("/user/{userId}/cache")
public class ClientCacheController {
}
