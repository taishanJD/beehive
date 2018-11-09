package com.quarkdata.yunpan.api.model.vo;

import java.util.Date;

/**
 * Created by yanyq1129@thundersoft.com on 2018/6/21.
 */
public class LogAddVO {
    private Long documentId;
    private String actionType;
    private Date actionTime;

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public Date getActionTime() {
        return actionTime;
    }

    public void setActionTime(Date actionTime) {
        this.actionTime = actionTime;
    }

}
