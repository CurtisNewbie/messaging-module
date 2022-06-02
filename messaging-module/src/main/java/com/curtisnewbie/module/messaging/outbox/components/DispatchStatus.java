package com.curtisnewbie.module.messaging.outbox.components;

/**
 * Dispatch Status
 *
 * @author yongj.zhuang
 */
public enum DispatchStatus {

    /** Message is being dispatched */
    DISPATCHING,

    /** Message is dispatched already */
    DISPATCHED,

    /** Message is not dispatched, it's marked as failed and thus will not be retried */
    FAILED;

}
