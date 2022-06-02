package com.curtisnewbie.module.messaging.outbox.dao;

import com.curtisnewbie.common.util.EnhancedMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for message
 *
 * @author yongj.zhuang
 */
@Mapper
public interface MessageMapper extends EnhancedMapper<MessageOutbox> {

}
