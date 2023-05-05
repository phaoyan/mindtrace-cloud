package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.service.AuthService;

@RestController
public class AuthenticationController {

    private final AuthService authService;

    @Autowired
    public AuthenticationController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/user/{userId}/public")
    public Boolean isUserPublic(@PathVariable Long userId){
        return authService.isUserPublic(userId);
    }

    @GetMapping("/knode/{knodeId}/public")
    public Boolean isKnodePublic(@PathVariable Long knodeId){
        return authService.isKnodePublic(knodeId);
    }

    @GetMapping("/enhancer/{enhancerId}/public")
    public Boolean isEnhancerPublic(@PathVariable Long enhancerId){
        return authService.isEnhancerPublic(enhancerId);
    }

    @GetMapping("/resource/{resourceId}/public")
    public Boolean isResourcePublic(@PathVariable Long resourceId){
        return authService.isResourcePublic(resourceId);
    }
}
