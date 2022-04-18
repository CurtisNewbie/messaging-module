package com.curtisnewbie.module.messaging.service;

import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
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

}
