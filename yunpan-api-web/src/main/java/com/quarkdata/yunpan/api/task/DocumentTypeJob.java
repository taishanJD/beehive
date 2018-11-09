package com.quarkdata.yunpan.api.task;

import com.quarkdata.yunpan.api.model.common.ResultCode;
import com.quarkdata.yunpan.api.service.ArchivalFileService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Created by liuda
 * Date : 2018/3/2
 */
public class DocumentTypeJob extends QuartzJobBean {
    /**
     * 查询文件类型，每天凌晨4点
     */

    static Logger logger = LoggerFactory.getLogger(QuartzJobBean.class);

    @Autowired
    private ArchivalFileService archivalFileService;
    @Autowired
    private  DocumentTypeList documentTypeList;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        logger.info("**********************查询文件类型*****开启****************************");
        archivalFileService = (ArchivalFileService)context.getJobDetail().getJobDataMap().get("archivalFileService");
        ResultCode resultCode = archivalFileService.selectAllDocumentType();
        documentTypeList = (DocumentTypeList)context.getJobDetail().getJobDataMap().get("documentTypeList");
        documentTypeList.setResultCode(resultCode);
        logger.info("**********************查询文件类型*****结束****************************");

    }
}
