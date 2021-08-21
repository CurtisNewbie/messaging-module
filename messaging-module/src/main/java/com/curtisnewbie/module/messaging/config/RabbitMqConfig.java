package com.curtisnewbie.module.messaging.config;

import com.curtisnewbie.module.messaging.callback.internal.ConfirmCallbackDelegate;
import com.curtisnewbie.module.messaging.callback.internal.ReturnsCallbackDelegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yongjie.zhuang
 */
@Slf4j
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

    @Autowired(required = false)
    private RabbitTemplateConfigurer rabbitTemplateConfigurer;

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();

        // do internal configuration
        rabbitTemplate = internalConfigure(rabbitTemplate);

        // if custom configurer is found, do some further configuration, allows it to override the internal configuration
        if (rabbitTemplateConfigurer != null) {
            log.info("Detected {}, applying configuration", RabbitTemplateConfigurer.class.getSimpleName());
            rabbitTemplate = rabbitTemplateConfigurer.configure(rabbitTemplate);
        }
        return rabbitTemplate;
    }

    private RabbitTemplate internalConfigure(RabbitTemplate rabbitTemplate) {
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(confirmCallbackDelegate);
        rabbitTemplate.setReturnsCallback(returnsCallbackDelegate);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
        return rabbitTemplate;
    }
}
