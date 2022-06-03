package com.curtisnewbie.module.messaging.outbox.dao;

import com.curtisnewbie.common.util.EnhancedMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Mapper for message_outbox
 *
 * @author yongj.zhuang
 */
@Component
@Mapper
public interface MessageOutboxMapper extends EnhancedMapper<MessageOutbox> {

}
