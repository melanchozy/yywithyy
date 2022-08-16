package com.yy.blog.config;

import com.yy.blog.utils.Mq;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(Mq.ART_EXCHANGE,true,false);
    }

    @Bean
    public Queue insertQueue(){
        return new Queue(Mq.ART_INSERT_QUEUE,true);
    }

    @Bean
    public Queue deleteQueue(){
        return new Queue(Mq.ART_DELETE_QUEUE,true);
    }
    //绑定关系
    @Bean
    public Binding insertQueueBinding(){
        return BindingBuilder.bind(insertQueue()).to(topicExchange()).with(Mq.ART_INSERT_KEY);
    }
    @Bean
    public Binding deleteQueueBinding(){
        return BindingBuilder.bind(deleteQueue()).to(topicExchange()).with(Mq.ART_DELETE_KEY);
    }
    // Tag
    @Bean
    public Queue insertTagQueue(){
        return new Queue(Mq.TAG_INSERT_QUEUE,true);
    }

    @Bean
    public Queue deleteTagQueue(){
        return new Queue(Mq.TAG_DELETE_QUEUE,true);
    }
    //绑定关系
    @Bean
    public Binding insertTagQueueBinding(){
        return BindingBuilder.bind(insertTagQueue()).to(topicExchange()).with(Mq.TAG_INSERT_KEY);
    }
    @Bean
    public Binding deleteTagQueueBinding(){
        return BindingBuilder.bind(deleteTagQueue()).to(topicExchange()).with(Mq.TAG_DELETE_KEY);
    }
}
