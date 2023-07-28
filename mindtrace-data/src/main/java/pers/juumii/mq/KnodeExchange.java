package pers.juumii.mq;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KnodeExchange {

    @Bean
    public DirectExchange knodeEventExchange(){
        return new DirectExchange(KNODE_EVENT_EXCHANGE);
    }
    public static final String KNODE_EVENT_EXCHANGE = "knode_event_exchange";


    @Bean
    public Binding updateKnodeEventBinding(
            @Qualifier("knodeEventExchange") DirectExchange ex,
            @Qualifier("updateKnodeEventMQ") Queue mq){
        return BindingBuilder.bind(mq).to(ex).with(ROUTING_KEY_UPDATE_KNODE);
    }
    @Bean
    public Queue updateKnodeEventMQ(){
        return new Queue(UPDATE_KNODE_EVENT_MQ);
    }
    public static final String ROUTING_KEY_UPDATE_KNODE = "update_knode";
    public static final String UPDATE_KNODE_EVENT_MQ = "update_knode_event_mq";


    @Bean
    public Binding updateEnhancerEventBinding(
            @Qualifier("knodeEventExchange") DirectExchange ex,
            @Qualifier("updateEnhancerEventMQ") Queue mq){
        return BindingBuilder.bind(mq).to(ex).with(ROUTING_KEY_UPDATE_ENHANCER);
    }
    @Bean
    public Queue updateEnhancerEventMQ(){ return new Queue(UPDATE_ENHANCER_EVENT_MQ);}
    public static final String ROUTING_KEY_UPDATE_ENHANCER = "update_enhancer";
    public static final String UPDATE_ENHANCER_EVENT_MQ = "update_enhancer_event_mq";


    @Bean
    public Binding updateResourceEventBinding(
            @Qualifier("knodeEventExchange") DirectExchange ex,
            @Qualifier("updateResourceEventMQ") Queue mq){
        return BindingBuilder.bind(mq).to(ex).with(ROUTING_KEY_UPDATE_RESOURCE);
    }
    @Bean
    public Queue updateResourceEventMQ(){ return new Queue(UPDATE_RESOURCE_EVENT_MQ);}
    public static final String ROUTING_KEY_UPDATE_RESOURCE = "update_resource";
    public static final String UPDATE_RESOURCE_EVENT_MQ = "update_resource_event_mq";


    @Bean
    public Binding addKnodeEventBinding(
            @Qualifier("knodeEventExchange") DirectExchange ex,
            @Qualifier("addKnodeEventMQ") Queue mq){
        return BindingBuilder.bind(mq).to(ex).with(ROUTING_KEY_ADD_KNODE);
    }
    @Bean
    public Queue addKnodeEventMQ(){ return new Queue(ADD_KNODE_EVENT_MQ);}
    public static final String ROUTING_KEY_ADD_KNODE = "add_knode";
    public static final String ADD_KNODE_EVENT_MQ = "add_knode_event_mq";


    @Bean
    public Binding addEnhancerEventBinding(
            @Qualifier("knodeEventExchange") DirectExchange ex,
            @Qualifier("addEnhancerEventMQ") Queue mq){
        return BindingBuilder.bind(mq).to(ex).with(ROUTING_KEY_ADD_ENHANCER);
    }
    @Bean
    public Queue addEnhancerEventMQ(){ return new Queue(ADD_ENHANCER_EVENT_MQ);}
    public static final String ROUTING_KEY_ADD_ENHANCER = "add_enhancer";
    public static final String ADD_ENHANCER_EVENT_MQ = "add_enhancer_event_mq";


    @Bean
    public Binding addResourceEventBinding(
            @Qualifier("knodeEventExchange") DirectExchange ex,
            @Qualifier("addResourceEventMQ") Queue mq){
        return BindingBuilder.bind(mq).to(ex).with(ROUTING_KEY_ADD_RESOURCE);
    }
    @Bean
    public Queue addResourceEventMQ(){ return new Queue(ADD_RESOURCE_EVENT_MQ);}
    public static final String ROUTING_KEY_ADD_RESOURCE = "add_resource";
    public static final String ADD_RESOURCE_EVENT_MQ = "add_resource_event_mq";

    @Bean
    public Binding removeKnodeEventBinding(
            @Qualifier("knodeEventExchange") DirectExchange ex,
            @Qualifier("removeKnodeEventMQ") Queue mq){
        return BindingBuilder.bind(mq).to(ex).with(ROUTING_KEY_REMOVE_KNODE);
    }
    @Bean
    public Queue removeKnodeEventMQ(){
        return new Queue(REMOVE_KNODE_EVENT_MQ);
    }
    public static final String ROUTING_KEY_REMOVE_KNODE = "remove_knode";
    public static final String REMOVE_KNODE_EVENT_MQ = "remove_knode_event_mq";


    @Bean
    public Binding removeEnhancerEventBinding(
            @Qualifier("knodeEventExchange") DirectExchange ex,
            @Qualifier("removeEnhancerEventMQ") Queue mq){
        return BindingBuilder.bind(mq).to(ex).with(ROUTING_KEY_REMOVE_ENHANCER);
    }
    @Bean
    public Queue removeEnhancerEventMQ(){
        return new Queue(REMOVE_ENHANCER_EVENT_MQ);
    }
    public static final String ROUTING_KEY_REMOVE_ENHANCER = "remove_enhancer";
    public static final String REMOVE_ENHANCER_EVENT_MQ = "remove_enhancer_event_mq";


    @Bean
    public Binding disconnectEnhancerFromKnodeEventBinding(
            @Qualifier("knodeEventExchange") DirectExchange ex,
            @Qualifier("disconnectEnhancerFromKnodeEventMQ") Queue mq){
        return BindingBuilder.bind(mq).to(ex).with(ROUTING_KEY_DISCONNECT_ENHANCER_FROM_KNODE);
    }
    @Bean
    public Queue disconnectEnhancerFromKnodeEventMQ(){
        return new Queue(DISCONNECT_ENHANCER_FROM_KNODE_MQ);
    }
    public static final String ROUTING_KEY_DISCONNECT_ENHANCER_FROM_KNODE = "disconnect_enhancer_from_knode";
    public static final String DISCONNECT_ENHANCER_FROM_KNODE_MQ = "disconnect_enhancer_from_knode_mq";


    @Bean
    public Binding removeResourceEventBinding(
            @Qualifier("knodeEventExchange") DirectExchange ex,
            @Qualifier("removeResourceEventMQ") Queue mq){
        return BindingBuilder.bind(mq).to(ex).with(ROUTING_KEY_REMOVE_RESOURCE);
    }
    @Bean
    public Queue removeResourceEventMQ(){
        return new Queue(REMOVE_RESOURCE_EVENT_MQ);
    }
    public static final String ROUTING_KEY_REMOVE_RESOURCE = "remove_resource";
    public static final String REMOVE_RESOURCE_EVENT_MQ = "remove_resource_event_mq";

}