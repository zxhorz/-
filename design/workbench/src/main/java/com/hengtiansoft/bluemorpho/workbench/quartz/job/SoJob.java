package com.hengtiansoft.bluemorpho.workbench.quartz.job;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.hengtiansoft.bluemorpho.workbench.dto.AutoTagFeedback;
import com.hengtiansoft.bluemorpho.workbench.dto.AutoTagResult;
import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;
import com.hengtiansoft.bluemorpho.workbench.util.FileStatusUtil;
import com.hengtiansoft.bluemorpho.workbench.util.ProcessBuilderUtil;
import com.hengtiansoft.bluemorpho.workbench.util.TemplateUtil;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 4, 2018 6:42:49 PM
 */
public class SoJob implements Job {
	
	private static final Logger LOGGER = Logger.getLogger(SoJob.class);
	private static final String SO_CONFIG = "so_arg.ftl";
	private static final String SO_INCRE_CONFIG = "so_incremental_arg.ftl";
	private static final String SO_GENERATOR = "/OntologyExportor-0.0.1-SNAPSHOT.jar";
	private static final String SO_INCRE_GENERATOR = "/OntologyIncremental-0.0.1-SNAPSHOT.jar";
	private static final String SO_ARGUMENT = "/arguments.properties";
	private static final String SO_INCRE_ARGUMENT = "/incrementalArguments.properties";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDetail jobDetail = context.getJobDetail();
		String jobName = jobDetail.getKey().getName();
		
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		String projectId = jobDataMap.getString("projectId");
		String projectPath = jobDataMap.getString("projectPath");
		String codeVersion = jobDataMap.getString("codeVersion");
		String isIncremental = jobDataMap.getString("isIncremental");
		String incrementBaseVersion = jobDataMap.getString("incrementBaseVersion");
		String neo4jUri = jobDataMap.getString("neo4jUri");
		
		String neo4jConfig = FilePathUtil.getSoConfig(projectPath);
		File file = new File(neo4jConfig);
		if(!file.exists()){
			file.mkdirs();
		}
		
