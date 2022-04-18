package com.curtisnewbie.module.messaging.tracing;

import com.curtisnewbie.module.tracing.common.*;
import lombok.extern.slf4j.*;
import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.springframework.amqp.core.*;
import org.springframework.stereotype.*;

/**
 * Aspect for Message Listener Tracing
 *
 * @author yongj.zhuang
 */
@Slf4j
@Component
@Aspect
public class MessageListenerTracingAspect {

    @Pointcut("within(org.springframework.amqp.core.MessageListener+)")
    public void listenerPointCut() {

    }

    @Around("controllerPointcut() && && execution(* onMessage(..))")
    public Object methodCall(ProceedingJoinPoint pjp) throws Throwable {
        try {
            final Object[] args = pjp.getArgs();
            for (int i = 0; i < args.length; i++) {
                final Object ag = args[i];
                if (ag instanceof Message) {
                    MdcUtil.setTraceId(MessageTracingUtil.getTraceId((Message) ag));
                    break;
                }
            }
            return pjp.proceed(args);
        } finally {
            MdcUtil.removeTraceId();
        }
    }

}
