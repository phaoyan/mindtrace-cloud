package pers.juumii.mq;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KnodeExchange {

    public static final String ROUTING_KEY_UPDATE = "update";
    public static final String KNODE_EVENT_EXCHANGE = "knode_event_exchange";

    @Bean
    public DirectExchange knodeEventExchange(){
        return new DirectExchange(KNODE_EVENT_EXCHANGE);
    }
    @Bean
    public Queue updateEventMQ(){
        return new Queue("update_event_mq");
    }
    @Bean
    public Binding updateEventBinding(DirectExchange knodeEventExchange, Queue updateEventMQ){
        return BindingBuilder.bind(updateEventMQ).to(knodeEventExchange).with(ROUTING_KEY_UPDATE);
    }

}