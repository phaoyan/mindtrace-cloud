package pers.juumii.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pers.juumii.data.Enhancer;
import pers.juumii.data.Resource;
import pers.juumii.dto.IdPair;
import pers.juumii.dto.ResourceDTO;
import pers.juumii.service.EnhancerService;
import pers.juumii.service.ResourceService;
import pers.juumii.utils.AuthUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
public class ResourceController {

    private final ResourceService resourceService;
    private final EnhancerService enhancerService;
    private final AuthUtils authUtils;
    @NacosValue(value = "${tencent.cos.bucket.name}", autoRefreshed = true)
    private String COS_BUCKET_NAME;
    @NacosValue(value = "${tencent.cos.region}", autoRefreshed = true)
    private String COS_REGION;

    @Autowired
    public ResourceController(
            ResourceService resourceService,
            EnhancerService enhancerService,
            AuthUtils authUtils) {
        this.resourceService = resourceService;
        this.enhancerService = enhancerService;
        this.authUtils = authUtils;
    }

    private void enhancerSameUser(Long enhancerId){
        Enhancer enhancer = enhancerService.getEnhancerById(enhancerId);
        authUtils.same(enhancer.getCreateBy());
    }

    private void resourceSameUser(Long resourceId){
        Resource resource = resourceService.getResource(resourceId);
        authUtils.same(Convert.toLong(resource.getCreateBy()));
    }

    @PostMapping("/enhancer/{enhancerId}/resource")
    public ResourceDTO addResource(@PathVariable Long enhancerId, @RequestBody ResourceDTO dto){
        enhancerSameUser(enhancerId);
        return Resource.transfer(resourceService.addResourceToEnhancer(enhancerId, dto));
    }

    @PutMapping("/resource")
    public ResourceDTO addResource(@RequestParam(required = false) String type){
        return Resource.transfer(resourceService.addResource(StpUtil.getLoginIdAsLong(), type));
    }

    @PutMapping("/resource/{resourceId}/title")
    public void editTitle(@PathVariable Long resourceId, @RequestParam(required = false) String title){
        resourceSameUser(resourceId);
        resourceService.editTitle(resourceId, title);
    }
    @PutMapping("/resource/{resourceId}/type")
    public void editType(@PathVariable Long resourceId, @RequestParam(required = false) String type){
        resourceSameUser(resourceId);
        resourceService.editType(resourceId, type);
    }
    @PutMapping("/resource/{resourceId}/createTime")
    public void editCreateTime(@PathVariable Long resourceId, @RequestParam(required = false) String createTime){
        resourceSameUser(resourceId);
        resourceService.editCreateTime(resourceId, createTime);
    }
    @GetMapping("/resource/{resourceId}/meta")
    public ResourceDTO getResource(@PathVariable Long resourceId){
        return Resource.transfer(resourceService.getResource(resourceId));
    }

    @GetMapping("/resource/{resourceId}/data")
    public Map<String, byte[]> getDataFromResource(@PathVariable Long resourceId){
        return resourceService.getDataFromResource(resourceId);
    }

    @PostMapping("/batch/resource/data")
    public List<Map<String, byte[]>> getDataFromResourceBatch(@RequestBody List<Long> resourceIds){
        return resourceIds.stream().map(resourceService::getDataFromResource).toList();
    }

    @GetMapping("/resource/{resourceId}/data/{dataName}")
    public byte[] getDataFromResource(
            @PathVariable Long resourceId,
            @PathVariable String dataName) {
        return resourceService.getDataFromResource( resourceId, dataName);
    }

    @GetMapping("/resource/{resourceId}/data/{dataName}/file")
    public ResponseEntity<byte[]> getDataFromResourceAsFile(
            @PathVariable Long resourceId,
            @PathVariable String dataName,
            @RequestParam String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
        return ResponseEntity.ok()
                .headers(headers)
                .body(resourceService.getDataFromResource( resourceId, dataName));
    }

    @GetMapping("/resource/{resourceId}/data/{dataName}/url")
    public String getCosResourceUrl(
            @PathVariable Long resourceId,
            @PathVariable String dataName){
        Resource resource = resourceService.getResource(resourceId);
        return "https://" + COS_BUCKET_NAME + ".cos." + COS_REGION + ".myqcloud.com/" + resource.getCreateBy() + "/" + resourceId + "/" + dataName;
    }

    @PutMapping("/resource/{resourceId}/data")
    public void addDataToResource(
            @PathVariable Long resourceId,
            @RequestBody Map<String, byte[]> data){
        resourceSameUser(resourceId);
        resourceService.addDataToResource(resourceId, data);
    }

    @PostMapping("/resource/{resourceId}/data/{dataName}/file")
    public String addDataToResource(
            @PathVariable Long resourceId,
            @PathVariable String dataName,
            @RequestParam MultipartFile file) throws IOException {
        resourceSameUser(resourceId);
        resourceService.addDataToResource(resourceId, dataName, IoUtil.readBytes(file.getInputStream()));
        return "https://" + COS_BUCKET_NAME + ".cos." + COS_REGION + ".myqcloud.com/" + StpUtil.getLoginIdAsLong() + "/" + resourceId + "/" + dataName;
    }

    @PostMapping("/resource/{resourceId}/data/{dataName}")
    public void addDataToResource(
            @PathVariable Long resourceId,
            @PathVariable String dataName,
            @RequestBody String data){
        resourceSameUser(resourceId);
        resourceService.addDataToResource(resourceId, dataName, data.getBytes(StandardCharsets.UTF_8));
    }

    @DeleteMapping("/resource/{resourceId}")
    public void removeResource(@PathVariable Long resourceId){
        resourceSameUser(resourceId);
        resourceService.removeResource(resourceId);
    }

    @GetMapping("/enhancer/{enhancerId}/resource")
    public List<ResourceDTO> getResourcesOfEnhancer(@PathVariable Long enhancerId){
        return Resource.transfer(resourceService.getResourcesOfEnhancer(enhancerId));
    }

    @PostMapping("/batch/enhancer/resource")
    public List<ResourceDTO> getResourceOfEnhancerBatch(@RequestBody List<Long> enhancerIds){
        return enhancerIds.stream().map(resourceService::getResourcesOfEnhancer)
                .flatMap(Collection::stream)
                .map(Resource::transfer)
                .toList();
    }

    @GetMapping("/knode/{knodeId}/resource")
    public List<ResourceDTO> getResourcesOfKnode(@PathVariable Long knodeId){
        return Resource.transfer(resourceService.getResourcesOfKnode(knodeId));
    }

    @PostMapping("/rel/enhancer/resource")
    public List<IdPair> getEnhancerResourceRels(@RequestBody List<Long> enhancerIds){
        return resourceService.getEnhancerResourceRels(enhancerIds);
    }

    @PutMapping("/rel/enhancer/resource")
    public void addEnhancerResourceRel(@RequestParam Long enhancerId, @RequestParam Long resourceId){
        resourceSameUser(resourceId);
        enhancerSameUser(enhancerId);
        resourceService.connectResourceToEnhancer(enhancerId, resourceId);
    }

    @PostMapping("/rel/enhancer/resource/index")
    public void setResourceIndexInEnhancer(
            @RequestParam Long enhancerId,
            @RequestParam Long resourceId,
            @RequestParam Integer index){
        enhancerSameUser(enhancerId);
        resourceSameUser(resourceId);
        resourceService.setResourceIndexInEnhancer(enhancerId, resourceId, index);
    }

}
