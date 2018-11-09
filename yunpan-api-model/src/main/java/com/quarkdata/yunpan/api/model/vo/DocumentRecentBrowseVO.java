package com.quarkdata.yunpan.api.model.vo;

import com.quarkdata.yunpan.api.model.dataobj.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yanyq1129@thundersoft.com on 2018/4/27.
 */
public class DocumentRecentBrowseVO extends Document implements Serializable{

    private String namePath;

    private String source; //文档来源

    private Date lastAccessTime; // 最后访问时间

    private String permission; // 读写权限

    private Long logId; // 日志记录ID

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getNamePath() {
        return namePath;
    }

    public void setNamePath(String namePath) {
        this.namePath = namePath;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

}
