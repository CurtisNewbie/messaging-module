package com.curtisnewbie.module.messaging.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import javax.annotation.PostConstruct;

/**
 * Properties for message-module
 *
 * @author yongj.zhuang
 */
@Slf4j
@Getter
@Configuration
public class MessagingModuleProperties {

    /**
     * Whether the declaration is concurrent (executed in CompletableFuture)
     * <p>
     * Because it's executed in CompletableFuture, when exceptions are thrown by the declaration methods, it won't stop
     * the spring application bootstrap
     */
    @Value("${messaging.concurrent-declaration: false}")
    private boolean concurrentDeclaration;

    /**
     * Max attempt for {@link RetryTemplate}
     */
    @Value("${messaging.retry.max-attempt: 8}")
    private int tryMaxAttempt;

    /**
     * Initial Interval for {@link ExponentialBackOffPolicy}
     */
    @Value("${messaging.retry.backoff.initial-interval: 500}")
    private int backOffInitialInterval;

    /**
     * Multiplier for {@link ExponentialBackOffPolicy}
     */
    @Value("${messaging.retry.backoff.multiplier: 2}")
    private int backOffMultiplier;

    /**
     * Max Interval for {@link ExponentialBackOffPolicy}
     */
    @Value("${messaging.retry.backoff.max-interval: 5000}")
    private int backOffMaxInterval;

}
