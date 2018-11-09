package com.quarkdata.yunpan.api.model.request;

import java.util.Date;
import java.util.List;

public class AddArchivalFileVo {
    private String archiveType;
    private String docName;
    private String isKeepSource;
    private String lastUpdateTime;
    private List<String> documentType;
    private List<Long> tagIdList;
    private List<Long> docIdList;
    private List<Receiver> receiverList;
    private String docDes;

    public String getDocDes() {
        return docDes;
    }

    public void setDocDes(String docDes) {
        this.docDes = docDes;
    }

    public List<Receiver> getReceiverList() {
        return receiverList;
    }

    public void setReceiverList(List<Receiver> receiverList) {
        this.receiverList = receiverList;
    }

    public List<Long> getDocIdList() {
        return docIdList;
    }

    public void setDocIdList(List<Long> docIdList) {
        this.docIdList = docIdList;
    }

    public String getArchiveType() {
        return archiveType;
    }

    public void setArchiveType(String archiveType) {
        this.archiveType = archiveType;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getIsKeepSource() {
        return isKeepSource;
    }

    public void setIsKeepSource(String isKeepSource) {
        this.isKeepSource = isKeepSource;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public List<String> getDocumentType() {
        return documentType;
    }

    public void setDocumentType(List<String> documentType) {
        this.documentType = documentType;
    }

    public List<Long> getTagIdList() {
        return tagIdList;
    }

    public void setTagIdList(List<Long> tagIdList) {
        this.tagIdList = tagIdList;
    }

    @Override
    public String toString() {
        return "AddArchivalFileVo{" +
                "archiveType='" + archiveType + '\'' +
                ", docName='" + docName + '\'' +
                ", isKeepSource='" + isKeepSource + '\'' +
                ", lastUpdateTime='" + lastUpdateTime + '\'' +
                ", documentType=" + documentType +
                ", tagIdList=" + tagIdList +
                ", docIdList=" + docIdList +
                ", receiverList=" + receiverList +
                ", docDes='" + docDes + '\'' +
                '}';
    }
}
