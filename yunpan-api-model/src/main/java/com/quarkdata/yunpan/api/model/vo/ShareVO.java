package com.quarkdata.yunpan.api.model.vo;

import com.quarkdata.yunpan.api.model.dataobj.Tag;

import java.util.Date;
import java.util.List;

/**
 * @author maorl
 * @date 12/11/17.
 */
public class ShareVO {
    Long id;
    String sharer;
    String permission;
    String documentName;
    String documentType;
    Date updateTime;
    List<Tag> tagList;
    String isCollect;
    Long size;

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSharer() {
        return sharer;
    }

    public void setSharer(String sharer) {
        this.sharer = sharer;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
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


    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }

    public String getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(String isCollect) {
        this.isCollect = isCollect;
    }
}
