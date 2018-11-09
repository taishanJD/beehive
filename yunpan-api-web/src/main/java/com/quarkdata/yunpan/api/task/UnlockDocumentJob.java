package com.quarkdata.yunpan.api.task;

import com.quarkdata.yunpan.api.service.DocumentService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 文档到期自动解锁任务
 */
public class UnlockDocumentJob extends QuartzJobBean {

    private Logger logger = LoggerFactory.getLogger(UnlockDocumentJob.class);

    private Integer unlockDay;

    public void setUnlockDay(Integer unlockDay) {
        this.unlockDay = unlockDay;
    }

    private DocumentService documentService;

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        logger.info("############# 文档自动解锁任务启动 #############");
        Long count = this.documentService.timingUnlock(unlockDay);
        logger.info("############# 文档自动解锁任务结束, 共解锁文档: {}个 #############", count);
    }
}
