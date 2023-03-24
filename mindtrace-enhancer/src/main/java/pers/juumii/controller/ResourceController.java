package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pers.juumii.dto.ResourceDTO;
import pers.juumii.service.ResourceService;
import pers.juumii.utils.SaResult;

@RestController
@RequestMapping("user/{userId}/resource")
public class ResourceController {

    private final ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping("/{id}")
    public SaResult fetch(
            @PathVariable Long id,
            @PathVariable Long userId){
        return resourceService.fetch(id,userId);
    }

    @PutMapping
    public SaResult put(
            @PathVariable Long userId,
            @RequestParam("metadata") ResourceDTO meta,
            @RequestBody MultipartFile file){
        return resourceService.put(userId, file, meta);
    }

    @PostMapping("/{id}")
    public SaResult modify(
            @PathVariable Long id,
            @PathVariable Long userId,
            @RequestBody ResourceDTO meta){
        return resourceService.modify(id, userId, meta);
    }

    @PostMapping("/{id}")
    public SaResult alterSource(
            @PathVariable Long id,
            @PathVariable Long userId,
            @RequestBody MultipartFile source){
        return resourceService.alterSource(id, userId, source);
    }

    @DeleteMapping("/{id}")
    public SaResult disconnect(
            @PathVariable Long id,
            @PathVariable Long userId){
        return resourceService.disconnect(id, userId);
    }

}
