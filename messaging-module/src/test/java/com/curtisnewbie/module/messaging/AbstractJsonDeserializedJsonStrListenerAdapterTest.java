package com.curtisnewbie.module.messaging;

import com.curtisnewbie.common.util.JsonUtils;
import com.curtisnewbie.module.messaging.config.RabbitTemplateConfigurer;
import com.curtisnewbie.module.messaging.service.MessagingParam;
import com.curtisnewbie.module.messaging.service.MessagingService;
import com.curtisnewbie.module.messaging.listener.AbstractJsonDeserializedListenerAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author yongjie.zhuang
 */
@Configuration
@Slf4j
@SpringBootTest(classes = {TestBaseConfig.class, AbstractJsonDeserializedJsonStrListenerAdapterTest.class})
public class AbstractJsonDeserializedJsonStrListenerAdapterTest {

    public static final String MOCKED_LISTENER_ID_1 = "demoBeanListener-1";
    public static final String TEST_EXCHANGE_1 = "test-exchange-1";
    public static final String TEST_QUEUE_1 = "test-queue-1";
    public static final String DEMO_BEAN_NAME_1 = "Pog-";

    @Autowired
    private MessagingService messagingService;

    @Test
    public void shouldSend() throws InterruptedException {

        // listener should only receive one message
        DemoBean payload = new DemoBean(DEMO_BEAN_NAME_1);
        Assertions.assertDoesNotThrow(() -> {
            // should be able to send the message
            messagingService.send(MessagingParam.builder()
                    .payload(JsonUtils.writeValueAsString(payload))
                    .exchange(TEST_EXCHANGE_1)
                    .routingKey(TEST_QUEUE_1)
                    .build());
        });

        // should receive message before timeout 
        Assertions.assertTrue(JsonStrListener.latch.await(10, TimeUnit.SECONDS));

        // verify that the listener is invoked, and the received bean equals
        Assertions.assertTrue(Objects.equals(JsonStrListener.bean, payload));
    }

    @Bean
    public MessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) {
        DirectMessageListenerContainer messageListenerContainer = new DirectMessageListenerContainer();
        messageListenerContainer.setMessageListener(new JsonStrListener());
        messageListenerContainer.setQueueNames(TEST_QUEUE_1);
        messageListenerContainer.setListenerId(MOCKED_LISTENER_ID_1);
        messageListenerContainer.setConnectionFactory(connectionFactory);
        return messageListenerContainer;
    }

    @Bean
    public RabbitTemplateConfigurer configurer() {
        return new RabbitTemplateConfigurer() {
            @Override
            public RabbitTemplate configure(RabbitTemplate rabbitTemplate) {
                // override the default jackson message converter
                rabbitTemplate.setMessageConverter(new SimpleMessageConverter());
                return rabbitTemplate;
            }
        };
    }

    /**
     * @author yongjie.zhuang
     */
    @Slf4j
    public static class JsonStrListener extends AbstractJsonDeserializedListenerAdapter<DemoBean> {

        public static DemoBean bean;
        public static final CountDownLatch latch = new CountDownLatch(1);

        @Override
        public void handle(DemoBean demoBean, Message message) {
            synchronized (JsonStrListener.class) {
                latch.countDown();
                bean = demoBean;
                log.info("Received {}", demoBean);
            }
        }
    }

    /**
     * @author yongjie.zhuang
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DemoBean {

        private String name;

    }

    @Bean
    public Queue queue() {
        return new Queue(AbstractJsonDeserializedJsonStrListenerAdapterTest.TEST_QUEUE_1, false, false, false);
    }

    @Bean
    public Exchange exchange() {
        return new DirectExchange(AbstractJsonDeserializedJsonStrListenerAdapterTest.TEST_EXCHANGE_1, false, false);
    }

    @Bean
    public Binding binding() {
        return new Binding(AbstractJsonDeserializedJsonStrListenerAdapterTest.TEST_QUEUE_1, Binding.DestinationType.QUEUE,
                AbstractJsonDeserializedJsonStrListenerAdapterTest.TEST_EXCHANGE_1,
                AbstractJsonDeserializedJsonStrListenerAdapterTest.TEST_QUEUE_1, null);
    }
}
