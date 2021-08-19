package com.curtisnewbie.module.messaging.config;

import com.curtisnewbie.module.messaging.handler.OnMessageConfirmedHandlerDelegate;
import com.curtisnewbie.module.messaging.handler.OnMessageReturnedHandlerDelegate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yongjie.zhuang
 */
//@EnableRabbit
@Configuration
public class RabbitMqConfig {

    @Autowired
    private OnMessageConfirmedHandlerDelegate onMessageConfirmedHandlerDelegate;

    @Autowired
    private OnMessageReturnedHandlerDelegate onMessageReturnedHandlerDelegate;

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(onMessageConfirmedHandlerDelegate);
        rabbitTemplate.setReturnsCallback(onMessageReturnedHandlerDelegate);
        return rabbitTemplate;
    }
}
