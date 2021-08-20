package com.curtisnewbie.module.messaging.service;

import com.curtisnewbie.module.messaging.tracing.MessageTracingUtil;
import com.curtisnewbie.module.messaging.tracing.MessageTracingConfig;
import com.curtisnewbie.module.tracing.common.MdcUtil;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author yongjie.zhuang
 */
@Service
public class MessagingServiceImpl implements MessagingService {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private MessageTracingConfig messageTracingConfig;

    @Override
    public void send(@NotNull @Valid MessagingParam param) {
        send(param.getPayload(), param.getExchange(), param.getRoutingKey(), param.getDeliveryMode(), param.getCorrelationData());
    }

    @Override
    public void send(@NotNull Object msg, @NotEmpty String exchange, @NotEmpty String routingKey) {
        send(msg, exchange, routingKey, MessageDeliveryMode.PERSISTENT, null);
    }

    @Override
    public void send(@NotNull Object msg, @NotEmpty String exchange, @NotEmpty String routingKey, @NotNull MessageDeliveryMode deliveryMode,
                     @Nullable CorrelationData correlationData) {
        rabbitTemplate.convertAndSend(exchange, routingKey, msg,
                new GeneralPropertiesMessagePostProcessor(deliveryMode, messageTracingConfig.isEnabled()),
                correlationData);
    }

    private static class GeneralPropertiesMessagePostProcessor implements MessagePostProcessor {
        private final MessageDeliveryMode deliveryMode;
        private final boolean tracingEnabled;

        public GeneralPropertiesMessagePostProcessor(MessageDeliveryMode deliveryMode, boolean tracingEnabled) {
            this.deliveryMode = deliveryMode;
            this.tracingEnabled = tracingEnabled;
        }

        @Override
        public Message postProcessMessage(Message message) throws AmqpException {
            message.getMessageProperties().setDeliveryMode(deliveryMode);
            message.getMessageProperties().setTimestamp(new Date());
            if (tracingEnabled) {
                // don't use it for header exchange
                String traceId = MdcUtil.getTraceId();
                if (traceId != null)
                    MessageTracingUtil.setTraceId(message, traceId);
            }
            return message;
        }
    }
}

