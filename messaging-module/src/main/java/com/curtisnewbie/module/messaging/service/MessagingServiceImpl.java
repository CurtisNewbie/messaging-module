package com.curtisnewbie.module.messaging.service;

import com.curtisnewbie.module.tracing.common.MdcUtil;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public void send(@NotNull @Valid MessagingParam param) {
        MessagePostProcessor mpp = param.getMessagePostProcessor();
        mpp = new GeneralPropertiesMessagePostProcessor(param.getDeliveryMode())
                .wrap(mpp);

        rabbitTemplate.convertAndSend(param.getExchange(),
                param.getRoutingKey(),
                param.getPayload(),
                mpp,
                param.getCorrelationData());
    }

    @Override
    public void send(@NotNull(message = "payload can't be null") Object payload,
                     @NotEmpty(message = "exchange can't be empty") String exchange) {
        send(MessagingParam.builder()
                .payload(payload)
                .exchange(exchange)
                .deliveryMode(MessageDeliveryMode.PERSISTENT)
                .build());
    }

    private static class GeneralPropertiesMessagePostProcessor implements MessagePostProcessor {
        private final MessageDeliveryMode deliveryMode;
        private MessagePostProcessor delegate = null;

        public GeneralPropertiesMessagePostProcessor(MessageDeliveryMode deliveryMode) {
            this.deliveryMode = deliveryMode != null ? deliveryMode : MessageDeliveryMode.PERSISTENT;
        }

        @Override
        public Message postProcessMessage(Message message) throws AmqpException {
            message.getMessageProperties().setDeliveryMode(deliveryMode);
            message.getMessageProperties().setTimestamp(new Date());

            if (delegate != null)
                return delegate.postProcessMessage(message);
            else return message;
        }

        /** Wrap the {@link org.springframework.amqp.core.MessagePostProcessor} */
        public MessagePostProcessor wrap(MessagePostProcessor mpp) {
            if (this.delegate != null)
                throw new IllegalStateException("Already wrapped a post processor");

            this.delegate = mpp;
            return this;
        }
    }
}

