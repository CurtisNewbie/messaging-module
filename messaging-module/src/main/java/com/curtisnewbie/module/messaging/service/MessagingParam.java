package com.curtisnewbie.module.messaging.service;

import com.curtisnewbie.module.messaging.config.RoutingInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;

/**
 * Parameters for messaging
 *
 * @author yongjie.zhuang
 */
@Data
@AllArgsConstructor
@lombok.Builder
public class MessagingParam {

    /**
     * Message payload, will be serialised as JSON string
     */
    @NotNull
    private final Object payload;

    /** Exchange name */
    private final String exchange;

    /** Routing key */
    private final String routingKey;

    /**
     * Same as {@link #exchange} and {@link #routingKey}, can't be null if {@link #exchange} and {@link #routingKey} are
     * both null
     */
    private final RoutingInfo routingInfo;

    @Nullable
    private final MessageDeliveryMode deliveryMode;

    @Nullable
    private final CorrelationData correlationData;
}
