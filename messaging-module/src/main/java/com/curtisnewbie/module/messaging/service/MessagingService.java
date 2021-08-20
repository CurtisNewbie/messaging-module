package com.curtisnewbie.module.messaging.service;

import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Service for messaging
 *
 * @author yongjie.zhuang
 */
@Validated
public interface MessagingService {

    /**
     * <p>
     * Send message to exchange
     * </p>
     * <p>
     * DeliveryMode is by default {@link MessageDeliveryMode#PERSISTENT}
     * </p>
     */
    void send(@NotNull @Valid MessagingParam param);

    /**
     * <p>
     * Send message to exchange
     * </p>
     * <p>
     * DeliveryMode is by default {@link MessageDeliveryMode#PERSISTENT}
     * </p>
     *
     * @param msg        message (will be serialised as JSON)
     * @param exchange   exchange
     * @param routingKey routingKey
     */
    void send(@NotNull Object msg, @NotEmpty String exchange, @NotEmpty String routingKey);

    /**
     * <p>
     * Send message to exchange
     * </p>
     *
     * @param msg             message (will be serialised as JSON)
     * @param exchange        exchange
     * @param routingKey      routingKey
     * @param deliveryMode    deliveryMode
     * @param correlationData correlation data (nullable)
     */
    void send(@NotNull Object msg, @NotEmpty String exchange, @NotEmpty String routingKey, @NotNull MessageDeliveryMode deliveryMode,
              @Nullable CorrelationData correlationData);

}
