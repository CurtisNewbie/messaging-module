package com.curtisnewbie.module.messaging.listener;

import com.curtisnewbie.common.util.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * <p>
 * Abstract message Listener adapter that deserializes json string
 * </p>
 * <p>
 * This is for those publisher that do not use jackson message converter to serialize object to json, the headers are
 * not found in these messages, so the jackson message converter cannot deserialize them correctly. Content type of
 * Message published to this kind of listener should be "text/*" rather than "application/json"
 * </p>
 * <p>
 * It uses the method name 'handleMessageInternal', so when configuring such bean in xml or listener container, don't
 * specify method name to use it. All you need to do is to implement {@link #handle(Object, Message)}}.
 * </p>
 * <p>
 * Note that <b>do not overload {@link #handle(Object, Message)} method</b>.
 * </p>
 * Examples:
 * <pre>
 * {@code
 * public class JsonStrListener extends AbstractJsonDeserializedListenerAdapter<DemoBean> {
 *
 *      @Override
 *      public void handle(DemoBean demoBean, Message message) {
 *          // do something
 *      }
 * }
 * }
 * </pre>
 *
 * @author yongjie.zhuang
 */
@Slf4j
public abstract class AbstractJsonDeserializedListenerAdapter<T> extends MessageListenerAdapter {

    private static final String INHERITED_METHOD_NAME = "handle";
    private final Class<T> tClazz;

    protected AbstractJsonDeserializedListenerAdapter() {
        Method[] methods = this.getClass().getMethods();
        Method handleMethod = null;
        for (Method m : methods) {
            if (m.getName().equals(INHERITED_METHOD_NAME)) {

                // the first one being the pojo and the second one being the Message object
                if (m.getParameterCount() != 2 && Objects.equals(m.getParameterTypes()[1], org.springframework.messaging.Message.class))
                    continue;

                handleMethod = m;
                break;
            }
        }
        try {
            Assert.notNull(handleMethod, "Unable to find method \"handle(T t, Message message)\"");
            this.tClazz = (Class<T>) Class.forName(handleMethod.getGenericParameterTypes()[0].getTypeName());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Unable to resolve type of T");
        }
        log.info("T: {}", tClazz);
    }

    /**
     * <p>
     * On message received
     * </p>
     *
     * @param t       message deserialized as object
     * @param message message
     */
    public abstract void handle(T t, Message message);

    /**
     * Method invoked by the listener container, this is where json string deserialized as T object
     */
    private void handleMessageInternal(String json, Message message) {
        try {
            final T t = JsonUtils.readValueAsObject(json, tClazz);
            handle(t, message);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to deserialize json", e);
        }
    }

    @Override
    protected String getListenerMethodName(Message originalMessage, Object extractedMessage) {
        return "handleMessageInternal";
    }

    @Override
    protected Object[] buildListenerArguments(Object extractedMessage, Channel channel, Message message) {
        return new Object[]{extractedMessage, message};
    }

}
