package com.hengtiansoft.bluemorpho.workbench.quartz.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;
import com.hengtiansoft.bluemorpho.workbench.util.ProcessBuilderUtil;
import com.hengtiansoft.bluemorpho.workbench.util.TemplateUtil;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 4, 2018 6:50:36 PM
 */
public class DeadCodeJob implements Job {
	
	private static final Logger LOGGER = Logger.getLogger(DeadCodeJob.class);
	private static final String DEAD_CODE_CONFIG = "dead_code_arg.ftl";
	private static final String DEAD_CODE_GENERATOR = "/OntologyDeadCode-0.0.1-SNAPSHOT.jar";
	private static final String DEAD_CODE_ARGUMENT = "/arguments.properties";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDetail jobDetail = context.getJobDetail();
		String jobName = jobDetail.getKey().getName();
		
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		String projectPath = jobDataMap.getString("projectPath");
		String codeVersion = jobDataMap.getString("codeVersion");
		Map<String, Object> pathConfig = FilePathUtil
				.getDefaultDeadCodePathConfig(projectPath, codeVersion);
		String deadCodeConfig = FilePathUtil.getDeadCodeConfig(projectPath);
		TemplateUtil.generateFile(DEAD_CODE_CONFIG, deadCodeConfig
				+ DEAD_CODE_ARGUMENT, pathConfig);
		String toolPath = FilePathUtil.getToolPath();
		List<String> cmd = builderDeadCodeCommand(deadCodeConfig
				+ DEAD_CODE_ARGUMENT, toolPath);
		int code = ProcessBuilderUtil.processBuilder(cmd, toolPath,
				FilePathUtil.getDeadCodeLogPath(projectPath, codeVersion), true);
		LOGGER.info("deadcode job (" + jobName + ") execute code : " + code);
		jobDataMap.put("code", String.valueOf(code));
	}
	
	private List<String> builderDeadCodeCommand(String deadCodeConfig,
			String toolPath) {
		List<String> cmd = new ArrayList<>();
		cmd.add("java");
		cmd.add("-jar");
		cmd.add(toolPath + DEAD_CODE_GENERATOR);
		cmd.add(deadCodeConfig);
		cmd.add("n");
		return cmd;
	}

}
