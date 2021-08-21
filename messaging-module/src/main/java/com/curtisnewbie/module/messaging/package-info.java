/**
 * <h3>messaging-module</h3>
 * <p>
 * This module internally uses RabbitMQ for async messaging.
 * </p>
 * <p>
 * {@code RabbitTemplate} is automatically populated, so you don't need to populate this bean using {@code @Bean} or
 * xml-based configuration. But you can configure the RabbitTemplate before it's populated using {@link
 * com.curtisnewbie.module.messaging.config.RabbitTemplateConfigurer}. However, you do need to provide the {@code
 * ConnectionFactory} bean, a convenient factory method is provided to create such bean using properties-based
 * configuration, see {@link com.curtisnewbie.module.messaging.config.SimpleConnectionFactoryBeanFactory#createByProperties(org.springframework.core.env.Environment)}.
 * </p>
 * <p>
 * MessageConverter is automatically configured to use {@link org.springframework.amqp.support.converter.Jackson2JsonMessageConverter},
 * callbacks for publisher confirm and message returns are also automatically configured, to provide callback that
 * handles the 'ack'/'nack' messages or returned messages, simply implement beans {@link
 * com.curtisnewbie.module.messaging.callback.MessageConfirmCallback} and {@link com.curtisnewbie.module.messaging.callback.MessageReturnsCallback}.
 * But these features also depends on other settings, such as, the confirm-type on {@code ConnectionFactory} and {@code
 * MessageDeliveryMode} on messages.
 * </p>
 * <p>
 * To send messages, simply inject and use {@link com.curtisnewbie.module.messaging.service.MessagingService}.
 * </p>
 * <p>
 * Queues, Exchanges, and Listeners are created on your own, through any ways you like.
 * </p>
 */
package com.curtisnewbie.module.messaging;