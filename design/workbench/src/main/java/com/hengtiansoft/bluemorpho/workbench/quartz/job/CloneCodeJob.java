package com.hengtiansoft.bluemorpho.workbench.quartz.job;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hengtiansoft.bluemorpho.model.ParagraphClone;
import com.hengtiansoft.bluemorpho.model.ProgramClone;
import com.hengtiansoft.bluemorpho.workbench.dto.ClonePercentage;
import com.hengtiansoft.bluemorpho.workbench.enums.CypherMethod;
import com.hengtiansoft.bluemorpho.workbench.neo4j.dao.Neo4jDao;
import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;
import com.hengtiansoft.bluemorpho.workbench.util.ProcessBuilderUtil;
import com.hengtiansoft.bluemorpho.workbench.util.TemplateUtil;
import com.hengtiansoft.bluemorpho.workbench.websocket.ProgressBarWebSocket;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 4, 2018 6:48:19 PM
 */
public class CloneCodeJob implements Job {

	private static final Logger LOGGER = Logger.getLogger(CloneCodeJob.class);
	public static ProgressBarWebSocket webSocket = null;
	private static final String CLONE_CONFIG = "clone_arg.ftl";
	private static final String CLONE_CYPHER_CONFIG = "clone_cypher.ftl";
	private static final String CLONE_ARGUMENT = "/arguments.properties";
	private static final String CLONE_CYPHER_ARGUMENT = "/cypher.xml";

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		JobDetail jobDetail = context.getJobDetail();
		String jobName = jobDetail.getKey().getName();

		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		// 目前不需要选择tier，于是默认为所有tier
		String selectedTiers = "123";

		String projectId = jobDataMap.getString("projectId");
		String projectPath = jobDataMap.getString("projectPath");
		String bortPort = jobDataMap.getString("boltPort");
		String codeVersion = jobDataMap.getString("codeVersion");

		// 开始分析模拟给个20%进度
		// try {
		// webSocket.sendMessageTo(jobName + "/20", projectId);
		// } catch (IOException e) {
		// LOGGER.error("webSocket error in clone job.", e);
		// e.printStackTrace();
		// }

		Map<String, Object> pathConfig = FilePathUtil
				.getDefaultClonePathConfig(projectPath, bortPort, codeVersion);
		String cloneConfig = FilePathUtil.getCloneConfig(projectPath);
		File file = new File(cloneConfig);
		if (!file.exists()) {
			file.mkdirs();
		}
		TemplateUtil.generateFile(CLONE_CONFIG, cloneConfig + CLONE_ARGUMENT,
				pathConfig);
		TemplateUtil.generateFile(CLONE_CYPHER_CONFIG, cloneConfig
				+ CLONE_CYPHER_ARGUMENT, new HashMap<String, Object>());
		String toolPath = FilePathUtil.getToolPath();
		List<String> cmd = buildCloneCommandForAnalysis(projectPath, toolPath
				+ "/clone/script/main.py", cloneConfig + CLONE_ARGUMENT,
				toolPath + "/clone/script", selectedTiers, projectPath);

