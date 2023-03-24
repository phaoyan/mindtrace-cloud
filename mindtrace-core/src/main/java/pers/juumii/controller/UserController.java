package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.service.UserService;
import pers.juumii.utils.SaResult;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/{id}")
    public SaResult register(
            @PathVariable Long id,
            @RequestParam("name") String name){
        return userService.register(id, name);
    }

    // 为user创建一个根knode（学科）
    @PostMapping("/{id}/branch")
    public SaResult branch(
            @PathVariable Long id,
            @RequestParam("title") String title){
        return userService.branch(id, title);
    }

    @DeleteMapping("/{userId}/branch/{branchId}")
    public SaResult dropBranch(
            @PathVariable Long userId,
            @PathVariable Long branchId){
        return userService.dropBranch(userId, branchId);
    }

    @PostMapping("/{userId}/branch/{branchId}")
    public SaResult attachBranch(
            @PathVariable Long userId,
            @PathVariable Long branchId){
        return userService.attachBranch(userId, branchId);
    }

    // 返回user所有root的id
    @GetMapping("/{id}")
    public SaResult check(@PathVariable Long id){
        return userService.check(id);
    }

    @DeleteMapping("/{id}")
    public SaResult delete(@PathVariable Long id){
        return userService.remove(id);
    }
}
