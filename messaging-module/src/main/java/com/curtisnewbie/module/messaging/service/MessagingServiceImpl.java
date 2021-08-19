package com.curtisnewbie.module.messaging.service;

import com.curtisnewbie.module.messaging.config.RoutingInfo;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

/**
 * @author yongjie.zhuang
 */
@Component
public class MessagingServiceImpl implements MessagingService {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public void send(Object msg, String exchange, String routingKey) {
        send(msg, exchange, routingKey, MessageDeliveryMode.PERSISTENT);
    }

    @Override
    public void send(Object msg, String exchange, String routingKey, MessageDeliveryMode deliveryMode) {
        rabbitTemplate.convertAndSend(exchange, routingKey, msg, (message) -> {
            message.getMessageProperties().setDeliveryMode(deliveryMode);
            message.getMessageProperties().setTimestamp(new Date());
            return message;
        });
    }

    @Override
    public void send(Object msg, RoutingInfo routingInfo) {
        String exchange = routingInfo.getExchange();
        Objects.requireNonNull(exchange);
        String routingKey = routingInfo.getRoutingKey();
        Objects.requireNonNull(routingKey);
        send(msg, exchange, routingKey, MessageDeliveryMode.PERSISTENT);
    }

    @Override
    public void send(@NotNull Object msg, @NotNull RoutingInfo routingInfo, @NotNull MessageDeliveryMode deliveryMode) {
        String exchange = routingInfo.getExchange();
        Objects.requireNonNull(exchange);
        String routingKey = routingInfo.getRoutingKey();
        Objects.requireNonNull(routingKey);
        send(msg, exchange, routingKey, deliveryMode);
    }
}

