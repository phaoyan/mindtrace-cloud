package pers.juumii;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/mq/knode/remove")
    public void handleRemoveKnode(@RequestBody String data){
        consumer.handleRemoveKnode(data);
    }

    @PostMapping("/mq/enhancer/remove")
    public void handleRemoveEnhancer(@RequestBody String data){
        consumer.handleRemoveEnhancer(data);
    }

    @PostMapping("/mq/resource/remove")
    public void handleRemoveResource(@RequestBody String data){
        consumer.handleRemoveResource(data);
    }

}
