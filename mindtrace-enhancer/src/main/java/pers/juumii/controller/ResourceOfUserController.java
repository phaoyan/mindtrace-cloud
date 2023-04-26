package pers.juumii.controller;

import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.controller.aop.ControllerAspect;
import pers.juumii.dto.ResourceWithData;
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
        return resourceService.getResourceMetadata(resourceId);
    }

    @GetMapping("/{resourceId}/data")
    public Object getDataFromResource(
            @PathVariable Long userId,
            @PathVariable Long resourceId){
        aspect.checkResourceAvailability(userId, resourceId);
        return resourceService.getDataFromResource(resourceId);
    }

    @GetMapping("/{resourceId}/data/{dataName}")
    public Object getDataFromResource(
            @PathVariable Long userId,
            @PathVariable Long resourceId,
            @PathVariable String dataName){
        aspect.checkResourceAvailability(userId, resourceId);
        return resourceService.getDataFromResource( resourceId, dataName);
    }

    @PutMapping
    public Object addResourceToUser(
            @PathVariable Long userId,
            @RequestBody ResourceWithData params){
        aspect.checkUserExistence(userId);
        return resourceService.addResourceToUser(userId, params.getMeta(), params.getData());
    }

    @PostMapping("/{resourceId}/data")
    public Object addDataToResource(
            @PathVariable Long userId,
            @PathVariable Long resourceId,
            @RequestBody Map<String, Object> data){
        aspect.checkResourceAvailability(userId, resourceId);
        return resourceService.addDataToResource(resourceId, data);
    }

    @DeleteMapping("/{resourceId}")
    public Object removeResourceFromUser(
            @PathVariable Long userId,
            @PathVariable Long resourceId){
        aspect.checkResourceAvailability(userId, resourceId);
        resourceService.removeResourceFromUser(resourceId);
        return SaResult.ok();
    }

}
