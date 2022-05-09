package com.curtisnewbie.module.messaging.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Parameters for messaging
 *
 * @author yongjie.zhuang
 */
@Data
@AllArgsConstructor
@Builder
public class MessagingParam {

    /**
     * Message payload, will be serialised as JSON string
     */
    @NotNull(message = "payload must not be null")
    private Object payload;

    /** Exchange name */
    @NotEmpty(message = "exchange can't be empty")
    private String exchange;

    /** Routing key, by default it's '#' {@link MessagingService#DEFAULT_ROUTING_KEY} */
    @Nullable
    private String routingKey;

    /** Delivery mode, by default it's {@link org.springframework.amqp.core.MessageDeliveryMode#PERSISTENT} */
    @Nullable
    private MessageDeliveryMode deliveryMode;

    @Nullable
    private CorrelationData correlationData;

    @Nullable
    private MessagePostProcessor messagePostProcessor;
}
