package com.curtisnewbie.module.messaging;

import com.curtisnewbie.module.messaging.listener.EnableMsgListener;
import com.curtisnewbie.module.messaging.listener.MsgListener;
import com.curtisnewbie.module.messaging.service.MessagingService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * @author yongj.zhuang
 */
@Configuration
@Slf4j
@EnableMsgListener
@SpringBootTest(classes = {TestBaseConfig.class, MsgListenerRetryTest.class})
public class MsgListenerRetryTest {

    public static final String QUEUE = "msgListenerRetryTest";
    public static final String EXCHANGE = "msgListenerRetryExg";

    @Autowired
    private MessagingService messagingService;

    @Test
    public void should_receive_message() {
        MessagingServiceTest.DemoBean db = new MessagingServiceTest.DemoBean();
        db.setName("Mega Mug");
        messagingService.send(db, EXCHANGE);

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(10));
        Assertions.assertEquals(5, CustomListener.receiveCount.get());
    }

    @Component
    public static class CustomListener {

        public static final AtomicInteger receiveCount = new AtomicInteger(0);

        @MsgListener(queue = QUEUE, exchange = EXCHANGE)
        public void customMsgListener(MessagingServiceTest.DemoBean demoBean) {
            log.info("Received: {}", receiveCount.incrementAndGet());
            throw new IllegalStateException("Intended exception");
        }

    }

}
