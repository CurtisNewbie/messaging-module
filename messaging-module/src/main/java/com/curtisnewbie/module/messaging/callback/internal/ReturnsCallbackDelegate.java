package com.curtisnewbie.module.messaging.callback.internal;

import com.curtisnewbie.module.messaging.callback.MessageReturnsCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * Delegate for returned message
 * </p>
 *
 * @author yongjie.zhuang
 */
@Slf4j
@Component
public class ReturnsCallbackDelegate implements RabbitTemplate.ReturnsCallback {

    @Autowired(required = false)
    private List<MessageReturnsCallback> returnsCallbackList;

    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        log.debug("Message returned: {}", returnedMessage);
        if (returnsCallbackList != null)
            returnsCallbackList.forEach(c -> c.returnedMessage(returnedMessage));
    }
}
