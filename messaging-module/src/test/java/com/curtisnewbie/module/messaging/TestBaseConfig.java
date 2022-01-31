package com.curtisnewbie.module.messaging;

import com.curtisnewbie.module.messaging.config.SimpleConnectionFactoryBeanFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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

}
