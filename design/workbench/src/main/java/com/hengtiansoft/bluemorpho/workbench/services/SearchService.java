package com.hengtiansoft.bluemorpho.workbench.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hengtiansoft.bluemorpho.workbench.domain.Project;
import com.hengtiansoft.bluemorpho.workbench.dto.HighLightDto;
import com.hengtiansoft.bluemorpho.workbench.dto.HitResultDto;
import com.hengtiansoft.bluemorpho.workbench.dto.SearchResult;
import com.hengtiansoft.bluemorpho.workbench.dto.ServerSeachJson;
import com.hengtiansoft.bluemorpho.workbench.dto.TagInfoRequest;
import com.hengtiansoft.bluemorpho.workbench.dto.TagResponse;
import com.hengtiansoft.bluemorpho.workbench.enums.CypherMethod;
import com.hengtiansoft.bluemorpho.workbench.enums.SystemDocType;
import com.hengtiansoft.bluemorpho.workbench.neo4j.dao.Neo4jDao;
import com.hengtiansoft.bluemorpho.workbench.repository.ProjectRepository;
import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;
import com.hengtiansoft.bluemorpho.workbench.util.FileStatusUtil;
import com.hengtiansoft.bluemorpho.workbench.util.PortUtil;
import com.hengtiansoft.bluemorpho.workbench.util.ProcessBuilderUtil;

/**
 * @Description: search & tagging
 * @author gaochaodeng
 * @date Jun 12, 2018
 */
@Service
public class SearchService {
	@Autowired
	ProjectRepository projectRepository;
	@Autowired
	PortUtil portUtil;

	private static final Logger LOGGER = Logger.getLogger(SearchService.class);
	private static final String PARA_RESULT_JSON = "/paragraph_seach_result.json";
	private static final String PROGRAM_RESULT_JSON = "/program_seach_result.json";
	private static final String SEARCH_JAR = "/CodeAnalysis-0.0.1-SNAPSHOT_search.jar";

	/**
	 * search program/paragraph by keywords or similar codes
	 *
	 * @param projectId
	 * @param condition
	 * @return
	 */
	public List<SearchResult> searchByKeywordsOrCodes(String projectId,
			String queryCondition, String condition) {
		List<SearchResult> results = new ArrayList<SearchResult>();
		Project project = projectRepository.findOne(projectId);
		if (project == null) {
			return results;
		}
		String projectPath = project.getPath();
		String toolPath = FilePathUtil.getToolPath();
		String codeVersion = FileStatusUtil.checkCode(projectPath);
		String paraCorpusName = "paragraph_corpus_" + projectId + "_"
				+ codeVersion;
		String paraSourcePath = FilePathUtil.getParagraphsPath(projectPath,
				codeVersion);
		String paraJsonPath = FilePathUtil.getSearchOutputPath(projectPath)
				+ PARA_RESULT_JSON;

		String programCorpusName = "program_corpus_" + projectId + "_"
				+ codeVersion;
		String sourcePath = FilePathUtil.getPath(projectPath, "SOURCE");
		String programSourcePath = FilePathUtil.getPath(sourcePath, "COBOL");
		String programJsonPath = FilePathUtil.getSearchOutputPath(projectPath)
				+ PROGRAM_RESULT_JSON;

		// program search
		List<SearchResult> programResults = new ArrayList<SearchResult>();
		programResults.addAll(execSearch(projectPath, toolPath, queryCondition,
				programCorpusName, programSourcePath, programJsonPath,
				condition, "1"));
		// paragraph search
		List<SearchResult> paraResults = new ArrayList<SearchResult>();
		paraResults.addAll(execSearch(projectPath, toolPath, queryCondition,
				paraCorpusName, paraSourcePath, paraJsonPath, condition, "2"));
		// 从neo4j中找到每条记录对应的tag
		programResults = fillTags(programResults, projectId, "1");
		paraResults = fillTags(paraResults, projectId, "2");
		// 结果合并
		results.addAll(programResults);
		results.addAll(paraResults);
		// 通过matchScore排序
		Collections.sort(results, new Comparator<SearchResult>() {
			@Override
			public int compare(SearchResult o1, SearchResult o2) {
				return o2.getMatch_Score().compareTo(o1.getMatch_Score());
			}
		});
		return results;
	}

	/**
	 * search program/paragraph with tags(from neo4j)
	 *
	 * @param projectId
	 * @param condition
	 */
	public List<SearchResult> searchByTags(String projectId,
			List<String> condition) {
		// 直接操作neo4j库，不需要调用search的jar包
		List<SearchResult> results = new ArrayList<SearchResult>();
		results.addAll(queryByTags(projectId, condition, "1"));
//		results.addAll(queryByTags(projectId, condition, "2"));
		return results;
	}

