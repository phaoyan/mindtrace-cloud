package pers.juumii.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.convert.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.Enhancer;
import pers.juumii.dto.EnhancerDTO;
import pers.juumii.dto.IdPair;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.service.EnhancerService;
import pers.juumii.utils.AuthUtils;

import java.util.List;

@RestController
public class EnhancerController {

    private final EnhancerService enhancerService;
    private final AuthUtils authUtils;
    private final CoreClient coreClient;

    @Autowired
    public EnhancerController(
            EnhancerService enhancerService,
            AuthUtils authUtils,
            CoreClient coreClient) {
        this.enhancerService = enhancerService;
        this.authUtils = authUtils;
        this.coreClient = coreClient;
    }

    private void enhancerSameUser(Long enhancerId){
        Enhancer enhancer = enhancerService.getEnhancerById(enhancerId);
        authUtils.same(enhancer.getCreateBy());
    }

    private void knodeSameUser(Long knodeId){
        KnodeDTO knode = coreClient.check(knodeId);
        authUtils.same(Convert.toLong(knode.getCreateBy()));
    }

    @GetMapping("/user/{userId}/enhancer")
    public List<EnhancerDTO> getAllEnhancersFromUser(@PathVariable Long userId){
        return Enhancer.transfer(enhancerService.getAllEnhancers(userId));
    }

    @GetMapping("/enhancer/{enhancerId}")
    public EnhancerDTO getEnhancerById(@PathVariable Long enhancerId){
        return Enhancer.transfer(enhancerService.getEnhancerById(enhancerId));
    }

    @GetMapping("/date/enhancer")
    public List<EnhancerDTO> getEnhancersByDate(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long knodeId,
            @RequestParam(required = false) String left,
            @RequestParam(required = false) String right){
        if(knodeId == null)
            return Enhancer.transfer(enhancerService.getEnhancersByDate(userId, left, right));
        else return Enhancer.transfer(enhancerService.getEnhancersByDateBeneathKnode(knodeId, left, right));
    }

    @PostMapping("/enhancer/{enhancerId}")
    public void updateEnhancer(
            @PathVariable Long enhancerId,
            @RequestBody EnhancerDTO updated){
        enhancerSameUser(enhancerId);
        enhancerService.updateEnhancer(enhancerId, updated);
    }

    @PutMapping("/enhancer/{enhancerId}/title")
    public void setTitle(@PathVariable Long enhancerId, @RequestParam String title){
        enhancerSameUser(enhancerId);
        enhancerService.setTitle(enhancerId, title);
    }

    @PutMapping("/enhancer/{enhancerId}/isQuiz")
    public void setIsQuiz(@PathVariable Long enhancerId, @RequestParam Boolean isQuiz){
        enhancerSameUser(enhancerId);
        enhancerService.setIsQuiz(enhancerId, isQuiz);
    }

    @DeleteMapping("/enhancer/{enhancerId}")
    public SaResult removeEnhancer(
            @PathVariable Long enhancerId){
        enhancerSameUser(enhancerId);
        enhancerService.removeEnhancer(enhancerId);
        return SaResult.ok();
    }

    @GetMapping("/knode/{knodeId}/enhancer")
    public List<EnhancerDTO> getEnhancersFromKnode(
            @PathVariable Long knodeId){
        return Enhancer.transfer(enhancerService.getEnhancersFromKnode(knodeId));
    }

    @PostMapping("/batch/knode/enhancer")
    public List<EnhancerDTO> getEnhancerFromKnodeBatch(@RequestBody List<Long> knodeIds){
        return Enhancer.transfer(enhancerService.getEnhancersFromKnodeBatch(knodeIds));
    }

    @GetMapping("/knode/{knodeId}/offspring/enhancer")
    public List<EnhancerDTO> getEnhancersFromKnodeIncludingBeneath(@PathVariable Long knodeId){
        return Enhancer.transfer(enhancerService.getEnhancersFromKnodeIncludingBeneath(knodeId));
    }

    @GetMapping("/enhancer/{enhancerId}/knode")
    public List<KnodeDTO> getKnodeByEnhancerId(@PathVariable Long enhancerId){
        return enhancerService.getKnodeByEnhancerId(enhancerId);
    }

    @GetMapping("/knode/{rootId}/withQuiz")
    List<String> getKnodeIdsWithQuiz(@PathVariable Long rootId){
        return enhancerService.getKnodeIdsWithQuiz(rootId).stream().map(Object::toString).toList();
    }

    @PutMapping("knode/{knodeId}/enhancer")
    public EnhancerDTO addEnhancerToKnode(@PathVariable Long knodeId){
        knodeSameUser(knodeId);
        return Enhancer.transfer(enhancerService.addEnhancerToKnode(knodeId));
    }

    @PutMapping("/enhancer")
    public EnhancerDTO addEnhancer(){
        return Enhancer.transfer(enhancerService.addEnhancerToUser(StpUtil.getLoginIdAsLong()));
    }

    @PostMapping("knode/{knodeId}/enhancer/{enhancerId}")
    public void connectEnhancerToKnode(
            @PathVariable Long knodeId,
            @PathVariable Long enhancerId){
        knodeSameUser(knodeId);
        enhancerSameUser(enhancerId);
        enhancerService.connectEnhancerToKnode(knodeId, enhancerId);
    }

    @DeleteMapping("knode/{knodeId}/enhancer/{enhancerId}")
    public void disconnectEnhancerToKnode(
            @PathVariable Long enhancerId,
            @PathVariable Long knodeId){
        knodeSameUser(knodeId);
        enhancerSameUser(enhancerId);
        enhancerService.disconnectEnhancerFromKnode(knodeId,enhancerId);
    }

    @GetMapping("/knode/{knodeId}/enhancer/count")
    public Long getEnhancerCount(@PathVariable Long knodeId){
        return enhancerService.getEnhancerCount(knodeId);
    }


    @PostMapping("/rel/knode/enhancer")
    List<IdPair> getKnodeEnhancerRels(@RequestBody List<Long> knodeIds){
        return enhancerService.getKnodeEnhancerRels(knodeIds);
    }

    @PutMapping("/rel/knode/enhancer")
    void addKnodeEnhancerRel(@RequestParam Long knodeId, @RequestParam Long enhancerId){
        enhancerService.connectEnhancerToKnode(knodeId, enhancerId);
    }

}
