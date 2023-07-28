package pers.juumii.controller;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.convert.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.Enhancer;
import pers.juumii.dto.EnhancerDTO;
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

    @PostMapping("/enhancer/{enhancerId}")
    public SaResult updateEnhancer(
            @PathVariable Long enhancerId,
            @RequestBody EnhancerDTO updated){
        enhancerSameUser(enhancerId);
        return enhancerService.updateEnhancer(enhancerId, updated);
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

    @GetMapping("/knode/{knodeId}/offspring/enhancer")
    public List<EnhancerDTO> getEnhancersFromKnodeIncludingBeneath(@PathVariable Long knodeId){
        return Enhancer.transfer(enhancerService.getEnhancersFromKnodeIncludingBeneath(knodeId));
    }

    @PutMapping("knode/{knodeId}/enhancer")
    public EnhancerDTO addEnhancerToKnode(@PathVariable Long knodeId){
        knodeSameUser(knodeId);
        return Enhancer.transfer(enhancerService.addEnhancerToKnode(knodeId));
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

}
