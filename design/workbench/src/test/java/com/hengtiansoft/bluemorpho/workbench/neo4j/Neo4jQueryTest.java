package com.hengtiansoft.bluemorpho.workbench.neo4j;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;

import com.alibaba.fastjson.JSON;
import com.hengtiansoft.bluemorpho.model.CloneMember;
import com.hengtiansoft.bluemorpho.model.CloneRelation;
import com.hengtiansoft.bluemorpho.model.CloneResultItem;
import com.hengtiansoft.bluemorpho.model.ParagraphClone;
import com.hengtiansoft.bluemorpho.model.ProgramClone;
import com.hengtiansoft.bluemorpho.workbench.dto.ClonePercentage;
import com.hengtiansoft.bluemorpho.workbench.dto.ControlFlowDto;
import com.hengtiansoft.bluemorpho.workbench.dto.CostEstimationResult;
import com.hengtiansoft.bluemorpho.workbench.dto.CostParameters;
import com.hengtiansoft.bluemorpho.workbench.dto.FileStructureNode;
import com.hengtiansoft.bluemorpho.workbench.dto.ParagraphDetailItem;
import com.hengtiansoft.bluemorpho.workbench.dto.ProgramDetailItem;
import com.hengtiansoft.bluemorpho.workbench.enums.CypherMethod;
import com.hengtiansoft.bluemorpho.workbench.neo4j.dao.Neo4jDao;
import com.hengtiansoft.bluemorpho.workbench.services.CloneService;
import com.hengtiansoft.bluemorpho.workbench.services.CodeBrowserService;
import com.hengtiansoft.bluemorpho.workbench.services.CostEstimationService;
import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;
import com.hengtiansoft.bluemorpho.workbench.util.ProcessBuilderUtil;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：May 22, 2018 6:16:53 PM
 */
public class Neo4jQueryTest {

	@Test
	public void testNeo4jQuery() {
		Neo4jDao dao = new Neo4jDao("bolt://localhost:7687");
		StatementResult result = dao
				.executeReadCypher("match (n:Program) return count(n) as num;");
		Record single = result.single();
		int num = single.get("num").asInt();
		System.out.println("Program count : " + num);
		dao.close();
	}

