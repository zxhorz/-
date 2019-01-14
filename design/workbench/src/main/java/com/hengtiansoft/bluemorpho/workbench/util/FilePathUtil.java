package com.hengtiansoft.bluemorpho.workbench.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.FileCopyUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hengtiansoft.bluemorpho.workbench.dto.ControlFlowDto;

public class FilePathUtil {
	private static final Logger LOGGER = Logger.getLogger(FilePathUtil.class);
	// 以下内容使用方式后续更改，内容存入property文件，通过读入property文件一次性加载
	public static final String SOURCECODE = "/sourcecode";
	public static final String OUTPUT = "/output";
	private static final String CONFIG = "/config";
	private static final String DEFAULT_COBOL = "/COBOL";
	private static final String DEFAULT_JOB = "/JOB";
	private static final String DEFAULT_PROC = "/PROC";
	private static final String DEFAULT_COPYBOOK = "/COPYBOOK";
	private static final String PROJECT = "project/";
	private static final String UPLOAD_PATH = "upload/";
	private static final String DOWNLOAD_PATH = "download/";
	public static final String OUTPUT_NEO4J_DB = "/so/neo4j/data/databases/graph.db";
	private static final String NEO4J_DB = "/so/neo4j";
	private static final String CSV = "/so/csv";
	private static final String PREPROCESS = "/so/preprocess";
	private static final String SVG = "/so/svg";
	private static final String PARAGRAPH = "/so/paragraph";
	private static final String COMMENT = "/so/comment";
	private static final String SO_CONFIG = "/so";
	private static final String TOOL = "tools";
	private static final String DEAD_CODE_CONFIG = "/dead_code";
	private static final String NODES = "/nodes";
	private static final String RELATIONS = "/relations";
	private static final String CLONE_TYPE = "type3";
	private static final String CLONE_CONFIG = "/clone";
	private static final String NEO4J_PLUGIN_JAR = "Neo4j-function-plugin-0.0.1-SNAPSHOT.jar";
	private static final String SELECTED_ANALYSIS_TYPE = "/analysisTypes.txt";
	private static final String SYSTEMCONFIG = "systemconfig/";
	private static final String MENU = "/menus.xml";
	private static final String SMTP = "/smtp.properties";
	private static final String SUMMARYTABMAP = "/summarytabmap.json";
	private static final String VIRTUALMAP = "/virtualmap.txt";
	private static final String CLONE_RESULT = "/result.json";
	private static final String PROGRAM_CLONE_RESULT = "/programClone.json";
	private static final String CLONE_TOTALTILE = "/totalLine.txt";
	private static final String CLONE_PERCENTAGE = "/clonePercentage.json";
	private static final String CONTROL_FLOW = "/controlflow";
	private static final String DOCUMENTATION = "/documentation";
	private static final String DEPENDENCY = "/dependency";
	private static final String FILE_STRUCTURE = "/filestructure";
	private static final String SEARCH = "/search";
	private static final String SYSTEM_DOCUMENTATION = "/system_doc";
	private static final String COST_ESTIMATION = "/cost_estimation";
	private static final String CACHE = "/cache";
	private static final String TEMP = "temp/";
	private static final String SEARCHRESULT = "/searchResults.json";
	private static final String TAGRESULT ="/tagResult.json";
	private static final String AUTOTAG = "autoTag/";
	private static final String AUTOTAG_FEEDBACK = "feedback.json";
	private static final String SCRIPT = "scripts";
	private static final String SCRIPT_DESCRITION = "/README.md";
	public static final String SCRIPT_WORK_DIR = "/script_workdirs";
	public static final String SCRIPT_OUTPUT_DIR = "/output";
	private static final String PROPERTITES = "config/config.properties";

