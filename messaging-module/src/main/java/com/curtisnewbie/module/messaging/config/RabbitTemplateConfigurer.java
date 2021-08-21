package com.curtisnewbie.module.messaging.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * <p>
 * Configurer of RabbitTemplate
 * </p>
 * <p>
 * This configurer is used before populating {@code RabbitTemplate} bean, and it provides a chance to override some of
 * the settings used in this module.
 * </p>
 *
 * @author yongjie.zhuang
 */
public interface RabbitTemplateConfigurer {

    /**
     * Configure the given {@code RabbitTemplate}
     *
     * @param rabbitTemplate
     */
    RabbitTemplate configure(RabbitTemplate rabbitTemplate);

}

