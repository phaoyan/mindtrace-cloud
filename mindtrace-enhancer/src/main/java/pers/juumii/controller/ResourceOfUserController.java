package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.aop.ControllerAspect;
import pers.juumii.data.Resource;
import pers.juumii.service.ResourceService;

import java.util.Map;

@RestController
@RequestMapping("user/{userId}/resource")
public class ResourceOfUserController {

    private final ControllerAspect aspect;
    private final ResourceService resourceService;

    @Autowired
    public ResourceOfUserController(ControllerAspect aspect, ResourceService resourceService) {
        this.aspect = aspect;
        this.resourceService = resourceService;
    }

    @GetMapping("/{resourceId}/meta")
    public Object getResourceMetadata(
            @PathVariable Long userId,
            @PathVariable Long resourceId){
        aspect.checkResourceAvailability(userId, resourceId);
        return resourceService.getResourceMetadata(userId,resourceId);
    }

    @GetMapping("/{resourceId}")
    public Object getDataFromResource(
            @PathVariable Long userId,
            @PathVariable Long resourceId){
        aspect.checkResourceAvailability(userId, resourceId);
        return resourceService.getDataFromResource(userId, resourceId);
    }

    @GetMapping("/{resourceId}/data/{dataName}")
    public Object getDataFromResource(
            @PathVariable Long userId,
            @PathVariable Long resourceId,
            @PathVariable String dataName){
        aspect.checkResourceAvailability(userId, resourceId);
        return resourceService.getDataFromResource(userId, resourceId, dataName);
    }

    @PutMapping
    public Object addResourceToUser(
            @PathVariable Long userId,
            @RequestParam("meta") Resource meta,
            @RequestBody Map<String, Object> data){
        aspect.checkUserExistence(userId);
        return resourceService.addResourceToUser(userId, meta, data);
    }

    @PostMapping("/{resourceId}")
    public Object addDataToResource(
            @PathVariable Long userId,
            @PathVariable Long resourceId,
            @RequestBody Map<String, Object> data){
        aspect.checkResourceAvailability(userId, resourceId);
        return resourceService.addDataToResource(userId, resourceId, data);
    }

}
