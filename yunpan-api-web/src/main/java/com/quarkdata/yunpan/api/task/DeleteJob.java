package com.quarkdata.yunpan.api.task;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.quarkdata.yunpan.api.service.DeleteService;

/**
 * ceph底层删除任务，每天凌晨2-3点 每隔5分钟执行一次，每次获取10条数据到ceph集群删除
 */
public class DeleteJob extends QuartzJobBean {

	static Logger logger = LoggerFactory.getLogger(DeleteJob.class);

	@Autowired
	DeleteService deleteService;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		logger.info("**********************ceph底层删除任务*****开启****************************");
		deleteService = (DeleteService) context.getJobDetail().getJobDataMap().get("deleteService");
		doDeleteJob();
		logger.info("**********************ceph底层删除任务*****结束****************************");
	}

	public void doDeleteJob() {
		try {
			int count = 10;
			deleteService.getDeletingDocVersion(count);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