	private static String getCahchePath(String projectPath, String codeVersion) {
		String outputPath = getPath(projectPath, "OUTPUT");
		outputPath = StringUtils.replace(outputPath, "\\", "/");
		String cachePath = outputPath + "/" + codeVersion + CACHE;
		// 若该文件夹不存在，则创建
		File file = new File(cachePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return cachePath;
	}

	public static String getSystemDocPath(String projectPath, String codeVersion) {
		String systemDocPath = getCahchePath(projectPath, codeVersion)
				+ SYSTEM_DOCUMENTATION;
		// 若该文件夹不存在，则创建
		File file = new File(systemDocPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return systemDocPath;
	}

	public static String getCostEstimationPath(String projectPath,
			String codeVersion) {
		String costEstimationPath = getCahchePath(projectPath, codeVersion)
				+ COST_ESTIMATION;
		// 若该文件夹不存在，则创建
		File file = new File(costEstimationPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return costEstimationPath;
	}

	public static String getSoDetailPath(String projectPath, String codeVersion) {
		return getSystemDocPath(projectPath, codeVersion) + "/system_doc.json";
	}

	public static String getProgramDetailPath(String projectPath,
			String codeVersion) {
		return getSystemDocPath(projectPath, codeVersion) + "/program.json";
	}

	public static String getParagraphDetailPath(String projectPath,
			String codeVersion) {
		return getSystemDocPath(projectPath, codeVersion) + "/paragraph.json";
	}

	public static String getTableDetailPath(String projectPath,
			String codeVersion) {
		return getSystemDocPath(projectPath, codeVersion) + "/table.json";
	}

	public static String getFileDetailPath(String projectPath,
			String codeVersion) {
		return getSystemDocPath(projectPath, codeVersion) + "/file.json";
	}

	public static String getCpyDetailPath(String projectPath, String codeVersion) {
		return getSystemDocPath(projectPath, codeVersion) + "/copybook.json";
	}

	public static String getJclDetailPath(String projectPath, String codeVersion) {
		return getSystemDocPath(projectPath, codeVersion) + "/jcl.json";
	}

	public static String createDefaultPath(String projectName) {
		File file = new File(PROJECT + projectName);
		if (!file.exists()) {
			file.mkdirs();
		} else {
			// 若存在，则先删除
			try {
				FileUtils.deleteDirectory(file);
				file.mkdirs();
			} catch (IOException e) {
				LOGGER.error("error to delete file:" + file);
			}
		}
		String path = StringUtils.replace(file.getAbsolutePath(), "\\", "/");
		// Create Source code path

		createDefaultSourcePath(path);
		createDefaultSubPath(path, OUTPUT);
		createDefaultSubPath(path, CONFIG);
		return path;
	}

	public static String createUploadFile(String uploadFileName) {
		File file = new File(UPLOAD_PATH + uploadFileName);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		String path = StringUtils.replace(file.getAbsolutePath(), "\\", "/");
		return path;
	}

	public static String createTempDownloadFile(String downloadFileName) {
		File file = new File(DOWNLOAD_PATH + downloadFileName);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		String path = StringUtils.replace(file.getAbsolutePath(), "\\", "/");
		return path;
	}

	private static void createDefaultSourcePath(String path) {
		File file = new File(path + SOURCECODE);

		if (!file.exists()) {
			file.mkdir();
		}

		String sourcePath = StringUtils.replace(file.getAbsolutePath(), "\\",
				"/");
		createDefaultSubPath(sourcePath, DEFAULT_COBOL);
		createDefaultSubPath(sourcePath, DEFAULT_JOB);
		createDefaultSubPath(sourcePath, DEFAULT_COPYBOOK);
		createDefaultSubPath(sourcePath, DEFAULT_PROC);
	}

	public static void createDefaultSubPath(String absolutePath, String subPath) {
		File file = new File(absolutePath + subPath);
		if (!file.exists()) {
			file.mkdir();
		}
	}

	public static String getPath(String path, String name) {
		String realpath = "";
		switch (name) {
		case "OUTPUT":
			realpath = path + OUTPUT;
			break;
		case "CONFIG":
			realpath = path + CONFIG;
			break;
		case "SOURCE":
			realpath = path + SOURCECODE;
			break;
		case "COBOL":
			realpath = path + DEFAULT_COBOL;
			break;
		case "COPYBOOK":
			realpath = path + DEFAULT_COPYBOOK;
			break;
		case "JOB":
			realpath = path + DEFAULT_JOB;
			break;
		case "PROC":
			realpath = path + DEFAULT_PROC;
			break;
		// case "FILESTATUS": realpath = path + FILESTATUS; break;
		default:
			;
		}
		;
		return realpath;
	}

	public static String getSearchOutputPath(String path) {
		path = StringUtils.replace(path, "\\", "/");
		path = path + OUTPUT + SEARCH;
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return path;
	}

	public static String getSearchResultPath(String path) {
		path = StringUtils.replace(path, "\\", "/");
		path = path + SEARCHRESULT;
		return path;
	}

	public static String getTagResultPath() {
		File file = new File(AUTOTAG);
		if (!file.exists()) {
			file.mkdirs();
		}
		String filePath = StringUtils
				.replace(file.getAbsolutePath(), "\\", "/");
		return filePath + TAGRESULT;
	}

	public static String getAutoFeedbackPath() {
		String filePath = AUTOTAG + AUTOTAG_FEEDBACK;
		File file = new File(filePath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		return filePath;
	}

	public static String getNeo4jPath(String path, String codeVersion) {
		return path + OUTPUT + "/" + codeVersion + NEO4J_DB;
	}

	public static void placeNeo4jPlugin(String path, String codeVersion) {
		String neo4jPlugin = getNeo4jPath(path, codeVersion) + "/plugins/"
				+ NEO4J_PLUGIN_JAR;
		File pluginFile = new File(neo4jPlugin);
		if (!pluginFile.getParentFile().exists()) {
			pluginFile.getParentFile().mkdir();
		}
		String sourceFile = getToolPath() + "/" + NEO4J_PLUGIN_JAR;
		try {
			FileUtils.copyFile(new File(sourceFile), pluginFile);
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}

	public static Map<String, Object> getDefaultSoPathConfig(String path,
			String codeVersion) {
		path = StringUtils.replace(path, "\\", "/");
		String sourcePath = path + SOURCECODE;
		String outPath = path + OUTPUT + "/" + codeVersion;
		Map<String, Object> filePath = new HashMap<>();
		filePath.put("codeDirectory", sourcePath);
		filePath.put("cobolDirectory", sourcePath + DEFAULT_COBOL);
		filePath.put("copybookDirectory", sourcePath + DEFAULT_COPYBOOK);
		filePath.put("csvOutputDirectory", outPath + CSV);
		filePath.put("preprocessDirectory", outPath + PREPROCESS);
		filePath.put("neo4jDirectory", outPath + OUTPUT_NEO4J_DB);
		filePath.put("controlFlowSvgPath", outPath + SVG);
		filePath.put("jobDirectory", sourcePath + DEFAULT_JOB);
		filePath.put("procDirectory", sourcePath + DEFAULT_PROC);
		filePath.put("splitOutputDirectory", outPath + PARAGRAPH);
		filePath.put("commentDirectory", outPath + COMMENT);
		return filePath;
	}
	
	public static Map<String, Object> getDefaultSoIncrementalPathConfig(
			String path, String incrementBaseVersion, String currentVersion, String neo4jUri) {
		path = StringUtils.replace(path, "\\", "/");
		String sourcePath = path + SOURCECODE;
		String lastOutPath = path + OUTPUT + "/" + incrementBaseVersion;
		String outPath = path + OUTPUT + "/" + currentVersion;
		
		Map<String, Object> filePath = new HashMap<>();
		filePath.put("codeDirectory", sourcePath);
		filePath.put("cobolDirectory", sourcePath + DEFAULT_COBOL);
		filePath.put("copybookDirectory", sourcePath + DEFAULT_COPYBOOK);
		filePath.put("oldPreprocessDirectory", lastOutPath + PREPROCESS);
		filePath.put("newPreprocessDirectory", outPath + PREPROCESS);
		filePath.put("oldCsvPath", lastOutPath + CSV);
		filePath.put("newCsvPath", outPath + CSV);
		filePath.put("oldNeo4jPath", lastOutPath + OUTPUT_NEO4J_DB);
		filePath.put("newNeo4jPath", outPath + OUTPUT_NEO4J_DB);
		filePath.put("neo4jUri", neo4jUri);
		filePath.put("splitOutputOldDirectory", lastOutPath + PARAGRAPH);
		filePath.put("splitOutputNewDirectory", outPath + PARAGRAPH);
		filePath.put("jobDirectory", sourcePath + DEFAULT_JOB);
		filePath.put("procDirectory", sourcePath + DEFAULT_PROC);
		filePath.put("oldCommentDirectory", lastOutPath + COMMENT);
		filePath.put("newCommentDirectory", outPath + COMMENT);
		return filePath;
	}

	public static String getParagraphsPath(String path, String codeVersion) {
		path = StringUtils.replace(path, "\\", "/");
		return path + OUTPUT + "/" + codeVersion + PARAGRAPH;

	}

	public static Map<String, Object> getDefaultDeadCodePathConfig(String path,
			String codeVersion) {
		path = StringUtils.replace(path, "\\", "/");
		String outputPath = path + OUTPUT + "/" + codeVersion;
		String nodesPath = outputPath + CSV + NODES;
		String relationsPath = outputPath + CSV + RELATIONS;

		Map<String, Object> filePath = new HashMap<>();
		filePath.put("programCsvPath", nodesPath + "/program.csv");
		filePath.put("callCsvPath", relationsPath + "/call.csv");
		filePath.put("execPgmCsvPath", relationsPath + "/execPgm.csv");
		filePath.put("paragraphCsvPath", nodesPath + "/paragraph.csv");
		filePath.put("performCsvPath", relationsPath + "/perform.csv");
		filePath.put("reportLocation", outputPath + DEAD_CODE_CONFIG);
		// 发现dead code jar包不会主动新建dead_code文件夹
		File deadCodeDir = new File(outputPath + DEAD_CODE_CONFIG);
		if (!deadCodeDir.exists()) {
			deadCodeDir.mkdirs();
		}
		return filePath;
	}

	public static String getSoLogPath(String path, String codeVersion) {
		path = StringUtils.replace(path, "\\", "/");
		String logPath = path + OUTPUT + "/" + codeVersion + SO_CONFIG
				+ SO_CONFIG + ".log";
		File logFile = new File(logPath);
		if (!logFile.getParentFile().exists()) {
			logFile.getParentFile().mkdirs();
		}
		if (logFile.exists()) {
			logFile.delete();
		}
		return logPath;
	}

	public static String getCloneLogPath(String path, String codeVersion) {
		path = StringUtils.replace(path, "\\", "/");
		String logPath = path + OUTPUT + "/" + codeVersion + CLONE_CONFIG
				+ CLONE_CONFIG + ".log";
		File logFile = new File(logPath);
		if (!logFile.getParentFile().exists()) {
			logFile.getParentFile().mkdirs();
		}
		if (logFile.exists()) {
			logFile.delete();
		}
		return logPath;
	}

	public static String getControlFlowLogPath(String path, String codeVersion) {
		path = StringUtils.replace(path, "\\", "/");
		String logPath = path + OUTPUT + "/" + codeVersion + CONTROL_FLOW
				+ CONTROL_FLOW + ".log";
		File logFile = new File(logPath);
		if (!logFile.getParentFile().exists()) {
			logFile.getParentFile().mkdirs();
		}
		if (logFile.exists()) {
			logFile.delete();
		}
		return logPath;
	}

	public static String getSearchLogPath(String path) {
		path = StringUtils.replace(path, "\\", "/");
		String logPath = path + OUTPUT + SEARCH + SEARCH + ".log";
		File logFile = new File(logPath);
		if (!logFile.getParentFile().exists()) {
			logFile.getParentFile().mkdirs();
		}
		if (logFile.exists()) {
			logFile.delete();
		}
		return logPath;
	}

	public static String getFileStructureLogPath(String path, String codeVersion) {
		path = StringUtils.replace(path, "\\", "/");
		String logPath = path + OUTPUT + "/" + codeVersion + FILE_STRUCTURE
				+ FILE_STRUCTURE + ".log";
		File logFile = new File(logPath);
		if (!logFile.getParentFile().exists()) {
			logFile.getParentFile().mkdirs();
		}
		if (logFile.exists()) {
			logFile.delete();
		}
		return logPath;
	}

	public static String getDeadCodeLogPath(String path, String codeVersion) {
		path = StringUtils.replace(path, "\\", "/");
		String logPath = path + OUTPUT + "/" + codeVersion + DEAD_CODE_CONFIG
				+ DEAD_CODE_CONFIG + ".log";
		File logFile = new File(logPath);
		if (!logFile.getParentFile().exists()) {
			logFile.getParentFile().mkdirs();
		}
		if (logFile.exists()) {
			logFile.delete();
		}
		return logPath;
	}

	public static Map<String, Object> getDefaultClonePathConfig(String path,
			String port, String codeVersion) {
		path = StringUtils.replace(path, "\\", "/");
		String outputPath = path + OUTPUT + "/" + codeVersion;
		Map<String, Object> filePath = new HashMap<>();
		filePath.put("analyzeType", CLONE_TYPE);
		filePath.put("inputType", "file");
		// filePath.put("inputPath", getCloneConfig(path) + CLONE_CYPER);
		filePath.put("inputPath", getParagraphsPath(path, codeVersion));
		filePath.put("outputPath", outputPath + CLONE_CONFIG);
		filePath.put("protocol", "bolt");
		filePath.put("port", port);
		return filePath;
	}

	public static String getSoConfig(String path) {
		path = StringUtils.replace(path, "\\", "/");
		return path + CONFIG + SO_CONFIG;
	}

	public static String getDeadCodeConfig(String path) {
		path = StringUtils.replace(path, "\\", "/");
		String deadCodePath = path + CONFIG + DEAD_CODE_CONFIG;
		File deadCodeDir = new File(deadCodePath);
		if (!deadCodeDir.exists()) {
			deadCodeDir.mkdirs();
		}
		return deadCodePath;
	}

	public static String getCloneConfig(String path) {
		path = StringUtils.replace(path, "\\", "/");
		String clonePath = path + CONFIG + CLONE_CONFIG;
		File cloneDir = new File(clonePath);
		if (!cloneDir.exists()) {
			cloneDir.mkdirs();
		}
		return clonePath;
	}

	public static String getCloneResultPath(String path, String codeVersion) {
		path = StringUtils.replace(path, "\\", "/");
		return path + OUTPUT + "/" + codeVersion + CLONE_CONFIG + CLONE_RESULT;
	}

	public static String getProgramCloneResultPath(String path, String codeVersion) {
		path = StringUtils.replace(path, "\\", "/");
		return path + OUTPUT + "/" + codeVersion + CLONE_CONFIG + PROGRAM_CLONE_RESULT;
	}

	public static String getCloneTotalLinePath(String path, String codeVersion) {
		path = StringUtils.replace(path, "\\", "/");
		return path + OUTPUT + "/" + codeVersion + CLONE_CONFIG
				+ CLONE_TOTALTILE;
	}

	public static String getClonePercentagePath(String path, String codeVersion) {
		path = StringUtils.replace(path, "\\", "/");
		return path + OUTPUT + "/" + codeVersion + CLONE_CONFIG
				+ CLONE_PERCENTAGE;
	}

	public static String getScriptPath(){
	    return SCRIPT;
	}
	
	public static String getScriptOutputPath(String path,String scriptName,String runId){
	    return path + SCRIPT_WORK_DIR + "/" +scriptName + "/" + runId + OUTPUT;
	}

	public static String getToolPath() {
		File file = new File(TOOL);
		String toolPath = file.getAbsolutePath();
		toolPath = StringUtils.replace(toolPath, "\\", "/");
		return toolPath;
	}

	public static String getAnalysisTypePath(String path) {
		path = StringUtils.replace(path, "\\", "/");
		return path + CONFIG + SELECTED_ANALYSIS_TYPE;
	}

	public static void writeFile(String filePath, String content, boolean append ) {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(filePath), append)));
			writer.write(content);
			writer.close();
		} catch (FileNotFoundException e) {
		    LOGGER.error(e);
		} catch (IOException e) {
		    LOGGER.error(e);
		}
	}

	public static String readFile(String filePath) {
		BufferedReader reader;
		StringBuffer buffer = new StringBuffer();
		try {
			reader = new BufferedReader(new FileReader(new File(filePath)));

			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				buffer.append(tempString).append("\n");
			}
			reader.close();
		} catch (FileNotFoundException e) {
		    LOGGER.error(e);
		} catch (IOException e) {
		    LOGGER.error(e);
		}
		return buffer.toString();
	}

