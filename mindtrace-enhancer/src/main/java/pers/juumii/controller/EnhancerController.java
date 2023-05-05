package pers.juumii.controller;

import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.Enhancer;
import pers.juumii.dto.EnhancerDTO;
import pers.juumii.service.EnhancerService;

import java.util.List;

@RestController
public class EnhancerController {

    private final EnhancerService enhancerService;

    @Autowired
    public EnhancerController(EnhancerService enhancerService) {
        this.enhancerService = enhancerService;
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
        return enhancerService.updateEnhancer(enhancerId, updated);
    }

    @DeleteMapping("/enhancer/{enhancerId}")
    public SaResult removeEnhancer(
            @PathVariable Long enhancerId){
        enhancerService.removeEnhancer(enhancerId);
        return SaResult.ok();
    }

    @GetMapping("/knode/{knodeId}/enhancer")
    public List<EnhancerDTO> getEnhancersFromKnode(
            @PathVariable Long knodeId){
        return Enhancer.transfer(enhancerService.getEnhancersFromKnode(knodeId));
    }

    @PutMapping("knode/{knodeId}/enhancer")
    public EnhancerDTO addEnhancerToKnode(@PathVariable Long knodeId){
        return Enhancer.transfer(enhancerService.addEnhancerToKnode(knodeId));
    }

    @PostMapping("knode/{knodeId}/enhancer/{enhancerId}")
    public SaResult connectEnhancerToKnode(
            @PathVariable Long knodeId,
            @PathVariable Long enhancerId){
        return enhancerService.connectEnhancerToKnode(knodeId, enhancerId);
    }

    @DeleteMapping("knode/{knodeId}/enhancer/{enhancerId}")
    public SaResult disconnectEnhancerToKnode(
            @PathVariable Long enhancerId,
            @PathVariable Long knodeId){
        return enhancerService.disconnectEnhancerFromKnode(knodeId,enhancerId);
    }

}
