package pers.juumii.controller;

import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pers.juumii.data.Resource;
import pers.juumii.dto.ResourceDTO;
import pers.juumii.dto.ResourceWithData;
import pers.juumii.service.ResourceService;

import java.io.IOException;
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
            @PathVariable String dataName) throws InterruptedException {
        Thread.sleep(1000);
        return resourceService.getDataFromResource( resourceId, dataName);
    }

    @PostMapping("/resource/{resourceId}/data")
    public void addDataToResource(
            @PathVariable Long resourceId,
            @RequestBody Map<String, Object> data){
        resourceService.addDataToResource(resourceId, data);
    }

    @PostMapping("/resource/{resourceId}/data/{dataName}/file")
    public void addDataToResource(
            @PathVariable Long resourceId,
            @PathVariable String dataName,
            @RequestParam MultipartFile file) throws IOException {
        resourceService.addDataToResource(resourceId, dataName, file.getInputStream());
    }

    @PostMapping("/resource/{resourceId}/data/{dataName}/data")
    public void addDataToResource(
            @PathVariable Long resourceId,
            @PathVariable String dataName,
            @RequestBody Object data){
        resourceService.addDataToResource(resourceId, dataName, data);
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
    public List<ResourceDTO> getResourcesOfEnhancer(@PathVariable Long enhancerId){
        return Resource.transfer(resourceService.getResourcesOfEnhancer(enhancerId));
    }

    @GetMapping("/knode/{knodeId}/resource")
    public List<ResourceDTO> getResourcesOfKnode(@PathVariable Long knodeId){
        return Resource.transfer(resourceService.getResourcesOfKnode(knodeId));
    }

}
