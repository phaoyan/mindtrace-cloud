package pers.juumii.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserExchange {
    public static final String ROUTING_KEY_REGISTER = "register";
    public static final String USER_EVENT_EXCHANGE = "user_event_exchange";
    public static final String REGISTER_EVENT_MQ = "register_event_mq";

    @Bean
    public DirectExchange userEventExchange(){return new DirectExchange(USER_EVENT_EXCHANGE);}
    @Bean
    public Queue registerEventMQ(){
        return new Queue(REGISTER_EVENT_MQ);
    }
    @Bean
    public Binding registerEventBinding(DirectExchange userEventExchange, Queue registerEventMQ){
        return BindingBuilder.bind(registerEventMQ).to(userEventExchange).with(ROUTING_KEY_REGISTER);
    }
}