	public void saveTag(TagInfoRequest tagInfoRequest) {
		Project project = projectRepository.findOne(tagInfoRequest
				.getProjectId());
		if (project == null) {
			return;
		}
		//codesearch中为批量对不同类型文件删除tag
		if("codesearch".equalsIgnoreCase(tagInfoRequest.getFromPage())){
            if ("ADD".equals(tagInfoRequest.getAction())) {
                addTags(tagInfoRequest.getProjectId(), tagInfoRequest.getSelectedNames(), tagInfoRequest.getAddTags());
            } else if ("REMOVE".equals(tagInfoRequest.getAction())) {
                removeTags(tagInfoRequest.getProjectId(), tagInfoRequest.getSelectedNames(), tagInfoRequest.getDeleteTags());
            }
        } else {
            if ("ADD".equals(tagInfoRequest.getAction())) {
                saveAddedTags(tagInfoRequest);
            } else if ("REMOVE".equals(tagInfoRequest.getAction())) {
                saveRemovedTags(tagInfoRequest);
            }
        }
	}
	
	
	public void saveAddedTags(TagInfoRequest tagInfoRequest) {
		boolean singleName = false;
		List<String> selectedNames = tagInfoRequest.getSelectedNames();
		List<String> addTags = tagInfoRequest.getAddTags();
		String type = tagInfoRequest.getType();
		String addCypherStr = null;
		String removeCypherStr = null;
		//如果之前在该program执行过remove操作，那么在添加新的tag前，需要先删除包含新增tag的remove记录。
		if (SystemDocType.PROGRAM.toString().equalsIgnoreCase(type)) {
			if ("codebrowser".equalsIgnoreCase(tagInfoRequest.getFromPage())) {
				addCypherStr = CypherMethod.ADD_PROGRAM_TAG.toString();
				removeCypherStr = CypherMethod.REMOVE_PROGRAM_TAG_WITHOUT.toString();
				singleName = true;
			} else {
				addCypherStr = CypherMethod.ADD_PROGRAM_TAG_WITH_NODEID
						.toString();
				removeCypherStr = CypherMethod.REMOVE_PROGRAM_TAG_WITH_NODEID_WITHOUT
						.toString();
			}
		} else if (SystemDocType.TABLE.toString().equalsIgnoreCase(type)) {
			addCypherStr = CypherMethod.ADD_TABLE_TAG.toString();
			removeCypherStr = CypherMethod.REMOVE_TABLE_TAG_WITHOUT.toString();
		} else if (SystemDocType.FILE.toString().equalsIgnoreCase(type)) {
			addCypherStr = CypherMethod.ADD_FILE_TAG.toString();
			removeCypherStr = CypherMethod.REMOVE_FILE_TAG_WITHOUT.toString();
		} else if (SystemDocType.COPYBOOK.toString().equalsIgnoreCase(type)) {
			if ("codebrowser".equalsIgnoreCase(tagInfoRequest.getFromPage())) {
				addCypherStr = CypherMethod.ADD_COPYBOOK_TAG_WITH_NAME
						.toString();
				removeCypherStr = CypherMethod.REMOVE_COPYBOOK_TAG_WITH_NAME_WITHOUT
						.toString();
				singleName = true;
			} else {
				addCypherStr = CypherMethod.ADD_COPYBOOK_TAG.toString();
				removeCypherStr = CypherMethod.REMOVE_COPYBOOK_TAG_WITHOUT.toString();
			}
		} else if (SystemDocType.JOB.toString().equalsIgnoreCase(type)) {
			if ("codebrowser".equalsIgnoreCase(tagInfoRequest.getFromPage())) {
				addCypherStr = CypherMethod.ADD_JOB_TAG_WITH_NAME.toString();
				removeCypherStr = CypherMethod.REMOVE_JOB_TAG_WITH_NAME_WITHOUT
						.toString();
				singleName = true;
			} else {
				addCypherStr = CypherMethod.ADD_JCL_TAG.toString();
				removeCypherStr = CypherMethod.REMOVE_JCL_TAG_WITHOUT.toString();
			}
		} else if (SystemDocType.PROC.toString().equalsIgnoreCase(type)) {
			if ("codebrowser".equalsIgnoreCase(tagInfoRequest.getFromPage())) {
				addCypherStr = CypherMethod.ADD_PROC_TAG_WITH_NAME.toString();
				removeCypherStr = CypherMethod.REMOVE_PROC_TAG_WITH_NAME_WITHOUT
						.toString();
				singleName = true;
			} else {
				addCypherStr = CypherMethod.ADD_JCL_TAG.toString();
				removeCypherStr = CypherMethod.REMOVE_JCL_TAG_WITHOUT.toString();
			}
		}
		String uri = portUtil.getBoltUrl(tagInfoRequest.getProjectId());
		Neo4jDao neo4jDao = new Neo4jDao(uri);

		Map<String, Object> properties = new HashMap<String, Object>();
		// remove Tags
		properties = new HashMap<String, Object>();
		if (singleName) {
			properties.put("names", selectedNames.get(0));
		} else {
			properties.put("names", selectedNames);
		}
		properties.put("tagNames", addTags);
		StatementResult sr = neo4jDao.executeReadCypher(removeCypherStr, properties);
		
		// add tags
		StatementResult rsr;
		for (String tag : addTags) {
			properties = new HashMap<String, Object>();
			if (singleName) {
				properties.put("names", selectedNames.get(0));
			} else {
				properties.put("names", selectedNames);
			}
			properties.put("tagName", tag);
			rsr = neo4jDao.executeReadCypher(addCypherStr, properties);
		}
		neo4jDao.close();
	}
	
