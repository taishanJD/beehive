package com.quarkdata.yunpan.api.dal.dao;

import com.quarkdata.yunpan.api.model.vo.MessageExtend;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageMapper2 {

    /**
     * 查询未读用户消息
     * @param userId
     * @param incID
     * @return
     */
    List<MessageExtend> getMessage(@Param("userId")Long userId,@Param("incId")Long incID);
    
    /**
     * 查询用户所有消息（未读+已读）
     * @param userId
     * @param incID
     * @return
     */
    List<MessageExtend> getAllMessage(@Param("userId")Long userId,@Param("incId")Long incID);
}