	@Test
	public void testReadCloneResult() {
		String jsonFilePath = "D:\\result.json";
		String totalLineFilePath = "D:\\totalLine.txt";
		String jsonStr = "";
		try {
			jsonStr = FileUtils.readFileToString(new File(jsonFilePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<CloneResultItem> crs = new ArrayList<CloneResultItem>();
		JSONArray jsonArray = JSONArray.fromObject(jsonStr);
		List list = JSONArray.toList(jsonArray);
		Map<String, Class> classMap = new HashMap<String, Class>();
		classMap.put("cloneRelations", CloneRelation.class);
		classMap.put("members", CloneMember.class);
		for (Object li : list) {
			JSONObject jo = JSONObject.fromObject(li);
			CloneResultItem cr = (CloneResultItem) JSONObject.toBean(jo,
					CloneResultItem.class, classMap);
			crs.add(cr);
		}
		int groupCount = crs.size();
		System.out.println("groupCount : " + groupCount);
		int totalCloneLine = 0;
		List<String> programNames = new ArrayList<>();
		for (CloneResultItem cr : crs) {
			for (CloneMember it : cr.getMembers()) {
				String paraName = it.getName();
				String programName = paraName.substring(0,
						paraName.indexOf("."));
				if (!programNames.contains(programName)) {
					programNames.add(programName);
				}
				totalCloneLine += it.getLine();
			}
		}
		int programCount = programNames.size();
		System.out.println("programCount : " + programCount);
		String readFileToString = "";
		try {
			readFileToString = FileUtils.readFileToString(new File(
					totalLineFilePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int totalLine = Integer.valueOf(readFileToString.trim()).intValue();
		DecimalFormat df = new DecimalFormat("0.00%");
		String percent = df.format((float) totalCloneLine / totalLine);
		System.out.println(percent);
	}

	@Test
	public void testCloneGroupInfo() {
		int projectId = 1;
		CloneService cloneService = new CloneService();
		// List<CloneTierInGroup> groups =
		// cloneService.getTierInGroup(projectId);
		// List<CloneMemberDetail> details = cloneService.getGroupForDiff(
		// projectId, 1);
		// List<String> reslutList = cloneService.getParaSourceCode(projectId,
		// "AR136513.7200-SELECT-CUST", "AR127614.7300-SELECT-CUST");
		int tier = cloneService.diff(1, "AR136513.7200-SELECT-CUST",
				"AR127614.7300-SELECT-CUST");
		System.out.println();
	}

	@Test
	public void testControlFlow() {
		CodeBrowserService codeBrowserService = new CodeBrowserService();
		List<ControlFlowDto> li = codeBrowserService.getControlFlow("1",
				"MIPVW019");
		System.out.println();
	}

	@Test
	public void testFileStructure() {
		CodeBrowserService codeBrowserService = new CodeBrowserService();
		List<FileStructureNode> result = codeBrowserService.getFileStructure(
				"1", "MIPVW019");
		System.out.println();
	}

	@Test
	public void testCostProgramResult() {
		CostEstimationService costEstimationService = new CostEstimationService();
		CostParameters parameters = new CostParameters();
		CostEstimationResult result = costEstimationService
				.getCostEstimationResult(parameters);
		System.out.println();
	}

	@Test
	public void testPyServer() {
		// start "xxxxxx" %cd%\tools\Python35\python .\server.py
		List<String> cmd = new ArrayList<String>();
		// cmd.add("start");
		// cmd.add("\"py_server\"");
		// cmd.add(FilePathUtil.getToolPath()+"/Python35/python");
		// cmd.add(FilePathUtil.getToolPath()+"/prediction/py_server/server.py");
		cmd.add(FilePathUtil.getToolPath()
				+ "/prediction/py_server/start_py_server.bat");
		ProcessBuilderUtil.startPyServer(cmd);
	}

	@Test
	public void handleClonePercentage() {
		File file = new File(
				"F:/BWB/workbench/workbench/project/ada/output/2/clone/clonePercentage.json");
		if (!file.exists()) {
			return;
		} else {
			String jsonStr = null;
			try {
				jsonStr = FileUtils.readFileToString(file);
			} catch (IOException e) {
				return;
			}
			com.alibaba.fastjson.JSONArray batchObject = getClonePara(jsonStr);
			// 批量更改nep4j
			String uri = "bolt://localhost:7687";
			Neo4jDao neo4jDao = new Neo4jDao(uri);
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("batch", batchObject);
			StatementResult sr = neo4jDao.executeReadCypher(
					CypherMethod.SET_PARA_CLONE.toString(), properties);
			int count = sr.list().size();
			neo4jDao.close();
		}
	}

	@Test
	public void getParagraphDetail() {
		String uri = "bolt://localhost:7687";
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		List<ParagraphDetailItem> details = new ArrayList<ParagraphDetailItem>();
		StatementResult sr = neo4jDao
				.executeReadCypher(CypherMethod.PARAGRAPH_DETAIL.toString());
		neo4jDao.close();
		List<Record> records = sr.list();
		for (Record rec : records) {
			details.add(new ParagraphDetailItem(rec.get("paragraphName")
					.asString(), rec.get("lines").asString(), handleTags(rec
					.get("tags")), rec.get("paragraphId").asString(), rec.get(
					"programId").asString(), rec.get("programName").asString(),
					FilePathUtil.getFilePathFromNodeId(rec.get("programId")
							.asString()), rec.get("startLine").asString(), rec
							.get("endLine").asString(), rec.get("complexity")
							.asInt(), handleClonePercentage(rec.get("clone")
							.asString(), rec.get("lines").asString())));
		}
		System.out.println();
	}

	@Test
	public void getProgramDetail() {
		String uri = "bolt://localhost:7687";
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		List<ProgramDetailItem> details = new ArrayList<ProgramDetailItem>();
		StatementResult sr = neo4jDao
				.executeReadCypher(CypherMethod.PROGRAM_DETAIL.toString());
		neo4jDao.close();
		List<Record> records = sr.list();
		for (Record rec : records) {
			String nodeId = rec.get("nodeId").asString();
			// relativePath用于codeBrowser，folderTree中的文件定位，以及获取文件源代码
			String relativePath = FilePathUtil.getFilePathFromNodeId(nodeId);
			details.add(new ProgramDetailItem(nodeId, rec.get("name")
					.asString(), rec.get("type").asString(), rec.get("lines")
					.asString(), handleTags(rec.get("tags")), relativePath,
					String.valueOf(rec.get("complexity").asInt()),
					handleClonePercentage(rec.get("clone").asString(),
							String.valueOf(rec.get("paraLines").asInt()))));
		}
		System.out.println();
	}

	private String handleClonePercentage(String clone, String lines) {
		DecimalFormat df = new DecimalFormat("0.00%");
		String r = df.format(Double.valueOf(clone));
		if (r.equals("100.00%")) {
			return "100%";
		}
		return r;
	}

	private String handleTags(Value tag) {
		String tags = "";
		if (!tag.isEmpty()) {
			List<String> tagList = new ArrayList<String>();
			for (Object object : tag.asList()) {
				tagList.add(String.valueOf(object));
			}
			Collections.sort(tagList);
			tags = StringUtils.join(tagList, ", ");
		}
		return tags;
	}

	private com.alibaba.fastjson.JSONArray getClonePara(String jsonStr) {
		com.alibaba.fastjson.JSONArray result = new com.alibaba.fastjson.JSONArray();
		List<ClonePercentage> list = com.alibaba.fastjson.JSONArray.parseArray(
				jsonStr, ClonePercentage.class);
		List<ParagraphClone> cloneList = new ArrayList<ParagraphClone>();
		for (ClonePercentage clonePercentage : list) {
			String paraName = clonePercentage.getName();
			ParagraphClone maxMember = getMaxCloneLines(clonePercentage
					.getCloneMembers());
			if (maxMember != null) {
				cloneList.add(maxMember);
			}
		}
		result = com.alibaba.fastjson.JSONArray.parseArray(JSON
				.toJSONString(cloneList));
		return result;
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

	@Test
	public void programClone() {
		File file = new File(
				"F:/BWB/workbench/workbench/project/ada/output/2/clone/clonePercentage.json");
		if (!file.exists()) {
			return;
		} else {
			String jsonStr = null;
			try {
				jsonStr = FileUtils.readFileToString(file);
			} catch (IOException e) {
				return;
			}

			List<ClonePercentage> clonePercentages = com.alibaba.fastjson.JSONArray
					.parseArray(jsonStr, ClonePercentage.class);
			List<ProgramClone> programClones = new ArrayList<ProgramClone>();
			String uri = "bolt://localhost:" + 7688;
			Neo4jDao neo4jDao = new Neo4jDao(uri);
			// 先按照program 得到programNodeId, programName, 以及paragraphName组合
			StatementResult sr = neo4jDao
					.executeReadCypher(CypherMethod.PROGRAM_WITH_ALL_PARAGRAPH
							.toString());
			List<Record> records = sr.list();
			neo4jDao.close();
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
									paragraphPairs
											.add(new ParagraphClone(
													leftPara.toString(),
													rightPara.toString(),
													paragraphClone
															.getCloneLines(),
													paragraphClone
															.getClonePercentage()));
									totalCloneLine += Integer
											.valueOf(paragraphClone
													.getCloneLines());
									break;
								}
							}
						}
						// 计算每组program的相似度
						programClone.setParagraphPairs(paragraphPairs);
						programClone.setClone(String.valueOf(totalCloneLine));
						programClone
								.setPercentage(String
										.valueOf(((double)(totalCloneLine * 2) / (leftParaLine + rightParaLine))));
						programClones.add(programClone);
					}
				}
			}
			
			List<ProgramClone> result = new ArrayList<ProgramClone>();
			Map<String, ProgramClone> programCloneMap = new HashMap<String, ProgramClone>();
			
			for (ProgramClone pair : programClones) {
				if (programCloneMap.get(pair.getNodeId()) != null) {
					if (Double.valueOf(pair.getPercentage()).compareTo(
							Double.valueOf(programCloneMap.get(pair.getNodeId())
									.getPercentage())) > 0) {
						programCloneMap.put(pair.getNodeId(), pair);
					} 
				}else {
					programCloneMap.put(pair.getNodeId(), pair);
				}
			}
			result.addAll(programCloneMap.values());
			// 将该结果写入clone output，用于clone结果展现
			FilePathUtil
					.writeFile(
							"F:/BWB/workbench/workbench/project/ada/output/2/clone/programResult.json",
							JSON.toJSONString(programClones), false);
			String jsonStr2 = "";
			List<ProgramClone> programClones2 = new ArrayList<ProgramClone>();
			try {
				jsonStr2 = FileUtils.readFileToString(new File("F:/BWB/workbench/workbench/project/ada/output/2/clone/programResult.json"));
			} catch (IOException e) {
			}
			programClones2 = com.alibaba.fastjson.JSONArray.parseArray(jsonStr2,
					ProgramClone.class);
			System.out.println();
		}
	}

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
	
	@Test
	public void getProgramClone() {
//		Project project = projectRepository.findOne(String.valueOf(projectId));
//		String projectPath = project.getPath();
		String jsonFilePath ="F:/BWB/workbench/workbench/project/ada/output/2/clone/clonePercentage.json";
		String jsonStr = "";
		List<ProgramClone> programClones = new ArrayList<ProgramClone>();
		try {
			jsonStr = FileUtils.readFileToString(new File(jsonFilePath));
		} catch (IOException e) {
//			LOGGER.error(e);
			
		}
		programClones = com.alibaba.fastjson.JSONArray.parseArray(jsonStr,
				ProgramClone.class);
		System.out.println();
	}
}
