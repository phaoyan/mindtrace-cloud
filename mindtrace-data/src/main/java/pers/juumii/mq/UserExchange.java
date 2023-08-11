package pers.juumii.mq;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserExchange {
    public static final String USER_EVENT_EXCHANGE = "user_event_exchange";
    public static final String ROUTING_KEY_REGISTER = "register";

    @Bean
    public DirectExchange userEventExchange(){
        return new DirectExchange(USER_EVENT_EXCHANGE);
    }

}
