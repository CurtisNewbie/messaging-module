package com.curtisnewbie.module.messaging.listener;

import com.curtisnewbie.common.util.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Abstract message Listener
 *
 * @author yongjie.zhuang
 */
public abstract class AbstractMessageListener<T> implements MessageListener {

    /**
     * On message received
     */
    protected abstract void onMessage(T msg, Message message);

    /**
     * Type of the message
     */
    protected abstract Class<T> messageType();

    @Override
    public void onMessage(Message message) {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        Class<T> type = messageType();
        Objects.requireNonNull(type, "messageType() should not return null");
        try {
            onMessage(JsonUtils.readValueAsObject(msg, type), message);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to parse json value", e);
        }
    }
}
