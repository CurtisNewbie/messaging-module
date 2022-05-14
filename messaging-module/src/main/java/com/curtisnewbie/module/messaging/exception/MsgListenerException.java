package com.curtisnewbie.module.messaging.exception;


/**
 * Exception for {@link com.curtisnewbie.module.messaging.listener.MsgListener}
 *
 * @author yongj.zhuang
 */
public class MsgListenerException extends RuntimeException {

    public MsgListenerException() {
    }

    public MsgListenerException(String message) {
        super(message);
    }

    public MsgListenerException(String message, Throwable cause) {
        super(message, cause);
    }

    public MsgListenerException(Throwable cause) {
        super(cause);
    }
}
