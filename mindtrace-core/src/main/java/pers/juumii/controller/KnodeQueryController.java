package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.Knode;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.service.KnodeQueryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class KnodeQueryController {

    private final KnodeQueryService knodeQuery;

    @Autowired
    public KnodeQueryController(KnodeQueryService knodeQuery) {
        this.knodeQuery = knodeQuery;
    }


    @GetMapping("/root")
    public List<KnodeDTO> checkAllRoots(){
        return Knode.transfer(knodeQuery.checkAllRoots()).stream().filter(Objects::nonNull).toList();
    }


    @GetMapping("/knode")
    public List<KnodeDTO> checkAll(){
        return Knode.transfer(knodeQuery.checkAll()).stream().filter(Objects::nonNull).toList();
    }

    @GetMapping("/user/{userId}/knode")
    public List<KnodeDTO> checkAll(@PathVariable Long userId){
        return Knode.transfer(knodeQuery.checkAll(userId));
    }

    @GetMapping("/knode/{knodeId}")
    public KnodeDTO check(@PathVariable Long knodeId){
        return Knode.transfer(knodeQuery.check(knodeId));
    }

    @GetMapping("/date/knode")
    public List<KnodeDTO> checkByDate(
            @RequestParam String left,
            @RequestParam String right,
            @RequestParam Long knodeId){
        return Knode.transfer(knodeQuery.checkByDate(left, right, knodeId));
    }

    @GetMapping("/like/knode")
    public List<KnodeDTO> checkByLike(@RequestParam String like, @RequestParam Integer count){
        return Knode.transfer(knodeQuery.checkByLike(like, count));
    }

    @PostMapping("/batch/knode")
    public List<KnodeDTO> checkBatch(@RequestBody List<Long> knodeIds){
        return Knode.transfer(
                knodeIds.stream()
                .map(knodeQuery::check)
                .filter(Objects::nonNull)
                .toList());
    }

    // 返回一个knode的所有直系分支
    @GetMapping("/knode/{knodeId}/branches")
    public List<KnodeDTO> branches(@PathVariable Long knodeId){
        return Knode.transfer(knodeQuery.branches(knodeId));
    }

    // 返回一个knode的所有后代，包括自身
    @GetMapping("/knode/{knodeId}/offsprings")
    public List<KnodeDTO> offsprings(@PathVariable Long knodeId){
        return Knode.transfer(knodeQuery.offsprings(knodeId));
    }

    @GetMapping("/knode/{knodeId}/offspring/id")
    public List<Long> offspringIds(@PathVariable Long knodeId){
        return knodeQuery.offspringIds(knodeId);
    }

    @GetMapping("/knode/{parentId}/knode/{childId}")
    public Boolean isOffspring(@PathVariable Long childId, @PathVariable Long parentId){
        return knodeQuery.isOffspring(childId, parentId);
    }

    @GetMapping("/knode/{knodeId}/leaves")
    public List<KnodeDTO> leaves(@PathVariable Long knodeId){
        return Knode.transfer(knodeQuery.leaves(knodeId));
    }

    @GetMapping("/knode/{knodeId}/leave/count")
    public Integer leaveCount(@PathVariable Long knodeId){
        return knodeQuery.leaveCount(knodeId);
    }

    // 返回一个knode的直系亲代
    @GetMapping("/knode/{knodeId}/stem")
    public KnodeDTO stem(@PathVariable Long knodeId){
        return Knode.transfer(knodeQuery.stem(knodeId));
    }

    @GetMapping("/knode/{knodeId}/ancestor")
    public List<KnodeDTO> ancestors(@PathVariable Long knodeId){
        return Knode.transfer(knodeQuery.ancestors(knodeId));
    }

    @GetMapping("/knode/{knodeId}/ancestor/id")
    public List<Long> ancestorIds(@PathVariable Long knodeId){
        return knodeQuery.ancestors(knodeId).stream().map(Knode::getId).toList();
    }

    @PostMapping("/batch/knode/ancestor/id")
    public Map<Long, List<Long>> ancestorIdsBatch(@RequestBody List<Long> knodeIds){
        return knodeQuery.ancestorIdsBatch(knodeIds);
    }

    @GetMapping("/knode/{knodeId}/chainStyleTitle")
    public List<String> getChainStyleTitle(@PathVariable Long knodeId){
        return knodeQuery.chainStyleTitle(knodeId);
    }

    @GetMapping("/knode/{knodeId}/chainStyleTitleBeneath")
    public Map<String, List<String>> getChainStyleTitlesBeneath(@PathVariable Long knodeId){
        return knodeQuery.chainStyleTitleBeneath(knodeId);
    }

}
