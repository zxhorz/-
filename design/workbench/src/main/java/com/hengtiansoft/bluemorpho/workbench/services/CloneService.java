package com.hengtiansoft.bluemorpho.workbench.services;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hengtiansoft.bluemorpho.model.CloneDiffInfo;
import com.hengtiansoft.bluemorpho.model.CloneLinesInGroup;
import com.hengtiansoft.bluemorpho.model.CloneMember;
import com.hengtiansoft.bluemorpho.model.CloneMemberDetail;
import com.hengtiansoft.bluemorpho.model.CloneRelation;
import com.hengtiansoft.bluemorpho.model.CloneResultItem;
import com.hengtiansoft.bluemorpho.model.CloneTierInGroup;
import com.hengtiansoft.bluemorpho.model.ProgramClone;
import com.hengtiansoft.bluemorpho.workbench.domain.Project;
import com.hengtiansoft.bluemorpho.workbench.dto.CloneDiffRequest;
import com.hengtiansoft.bluemorpho.workbench.dto.CloneResult;
import com.hengtiansoft.bluemorpho.workbench.enums.CypherMethod;
import com.hengtiansoft.bluemorpho.workbench.neo4j.dao.Neo4jDao;
import com.hengtiansoft.bluemorpho.workbench.repository.ProjectRepository;
import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;
import com.hengtiansoft.bluemorpho.workbench.util.FileStatusUtil;
import com.hengtiansoft.bluemorpho.workbench.util.PortUtil;
import com.hengtiansoft.bluemorpho.workbench.util.ProcessBuilderUtil;
import com.hengtiansoft.bluemorpho.workbench.util.TemplateUtil;

@Service
public class CloneService {

	private static final Logger LOGGER = Logger.getLogger(CloneService.class);

	@Autowired
	ProjectRepository projectRepository;
	@Autowired
	PortUtil portUtil;

	private static final String CLONE_CONFIG = "clone_arg.ftl";
	private static final String CLONE_CYPHER_CONFIG = "clone_cypher.ftl";
	private static final String CLONE_ARGUMENT = "/arguments.properties";
	private static final String CLONE_CYPHER_ARGUMENT = "/cypher.xml";

	public CloneResult getCloneSummary(int projectId) {
		Project project = projectRepository.findOne(String.valueOf(projectId));
		String projectPath = project.getPath();
		String totalLineFilePath = FilePathUtil.getCloneTotalLinePath(
				projectPath, FileStatusUtil.checkCode(projectPath));
		List<CloneResultItem> crs = getCloneResult(projectId);
		if (null == crs || crs.size() == 0) {
			return new CloneResult(0, 0, "0", "0", "0", "0", "0");
		}
		int groupCount = crs.size();
		int totalCloneLine = 0;
		int tier1 = 0, tier2 = 0, tier3 = 0, tier4 = 0;
		List<String> programNames = new ArrayList<>();
		for (CloneResultItem cr : crs) {
			for (CloneMember it : cr.getMembers()) {
				String paraName = it.getName();
				String programName = paraName.substring(0,
						paraName.indexOf("."));
				if (!programNames.contains(programName)) {
					programNames.add(programName);
				}
//				totalCloneLine += it.getLine();
			}
			// 每个group中不同tier的clone lines总数，用于界面展示各类tier的饼图
			CloneLinesInGroup cloneLinesInGroup = cr.getCloneLines();
			tier1 +=cloneLinesInGroup.getTier_1_lines();
			tier2 +=cloneLinesInGroup.getTier_2_lines();
			tier3 +=cloneLinesInGroup.getTier_3_lines();
			tier4 +=cloneLinesInGroup.getTier_4_lines();
		}
		totalCloneLine = tier1 + tier2 + tier3 + tier4;

//		List<CloneTierInGroup> cti = getTierInGroup(projectId);
//		int tier1 = 0, tier2 = 0, tier3 = 0, tier4 = 0;
//		for (CloneTierInGroup cg : cti) {
//			tier1 = tier1 + cg.getTier1Loc();
//			tier2 = tier2 + cg.getTier2Loc();
//			tier3 = tier3 + cg.getTier3Loc();
//			tier4 = tier4 + cg.getTier4Loc();
//		}
		int programCount = programNames.size();
		String readFileToString = "";
		try {
			readFileToString = FileUtils.readFileToString(new File(
					totalLineFilePath));
		} catch (IOException e) {
			LOGGER.error(e);
			return new CloneResult(0, 0, "0", "0", "0", "0", "0");
		}
		int totalLine = Integer.valueOf(readFileToString.trim()).intValue();
//		DecimalFormat df = new DecimalFormat("0.00%");
//		String percent = df.format((float) totalCloneLine / totalLine);
//		String percent1 = String.valueOf((float) tier1 / totalLine);
//		String percent2 = String.valueOf((float) tier2 / totalLine);
//		String percent3 = String.valueOf((float) tier3 / totalLine);
//		String percent4 = String.valueOf((float) tier4 / totalLine);
		String percent = StringUtils.substringBeforeLast(getTotalClone(projectId),"_");
		String percent1 = String.valueOf((double) tier1 / totalLine);
		String percent2= String.valueOf((double) tier2 / totalLine);
		String percent3 = String.valueOf((double) tier3 / totalLine);
		// 由于目前没有tier_4，此时percent4表示Non-clone-code
		String percent4 = String.valueOf((double) (totalLine - tier1 - tier2 - tier3) / totalLine);

		return new CloneResult(groupCount, programCount, percent, percent1,
				percent2, percent3, percent4);
	}

