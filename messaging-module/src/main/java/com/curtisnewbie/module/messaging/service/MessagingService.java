package com.curtisnewbie.module.messaging.service;

import com.curtisnewbie.module.messaging.config.RoutingInfo;
import org.springframework.amqp.core.MessageDeliveryMode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Service for messaging
 *
 * @author yongjie.zhuang
 */
public interface MessagingService {

    /**
     * Send message to exchange
     * <br>
     * <br>
     * DeliveryMode is by default {@link MessageDeliveryMode#PERSISTENT}
     *
     * @param msg        message (will be serialised as JSON)
     * @param exchange   exchange
     * @param routingKey routingKey
     */
    void send(@NotNull Object msg, @NotEmpty String exchange, @NotEmpty String routingKey);

    /**
     * Send message to exchange
     *
     * @param msg          message (will be serialised as JSON)
     * @param exchange     exchange
     * @param routingKey   routingKey
     * @param deliveryMode deliveryMode
     */
    void send(@NotNull Object msg, @NotEmpty String exchange, @NotEmpty String routingKey, @NotNull MessageDeliveryMode deliveryMode);

    /**
     * Send message to exchange
     * <br>
     * <br>
     * DeliveryMode is by default {@link MessageDeliveryMode#PERSISTENT}
     *
     * @param msg         message (will be serialised as JSON)
     * @param routingInfo
     */
    void send(@NotNull Object msg, @NotNull RoutingInfo routingInfo);

    /**
     * Send message to exchange
     * <br>
     * <br>
     * DeliveryMode is by default {@link MessageDeliveryMode#PERSISTENT}
     *
     * @param msg          message (will be serialised as JSON)
     * @param routingInfo
     * @param deliveryMode deliveryMode
     */
    void send(@NotNull Object msg, @NotNull RoutingInfo routingInfo, @NotNull MessageDeliveryMode deliveryMode);

}
