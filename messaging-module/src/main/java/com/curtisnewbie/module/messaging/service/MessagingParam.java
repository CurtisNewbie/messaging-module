package com.curtisnewbie.module.messaging.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.amqp.core.MessageDeliveryMode;
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
    @NotNull
    private final Object payload;

    /** Exchange name */
    @NotEmpty
    private final String exchange;

    /** Routing key */
    @NotEmpty
    private final String routingKey;

    @Nullable
    private final MessageDeliveryMode deliveryMode;

    @Nullable
    private final CorrelationData correlationData;
}
