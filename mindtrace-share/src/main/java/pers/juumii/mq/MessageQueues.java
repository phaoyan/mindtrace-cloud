package pers.juumii.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageQueues {

    @Bean
    public Binding removeKnodeEventBinding(
            @Qualifier("knodeEventExchange") DirectExchange ex,
            @Qualifier("removeKnodeEventMQ") Queue mq){
        return BindingBuilder.bind(mq).to(ex).with(KnodeExchange.ROUTING_KEY_REMOVE_KNODE);
    }
    @Bean
    public Queue removeKnodeEventMQ(){
        return new Queue(REMOVE_KNODE_EVENT_MQ);
    }
    public static final String REMOVE_KNODE_EVENT_MQ = "share.knode.remove";

    @Bean
    public Binding removeEnhancerEventBinding(
            @Qualifier("knodeEventExchange") DirectExchange ex,
            @Qualifier("removeEnhancerEventMQ") Queue mq){
        return BindingBuilder.bind(mq).to(ex).with(KnodeExchange.ROUTING_KEY_REMOVE_ENHANCER);
    }
    @Bean
    public Queue removeEnhancerEventMQ(){
        return new Queue(REMOVE_ENHANCER_EVENT_MQ);
    }
    public static final String REMOVE_ENHANCER_EVENT_MQ = "share.enhancer.remove";


    @Bean
    public Binding removeResourceEventBinding(
            @Qualifier("knodeEventExchange") DirectExchange ex,
            @Qualifier("removeResourceEventMQ") Queue mq){
        return BindingBuilder.bind(mq).to(ex).with(KnodeExchange.ROUTING_KEY_REMOVE_RESOURCE);
    }
    @Bean
    public Queue removeResourceEventMQ(){
        return new Queue(REMOVE_RESOURCE_EVENT_MQ);
    }
    public static final String REMOVE_RESOURCE_EVENT_MQ = "share.resource.remove";
}
