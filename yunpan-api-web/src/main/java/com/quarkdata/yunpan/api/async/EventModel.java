package com.quarkdata.yunpan.api.async;

import com.quarkdata.quark.share.model.dataobj.Users;
import com.quarkdata.yunpan.api.model.request.Receiver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuda
 * Date : 2018/3/6
 */
public class EventModel {


    private Users user;
    private String notice;
    private String messageType;
    private List<Long> documentIds;
    private List<Receiver> receiverList;

    public EventModel(Users user,String notice,String messageType,List<Long> documentIds,List<Receiver> receiverList){
        this.user=user;
        this.notice=notice;
        this.messageType=messageType;
        this.documentIds=documentIds;
        this.receiverList=receiverList;
    }

    public EventModel(){

    }

    @Override
    public String toString() {
        return "EventModel{" +
                "user=" + user +
                ", notice='" + notice + '\'' +
                ", messageType='" + messageType + '\'' +
                ", documentIds=" + documentIds +
                ", receiverList=" + receiverList +
                '}';
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public List<Long> getDocumentIds() {
        return documentIds;
    }

    public void setDocumentIds(List<Long> documentIds) {
        this.documentIds = documentIds;
    }

    public List<Receiver> getReceiverList() {
        return receiverList;
    }

    public void setReceiverList(List<Receiver> receiverList) {
        this.receiverList = receiverList;
    }
}
