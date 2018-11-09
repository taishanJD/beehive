package com.quarkdata.yunpan.api.model.vo;

import com.quarkdata.yunpan.api.model.dataobj.Tag;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by liuda
 * Date : 2018/3/27
 * ES一条记录
 */
public class ResultEsVo {

    /**
     * documentId
     */
    private String id;

    /**
     * documentVersionId
     */
    private String documentVersionId;

    /**
     * 文件名称
     */
    private String documentName;


    /**
     * 文件名称
     */
    private String displayName;

    /**
     * 文件类型
     */
    private String documentType;

    /**
     * 文件内容
     */
    private String content;

    /**
     * 文件类型 个人文件 组织文件  归档文件
     */
    private String type;

    private ArrayList<String> versionIds;

    private String versionStr;

    private String updateUsername;

    private Date updateTime;

    private String idPath;

    private String namePath;

    private Long size;

    private String isShare;

    private String isLock;

    private String lockUsername;

    private Date lockTime;

    private List<Tag> tags;
    private String permission;

    public ResultEsVo() {
    }

    public ResultEsVo(DocumentExtend documentExtend) {
        this.id =documentExtend.getId().toString();
        this.type = documentExtend.getType();
        this.documentType = documentExtend.getDocumentType();
        this.updateTime = documentExtend.getUpdateTime();
        this.updateUsername = documentExtend.getUpdateUsername();
        this.size = documentExtend.getSize();
        this.idPath = documentExtend.getIdPath();
        this.displayName = documentExtend.getDocumentName();
        this.isShare = documentExtend.getIsShare();
        this.isLock = documentExtend.getIsLock();
        this.lockTime = documentExtend.getLockTime();
        this.lockUsername = documentExtend.getLockUsername();
        this.tags = documentExtend.getTags();
        this.permission = documentExtend.getPermission();
    }

    @Override
    public String toString() {
        return "ResultEsVo{" +
                "id='" + getId() + '\'' +
                ", documentName='" + documentName + '\'' +
                ", documentType='" + documentType + '\'' +
                ", content='" + content + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDocumentVersionId() {
        return documentVersionId;
    }

    public void setDocumentVersionId(String documentVersionId) {
        this.documentVersionId = documentVersionId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVersionStr() {
        return versionStr;
    }

    public void setVersionStr(String  versionStr) {
        this.versionStr = versionStr;
    }

    public String getUpdateUsername() {
        return updateUsername;
    }

    public void setUpdateUsername(String  updateUsername) {
        this.updateUsername = updateUsername;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setVersionStr(Date  updateTime) {
        this.updateTime = updateTime;
    }

    public String getIdPath() {
        return idPath;
    }

    public void setIdPath(String  idPath) {
        this.idPath = idPath;
    }

    public String getNamePath() {
        return namePath;
    }

    public void setNamePath(String  namePath) {
        this.namePath = namePath;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long  size) {
        this.size = size;
    }

    public String getIsShare() {
        return isShare;
    }

    public void setIsShare(String  isShare) {
        this.isShare = isShare;
    }

    public String getIsLock() {
        return isLock;
    }

    public void setIsLock(String isLock) {
        this.isLock = isLock;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String  displayName) {
        this.displayName = displayName;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getPermission(){ return permission; }

    public void setPermission(String permission){ this.permission = permission; }

    public String getLockUsername() {
        return lockUsername;
    }

    public void setLockUsername(String lockUsername) {
        this.lockUsername = lockUsername;
    }

    public Date getLockTime() {
        return lockTime;
    }

    public void setLockTime(Date lockTime) {
        this.lockTime = lockTime;
    }
}
