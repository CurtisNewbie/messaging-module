package com.curtisnewbie.module.messaging.config;

import com.curtisnewbie.module.messaging.callback.internal.ConfirmCallbackDelegate;
import com.curtisnewbie.module.messaging.callback.internal.ReturnsCallbackDelegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * <p>
 * Auto-configuration for MessagingModule
 * </p>
 *
 * @author yongjie.zhuang
 */
@Slf4j
public class MessagingModuleAutoConfiguration {

    @Autowired
    private ConfirmCallbackDelegate confirmCallbackDelegate;

    @Autowired
    private ReturnsCallbackDelegate returnsCallbackDelegate;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Only populate a {@code RabbitTemplate} bean with default settings when there is no such bean found
     */
    @Bean
    @ConditionalOnMissingBean(RabbitTemplate.class)
    public RabbitTemplate rabbitTemplate() {
        log.info("No RabbitTemplate found, populating one with default settings");

        RabbitTemplate rabbitTemplate = new RabbitTemplate();

        // connectionFactory is mandatory dependency
        rabbitTemplate.setConnectionFactory(connectionFactory);

        // register callbacks even though they are not needed, make sure they work out of box
        rabbitTemplate.setConfirmCallback(confirmCallbackDelegate);
        log.info("Registered ConfirmCallback '{}'", confirmCallbackDelegate.getClass().getName());
        rabbitTemplate.setReturnsCallback(returnsCallbackDelegate);
        log.info("Registered ReturnsCallback '{}'", returnsCallbackDelegate.getClass().getName());

        // check if a MessageConverter populated in the context, if so, register it
        try {
            MessageConverter msgCvt = applicationContext.getBean(MessageConverter.class);
            rabbitTemplate.setMessageConverter(msgCvt);
            log.info("Registered MessageConverter: '{}'", msgCvt.getClass().getName());
        } catch (NoSuchBeanDefinitionException e) {
            log.debug("No MessageConverter found, use the default one", e);
        }
        return rabbitTemplate;
    }

    /**
     * Only populate {@code Jackson2JsonMessageConverter} when there is no {@code MessageConverter} found
     */
    @Bean
    @ConditionalOnMissingBean(value = MessageConverter.class)
    public MessageConverter jackson2JsonMessageConverter() {
        log.info("No MessageConverter found, populating bean '{}'", Jackson2JsonMessageConverter.class.getName());
        return new Jackson2JsonMessageConverter();
    }
}
