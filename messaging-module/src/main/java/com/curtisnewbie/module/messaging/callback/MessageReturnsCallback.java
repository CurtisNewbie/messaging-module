package com.curtisnewbie.module.messaging.callback;

import com.curtisnewbie.module.messaging.callback.internal.ReturnsCallbackDelegate;
import org.springframework.amqp.core.ReturnedMessage;

/**
 * <p>
 * Message returns callback for messages that can't be routed
 * </p>
 * <p>
 * E.g., according to spring, this applies when "publish to an exchange but there is no matching destination queue"
 * </p>
 * <p>
 * When message is publish to a non-existing exchange, no return is generated, the channel is closed, so this callback
 * won't be invoked.
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
