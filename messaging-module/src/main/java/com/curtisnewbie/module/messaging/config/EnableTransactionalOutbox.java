package com.curtisnewbie.module.messaging.config;

import com.curtisnewbie.module.messaging.outbox.components.Outbox;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.lang.annotation.*;

/**
 * Enable Transactional outbox
 *
 * @author yongj.zhuang
 * @see Outbox
 * @see MessagingModuleAutoConfiguration
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@EnableScheduling
@Import({TransactionalOutboxConfiguration.class})
public @interface EnableTransactionalOutbox {
}
