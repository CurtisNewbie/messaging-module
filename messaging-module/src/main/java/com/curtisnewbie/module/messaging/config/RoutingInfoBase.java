package com.curtisnewbie.module.messaging.config;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Base class that stores routing information
 *
 * @author yongjie.zhuang
 */
@Data
@AllArgsConstructor
public class RoutingInfoBase implements RoutingInfo {

    /**
     * Exchange name
     */
    private String exchange;

    /**
     * Routing key
     */
    private String routingKey;
}
