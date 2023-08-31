package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pers.juumii.config.MqEventRegistration;

@RestController
public class MqController{

    private final MqEventRegistration registration;

    @Autowired
    public MqController(MqEventRegistration registration) {
        this.registration = registration;
    }

    @PostMapping("/mq/knode/add")
    public void handleAddKnode(@RequestBody String data){
        registration.handleAddKnode(data);
    }
}
