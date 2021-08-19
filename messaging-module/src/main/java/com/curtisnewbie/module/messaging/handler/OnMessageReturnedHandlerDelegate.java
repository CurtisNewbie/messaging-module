package com.curtisnewbie.module.messaging.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Handler delegate for returned message
 *
 * @author yongjie.zhuang
 */
@Slf4j
@Component
public class OnMessageReturnedHandlerDelegate implements RabbitTemplate.ReturnsCallback {

    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        log.info("Returned message: {}", returnedMessage);
    }
}