	public static File getMenuConfigPath() {
		String filePath = SYSTEMCONFIG + MENU;
		File file = new File(filePath);
		if (file.exists()) {
			return file;
		} else {
			return null;
		}
	}

    public static File getSmtpConfigPath() {
        String filePath = SYSTEMCONFIG + SMTP;
        File file = new File(filePath);
        if (file.exists()) {
            return file;
        } else {
            return null;
        }
    }
	
	public static File getVirtualMap() {
		String filePath = SYSTEMCONFIG + VIRTUALMAP;
		File file = new File(filePath);
		if (file.exists()) {
			return file;
		} else {
			return null;
		}		
	}
	
	public static File getSummaryTabMapConfigPath() {
		String filePath = SYSTEMCONFIG + SUMMARYTABMAP;
		File file = new File(filePath);
		if (file.exists()) {
			return file;
		} else {
			return null;
		}
	}
	
	public static String getFilePathFromNodeId(String nodeId) {
		// 相对路径
		return StringUtils.substringBeforeLast(nodeId, "/");
	}
	
	public static String getPgmNameFromBlockId(String blockId) {
		// 相对路径
		return blockId.substring(blockId.lastIndexOf("/") + 1, blockId.indexOf("#"));
	}

	public static String getPathFromNodeId(String projectPath, String nodeId) {
		String path = StringUtils.replace(nodeId, "\\", "/");
		path = StringUtils.substringBeforeLast(path, "/");
		// 绝对路径
		path = projectPath + SOURCECODE + "/" + path;
		return path;
	}

