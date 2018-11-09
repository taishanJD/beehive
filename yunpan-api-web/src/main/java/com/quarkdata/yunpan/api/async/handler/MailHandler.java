package com.quarkdata.yunpan.api.async.handler;

import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.yunpan.api.async.EventModel;
import com.quarkdata.yunpan.api.dal.dao.DocumentMapper;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.DepartmentApi;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.GroupApi;
import com.quarkdata.yunpan.api.dal.rest.quarkshare.UsersApi;
import com.quarkdata.yunpan.api.message.MessageType;
import com.quarkdata.yunpan.api.model.common.PushConstant;
import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.dataobj.IncUserConfig;
import com.quarkdata.yunpan.api.model.dataobj.Message;
import com.quarkdata.yunpan.api.model.dataobj.MessageUserDocumentRel;
import com.quarkdata.yunpan.api.model.request.Receiver;
import com.quarkdata.yunpan.api.service.MessageService;
import com.quarkdata.yunpan.api.service.PushService;
import com.quarkdata.yunpan.api.util.EmailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by liuda
 * Date : 2018/3/6
 */
public class MailHandler {

    private static final Logger logger = LoggerFactory.getLogger(MailHandler.class);

    @Autowired
    private GroupApi groupApi;

    @Autowired
    private DepartmentApi departmentApi;

    @Autowired
    private UsersApi usersApi;

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private MessageService messageService;

    @Autowired
    private PushService pushService;

