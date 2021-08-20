package com.curtisnewbie.module.messaging.config;

/**
 * Routing information that contains exchange name and routing key
 *
 * @author yongjie.zhuang
 */
public interface RoutingInfo {

    /** Get exchange name */
    String getExchange();

    /** Get routing key */
    String getRoutingKey();
}
