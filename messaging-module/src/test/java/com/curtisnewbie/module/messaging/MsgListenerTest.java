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

import java.beans.Customizer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

/**
 * @author yongj.zhuang
 */
@Configuration
@Slf4j
@EnableMsgListener
@SpringBootTest(classes = {TestBaseConfig.class, MsgListenerTest.class})
public class MsgListenerTest {

    public static final String QUEUE = "msgListenerTest";
    public static final String EXCHANGE = "msgListenerExg";

    @Autowired
    private MessagingService messagingService;

    @Test
    public void should_receive_message() {
        MessagingServiceTest.DemoBean db = new MessagingServiceTest.DemoBean();
        db.setName("MsgListenerTestDemoBean");
        messagingService.send(db, EXCHANGE);

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));
        Assertions.assertTrue(CustomListener.isReceived.get());
    }

    @Component
    public static class CustomListener {

        public static final AtomicBoolean isReceived = new AtomicBoolean(false);

        @MsgListener(queue = QUEUE, exchange = EXCHANGE)
        public void customMsgListener(MessagingServiceTest.DemoBean demoBean) {
            log.info("Received demoBean: {}", demoBean);
            isReceived.set(true);
        }

    }

}
