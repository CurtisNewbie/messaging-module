package com.curtisnewbie.module.messaging.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Helper for using {@link MessagingService} in a static way
 *
 * @author yongj.zhuang
 */
@Component
public class MqHelper implements InitializingBean {

    @Autowired(required = false)
    private MessagingService messagingService = null;

    private static volatile MessagingService _staticMessagingService = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        _staticMessagingService = messagingService;
    }

    public static MessagingService getCachedMessagingService() {
        Assert.notNull(_staticMessagingService, "Internally cached MessagingService is not found, func is called before spring context fully "
                + "initialized or the bean is simply not found");
        return _staticMessagingService;
    }

    public static void send(MessagingParam param) {
        getCachedMessagingService().send(param);
    }

    public static void send(Object payload, String exchange) {
        getCachedMessagingService().send(payload, exchange);
    }

    public static void send(Object payload, String exchange, String routingKey) {
        getCachedMessagingService().send(payload, exchange, routingKey);
    }
}