		String toolPath = FilePathUtil.getToolPath();
		List<String> cmd = null;
		if ("y".equals(isIncremental)) {
			Map<String,Object> pathConfig = FilePathUtil.getDefaultSoIncrementalPathConfig(projectPath, incrementBaseVersion, codeVersion, neo4jUri);
			TemplateUtil.generateFile(SO_INCRE_CONFIG, neo4jConfig + SO_INCRE_ARGUMENT, pathConfig);
			cmd = builderSoIncrementalCommand(neo4jConfig + SO_INCRE_ARGUMENT, toolPath);
		} else {
			Map<String,Object> pathConfig = FilePathUtil.getDefaultSoPathConfig(projectPath, codeVersion);
			TemplateUtil.generateFile(SO_CONFIG, neo4jConfig + SO_ARGUMENT, pathConfig);
			cmd = builderSoCommand(neo4jConfig + SO_ARGUMENT, toolPath);
			// SO 执行完后，将Neo4j插件jar包放至neo4j的plugin目录下
			FilePathUtil.placeNeo4jPlugin(projectPath, codeVersion);
		}
		int code = ProcessBuilderUtil.processBuilder(cmd, toolPath,
				FilePathUtil.getSoLogPath(projectPath, FileStatusUtil.checkCode(projectPath)), true, jobName, projectId);
//		// 连接neo4j
//		Neo4jServerPool pool = new Neo4jServerPool();
//		pool.getAndCreateDB(projectPath);
		LOGGER.info("so job (" + jobName + ") execute code : " + code);
		jobDataMap.put("code", String.valueOf(code));
		diff(projectId,projectPath,codeVersion);
	}

	private void diff(String projectId, String projectPath,String codeVersion) {
		// 首先判断last_filestatus.txt，不存在，则复制filestatus；存在，则与当前source diff
		String lastPath = FilePathUtil.getPath(projectPath, "CONFIG")
				+ "/last_filestatus.txt";
		File lastFile = new File(lastPath);
		if (lastFile.exists()) {
			handleDiff(projectId, projectPath, lastPath, codeVersion);
		}
		FileStatusUtil.lastFileStatus(projectPath);
	}

	private void handleDiff( String projectId,String projectPath, String lastPath, String codeVersion) {
		Map<String, List<String>> diffResult = new HashMap<String, List<String>>();
		diffResult = FileStatusUtil.diffFiles(projectPath, lastPath);
		String feedbackPath = FilePathUtil.getAutoFeedbackPath();
		List<AutoTagFeedback> feedbacks = FilePathUtil.readJson(feedbackPath,
				AutoTagFeedback.class);
		List<AutoTagResult> updateList = buildAutoTagResults(diffResult
				.get("UPDATE"));
		List<AutoTagResult> deleteList = buildAutoTagResults(diffResult
				.get("DELETE"));
		AutoTagFeedback existFeedback = checkProject(projectId, feedbacks);
		if (existFeedback == null) {
			// 不存在，则直接增加
			existFeedback = new AutoTagFeedback(projectId, "test", "test",
					"test", projectPath, codeVersion);
			existFeedback.setUpdateFiles(updateList);
			existFeedback.setDeleteFiles(deleteList);
			feedbacks.add(existFeedback);
		} else {
			// 先更新project版本
			existFeedback.setCodeVersion(codeVersion);
			// check delete
			for (AutoTagResult delete : deleteList) {
				// deleteFiles若没有，则增加
				if (checkAutoTagResult(existFeedback.getDeleteFiles(), delete) == null) {
					existFeedback.getDeleteFiles().add(delete);
				}
				// updateFiles中若存在，则移除
				AutoTagResult result = checkAutoTagResult(
						existFeedback.getUpdateFiles(), delete);
				if (result != null) {
					existFeedback.getUpdateFiles().remove(result);
				}
				// updateTags中若存在，则移除
				AutoTagResult updatTagsResult = checkAutoTagResult(
						existFeedback.getUpdateTags(), delete);
				if (updatTagsResult != null) {
					existFeedback.getUpdateTags().remove(result);
				}
			}
			// check update
			for (AutoTagResult update : updateList) {
				// updateFiles若没有，则增加
				if (checkAutoTagResult(existFeedback.getUpdateFiles(), update) == null) {
					existFeedback.getUpdateFiles().add(update);
				}
				// deleteFiles若存在，则移除
				AutoTagResult result = checkAutoTagResult(
						existFeedback.getDeleteFiles(), update);
				if (result != null) {
					existFeedback.getDeleteFiles().remove(result);
				}
			}
		}
		// 将更新后的结果写回至feedback.json中
		FilePathUtil.writeJson(feedbackPath, feedbacks, AutoTagFeedback.class);
	}

	/**
	 * 构建对象
	 *
	 * @param list
	 * @return
	 */
	private List<AutoTagResult> buildAutoTagResults(List<String> list) {
		List<AutoTagResult> results = new ArrayList<AutoTagResult>();
		for (String str : list) {
			str = StringUtils.replace(str, "\\", "/");
			String name = StringUtils.substringAfterLast(str, "/");
			String type = StringUtils.substringAfterLast(
					StringUtils.substringBeforeLast(str, "/"), "/");
			results.add(new AutoTagResult(name, FileStatusUtil
					.autoTagTypeMapping(type)));
		}
		return results;
	}

	private AutoTagFeedback checkProject(String projectId,
			List<AutoTagFeedback> feedbacks) {
		for (AutoTagFeedback autoTagFeedback : feedbacks) {
			if (autoTagFeedback.getProjectId().equals(projectId)) {
				return autoTagFeedback;
			}
		}
		return null;
	}

	private AutoTagResult checkAutoTagResult(List<AutoTagResult> list,
			AutoTagResult result) {
		for (AutoTagResult item : list) {
			if (item.getName().equalsIgnoreCase(result.getName())
					&& item.getType().equalsIgnoreCase(result.getType())) {
				return item;
			}
		}
		return null;
	}
	
	private List<String> builderSoIncrementalCommand(String neo4jConfig, String toolPath) {
		List<String> cmd = new ArrayList<>();
		cmd.add("java");
		cmd.add("-jar");
		cmd.add(toolPath + SO_INCRE_GENERATOR);
		cmd.add(neo4jConfig);
		return cmd;
	}

	private List<String> builderSoCommand(String neo4jConfig, String toolPath) {
		List<String> cmd = new ArrayList<>();
		cmd.add("java");
		cmd.add("-jar");
		cmd.add(toolPath + SO_GENERATOR);
		cmd.add(neo4jConfig);
		return cmd;
	}
	
}
