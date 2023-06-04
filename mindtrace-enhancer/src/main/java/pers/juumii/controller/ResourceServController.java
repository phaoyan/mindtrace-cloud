package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.juumii.dto.UnfoldingKnodeData;
import pers.juumii.service.ResourceDataService;

import java.util.List;

@RestController
public class ResourceServController {

    private final ResourceDataService resourceDataService;

    @Autowired
    public ResourceServController(ResourceDataService resourceDataService) {
        this.resourceDataService = resourceDataService;
    }


    @GetMapping("/resource/serv/unfolding")
    public List<UnfoldingKnodeData> getUnfoldingKnodeData(@RequestParam("rootId") Long rootId){
        return resourceDataService.getUnfoldingKnodeData(rootId);
    }

}
