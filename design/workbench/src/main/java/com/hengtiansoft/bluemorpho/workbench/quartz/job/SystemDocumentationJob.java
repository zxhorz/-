package com.hengtiansoft.bluemorpho.workbench.quartz.job;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.hengtiansoft.bluemorpho.workbench.services.SummaryService;
import com.hengtiansoft.bluemorpho.workbench.websocket.ProgressBarWebSocket;

public class SystemDocumentationJob implements Job{
    @Autowired
    private SummaryService summaryService;
	private static final Logger LOGGER = Logger.getLogger(SystemDocumentationJob.class);

    public static ProgressBarWebSocket webSocket = null;
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		JobDetail jobDetail = context.getJobDetail();
		String jobName = jobDetail.getKey().getName();
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
        String projectId = jobDataMap.getString("projectId");
        summaryService.generateSystemDocumentationDownloadHtml(projectId,jobName);
        String code = "0";
        jobDataMap.put("code", code);
		LOGGER.info("system Documentation job (" + jobName + ") execute code : " + code);
	}

}
