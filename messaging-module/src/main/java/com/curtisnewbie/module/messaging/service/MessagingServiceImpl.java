package com.curtisnewbie.module.messaging.service;

import com.curtisnewbie.module.messaging.config.RoutingInfo;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

/**
 * @author yongjie.zhuang
 */
@Service
public class MessagingServiceImpl implements MessagingService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void send(@NotNull @Valid MessagingParam param) {
        RoutingInfo r = param.getRoutingInfo();
        String exchange, routingKey;
        if (r != null) {
            exchange = r.getExchange();
            routingKey = r.getRoutingKey();
        } else {
            exchange = param.getExchange();
            routingKey = param.getRoutingKey();
        }

        Objects.requireNonNull(routingKey);
        Objects.requireNonNull(exchange);

        send(param.getPayload(), exchange, routingKey, param.getDeliveryMode(), param.getCorrelationData());
    }

    @Override
    public void send(@NotNull Object msg, @NotEmpty String exchange, @NotEmpty String routingKey) {
        send(msg, exchange, routingKey, MessageDeliveryMode.PERSISTENT, null);
    }

    @Override
    public void send(@NotNull Object msg, @NotEmpty String exchange, @NotEmpty String routingKey, @NotNull MessageDeliveryMode deliveryMode,
                     @Nullable CorrelationData correlationData) {
        rabbitTemplate.convertAndSend(exchange, routingKey, msg, new GeneralPropertiesMessagePostProcessor(deliveryMode), correlationData);
    }

    @Override
    public void send(@NotNull Object msg, @NotNull RoutingInfo routingInfo) {
        String exchange = routingInfo.getExchange();
        Objects.requireNonNull(exchange);
        String routingKey = routingInfo.getRoutingKey();
        Objects.requireNonNull(routingKey);
        send(msg, exchange, routingKey, MessageDeliveryMode.PERSISTENT, null);
    }

    @Override
    public void send(@NotNull Object msg, @NotNull RoutingInfo routingInfo, @NotNull MessageDeliveryMode deliveryMode) {
        String exchange = routingInfo.getExchange();
        Objects.requireNonNull(exchange);
        String routingKey = routingInfo.getRoutingKey();
        Objects.requireNonNull(routingKey);
        send(msg, exchange, routingKey, deliveryMode, null);
    }

    private static class GeneralPropertiesMessagePostProcessor implements MessagePostProcessor {
        private final MessageDeliveryMode deliveryMode;

        public GeneralPropertiesMessagePostProcessor(MessageDeliveryMode deliveryMode) {
            this.deliveryMode = deliveryMode;
        }

        @Override
        public Message postProcessMessage(Message message) throws AmqpException {
            message.getMessageProperties().setDeliveryMode(deliveryMode);
            message.getMessageProperties().setTimestamp(new Date());
            return message;
        }
    }
}

