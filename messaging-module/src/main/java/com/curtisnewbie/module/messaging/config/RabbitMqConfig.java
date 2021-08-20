package com.curtisnewbie.module.messaging.config;

import com.curtisnewbie.module.messaging.callback.internal.ConfirmCallbackDelegate;
import com.curtisnewbie.module.messaging.callback.internal.ReturnsCallbackDelegate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yongjie.zhuang
 */
@Configuration
public class RabbitMqConfig {

    @Autowired
    private ConfirmCallbackDelegate confirmCallbackDelegate;

    @Autowired
    private ReturnsCallbackDelegate returnsCallbackDelegate;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private Jackson2JsonMessageConverter jackson2JsonMessageConverter;

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(confirmCallbackDelegate);
        rabbitTemplate.setReturnsCallback(returnsCallbackDelegate);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
        return rabbitTemplate;
    }
}