	public String getTotalClone(int projectId) {
		// TODO: total clone用paragraph还是program的clone行数来计算，paragraph计算的话，clone百分比会更大一些。
		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		// 超过该threshold时，就称之为高重复率的程序，目前定义是超过10%的代码重复率
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("threshold", "0.1");
		StatementResult sr = neo4jDao.executeReadCypher(
				CypherMethod.TOTAL_CLONE.toString(), properties);
		neo4jDao.close();
		List<Record> records = sr.list();
		// union必须返回值必须统一，于是将paragraph totalLines也 别名为clone
		if (records.size() == 2) {
			Record rec0 = records.get(0);
			Record rec1 = records.get(1);
			List<Object> counts = rec0.get("count").asList();
			int count = 0;
			// 该部分的cypher语句为：collect(n1.clonePercentage >= {threshold})
			for (Object object : counts) {
				// true的个数即为满足条件的program个数
				if ("true".equalsIgnoreCase(object.toString())) {
					count++;
				}
			}
			int clone = rec0.get("clone").asInt();
			int totalLines = rec1.get("clone").asInt();
			DecimalFormat df = new DecimalFormat("0.00%");
			String r = df.format(Float.valueOf(clone)
					/ Float.valueOf(totalLines));
			if (r.equals("00.00%")) {
				return "0%_" + count;
			} else if (r.endsWith(".00%")) {
				return StringUtils.substringBefore(r, ".") + "%_" + count;
			} else {
				return r + "_" + count;
			}
		}
		return "0%_0";
	}

	private List<String> buildCloneCommandForDiff(String projectPath,
			String scriptFile, String leftParaName, String rightParaName,
			String cloneConfig, String scriptPath, String path) {
		List<String> cmd = new ArrayList<>();
		cmd.add("python");
		cmd.add(scriptFile);
		cmd.add("diff");
		cmd.add(leftParaName);
		cmd.add(rightParaName);
		cmd.add(cloneConfig);
		cmd.add(scriptPath);
		cmd.add(path);
		return cmd;
	}

