package com.curtisnewbie.module.messaging.outbox.components;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.curtisnewbie.common.util.JsonUtils;
import com.curtisnewbie.module.messaging.outbox.dao.MessageOutbox;
import com.curtisnewbie.module.messaging.outbox.dao.MessageMapper;
import com.curtisnewbie.module.messaging.service.MessagingParam;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yongj.zhuang
 */
public class DBOutbox implements Outbox {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public void push(MessagingParam mp) {
        MessageOutbox m = new MessageOutbox();
        m.setRoutingKey(mp.getRoutingKey());
        m.setExchange(mp.getExchange());
        m.setStatus(DispatchStatus.DISPATCHING);
        m.setPayload(payloadToJson(mp.getPayload()));
        messageMapper.insert(m);
    }

    @Override
    public List<MessageOutbox> _pull(int limit) {
        return messageMapper.selectList(new LambdaQueryWrapper<MessageOutbox>()
                .eq(MessageOutbox::getStatus, DispatchStatus.DISPATCHING)
                .orderByAsc(MessageOutbox::getId)
                .last("limit " + limit));
    }

    @Override
    public void _setDispatched(int id) {
        MessageOutbox m = new MessageOutbox();
        m.setStatus(DispatchStatus.DISPATCHED);
        m.setDeliveryTime(LocalDateTime.now());
        messageMapper.updateOneEq(MessageOutbox::getId, id, m);
    }

    // --------------------------------- private helper methods ----------------------------

    private String payloadToJson(Object o) {
        if (o == null) return "";
        try {
            return JsonUtils.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize payload to json string", e);
        }
    }
}
