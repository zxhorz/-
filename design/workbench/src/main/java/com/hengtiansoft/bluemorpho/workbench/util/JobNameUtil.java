package com.hengtiansoft.bluemorpho.workbench.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hengtiansoft.bluemorpho.workbench.quartz.job.datastructure.JobInfo;
import com.hengtiansoft.bluemorpho.workbench.repository.AnalysisTypeRepository;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 6, 2018 11:09:20 AM
 */
@Component
public class JobNameUtil {

//	@Autowired
//	private UserUtil userUtil;
	@Autowired
	private AnalysisTypeRepository analysisTypeRepository;
	
	// job name约定为projectId_codeVersion_userId_analysisTypeId_requestReceivedTime
	public String generatedJobName(String projectId, String codeVersion, String userId, String analysisId, String requestReceivedTime) {
//		String userId = userUtil.getCurrentUserId();
		return projectId + "_" + codeVersion + "_" + userId + "_" + analysisId + "_" + requestReceivedTime;
	}
	
	// job group name约定为projectId_codeVersion_userId_requestReceivedTime
	public String generatedJobGroup(String projectId, String codeVersion, String userId, String requestReceivedTime) {
//		String userId = userUtil.getCurrentUserId();
		return projectId + "_" + codeVersion + "_" + userId + "_" + requestReceivedTime;
	}
	
	public JobInfo parseJobInfoFromJobName(String jobName) {
		String[] split = jobName.split("_");
		String projectId = split[0];
		String codeVersion = split[1];
		String userId = split[2];
		String analysisTypeId = split[3];
		String time = split[4];
		String jobGroup = projectId + "_" + codeVersion + "_" + userId + "_" + time;
		
		JobInfo jobInfo = new JobInfo();
		jobInfo.setProjectId(projectId);
		jobInfo.setCodeVersion(codeVersion);
		jobInfo.setAnalysisTypeId(analysisTypeId);
		jobInfo.setJobGroup(jobGroup);
		jobInfo.setUserId(userId);
		jobInfo.setJobName(jobName);
		return jobInfo;
	}
	
}