	/**
	 * 得到生成的control flow文件存在目录
	 *
	 * @param projectPath
	 * @return
	 */
	public static String getControlFlowPath(String projectPath,
			String codeVersion) {
		projectPath = StringUtils.replace(projectPath, "\\", "/");
		return projectPath + OUTPUT + "/" + codeVersion + CONTROL_FLOW + "/";
	}
	
	public static String getAllProjectDocPath(String projectPath,
			String codeVersion) {
		projectPath = StringUtils.replace(projectPath, "\\", "/");
		return projectPath + OUTPUT + "/" + codeVersion + DOCUMENTATION + "/" + "all_project";
	}
	
	public static String getSingleProgramDocPath(String projectPath,
			String codeVersion) {
		projectPath = StringUtils.replace(projectPath, "\\", "/");
		return projectPath + OUTPUT + "/" + codeVersion + DOCUMENTATION + "/" + "single_program";
	}
	
	public static String getSingleGraphDocPath(String projectPath,
			String codeVersion) {
		projectPath = StringUtils.replace(projectPath, "\\", "/");
		return projectPath + OUTPUT + "/" + codeVersion + DOCUMENTATION + "/" + "single_graph";
	}
	
	public static String getWholePgmDocPath(String projectPath,
			String codeVersion) {
		projectPath = StringUtils.replace(projectPath, "\\", "/");
		return projectPath + OUTPUT + "/" + codeVersion + DOCUMENTATION + "/" + "whole_program";
	}
	
