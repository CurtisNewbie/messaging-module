package com.curtisnewbie.module.messaging.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Handler delegate for confirmed message
 *
 * @author yongjie.zhuang
 */
@Slf4j
@Component
public class OnMessageConfirmedHandlerDelegate implements RabbitTemplate.ConfirmCallback {


    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        log.info("Confirmed message: {}", s);
    }
}
