package pers.juumii.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.entity.User;
import pers.juumii.service.LoginService;
import pers.juumii.service.UserService;

import java.net.http.HttpRequest;

@RestController
@RequestMapping("/user")
public class UserController {

    private final LoginService loginService;
    private final UserService userService;

    @Autowired
    public UserController(
            LoginService loginService,
            UserService userService) {
        this.loginService = loginService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public SaResult login(@RequestBody User user) {
        return loginService.login(user);
    }

    @PostMapping("/logout")
    public SaResult logout(){
        StpUtil.logout();
        return SaResult.ok();
    }

    @GetMapping("/login")
    public SaResult isLogin() {
        return SaResult.ok("是否登录：" + StpUtil.isLogin());
    }

    @PostMapping("/register")
    public SaResult register(){
        //TODO
        return SaResult.error("待开发");
    }

    @SaIgnore
    @GetMapping(value = "/{id}", produces = "application/json")
    public SaResult exists(@PathVariable Long id){
        return SaResult.data(userService.exists(id));
    }

}
