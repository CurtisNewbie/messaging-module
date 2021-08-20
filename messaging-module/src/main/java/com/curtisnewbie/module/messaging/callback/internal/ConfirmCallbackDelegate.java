package com.curtisnewbie.module.messaging.callback.internal;

import com.curtisnewbie.module.messaging.callback.MessageConfirmCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * Delegate for publisher-confirm messages
 * </p>
 *
 * @author yongjie.zhuang
 */
@Slf4j
@Component
public class ConfirmCallbackDelegate implements RabbitTemplate.ConfirmCallback {

    @Autowired(required = false)
    private List<MessageConfirmCallback> messageConfirmCallbackList;

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        log.debug("Message confirmed, correlation_data: '{}', ack: '{}', cause: '{}'", correlationData, ack, cause);
        messageConfirmCallbackList.forEach(c -> c.confirm(correlationData, ack, cause));
    }
}
