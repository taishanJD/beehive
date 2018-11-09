package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.dataobj.ExternalLinkMsgNotification;
import com.quarkdata.yunpan.api.model.dataobj.ExternalLinkMsgNotificationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ExternalLinkMsgNotificationMapper {
    long countByExample(ExternalLinkMsgNotificationExample example);

    int deleteByExample(ExternalLinkMsgNotificationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ExternalLinkMsgNotification record);

    int insertSelective(ExternalLinkMsgNotification record);

    List<ExternalLinkMsgNotification> selectByExample(ExternalLinkMsgNotificationExample example);

    ExternalLinkMsgNotification selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ExternalLinkMsgNotification record, @Param("example") ExternalLinkMsgNotificationExample example);

    int updateByExample(@Param("record") ExternalLinkMsgNotification record, @Param("example") ExternalLinkMsgNotificationExample example);

    int updateByPrimaryKeySelective(ExternalLinkMsgNotification record);

    int updateByPrimaryKey(ExternalLinkMsgNotification record);

    ExternalLinkMsgNotification selectForUpdate(Long id);
}