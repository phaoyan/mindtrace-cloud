package pers.juumii.controller;

import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pers.juumii.data.KnodeInfoCollection;
import pers.juumii.service.SerializeService;

import java.nio.charset.StandardCharsets;

@RestController
public class SerializeController {

    private final SerializeService serializeService;

    @Autowired
    public SerializeController(SerializeService serializeService) {
        this.serializeService = serializeService;
    }

    @GetMapping("/knode/{knodeId}/all")
    public ResponseEntity<byte[]> serializeAll(@PathVariable Long knodeId){
        return serializeService.serializeAll(knodeId);
    }

    @GetMapping("/knode/{knodeId}/content")
    public ResponseEntity<byte[]> serializeContent(@PathVariable Long knodeId){
        return serializeService.serializeContentsToMarkdown(knodeId);
    }



}
