package pers.juumii.controller;

import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.juumii.feign.GlobalClient;

@RestController
public class GeneralController {

    private final GlobalClient client;

    @Autowired
    public GeneralController(GlobalClient client) {
        this.client = client;
    }

    @GetMapping("/hello")
    public String hello(){
        return "hello mindtrace-enhancer";
    }

    @GetMapping("/echo/core")
    public SaResult echoWithCore(){
        return client.echo();
    }
}
