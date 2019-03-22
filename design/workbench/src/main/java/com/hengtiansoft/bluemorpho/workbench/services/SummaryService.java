package com.hengtiansoft.bluemorpho.workbench.services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.hengtiansoft.bluemorpho.workbench.cache.CacheCopybook;
import com.hengtiansoft.bluemorpho.workbench.cache.CacheFile;
import com.hengtiansoft.bluemorpho.workbench.cache.CacheJcl;
import com.hengtiansoft.bluemorpho.workbench.cache.CacheTable;
import com.hengtiansoft.bluemorpho.workbench.domain.Project;
import com.hengtiansoft.bluemorpho.workbench.dto.CopyBookDetail;
import com.hengtiansoft.bluemorpho.workbench.dto.CopybookDetailItem;
import com.hengtiansoft.bluemorpho.workbench.dto.DetailExcelDto;
import com.hengtiansoft.bluemorpho.workbench.dto.FileColumnDetail;
import com.hengtiansoft.bluemorpho.workbench.dto.FileDetailItem;
import com.hengtiansoft.bluemorpho.workbench.dto.FileItem;
import com.hengtiansoft.bluemorpho.workbench.dto.JclDetailItem;
import com.hengtiansoft.bluemorpho.workbench.dto.JclStepItem;
import com.hengtiansoft.bluemorpho.workbench.dto.ParagraphDetailItem;
import com.hengtiansoft.bluemorpho.workbench.dto.ParagraphUseTableInfo;
import com.hengtiansoft.bluemorpho.workbench.dto.ProgramDetailItem;
import com.hengtiansoft.bluemorpho.workbench.dto.ProgramInfo;
import com.hengtiansoft.bluemorpho.workbench.dto.SummaryDetailItem;
import com.hengtiansoft.bluemorpho.workbench.dto.TableColumnDetail;
import com.hengtiansoft.bluemorpho.workbench.dto.TableDetailItem;
import com.hengtiansoft.bluemorpho.workbench.dto.TableItem;
import com.hengtiansoft.bluemorpho.workbench.dto.TableUsedInItem;
import com.hengtiansoft.bluemorpho.workbench.enums.CypherMethod;
import com.hengtiansoft.bluemorpho.workbench.enums.SqlOperationType;
import com.hengtiansoft.bluemorpho.workbench.enums.SystemDocType;
import com.hengtiansoft.bluemorpho.workbench.neo4j.dao.Neo4jDao;
import com.hengtiansoft.bluemorpho.workbench.repository.ProjectRepository;
import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;
import com.hengtiansoft.bluemorpho.workbench.util.FileStatusUtil;
import com.hengtiansoft.bluemorpho.workbench.util.PictureUtil;
import com.hengtiansoft.bluemorpho.workbench.util.PortUtil;
import com.hengtiansoft.bluemorpho.workbench.util.TemplateUtil;
import com.hengtiansoft.bluemorpho.workbench.util.WordUtil;
import com.hengtiansoft.bluemorpho.workbench.websocket.ProgressBarWebSocket;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date Jun 8, 2018
 */
@Service
public class SummaryService {

	private static final Logger LOGGER = Logger.getLogger(SummaryService.class);
	private static final String HTML_DOWNLOAD_TEMPLATE_FTL = "system_documentation_for_html_download.ftl";
	private static final String PDF_DOWNLOAD_TEMPLATE_FTL = "system_documentation_for_pdf_download.ftl";
	private static final String HTML_PRINT_TEMPLATE_FTL = "system_documentation_for_print.ftl";
	private static final String COBOL = "cobol/";
    private static final String SYSTEM_DOCUMENTATION = "system_documentation.ftl";
    private static final String OUTPUT = "output/";
    private static final String CONFIG = "config/config.properties";
	@Autowired
	PortUtil portUtil;
	@Autowired
	private ProjectRepository projectRepository;
    @Autowired
	private CodeBrowserService codeBrowserService;
    @Autowired
    private ProgressBarWebSocket webSocket; 
    
	public List<SummaryDetailItem> getSummaryInfo(int projectId) {
        Project project = projectRepository.findOne(String.valueOf(projectId));
        String projectPath = project.getPath();
        LOGGER.info("check code");
        String codeVersion = FileStatusUtil.checkCode(projectPath);
        LOGGER.info("check code end");
        String uri = portUtil.getBoltUrl(projectId);
        Neo4jDao neo4jDao = new Neo4jDao(uri);

        String summaryInfoPath = needCache(projectPath, SystemDocType.SUM_INFO.toString(), codeVersion);

        List<SummaryDetailItem> summary = new ArrayList<SummaryDetailItem>();
        if (summaryInfoPath != null) {
            StatementResult sr = neo4jDao.executeReadCypher(CypherMethod.SO_SUMMARY.toString());
            List<ParagraphUseTableInfo> paragraphUseTableInfos= getSqlLogic(projectId, "");
            // neo4jDao.close();
            List<Record> records = sr.list();
            int loc = 0;
            for (Record rec : records) {
                // 统计文件代码总行数
                String name = rec.get("name").asString();
                int value = rec.get("num").asInt();
                if (name.endsWith("LOC")) {
                    loc += value;
                } else {
                    summary.add(new SummaryDetailItem(name, String.valueOf(value)));
                }
            }
            summary.add(new SummaryDetailItem("SqlLogic", String.valueOf(paragraphUseTableInfos.size())));
            summary.add(new SummaryDetailItem("LOC", String.valueOf(loc)));
            cacheResult(summaryInfoPath, summary);
        } else {
            LOGGER.info("Suminfo cached");
            summary = FilePathUtil.readJson(FilePathUtil.getSystemDocPath(projectPath, codeVersion) + "/"
                    + SystemDocType.SUM_INFO.toString().toLowerCase() + ".json", SummaryDetailItem.class);
        }
        // cache SO分析结果
        LOGGER.info("Cache SO result");
//        cacheSO(projectId, summary, neo4jDao, codeVersion);
        return summary;
	}

