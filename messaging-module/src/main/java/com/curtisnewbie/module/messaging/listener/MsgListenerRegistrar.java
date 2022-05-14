package com.curtisnewbie.module.messaging.listener;

import com.curtisnewbie.module.messaging.config.MessagingModuleProperties;
import com.curtisnewbie.module.messaging.exception.MsgListenerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.String.*;

/**
 * Registration of {@link MsgListener} beans
 *
 * @author yongj.zhuang
 */
@Slf4j
public class MsgListenerRegistrar implements RabbitListenerConfigurer, InitializingBean {

    private RetryTemplate retryTemplate;

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private MessageConverter messageConverter;
    @Autowired
    private AmqpAdmin amqpAdmin;
    @Autowired
    private MessagingModuleProperties properties;
    @Autowired(required = false)
    @Nullable
    private RabbitListenerContainerFactory<?> rabbitListenerContainerFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (properties.isConcurrentDeclaration())
            log.info("Concurrent declaration of Queue, Exchange and Binding is enabled");

        retryTemplate = defaultRetryTemplate();
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        final String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanDefinitionNames) {
            final Object bean = applicationContext.getBean(beanName);
            final Class<?> clz = bean.getClass();
            final Method[] declaredMethods = clz.getDeclaredMethods();
            for (Method m : declaredMethods) {
                final MsgListener msgListener = m.getDeclaredAnnotation(MsgListener.class);
                if (msgListener == null)
                    continue;

                // try to declare queue, exchange, and bindings
                declareBindings(msgListener, properties.isConcurrentDeclaration());

                // register reflective listener for the endpoint
                final String queueName = msgListener.queue();
                final SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
                endpoint.setId(endpointId(queueName));
                endpoint.setQueueNames(queueName);
                endpoint.setAckMode(msgListener.ackMode());
                endpoint.setMessageListener(new ReflectiveMessageListener(retryTemplate, m, messageConverter, bean));
                registrar.registerEndpoint(endpoint, rabbitListenerContainerFactory);
            }
        }
    }

    /** Default retry template */
    public RetryTemplate defaultRetryTemplate() {
        final RetryTemplate rt = new RetryTemplate();

        // backoff
        final ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(properties.getBackOffInitialInterval());
        backOffPolicy.setMultiplier(properties.getBackOffMultiplier());
        backOffPolicy.setMaxInterval(properties.getBackOffMaxInterval());
        rt.setBackOffPolicy(backOffPolicy);

        // retry
        final SimpleRetryPolicy rp = new SimpleRetryPolicy();
        rp.setMaxAttempts(properties.getTryMaxAttempt());

        rt.setRetryPolicy(rp);
        return rt;
    }

    protected void declareBindings(MsgListener msgListener) {
        // always declare queue
        final Queue queue = new Queue(msgListener.queue(), true);
        amqpAdmin.declareQueue(queue);
        log.info("Declared queue: {}", queue);

        // exchange and binding
        final String exchangeName = msgListener.exchange();
        if (!Objects.equals(exchangeName, MsgListener.NONE)) {
            final DirectExchange exchange = new DirectExchange(exchangeName, true, false);
            amqpAdmin.declareExchange(exchange);
            log.info("Declared exchange: {}", exchange);

            final Binding binding = BindingBuilder.bind(queue).to(exchange).with(msgListener.routingKey());
            amqpAdmin.declareBinding(binding);
            log.info("Declared binding: {}", binding);
        }
    }

    protected void declareBindings(MsgListener msgListener, boolean isConcurrentRegistration) {
        if (isConcurrentRegistration)
            CompletableFuture.runAsync(() -> declareBindings(msgListener))
                    .exceptionally(e -> {
                        log.error("Failed to declare Queue, Exchange or Binding", e);
                        return null;
                    });
        else
            declareBindings(msgListener);
    }

    protected String endpointId(String queue) {
        return queue + "-" + timestamp(5);
    }

    public static String timestamp(int lastNDigits) {
        final String t = System.currentTimeMillis() + "";
        final int len = t.length();
        if (lastNDigits > len)
            return t;
        return t.substring(len - lastNDigits, len);
    }

    public static class ReflectiveMessageListener implements MessageListener {

        private final RetryTemplate retryTemplate;
        private final Method m;
        private final MessageConverter messageConverter;
        private final Object bean;

        public ReflectiveMessageListener(RetryTemplate retryTemplate, Method m, MessageConverter messageConverter, Object bean) {
            this.retryTemplate = retryTemplate;
            this.m = m;
            this.messageConverter = messageConverter;
            this.bean = bean;
        }

        @Override
        public void onMessage(Message message) {
            retryTemplate.execute((ctx) -> {
                try {
                    m.invoke(bean, messageConverter.fromMessage(message));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new MsgListenerException(format("Failed to invoke @MsgListener annotated method '%s'", m), e);
                }
                return null;
            }, (ctx) -> {
                // do nothing, because the retry is exhausted already, we just silently drop the message
                log.error("Failed to retry @MsgListener, message is dropped to prevent indefinite redelivery", ctx.getLastThrowable());
                return null;
            });
        }
    }
}
