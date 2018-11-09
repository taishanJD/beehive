package com.quarkdata.yunpan.api.model.vo;

import com.quarkdata.yunpan.api.model.dataobj.Document;

import java.util.Date;

/**
 * @author maorl
 * @date 12/16/17.
 */
public class DocPermissionExtend extends Document{
    private Integer viewCount;
    private Integer downloadCount;
    private Date createTime;
    private Date externalLinkExpireTime;
    private String displayTime;

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
