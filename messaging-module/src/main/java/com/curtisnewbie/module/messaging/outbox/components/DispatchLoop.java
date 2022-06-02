package com.curtisnewbie.module.messaging.outbox.components;

import com.curtisnewbie.common.util.JsonUtils;
import com.curtisnewbie.common.util.LockUtils;
import com.curtisnewbie.module.messaging.outbox.dao.MessageOutbox;
import com.curtisnewbie.module.messaging.service.MessagingParam;
import com.curtisnewbie.module.messaging.service.MessagingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Dispatch Loop
 *
 * @author yongj.zhuang
 */
@Slf4j
@EnableScheduling
public class DispatchLoop {

    private final Supplier<List<MessageOutbox>> messageSupplier;
    private final Consumer<Integer /* message.id */> onDispatched;
    private final MessagingService messagingService;
    private final Supplier<Lock> lockSupplier;

    public DispatchLoop(Supplier<List<MessageOutbox>> messageSupplier, Consumer<Integer> onDispatched, MessagingService messagingService,
                        @Nullable Supplier<Lock> lockSupplier) {
        this.messageSupplier = messageSupplier;
        this.onDispatched = onDispatched;
        this.messagingService = messagingService;
        this.lockSupplier = lockSupplier;
    }

    @Scheduled(fixedRate = 1_000)
    public void onStartup() {
        log.info("DispatchLoop starts");
        if (lockSupplier != null) {
            final Lock lock = lockSupplier.get();
            LockUtils.lockAndRun(lock, this::doDispatch);
        } else {
            doDispatch();
        }
        log.info("DispatchLoop ends");
    }

    private void doDispatch() {
        List<MessageOutbox> messages;
        while ((messages = messageSupplier.get()) != null && !messages.isEmpty()) {
            messages.forEach(this::_send);
        }
    }

    // ---------------------------------------- private helper methods ---------------------------------

    private void _send(MessageOutbox m) {

        final int id = m.getId();
        final String routingKey = m.getRoutingKey();
        final String exchange = m.getExchange();

        // todo: it will be great if we can send the payload as string directly to the broker
        //  without breaking the deserialization for the listener

        Object payload;
        final String typeName = m.getTypeName();
        if (StringUtils.hasText(typeName)) {
            try {
                final Class<?> clz = Class.forName(typeName);
                payload = JsonUtils.readValueAsObject(m.getPayload(), clz);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to deserialize payload object from type name: " + typeName, e);
            }
        } else {
            payload = m.getPayload();
        }

        // send the message to broker
        messagingService.send(MessagingParam.builder()
                .payload(payload)
                .exchange(exchange)
                .routingKey(routingKey)
                .deliveryMode(MessageDeliveryMode.PERSISTENT)
                .build());

        log.info("Dispatched message, id: {}, exchange: {}, routing_key: {}", id, exchange, routingKey);

        // mark as dispatched
        onDispatched.accept(id);
    }

}
