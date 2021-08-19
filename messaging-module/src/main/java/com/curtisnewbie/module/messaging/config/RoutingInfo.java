package com.curtisnewbie.module.messaging.config;

/**
 * Routing information
 *
 * @author yongjie.zhuang
 */
public interface RoutingInfo {

    /** Get exchange name */
    String getExchange();

    /** Get routing key */
    String getRoutingKey();
}
