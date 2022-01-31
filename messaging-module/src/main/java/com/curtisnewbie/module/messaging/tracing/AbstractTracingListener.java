package com.curtisnewbie.module.messaging.tracing;

import com.curtisnewbie.module.tracing.common.MdcUtil;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * Abstract message Listener with log tracing
 * </p>
 * <p>
 * All you need to do is to implement {@link #onMessage(Object, Message)}}
 * </p>
 *
 * @author yongjie.zhuang
 */
public abstract class AbstractTracingListener<T> extends MessageListenerAdapter {

    @Autowired
    private Jackson2JsonMessageConverter jackson2JsonMessageConverter;

    /**
     * On message received
     *
     * @param t       message deserialized as object
     * @param message message
     */
    protected abstract void onMessage(T t, Message message);

    private void handleMessage(T t, Message message) {
        String traceId = MessageTracingUtil.getTraceId(message);
        MdcUtil.setTraceId(traceId);
        try {
            onMessage(t, message);
        } finally {
            MdcUtil.removeTraceId();
        }
    }

    @Override
    protected MessageConverter getMessageConverter() {
        return jackson2JsonMessageConverter;
    }

    @Override
    protected Object[] buildListenerArguments(Object extractedMessage, Channel channel, Message message) {
        return new Object[]{extractedMessage, message};
    }
}
