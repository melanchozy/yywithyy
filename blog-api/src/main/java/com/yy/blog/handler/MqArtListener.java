package com.yy.blog.handler;

import com.yy.blog.service.SearchService;
import com.yy.blog.utils.Mq;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MqArtListener {
    @Autowired
    private SearchService searchService;

    @RabbitListener(queues = Mq.ART_INSERT_QUEUE)
    public void listenArtInsertOrUpdate(Long id){
        searchService.insertById(id);
    }
    @RabbitListener(queues = Mq.ART_DELETE_QUEUE)
    public void listenArtDelete(Long id){
        searchService.deleteById(id);
    }
    // tag索引库的删改
    @RabbitListener(queues = Mq.TAG_INSERT_QUEUE)
    public void listenTagInsertOrUpdate(Long id){
        searchService.insertTagById(id);
    }
    @RabbitListener(queues = Mq.TAG_DELETE_QUEUE)
    public void listenTagDelete(Long id){
        searchService.deleteTagById(id);
    }
}
