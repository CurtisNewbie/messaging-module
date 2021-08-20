package com.curtisnewbie.module.messaging.tracing;

import org.springframework.amqp.core.Message;

/**
 * <p>
 * Utility class for tracing in messages
 * </p>
 * <p>
 * This class uses 'headers' in {@link org.springframework.amqp.core.MessageProperties}, so it will affect header
 * exchange.
 * </p>
 *
 * @author yongjie.zhuang
 */
public final class MessageTracingUtil {

    private static final String TRACE_ID = "traceId";

    private MessageTracingUtil(){}

    /**
     * Set traceId
     *
     * @param message message
     * @param traceId traceId
     */
    public static void setTraceId(Message message, String traceId) {
        message.getMessageProperties().setHeader(TRACE_ID, traceId);
    }

    /**
     * Get traceId
     *
     * @param message message
     * @return traceId
     */
    public static String getTraceId(Message message) {
        return message.getMessageProperties().getHeader(TRACE_ID);
    }

}
