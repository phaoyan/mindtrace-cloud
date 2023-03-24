package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.Enhancer;
import pers.juumii.dto.EnhancerDTO;
import pers.juumii.service.EnhancerService;
import pers.juumii.utils.SaResult;

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
        return enhancerService.query(userId, id);
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
