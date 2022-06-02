package com.curtisnewbie.module.messaging.outbox.dao;

import com.curtisnewbie.common.util.EnhancedMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Mapper for message
 *
 * @author yongj.zhuang
 */
@Component
@Mapper
public interface MessageMapper extends EnhancedMapper<MessageOutbox> {

}
