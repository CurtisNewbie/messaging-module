package com.curtisnewbie.module.messaging.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author yongjie.zhuang
 */
@Slf4j
@Service
public class MessagingServiceImpl implements MessagingService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void send(MessagingParam param) {
        MessagePostProcessor mpp = param.getMessagePostProcessor();
        mpp = new GeneralPropertiesMessagePostProcessor(param.getDeliveryMode())
                .wrap(mpp);

        if (param.getRoutingKey() == null)
            param.setRoutingKey(MessagingService.DEFAULT_ROUTING_KEY);

        rabbitTemplate.convertAndSend(param.getExchange(),
                param.getRoutingKey(),
                param.getPayload(),
                mpp,
                param.getCorrelationData());
    }

    @Override
    public void send(Object payload, String exchange) {
        send(MessagingParam.builder()
                .payload(payload)
                .exchange(exchange)
                .deliveryMode(MessageDeliveryMode.PERSISTENT)
                .routingKey(DEFAULT_ROUTING_KEY)
                .build());
    }

    @Override
    public void send(Object payload, String exchange, String routingKey) {
        send(MessagingParam.builder()
                .payload(payload)
                .exchange(exchange)
                .deliveryMode(MessageDeliveryMode.PERSISTENT)
                .routingKey(routingKey)
                .build());
    }

    // ------------------------------------ private helper methods ------------------------------------

    private static class GeneralPropertiesMessagePostProcessor implements MessagePostProcessor {
        private final MessageDeliveryMode deliveryMode;
        private MessagePostProcessor delegate = null;

        public GeneralPropertiesMessagePostProcessor(MessageDeliveryMode deliveryMode) {
            this.deliveryMode = deliveryMode != null ? deliveryMode : MessageDeliveryMode.PERSISTENT;
        }

        @Override
        public Message postProcessMessage(Message message) throws AmqpException {
            message.getMessageProperties().setDeliveryMode(deliveryMode);
            message.getMessageProperties().setTimestamp(new Date());

            if (delegate != null)
                return delegate.postProcessMessage(message);
            else return message;
        }

        /** Wrap the {@link org.springframework.amqp.core.MessagePostProcessor} */
        public MessagePostProcessor wrap(MessagePostProcessor mpp) {
            if (this.delegate != null)
                throw new IllegalStateException("Already wrapped a post processor");

            this.delegate = mpp;
            return this;
        }
    }
}

