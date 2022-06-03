package com.curtisnewbie.module.messaging.config;

import com.curtisnewbie.module.messaging.outbox.components.DBOutbox;
import com.curtisnewbie.module.messaging.outbox.components.DispatchLoop;
import com.curtisnewbie.module.messaging.outbox.components.Outbox;
import com.curtisnewbie.module.messaging.service.MessagingService;
import com.curtisnewbie.module.redisutil.RedisController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

/**
 * Configuration for Transactional Outbox
 *
 * @author yongj.zhuang
 */
@Slf4j
public class TransactionalOutboxConfiguration {

    @Bean
    public DispatchLoop dispatchLoop(MessagingService messagingService, Outbox outbox, RedisController redisController) {
        log.info("Transactional-Outbox enabled, populating DispatchLoop");
        final Supplier<Lock> lockSupplier = () -> redisController.getLock("message:dispatchloop:global");
        return new DispatchLoop(outbox::_pull, outbox::_setDispatched, messagingService, lockSupplier);
    }

    @Bean
    public Outbox outbox() {
        log.info("Transactional-Outbox enabled, populating Outbox");
        return new DBOutbox();
    }

}
