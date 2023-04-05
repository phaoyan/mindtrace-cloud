package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.controller.aop.ControllerAspect;
import pers.juumii.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    private final ControllerAspect aspect;
    private final UserService userService;

    @Autowired
    public UserController(ControllerAspect aspect, UserService userService) {
        this.aspect = aspect;
        this.userService = userService;
    }

    @PostMapping("/{userId}")
    public Object register(@PathVariable Long userId){
        return userService.register(userId);
    }

    @DeleteMapping("/{userId}")
    public Object unregister(@PathVariable Long userId){
        aspect.checkUserExistence(userId);
        return userService.unregister(userId);
    }

    @GetMapping("/{userId}/root")
    public Object rootId(@PathVariable Long userId){
        aspect.checkUserExistence(userId);
        return userService.checkRootId(userId);
    }



}
