package com.curtisnewbie.module.messaging;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.*;

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

}
