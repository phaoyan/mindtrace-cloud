package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.controller.aop.ControllerAspect;
import pers.juumii.data.Knode;
import pers.juumii.service.KnodeQueryService;

@RestController
@RequestMapping("/user/{userId}/knode")
public class KnodeQueryController {

    private final ControllerAspect aspect;
    private final KnodeQueryService knodeQuery;

    @Autowired
    public KnodeQueryController(ControllerAspect aspect, KnodeQueryService knodeQuery) {
        this.aspect = aspect;
        this.knodeQuery = knodeQuery;
    }

    @GetMapping
    public Object checkAll(@PathVariable Long userId){
        return Knode.transfer(knodeQuery.checkAll(userId));
    }

    @GetMapping("/{knodeId}")
    public Object check(
            @PathVariable Long userId,
            @PathVariable Long knodeId){
        aspect.checkKnodeAvailability(userId, knodeId);
        return Knode.transfer(knodeQuery.check(knodeId));
    }

    @GetMapping("/title")
    public Object check(
            @PathVariable Long userId,
            @RequestParam("title") String title){
        aspect.checkUserExistence(userId);
        return Knode.transfer(knodeQuery.checkByTitle(userId, title));
    }

    @GetMapping("/label")
    public Object checkByLabel(
            @PathVariable Long userId,
            @RequestParam("labelName") String labelName){
        aspect.checkUserExistence(userId);
        return Knode.transfer(knodeQuery.checkByLabel(labelName));
    }

    // 返回一个knode的所有直系分支
    @GetMapping("/{knodeId}/branches")
    public Object branches(
            @PathVariable Long userId,
            @PathVariable Long knodeId){
        aspect.checkKnodeAvailability(userId, knodeId);
        return Knode.transfer(knodeQuery.branches(knodeId));
    }

    // 返回一个knode的所有后代
    @GetMapping("/{knodeId}/offsprings")
    public Object offsprings(
            @PathVariable Long userId,
            @PathVariable Long knodeId){
        aspect.checkKnodeAvailability(userId, knodeId);
        return Knode.transfer(knodeQuery.offsprings(knodeId));
    }

    @GetMapping("/{knodeId}/leaves")
    public Object leaves(
            @PathVariable Long userId,
            @PathVariable Long knodeId){
        aspect.checkKnodeAvailability(userId, knodeId);
        return Knode.transfer(knodeQuery.leaves(knodeId));
    }

    // 返回一个knode的直系亲代
    @GetMapping("/{knodeId}/stem")
    public Object stem(
            @PathVariable Long userId,
            @PathVariable Long knodeId){
        aspect.checkKnodeAvailability(userId, knodeId);
        return Knode.transfer(knodeQuery.stem(knodeId));
    }

    @GetMapping("/{knodeId}/ancestors")
    public Object ancestors(
            @PathVariable Long userId,
            @PathVariable Long knodeId){
        aspect.checkKnodeAvailability(userId, knodeId);
        return Knode.transfer(knodeQuery.ancestors(knodeId));
    }


}
