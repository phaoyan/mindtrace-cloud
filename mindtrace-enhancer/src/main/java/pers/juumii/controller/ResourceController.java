package pers.juumii.controller;

import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.Resource;
import pers.juumii.dto.ResourceDTO;
import pers.juumii.dto.ResourceWithData;
import pers.juumii.service.ResourceService;

import java.util.List;
import java.util.Map;

@RestController
public class ResourceController {

    private final ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping("/resource/{resourceId}/meta")
    public ResourceDTO getResourceMetadata(@PathVariable Long resourceId){
        return Resource.transfer(resourceService.getResourceMetadata(resourceId));
    }

    @GetMapping("/resource/{resourceId}/data")
    public Map<String, Object> getDataFromResource(@PathVariable Long resourceId){
        return resourceService.getDataFromResource(resourceId);
    }

    @GetMapping("/resource/{resourceId}/data/{dataName}")
    public Object getDataFromResource(
            @PathVariable Long resourceId,
            @PathVariable String dataName){
        return resourceService.getDataFromResource( resourceId, dataName);
    }

    @PostMapping("/resource/{resourceId}/data")
    public SaResult addDataToResource(
            @PathVariable Long resourceId,
            @RequestBody Map<String, Object> data){
        return resourceService.addDataToResource(resourceId, data);
    }

    @DeleteMapping("/resource/{resourceId}")
    public SaResult removeResource(@PathVariable Long resourceId){
        resourceService.removeResource(resourceId);
        return SaResult.ok();
    }

    @PutMapping("/enhancer/{enhancerId}/resource")
    public ResourceDTO addResourceToEnhancer(
            @PathVariable Long enhancerId,
            @RequestBody ResourceWithData params){
        return Resource.transfer(resourceService.addResourceToEnhancer(enhancerId, params.getMeta(), params.getData()));
    }

    @GetMapping("/enhancer/{enhancerId}/resource")
    public List<ResourceDTO> getResourcesFromEnhancer(
            @PathVariable Long enhancerId){
        return Resource.transfer(resourceService.getResourcesFromEnhancer(enhancerId));
    }

}