	public static String getDependencyPath(String projectPath,
			String codeVersion) {
		projectPath = StringUtils.replace(projectPath, "\\", "/");
		return projectPath + OUTPUT + "/" + codeVersion + DEPENDENCY;
	}

	public static String getFileStructureTempPath(String projectPath,
			String fileName, String codeVersion) {
		projectPath = StringUtils.replace(projectPath, "\\", "/");
		return projectPath + OUTPUT + "/" + codeVersion + FILE_STRUCTURE + "/"
				+ fileName + "_fileStrucureNodes.txt";
	}

	/**
	 * 得到graphNames.txt路径，该文件保存着graph名字和路径
	 *
	 * @param projectPath
	 * @param fileName
	 * @return
	 */

	public static String getControlFlowNamesPath(String projectPath,
			String fileName, String codeVersion) {
		return getControlFlowPath(projectPath, codeVersion) + "/" + fileName
				+ "/graphNames.txt";
	}

	/**
	 * 从文件中读取
	 *
	 * @param projectPath
	 * @param fileName
	 * @return
	 */
	public static List<ControlFlowDto> getGraphNames(String projectPath,
			String fileName, String codeVersion) {
		String filePath = getControlFlowPath(projectPath, codeVersion) + "/"
				+ fileName + "/graphNames.txt";
		List<ControlFlowDto> controlFlows = new ArrayList<ControlFlowDto>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(filePath)));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				String name = StringUtils.substringBefore(tempString, "||");
				String path = StringUtils
						.replace(StringUtils.substringAfter(tempString, "||"),
								"\\", "/");
				String fileString = FileUtils.readFileToString(new File(path));
				String svgContent = fileString.substring(fileString
						.indexOf("<svg"));
				controlFlows.add(new ControlFlowDto(name, path, svgContent));
			}
		} catch (FileNotFoundException e) {
		    LOGGER.error(e);
		} catch (IOException e) {
		    LOGGER.error(e);
		}finally{
		    try {
                reader.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                LOGGER.error(e);
            }
		}
		Collections.sort(controlFlows);
		ControlFlowDto[] result = new ControlFlowDto[controlFlows.size()];
		int index = 1;
		for (ControlFlowDto controlFlow : controlFlows) {
			if (controlFlow.getGraphName().equals(fileName)) {
				result[0] = controlFlow;
			} else {
				result[index] = controlFlow;
				index++;
			}
		}
		return Arrays.asList(result);
	}

	public static String getTempPath() {
		File file = new File(TEMP);
		if(file.exists()){
			return file.getAbsolutePath();
		}else{
			file.mkdirs();
			return file.getAbsolutePath();
		}
	}

	public static <T> List<T> readJson(String filePath, Class<T> clazz) {
		List<T> results = new ArrayList<T>();
		File file = new File(filePath);
		if (!file.exists()) {
			return results;
		} else {
			String jsonStr = null;
			try {
				jsonStr = FileUtils.readFileToString(file);
				results = JSONArray.parseArray(jsonStr, clazz);
			} catch (IOException e) {
				LOGGER.error(e);
				return results;
			}
		}
		return results;
	}

	public static <T> void writeJson(String filePath, List<T> results,
			Class<T> clazz) {
		File file = new File(filePath);
		try {
			if (!file.exists()) {
				// 文件不存在时，new
				file.createNewFile();
			}
			String jsonStr = JSON.toJSONString(results);
			FileUtils.write(file, jsonStr, false);
		} catch (IOException e) {
		    LOGGER.error(e);
		}
	}

    public static String getScriptDesciption(String scriptName) {
        // TODO Auto-generated method stub
        return getScriptPath() + "/" + scriptName + SCRIPT_DESCRITION;
    }
    
    public static Properties getProperties() {
        Properties properties = new Properties();
        // 使用InPutStream流读取properties文件
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(PROPERTITES));
            properties.load(bufferedReader);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            LOGGER.error(e);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            LOGGER.error(e);
        }
        return properties;
    }
    
    public static List<String> scanDirectory(String path) {
        List<String> result = new ArrayList<>();
        File directory = new File(path);
        if (!directory.isDirectory())
            return result;
        File[] fileLists = directory.listFiles();
        for (int i = 0; i < fileLists.length; i++) {
            result.add(fileLists[i].getName());
        }
        return result;
    }
    
    public static void download(File file,HttpServletResponse response) throws FileNotFoundException, IOException {
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource Not Found");
            return;
        }
        boolean isFolder = file.isDirectory();
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        response.setContentType(mimeType);
        if(!isFolder){
            response.setHeader("Content-disposition", String.format("attachment; filename=\"%s\"", URLEncoder.encode(file.getName(), "UTF-8")));
            try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
                FileCopyUtils.copy(inputStream, response.getOutputStream());
                inputStream.close();
            }
        }else {
            response.setHeader("Content-disposition", String.format("attachment; filename=\"%s\"", URLEncoder.encode(file.getName()+".zip", "UTF-8")));
            try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()))) {
                CompressUtils.toZip(zos,"", file);
                response.flushBuffer();
                zos.close();
            }
        }
    }
}