	public void saveRemovedTags(TagInfoRequest tagInfoRequest) {
		boolean singleName = false;
		List<String> selectedNames = tagInfoRequest.getSelectedNames();
		List<String> removeTags = tagInfoRequest.getDeleteTags();
		String type = tagInfoRequest.getType();
		String addCypherStr = null;
		String removeCypherStr = null;
		//删除tag之后，需要给删除的tag新建remove记录。
		if (SystemDocType.PROGRAM.toString().equalsIgnoreCase(type)) {
			if ("codebrowser".equalsIgnoreCase(tagInfoRequest.getFromPage())) {
				addCypherStr = CypherMethod.ADD_PROGRAM_TAG_WITHOUT.toString();
				removeCypherStr = CypherMethod.REMOVE_PROGRAM_TAG.toString();
				singleName = true;
			} else {
				addCypherStr = CypherMethod.ADD_PROGRAM_TAG_WITH_NODEID_WITHOUT
						.toString();
				removeCypherStr = CypherMethod.REMOVE_PROGRAM_TAG_WITH_NODEID
						.toString();
			}
		} else if (SystemDocType.TABLE.toString().equalsIgnoreCase(type)) {
			addCypherStr = CypherMethod.ADD_TABLE_TAG_WITHOUT.toString();
			removeCypherStr = CypherMethod.REMOVE_TABLE_TAG.toString();
		} else if (SystemDocType.FILE.toString().equalsIgnoreCase(type)) {
			addCypherStr = CypherMethod.ADD_FILE_TAG_WITHOUT.toString();
			removeCypherStr = CypherMethod.REMOVE_FILE_TAG.toString();
		} else if (SystemDocType.COPYBOOK.toString().equalsIgnoreCase(type)) {
			if ("codebrowser".equalsIgnoreCase(tagInfoRequest.getFromPage())) {
				addCypherStr = CypherMethod.ADD_COPYBOOK_TAG_WITH_NAME_WITHOUT
						.toString();
				removeCypherStr = CypherMethod.REMOVE_COPYBOOK_TAG_WITH_NAME
						.toString();
				singleName = true;
			} else {
				addCypherStr = CypherMethod.ADD_COPYBOOK_TAG_WITHOUT.toString();
				removeCypherStr = CypherMethod.REMOVE_COPYBOOK_TAG.toString();
			}
		} else if (SystemDocType.JOB.toString().equalsIgnoreCase(type)) {
			if ("codebrowser".equalsIgnoreCase(tagInfoRequest.getFromPage())) {
				addCypherStr = CypherMethod.ADD_JOB_TAG_WITH_NAME_WITHOUT.toString();
				removeCypherStr = CypherMethod.REMOVE_JOB_TAG_WITH_NAME
						.toString();
				singleName = true;
			} else {
				addCypherStr = CypherMethod.ADD_JCL_TAG_WITHOUT.toString();
				removeCypherStr = CypherMethod.REMOVE_JCL_TAG.toString();
			}
		} else if (SystemDocType.PROC.toString().equalsIgnoreCase(type)) {
			if ("codebrowser".equalsIgnoreCase(tagInfoRequest.getFromPage())) {
				addCypherStr = CypherMethod.ADD_PROC_TAG_WITH_NAME_WITHOUT.toString();
				removeCypherStr = CypherMethod.REMOVE_PROC_TAG_WITH_NAME
						.toString();
				singleName = true;
			} else {
				addCypherStr = CypherMethod.ADD_JCL_TAG_WITHOUT.toString();
				removeCypherStr = CypherMethod.REMOVE_JCL_TAG.toString();
			}
		}
		String uri = portUtil.getBoltUrl(tagInfoRequest
				.getProjectId());
		Neo4jDao neo4jDao = new Neo4jDao(uri);

		Map<String, Object> properties = new HashMap<String, Object>();
		
		// remove Tags
		properties = new HashMap<String, Object>();
		if (singleName) {
			properties.put("names", selectedNames.get(0));
		} else {
			properties.put("names", selectedNames);
		}
		properties.put("tagNames", removeTags);
		StatementResult sr = neo4jDao.executeReadCypher(removeCypherStr, properties);
		
		// add tags
		StatementResult rsr;
		for (String tag : removeTags) {
			properties = new HashMap<String, Object>();
			if (singleName) {
				properties.put("names", selectedNames.get(0));
			} else {
				properties.put("names", selectedNames);
			}
			properties.put("tagName", tag);
			rsr = neo4jDao.executeReadCypher(addCypherStr, properties);
		}
		neo4jDao.close();
	}

