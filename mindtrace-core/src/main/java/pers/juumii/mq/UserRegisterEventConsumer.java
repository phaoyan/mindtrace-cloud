package pers.juumii.mq;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.juumii.service.UserService;

@Component
public class UserRegisterEventConsumer {

    private final UserService userService;

    @Autowired
    public UserRegisterEventConsumer(UserService userService) {
        this.userService = userService;
    }

    @RabbitListener(queues = UserExchange.REGISTER_EVENT_MQ)
    public void handleUserRegister(String message){
        userService.register(Convert.toLong(message));
    }
}
