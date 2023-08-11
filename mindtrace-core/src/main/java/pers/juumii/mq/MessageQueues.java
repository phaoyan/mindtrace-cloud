package pers.juumii.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageQueues {

    public static final String REGISTER_EVENT_MQ = "core.user.register";

    @Bean
    public Queue registerEventMQ(){
        return new Queue(REGISTER_EVENT_MQ);
    }
    @Bean
    public Binding registerEventBinding(DirectExchange userEventExchange, Queue registerEventMQ){
        return BindingBuilder.bind(registerEventMQ).to(userEventExchange).with(UserExchange.ROUTING_KEY_REGISTER);
    }
}
