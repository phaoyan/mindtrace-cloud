package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.Knode;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.service.KnodeQueryService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class KnodeQueryController {

    private final KnodeQueryService knodeQuery;

    @Autowired
    public KnodeQueryController(KnodeQueryService knodeQuery) {
        this.knodeQuery = knodeQuery;
    }


    @GetMapping("/user/{userId}/knode")
    public List<KnodeDTO> checkAll(@PathVariable Long userId){
        return Knode.transfer(knodeQuery.checkAll(userId));
    }

    @GetMapping("/knode/{knodeId}")
    public KnodeDTO check(@PathVariable Long knodeId){
        return Knode.transfer(knodeQuery.check(knodeId));
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

    @GetMapping("/knode/{knodeId}/leaves")
    public List<KnodeDTO> leaves(@PathVariable Long knodeId){
        return Knode.transfer(knodeQuery.leaves(knodeId));
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

    @GetMapping("/knode/{knodeId}/chainStyleTitle")
    public List<String> getChainStyleTitle(@PathVariable Long knodeId){
        return knodeQuery.chainStyleTitle(knodeId);
    }

    @GetMapping("/knode/{knodeId}/chainStyleTitleBeneath")
    public Map<String, List<String>> getChainStyleTitlesBeneath(@PathVariable Long knodeId){
        return knodeQuery.chainStyleTitleBeneath(knodeId);
    }

}
