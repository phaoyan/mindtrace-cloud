package pers.juumii.controller;

import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.dto.EnhancerDTO;
import pers.juumii.service.EnhancerService;

@RestController
@RequestMapping("user/{userId}/enhancer")
public class EnhancerController {

    private final EnhancerService enhancerService;

    @Autowired
    public EnhancerController(EnhancerService enhancerService) {
        this.enhancerService = enhancerService;
    }

    @GetMapping("/{id}")
    public SaResult query(
            @PathVariable Long userId,
            @PathVariable Long id){
        return enhancerService.queryByUserId(userId, id);
    }

    @PutMapping
    public SaResult create(
            @PathVariable Long userId,
            @RequestBody EnhancerDTO dto){
        return enhancerService.create(userId, dto);
    }

    @PostMapping("/{id}")
    public SaResult update(
            @PathVariable Long userId,
            @PathVariable Long id,
            @RequestBody EnhancerDTO dto){
        return enhancerService.update(userId, id, dto);
    }

    @DeleteMapping("/{id}")
    public SaResult delete(
            @PathVariable Long userId,
            @PathVariable Long id){
        return enhancerService.delete(userId, id);
    }

}
