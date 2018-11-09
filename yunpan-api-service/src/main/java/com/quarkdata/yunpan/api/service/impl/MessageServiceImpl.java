package com.quarkdata.yunpan.api.service.impl;

import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.yunpan.api.dal.dao.*;
import com.quarkdata.yunpan.api.model.dataobj.*;
import com.quarkdata.yunpan.api.model.vo.MessageExtend;
import com.quarkdata.yunpan.api.service.MessageService;
import com.quarkdata.yunpan.api.util.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * @Author liuda1211
 * @Description 消息通知
 */
@Transactional(readOnly =false, rollbackFor = Exception.class)
@Service
public class MessageServiceImpl implements MessageService {
    /**
     * 日志记录
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MessageUserDocumentRelMapper messageUserDocumentRelMapper;

    @Autowired
    private MessageMapper2 messageMapper2;

    @Autowired
    private IncUserConfigMapper incUserConfigMapper;

    @Autowired
    private IncUserConfigMapper2 incUserConfigMapper2;

    @Autowired
    private MessageUserDocumentRelMapper2 messageUserDocumentRelMapper2;

    @Transactional(readOnly = false)
    @Override
    public void createMessage(Message message) {
        messageMapper.insertSelective(message);
    }

    @Transactional(readOnly = false)
    @Override
    public void createMessageUserDocument(Message message,MessageUserDocumentRel messageUserDocumentRel) {
        try {
            messageUserDocumentRel.setMessageId(message.getId());
            messageUserDocumentRelMapper.insertSelective(messageUserDocumentRel);
        }catch (Exception e){
            // 记录本地异常日志
            LOGGER.error("通知消息插入异常");
            LOGGER.error("异常信息:" + e.getMessage());
        }

    }

    /**
     * 查询未读消息列表
     * @param userId
     * @param incId
     * @return
     */
    @Override
    public List<MessageExtend> getMessageList(Long userId, Long incId) {

        try {
            List<MessageExtend> messageList = messageMapper2.getMessage(userId, incId);
            return messageList;
        }catch (Exception e){
            LOGGER.info("查询未读消息异常");
            e.printStackTrace();

        }
        return null;
    }
    
    /**
     * 查询用户所有消息（未读+已读）
     * @param userId
     * @param incId
     * @return
     */
    @Override
    public List<MessageExtend> getAllMessageList(Long userId, Long incId) {

        try {
            List<MessageExtend> messageList = messageMapper2.getAllMessage(userId, incId);
            return messageList;
        }catch (Exception e){
            LOGGER.info("查询未读消息异常");
            e.printStackTrace();

        }
        return null;
    }

    /**
     * 查询用户是否接收通知状态
     * @param userId
     * @param incId
     * @return
     */
    @Override
    public IncUserConfig getUserType(Long userId, Long incId) {
        IncUserConfig incUserConfig = incUserConfigMapper2.selectByUserIdAndIncId(userId, incId);
        return incUserConfig;
    }

    /**
     * 添加用户消息通知设置
     * @param incUserConfig
     */
    @Override
    public void toIncUserConfig(IncUserConfig incUserConfig) {
        incUserConfigMapper.insert(incUserConfig);
    }

    /**
     * 修改用户接受通知设置
     */
    @Override
    public void updateUserType(IncUserConfig incUserConfig) {
        incUserConfigMapper.updateByPrimaryKeySelective(incUserConfig);
    }

    /**
     * 将未读消息设为已读
     */
    @Override
    public void updateIsRead(Long incId, Long userId, String msgIds) {
        for(String msgId: msgIds.split(",")) {
            messageUserDocumentRelMapper2.updateIsRead(incId,userId,Long.parseLong(msgId));
        }
    }

    /**
     * 批量删除消息
     * @param incId
     * @param user
     * @param msgIds
     */
    @Override
    public void delMsgBatch(Integer incId, Users user, String msgIds) {
        String[] msgIdArr = msgIds.split(",");
        for(String msgId: msgIdArr) {
            if(StringUtils.isNotBlank(msgId)) {
                MessageUserDocumentRel record = new MessageUserDocumentRel();
                record.setIsDelete("1");
                MessageUserDocumentRelExample example = new MessageUserDocumentRelExample();
                example.createCriteria().andIncIdEqualTo(Long.parseLong(incId.toString()))
                        .andUserIdEqualTo(Long.parseLong(user.getUserId().toString()))
                        .andMessageIdEqualTo(Long.parseLong(msgId));
                this.messageUserDocumentRelMapper.updateByExampleSelective(record, example);
            }
        }
    }

    @Override
    public Integer getNotReadList(Integer incId, Users user) {
        MessageUserDocumentRelExample example = new MessageUserDocumentRelExample();
        MessageUserDocumentRelExample.Criteria criteria = example.createCriteria();
        criteria.andIncIdEqualTo(incId.longValue())
                .andUserIdEqualTo(user.getUserId().longValue())
                .andIsDeleteEqualTo("0")
                .andIsReadEqualTo("0");
        List<MessageUserDocumentRel> messageUserDocumentRels = messageUserDocumentRelMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(messageUserDocumentRels)){
            return messageUserDocumentRels.size();
        }
        return 0;
    }
}
