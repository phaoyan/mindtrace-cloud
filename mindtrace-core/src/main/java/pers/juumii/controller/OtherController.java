package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pers.juumii.service.UserService;

@RestController
public class OtherController {

    private final UserService userService;

    @Autowired
    public OtherController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/knode/{knodeId}/user")
    public Long findUser(@PathVariable Long knodeId){
        return userService.findUserId(knodeId);
    }

}