	public List<ProgramDetailItem> getProgramDetail(int projectId) {
		String uri = portUtil.getBoltUrl(projectId);
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
			String programName = nodeId.split("/")[1];
			details.add(new ProgramDetailItem(nodeId, programName, rec.get("type").asString(), rec.get("lines")
					.asString(), handleTags(rec.get("tags")), relativePath,
					String.valueOf(rec.get("complexity").asInt()),
					handleClonePercentage(rec.get("clone").asString())));
		}
		return handleProgramDetail(details, "CICS");
	}

	public Page<ProgramDetailItem> getProgramDetail(int projectId, String type,
			int page, int size, String query) {
		PageRequest pageRequest = new PageRequest(page - 1, size);
		List<ProgramDetailItem> content = new ArrayList<ProgramDetailItem>();
		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		List<ProgramDetailItem> details = new ArrayList<ProgramDetailItem>();
		StatementResult sr = null;
		if (query.isEmpty()) {
			sr = neo4jDao.executeReadCypher(CypherMethod.PROGRAM_DETAIL
					.toString());
		} else {
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("query", query);
			sr = neo4jDao.executeReadCypher(
					CypherMethod.PROGRAM_DETAIL_WITH_QUERY.toString(),
					properties);
		}
		neo4jDao.close();
		List<Record> finds = sr.list();

		for (Record rec : finds) {
			String nodeId = rec.get("nodeId").asString();
			// relativePath用于codeBrowser，folderTree中的文件定位，以及获取文件源代码
			String relativePath = FilePathUtil.getFilePathFromNodeId(nodeId);
			if (!query.isEmpty()) {
				if(nodeId.contains(query)&&!relativePath.contains(query)) {
					continue;
				}
			}
			details.add(new ProgramDetailItem(nodeId, rec.get("name")
					.asString(), rec.get("type").asString(), rec.get("lines")
					.asString(), handleTags(rec.get("tags")), relativePath,
					String.valueOf(rec.get("complexity").asInt()),
					handleClonePercentage(rec.get("clone").asString())));
		}
		List<ProgramDetailItem> sorted = handleProgramDetail(details, type);

		int totalSize = sorted.size();
		int skip = (page - 1) * size;
		if (skip >= totalSize) {
			return new PageImpl<ProgramDetailItem>(content, pageRequest,
					totalSize);
		}
		int contentSize = 0;
		for (int i = skip; i < totalSize; i++) {
			if (contentSize < size) {
				ProgramDetailItem item = sorted.get(i);
				content.add(item);
				contentSize++;
			} else {
				break;
			}
		}
		return new PageImpl<ProgramDetailItem>(content, pageRequest, totalSize);
	}

	public List<ParagraphDetailItem> getParagraphDetail(int projectId) {
		String uri = portUtil.getBoltUrl(projectId);
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
							.asString())));
		}
		return handleParagraphDetail(details);
	}

	public Page<ParagraphDetailItem> getParagraphDetail(int projectId,
			int page, int size, String query) {
		PageRequest pageRequest = new PageRequest(page - 1, size);
		List<ParagraphDetailItem> content = new ArrayList<ParagraphDetailItem>();

		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		List<ParagraphDetailItem> details = new ArrayList<ParagraphDetailItem>();
		StatementResult sr = null;
		if (query.isEmpty()) {
			sr = neo4jDao.executeReadCypher(CypherMethod.PARAGRAPH_DETAIL
					.toString());
		} else {
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("query", query);
			sr = neo4jDao.executeReadCypher(
					CypherMethod.PARAGRAPH_DETAIL_WITH_QUERY.toString(),
					properties);
		}
		neo4jDao.close();
		List<Record> records = sr.list();
		for (Record rec : records) {
//			String relativePath = FilePathUtil.getFilePathFromNodeId(rec.get("programId").asString());
//			if (!query.isEmpty()&&!relativePath.contains(query)) {
//				continue;
//			}
			details.add(new ParagraphDetailItem(rec.get("paragraphName")
					.asString(), rec.get("lines").asString(), handleTags(rec
					.get("tags")), rec.get("paragraphId").asString(), rec.get(
					"programId").asString(), rec.get("programName").asString(),
					FilePathUtil.getFilePathFromNodeId(rec.get("programId")
							.asString()), rec.get("startLine").asString(), rec
							.get("endLine").asString(), rec.get("complexity")
							.asInt(), handleClonePercentage(rec.get("clone")
							.asString())));
		}
		List<ParagraphDetailItem> sorted = handleParagraphDetail(details);

		int totalSize = sorted.size();
		int skip = (page - 1) * size;
		if (skip >= totalSize) {
			return new PageImpl<ParagraphDetailItem>(content, pageRequest,
					totalSize);
		}
		int contentSize = 0;
		for (int i = skip; i < totalSize; i++) {
			if (contentSize < size) {
				ParagraphDetailItem item = sorted.get(i);
				content.add(item);
				contentSize++;
			} else {
				break;
			}
		}
		return new PageImpl<ParagraphDetailItem>(content, pageRequest,
				totalSize);
	}

	private String handleClonePercentage(String clone) {
		DecimalFormat df = new DecimalFormat("0.00%");
		String r = df.format(Float.valueOf(clone));
		if (r.equals("00.00%")) {
			return "0%";
		} else if (r.endsWith(".00%")) {
			return StringUtils.substringBefore(r, ".") + "%";
		}
		return r;
	}

	public List<TableItem> getTableItems(int projectId) {
		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		List<TableItem> tableItems = new ArrayList<TableItem>();
		StatementResult sr = neo4jDao.executeReadCypher(CypherMethod.TABLE_ITEM
				.toString());
		neo4jDao.close();
		List<Record> records = sr.list();
		for (Record rec : records) {
			tableItems.add(new TableItem(rec.get("nodeId").asString(), rec.get(
					"name").asString(), handleTags(rec.get("tags"))));
		}
		return handleTableItems(tableItems);
	}

	public Page<TableItem> getTableItems(int projectId, int page, int size,
			String query) {
		PageRequest pageRequest = new PageRequest(page - 1, size);
		List<TableItem> content = new ArrayList<TableItem>();

		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		List<TableItem> tableItems = new ArrayList<TableItem>();
		StatementResult sr = null;
		if (query.isEmpty()) {
			sr = neo4jDao.executeReadCypher(CypherMethod.TABLE_ITEM.toString());
		} else {
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("query", query);
			sr = neo4jDao.executeReadCypher(
					CypherMethod.TABLE_ITEM_WITH_QUERY.toString(), properties);
		}
		neo4jDao.close();
		List<Record> records = sr.list();
		for (Record rec : records) {
			tableItems.add(new TableItem(rec.get("nodeId").asString(), rec.get(
					"name").asString(), handleTags(rec.get("tags"))));
		}

		List<TableItem> sorted = handleTableItems(tableItems);

		int totalSize = sorted.size();
		int skip = (page - 1) * size;
		if (skip >= totalSize) {
			return new PageImpl<TableItem>(content, pageRequest, totalSize);
		}
		int contentSize = 0;
		for (int i = skip; i < totalSize; i++) {
			if (contentSize < size) {
				TableItem item = sorted.get(i);
				content.add(item);
				contentSize++;
			} else {
				break;
			}
		}
		return new PageImpl<TableItem>(content, pageRequest, totalSize);
	}

	public List<FileItem> getFileItems(int projectId) {
		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		List<FileItem> fileItems = new ArrayList<FileItem>();
		StatementResult sr = neo4jDao.executeReadCypher(CypherMethod.FILE_ITEM
				.toString());
		neo4jDao.close();
		List<Record> records = sr.list();
		for (Record rec : records) {
			FileItem item = new FileItem(rec.get("nodeId").asString(), rec.get(
					"name").asString(), handleTags(rec.get("tags")), rec.get(
					"openType").asString(), rec.get("definitionStart")
					.asString(), rec.get("definitionEnd").asString());
			// 得到File所在Cobol信息
			getFileProgramInfo(item);
			fileItems.add(item);
		}
		return handleFileItems(fileItems);
	}

	public Page<FileItem> getFileItems(int projectId, int page, int size,
			String query) {
		PageRequest pageRequest = new PageRequest(page - 1, size);
		List<FileItem> content = new ArrayList<FileItem>();

		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		List<FileItem> fileItems = new ArrayList<FileItem>();
		StatementResult sr = null;
		if (query.isEmpty()) {
			sr = neo4jDao.executeReadCypher(CypherMethod.FILE_ITEM.toString());
		} else {
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("query", query);
			sr = neo4jDao.executeReadCypher(
					CypherMethod.FILE_ITEM_WITH_QUERY.toString(), properties);
		}
		neo4jDao.close();
		List<Record> records = sr.list();
		for (Record rec : records) {
			FileItem item = new FileItem(rec.get("nodeId").asString(), rec.get(
					"name").asString(), handleTags(rec.get("tags")), rec.get(
					"openType").asString(), rec.get("definitionStart")
					.asString(), rec.get("definitionEnd").asString());
			// 得到File所在Cobol信息
			getFileProgramInfo(item);
			fileItems.add(item);
		}

		List<FileItem> sorted = handleFileItems(fileItems);
		int totalSize = sorted.size();
		int skip = (page - 1) * size;
		if (skip >= totalSize) {
			return new PageImpl<FileItem>(content, pageRequest, totalSize);
		}
		int contentSize = 0;
		for (int i = skip; i < totalSize; i++) {
			if (contentSize < size) {
				FileItem item = sorted.get(i);
				content.add(item);
				contentSize++;
			} else {
				break;
			}
		}
		return new PageImpl<FileItem>(content, pageRequest, totalSize);
	}

	public List<TableDetailItem> getTableDetailItems(Neo4jDao neo4jDao,
			String nodeId) {
//	    LOGGER.info("Neo4j start");
//		String uri = portUtil.getBoltUrl(projectId);
//		Neo4jDao neo4jDao = new Neo4jDao(uri);
//		LOGGER.info("Neo4j end");
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("nodeId", nodeId);
//		LOGGER.info("nodeId:"+nodeId);
		List<TableDetailItem> tableDetailItems = new ArrayList<TableDetailItem>();
		LOGGER.info("query start");
		StatementResult sr = neo4jDao.executeReadCypher(
				CypherMethod.TABLE_DETAIL.toString(), properties);
//		neo4jDao.close();
		LOGGER.info("query end");
		LOGGER.info("got table_detail from Neo4j");
		List<Record> records = sr.list();
		for (Record rec : records) {
			tableDetailItems.add(new TableDetailItem(rec.get("nodeId")
					.asString(), rec.get("name").asString(), rec.get("type")
					.asString()));
		}
		return tableDetailItems;
	}

	public Page<TableDetailItem> getTableDetailItems(int projectId,
			String nodeId, int page, int size) {
		PageRequest pageRequest = new PageRequest(page - 1, size);
		List<TableDetailItem> content = new ArrayList<TableDetailItem>();

		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("nodeId", nodeId);

		List<TableDetailItem> finds = new ArrayList<TableDetailItem>();
		StatementResult sr = neo4jDao.executeReadCypher(
				CypherMethod.TABLE_DETAIL.toString(), properties);
		neo4jDao.close();
		List<Record> records = sr.list();
		for (Record rec : records) {
			finds.add(new TableDetailItem(rec.get("nodeId").asString(), rec
					.get("name").asString(), rec.get("type").asString()));
		}

		int totalSize = finds.size();
		int skip = (page - 1) * size;
		if (skip >= totalSize) {
			return new PageImpl<TableDetailItem>(content, pageRequest,
					totalSize);
		}
		int contentSize = 0;
		for (int i = skip; i < totalSize; i++) {
			if (contentSize < size) {
				TableDetailItem item = finds.get(i);
				content.add(item);
				contentSize++;
			} else {
				break;
			}
		}
		return new PageImpl<TableDetailItem>(content, pageRequest, totalSize);
	}

	public List<TableDetailItem> getAllTableDetailItems(int projectId,
			String nodeId) {
		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("nodeId", nodeId);

		List<TableDetailItem> finds = new ArrayList<TableDetailItem>();
		StatementResult sr = neo4jDao.executeReadCypher(
				CypherMethod.TABLE_DETAIL.toString(), properties);
		neo4jDao.close();
		List<Record> records = sr.list();
		for (Record rec : records) {
			finds.add(new TableDetailItem(rec.get("nodeId").asString(), rec
					.get("name").asString(), rec.get("type").asString()));
		}
		return finds;
	}
	   
    private List<FileDetailItem> getAllFileDetailItems(int projectId,
            String nodeId) {
        // TODO Auto-generated method stub
        String uri = portUtil.getBoltUrl(projectId);
        Neo4jDao neo4jDao = new Neo4jDao(uri);
        List<FileDetailItem> fileDetailItems = new ArrayList<FileDetailItem>();
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("nodeId", nodeId);
        StatementResult sr1 = neo4jDao.executeReadCypher(
                CypherMethod.FILE_SUPERID.toString(), properties);
        List<Record> records1 = sr1.list();
        String superId = null;
        if (records1.size() > 0) {
            superId = records1.get(0).get("superId").asString();
        }
        if (superId != null) {
            properties = new HashMap<String, Object>();
            properties.put("superId", superId);
            StatementResult sr2 = neo4jDao.executeReadCypher(
                    CypherMethod.FILE_DETAIL.toString(), properties);
//          neo4jDao.close();
            List<Record> records2 = sr2.list();
            for (Record rec : records2) {
                fileDetailItems.add(new FileDetailItem(rec.get("nodeId")
                        .asString(), rec.get("name").asString(), rec
                        .get("type").asString()));
            }
        }
        neo4jDao.close();
        return fileDetailItems;
    }

	
	public List<FileDetailItem> getFileDetailItems(Neo4jDao neo4jDao, String nodeId) {
//		String uri = portUtil.getBoltUrl(projectId);
//		Neo4jDao neo4jDao = new Neo4jDao(uri);
		List<FileDetailItem> fileDetailItems = new ArrayList<FileDetailItem>();
		// 首先获取file对应的record的nodeId，对应的是其child的superId
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("nodeId", nodeId);
		StatementResult sr1 = neo4jDao.executeReadCypher(
				CypherMethod.FILE_SUPERID.toString(), properties);
		List<Record> records1 = sr1.list();
		String superId = null;
		if (records1.size() > 0) {
			superId = records1.get(0).get("superId").asString();
		}
		if (superId != null) {
			properties = new HashMap<String, Object>();
			properties.put("superId", superId);
			StatementResult sr2 = neo4jDao.executeReadCypher(
					CypherMethod.FILE_DETAIL.toString(), properties);
//			neo4jDao.close();
			List<Record> records2 = sr2.list();
			for (Record rec : records2) {
				fileDetailItems.add(new FileDetailItem(rec.get("nodeId")
						.asString(), rec.get("name").asString(), rec
						.get("type").asString()));
			}
		}
		return fileDetailItems;
	}

	public Page<FileDetailItem> getFileDetailItems(int projectId,
			String nodeId, int page, int size) {
		PageRequest pageRequest = new PageRequest(page - 1, size);
		List<FileDetailItem> content = new ArrayList<FileDetailItem>();

		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		List<FileDetailItem> finds = new ArrayList<FileDetailItem>();
		// 首先获取file对应的record的nodeId，对应的是其child的superId
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("nodeId", nodeId);
		StatementResult sr1 = neo4jDao.executeReadCypher(
				CypherMethod.FILE_SUPERID.toString(), properties);
		List<Record> records1 = sr1.list();
		String superId = null;
		if (records1.size() > 0) {
			superId = records1.get(0).get("superId").asString();
		}
		if (superId != null) {
			properties = new HashMap<String, Object>();
			properties.put("superId", superId);
			StatementResult sr2 = neo4jDao.executeReadCypher(
					CypherMethod.FILE_DETAIL.toString(), properties);
			neo4jDao.close();
			List<Record> records2 = sr2.list();
			for (Record rec : records2) {
				finds.add(new FileDetailItem(rec.get("nodeId").asString(), rec
						.get("name").asString(), rec.get("type").asString(),
						rec.get("cpyName").asString()));
			}
		}

		int totalSize = finds.size();
		int skip = (page - 1) * size;
		if (skip >= totalSize) {
			return new PageImpl<FileDetailItem>(content, pageRequest, totalSize);
		}
		int contentSize = 0;
		for (int i = skip; i < totalSize; i++) {
			if (contentSize < size) {
				FileDetailItem item = finds.get(i);
				content.add(item);
				contentSize++;
			} else {
				break;
			}
		}
		return new PageImpl<FileDetailItem>(content, pageRequest, totalSize);
	}

	public List<CopybookDetailItem> getCopybookDetail(int projectId) {
		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		List<CopybookDetailItem> details = new ArrayList<CopybookDetailItem>();
		StatementResult sr = neo4jDao
				.executeReadCypher(CypherMethod.COPYBOOK_DETAIL.toString());
		neo4jDao.close();
		List<Record> records = sr.list();
		for (Record rec : records) {
			details.add(new CopybookDetailItem(rec.get("nodeId").asString(),
					rec.get("cpyName").asString(), rec.get("type").asString(),
					FilePathUtil.getFilePathFromNodeId(rec.get("nodeId")
							.asString()), handleTags(rec.get("tags"))));
		}
		return handleCopybookItems(details);
	}

	public Page<CopybookDetailItem> getCopybookDetail(int projectId, int page,
			int size, String query) {
		PageRequest pageRequest = new PageRequest(page - 1, size);
		List<CopybookDetailItem> content = new ArrayList<CopybookDetailItem>();

		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		List<CopybookDetailItem> finds = new ArrayList<CopybookDetailItem>();
		StatementResult sr = null;
		if (query.isEmpty()) {
			sr = neo4jDao.executeReadCypher(CypherMethod.COPYBOOK_DETAIL
					.toString());
		} else {
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("query", query);
			sr = neo4jDao.executeReadCypher(
					CypherMethod.COPYBOOK_DETAIL_WITH_QUERY.toString(),
					properties);
		}
		neo4jDao.close();
		List<Record> records = sr.list();
		for (Record rec : records) {
			finds.add(new CopybookDetailItem(rec.get("nodeId").asString(), rec
					.get("cpyName").asString(), rec.get("type").asString(),
					FilePathUtil.getFilePathFromNodeId(rec.get("nodeId")
							.asString()), handleTags(rec.get("tags"))));
		}

		List<CopybookDetailItem> sorted = handleCopybookItems(finds);
		int totalSize = sorted.size();
		int skip = (page - 1) * size;
		if (skip >= totalSize) {
			return new PageImpl<CopybookDetailItem>(content, pageRequest,
					totalSize);
		}
		int contentSize = 0;
		for (int i = skip; i < totalSize; i++) {
			if (contentSize < size) {
				CopybookDetailItem item = sorted.get(i);
				content.add(item);
				contentSize++;
			} else {
				break;
			}
		}
		return new PageImpl<CopybookDetailItem>(content, pageRequest, totalSize);
	}

	public List<JclDetailItem> getJclDetail(int projectId) {
		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		List<JclDetailItem> details = new ArrayList<JclDetailItem>();
		StatementResult sr = neo4jDao.executeReadCypher(CypherMethod.JCL_DETAIL
				.toString());
		neo4jDao.close();
		List<Record> records = sr.list();
		for (Record rec : records) {
			details.add(new JclDetailItem(rec.get("nodeId").asString(), rec
					.get("name").asString(), FilePathUtil
					.getFilePathFromNodeId(rec.get("nodeId").asString()),
					handleTags(rec.get("tags")), rec.get("type").asString()));
		}
		return handleJclDetailItems(details);
	}

	public PageImpl<JclDetailItem> getJclDetail(int projectId, int page,
			int size, String query) {
		PageRequest pageRequest = new PageRequest(page - 1, size);
		List<JclDetailItem> content = new ArrayList<JclDetailItem>();
		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		List<JclDetailItem> details = new ArrayList<JclDetailItem>();
		StatementResult sr = null;
		if (query.isEmpty()) {
			sr = neo4jDao.executeReadCypher(CypherMethod.JCL_DETAIL.toString());
		} else {
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("query", query);
			sr = neo4jDao.executeReadCypher(
					CypherMethod.JCL_DETAIL_WITH_QUERY.toString(), properties);
		}
		neo4jDao.close();
		List<Record> records = sr.list();
		for (Record rec : records) {
			details.add(new JclDetailItem(rec.get("nodeId").asString(), rec
					.get("name").asString(), FilePathUtil
					.getFilePathFromNodeId(rec.get("nodeId").asString()),
					handleTags(rec.get("tags")), rec.get("type").asString()));
		}
		details = handleJclDetailItems(details);
		int totalSize = details.size();
		int skip = (page - 1) * size;
		if (skip >= totalSize) {
			return new PageImpl<JclDetailItem>(content, pageRequest, totalSize);
		}
		int contentSize = 0;
		for (int i = skip; i < totalSize; i++) {
			if (contentSize < size) {
				JclDetailItem item = details.get(i);
				content.add(item);
				contentSize++;
			} else {
				break;
			}
		}
		return new PageImpl<JclDetailItem>(content, pageRequest, totalSize);
	}

	public List<JclStepItem> getJclSteps(Neo4jDao neo4jDao, String nodeId) {
//		String uri = portUtil.getBoltUrl(projectId);
//		Neo4jDao neo4jDao = new Neo4jDao(uri);
		List<JclStepItem> details = new ArrayList<JclStepItem>();
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("nodeId", nodeId);
		LOGGER.info("query start");
		StatementResult sr = neo4jDao.executeReadCypher(
				CypherMethod.JCL_STEP.toString(), properties);
        LOGGER.info("query end");
//		neo4jDao.close();
		List<Record> records = sr.list();
		for (Record rec : records) {
			details.add(new JclStepItem(nodeId, rec.get("stepName").asString(),
					rec.get("pgmName").asString(), rec.get("procName")
							.asString()));
		}
		// 按照setpName排序
		LOGGER.info("sort jcl start");
		Collections.sort(details, new Comparator<JclStepItem>() {
			@Override
			public int compare(JclStepItem o1, JclStepItem o2) {
				return o1.getStepName().compareToIgnoreCase(o2.getStepName());
			}
		});
		LOGGER.info("sort jcl end");
		
	    LOGGER.info("get jcl step detail");
		for (JclStepItem item : details) {
		    LOGGER.info("get single jcl step detail");
			getJclStepDetail(neo4jDao, item);
			LOGGER.info("get single end");
		}
		LOGGER.info("get end");
		return details;
	}

	public PageImpl<JclStepItem> getJclSteps(int projectId, String nodeId,
			int page, int size) {
		PageRequest pageRequest = new PageRequest(page - 1, size);
		List<JclStepItem> content = new ArrayList<JclStepItem>();
		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		List<JclStepItem> details = new ArrayList<JclStepItem>();
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("nodeId", nodeId);
		StatementResult sr = neo4jDao.executeReadCypher(
				CypherMethod.JCL_STEP.toString(), properties);
//		neo4jDao.close();
		List<Record> records = sr.list();
		for (Record rec : records) {
			details.add(new JclStepItem(nodeId, rec.get("stepName").asString(),
					rec.get("pgmName").asString(), rec.get("procName")
							.asString()));
		}
		// 按照setpName排序
		Collections.sort(details, new Comparator<JclStepItem>() {
			@Override
			public int compare(JclStepItem o1, JclStepItem o2) {
				return o1.getStepName().compareToIgnoreCase(o2.getStepName());
			}
		});
		for (JclStepItem item : details) {
			getJclStepDetail(neo4jDao, item);
		}
		neo4jDao.close();
		int totalSize = details.size();
		int skip = (page - 1) * size;
		if (skip >= totalSize) {
			return new PageImpl<JclStepItem>(content, pageRequest, totalSize);
		}
		int contentSize = 0;
		for (int i = skip; i < totalSize; i++) {
			if (contentSize < size) {
				JclStepItem item = details.get(i);
				content.add(item);
				contentSize++;
			} else {
				break;
			}
		}
		return new PageImpl<JclStepItem>(content, pageRequest, totalSize);
	}

	private JclStepItem getJclStepDetail(Neo4jDao neo4jDao, JclStepItem item) {
		String cypher = null;
		boolean isProc = true;
		Map<String, Object> properties = new HashMap<String, Object>();
		if (StringUtils.isBlank(item.getPgmName())
				&& !StringUtils.isBlank(item.getProcName())) {
			cypher = CypherMethod.JCL_STEP_PROC_DETAIL.toString();
			properties.put("name", item.getProcName());
			isProc = true;
		} else if (StringUtils.isBlank(item.getProcName())
				&& !StringUtils.isBlank(item.getPgmName())) {
			cypher = CypherMethod.JCL_STEP_PGM_DETAIL.toString();
			properties.put("name", item.getPgmName());
			isProc = false;
		}
		if (cypher == null) {
			return item;
		}

//		String uri = portUtil.getBoltUrl(projectId);
//		Neo4jDao neo4jDao = new Neo4jDao(uri);
		StatementResult sr = neo4jDao.executeReadCypher(cypher, properties);
//		neo4jDao.close();
		List<Record> records = sr.list();
		for (Record rec : records) {
			if (isProc) {
				item.setProcId(rec.get("nodeId").asString());
				item.setProcLocation(FilePathUtil.getFilePathFromNodeId(item
						.getProcId()));
			} else {
				item.setPgmId(rec.get("nodeId").asString());
				item.setPgmLocation(FilePathUtil.getFilePathFromNodeId(item
						.getPgmId()));
			}
			break;
		}
		return item;
	}

	public void handleSummary(List<SummaryDetailItem> details) {
		int count = 0;
		List<String> names = Arrays.asList("cobolLOC", "cpyLOC", "jclLOC");
		for (SummaryDetailItem item : details) {
			if (names.contains(item.getDetailName())) {
				count += (Integer.valueOf(item.getDetailData()));
			}
		}

		SummaryDetailItem totalLOC = new SummaryDetailItem("LOC",
				String.valueOf(count));
		details.add(totalLOC);
	}

	public List<ParagraphUseTableInfo> getSqlLogic(int projectId, String query) {
        List<ParagraphUseTableInfo> useTables = new ArrayList<ParagraphUseTableInfo>();
        String uri = portUtil.getBoltUrl(projectId);
        Neo4jDao neo4jDao = new Neo4jDao(uri);
        StatementResult sr1 = null;
        StatementResult sr2 = null;
        if (query.isEmpty()) {
            sr1 = neo4jDao.executeReadCypher(CypherMethod.ALL_PARAGRAPH_USE_TABLE_DETAIL1.toString());
        } else {
            List<String> querycommand = handleCommandToOperation(query);
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put("query", query);
            properties.put("querycommand", querycommand);
            sr1 = neo4jDao.executeReadCypher(CypherMethod.ALL_PARAGRAPH_USE_TABLE_DETAIL_WITH_QUERY1.toString(),
                    properties);
        }
        List<Record> records1 = sr1.list();
        
        Set<String> blockIdSet = new HashSet<String>();
        List<Record> records = new ArrayList<>();
        for (Record rec : records1) {
        	String nodeId = rec.get("programId").asString();
			String relativePath = FilePathUtil.getFilePathFromNodeId(nodeId);
			if (!query.isEmpty()) {
				if(nodeId.contains(query)&&!relativePath.contains(query)) {
					continue;					
				}
			}
            blockIdSet.add(rec.get("blockId").asString());
            records.add(rec);
        }
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("blockIds", blockIdSet);
        if (query.isEmpty()) {
            sr2 = neo4jDao.executeReadCypher(CypherMethod.ALL_PARAGRAPH_USE_TABLE_DETAIL2.toString(), properties);
        } else {
            List<String> querycommand = handleCommandToOperation(query);
            properties.put("query", query);
            properties.put("querycommand", querycommand);
            sr2 = neo4jDao.executeReadCypher(CypherMethod.ALL_PARAGRAPH_USE_TABLE_DETAIL_WITH_QUERY2.toString(),
                    properties);
        }
        List<Record> records2 = sr2.list();
        
//        List<Record> records = records1;
        for (Record rec : records2) {
        	String nodeId = rec.get("programId").asString();
			String relativePath = FilePathUtil.getFilePathFromNodeId(nodeId);
			if (!query.isEmpty()) {
				if(nodeId.contains(query)&&!relativePath.contains(query)) {
					continue;					
				}
			}
            records.add(rec);
        }
        
//        records.addAll(records2);
        for (Record rec : records) {
            String programId = rec.get("programId").asString();
            // programId = programId.split("/")[2];
            String pattern1 = "(\\w+/\\w+\\.cbl/\\w+)\\.(.*)";
            String pattern2 = "(\\w+/\\w+/\\w+)\\.(.*)";
            Pattern r1 = Pattern.compile(pattern1);
            Matcher m1 = r1.matcher(programId);
            Pattern r2 = Pattern.compile(pattern2);
            Matcher m2 = r2.matcher(programId);
            if (programId.contains("#")) {
                programId = programId.substring(0, programId.indexOf("#"));
            }
            // if (programId.contains(".") && programId.matches("^(\\w+/\\w+\\.cbl/\\w+)\\.(.*)")) {
            // programId = programId.substring(0, programId.indexOf("."));
            // }
            if (m1.find()) {
                programId = m1.group(1);
            }
            if (m2.find()) {
                programId = m2.group(1);
            }
            String programLocation = FilePathUtil.getFilePathFromNodeId(programId);
            String programName = StringUtils.substringAfterLast(programLocation, "/");
            String blockId = rec.get("blockId").asString();
            ParagraphUseTableInfo useTableInfo = new ParagraphUseTableInfo(rec.get("tableId").asString(), rec.get(
                    "paragraphName").asString(), rec.get("paragraphId").asString(), programId, programName,
                    programLocation, rec.get("paraStartLine").asString(), rec.get("paraEndLine").asString(), rec.get(
                            "startLine").asString(), rec.get("endLine").asString(), handleOperation(rec
                            .get("operation").asString()), rec.get("tableName").asString(), rec.get("copybook")
                            .asString(), blockId);
            handleSqlLogicInCpy(useTableInfo, neo4jDao, blockId);
            useTables.add(useTableInfo);
        }
        neo4jDao.close();
        return useTables;
    }

	private void handleSqlLogicInCpy(ParagraphUseTableInfo useTableInfo,
			Neo4jDao neo4jDao, String blockId) {
		if (!StringUtils.isBlank(useTableInfo.getCopybook())) {
			// 该sql语句在copybook中，则定位至copy语句处
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("nodeId", useTableInfo.getCopybook());
			StatementResult sr = neo4jDao.executeReadCypher(
					CypherMethod.GET_COPY_COMMAND.toString(), properties);
			List<Record> records = sr.list();
			if (records.size() == 1) {
				Record record = records.get(0);
				Value cpyCommand = record.get("copyCommands");
				if (!cpyCommand.isEmpty()) {
					List<Object> copyCommands = cpyCommand.asList();
					List<Object> copyLines = record.get("copyLines").asList();
					// blockId中提取startLine;
					int blockStartLine = Integer.valueOf(StringUtils
							.substringAfterLast(StringUtils
									.substringBeforeLast(blockId, "#"), "#"));
					int minIndex = 0;
					int minDistance = Integer.MAX_VALUE;
					int distance = 0;
					for (int i = 0; i < copyCommands.size(); i++) {
						String copyCommand = copyCommands.get(i).toString();
						int copyCommandLine = Integer.valueOf(StringUtils
								.substringAfterLast(copyCommand, "#"));
						if (copyCommandLine == blockStartLine) {
							minIndex = i;
							break;
						} else if (copyCommandLine < blockStartLine) {
							distance = blockStartLine - copyCommandLine;
							if (distance < minDistance) {
								minDistance = distance;
								minIndex = i;
							}
						}

					}
					String copyLine = copyLines.get(minIndex).toString();
					useTableInfo.setStartLine(copyLine);
					useTableInfo.setEndLine(copyLine);
				}
			}
		}
	}

	public Page<ParagraphUseTableInfo> getSqlLogic(int projectId, int page,
			int size, String query) {
		PageRequest pageRequest = new PageRequest(page - 1, size);

		List<ParagraphUseTableInfo> content = new ArrayList<ParagraphUseTableInfo>();
		List<ParagraphUseTableInfo> useTables = getSqlLogic(projectId, query);

		int totalSize = useTables.size();
		int skip = (page - 1) * size;
		if (skip >= totalSize) {
			return new PageImpl<ParagraphUseTableInfo>(content, pageRequest,
					totalSize);
		}
		int contentSize = 0;
		for (int i = skip; i < totalSize; i++) {
			if (contentSize < size) {
				ParagraphUseTableInfo item = useTables.get(i);
				content.add(item);
				contentSize++;
			} else {
				break;
			}
		}
		return new PageImpl<ParagraphUseTableInfo>(content, pageRequest,
				totalSize);
	}

	public List<ProgramDetailItem> getProgramWithCpyId(Neo4jDao neo4jDao,
			String cpyId) {
//		String uri = portUtil.getBoltUrl(projectId);
//		Neo4jDao neo4jDao = new Neo4jDao(uri);
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("nodeId", cpyId);
//		LOGGER.info("query start");
		List<ProgramDetailItem> details = new ArrayList<ProgramDetailItem>();
		StatementResult sr = neo4jDao.executeReadCypher(
				CypherMethod.USE_COPYBOOK_DETAIL.toString(), properties);
//		LOGGER.info("query end");
//		neo4jDao.close();
		List<Record> records = sr.list();
		for (Record rec : records) {
			details.add(new ProgramDetailItem(rec.get("nodeId").asString(), rec
					.get("name").asString(), StringUtils.substringBeforeLast(
					rec.get("nodeId").asString(), "/"), rec.get("line")
					.asString()));
		}
		return details;
	}

	public Page<ProgramDetailItem> getProgramWithCpyId(int projectId,
			String cpyId, int page, int size) {
		PageRequest pageRequest = new PageRequest(page - 1, size);
		List<ProgramDetailItem> content = new ArrayList<ProgramDetailItem>();

		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("nodeId", cpyId);
		List<ProgramDetailItem> finds = new ArrayList<ProgramDetailItem>();
		StatementResult sr = neo4jDao.executeReadCypher(
				CypherMethod.USE_COPYBOOK_DETAIL.toString(), properties);
		neo4jDao.close();
		List<Record> records = sr.list();
		for (Record rec : records) {
			finds.add(new ProgramDetailItem(rec.get("nodeId").asString(), rec
					.get("name").asString(), StringUtils.substringBeforeLast(
					rec.get("nodeId").asString(), "/"), rec.get("line")
					.asString()));
		}

		int totalSize = finds.size();
		int skip = (page - 1) * size;
		if (skip >= totalSize) {
			return new PageImpl<ProgramDetailItem>(content, pageRequest,
					totalSize);
		}
		int contentSize = 0;
		for (int i = skip; i < totalSize; i++) {
			if (contentSize < size) {
				ProgramDetailItem item = finds.get(i);
				content.add(item);
				contentSize++;
			} else {
				break;
			}
		}
		return new PageImpl<ProgramDetailItem>(content, pageRequest, totalSize);
	}

	public List<ProgramDetailItem> getAllProgramWithCpyId(int projectId,
			String cpyId) {
		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("nodeId", cpyId);
		List<ProgramDetailItem> finds = new ArrayList<ProgramDetailItem>();
		StatementResult sr = neo4jDao.executeReadCypher(
				CypherMethod.USE_COPYBOOK_DETAIL.toString(), properties);
		neo4jDao.close();
		List<Record> records = sr.list();
		for (Record rec : records) {
		    String nodeId = rec.get("nodeId").asString();
		    String name = nodeId.split("/")[1];
			finds.add(new ProgramDetailItem(rec.get("nodeId").asString(), name, StringUtils.substringBeforeLast(
					rec.get("nodeId").asString(), "/"), rec.get("line")
					.asString()));
		}
		return finds;
	}

	public Page<ParagraphUseTableInfo> getParagraphWithTable(int projectId,
			String tableId, int page, int size) {
		PageRequest pageRequest = new PageRequest(page - 1, size);
		List<ParagraphUseTableInfo> content = new ArrayList<ParagraphUseTableInfo>();
		List<ParagraphUseTableInfo> finds = new ArrayList<ParagraphUseTableInfo>();
		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("name", tableId);
		StatementResult sr1 = neo4jDao.executeReadCypher(
				CypherMethod.PARAGRAPH_USE_TABLE_DETAIL.toString(), properties);
		
		List<Record> records1 = sr1.list();
		for (Record rec : records1) {
			String programId = rec.get("programId").asString();
			String programLocation = FilePathUtil
					.getFilePathFromNodeId(programId);
			String programName = StringUtils.substringAfterLast(
					programLocation, "/");
			ParagraphUseTableInfo useTableInfo = new ParagraphUseTableInfo(
					tableId, rec.get("paragraphName").asString(), rec.get(
							"paragraphId").asString(), programId, programName,
					rec.get("operation").asString(), rec.get("blockId")
							.asString());
			finds.add(useTableInfo);
		}
		
		// declare cursor in program data division 
		StatementResult sr2 = neo4jDao.executeReadCypher(
				CypherMethod.DELCARE_CURSOR_IN_PROGRAM.toString(), properties);
		neo4jDao.close();
		List<Record> records2 = sr2.list();
		for (Record rec : records2) {
			String blockId = rec.get("blockId").asString();
			String programName = FilePathUtil
					.getPgmNameFromBlockId(blockId);
			ParagraphUseTableInfo useTableInfo = new ParagraphUseTableInfo(
					tableId, "", "", "", programName, rec.get("operation").asString(), rec.get("blockId").asString());
			finds.add(useTableInfo);
		}
		
		Collections.sort(finds, new Comparator<ParagraphUseTableInfo>() {
			@Override
			public int compare(ParagraphUseTableInfo p1, ParagraphUseTableInfo p2) {
				int result1 = p1.getProgramId().compareToIgnoreCase(p2.getProgramId());
				if (result1 == 0) {
					int result2 = p1.getParagraphId().compareToIgnoreCase(p2.getParagraphId());
					if (result2 == 0) {
						int result3 = p1.getOperation().compareToIgnoreCase(p2.getOperation());
						return result3;
					} else {
						return result2;
					}
				} else {
					return result1;
				}
			}
		});

		int totalSize = finds.size();
		int skip = (page - 1) * size;
		if (skip >= totalSize) {
			return new PageImpl<ParagraphUseTableInfo>(content, pageRequest,
					totalSize);
		}
		int contentSize = 0;
		for (int i = skip; i < totalSize; i++) {
			if (contentSize < size) {
				ParagraphUseTableInfo item = finds.get(i);
				content.add(item);
				contentSize++;
			} else {
				break;
			}
		}
		return new PageImpl<ParagraphUseTableInfo>(content, pageRequest,
				totalSize);
	}

	public List<ParagraphUseTableInfo> getAllParagraphWithTable(int projectId,
			String tableId) {
		List<ParagraphUseTableInfo> finds = new ArrayList<ParagraphUseTableInfo>();
		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("name", tableId);
		StatementResult sr1 = neo4jDao.executeReadCypher(
				CypherMethod.PARAGRAPH_USE_TABLE_DETAIL.toString(), properties);
		neo4jDao.close();
		List<Record> records1 = sr1.list();
		for (Record rec : records1) {
			String programId = rec.get("programId").asString();
			String programLocation = FilePathUtil
					.getFilePathFromNodeId(programId);
			String programName = StringUtils.substringAfterLast(
					programLocation, "/");
			ParagraphUseTableInfo useTableInfo = new ParagraphUseTableInfo(
					tableId, rec.get("paragraphName").asString(), rec.get(
							"paragraphId").asString(), programId, programName,
					rec.get("operation").asString(), rec.get("blockId")
							.asString());
			finds.add(useTableInfo);
		}
		return finds;
	}

	public Page<ProgramDetailItem> getProgramWithTable(int projectId,
			String tableId, int page, int size) {
		PageRequest pageRequest = new PageRequest(page - 1, size);
		List<ProgramDetailItem> content = new ArrayList<ProgramDetailItem>();

		String uri = portUtil.getBoltUrl(projectId);
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("name", tableId);
		List<ProgramDetailItem> finds = new ArrayList<ProgramDetailItem>();
		StatementResult sr = neo4jDao.executeReadCypher(
				CypherMethod.SQL_USE_TABLE_DETAIL.toString(), properties);
		List<Record> records = sr.list();
		Set<String> pgmIdSet = new HashSet<String>();
		Set<String> paraNameSet = new HashSet<String>();

		for (Record rec : records) {
			String blockId = rec.get("blockId").asString();
			String paraName = rec.get("paraName").asString();
			if (StringUtils.isBlank(paraName)) {
				String pgmNodeId = StringUtils.substringBefore(blockId, "#");
				if (!pgmIdSet.contains(pgmNodeId)) {
					String location = FilePathUtil
							.getFilePathFromNodeId(pgmNodeId);
					String pgmName = StringUtils.substringAfterLast(location,
							"/");
					finds.add(new ProgramDetailItem(pgmNodeId, pgmName,
							location));
					pgmIdSet.add(pgmNodeId);
				}
			} else {
				paraNameSet.add(paraName);
			}
		}
		Map<String, Object> properties1 = new HashMap<String, Object>();
		properties1.put("names", paraNameSet);
		StatementResult sr1 = neo4jDao.executeReadCypher(
				CypherMethod.PROGRAM_USE_TABLE_DETAIL.toString(), properties1);
		neo4jDao.close();
		List<Record> records1 = sr1.list();
		for (Record rec : records1) {
			String nodeId = rec.get("nodeId").asString();
			if (!pgmIdSet.contains(nodeId)) {
				String location = FilePathUtil.getFilePathFromNodeId(nodeId);
				String pgmName = StringUtils.substringAfterLast(location, "/");
				finds.add(new ProgramDetailItem(nodeId, pgmName, location));
				pgmIdSet.add(nodeId);
			}
		}

		int totalSize = finds.size();
		int skip = (page - 1) * size;
		if (skip >= totalSize) {
			return new PageImpl<ProgramDetailItem>(content, pageRequest,
					totalSize);
		}
		int contentSize = 0;
		for (int i = skip; i < totalSize; i++) {
			if (contentSize < size) {
				ProgramDetailItem item = finds.get(i);
				content.add(item);
				contentSize++;
			} else {
				break;
			}
		}
		return new PageImpl<ProgramDetailItem>(content, pageRequest, totalSize);
	}

	/**
	 * 从Table Tab中点击某条记录，跳转至Sql Logic Tab中，并选中对应记录
	 *
	 * @param useTableInfo
	 * @return
	 */
	public int findUseTableInfo(ParagraphUseTableInfo useTableInfo) {
		String compareStr = useTableInfo.getBlockId()
				+ useTableInfo.getProgramId() + useTableInfo.getParagraphId()
				+ useTableInfo.getNodeId() + useTableInfo.getOperation();
		// 为了请求参数统一，并且当前操作不需要tableName，于是参数中将setTableName(projectId)。
		String uri = portUtil.getBoltUrl(useTableInfo.getTableName());
		Neo4jDao neo4jDao = new Neo4jDao(uri);
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("name", compareStr);
		StatementResult sr = neo4jDao.executeReadCypher(
				CypherMethod.FIND_USE_TABLE.toString(), properties);
		List<Record> records = sr.list();
		int index = records.size();
		neo4jDao.close();
		return index;
	}

	/**
	 * 点击summary中某类型文件，得到source code
	 *
	 * @param path
	 * @return
	 */
	public String getSourceCode(String path) {
		return FilePathUtil.readFile(path);
	}

	public DetailExcelDto getExcelDownloadData(int projectId) {
		List<SummaryDetailItem> summaryDetails = getSummaryInfo(projectId);
		List<ProgramDetailItem> programDetails = getProgramDetail(projectId);
		List<ParagraphDetailItem> paraDetails = getParagraphDetail(projectId);
		List<TableItem> tableItems = getTableItems(projectId);
		List<FileItem> fileItems = getFileItems(projectId);
		List<CopybookDetailItem> copybookDetails = getCopybookDetail(projectId);
		List<JclDetailItem> jclDetailItems = getJclDetail(projectId);
		List<ParagraphUseTableInfo> sqlLogicItems = getSqlLogic(projectId, "");
		DetailExcelDto detailExcelDto = new DetailExcelDto(summaryDetails,
				programDetails, paraDetails, tableItems, fileItems,
				copybookDetails, jclDetailItems, sqlLogicItems);
		return detailExcelDto;
	}

	private List<ProgramDetailItem> handleProgramDetail(
			List<ProgramDetailItem> programDetails, String type) {
		List<ProgramDetailItem> result = new ArrayList<ProgramDetailItem>();
		Map<String, ProgramDetailItem> map = new HashMap<String, ProgramDetailItem>();
		for (ProgramDetailItem detail : programDetails) {
			if (map.containsKey(detail.getNodeId())) {
				if (StringUtils.isNotBlank(detail.getTags())) {
					map.put(detail.getNodeId(), detail);
				}
			} else {
				map.put(detail.getNodeId(), detail);
			}
		}
		result.addAll(map.values());
		if ("CICS".equalsIgnoreCase(type)) {
			// 先按照cics排序，再按照名字排序, cics程序在前
			Collections.sort(result, new Comparator<ProgramDetailItem>() {
				@Override
				public int compare(ProgramDetailItem o1, ProgramDetailItem o2) {
					int result = o1.getType().compareToIgnoreCase(o2.getType());
					if (result == 0) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					} else {
						return result;
					}
				}
			});
		} else {
			// 先按照cics排序，再按照名字排序， cics程序在后
			Collections.sort(result, new Comparator<ProgramDetailItem>() {
				@Override
				public int compare(ProgramDetailItem o1, ProgramDetailItem o2) {
					int result = o2.getType().compareToIgnoreCase(o1.getType());
					if (result == 0) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					} else {
						return result;
					}
				}
			});
		}
		return result;
	}

	private List<ParagraphDetailItem> handleParagraphDetail(
			List<ParagraphDetailItem> paragraphDetails) {
		List<ParagraphDetailItem> result = new ArrayList<ParagraphDetailItem>();
		Map<String, ParagraphDetailItem> map = new HashMap<String, ParagraphDetailItem>();
		for (ParagraphDetailItem detail : paragraphDetails) {
			if (map.containsKey(detail.getParagraphId())) {
				if (StringUtils.isNotBlank(detail.getTags())) {
					map.put(detail.getParagraphId(), detail);
				}
			} else {
				map.put(detail.getParagraphId(), detail);
			}
		}
		result.addAll(map.values());
		// 按照paragramName排序，其中paragraphName是带有programName的全称
		Collections.sort(result, new Comparator<ParagraphDetailItem>() {
			@Override
			public int compare(ParagraphDetailItem o1, ParagraphDetailItem o2) {
				return o1.getParagraphName().compareToIgnoreCase(
						o2.getParagraphName());
			}
		});
		return result;
	}

	private List<TableItem> handleTableItems(List<TableItem> tableItems) {
		List<TableItem> result = new ArrayList<TableItem>();
		Map<String, TableItem> map = new HashMap<String, TableItem>();
		for (TableItem item : tableItems) {
			if (map.containsKey(item.getNodeId())) {
				if (StringUtils.isNotBlank(item.getTags())) {
					map.put(item.getNodeId(), item);
				}
			} else {
				map.put(item.getNodeId(), item);
			}
		}
		result.addAll(map.values());
		// 按照tableName排序
		Collections.sort(result, new Comparator<TableItem>() {
			@Override
			public int compare(TableItem o1, TableItem o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		return result;
	}

	private List<FileItem> handleFileItems(List<FileItem> fileItems) {
		List<FileItem> result = new ArrayList<FileItem>();
		Map<String, FileItem> map = new HashMap<String, FileItem>();
		for (FileItem item : fileItems) {
			if (map.containsKey(item.getNodeId())) {
				if (StringUtils.isNotBlank(item.getTags())) {
					map.put(item.getNodeId(), item);
				}
			} else {
				map.put(item.getNodeId(), item);
			}
		}
		result.addAll(map.values());
		// 按照programName, fileName排序
		Collections.sort(result, new Comparator<FileItem>() {
			@Override
			public int compare(FileItem o1, FileItem o2) {
				int result = o1.getPgmFileName().compareToIgnoreCase(
						o2.getPgmFileName());
				if (result == 0) {
					return o1.getName().compareToIgnoreCase(o2.getName());
				} else {
					return result;
				}
			}
		});
		return result;
	}

	private List<CopybookDetailItem> handleCopybookItems(
			List<CopybookDetailItem> copybookItems) {
		List<CopybookDetailItem> result = new ArrayList<CopybookDetailItem>();
		Map<String, CopybookDetailItem> map = new HashMap<String, CopybookDetailItem>();
		for (CopybookDetailItem item : copybookItems) {
			if (map.containsKey(item.getNodeId())) {
				if (StringUtils.isNotBlank(item.getTags())) {
					map.put(item.getNodeId(), item);
				}
			} else {
				map.put(item.getNodeId(), item);
			}
		}
		result.addAll(map.values());
		// 按照cpyName排序
		Collections.sort(result, new Comparator<CopybookDetailItem>() {
			@Override
			public int compare(CopybookDetailItem o1, CopybookDetailItem o2) {
				return o1.getCpyName().compareToIgnoreCase(o2.getCpyName());
			}
		});
		return result;
	}

	private List<JclDetailItem> handleJclDetailItems(
			List<JclDetailItem> jclDetailItems) {
		List<JclDetailItem> result = new ArrayList<JclDetailItem>();
		Map<String, JclDetailItem> map = new HashMap<String, JclDetailItem>();
		for (JclDetailItem item : jclDetailItems) {
			if (map.containsKey(item.getNodeId())) {
				if (StringUtils.isNotBlank(item.getTags())) {
					map.put(item.getNodeId(), item);
				}
			} else {
				map.put(item.getNodeId(), item);
			}
		}
		result.addAll(map.values());
		// 按照jclName排序
		Collections.sort(result, new Comparator<JclDetailItem>() {
			@Override
			public int compare(JclDetailItem o1, JclDetailItem o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		return result;
	}

	/**
	 * 因为tag有空字符串和list两种。判断tag是否为list，若是list，则转换成逗号相隔的字符串返回
	 *
	 * @param tag
	 * @return
	 */
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

	private void getFileProgramInfo(FileItem fileItem) {
		String fileId = fileItem.getNodeId();
		String pgmId = StringUtils.substringBeforeLast(fileId, ".");
		String pgmLocation = FilePathUtil.getFilePathFromNodeId(pgmId);
		String pgmFileName = StringUtils.substringAfterLast(pgmLocation, "/");
		fileItem.setPgmId(pgmId);
		fileItem.setPgmLocation(pgmLocation);
		fileItem.setPgmFileName(pgmFileName);
	}

	private String handleOperation(String operation) {
		if (SqlOperationType.DECLARE_CURSOR.getType().equals(operation)) {
			return SqlOperationType.DECLARE_CURSOR.name();
		} else if (SqlOperationType.SELECT.getType().equals(operation)) {
			return SqlOperationType.SELECT.name();
		} else if (SqlOperationType.UPDATE.getType().equals(operation)) {
			return SqlOperationType.UPDATE.name();
		} else if (SqlOperationType.DELETE.getType().equals(operation)) {
			return SqlOperationType.DELETE.name();
		} else if (SqlOperationType.INSERT.getType().equals(operation)) {
			return SqlOperationType.INSERT.name();
		} else {
			return "";
		}
	}

	private List<String> handleCommandToOperation(String operation) {
        List<String> result = new ArrayList<String>();
        if (SqlOperationType.DECLARE_CURSOR.toString().contains(operation)) {
            result.add(SqlOperationType.DECLARE_CURSOR.getType());
        }
        if (SqlOperationType.SELECT.toString().contains(operation)) {
            result.add(SqlOperationType.SELECT.getType());
        }
        if (SqlOperationType.UPDATE.toString().contains(operation)) {
            result.add(SqlOperationType.UPDATE.getType());
        }
        if (SqlOperationType.DELETE.toString().contains(operation)) {
            result.add(SqlOperationType.DELETE.getType());
        }
        if (SqlOperationType.INSERT.toString().contains(operation)) {
            result.add(SqlOperationType.INSERT.getType());
        }
		result.add(operation);
		return result;
	}
	
	public void downLoadSystemDocumentationDoc(HttpServletResponse response,String projectId) {
        Project project = projectRepository.findOne(projectId);
        String projectPath = project.getPath();
        String codeVersion = FileStatusUtil.checkCode(projectPath);
        String docPath = projectPath + "/" + OUTPUT + codeVersion + "/SystemDocumentation.doc";
		File docFile = new File(docPath);
		if(!docFile.exists()){
		    generateSystemDocumentationDownloadHtml(projectId);
		    docFile = new File(docPath);
		}
//		try {
//			DocConverterUtil.convertHtmlToDoc(htmlFile, docPath);
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			out = new BufferedOutputStream(response.getOutputStream());
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Content-Disposition", "attachment; filename=" + docFile.getName());
			response.setHeader("Content-Length", String.valueOf(docFile.length()));
			if (!docFile.exists()) {
				out.write("nothing".getBytes());
				return;
			}
			in = new BufferedInputStream(new FileInputStream(docFile));
			byte[] data = new byte[1024];
			int len = 0;
			while (-1 != (len = in.read(data, 0, data.length))) {
				out.write(data, 0, len);
			}
		} catch (Exception e) {
			LOGGER.error("Download failed.", e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				LOGGER.error("IO Exception", e);
			}
		}
//		htmlFile.delete();
//		docFile.delete();
	}

    public String printSystemDocumentationPdf(HttpServletResponse response, String projectId) {
        Project project = projectRepository.findOne(projectId);
        String projectPath = project.getPath();
        String codeVersion = FileStatusUtil.checkCode(projectPath);
        String pdfPath = projectPath + "/" + OUTPUT + codeVersion + "/SystemDocumentation.pdf";
        File pdfFile = new File(pdfPath);
        if (!pdfFile.exists()) {
            pdfPath = generateSystemDocumentationDownloadHtml(projectId);
            return pdfPath;
        }
        return pdfPath;
    }

    public String generateSystemDocumentationDownloadHtml(String... args) {
//  public String generateSystemDocumentationDownloadHtml(String projectId,String jobName) {
        String projectId = args[0];
        Project project = projectRepository.findOne(projectId);
        String projectPath = project.getPath();
        String codeVersion = FileStatusUtil.checkCode(projectPath);
        String docFilePath = projectPath + "/" + OUTPUT + codeVersion + "/SystemDocumentation.doc";
        // String htmlPath = projectPath + "/" + OUTPUT + codeVersion + "/SystemDocumentation.html";
        String pdfPath = projectPath + "/" + OUTPUT + codeVersion + "/SystemDocumentation.pdf";
        File docFile = new File(docFilePath);
        File pdfFile = new File(pdfPath);
        if (!docFile.exists()) {
            Map<String, Object> datas = new HashMap<String, Object>();
            datas.put("projectName", project.getName());

            Date currentTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("MMMM d,yyyy");
            String time = formatter.format(currentTime);
            datas.put("time", time);

            Properties properties = FilePathUtil.getProperties();
            String companyName = "";
            String logoBase64 = "";
            companyName = properties.getProperty("companyName");
            String logoPath = properties.getProperty("logoPath");
            logoBase64 = PictureUtil.imageToBase64Str(logoPath);
            datas.put("companyName", companyName);
            datas.put("logoBase64", logoBase64);

            DetailExcelDto htmlData = getExcelDownloadData(Integer.valueOf(projectId));

            datas.put("projectDescription", project.getDescription());
            datas.put("summaryDetails", htmlData.getSummaryDetails());
            datas.put("programDetails", htmlData.getProgramDetails());
            datas.put("paraDetails", htmlData.getParaDetails());
            datas.put("fileDetails", htmlData.getFileItems());
            datas.put("jclDetails", htmlData.getJclDetailItems());
            List<TableItem> tableItems = htmlData.getTableItems();
            List<FileItem> fileItems = htmlData.getFileItems();
            List<CopybookDetailItem> copybookDetails = htmlData.getCopybookDetails();

            List<TableUsedInItem> usedInItems = new ArrayList<TableUsedInItem>();
            for (TableItem ti : tableItems) {
                String nodeId = ti.getNodeId();
                List<ParagraphUseTableInfo> usedIn = getAllParagraphWithTable(Integer.valueOf(projectId), nodeId);
                TableUsedInItem tableUsedIn = new TableUsedInItem(nodeId, ti.getName(), ti.getTags(), usedIn);
                usedInItems.add(tableUsedIn);
            }
            datas.put("usedInItems", usedInItems);

            List<FileColumnDetail> fileColumnDetails = new ArrayList<FileColumnDetail>();
            for (FileItem fi : fileItems) {
                String nodeId = fi.getNodeId();
                List<FileDetailItem> allFileDetailItems = getAllFileDetailItems(Integer.valueOf(projectId), nodeId);
                FileColumnDetail fileColumnDetail = new FileColumnDetail(nodeId, fi.getName(), fi.getTags(),
                        allFileDetailItems);
                fileColumnDetails.add(fileColumnDetail);
            }
            datas.put("fileColumnDetails", fileColumnDetails);

            List<TableColumnDetail> tableColumnDetails = new ArrayList<TableColumnDetail>();
            for (TableItem ti : tableItems) {
                String nodeId = ti.getNodeId();
                List<TableDetailItem> allTableDetailItems = getAllTableDetailItems(Integer.valueOf(projectId), nodeId);
                TableColumnDetail tableColumnDetail = new TableColumnDetail(nodeId, ti.getName(), ti.getTags(),
                        allTableDetailItems);
                tableColumnDetails.add(tableColumnDetail);
            }
            datas.put("tableColumnDetails", tableColumnDetails);

            List<CopyBookDetail> copyBookDetails = new ArrayList<CopyBookDetail>();
            for (CopybookDetailItem cd : copybookDetails) {
                String cpyId = cd.getNodeId();
                List<ProgramDetailItem> usedInPrograms = getAllProgramWithCpyId(Integer.valueOf(projectId), cpyId);
                CopyBookDetail cpyDetail = new CopyBookDetail(cpyId, cd.getCpyName(), cd.getType(), cd.getTags(),
                        usedInPrograms);
                copyBookDetails.add(cpyDetail);
            }
            datas.put("copyBookDetails", copyBookDetails);

            List<ProgramInfo> programs = new ArrayList<ProgramInfo>();
            Map<String, List<String>> sourceCodeMap = new HashMap<String, List<String>>();
            
            //分进度条
            if (args.length > 1) {
                String jobName = "";
                jobName = args[1];
                double perValue = 0;
                DecimalFormat df = new DecimalFormat("#.00");
                int length = htmlData.getProgramDetails().size();
                for (ProgramDetailItem programDetailItem : htmlData.getProgramDetails()) {
                    ProgramInfo program = new ProgramInfo();
                    String programName = programDetailItem.getName();
                    program = codeBrowserService.getProgramInfo(projectId, project.getName(), projectPath, codeVersion,
                            programName);
                    sourceCodeMap.put(program.getProgramName(), program.getSourceCode());
                    program.setProgramDetailItem(programDetailItem);
                    programs.add(program);
                    // total 80% percents
                    perValue = perValue + 80 / length;
                    try {
                        webSocket.sendMessageTo(jobName + "/" + df.format(perValue), projectId);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        LOGGER.error(e);
                    }
                }
            } else {
                for (ProgramDetailItem programDetailItem : htmlData.getProgramDetails()) {
                    ProgramInfo program = new ProgramInfo();
                    String programName = programDetailItem.getName();
                    program = codeBrowserService.getProgramInfo(projectId, project.getName(), projectPath, codeVersion,
                            programName);
                    sourceCodeMap.put(program.getProgramName(), program.getSourceCode());
                    program.setProgramDetailItem(programDetailItem);
                    programs.add(program);
                }
            }

            List<ParagraphUseTableInfo> sqlLogicItems = htmlData.getSqlLogicItems();
            for (int i = 0; i < sqlLogicItems.size(); i++) {
                ParagraphUseTableInfo sqlLogicItem = sqlLogicItems.get(i);
                if (sqlLogicItem.getParagraphName().equals(""))
                    sqlLogicItem.setParagraphName(sqlLogicItem.getProgramName());
                Integer startLine = Integer.valueOf(sqlLogicItem.getStartLine()) - 1;
                Integer endLine = Integer.valueOf(sqlLogicItem.getEndLine());
                List<String> sourceCode = sourceCodeMap.get(sqlLogicItem.getProgramName());
                sqlLogicItem.setSourceCode(sourceCode.subList(startLine, endLine));
                sqlLogicItems.set(i, sqlLogicItem);
            }
            datas.put("sqlLogicItems", sqlLogicItems);
            datas.put("programs", programs);

            // datas.put("projectName", projectName);
            // datas.put("programName", programName);
            // datas.put("controlflowImgMap", ctrlFlowImgMap);
            // datas.put("rootName", rootName);
            // datas.put("rows", sorted);
            // datas.put("dependencyImg", dependencyImg);

            TemplateUtil.generateFile(SYSTEM_DOCUMENTATION, docFilePath, datas);
            docFile = new File(docFilePath);
            WordUtil.updateIndex(docFile);
        }
        if (!pdfFile.exists()) {
            WordUtil.wordToPdf(docFile, pdfPath);
        }
        return pdfPath;
    }
	
//    public String generateSystemDocumentationDownloadHtml(int projectId, boolean isDownload) {
//		Project project = projectRepository.findOne(String.valueOf(projectId));
//		String projectPath = project.getPath();
//		String htmlPath = projectPath + "/SystemDocumentation.html";
//		File file = new File(htmlPath);
//		if (!file.exists()) {
//			file.mkdirs();
//		}
//		DetailExcelDto htmlData = getExcelDownloadData(projectId);
//		Map<String, Object> data = new HashMap<String, Object>();
//		data.put("projectName", project.getName());
//		data.put("projectDescription", project.getDescription());
//		data.put("summaryDetails", htmlData.getSummaryDetails());
//		data.put("programDetails", htmlData.getProgramDetails());
//		data.put("paraDetails", htmlData.getParaDetails());
//		List<TableItem> tableItems = htmlData.getTableItems();
//		List<CopybookDetailItem> copybookDetails = htmlData
//				.getCopybookDetails();
//
//		List<TableUsedInItem> usedInItems = new ArrayList<TableUsedInItem>();
//		for (TableItem ti : tableItems) {
//			String nodeId = ti.getNodeId();
//			List<ParagraphUseTableInfo> usedIn = getAllParagraphWithTable(
//					projectId, nodeId);
//			TableUsedInItem tableUsedIn = new TableUsedInItem(nodeId,
//					ti.getName(), ti.getTags(), usedIn);
//			usedInItems.add(tableUsedIn);
//		}
//		data.put("usedInItems", usedInItems);
//
//		List<TableColumnDetail> tableColumnDetails = new ArrayList<TableColumnDetail>();
//		for (TableItem ti : tableItems) {
//			String nodeId = ti.getNodeId();
//			List<TableDetailItem> allTableDetailItems = getAllTableDetailItems(
//					projectId, nodeId);
//			TableColumnDetail tableColumnDetail = new TableColumnDetail(nodeId,
//					ti.getName(), ti.getTags(), allTableDetailItems);
//			tableColumnDetails.add(tableColumnDetail);
//		}
//		data.put("tableColumnDetails", tableColumnDetails);
//
//		List<CopyBookDetail> copyBookDetails = new ArrayList<CopyBookDetail>();
//		for (CopybookDetailItem cd : copybookDetails) {
//			String cpyId = cd.getNodeId();
//			List<ProgramDetailItem> usedInPrograms = getAllProgramWithCpyId(
//					projectId, cpyId);
//			CopyBookDetail cpyDetail = new CopyBookDetail(cpyId,
//					cd.getCpyName(), cd.getType(), cd.getTags(), usedInPrograms);
//			copyBookDetails.add(cpyDetail);
//		}
//		data.put("copyBookDetails", copyBookDetails);
//
//		data.put("sqlLogicItems", htmlData.getSqlLogicItems());
//		if (isDownload) {
//			TemplateUtil.generateFile(PDF_DOWNLOAD_TEMPLATE_FTL, htmlPath, data);
//		} else {
//			TemplateUtil.generateFile(HTML_PRINT_TEMPLATE_FTL, htmlPath, data);
//		}
//		return htmlPath;
//	}

	private void cacheSO(int projectId,
			List<SummaryDetailItem> summaryDetailItems,Neo4jDao neo4jDao,String codeVersion) {
		Project project = projectRepository.findOne(String.valueOf(projectId));
		if (project == null) {
			return;
		}
		String projectPath = project.getPath();
		
		// 判断是否需要cache，若需要，则取数据，再cache
		// summary detail
		String summaryDetailPath = needCache(projectPath,
				SystemDocType.SYSTEM_DOC.toString(),codeVersion);
        if (summaryDetailPath != null) {
            LOGGER.info("Cache SYSTEM_DOC.");
            cacheResult(summaryDetailPath, summaryDetailItems);
        } else {
            LOGGER.info("SYSTEM_DOC already cached.");
        }

		// program
		String programPath = needCache(projectPath,
				SystemDocType.PROGRAM.toString(),codeVersion);
        if (programPath != null) {
            LOGGER.info("Cache PROGRAM.");
            List<ProgramDetailItem> programDetails = getProgramDetail(projectId);
            cacheResult(programPath, programDetails);
        } else {
            LOGGER.info("PROGRAM already cached.");
        }

		// paragraph
		String paragraphPath = needCache(projectPath,
				SystemDocType.PARAGRAPH.toString(),codeVersion);
		if (paragraphPath != null) {
		    LOGGER.info("Cache PARAGRAPH.");
			List<ParagraphDetailItem> paraDetails = getParagraphDetail(projectId);
			cacheResult(paragraphPath, paraDetails);
		} else {
            LOGGER.info("PARAGRAPH already cached.");
        }
		// table
		String tablePath = needCache(projectPath,
				SystemDocType.TABLE.toString(),codeVersion);
		if (tablePath != null) {
		    LOGGER.info("Cache TABLE.");
			List<TableItem> tableItems = getTableItems(projectId);
			LOGGER.info("tableItemsSize:" + tableItems.size());
			List<CacheTable> cacheTables = new ArrayList<CacheTable>();
			for (TableItem tableItem : tableItems) {
				String nodeId = tableItem.getNodeId();
				List<TableDetailItem> items = getTableDetailItems(neo4jDao,
						nodeId);
				cacheTables.add(new CacheTable(tableItem, items));
			}
            LOGGER.info("cacheTables:" + cacheTables.size());
			cacheResult(tablePath, cacheTables);
		} else {
            LOGGER.info("TABLE already cached.");
        }

		// file
		String filePath = needCache(projectPath, SystemDocType.FILE.toString(),codeVersion);
		if (filePath != null) {
		    LOGGER.info("Cache FILE.");
			List<FileItem> fileItems = getFileItems(projectId);
			List<CacheFile> cacheFiles = new ArrayList<CacheFile>();
			for (FileItem fileItem : fileItems) {
				String nodeId = fileItem.getNodeId();
				List<FileDetailItem> items = getFileDetailItems(neo4jDao,
						nodeId);
				cacheFiles.add(new CacheFile(fileItem, items));
			}
			cacheResult(filePath, cacheFiles);
		} else {
            LOGGER.info("FILE already cached.");
        }

		// copybook
		String copybookPath = needCache(projectPath,
				SystemDocType.COPYBOOK.toString(),codeVersion);
		if (copybookPath != null) {
		    LOGGER.info("Cache COPYBOOK.");
			List<CopybookDetailItem> copybookDetails = getCopybookDetail(projectId);
			List<CacheCopybook> cacheCopybooks = new ArrayList<CacheCopybook>();
			for (CopybookDetailItem copybookDetailItem : copybookDetails) {
				String nodeId = copybookDetailItem.getNodeId();
				List<ProgramDetailItem> items = getProgramWithCpyId(neo4jDao,
						nodeId);
				cacheCopybooks
						.add(new CacheCopybook(copybookDetailItem, items));
			}
			cacheResult(copybookPath, cacheCopybooks);
		} else {
            LOGGER.info("COPYBOOK already cached.");
        }

		// jcl
		String jclPath = needCache(projectPath, SystemDocType.JCL.toString(),codeVersion);
		if (jclPath != null) {
		    LOGGER.info("Cache JCL.");
			List<JclDetailItem> jclDetails = getJclDetail(projectId);
			List<CacheJcl> cacheJcls = new ArrayList<CacheJcl>();
            LOGGER.info("JCL size:" + jclDetails.size());
			for (JclDetailItem jclDetailItem : jclDetails) {
				String nodeId = jclDetailItem.getNodeId();
				List<JclStepItem> items = getJclSteps(neo4jDao, nodeId);
				cacheJcls.add(new CacheJcl(jclDetailItem, items));
			}
			cacheResult(jclPath, cacheJcls);
		} else {
            LOGGER.info("JCL already cached.");
        }

		// sql logic
		String sqlLogicPath = needCache(projectPath,
				SystemDocType.SQL_LOGIC.toString(),codeVersion);
		if (sqlLogicPath != null) {
		    LOGGER.info("Cache SQL_LOGIC.");
			List<ParagraphUseTableInfo> sqlLogics = getSqlLogic(projectId, "");
			cacheResult(sqlLogicPath, sqlLogics);
		} else {
            LOGGER.info("SQL_LOGIC already cached.");
        }
		
		neo4jDao.close();
	}

	/**
	 * 若需要cache，则返回cache path
	 * 
	 * null表示已存在，不需要再cache
	 * 
	 * @param projectPath
	 * @param type
	 * @return
	 */
	private String needCache(String projectPath, String type,String codeVersion) {
		String path = FilePathUtil.getSystemDocPath(projectPath, codeVersion)
				+ "/" + type.toLowerCase() + ".json";
		File file = new File(path);
		if (!file.exists()) {
			return path;
		}
		return null;
	}

	private void cacheResult(String path, List<? extends Object> result) {
		File file = new File(path);
		if (!file.exists()) {
			// 该文件不存在，则将result写入
		    LOGGER.info("Write cache");
			String jsonString = JSON.toJSONString(result);
			try {
                FileUtils.writeStringToFile(file, jsonString);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                LOGGER.error(e);
            }
		}
	}
}
