package com.curtisnewbie.module.messaging;

import com.curtisnewbie.module.messaging.config.SimpleConnectionFactoryBeanFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

/**
 * @author yongjie.zhuang
 */
@RabbitListenerTest
@SpringBootApplication
@PropertySource("classpath:application.properties")
public class TestBaseConfig {

    @Bean
    public SimpleRabbitListenerContainerFactory containerFactoryBean(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory f = new SimpleRabbitListenerContainerFactory();
        f.setConnectionFactory(connectionFactory);
        return f;
    }

    @Autowired
    private Environment environment;

    @Bean
    public ConnectionFactory connectionFactory() {
        return SimpleConnectionFactoryBeanFactory.createByProperties(environment);
    }

}
