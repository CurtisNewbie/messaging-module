package com.curtisnewbie.module.messaging;

import com.curtisnewbie.module.messaging.service.MessagingParam;
import com.curtisnewbie.module.messaging.service.MessagingService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.amqp.rabbit.test.mockito.LatchCountDownAndCallRealMethodAnswer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @author yongjie.zhuang
 */
@Configuration
@Slf4j
@SpringBootTest(classes = {TestBaseConfig.class, MessagingServiceTest.class})
public class MessagingServiceTest {

    public static final String MOCKED_LISTENER_ID = "demoBeanListener";
    public static final String TEST_EXCHANGE = "test-exchange";
    public static final String TEST_QUEUE = "test-queue";
    public static final String DEMO_BEAN_NAME = "Monkas";

    @Autowired
    private MessagingService messagingService;

    @Autowired
    private RabbitListenerTestHarness harness;

    @Captor
    ArgumentCaptor<DemoBean> demoBeanCaptor;

    @Test
    public void shouldSend() throws InterruptedException {

        // spied listener
        Listener spiedListener = harness.getSpy(MOCKED_LISTENER_ID);
        Assertions.assertNotNull(spiedListener);

        // listener should only receive one message
        LatchCountDownAndCallRealMethodAnswer answer = this.harness.getLatchAnswerFor(MOCKED_LISTENER_ID,
                1);
        Mockito.doAnswer(answer).when(spiedListener).handle(Mockito.any());

        DemoBean payload = new DemoBean(DEMO_BEAN_NAME);
        Assertions.assertDoesNotThrow(() -> {
            // should be able to send the message
            messagingService.send(MessagingParam.builder()
                    .payload(payload)
                    .exchange(TEST_EXCHANGE)
                    .routingKey(TEST_QUEUE)
                    .build());
        });

        // should receive message before timeout 
        Assertions.assertTrue(answer.await(10));

        // verify that the listener is invoked 
        Mockito.verify(spiedListener).handle(demoBeanCaptor.capture());

        // verify that the payload received matches the one sent
        Assertions.assertTrue(demoBeanCaptor.getValue().equals(payload));
    }

    /**
     * @author yongjie.zhuang
     */
    @Slf4j
    @Component
    public static class Listener {

        @RabbitListener(
                id = MOCKED_LISTENER_ID,
                queues = TEST_QUEUE
        )
        public void handle(@Payload DemoBean demoBean) {
            log.info("Received {}", demoBean);
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
        return new Queue(MessagingServiceTest.TEST_QUEUE, false, false, false);
    }

    @Bean
    public Exchange exchange() {
        return new DirectExchange(MessagingServiceTest.TEST_EXCHANGE, false, false);
    }

    @Bean
    public Binding binding() {
        return new Binding(MessagingServiceTest.TEST_QUEUE, Binding.DestinationType.QUEUE, MessagingServiceTest.TEST_EXCHANGE,
                MessagingServiceTest.TEST_QUEUE, null);
    }
}
