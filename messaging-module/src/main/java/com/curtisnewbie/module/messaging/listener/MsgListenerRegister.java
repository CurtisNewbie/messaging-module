package com.curtisnewbie.module.messaging.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Registration of {@link MsgListener} beans
 *
 * @author yongj.zhuang
 */
@Slf4j
public class MsgListenerRegister implements RabbitListenerConfigurer {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private MessageConverter messageConverter;
    @Autowired
    private AmqpAdmin amqpAdmin;

    @Value("${spring.application.name: anonymous}")
    private String appName;

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
                declareBindings(msgListener);

                // register reflective listener for the endpoint
                final SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
                endpoint.setId(appName + "-" + msgListener.queue() + "-" + timestamp());
                endpoint.setQueueNames(msgListener.queue());
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

    protected String timestamp() {
        final String t = System.currentTimeMillis() + "";
        final int len = t.length();
        return t.substring(len - 5, len);
    }
}
