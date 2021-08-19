package com.curtisnewbie.module.messaging.service;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author yongjie.zhuang
 */
@Component
public class MessagingServiceImpl implements MessagingService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void send(String msg, String exchange, String routingKey) {
        send(msg, exchange, routingKey, MessageDeliveryMode.NON_PERSISTENT);
    }

    @Override
    public void send(String msg, String exchange, String routingKey, MessageDeliveryMode deliveryMode) {
        Message message = MessageBuilder.withBody(msg.getBytes(StandardCharsets.UTF_8))
                .setContentEncoding(StandardCharsets.UTF_8.name())
                .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
                .setDeliveryMode(deliveryMode)
                .setTimestamp(new Date())
                .build();
        rabbitTemplate.send(exchange, routingKey, message);
    }

    @Override
    public void sendJson(Object msg, String exchange, String routingKey) {
        sendJson(msg, exchange, routingKey, MessageDeliveryMode.NON_PERSISTENT);
    }

    @Override
    public void sendJson(Object msg, String exchange, String routingKey, MessageDeliveryMode deliveryMode) {
        rabbitTemplate.convertAndSend(exchange, routingKey, msg, (message) -> {
            message.getMessageProperties().setDeliveryMode(deliveryMode);
            message.getMessageProperties().setTimestamp(new Date());
            return message;
        });
    }
}

