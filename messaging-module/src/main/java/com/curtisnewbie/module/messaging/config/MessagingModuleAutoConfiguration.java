package com.curtisnewbie.module.messaging.config;

import com.curtisnewbie.common.util.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

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
    private ApplicationContext applicationContext;

    /**
     * Only populate a {@code RabbitTemplate} bean with default settings when there is no such bean found
     */
    @Bean
    @ConditionalOnMissingBean(RabbitTemplate.class)
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        log.info("No RabbitTemplate found, populating one with default settings");

        RabbitTemplate rabbitTemplate = new RabbitTemplate();

        // connectionFactory is mandatory dependency
        rabbitTemplate.setConnectionFactory(connectionFactory);

        // check if a MessageConverter populated in the context, if so, register it
        try {
            MessageConverter msgCvt = applicationContext.getBean(MessageConverter.class);
            rabbitTemplate.setMessageConverter(msgCvt);
            log.info("Registered MessageConverter: '{}'", msgCvt.getClass());
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
    public MessageConverter jackson2JsonMessageConverter(ObjectMapper om) {
        log.info("No MessageConverter found, populating bean '{}'", Jackson2JsonMessageConverter.class);
        return new Jackson2JsonMessageConverter(om);
    }

    /** Only populate {@link com.fasterxml.jackson.databind.ObjectMapper} when it's not found */
    @Bean
    @ConditionalOnMissingBean(value = ObjectMapper.class)
    public ObjectMapper objectMapper() {
        ObjectMapper om = JsonUtils.constructsJsonMapper();
        om.registerModule(new JavaTimeModule());
        return om;
    }
}