    /**
     * 日志记录
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MailHandler.class);


    public void doHandler(EventModel eventModel) {
        try {
            if ("0".equals(eventModel.getNotice())){
            String incType = null;
                ArrayList<Long> pushUserIds = new ArrayList<>();
                if (eventModel.getDocumentIds() != null && eventModel.getDocumentIds().size() > 0){
                for (Long i: eventModel.getDocumentIds()) {
                    if (eventModel.getReceiverList() != null && eventModel.getReceiverList().size() > 0){
                        for (Receiver r: eventModel.getReceiverList()) {
                            //接收者类型：0用户、1用户组、2部门
                            if("0".equals(r.getRecType())){
                                incType = "与我";
                                Message message = createMessage(eventModel.getUser(), incType, eventModel.getMessageType(),r);
                                if (addMsg(message,r.getRecId(),i)){
                                    pushUserIds.add(Long.parseLong(r.getRecId()));
                                }
                            }else if ("1".equals(r.getRecType())){
                                incType = "与我所在组";
                                Message message = createMessage(eventModel.getUser(), incType, eventModel.getMessageType(),r);
                                List<Integer> groupAllUserId = groupApi.getGroupAllUserId(eventModel.getUser().getIncid().toString(), r.getRecId());
                                if (groupAllUserId != null&&groupAllUserId.size()>0){
                                    for (Integer uId: groupAllUserId) {
                                        if (addMsg(message,uId.toString(),i)){
                                            pushUserIds.add(uId.longValue());
                                        }
                                    }
                                }
                            }else if("2".equals(r.getRecType())){
                                incType = "与我所在部门";
                                Message message = createMessage(eventModel.getUser(), incType, eventModel.getMessageType(),r);
                                List<Integer> deptAllUserId = departmentApi.getDeptAllUserId(r.getRecId());
                                if (deptAllUserId != null&&deptAllUserId.size()>0){
                                    for (Integer uId: deptAllUserId) {
                                        if (addMsg(message,uId.toString(),i)){
                                            pushUserIds.add(uId.longValue());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //TODO 消息推送，推送服务部署打开注释
                pushService.pushMessage(pushUserIds, PushConstant.BEEHIVE_PUSH_CMD,"beehive","你有一条新消息，请查收",null,null);
        }
        }catch (Exception e){
            // 记录本地异常日志
            LOGGER.error("消息通知后置异常异常");
            LOGGER.error("异常信息:" + e.getMessage());
        }
    }

    public Boolean addMsg(Message message,String receiverId,Long documentId) throws UnknownHostException {
        //消息是否推送
        Boolean isPush = false;
        //创建关联
        MessageUserDocumentRel messageUserDocumentRel = new MessageUserDocumentRel();
        messageUserDocumentRel.setUserId(Long.valueOf(receiverId));
        //messageUserDocumentRel.setMessageId(1L);
        messageUserDocumentRel.setDocumentId(documentId);
        messageUserDocumentRel.setIsDelete("0");
        messageUserDocumentRel.setIsRead("0");
        Users users = usersApi.getUserById(receiverId).getData();
        try{
            messageUserDocumentRel.setIncId(users.getIncid().longValue());
        }catch (Exception e){
            LOGGER.error("用户错误,请管理员检查是否存在该用户");
            LOGGER.error("异常信息:" + e.getMessage());
        }
        IncUserConfig incUserConfig = messageService.getUserType(users.getUserId().longValue(), users.getIncid().longValue());
        if (incUserConfig == null){
            incUserConfig = new IncUserConfig();
            incUserConfig.setUserId(users.getUserId().longValue());
            incUserConfig.setIncId(users.getIncid().longValue());
            incUserConfig.setIsReceiveMessage("1");
            incUserConfig.setIsReceiveEmailMessage("0");
            messageService.toIncUserConfig(incUserConfig);
        }
        //从用户关联表中查询是否设为未读
        if ("1".equals(incUserConfig.getIsReceiveMessage())){
            isPush = true;
        }
        //从用户关联表中查询是否邮箱提醒
        if("1".equals(incUserConfig.getIsReceiveEmailMessage())){
            Document document = this.documentMapper.selectByPrimaryKey(documentId);
            //邮箱通知
            String[] address = {users.getEmail()};
            String title = "云盘消息通知";
            String text = message.getMessage() + ": 《" + document.getDocumentName() + "》" + ",请尽快登录云盘官网查看";
            try {
                EmailUtil.send(address, text, title, null, null);
            }catch (Exception e){
                // 记录本地异常日志
                LOGGER.error("邮件发送异常，请查看邮箱地址是否正确");
                LOGGER.error("异常信息:" + e.getMessage());
            }

        }
        messageService.createMessageUserDocument(message,messageUserDocumentRel);
        return isPush;
    }

    public Message createMessage(Users user,String incType,String messageType,Receiver r){
        //创建消息
        String userName = user.getUserName();
        Message message = new Message();
        message.setIncId(user.getIncid().longValue());
        message.setCreateUsername(userName);
        message.setCreateTime(new Date());
        //添加messageType
        if (MessageType.Collection_success.equals(messageType) || MessageType.Collection_cancel.equals(messageType)){
            message.setMessageType(3);
        }else if (MessageType.Share.equals(messageType) || MessageType.Archival.equals(messageType)
                || MessageType.organized_space.equals(messageType)){
            message.setMessageType(2);
        }else if (MessageType.add_permission.equals(messageType) || MessageType.delete_permission.equals(messageType)){
            message.setMessageType(1);
        }else {
            message.setMessageType(4);
        }
        ///////////////
        if ("0".equals(r.getPermission())){
            String permission = "只读";
            if(messageType.equals(MessageType.Share) || messageType.equals(MessageType.delete_Share)) {
                message.setMessage(userName + incType + messageType);
            } else {
                message.setMessage(userName + incType + messageType + "授予了" + permission + "权限");
            }
        }else if ("1".equals(r.getPermission())){
            String permission = "读写";
            if(messageType.equals(MessageType.Share) || messageType.equals(MessageType.delete_Share)) {
                message.setMessage(userName + incType + messageType);
            } else {
                message.setMessage(userName + incType + messageType + "授予了" + permission + "权限");
            }
        }else if ("2".equals(r.getPermission())){
            String permission = "管理";
            if(messageType.equals(MessageType.Share) || messageType.equals(MessageType.delete_Share)) {
                message.setMessage(userName + incType + messageType);
            } else {
                message.setMessage(userName + incType + messageType + "授予了" + permission + "权限");
            }
        }else if ("3".equals(r.getPermission())){
            message.setMessage(messageType);
        }
        messageService.createMessage(message);
        return message;
    }
}
