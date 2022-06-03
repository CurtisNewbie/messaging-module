package com.curtisnewbie.module.messaging.outbox.components;

import com.curtisnewbie.module.messaging.config.EnableTransactionalOutbox;
import com.curtisnewbie.module.messaging.outbox.dao.MessageOutbox;
import com.curtisnewbie.module.messaging.service.MessagingParam;
import org.springframework.amqp.core.MessageDeliveryMode;

import java.util.List;

/**
 * Message Outbox
 * <p>
 * Messages that are pushed into the outbox will be automatically dispatched to the broker.
 * <p>
 * Using this with transaction makes the message dispatching transactional, since 'push()' is simply inserting the
 * message to database, but this will for sure introduce latency
 * <p>
 * E.g., one may want to update the table A, and then dispatch a message to notify other services about the event. By
 * using {@code Outbox}, we can wrap these two operations inside a single transaction. If the message isn't dispatched
 * to the broker, the table update will rollback.
 *
 * @author yongj.zhuang
 * @see EnableTransactionalOutbox
 */
public interface Outbox {

    int DEFAULT_PULL_LIMIT = 100;

    /**
     * <p>
     * Push message into outbox, which will then dispatch the message to the broker later
     * <p>
     * DeliveryMode is always {@link MessageDeliveryMode#PERSISTENT} when the message is dispatched to the broker
     */
    void push(MessagingParam message);

    /**
     * Pull messages from outbox
     * <p>
     * This method should only be used by {@link DispatchLoop}
     */
    List<MessageOutbox> _pull(int limit);

    /**
     * Pull messages from outbox using default batch size
     * <p>
     * This method should only be used by {@link DispatchLoop}
     */
    default List<MessageOutbox> _pull() {
        return _pull(DEFAULT_PULL_LIMIT);
    }

    /**
     * Mark the message as 'dispatched'
     * <p>
     * This method should only be used by {@link DispatchLoop}
     */
    void _setDispatched(int id);

}