	//codesearch 增加tag
	public void addTags(String projectId, List<String> selectedNames,
			List<String> tags) {
		Project project = projectRepository.findOne(projectId);
		if (project == null) {
			return;
		}
//		String projectPath = project.getPath();
//		int boltPort = portUtil.getBoltPort(projectPath);
//		String uri = "bolt://localhost:" + boltPort;
		String uri = portUtil.getBoltUrl(project.getId());
		Neo4jDao neo4jDao = new Neo4jDao(uri);

		for (String tag : tags) {
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("names", selectedNames);
			properties.put("tagName", tag);
	        neo4jDao.executeReadCypher(
	                    CypherMethod.REMOVE_TAG_WITH_NODEID_WITHOUT.toString(), properties);
			neo4jDao.executeReadCypher(
					CypherMethod.ADD_TAG_WITH_NODEID.toString(), properties);
		}
		neo4jDao.close();
	}

	//codesearch删除tag
	public void removeTags(String projectId, List<String> selectedNames,
			List<String> tags) {
		Project project = projectRepository.findOne(projectId);
		if (project == null) {
			return;
		}
//		String projectPath = project.getPath();
//		int boltPort = portUtil.getBoltPort(projectPath);
//		String uri = "bolt://localhost:" + boltPort;
		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);