		int code = ProcessBuilderUtil.processBuilder(cmd, toolPath,
				FilePathUtil.getCloneLogPath(projectPath, codeVersion), false,
				jobName, projectId);
		if (code == 0) {
			handleClonePercentage(projectPath, codeVersion, bortPort);
		}
		LOGGER.info("clonecode job (" + jobName + ") execute code : " + code);
		jobDataMap.put("code", String.valueOf(code));
	}

	private List<String> buildCloneCommandForAnalysis(String projectPath,
			String scriptFile, String cloneConfig, String scriptPath,
			String selectedTiers, String path) {
		List<String> cmd = new ArrayList<>();
		cmd.add("python");
		cmd.add(scriptFile);
		cmd.add(cloneConfig);
		cmd.add(scriptPath);
		cmd.add(selectedTiers);
		cmd.add(path);
		return cmd;
	}

	/**
	 * 计算paragraph，program的相似度
	 *
	 * @param projectPath
	 * @param codeVersion
	 * @param boltPort
	 */
	private void handleClonePercentage(String projectPath, String codeVersion,
			String boltPort) {
		// 读取paragraph clone percentage结果文件
		File file = new File(FilePathUtil.getClonePercentagePath(projectPath,
				codeVersion));
		if (!file.exists()) {
			return;
		} else {
			String jsonStr = null;
			try {
				jsonStr = FileUtils.readFileToString(file);
			} catch (IOException e) {
				LOGGER.error(e);
				return;
			}
			List<ParagraphClone> paragraphClones = getClonePara(jsonStr);
			JSONArray batchObject = JSONArray.parseArray(JSON
					.toJSONString(paragraphClones));
			// 批量更改neo4j, set paragraph cloneLines, clonePercentage
			String uri = "bolt://localhost:" + boltPort;
			Neo4jDao neo4jDao = new Neo4jDao(uri);
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("batch", batchObject);
			StatementResult sr = neo4jDao.executeReadCypher(
					CypherMethod.SET_PARA_CLONE.toString(), properties);
			int count = sr.list().size();
			// handle program clone
			List<ClonePercentage> clonePercentages = JSONArray.parseArray(
					jsonStr, ClonePercentage.class);
			handleProgramClone(clonePercentages,
					FilePathUtil.getClonePercentagePath(projectPath,
							codeVersion), neo4jDao);
			neo4jDao.close();

		}
	}

	private List<ParagraphClone> getClonePara(String jsonStr) {
		List<ClonePercentage> list = JSONArray.parseArray(jsonStr,
				ClonePercentage.class);
		List<ParagraphClone> cloneList = new ArrayList<ParagraphClone>();
		for (ClonePercentage clonePercentage : list) {
			ParagraphClone maxMember = getMaxCloneLines(clonePercentage
					.getCloneMembers());
			if (maxMember != null) {
				cloneList.add(maxMember);
			}
		}
		return cloneList;
	}

	private ParagraphClone getMaxCloneLines(List<ParagraphClone> cloneMembers) {
		// TODO：是查找最大的cloneLines还是percentage??
		// 目前以查找最大的percentage
		if (cloneMembers.size() == 0) {
			return null;
		}
		ParagraphClone max = cloneMembers.get(0);
		for (ParagraphClone member : cloneMembers) {
			Double left = Double.valueOf(member.getClonePercentage());
			Double right = Double.valueOf(max.getClonePercentage());
			if (left.compareTo(right) > 0) {
				max = member;
			}
		}
		return max;
	}

	/**
	 * 根据两两program的相似度，针对某个program，找出该program和其他Program的最大相似度，
	 * 该最大相似度作为该Program的相似度
	 *
	 * @param clonePercentages
	 * @param filePath
	 * @param neo4jDao
	 * @return
	 */
	private void handleProgramClone(List<ClonePercentage> clonePercentages,
			String filePath, Neo4jDao neo4jDao) {
		List<ProgramClone> result = new ArrayList<ProgramClone>();
		// key为programNodeId
		Map<String, ProgramClone> programCloneMap = new HashMap<String, ProgramClone>();
		// 得到两两program的相似度
		List<ProgramClone> programClones = getProgramClonePairs(
				clonePercentages, filePath, neo4jDao);
		for (ProgramClone pair : programClones) {
			if (programCloneMap.get(pair.getNodeId()) != null) {
				// 得到两两program中最大相似度
				if (Double.valueOf(pair.getPercentage()).compareTo(
						Double.valueOf(programCloneMap.get(pair.getNodeId())
								.getPercentage())) > 0) {
					programCloneMap.put(pair.getNodeId(), pair);
				}
			} else {
				programCloneMap.put(pair.getNodeId(), pair);
			}
		}
		result.addAll(programCloneMap.values());
		Map<String, Object> properties = new HashMap<String, Object>();
		JSONArray batchObject = JSONArray.parseArray(JSON.toJSONString(result));
		properties.put("batch", batchObject);
		StatementResult sr = neo4jDao.executeReadCypher(
				CypherMethod.SET_PROGRAM_CLONE.toString(), properties);
		int count = sr.list().size();
	}

	/**
	 * 得到两两program比较的相似度
	 *
	 * @param clonePercentages
	 * @param filePath
	 * @param neo4jDao
	 * @return
	 */
	private List<ProgramClone> getProgramClonePairs(
			List<ClonePercentage> clonePercentages, String filePath,
			Neo4jDao neo4jDao) {
		List<ProgramClone> programClones = new ArrayList<ProgramClone>();
		// 先按照program 得到programNodeId, programName, 以及paragraphName组合
		StatementResult sr = neo4jDao
				.executeReadCypher(CypherMethod.PROGRAM_WITH_ALL_PARAGRAPH
						.toString());
		List<Record> records = sr.list();
		for (Record left : records) {
			String leftId = left.get("nodeId").asString();
			String leftName = left.get("name").asString();
			int leftParaLine = left.get("totalParaLine").asInt();
			List<Object> leftParas = left.get("paraNames").asList();
			for (Record right : records) {
				String rightId = right.get("nodeId").asString();
				String rightName = right.get("name").asString();
				int rightParaLine = left.get("totalParaLine").asInt();
				List<Object> rightParas = right.get("paraNames").asList();
				// 程序两两比较
				if (!leftId.equals(rightId)) {
					ProgramClone programClone = new ProgramClone(leftName,
							leftId, rightName, rightId);
					List<ParagraphClone> paragraphPairs = new ArrayList<ParagraphClone>();
					int totalCloneLine = 0;
					for (Object leftPara : leftParas) {
						for (Object rightPara : rightParas) {
							// 段落两两比较，记录相似段
							ParagraphClone paragraphClone = findParagraphClone(
									clonePercentages, leftPara.toString(),
									rightPara.toString());
							if (paragraphClone != null) {
								paragraphPairs.add(new ParagraphClone(leftPara
										.toString(), rightPara.toString(),
										paragraphClone.getCloneLines(),
										paragraphClone.getClonePercentage()));
								totalCloneLine += Integer
										.valueOf(paragraphClone.getCloneLines());
								break;
							}
						}
					}
					// 计算每组program的相似度
					programClone.setParagraphPairs(paragraphPairs);
					programClone.setClone(String.valueOf(totalCloneLine));
					// program相似度的计算方法
					programClone
							.setPercentage(String
									.valueOf(((double) (totalCloneLine * 2) / (leftParaLine + rightParaLine))));
					programClones.add(programClone);
				}
			}
		}
		// 将该结果写入clone output，用于clone结果展现
		FilePathUtil.writeFile(filePath, JSON.toJSONString(programClones), false);
		return programClones;
	}

	/**
	 * 从paragraph clone 结果集中根据paragraph name 查找相似段组
	 *
	 * @param clonePercentages
	 * @param leftName
	 * @param rightName
	 * @return
	 */
	private ParagraphClone findParagraphClone(
			List<ClonePercentage> clonePercentages, String leftName,
			String rightName) {
		for (ClonePercentage clonePercentage : clonePercentages) {
			if (leftName.equals(clonePercentage.getName())) {
				for (ParagraphClone paragraphClone : clonePercentage
						.getCloneMembers()) {
					if (rightName.equals(paragraphClone.getName())) {
						return paragraphClone;
					}
				}
			}
		}
		return null;
	}
}
