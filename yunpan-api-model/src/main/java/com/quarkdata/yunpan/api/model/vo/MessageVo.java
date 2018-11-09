package com.quarkdata.yunpan.api.model.vo;

import java.util.List;

public class MessageVo {
    //消息列表
    private List<MessageExtend> messageList;

    //消息通知开关
    private boolean msgValue;

    //邮箱通知开关
    private boolean emailValue;

    public List<MessageExtend> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<MessageExtend> messageList) {
        this.messageList = messageList;
    }

    public boolean isMsgValue() {
        return msgValue;
    }

    public void setMsgValue(boolean msgValue) {
        this.msgValue = msgValue;
    }

    public boolean isEmailValue() {
        return emailValue;
    }

    public void setEmailValue(boolean emailValue) {
        this.emailValue = emailValue;
    }

    @Override
    public String toString() {
        return "MessageVo{" +
                "messageList=" + messageList +
                ", msgValue=" + msgValue +
                ", emailValue=" + emailValue +
                '}';
    }
}
