package com.hengtiansoft.bluemorpho.workbench.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.hengtiansoft.bluemorpho.model.CloneMember;
import com.hengtiansoft.bluemorpho.model.CloneRelation;
import com.hengtiansoft.bluemorpho.model.CloneResultItem;
import com.hengtiansoft.bluemorpho.workbench.domain.Project;
import com.hengtiansoft.bluemorpho.workbench.dto.CostCloneResult;
import com.hengtiansoft.bluemorpho.workbench.dto.CostEstimationResult;
import com.hengtiansoft.bluemorpho.workbench.dto.CostParagraphResult;
import com.hengtiansoft.bluemorpho.workbench.dto.CostParameters;
import com.hengtiansoft.bluemorpho.workbench.dto.CostProgramResult;
import com.hengtiansoft.bluemorpho.workbench.enums.CypherMethod;
import com.hengtiansoft.bluemorpho.workbench.neo4j.dao.Neo4jDao;
import com.hengtiansoft.bluemorpho.workbench.repository.ProjectRepository;
import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;
import com.hengtiansoft.bluemorpho.workbench.util.FileStatusUtil;
import com.hengtiansoft.bluemorpho.workbench.util.NumberHelper;
import com.hengtiansoft.bluemorpho.workbench.util.PortUtil;

/**
 * @Description: Cost Estimation
 * @author gaochaodeng
 * @date Jun 28, 2018
 */

@Service
public class CostEstimationService {
	@Autowired
	ProjectRepository projectRepository;
	@Autowired
	PortUtil portUtil;
	@Autowired
	CloneService cloneService;

	private static final Logger LOGGER = Logger
			.getLogger(CostEstimationService.class);
	private static final String TYPE_TABLE = "table";
	private static final String TYPE_LOOP = "loop";
	private static final String TYPE_CONDITION = "condition";
	private static final String TYPE_VARIABLE = "variable";
	private static final double RATIO = 0.1;

	/**
	 * 得到CostEstimation页面需要的数据，主要是program,paragraph,clone等
	 *
	 * @param projectId
	 * @param parameters
	 * @return
	 */
	public CostEstimationResult getCostEstimationResult(
			CostParameters parameters) {
		CostEstimationResult cacheResult = getCacheCostEstimationResult(parameters);
		if (cacheResult != null) {
			return cacheResult;
		}
		int projectId = parameters.getProjectId();
		CostEstimationResult result = new CostEstimationResult();
		// cost clone info
		List<CostCloneResult> cloneResults = getCostClone(projectId);
		// cost program info
		List<CostProgramResult> programResults = generateCostProgramResults(
				parameters, cloneResults);
		// 计算cost, man hour
		handleCostClone(cloneResults, parameters.getManHour());
		result.setCloneResults(cloneResults);
		result.setProgramResults(programResults);
		// 一些manHour的统计信息
		double programSubTotal = 0.0;
		for (CostProgramResult program : programResults) {
			programSubTotal += program.getManHour();
		}
		double cloneSubTotal = 0.0;
		for (CostCloneResult clone : cloneResults) {
			cloneSubTotal += clone.getManHour();
		}
		result.setProgramSubTotal(programSubTotal);
		result.setCloneSubTotal(cloneSubTotal);
		result.setGrandTotal(programSubTotal - cloneSubTotal);
		result.setWithBudget(result.getGrandTotal()
				* parameters.getHourlyRate());
		parameters.setResourceNeeds(result.getGrandTotal()
				/ parameters.getAvailableTimeline());
		result.setCostParameters(parameters);
		cacheCostEstimation(projectId, result);
		return result;
	}

