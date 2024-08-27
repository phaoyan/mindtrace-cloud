package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pers.juumii.service.SerializeService;

@RestController
public class SerializeController {

    private final SerializeService serializeService;

    @Autowired
    public SerializeController(SerializeService serializeService) {
        this.serializeService = serializeService;
    }


    @GetMapping("/knode/{knodeId}/content")
    public ResponseEntity<byte[]> serializeContent(@PathVariable Long knodeId){
        return serializeService.serializeContentsToMarkdown(knodeId);
    }

    @GetMapping("/enhancer/{enhancerId}/content")
    public ResponseEntity<byte[]> serializeEnhancerContent(@PathVariable Long enhancerId){
        return serializeService.serializeEnhancerContent(enhancerId);
    }

    @GetMapping("/enhancer-group/{groupId}/content")
    public ResponseEntity<byte[]> serializeEnhancerGroupContent(@PathVariable Long groupId){
        return serializeService.serializeEnhancerGroupContent(groupId);
    }



}
