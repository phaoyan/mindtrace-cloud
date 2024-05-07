package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pers.juumii.config.MqEventRegistration;

@RestController
public class MqController {

    private final MqEventRegistration consumer;

    @Autowired
    public MqController(MqEventRegistration consumer) {
        this.consumer = consumer;
    }

    @PostMapping("/mq/knode/update")
    public void handleUpdateKnode(@RequestBody String data){
        consumer.handleUpdateKnode(data);
    }
}
