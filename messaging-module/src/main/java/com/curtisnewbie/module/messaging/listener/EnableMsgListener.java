package com.curtisnewbie.module.messaging.listener;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Enable {@link MsgListener} registration
 *
 * @author yongj.zhuang
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
@Import(MsgListenerRegistrar.class)
public @interface EnableMsgListener {

}
