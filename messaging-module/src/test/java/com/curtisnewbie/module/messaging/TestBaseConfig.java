package com.curtisnewbie.module.messaging;

import com.curtisnewbie.module.messaging.config.EnableTransactionalOutbox;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.*;

import java.io.IOException;

/**
 * @author yongjie.zhuang
 */
@EnableTransactionalOutbox
@RabbitListenerTest
@SpringBootApplication(scanBasePackages = "com.curtisnewbie")
@PropertySource("classpath:application.properties")
public class TestBaseConfig {

    @Bean
    public SimpleRabbitListenerContainerFactory containerFactoryBean(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory f = new SimpleRabbitListenerContainerFactory();
        f.setConnectionFactory(connectionFactory);
        return f;
    }

    @Value("${redisson-config}")
    private String redissonConfig;

    @Bean
    public RedissonClient redissonClient() throws IOException {
        Config config = Config.fromYAML(this.getClass().getClassLoader().getResourceAsStream(redissonConfig));
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

}
