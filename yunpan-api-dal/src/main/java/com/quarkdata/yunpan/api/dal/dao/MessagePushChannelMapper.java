package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.dataobj.MessagePushChannel;
import com.quarkdata.yunpan.api.model.dataobj.MessagePushChannelExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MessagePushChannelMapper {
    long countByExample(MessagePushChannelExample example);

    int deleteByExample(MessagePushChannelExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MessagePushChannel record);

    int insertSelective(MessagePushChannel record);

    List<MessagePushChannel> selectByExample(MessagePushChannelExample example);

    MessagePushChannel selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MessagePushChannel record, @Param("example") MessagePushChannelExample example);

    int updateByExample(@Param("record") MessagePushChannel record, @Param("example") MessagePushChannelExample example);

    int updateByPrimaryKeySelective(MessagePushChannel record);

    int updateByPrimaryKey(MessagePushChannel record);

    MessagePushChannel selectForUpdate(Long id);
}