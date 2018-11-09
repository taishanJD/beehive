package com.quarkdata.yunpan.api.task;

import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.quarkdata.yunpan.api.model.dataobj.Recycle;
import com.quarkdata.yunpan.api.service.DeleteService;

public class AddToCephDeleteJob extends QuartzJobBean {

	static Logger logger = LoggerFactory.getLogger(AddToCephDeleteJob.class);

	@Autowired
	DeleteService deleteService;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		logger.info("**********************自动删除任务*****开启****************************");
		deleteService = (DeleteService) context.getJobDetail().getJobDataMap().get("deleteService");
		doAutoDeleteJob();
		logger.info("**********************自动删除任务*****结束****************************");
	}

	// 将在规定时间内没有操作的recycle文档，自动放入document_ceph_delete表中
	void doAutoDeleteJob() {
		try {
			deleteService.autoDelete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
