package com.curtisnewbie.module.messaging.handler;

import org.springframework.amqp.core.ReturnedMessage;

/**
 * <p>
 * Message returns callback for messages that can't be routed
 * </p>
 * <p>
 * Beans that implement this interface is automatically invoked for messages returned, there can be more than one such
 * callback provided, but the order in which they are invoked is undefined.
 * <p>
 *
 * @author yongjie.zhuang
 * @see ReturnsCallbackDelegate
 */
public interface MessageReturnsCallback {

    /**
     * Returned message callback.
     *
     * @param returned the returned message and metadata.
     */
    void returnedMessage(ReturnedMessage returned);
}
