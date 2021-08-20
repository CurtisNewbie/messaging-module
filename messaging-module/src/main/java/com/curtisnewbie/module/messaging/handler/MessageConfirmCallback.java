package com.curtisnewbie.module.messaging.handler;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.lang.Nullable;

/**
 * <p>
 * Message confirmation callback, used for publisher confirm
 * </p>
 * <p>
 * Beans that implement this interface is automatically invoked for message confirm (for publisher), there can be more
 * than one such callback provided, but the order in which they are invoked is undefined.
 * <p>
 * Since this callback is used for publisher confirm, it must be enabled first in the {@code ConnectionFactory} by
 * setting confirm-type to "CORRELATED". This can be configured in code or xml (under rabbit namespace). See {@link
 * org.springframework.amqp.rabbit.connection.CachingConnectionFactory.ConfirmType}
 *
 * @author yongjie.zhuang
 * @see ConfirmCallbackDelegate
 */
public interface MessageConfirmCallback {

    /**
     * Confirmation callback.
     *
     * @param correlationData correlation data for the callback.
     * @param ack             true for ack, false for nack
     * @param cause           An optional cause, for nack, when available, otherwise null.
     */
    void confirm(@Nullable CorrelationData correlationData, boolean ack, @Nullable String cause);
}
