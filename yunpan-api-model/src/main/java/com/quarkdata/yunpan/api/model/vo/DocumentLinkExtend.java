package com.quarkdata.yunpan.api.model.vo;

import com.quarkdata.yunpan.api.model.dataobj.Document;
import com.quarkdata.yunpan.api.model.dataobj.Tag;

import java.util.Date;
import java.util.List;

/**
 * @author maorl
 * @date 12/16/17.
 */
public class DocumentLinkExtend extends Document{
    private Long id;
    private Integer viewCount;
    private Integer downloadCount;
    private Date createTime;
    private Date externalLinkExpireTime;
    private String displayTime;
//    private List<Tag> tags;
    private String externalLinkCode;

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    private Long documentId;
    public String getexternalLinkCode() {
        return externalLinkCode;
    }

    public void setexternalLinkCode(String externalLinkCode) {
        this.externalLinkCode = externalLinkCode;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
//    public List<Tag> getTags() {
//        return tags;
//    }

//    public void setTags(List<Tag> tags) {
//        this.tags = tags;
//    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getExternalLinkExpireTime() {
        return externalLinkExpireTime;
    }

    public void setExternalLinkExpireTime(Date externalLinkExpireTime) {
        this.externalLinkExpireTime = externalLinkExpireTime;
    }

    public String getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(String displayTime) {
        this.displayTime = displayTime;
    }
}
