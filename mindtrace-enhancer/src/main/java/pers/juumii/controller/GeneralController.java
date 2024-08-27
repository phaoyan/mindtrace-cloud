package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pers.juumii.feign.ChatClient;


@RestController
public class GeneralController {

    private final ChatClient chatClient;

    @Autowired
    public GeneralController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/hello")
    public String hello(){
        return "hello mindtrace-enhancer";
    }

    @PostMapping("/chat")
    public String getResponse(@RequestBody String json){
        return chatClient.getResponse(json);
    }

}
