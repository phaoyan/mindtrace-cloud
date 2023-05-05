package pers.juumii.controller;

import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.controller.aop.ControllerAspect;
import pers.juumii.service.UserService;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user/{userId}")
    public SaResult register(@PathVariable Long userId){
        return userService.register(userId);
    }

    @DeleteMapping("/user/{userId}")
    public SaResult unregister(@PathVariable Long userId){
        return userService.unregister(userId);
    }

    @GetMapping("/user/{userId}/root")
    public Long rootId(@PathVariable Long userId){
        return userService.checkRootId(userId);
    }



}
