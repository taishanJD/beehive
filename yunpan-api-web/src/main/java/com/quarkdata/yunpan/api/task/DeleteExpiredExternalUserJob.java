package com.quarkdata.yunpan.api.task;

import com.quarkdata.yunpan.api.service.ExternalUserService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 删除过期系统账户任务
 */
public class DeleteExpiredExternalUserJob extends QuartzJobBean {

    static Logger logger = LoggerFactory.getLogger(DeleteJob.class);

    private ExternalUserService externalUserService;

    public void setExternalUserService(ExternalUserService externalUserService) {
        this.externalUserService = externalUserService;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("######### 删除过期系统账号任务启动 #########");
        Long count = externalUserService.timingDelete();
        logger.info("######### 删除过期系统账号任务结束, 共删除账号: {}个 #########", count);
    }
}
