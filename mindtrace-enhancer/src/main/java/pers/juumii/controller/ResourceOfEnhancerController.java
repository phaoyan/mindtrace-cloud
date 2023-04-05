package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.aop.ControllerAspect;
import pers.juumii.data.Resource;
import pers.juumii.service.ResourceService;

import java.util.Map;

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
            @RequestParam("meta") Resource meta,
            @RequestBody Map<String, Object> data){
        aspect.checkEnhancerAvailability(userId, enhancerId);
        return resourceService.addResourceToEnhancer(userId, enhancerId, meta, data);
    }

    @GetMapping
    public Object getResourcesFromEnhancer(
            @PathVariable Long userId,
            @PathVariable Long enhancerId){
        aspect.checkEnhancerAvailability(userId, enhancerId);
        return resourceService.getResourcesFromEnhancer(userId, enhancerId);
    }

}
