package com.quarkdata.yunpan.api.service;


import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.yunpan.api.model.dataobj.IncUserConfig;
import com.quarkdata.yunpan.api.model.dataobj.Message;
import com.quarkdata.yunpan.api.model.dataobj.MessageUserDocumentRel;
import com.quarkdata.yunpan.api.model.vo.MessageExtend;

import java.util.List;

/**
 * @Author liuda1211
 * @Description 消息通知
 */
public interface MessageService {


    /**
     * 创建消息
     * @param message
     */
    void createMessage(Message message);

    /**
     * 创建消息用户文档关联表
     * @param messageUserDocumentRel
     * @return
     */
    void createMessageUserDocument(Message message,MessageUserDocumentRel messageUserDocumentRel);

    /**
     * 查询未读消息列表
     * @param userId
     * @param incId
     * @return
     */
    List<MessageExtend> getMessageList(Long userId, Long incId);
    
    /**
     * 查询用户所有消息（未读+已读）
     * @param userId
     * @param incId
     * @return
     */
    List<MessageExtend> getAllMessageList(Long userId, Long incId);

    /**
     * 查询用户是否接收通知状态
     * @param userId
     * @param incId
     * @return
     */
    IncUserConfig getUserType(Long userId, Long incId);

    /**
     * 添加用户消息通知设置
     * @param incUserConfig
     */
    void toIncUserConfig(IncUserConfig incUserConfig);

    /**
     * 修改用户接受通知设置
     */
    void updateUserType(IncUserConfig incUserConfig);

    /**
     * 将未读消息设为已读
     */
    void updateIsRead(Long incId, Long userId, String msgIds);

    /**
     * 批量删除消息
     * @param incId
     * @param user
     * @param msgIds
     */
    void delMsgBatch(Integer incId, Users user, String msgIds);

    /**
     * 获取当前用户未读消息
     * @param incId
     * @param user
     */
    Integer getNotReadList(Integer incId, Users user);
}
