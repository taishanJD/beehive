package com.quarkdata.yunpan.api.model.vo;

import java.util.Date;
import java.util.List;

/**
 * Created by yanyq1129@thundersoft.com on 2018/5/18.
 */
public class ShareAndLinkVO extends DocumentExtend{

    // 分享记录ID(外链)
    private Long shareId;

    // 分享类型: 共享 - 外链
    private String shareType;

    // 浏览次数
    private Integer viewCount;

    // 下载次数
    private Integer downloadCount;

    // 分享时间
    private Date shareTime;

    // 1-直接外链, 0-子文件,判断是否可以取消外链
    private String isLink;

    // 过期时间(外链)
    private Date expirationTime;

    private Date externalLinkExpireTime;

    // 倒计时(外链): 几小时后,几天后...
    private String displayTime;

    // 外链识别码(外链)
    private String externalLinkCode;

    // 共享或外链文件夹添加namePath属性,前端跳转使用
    private String namePath;

    private Boolean allowPreview;

    private Boolean allowDownload;

    private Boolean isSecret;

    private String fetchCode;

    private List<String> emails;

    private List<String> telephones;

    public Boolean getAllowPreview() {
        return allowPreview;
    }

    public void setAllowPreview(Boolean allowPreview) {
        this.allowPreview = allowPreview;
    }

    public Boolean getAllowDownload() {
        return allowDownload;
    }

    public void setAllowDownload(Boolean allowDownload) {
        this.allowDownload = allowDownload;
    }

    public Boolean getIsSecret() {
        return isSecret;
    }

    public void setIsSecret(Boolean secret) {
        isSecret = secret;
    }

    public String getFetchCode() {
        return fetchCode;
    }

    public void setFetchCode(String fetchCode) {
        this.fetchCode = fetchCode;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public List<String> getTelephones() {
        return telephones;
    }

    public void setTelephones(List<String> telephones) {
        this.telephones = telephones;
    }

    public Long getShareId() {
        return shareId;
    }

    public void setShareId(Long shareId) {
        this.shareId = shareId;
    }

    public String getShareType() {
        return shareType;
    }

    public void setShareType(String shareType) {
        this.shareType = shareType;
    }

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

    public Date getShareTime() {
        return shareTime;
    }

    public void setShareTime(Date shareTime) {
        this.shareTime = shareTime;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(String displayTime) {
        this.displayTime = displayTime;
    }

    public String getExternalLinkCode() {
        return externalLinkCode;
    }

    public void setExternalLinkCode(String externalLinkCode) {
        this.externalLinkCode = externalLinkCode;
    }

    public String getNamePath() {
        return namePath;
    }

    public void setNamePath(String namePath) {
        this.namePath = namePath;
    }

    public String getIsLink() {
        return isLink;
    }

    public void setIsLink(String isLink) {
        this.isLink = isLink;
    }

    public Date getExternalLinkExpireTime() {
        return externalLinkExpireTime;
    }

    public void setExternalLinkExpireTime(Date externalLinkExpireTime) {
        this.externalLinkExpireTime = externalLinkExpireTime;
    }
}
