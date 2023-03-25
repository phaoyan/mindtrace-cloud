package pers.juumii.controller;

import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeneralController {

    @GetMapping("/hello")
    public Object hello(){
        return SaResult.ok("hello mindtrace-core");
    }
}
