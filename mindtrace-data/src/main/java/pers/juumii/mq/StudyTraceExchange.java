package pers.juumii.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StudyTraceExchange {

    public static final String STUDY_TRACE_EVENT_EXCHANGE = "study_trace_event_exchange";
    public static final String SETTLE_EVENT_MQ = "settle_event_mq";
    public static final String REMOVE_EVENT_MQ = "remove_event_mq";
    public static final String ROUTING_KEY_SETTLE = "settle";
    public static final String ROUTING_KEY_REMOVE = "remove";


    @Bean
    public DirectExchange studyTraceEventExchange(){
        return new DirectExchange(STUDY_TRACE_EVENT_EXCHANGE);
    }
    @Bean
    public Queue settleEventMQ(){
        return new Queue(SETTLE_EVENT_MQ);
    }
    @Bean
    public Binding settleEventBinding(DirectExchange studyTraceEventExchange, Queue settleEventMQ){
        return BindingBuilder.bind(settleEventMQ).to(studyTraceEventExchange).with(ROUTING_KEY_SETTLE);
    }
    @Bean
    public Queue removeEventMQ(){
        return new Queue(REMOVE_EVENT_MQ);
    }
    @Bean
    public Binding removeEventBinding(DirectExchange studyTraceEventExchange, Queue removeEventMQ){
        return BindingBuilder.bind(removeEventMQ).to(studyTraceEventExchange).with(ROUTING_KEY_REMOVE);
    }
}
