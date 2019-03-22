package com.hengtiansoft.bluemorpho.workbench.quartz.job;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 7, 2018 4:57:33 PM
 */
public class DataDependencyJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		System.out.println("DataDependency(9) ing");
	}

}
