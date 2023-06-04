package pers.juumii.controller;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugController {

    @NacosValue("tencent.cos.bucket.name")
    private String BUCKET_NAME;

    @GetMapping("/debug")
    public String debug(){
        return BUCKET_NAME;
    }
}