	/**
	 * 得到每个group的各个tier的信息，个数和总行数
	 *
	 * @param projectId
	 * @return
	 */
	public List<CloneTierInGroup> getTierInGroup(int projectId) {
		List<CloneTierInGroup> tierInGroups = new ArrayList<CloneTierInGroup>();
		List<CloneResultItem> crs = getCloneResult(projectId);

		for (CloneResultItem resultItem : crs) {
			CloneTierInGroup group = new CloneTierInGroup();
//			List<CloneMember> cloneMembers = resultItem.getMembers();
			// set，避免重复
			Set<Integer> tier_1 = new HashSet<Integer>();
			Set<Integer> tier_2 = new HashSet<Integer>();
			Set<Integer> tier_3 = new HashSet<Integer>();
			Set<Integer> tier_4 = new HashSet<Integer>();

			for (CloneRelation relation : resultItem.getCloneRelations()) {
				tier_1.addAll(relation.getTier_1());
				tier_2.addAll(relation.getTier_2());
				tier_3.addAll(relation.getTier_3());
				tier_4.addAll(relation.getTier_4());
			}
			// set group info
			group.setGroupNo(resultItem.getGroupNo());
			group.setTier1Loc(resultItem.getCloneLines().getTier_1_lines());
			group.setTier2Loc(resultItem.getCloneLines().getTier_2_lines());
			group.setTier3Loc(resultItem.getCloneLines().getTier_3_lines());
			group.setTier4Loc(resultItem.getCloneLines().getTier_4_lines());
			group.setTier1Num(tier_1.size());
			group.setTier2Num(tier_2.size());
			group.setTier3Num(tier_3.size());
			group.setTier4Num(tier_4.size());
			tierInGroups.add(group);
		}
		return tierInGroups;
	}

	/**
	 * 选中某一个group，得到该group中的每个member的tier信息，便于做diff分析
	 *
	 * @param projectId
	 * @param groupNo
	 * @return
	 */
	public CloneDiffInfo getGroupForDiff(int projectId, int groupNo) {
		CloneDiffInfo cdInfo = new CloneDiffInfo();
		List<CloneResultItem> crs = getCloneResult(projectId);
		CloneResultItem selectedGroup = null;
		// 找到groupNo对应的记录
		for (CloneResultItem item : crs) {
			if (item.getGroupNo() == groupNo) {
				selectedGroup = item;
				cdInfo.setMemberInfo(item.getMembers());
				break;
			}
		}
		// 存储member的id和name的对应关系,用于后续查找
		Map<Integer, String> memberNames = new HashMap<Integer, String>();
		List<CloneMember> members = selectedGroup.getMembers();
		for (CloneMember member : members) {
			memberNames.put(member.getId(), member.getName());
		}

		// 得到member，用于前端渲染
		List<CloneMemberDetail> memberDetails = new ArrayList<CloneMemberDetail>();
		for (CloneMember member : members) {
			for (CloneRelation relation : selectedGroup.getCloneRelations()) {
				if (member.getId() == relation.getMemberId()) {
					CloneMemberDetail memberDetail = new CloneMemberDetail();
					memberDetail.setMemberId(member.getId());
					memberDetail.setMemberName(member.getName());
					memberDetail.getTier_1().addAll(
							getMemberName(memberNames, relation.getTier_1()));
					memberDetail.getTier_2().addAll(
							getMemberName(memberNames, relation.getTier_2()));
					memberDetail.getTier_3().addAll(
							getMemberName(memberNames, relation.getTier_3()));
					memberDetail.getTier_4().addAll(
							getMemberName(memberNames, relation.getTier_4()));
					// 若tier1,2,3,4为空，则忽略
					if (memberDetail.getTier_1().size() == 0
							&& memberDetail.getTier_2().size() == 0
							&& memberDetail.getTier_3().size() == 0
							&& memberDetail.getTier_4().size() == 0) {
						continue;
					}
					memberDetails.add(memberDetail);
				}
			}
		}
		cdInfo.setMemberTier(memberDetails);
		return cdInfo;
	}

