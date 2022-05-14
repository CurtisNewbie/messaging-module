package com.curtisnewbie.module.messaging.config;

import com.curtisnewbie.common.util.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.config.DirectRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.ListenerContainerFactoryBean;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

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
    @Autowired
    private MessagingModuleProperties properties;

    /**
     * Only populate a {@code RabbitTemplate} bean with default settings when there is no such bean found
     */
    @Bean
    @ConditionalOnMissingBean(RabbitTemplate.class)
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory, final MessageConverter messageConverter) {
        log.info("No RabbitTemplate found, populating one with default settings");

        RabbitTemplate rabbitTemplate = new RabbitTemplate();

        // connectionFactory is mandatory dependency
        rabbitTemplate.setConnectionFactory(connectionFactory);

        // message converter
        rabbitTemplate.setMessageConverter(messageConverter);
        log.info("Registered MessageConverter: '{}'", messageConverter.getClass());

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

    /** Only populate {@link DirectRabbitListenerContainerFactory} when it's not found */
    @Bean
    @ConditionalOnMissingBean(value = RabbitListenerContainerFactory.class)
    public RabbitListenerContainerFactory<?> defaultListenerContainerFactory(MessageConverter messageConverter) {
        DirectRabbitListenerContainerFactory containerFactory = new DirectRabbitListenerContainerFactory();
        containerFactory.setRetryTemplate(defaultRetryTemplate());
        containerFactory.setMessageConverter(messageConverter);
        log.info("No RabbitListenerContainerFactory found, populating bean '{}'", containerFactory.getClass());
        return containerFactory;
    }

    /** Default retry template */
    public RetryTemplate defaultRetryTemplate() {
        final RetryTemplate rt = new RetryTemplate();

        // backoff
        final ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(properties.getBackOffInitialInterval());
        backOffPolicy.setMultiplier(properties.getBackOffMultiplier());
        backOffPolicy.setMaxInterval(properties.getBackOffMaxInterval());
        rt.setBackOffPolicy(backOffPolicy);

        // retry
        final SimpleRetryPolicy rp = new SimpleRetryPolicy();
        rp.setMaxAttempts(properties.getTryMaxAttempt());

        rt.setRetryPolicy(rp);
        return rt;
    }
}
