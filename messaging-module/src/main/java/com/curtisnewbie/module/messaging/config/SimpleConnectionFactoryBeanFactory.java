package com.curtisnewbie.module.messaging.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * <p>
 * Simple bean factory for {@link org.springframework.amqp.rabbit.connection.ConnectionFactory}
 * </p>
 * <p>
 * This class is used to facilitate programmatic configuration of a ConnectionFactory bean
 * </p>
 *
 * @author yongjie.zhuang
 */
@Slf4j
@Configuration
public class SimpleConnectionFactoryBeanFactory {

    /**
     * <p>
     * Create a basic {@link CachingConnectionFactory} based on a set of property values, the publisher confirm is
     * enabled and set to {@link CachingConnectionFactory.ConfirmType#SIMPLE}
     * </p>
     * <ul>
     *   <li>spring.rabbitmq.virtualHost</li>
     *   <li>spring.rabbitmq.host</li>
     *   <li>spring.rabbitmq.password</li>
     *   <li>spring.rabbitmq.username</li>
     *   <li>spring.rabbitmq.port</li>
     * </ul>
     *
     * @param environment environment used to get the required property
     * @return a new CachingConnectionFactory that can be further configured
     */
    @Bean
    @ConditionalOnMissingBean(ConnectionFactory.class)
    public CachingConnectionFactory createByProperties(Environment environment) {
        log.info("No ConnectionFactory found, populating bean '{}'", CachingConnectionFactory.class);
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setVirtualHost(environment.getRequiredProperty("spring.rabbitmq.virtualHost"));
        connectionFactory.setHost(environment.getRequiredProperty("spring.rabbitmq.host"));
        connectionFactory.setPassword(environment.getRequiredProperty("spring.rabbitmq.password"));
        connectionFactory.setUsername(environment.getRequiredProperty("spring.rabbitmq.username"));
        connectionFactory.setPort(environment.getRequiredProperty("spring.rabbitmq.port", Integer.class));
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.SIMPLE);
        return connectionFactory;
    }
}
