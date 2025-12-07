package com.example.KursNews.services;

import com.example.KursNews.dto.CommentNotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private static final String TOPIC = "comment-notifications";

    @Autowired
    private KafkaTemplate<String, CommentNotificationEvent> kafkaTemplate;

    public void sendCommentNotification(CommentNotificationEvent event) {
        logger.info("Sending comment notification event to Kafka: {}", event);
        
        CompletableFuture<SendResult<String, CommentNotificationEvent>> future = 
            kafkaTemplate.send(TOPIC, event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Comment notification sent successfully: offset={}", 
                    result.getRecordMetadata().offset());
            } else {
                logger.error("Failed to send comment notification", ex);
            }
        });
    }
}

