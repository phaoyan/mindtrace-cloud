package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pers.juumii.controller.aop.ControllerAspect;
import pers.juumii.service.UserService;

@RestController
public class OtherController {

    private final ControllerAspect aspect;
    private final UserService userService;

    @Autowired
    public OtherController(ControllerAspect aspect, UserService userService) {
        this.aspect = aspect;
        this.userService = userService;
    }

    @GetMapping("/knode/{knodeId}/user")
    public Long findUser(@PathVariable Long knodeId){
        aspect.checkKnodeExistence(knodeId);
        return userService.findUserId(knodeId);
    }

}
