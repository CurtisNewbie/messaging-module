package com.curtisnewbie.module.messaging.service;

import org.springframework.amqp.core.MessageDeliveryMode;
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

    /** Default routing key used */
    String DEFAULT_ROUTING_KEY = "#";

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
     * Send message to exchange with default routing key {@link #DEFAULT_ROUTING_KEY}
     * <p>
     * DeliveryMode is {@link MessageDeliveryMode#PERSISTENT}
     */
    void send(@NotNull(message = "payload can't be null") Object payload, @NotEmpty(message = "exchange can't be empty") String exchange);

}
