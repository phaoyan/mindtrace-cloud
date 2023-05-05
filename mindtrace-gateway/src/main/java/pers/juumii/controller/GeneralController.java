package pers.juumii.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeneralController {

    @PostMapping("/debug")
    public SaResult debug(Object data){
        System.out.println("Permissions: " + StpUtil.getPermissionList());
        System.out.println("Roles: " + StpUtil.getRoleList());
        return SaResult.ok();
    }

    @GetMapping("/hello")
    public String hello(){
        return "hello mindtrace-security";
    }
}
