package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.aop.ControllerAspect;
import pers.juumii.dto.ResourceWithData;
import pers.juumii.service.ResourceService;

@RestController
@RequestMapping("user/{userId}/enhancer/{enhancerId}/resource")
public class ResourceOfEnhancerController {

    private final ResourceService resourceService;
    private final ControllerAspect aspect;

    @Autowired
    public ResourceOfEnhancerController(ResourceService resourceService, ControllerAspect aspect) {
        this.resourceService = resourceService;
        this.aspect = aspect;
    }

    @PutMapping
    public Object addResourceToEnhancer(
            @PathVariable Long userId,
            @PathVariable Long enhancerId,
            @RequestBody ResourceWithData params){
        aspect.checkEnhancerAvailability(userId, enhancerId);
        return resourceService.addResourceToEnhancer(enhancerId, params.getMeta(), params.getData());
    }

    @GetMapping
    public Object getResourcesFromEnhancer(
            @PathVariable Long userId,
            @PathVariable Long enhancerId){
        aspect.checkEnhancerAvailability(userId, enhancerId);
        return resourceService.getResourcesFromEnhancer(enhancerId);
    }



}
