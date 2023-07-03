package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.EnhancerShare;
import pers.juumii.dto.EnhancerDTO;
import pers.juumii.dto.share.EnhancerShareDTO;
import pers.juumii.service.EnhancerShareService;

import java.util.List;

@RestController
public class EnhancerShareController {
    private final EnhancerShareService enhancerShareService;

    @Autowired
    public EnhancerShareController(EnhancerShareService enhancerShareService) {
        this.enhancerShareService = enhancerShareService;
    }

    @GetMapping("/enhancer/{enhancerId}")
    public EnhancerShareDTO getEnhancerShare(@PathVariable Long enhancerId){
        return EnhancerShare.transfer(enhancerShareService.getEnhancerShare(enhancerId));
    }

    @GetMapping("/knode/{knodeId}/enhancer")
    public List<EnhancerShareDTO> getOwnedEnhancerShare(@PathVariable Long knodeId){
        return EnhancerShare.transfer(enhancerShareService.getOwnedEnhancerShare(knodeId));
    }

    @GetMapping("/knode/{knodeId}/similar/enhancer")
    public Object getRelatedEnhancerShare(
            @PathVariable Long knodeId,
            @RequestParam("knodeCount") Long knodeCount,
            @RequestParam("withMapping") Boolean withMapping){
        return withMapping ?
                enhancerShareService.getRelatedEnhancerShareWithMapping(knodeId, knodeCount) :
                EnhancerShare.transfer(enhancerShareService.getRelatedEnhancerShare(knodeId, knodeCount));
    }

    @PostMapping("/enhancerShare/{shareId}/to/{targetId}")
    public EnhancerDTO forkEnhancerShare(@PathVariable Long shareId, @PathVariable Long targetId){
        return enhancerShareService.forkEnhancerShare(shareId, targetId);
    }

}
