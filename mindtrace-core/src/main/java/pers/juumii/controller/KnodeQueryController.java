package pers.juumii.controller;

import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.service.KnodeQueryService;

@RestController
@RequestMapping("/user/{userId}/knode")
public class KnodeQueryController {

    private final KnodeQueryService knodeQuery;

    @Autowired
    public KnodeQueryController(KnodeQueryService knodeQuery) {
        this.knodeQuery = knodeQuery;
    }

    @GetMapping("/{knodeId}")
    public Object check(
            @PathVariable Long userId,
            @PathVariable Long knodeId){
        return knodeQuery.check(knodeId);
    }

    @GetMapping
    public Object check(
            @PathVariable Long userId,
            @RequestParam("title") String title){
        return knodeQuery.checkByTitle(userId, title);
    }

    // 返回一个knode的所有直系分支
    @GetMapping("/{knodeId}/branches")
    public Object branches(
            @PathVariable Long userId,
            @PathVariable Long knodeId){
        return knodeQuery.branches(knodeId);
    }

    // 返回一个knode的所有后代
    @GetMapping("/{knodeId}/offsprings")
    public Object offsprings(
            @PathVariable Long userId,
            @PathVariable Long knodeId){
        return knodeQuery.offsprings(knodeId);
    }

    // 返回一个knode的直系亲代
    @GetMapping("/{knodeId}/stem")
    public Object stem(
            @PathVariable Long userId,
            @PathVariable Long knodeId){
        return knodeQuery.stem(knodeId);
    }

    @GetMapping("/{knodeId}/ancestors")
    public Object ancestors(
            @PathVariable Long userId,
            @PathVariable Long knodeId){
        return knodeQuery.ancestors(knodeId);
    }

}
