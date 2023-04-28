package pers.juumii.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.convert.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.dto.UserDTO;
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

    @PostMapping("/register")
    public SaResult register(@RequestBody User user){
        return userService.register(user.getUsername(), user.getPassword());
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

    @SaIgnore
    @GetMapping(value = "/{id}")
    public SaResult exists(@PathVariable Long id){
        return SaResult.data(userService.exists(id));
    }

    @GetMapping
    public SaResult userInfo(){
        Long loginId = Convert.toLong(StpUtil.getLoginId());
        if(Convert.toBool(exists(loginId).getData()))
            return SaResult.data(UserDTO.transfer(userService.check(loginId)));
        else return SaResult.error("用户未登录");
    }

}
