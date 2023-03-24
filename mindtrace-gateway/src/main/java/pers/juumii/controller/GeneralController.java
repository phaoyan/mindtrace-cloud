package pers.juumii.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeneralController {

    @SaCheckLogin
    @GetMapping("/debug")
    public SaResult debug(){
        System.out.println("Permissions: " + StpUtil.getPermissionList());
        System.out.println("Roles: " + StpUtil.getRoleList());
        return SaResult.ok();
    }
}
