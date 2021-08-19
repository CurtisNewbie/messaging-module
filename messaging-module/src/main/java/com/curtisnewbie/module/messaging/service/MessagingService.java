package com.curtisnewbie.module.messaging.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.amqp.core.MessageDeliveryMode;

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
     * DeliveryMode is by default {@link MessageDeliveryMode#NON_PERSISTENT}
     *
     * @param msg        message
     * @param exchange   exchange
     * @param routingKey routingKey
     */
    void send(String msg, String exchange, String routingKey);

    /**
     * Send message to exchange
     *
     * @param msg          message
     * @param exchange     exchange
     * @param routingKey   routingKey
     * @param deliveryMode deliveryMode
     */
    void send(String msg, String exchange, String routingKey, MessageDeliveryMode deliveryMode);

    /**
     * Send message to exchange
     * <br>
     * <br>
     * DeliveryMode is by default {@link MessageDeliveryMode#NON_PERSISTENT}
     *
     * @param msg        message (will be serialised as JSON)
     * @param exchange   exchange
     * @param routingKey routingKey
     */
    void sendJson(Object msg, String exchange, String routingKey);

    /**
     * Send message to exchange
     *
     * @param msg          message (will be serialised as JSON)
     * @param exchange     exchange
     * @param routingKey   routingKey
     * @param deliveryMode deliveryMode
     */
    void sendJson(Object msg, String exchange, String routingKey, MessageDeliveryMode deliveryMode);
}