	/**
	 * 调用python脚本，clone diff，选择两个段名，diff分析后返回两个段的分析结果，所属的tier数字
	 *
	 * @param projectId
	 * @param leftParaName
	 * @param rightParaName
	 * @return
	 */
	public int diff(int projectId, String leftParaName, String rightParaName) {
		Project project = projectRepository.findOne(String.valueOf(projectId));
		if (null == project) {
			return 1000;
		}
		String projectPath = project.getPath();
		String codeVersion = FileStatusUtil.checkCode(projectPath);
		Map<String, Object> pathConfig = FilePathUtil
				.getDefaultClonePathConfig(projectPath,
						String.valueOf(portUtil.getBoltPort(projectPath)),
						codeVersion);
		String cloneConfig = FilePathUtil.getCloneConfig(projectPath);
		TemplateUtil.generateFile(CLONE_CONFIG, cloneConfig + CLONE_ARGUMENT,
				pathConfig);
		TemplateUtil.generateFile(CLONE_CYPHER_CONFIG, cloneConfig
				+ CLONE_CYPHER_ARGUMENT, new HashMap<String, Object>());
		String toolPath = FilePathUtil.getToolPath();
		List<String> cmd = buildCloneCommandForDiff(projectPath, toolPath
				+ "/clone/script/main.py", leftParaName, rightParaName,
				cloneConfig + CLONE_ARGUMENT, toolPath + "/clone/script",
				projectPath);
		int result = ProcessBuilderUtil.processBuilderForCloneDiff(cmd,
				toolPath);
		return result;
	}

	/**
	 * 选中两个段，从neo4j中得到它们的源代码
	 *
	 * @param projectId
	 * @param leftParaName
	 * @param rightParaName
	 * @return
	 */
	public List<String> getParaSourceCode(CloneDiffRequest diffInfo) {

		Project project = projectRepository.findOne(diffInfo.getProjectId());
		int boltPort = portUtil.getBoltPort(project.getPath());
		String uri = "bolt://localhost:" + boltPort;
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		List<String> paraNames = new ArrayList<String>();
		paraNames.add(diffInfo.getLeftParaName());
		paraNames.add(diffInfo.getRightParaName());
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("paraNames", paraNames);
		StatementResult sr = neo4jDao.executeReadCypher(
				CypherMethod.GET_PARAGRAPH_CONTENT.toString(), properties);
		neo4jDao.close();
		List<String> result = new ArrayList<String>();
		List<Record> records = sr.list();
		for (Record rec : records) {
		    if(rec.get("name").asString().equals(diffInfo.getLeftParaName())){
		        result.add(rec.get("content").asString());
		        break;
		    }
		}
		for (Record rec : records) {
            if(rec.get("name").asString().equals(diffInfo.getRightParaName())){
                result.add(rec.get("content").asString());
                break;
            }
        }
		
		return result;
	}

	/**
	 * 根据tier中的memberIds，得到其对应的memberNames
	 *
	 * @param memberNames
	 *            : <id, name>
	 * @param tiers
	 * @return
	 */
	private List<String> getMemberName(Map<Integer, String> memberNames,
			List<Integer> tiers) {
		List<String> result = new ArrayList<String>();
		for (Integer tier : tiers) {
			result.add(memberNames.get(tier));
		}
		return result;
	}

	public List<CloneResultItem> getCloneResult(int projectId) {
		Project project = projectRepository.findOne(String.valueOf(projectId));
		String projectPath = project.getPath();
		String jsonFilePath = FilePathUtil.getCloneResultPath(projectPath,
				FileStatusUtil.checkCode(projectPath));
		String jsonStr = "";
		List<CloneResultItem> crs = new ArrayList<CloneResultItem>();
		try {
			jsonStr = FileUtils.readFileToString(new File(jsonFilePath));
		} catch (IOException e) {
			LOGGER.error(e);
			return crs;
		}

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
		return crs;
	}

	/**
	 * clone 分析时候将programClone结果存储在programCloneResultPath中
	 * 
	 * 从该文件中获取该结果用于界面展示
	 * 
	 * @param projectId
	 * @return
	 */
	public List<ProgramClone> getProgramClone(int projectId) {
		Project project = projectRepository.findOne(String.valueOf(projectId));
		String projectPath = project.getPath();
		String jsonFilePath = FilePathUtil.getProgramCloneResultPath(
				projectPath, FileStatusUtil.checkCode(projectPath));
		String jsonStr = "";
		List<ProgramClone> programClones = new ArrayList<ProgramClone>();
		try {
			jsonStr = FileUtils.readFileToString(new File(jsonFilePath));
		} catch (IOException e) {
			LOGGER.error(e);
			return programClones;
		}
		programClones = com.alibaba.fastjson.JSONArray.parseArray(jsonStr,
				ProgramClone.class);
		return programClones;
	}
}
