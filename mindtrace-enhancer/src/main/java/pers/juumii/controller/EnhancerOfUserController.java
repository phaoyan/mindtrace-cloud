package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.aop.ControllerAspect;
import pers.juumii.data.Enhancer;
import pers.juumii.service.EnhancerService;

@RestController
@RequestMapping("user/{userId}/enhancer")
public class EnhancerOfUserController {

    private final ControllerAspect aspect;
    private final EnhancerService enhancerService;

    @Autowired
    public EnhancerOfUserController(ControllerAspect aspect, EnhancerService enhancerService) {
        this.aspect = aspect;
        this.enhancerService = enhancerService;
    }

    @GetMapping
    public Object getAllEnhancersFromUser(@PathVariable Long userId){
        aspect.checkUserExistence(userId);
        return enhancerService.getAllEnhancersFromUser(userId);
    }

    @GetMapping("/{enhancerId}")
    public Object getEnhancerFromUser(
            @PathVariable Long userId,
            @PathVariable Long enhancerId){
        aspect.checkEnhancerAvailability(userId,enhancerId);
        return enhancerService.getEnhancerFromUser(userId, enhancerId);
    }

    @PutMapping
    public Object addEnhancerToUser(@PathVariable Long userId){
        aspect.checkUserExistence(userId);
        return enhancerService.addEnhancerToUser(userId);
    }

    @PostMapping("/{enhancerId}")
    public Object updateEnhancerOfUser(
            @PathVariable Long userId,
            @PathVariable Long enhancerId,
            @RequestBody Enhancer updated){
        aspect.checkEnhancerAvailability(userId, enhancerId);
        return enhancerService.updateEnhancerOfUser(userId, enhancerId, updated);
    }

    @DeleteMapping("/{enhancerId}")
    public Object removeEnhancerFromUser(
            @PathVariable Long userId,
            @PathVariable Long enhancerId){
        aspect.checkEnhancerAvailability(userId,enhancerId);
        return enhancerService.removeEnhancerFromUser(userId, enhancerId);
    }

}