		for(String tag : tags){
		    Map<String, Object> properties = new HashMap<String, Object>();
		    properties.put("names", selectedNames);
		    properties.put("tagName", tag);
    		neo4jDao.executeReadCypher(
                    CypherMethod.ADD_TAG_WITH_NODEID_WITHOUT.toString(), properties);
    		neo4jDao.executeReadCypher(
    				CypherMethod.REMOVE_TAG_WITH_NODEID.toString(), properties);
		}
		neo4jDao.close();
	}

	public String getParaSourceCode(String projectId, String paraName) {
		String content = "";
		Project project = projectRepository.findOne(projectId);
		if (project == null) {
			return content;
		}
//		String projectPath = project.getPath();
//		int boltPort = portUtil.getBoltPort(projectPath);
//		String uri = "bolt://localhost:" + boltPort;
		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("paraName", paraName);
		StatementResult sr = neo4jDao.executeReadCypher(
				CypherMethod.PARAGRAPH_CODE.toString(), properties);
		List<Record> records = sr.list();
		if (records.size() > 0) {
			content = records.get(0).get("content").asString();
		}
		neo4jDao.close();
		return content;
	}

	/**
	 * 获取所有tag
	 *
	 * @param projectId
	 * @return
	 */
	public List<String> getAllTags(String projectId) {
		List<String> tags = new ArrayList<String>();
		Project project = projectRepository.findOne(projectId);
		if (project == null) {
			return tags;
		}
//		String projectPath = project.getPath();
//		int boltPort = portUtil.getBoltPort(projectPath);
//		String uri = "bolt://localhost:" + boltPort;
		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		StatementResult sr = neo4jDao.executeReadCypher(CypherMethod.ALL_TAG
				.toString());
		List<Record> records = sr.list();
		for (Record record : records) {
			tags.add(record.get("tagName").asString());
		}
		neo4jDao.close();
		// 排序
		Collections.sort(tags);
		return tags;
	}

	/**
	 * add/remove tag之前，默认会将选中的那些记录的所有tag显示出来
	 *
	 * @param projectId
	 * @param selectedNames
	 * @param type
	 * @return
	 */
	public TagResponse getAllSelectedTags(String projectId,
			List<String> selectedNames, String type, String fromPage) {
		TagResponse tagResponse = new TagResponse();
		List<String> confirmedTags = new ArrayList<String>();
		List<String> deniedTags = new ArrayList<String>();
		Project project = projectRepository.findOne(projectId);
		if (project == null) {
			return tagResponse;
		}
		Map<String, Object> properties = new HashMap<String, Object>();
//		String projectPath = project.getPath();
//		int boltPort = portUtil.getBoltPort(projectPath);
//		String uri = "bolt://localhost:" + boltPort;
		String uri = portUtil.getBoltUrl(projectId);
		String cypherConfirmedTags = null;
		String cypherDeniedTags = null; 
		if (SystemDocType.PROGRAM.toString().equalsIgnoreCase(type)) {
			if ("codebrowser".equalsIgnoreCase(fromPage)) {
				properties.put("name", selectedNames.get(0));
				cypherConfirmedTags = CypherMethod.ALL_PROGRM_WITH_FILE_NAME_TAG
						.toString();
				cypherDeniedTags = CypherMethod.ALL_PROGRM_WITH_FILE_NAME_TAG_DENY.toString();
			} else {
				properties.put("name", selectedNames);
				cypherConfirmedTags = CypherMethod.ALL_PROGRM_TAG.toString();
				cypherDeniedTags = CypherMethod.ALL_PROGRM_TAG_DENY.toString();
			}
		} else if (SystemDocType.TABLE.toString().equalsIgnoreCase(type)) {
			properties.put("name", selectedNames);
			cypherConfirmedTags = CypherMethod.ALL_TABLE_TAG.toString();
			cypherDeniedTags = CypherMethod.ALL_TABLE_TAG_DENY.toString();
		} else if (SystemDocType.FILE.toString().equalsIgnoreCase(type)) {
			properties.put("name", selectedNames);
			cypherConfirmedTags = CypherMethod.ALL_FILE_TAG.toString();
			cypherDeniedTags = CypherMethod.ALL_FILE_TAG_DENY.toString();
		} else if (SystemDocType.COPYBOOK.toString().equalsIgnoreCase(type)) {
			if ("codebrowser".equalsIgnoreCase(fromPage)) {
				properties.put("name", selectedNames.get(0));
				cypherConfirmedTags = CypherMethod.ALL_COPYBOOK_TAG_WITH_NAME.toString();
				cypherDeniedTags = CypherMethod.ALL_COPYBOOK_TAG_WITH_NAME_DENY.toString();
			} else {
				properties.put("name", selectedNames);
				cypherConfirmedTags = CypherMethod.ALL_COPYBOOK_TAG.toString();
				cypherDeniedTags = CypherMethod.ALL_COPYBOOK_TAG_DENY.toString();
			}
		} else if (SystemDocType.JOB.toString().equalsIgnoreCase(type)) {
			if ("codebrowser".equalsIgnoreCase(fromPage)) {
				properties.put("name", selectedNames.get(0));
				cypherConfirmedTags = CypherMethod.ALL_JOB_TAG_WITH_NAME.toString();
				cypherDeniedTags = CypherMethod.ALL_JOB_TAG_WITH_NAME_DENY.toString();
			} else {
				properties.put("name", selectedNames);
				cypherConfirmedTags = CypherMethod.ALL_JCL_TAG.toString();
				cypherDeniedTags = CypherMethod.ALL_JCL_TAG_DENY.toString();
			}
		} else if (SystemDocType.PROC.toString().equalsIgnoreCase(type)) {
			if ("codebrowser".equalsIgnoreCase(fromPage)) {
				properties.put("name", selectedNames.get(0));
				cypherConfirmedTags = CypherMethod.ALL_PROC_TAG_WITH_NAME.toString();
				cypherDeniedTags = CypherMethod.ALL_PROC_TAG_WITH_NAME_DENY.toString();
			} else {
				properties.put("name", selectedNames);
				cypherConfirmedTags = CypherMethod.ALL_JCL_TAG.toString();
				cypherDeniedTags = CypherMethod.ALL_JCL_TAG_DENY.toString();
			}
		}
		// set 去重
		Set<String> tagSet = new HashSet<String>();
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		//get confirmed tag
		StatementResult sr = neo4jDao.executeReadCypher(cypherConfirmedTags, properties);

		List<Record> records = sr.list();
		for (Record record : records) {
			tagSet.add(record.get("tags").asString());
		}
		confirmedTags.addAll(tagSet);
		// 排序
		Collections.sort(confirmedTags);
		
		//get denied tag
		StatementResult rsr = neo4jDao.executeReadCypher(cypherDeniedTags, properties);

		List<Record> recordss = rsr.list();
		tagSet = new HashSet<String>();
		for (Record record : recordss) {
			tagSet.add(record.get("tags").asString());
		}
		deniedTags.addAll(tagSet);
		// 排序
		Collections.sort(deniedTags);
		neo4jDao.close();
		
		tagResponse.setConfirmedTags(confirmedTags);
		tagResponse.setDeniedTags(deniedTags);
		return tagResponse;
	}

	private List<SearchResult> queryByTags(String projectId,
			List<String> condition, String type) {
		List<SearchResult> results = new ArrayList<SearchResult>();
		Project project = projectRepository.findOne(projectId);
		if (project == null) {
			return results;
		}
//		String projectPath = project.getPath();
//		int boltPort = portUtil.getBoltPort(projectPath);
//		String uri = "bolt://localhost:" + boltPort;
		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		String cypherStr = null;
		StatementResult sr = null;
		// 若condition为空，即不选择任何tag，则搜索全部
		if (condition == null || condition.size() == 0) {
			if ("1".equals(type)) {
				cypherStr = CypherMethod.TAG_PROGRAM_NO_CONDITION.toString();
			} else if ("2".equals(type)) {
				cypherStr = CypherMethod.TAG_PARAGRAPH_NO_CONDITION.toString();
			}
			sr = neo4jDao.executeReadCypher(cypherStr);
		} else {
			// 先查找所有有tag的program和paragraph，再根据condition来过滤
			if ("1".equals(type)) {
				cypherStr = CypherMethod.TAG_PROGRAM.toString();
			} else if ("2".equals(type)) {
				cypherStr = CypherMethod.TAG_PARAGRAPH.toString();
			}
			sr = neo4jDao.executeReadCypher(cypherStr);
		}
		neo4jDao.close();
		Map<String, SearchResult> maps = new HashMap<String, SearchResult>();
		List<Record> records = sr.list();
		for (Record rec : records) {
//			String name = rec.get("name").asString();
			String tagName = rec.get("tagName").asString();
			String nodeId = rec.get("nodeId").asString();
			String name = nodeId.split("/")[1];
			if (maps.get(nodeId) == null) {
				SearchResult result = new SearchResult();
				result.setType(type);
				if ("1".equals(type)) {
					result.setProgram(name);
					result.setProgramId(nodeId);
					if (!StringUtils.isBlank(tagName)) {
						result.getProgramTags().add(tagName);
					}
				} else if ("2".equals(type)) {
					result.setParagraph(name);
					result.setParagraphId(nodeId);
					if (!StringUtils.isBlank(tagName)) {
						result.getParagraphTags().add(tagName);
					}
				}
				maps.put(nodeId, result);
			} else {
				if (!StringUtils.isBlank(tagName)) {
					SearchResult result = maps.get(nodeId);
					if ("1".equals(type)) {
						result.getProgramTags().add(tagName);
					} else if ("2".equals(type)) {
						result.getParagraphTags().add(tagName);
					}
				}
			}
		}

		if (condition == null || condition.size() == 0) {
			// 若是查询全部，则直接返回结果
			results.addAll(maps.values());
			if("1".equals(type)){
                Collections.sort(results, new Comparator<SearchResult>() {
                    @Override
                    public int compare(SearchResult o1, SearchResult o2) {
                        return o1.getProgram().compareToIgnoreCase(
                                o2.getProgram());
                    }
                });
			}
			else if ("2".equals(type)){
                Collections.sort(results, new Comparator<SearchResult>() {
                    @Override
                    public int compare(SearchResult o1, SearchResult o2) {
                        return o1.getParagraph().compareToIgnoreCase(
                                o2.getParagraph());
                    }
                });
			}
		} else {
			// 对结果进行过滤
			for (Entry<String, SearchResult> entry : maps.entrySet()) {
				SearchResult searchResult = entry.getValue();
				if ("1".equals(type)) {
					List<String> programTags = new ArrayList<String>();
					programTags.addAll(searchResult.getProgramTags());
					programTags.retainAll(condition);
					if (programTags.size() > 0) {
						results.add(searchResult);
					}
					// 通过Name排序
					Collections.sort(results, new Comparator<SearchResult>() {
						@Override
						public int compare(SearchResult o1, SearchResult o2) {
							return o1.getProgram().compareToIgnoreCase(
									o2.getProgram());
						}
					});
				} else if ("2".equals(type)) {
					List<String> paraTags = new ArrayList<String>();
					paraTags.addAll(searchResult.getParagraphTags());
					paraTags.retainAll(condition);
					if (paraTags.size() > 0) {
						results.add(searchResult);
					}
					// 通过Name排序
					Collections.sort(results, new Comparator<SearchResult>() {
						@Override
						public int compare(SearchResult o1, SearchResult o2) {
							return o1.getParagraph().compareToIgnoreCase(
									o2.getParagraph());
						}
					});
				}
			}
		}
		// 对tags排序
		for (SearchResult item : results) {
			Collections.sort(item.getProgramTags());
			Collections.sort(item.getParagraphTags());
		}
		return results;
	}

	private List<SearchResult> execSearch(String projectPath, String toolPath,
			String queryCondition, String corpusName, String sourcePath,
			String jsonPath, String condition, String type) {
		List<SearchResult> results = new ArrayList<SearchResult>();

		String jsonStr = null;
		List<String> paraCmd = buildSearchCommand(toolPath, queryCondition,
				corpusName, sourcePath, jsonPath, condition);
		int code = ProcessBuilderUtil.processBuilder(paraCmd, toolPath,
				FilePathUtil.getSearchLogPath(projectPath), true);

		if (code == 0) {
			// 执行成功，读取json结果文件，转换成对象
			try {
				jsonStr = FileUtils.readFileToString(new File(jsonPath));
			} catch (IOException e) {
				LOGGER.error(e);
				return results;
			}
			// json内容转换成对应的对象
			JSONArray jsonArray = JSONArray.fromObject(jsonStr);
			List list = JSONArray.toList(jsonArray);
			Map<String, Class> classMap = new HashMap<String, Class>();
			classMap.put("paragraph", String.class);
			classMap.put("program", String.class);
			classMap.put("match_Score", String.class);
			for (Object li : list) {
				JSONObject jo = JSONObject.fromObject(li);
				SearchResult cr = (SearchResult) JSONObject.toBean(jo,
						SearchResult.class, classMap);
				cr.setType(type);
				results.add(cr);
			}
		}
		return results;
	}

	private List<SearchResult> fillTags(List<SearchResult> results,
			String projectId, String type) {
		Map<String, SearchResult> namesMap = new HashMap<String, SearchResult>();
		String cypherStr = null;
		if ("1".equals(type)) {
			namesMap = getProgramNames(results);
			cypherStr = CypherMethod.PROGRAM_TAG.toString();
		} else if ("2".equals(type)) {
			namesMap = getParagraphNames(results);
			cypherStr = CypherMethod.PARAGRAPH_TAG.toString();
		}
//		int boltPort = portUtil.getBoltPort(projectPath);
//		String uri = "bolt://localhost:" + boltPort;
		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("names", namesMap.keySet());
		StatementResult sr = neo4jDao.executeReadCypher(cypherStr, properties);
		neo4jDao.close();
		List<Record> records = sr.list();
		for (Record rec : records) {
			String name = rec.get("name").asString();
			String nodeId = rec.get("nodeId").asString();
			String tagName = rec.get("tagName").asString();
			SearchResult searchResult = namesMap.get(name);
			if (searchResult != null) {
				if ("1".equals(type)) {
					if (StringUtils.isBlank(tagName)) {
						searchResult.setProgramId(nodeId);
					} else {
						searchResult.getProgramTags().add(tagName);
					}
				} else if ("2".equals(type)) {
					if (StringUtils.isBlank(tagName)) {
						searchResult.setParagraphId(nodeId);
					} else {
						searchResult.getParagraphTags().add(tagName);
					}
				}
			}
		}
		List<SearchResult> newResults = new ArrayList<SearchResult>();
		newResults.addAll(namesMap.values());
		// 对tags排序
		for (SearchResult item : newResults) {
			Collections.sort(item.getProgramTags());
			Collections.sort(item.getParagraphTags());
		}
		return newResults;
	}

	private Map<String, SearchResult> getParagraphNames(
			List<SearchResult> results) {
		Map<String, SearchResult> namesMap = new HashMap<String, SearchResult>();
		for (SearchResult searchResult : results) {
			if ("2".equals(searchResult.getType())) {
				// 过滤脏数据
				if (StringUtils.isBlank(searchResult.getParagraph())) {
					continue;
				}
				String paraName = searchResult.getProgram() + "."
						+ searchResult.getParagraph();
				namesMap.put(paraName, searchResult);
				// searchResult的paragraph名字更新为program.paragraph
				searchResult.setParagraph(paraName);
			}
		}
		return namesMap;
	}

	private Map<String, SearchResult> getProgramNames(List<SearchResult> results) {
		Map<String, SearchResult> namesMap = new HashMap<String, SearchResult>();
		for (SearchResult searchResult : results) {
			if ("1".equals(searchResult.getType())) {
				namesMap.put(searchResult.getProgram(), searchResult);
			}
		}
		return namesMap;
	}

	private List<String> buildSearchCommand(String toolPath,
			String queryCondition, String corpusName, String sourcePath,
			String jsonPath, String condition) {
		List<String> cmd = new ArrayList<>();
		cmd.add("java");
		cmd.add("-jar");
		cmd.add(toolPath + SEARCH_JAR);
		cmd.add(queryCondition);
		cmd.add(corpusName);
		cmd.add(sourcePath);
		cmd.add(jsonPath);
		cmd.add(condition);
		return cmd;
	}

	public List<SearchResult> getSearchResult(String projectId) {
		Project project = projectRepository.findOne(projectId);
		String jsonPath = FilePathUtil.getSearchOutputPath(project.getPath());
		jsonPath = FilePathUtil.getSearchResultPath(jsonPath);
		String jsonStr = null;
		// 执行成功，读取json结果文件，转换成对象
		try {
			jsonStr = FileUtils.readFileToString(new File(jsonPath));
			if(jsonStr.isEmpty()){
				return null;
			}
			ServerSeachJson result = JSON.parseObject(jsonStr, new TypeReference<ServerSeachJson>() {});
			List<SearchResult> results = buildSearchResult(result,project.getPath());
			return results;
		} catch (IOException e) {
			LOGGER.error(e);
			return null;
		}
	}

	private List<SearchResult> buildSearchResult(ServerSeachJson result,String rootPath) {
		List<SearchResult> searchResults = new ArrayList<>();
		if(result.getSearch_message()!=null&&result.getSearch_message().getHits().size()==0){
			return searchResults;
		}
		List<HitResultDto> hitResults = result.getSearch_message().getHits();
		String type = "1";
		switch(result.getSearch_message().getHits().get(0).getType()){
		case "program": type = "1";break;
		case "paragraph": type="2";break;
		case "tables": type = "3"; break;
		default : type="1";break;
		}

		if("1".equals(type)){
			for(HitResultDto hit: hitResults){
				SearchResult res= buildProgramResult(hit,type,rootPath);
				searchResults.add(res);
			}	
		}else if("2".equals(type)){
			for(HitResultDto hit: hitResults){
				SearchResult res= buildParagraphResult(hit,type,rootPath); 
				searchResults.add(res);
			}			
		}else{
			//TODO
		}
		return searchResults;
	}

	private SearchResult buildParagraphResult(HitResultDto hit, String type,String rootPath) {
		SearchResult res = new SearchResult();
		return res;
	}

	private SearchResult buildProgramResult(HitResultDto hit, String type,String rootPath) {
		int boltPort = portUtil.getBoltPort(rootPath);
		String uri = "bolt://localhost:" + boltPort;
		SearchResult res = new SearchResult();
		res.setMatch_Score(hit.getScore());
		String snippet = buildSnippet(hit.getHighlight());
		res.setSnippet(snippet);
		res.setType(type);
		String nodeId = hit.get_id();
		res.setProgram(FilenameUtils.getName(nodeId));
		String sourcePath = FilePathUtil.getPath(rootPath, "SOURCE");
		if(nodeId.startsWith(sourcePath)){
			nodeId = nodeId.substring(nodeId.indexOf(sourcePath)+sourcePath.length()+1);
			Neo4jDao neo4jDao = new Neo4jDao(uri);
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("nodeId", nodeId);
			StatementResult sr = neo4jDao.executeReadCypher(CypherMethod.FIND_PROGRAM.toString(), properties);
			List<Record> records = sr.list();
			if(null==records||records.size()==0){
				res.setProgramId(null);
				neo4jDao.close();
			}else{
				res.setProgramId(records.get(0).get("nodeId").asString());
				properties.put("nodeId", res.getProgramId());
				StatementResult sr1 = neo4jDao.executeReadCypher(CypherMethod.TAG_PROGRAM_WITH_NODEID.toString(), properties);
				neo4jDao.close();
				List<Record> recs = sr1.list();
				if(null!=records&&records.size()>0){
					Value v1 = recs.get(0).get("tagName");
					if(!v1.isEmpty()){
						for(int i = 0; i<v1.asList().size();i++){
						res.getProgramTags().add(v1.asList().get(i).toString());	
						}					
					}
				}
			}
			
		}else{
			return res;
		}
		return res;
	}

	private String buildSnippet(HighLightDto highLightDto) {
		if(highLightDto==null||highLightDto.getFull_text().size()==0){
			return "";
		}else{
			String snippet = String.join(" ", highLightDto.getFull_text());
			snippet = snippet.replaceAll("\\n+", " ...... ").replaceAll("\\s+", " ").trim();
			return snippet;
		}
	}
}