	private List<CostProgramResult> generateCostProgramResults(
			CostParameters parameters, List<CostCloneResult> cloneResults) {
		int projectId = parameters.getProjectId();
		List<CostProgramResult> results = new ArrayList<CostProgramResult>();
		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		StatementResult sr = null;
		// program的一些基本信息, loc等
		sr = neo4jDao.executeReadCypher(CypherMethod.COST_PROGRAM_BASIC
				.toString());
		List<Record> programBasic = sr.list();
		Map<String, Object> properties = new HashMap<String, Object>();
		for (Record record : programBasic) {
			String nodeId = record.get("nodeId").asString();
			String name = record.get("name").asString();
			int totalLine = Integer.valueOf(record.get("totalLine").asString());
			double complexity = record.get("complexity").asDouble();
			int paraCount = record.get("count").asInt();
			CostProgramResult result = new CostProgramResult(nodeId, name,
					totalLine, complexity, paraCount);

			properties = new HashMap<String, Object>();
			properties.put("nodeId", nodeId);
			sr = neo4jDao.executeReadCypher(
					CypherMethod.COST_PROGRAM_COUNT.toString(), properties);
			for (Record countRecord : sr.list()) {
				int count = countRecord.get("count").asInt();
				String type = countRecord.get("type").asString();
				if (TYPE_VARIABLE.equalsIgnoreCase(type)) {
					result.setVariables(count);
				} else if (TYPE_TABLE.equalsIgnoreCase(type)) {
					result.setTables(count);
				} else if (TYPE_CONDITION.equalsIgnoreCase(type)) {
					result.setConditionalStatements(count);
				} else if (TYPE_LOOP.equalsIgnoreCase(type)) {
					result.setLoop(count);
				}
			}
			// 计算complexityRatio
			double complexityLoc = ((result.getLoc() - parameters
					.getMedianLoc()) / parameters.getMedianLoc())
					* parameters.getLoc();
			complexityLoc = complexityLoc < 0 ? 0 : complexityLoc;
			double complexityLoop = ((result.getLoop() - parameters
					.getMedianLoops()) / parameters.getMedianLoops())
					* parameters.getLoop();
			complexityLoop = complexityLoop < 0 ? 0 : complexityLoop;
			double complexityCondition = ((result.getConditionalStatements() - parameters
					.getMedianConditions()) / parameters.getMedianConditions())
					* parameters.getConditionalStatements();
			complexityCondition = complexityCondition < 0 ? 0
					: complexityCondition;
			double complexityTable = ((result.getTables() - parameters
					.getMedianTables()) / parameters.getMedianTables())
					* parameters.getTables();
			complexityTable = complexityTable < 0 ? 0 : complexityTable;
			double complexityVariable = ((result.getVariables() - parameters
					.getMedianVariables()) / parameters.getMedianVariables())
					* parameters.getVariables();
			complexityVariable = complexityVariable < 0 ? 0
					: complexityVariable;
			double complexityRatio = complexityLoc + complexityLoop
					+ complexityCondition + complexityTable
					+ complexityVariable;
			// TODO complexityRatio如何与result.getComplexity()再进行计算
			result.setComplexityRatio(complexityRatio);

			// 计算costPoint
			double costPoint = (result.getLoc() * complexityRatio)
					/ parameters.getCostPoint();
			result.setCostPoint(costPoint);

			// 计算manHour
			double manHour = costPoint * parameters.getManHour();
			result.setManHour(manHour);
			// 计算paragraph
			result.setParagraphResults(generateCostParagraphResults(
					result.getNodeId(), parameters, cloneResults, neo4jDao));
			results.add(result);
		}
		neo4jDao.close();

		// 按照programName排序
		Collections.sort(results, new Comparator<CostProgramResult>() {
			@Override
			public int compare(CostProgramResult o1, CostProgramResult o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		return results;
	}

	private List<CostParagraphResult> generateCostParagraphResults(
			String programNodeId, CostParameters parameters,
			List<CostCloneResult> cloneResults, Neo4jDao neo4jDao) {
		List<CostParagraphResult> results = new ArrayList<CostParagraphResult>();
		// paragraph的一些基本信息, loc等
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("programNodeId", programNodeId);
		StatementResult sr = neo4jDao.executeReadCypher(
				CypherMethod.COST_PARAGRAPH_BASIC.toString(), properties);
		List<Record> paragraphBasic = sr.list();

		for (Record record : paragraphBasic) {
			String nodeId = record.get("nodeId").asString();
			String name = record.get("name").asString();
			int totalLine = Integer.valueOf(record.get("totalLine").asString());
			double complexity = record.get("complexity").asDouble();

			// 计算tables, variables, loops, conditions
			int tables = 0;
			int variables = 0;
			int loops = 0;
			int conditions = 0;
			properties = new HashMap<String, Object>();
			properties.put("nodeId", nodeId);
			StatementResult srCount = neo4jDao.executeReadCypher(
					CypherMethod.COST_PARAGRAPH_COUNT.toString(), properties);

			for (Record countRecord : srCount.list()) {
				int count = countRecord.get("count").asInt();
				String type = countRecord.get("type").asString();
				if (TYPE_TABLE.equalsIgnoreCase(type)) {
					tables = count;
				} else if (TYPE_VARIABLE.equalsIgnoreCase(type)) {
					variables = count;
				} else if (TYPE_CONDITION.equalsIgnoreCase(type)) {
					conditions = count;
				} else if (TYPE_LOOP.equalsIgnoreCase(type)) {
					loops = count;
				}
			}
			CostParagraphResult result = new CostParagraphResult(name,
					totalLine, loops, conditions, tables, variables, complexity);
			// 计算complexityRatio
			double complexityLoc = ((result.getLoc() - parameters
					.getMedianLoc()) / parameters.getMedianLoc())
					* parameters.getLoc();
			complexityLoc = complexityLoc < 0 ? 0 : complexityLoc;
			double complexityLoop = ((result.getLoop() - parameters
					.getMedianLoops()) / parameters.getMedianLoops())
					* parameters.getLoop();
			complexityLoop = complexityLoop < 0 ? 0 : complexityLoop;
			double complexityCondition = ((result.getConditionalStatements() - parameters
					.getMedianConditions()) / parameters.getMedianConditions())
					* parameters.getConditionalStatements();
			complexityCondition = complexityCondition < 0 ? 0
					: complexityCondition;
			double complexityTable = ((result.getTables() - parameters
					.getMedianTables()) / parameters.getMedianTables())
					* parameters.getTables();
			complexityTable = complexityTable < 0 ? 0 : complexityTable;
			double complexityVariable = ((result.getVariables() - parameters
					.getMedianVariables()) / parameters.getMedianVariables())
					* parameters.getVariables();
			complexityVariable = complexityVariable < 0 ? 0
					: complexityVariable;
			double complexityRatio = complexityLoc + complexityLoop
					+ complexityCondition + complexityTable
					+ complexityVariable;
			// TODO complexityRatio如何与result.getComplexity()再进行计算
			result.setComplexityRatio(complexityRatio);

			// 计算costPoint
			double costPoint = (result.getLoc() * complexityRatio)
					/ parameters.getCostPoint();
			result.setCostPoint(costPoint);

			// 计算manHour
			double manHour = costPoint * parameters.getManHour();
			result.setManHour(manHour);

			// 计算clone group, tier
			for (CostCloneResult cloneResult : cloneResults) {
				HashMap<String, Integer> paraTierMap = cloneResult
						.getParaTierMap();
				if (paraTierMap.get(name) != null) {
					result.setCloneGroup(cloneResult.getGroupNo());
					int tier = paraTierMap.get(name);
					result.setCloneTier(tier);
					cloneResult.setTotalCostPoint(cloneResult
							.getTotalCostPoint() + result.getCostPoint());
					// 计算实际的
					if (tier == 1) {
						if (cloneResult.getTier_1() == 0) {
							cloneResult.setCostPoint(NumberHelper.add(
									cloneResult.getCostPoint(),
									result.getCostPoint()));
						} else {
							cloneResult.setCostPoint(NumberHelper.add(
									cloneResult.getCostPoint(),
									result.getCostPoint() * RATIO));
						}
						cloneResult.setTier_1(cloneResult.getTier_1() + 1);
					} else {
						cloneResult.setCostPoint(NumberHelper.add(
								cloneResult.getCostPoint(),
								result.getCostPoint()));
					}
					break;
				}
			}
			results.add(result);
		}
		// 按照paragraphName排序
		Collections.sort(results, new Comparator<CostParagraphResult>() {
			@Override
			public int compare(CostParagraphResult o1, CostParagraphResult o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		return results;
	}

	/**
	 * 得到clone info
	 *
	 * @param projectId
	 * @return
	 */
	private List<CostCloneResult> getCostClone(int projectId) {
		List<CloneResultItem> cloneResults = cloneService
				.getCloneResult(projectId);
		List<CostCloneResult> costCloneResults = new ArrayList<CostCloneResult>();

		for (CloneResultItem item : cloneResults) {
			CostCloneResult costCloneResult = new CostCloneResult();
			// groupNo
			costCloneResult.setGroupNo(item.getGroupNo());
			// paraTierMap
			HashMap<String, Integer> paraTierMap = new HashMap<String, Integer>();
			List<CloneRelation> relations = item.getCloneRelations();
			List<CloneMember> members = item.getMembers();
			int tier_1 = 0;
			for (CloneMember member : members) {
				String memberName = member.getName();
				int memberId = member.getId();
				int tier = 0;
				for (CloneRelation relation : relations) {
					if (memberId == relation.getMemberId()) {
						if (relation.getTier_1().size() > 0) {
							tier = 1;
							tier_1++;
						} else if (relation.getTier_2().size() > 0) {
							tier = 2;
						} else if (relation.getTier_3().size() > 0) {
							tier = 3;
						} else if (relation.getTier_4().size() > 0) {
							tier = 4;
						}
						paraTierMap.put(memberName, tier);
						break;
					}
				}
			}
			costCloneResult.setParaTierMap(paraTierMap);
			costCloneResult.setParaCount(paraTierMap.size());
			if (tier_1 == 0) {
				costCloneResult.setCombinedOutput(costCloneResult
						.getParaCount());
			} else {
				costCloneResult.setCombinedOutput(costCloneResult
						.getParaCount() - tier_1 + 1 + (tier_1 - 1) * RATIO);
			}
			costCloneResults.add(costCloneResult);
		}
		return costCloneResults;
	}

	private void handleCostClone(List<CostCloneResult> results, double manHour) {
		for (CostCloneResult result : results) {
			if (result.getTier_1() == 0) {
				result.setCostPoint(0.0);
				result.setManHour(0.0);
			} else {
				double finalCostPoint = NumberHelper.sub(
						result.getTotalCostPoint(), result.getCostPoint());
				double count = result.getTier_1() - 1;
				result.setCostPoint(NumberHelper.div(finalCostPoint, count, 8));
				result.setManHour(NumberHelper.mul(result.getCostPoint(),
						manHour));
			}
		}
	}

	private CostEstimationResult getCacheCostEstimationResult(
			CostParameters costParameters) {
		String fileName = getMD5(costParameters.toString());
		Project project = projectRepository.findOne(String
				.valueOf(costParameters.getProjectId()));
		if (project == null) {
			return null;
		}
		String projectPath = project.getPath();
		String codeVersion = FileStatusUtil.checkCode(projectPath);
		String path = FilePathUtil.getCostEstimationPath(projectPath,
				codeVersion) + "/" + fileName + ".json";
		File file = new File(path);
		if (!file.exists()) {
			return null;
		} else {
			String jsonStr = null;
			try {
				jsonStr = FileUtils.readFileToString(file);
			} catch (IOException e) {
				LOGGER.error(e);
				return null;
			}

			JSONObject jo = JSONObject.fromObject(jsonStr);
			Map<String, Class> classMap = new HashMap<String, Class>();
			classMap.put("programResults", CostProgramResult.class);
			classMap.put("cloneResults", CostCloneResult.class);
			classMap.put("paragraphResults", CostParagraphResult.class);
			classMap.put("paraTierMap", HashMap.class);
			CostEstimationResult result = (CostEstimationResult) JSONObject
					.toBean(jo, CostEstimationResult.class, classMap);

			return result;
		}
	}

	private void cacheCostEstimation(int projectId, CostEstimationResult result) {
		// fileName需要唯一，使用md5
		String fileName = getMD5(result.getCostParameters().toString());
		Project project = projectRepository.findOne(String.valueOf(projectId));
		if (project == null) {
			return;
		}
		String projectPath = project.getPath();
		String codeVersion = FileStatusUtil.checkCode(projectPath);
		String path = FilePathUtil.getCostEstimationPath(projectPath,
				codeVersion) + "/" + fileName + ".json";
		File file = new File(path);
		if (!file.exists()) {
			// 该文件不存在，则将result写入
			String jsonString = JSON.toJSONString(result);
			writeFile(path, jsonString);
		}
	}

	private void writeFile(String filePath, String result) {
		try {
			FileWriter fw = new FileWriter(filePath);
			PrintWriter out = new PrintWriter(fw);
			out.write(result);
			out.println();
			fw.close();
			out.close();
		} catch (IOException e) {
			LOGGER.info("Write file IO exception");
		}
	}

	private String getMD5(String text) {
		try {
			StringBuffer buf = new StringBuffer();
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(text.getBytes());
			byte b[] = md.digest();
			int i;
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
            LOGGER.info("GetMD5 exception");
		}
		return null;
	}
}
