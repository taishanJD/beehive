package com.quarkdata.yunpan.api.service;

import javax.servlet.http.HttpServletRequest;
import java.net.SocketException;
import java.util.Date;

/**
 * Created by yanyq1129@thundersoft.com on 2018/4/18.
 */
public interface BaseLogService {
    /**
     * 添加文档相关操作日志
     * @param req
     * @param documentId
     * @param actionType
     * @param detail
     * @param createTime
     */
    void addDocumentLog(HttpServletRequest req, String documentId, Integer actionType, String detail, Date createTime) throws SocketException;

    /**
     * 添加其他类型操作日志
     * @param req
     * @param actionType
     * @param detail
     * @param createTime
     */
    void addOtherLog(HttpServletRequest req, Integer actionType, String detail, Date createTime);
}
