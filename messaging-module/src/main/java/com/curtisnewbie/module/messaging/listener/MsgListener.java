package com.curtisnewbie.module.messaging.listener;

import com.curtisnewbie.module.messaging.service.MessagingService;
import org.springframework.amqp.core.AcknowledgeMode;

import java.lang.annotation.*;

/**
 * MQ Message Listener
 * <p>
 * Message will be deserialized using the {@link org.springframework.amqp.support.converter.MessageConverter} bean
 * <p>
 * Annotated method should only accept one single argument (the pojo or string, it depends on the MessageListener used)
 *
 * @author yongj.zhuang
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface MsgListener {

    String NONE = "";

    /**
     * Name of the direct exchange (durable, and if specified, this exchange will be bound to the specified queue)
     */
    String exchange() default NONE;

    /**
     * Name of the queue (durable, and non-exclusive)
     */
    String queue();

    /**
     * Routing Key, by default it's '#'
     */
    String routingKey() default MessagingService.DEFAULT_ROUTING_KEY;

    /**
     * AcknowledgeMode, by default it's AUTO (that will return ack/nack based on whether or not the listener method
     * returns normally)
     */
    AcknowledgeMode ackMode() default AcknowledgeMode.AUTO;

    /**
     * Concurrency, by default it's not configured, and is up to the container configuration
     */
    int concurrency() default 0;
}
