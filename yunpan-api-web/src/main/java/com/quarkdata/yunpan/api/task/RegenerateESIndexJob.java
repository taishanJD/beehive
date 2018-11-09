package com.quarkdata.yunpan.api.task;

import com.quarkdata.yunpan.api.model.dataobj.ExternalUser;
import com.quarkdata.yunpan.api.service.DeleteService;
import com.quarkdata.yunpan.api.service.FullTextService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class RegenerateESIndexJob extends QuartzJobBean {

    static Logger logger = LoggerFactory.getLogger(DeleteJob.class);

    @Autowired
    private FullTextService fullTextService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        this.logger.info("<<<<<< delete external_user job start<<<<<<");

        fullTextService = (FullTextService) jobExecutionContext.getJobDetail().getJobDataMap().get("fullTextService");
        fullTextService.generateIndex(1);
        this.logger.info(">>>>>> delete external_user job end >>>>>>");
    }
}
