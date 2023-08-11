package pers.juumii.mq;


import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KnodeExchange {


    @Bean
    public DirectExchange knodeEventExchange(){
        return new DirectExchange(KNODE_EVENT_EXCHANGE);
    }
    public static final String KNODE_EVENT_EXCHANGE = "knode_event_exchange";
    public static final String ROUTING_KEY_UPDATE_KNODE = "update_knode";
    public static final String ROUTING_KEY_ADD_KNODE = "add_knode";
    public static final String ROUTING_KEY_ADD_ENHANCER = "add_enhancer";
    public static final String ROUTING_KEY_ADD_RESOURCE = "add_resource";
    public static final String ROUTING_KEY_REMOVE_KNODE = "remove_knode";
    public static final String ROUTING_KEY_REMOVE_ENHANCER = "remove_enhancer";
    public static final String ROUTING_KEY_REMOVE_RESOURCE = "remove_resource";



}