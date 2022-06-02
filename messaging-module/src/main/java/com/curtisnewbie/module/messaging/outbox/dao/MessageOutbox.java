package com.curtisnewbie.module.messaging.outbox.dao;

import java.time.*;

import com.baomidou.mybatisplus.annotation.*;

import com.curtisnewbie.common.dao.DaoSkeleton;
import com.curtisnewbie.module.messaging.outbox.components.DispatchStatus;
import lombok.*;

/**
 * Message Outbox
 *
 * @author yongj.zhuang
 */
@Data
@TableName(value = "message_outbox")
public class MessageOutbox extends DaoSkeleton {

    /** routing key */
    @TableField("routing_key")
    private String routingKey;

    /** name of exchange */
    @TableField("exchange")
    private String exchange;

    /** delivery status */
    @TableField("status")
    private DispatchStatus status;

    /** payload */
    @TableField("payload")
    private String payload;

    /** payload type name */
    @TableField("type_name")
    private String typeName;

    /** delivery time */
    @TableField("delivery_time")
    private LocalDateTime deliveryTime;

}
