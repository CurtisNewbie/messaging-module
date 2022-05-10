package com.curtisnewbie.module.messaging.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Registration of {@link MsgListener} beans
 *
 * @author yongj.zhuang
 */
@Slf4j
public class MsgListenerRegistration implements RabbitListenerConfigurer {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private MessageConverter messageConverter;
    @Autowired
    private AmqpAdmin amqpAdmin;

    /**
     * Whether the declaration is concurrent (executed in CompletableFuture)
     * <p>
     * Because it's executed in CompletableFuture, when exceptions are
     * thrown by the declaration methods, it won't stop the spring application bootstrap
     */
    @Value("${messaging.endpoint.concurrent-declaration: false}")
    private boolean isConcurrentDeclaration;

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
                declareBindings(msgListener, isConcurrentDeclaration);

                // register reflective listener for the endpoint
                final String queueName = msgListener.queue();
                final SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
                endpoint.setId(endpointId(queueName));
                endpoint.setQueueNames(queueName);
                endpoint.setMessageListener(message -> {
                    try {
                        m.invoke(bean, messageConverter.fromMessage(message));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new IllegalStateException("Failed to invoke method on @MsgListener method", e);
                    }
                });
                registrar.registerEndpoint(endpoint);
            }
        }
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
                        log.error("Failed to declare queue/exchange/binding", e);
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
}
