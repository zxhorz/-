package com.hengtiansoft.bluemorpho.workbench.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.hengtiansoft.bluemorpho.workbench.domain.CustomScriptRunHistory;
import com.hengtiansoft.bluemorpho.workbench.domain.Project;
import com.hengtiansoft.bluemorpho.workbench.domain.Script;
import com.hengtiansoft.bluemorpho.workbench.domain.ScriptHistoryDetail;
import com.hengtiansoft.bluemorpho.workbench.dto.CustomScriptRunHistoryResult;
import com.hengtiansoft.bluemorpho.workbench.enums.ScriptProcessStatus;
import com.hengtiansoft.bluemorpho.workbench.neo4j.Neo4jServerPool;
import com.hengtiansoft.bluemorpho.workbench.repository.CustomScriptRunHistoryRepository;
import com.hengtiansoft.bluemorpho.workbench.repository.ProjectRepository;
import com.hengtiansoft.bluemorpho.workbench.util.FilePathUtil;
import com.hengtiansoft.bluemorpho.workbench.util.FileStatusUtil;
import com.hengtiansoft.bluemorpho.workbench.util.PortUtil;
import com.hengtiansoft.bluemorpho.workbench.util.ProcessBuilderUtil;
import com.hengtiansoft.bluemorpho.workbench.websocket.CustomScriptWebSocket;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Sep 25, 2018 1:43:36 PM
 */
@Service
public class CustomScriptService {
    private static final Logger LOGGER = Logger.getLogger(CustomScriptService.class);
    
	public static final String SCRIPT_WORK_DIR = "/script_workdirs";
	public static final String SCRIPT_OUTPUT_DIR = "/output";
	public static final String SCRIPT_TEMP_DIR = "/temp";
	public static final String CONSOLELOG = "/console.log";
//	public static final String LOG_DIR = "/log";
	@Autowired
	private ProjectService projectService;
	@Autowired
	private JobService jobService;
	@Autowired
	private CustomScriptRunHistoryRepository customScriptRunHistoryRepository;
	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private PortUtil portUtil;
	@Autowired
	private CustomScriptWebSocket customScriptWebSocket;
	@Autowired
	Neo4jServerPool pool;   
	
	public String addScriptHistory(int projectId, String scriptName, String commandLineOptions) {
		String scriptDir = FilePathUtil.getScriptPath();
		String scriptPath = scriptDir + "/" + scriptName;
		// create script work dir
		if(!jobService.getSpecJobStatus(String.valueOf(projectId),"SO").equals("S"))
		    return null;
		String finds = customScriptRunHistoryRepository.findByLastScript(String.valueOf(projectId));
        Project project = projectRepository.findOne(String.valueOf(projectId));
        String codeVersion = FileStatusUtil.checkCode(project.getPath());

        String runId = finds;
        Integer numId;

        if (runId != null) {
            String codeVersionId = runId.split("_")[1];
            if (!codeVersion.equals(codeVersionId))
                numId = 0;
            else
                numId = Integer.valueOf(runId.split("_")[2]);
        } else {
            numId = 0;
        }
        numId++;
		String newRunId = projectId + "_" + codeVersion + "_" +numId;
		CustomScriptRunHistory history = new CustomScriptRunHistory(newRunId,scriptName, scriptPath, 
				commandLineOptions, String.valueOf(projectId), null, null, ScriptProcessStatus.NS.toString(), null);
		customScriptRunHistoryRepository.save(history);
		return newRunId;
	}
	
    public String runScript(String runId) {
        setWebSocketInProcessBuilderUtil();
        CustomScriptRunHistory find = customScriptRunHistoryRepository.findByRunId(runId);
        String basedProjectId = find.getBasedProjectId();

        Project project = projectRepository.findOne(basedProjectId);
        if (project != null) {
            String codeVersion = FileStatusUtil.checkCode(project.getPath());
            String databasePath = FilePathUtil.getNeo4jPath(project.getPath(), codeVersion);
            File db = new File(databasePath);
            if (!db.exists()) {
                return "No neo4j path existed";
            } else {
                //打开neo4j
                pool.getAndCreateDB(project.getPath(), codeVersion);

                String scriptName = find.getScriptName();
                String commandLineOptions = find.getCommandLineOptions();

                String projectPath = projectRepository.findOne(String.valueOf(basedProjectId)).getPath();
                String scriptWorkDir = projectPath.replace("\\", "/") + SCRIPT_WORK_DIR + "/" +scriptName + "/" + runId;
                File workDir = new File(scriptWorkDir);
                if (!workDir.exists()) {
                    workDir.mkdirs();
                }

                // set environment variables
                // int exitValue1 = -1;
                // String boltUrl = portUtil.getBoltUrl(basedProjectId);
                // if (boltUrl != "") {
                // String cmd1 = "setx SO_DB_URL " + boltUrl;
                // exitValue1 = ProcessBuilderUtil.processBuilderForSetEnviVars(cmd1);
                // }
                // String cmd2 = "setx SRC_DIR " + projectPath + FilePathUtil.SOURCECODE;
                // String cmd3 = "setx OUTPUT_DIR " + scriptWorkDir + SCRIPT_OUTPUT_DIR;
                // String cmd4 = "setx TEMP_DIR " + scriptWorkDir + SCRIPT_TEMP_DIR;
                //
                // int exitValue2 = ProcessBuilderUtil.processBuilderForSetEnviVars(cmd2);
                // int exitValue3 = ProcessBuilderUtil.processBuilderForSetEnviVars(cmd3);
                // int exitValue4 = ProcessBuilderUtil.processBuilderForSetEnviVars(cmd4);
                //
                // if ((boltUrl == "" || (boltUrl != "" && exitValue1 == 0))
                // && exitValue2 == 0
                // && exitValue3 == 0
                // && exitValue4 == 0) {

                // environment variables
                Map<String, String> envMap = new HashMap<String, String>();
                String boltUrl = portUtil.getBoltUrl(basedProjectId);
                envMap.put("SO_DB_URL", boltUrl);
                envMap.put("SRC_DIR", projectPath + FilePathUtil.SOURCECODE);
                File outputDir = new File(scriptWorkDir + SCRIPT_OUTPUT_DIR);
                if (!outputDir.exists()) {
                	outputDir.mkdirs();
                }
                envMap.put("OUTPUT_DIR", scriptWorkDir + SCRIPT_OUTPUT_DIR);
                envMap.put("TEMP_DIR", scriptWorkDir + SCRIPT_TEMP_DIR);

                // run script
                List<String> cmd = buildCommandForCustomScript(scriptName, commandLineOptions);
                find.setStatus(ScriptProcessStatus.P.toString());
                customScriptRunHistoryRepository.save(find);
                find.setStartTime(new Date());
                int code = ProcessBuilderUtil.processBuilderForCustomScript(cmd, envMap, scriptName,
                        FilePathUtil.getScriptPath(), scriptWorkDir + SCRIPT_OUTPUT_DIR, runId, projectPath);
                if (code == 0) {
                    find.setStatus(ScriptProcessStatus.S.toString());
                    find.setStopTime(new Date());
                    customScriptRunHistoryRepository.save(find);
                    return "custom script was successfully run.";
                } else {
                    find.setStatus(ScriptProcessStatus.F.toString());
                    customScriptRunHistoryRepository.save(find);
                    return "exception";
                }
                // } else {
                // return "set environment variables error.";
                // }
            }
        } else {
            return "Project is not existed";
        }
    }
	
	private List<String> buildCommandForCustomScript(String scriptName, String commandLineOptions) {
		List<String> cmd = new ArrayList<String>();
		String suffix = scriptName.substring(scriptName.lastIndexOf(".") + 1);
		if ("py".equals(suffix)) {
			cmd.add("python");
			cmd.add(scriptName);
		} else if ("jar".equals(suffix)) {
			cmd.add("java");
			cmd.add("-jar");
			cmd.add(scriptName);
		} else {
			return cmd;
		}
		String[] args = commandLineOptions.split("\\s+");
		for (String arg : args) {
			cmd.add(arg.trim());
		}
		return cmd;
	}

	public List<String> getOutputList(String runId) {
		List<String> outputs = new ArrayList<String>();
		CustomScriptRunHistory history = customScriptRunHistoryRepository.findByRunId(runId);
		String basedProjectId = history.getBasedProjectId();
		String scriptName =  history.getScriptName();
		String projectPath = projectRepository.findOne(basedProjectId).getPath();
		String scriptWorkDir = projectPath.replace("\\", "/") + SCRIPT_WORK_DIR + "/" +scriptName + "/" + runId;
		File output = new File(scriptWorkDir + "/output.zip");
		if(!output.exists()){
		    return outputs;
		}
		outputs.add(output.getName());
		return outputs;
	}
	
    public Page<Script> getScriptList(int page, int size) {
        // TODO Auto-generated method stub
        PageRequest pageRequest = new PageRequest(page - 1, size);
        
        List<Script> content = new ArrayList<Script>();
        List<Script> scriptList = new ArrayList<Script>();
        File directory = new File(FilePathUtil.getScriptPath());
        if(!directory.exists()){
            directory.mkdirs();
        }
        File[] fileLists = directory.listFiles();
        for (int i = 0; i < fileLists.length; i++) {
            String scriptName = fileLists[i].getName();
            String scriptDescription = getScriptDescription(scriptName);

            Script script = new Script(scriptName, scriptDescription);
            if(fileLists[i].isDirectory())
                scriptList.add(script);
        }
        int totalSize = scriptList.size();
        int skip = (page - 1) * size;
        if( skip > totalSize)
            return new PageImpl<Script>(content, pageRequest, totalSize); 

        int contentSize=0;
        for (int i = skip; i < totalSize; i++) {
            if (contentSize < size) {
                Script item = scriptList.get(i);
                content.add(item);
                contentSize++;
            } else {
                break;
            }
        }
        return new PageImpl<Script>(content, pageRequest, totalSize);

    }

    public String scriptLaunch(String scriptName) {
        // TODO Auto-generated method stub
        String scriptDescription = getScriptDescription(scriptName);
        return scriptDescription;
    }
    
    public String getScriptDescription(String scriptName){
        StringBuilder scriptDescription = new StringBuilder();

        // 读取脚本描述
        try {
            File file = new File(FilePathUtil.getScriptDesciption(scriptName));
            if(!file.exists())
                return "";
            FileReader reader = new FileReader(file);
            BufferedReader bReader = new BufferedReader(reader);
            String s = "";
            while ((s = bReader.readLine()) != null) {
                scriptDescription.append(s + "\n");
                // System.out.println(s);
            }
            bReader.close();
        } catch (IOException e) {
        	LOGGER.error(e);
        }
        return scriptDescription.toString();
    }

    public List<CustomScriptRunHistoryResult> scriptHistory() {
        // TODO Auto-generated method stub
        List<Project> projects = projectService.getProjectList();
        List<String> projectId = new ArrayList<String>();
        for (Project project : projects) {
            projectId.add(project.getId());
        }
        Iterable<CustomScriptRunHistory> iterable = customScriptRunHistoryRepository.findByProjectId(projectId);
        List<CustomScriptRunHistory> customScriptRunHistory = new ArrayList<CustomScriptRunHistory>();
        iterable.forEach(single -> {
            if (single.getRunId().split("_").length == 3)
                customScriptRunHistory.add(single);
        });

        List<CustomScriptRunHistoryResult> customScriptRunHistoryResult = new ArrayList<CustomScriptRunHistoryResult>();
        for (CustomScriptRunHistory c : customScriptRunHistory) {
            String name = projectRepository.findByProjectId(c.getBasedProjectId()).getName();
            customScriptRunHistoryResult.add(new CustomScriptRunHistoryResult(c, name));
        }
        return customScriptRunHistoryResult;
    }

    /**
	 * due to the jobs can not get the instance from
	 * IOC container(quartz create the job instance
	 * by Java reflection), so the ProcessBuilderUtil
	 * used in job definition also can not be registered
	 * in IOC container, and the websocket instance
	 * used in ProcessBuilderUtil class should defined
	 * as static and get the instance here.
	 */
	private void setWebSocketInProcessBuilderUtil() {
		if (ProcessBuilderUtil.customScriptWebSocket == null) {
			ProcessBuilderUtil.customScriptWebSocket = this.customScriptWebSocket;
		}
	}

    public String scriptOutputOpen(String runId, String fileName) {
        // TODO Auto-generated method stub
        CustomScriptRunHistory find = customScriptRunHistoryRepository.findByRunId(runId);
        String basedProjectId = find.getBasedProjectId();
        String scriptName = find.getScriptName();
        String projectPath = projectRepository.findOne(String.valueOf(basedProjectId)).getPath();
        String scriptWorkDir = projectPath.replace("\\", "/") + SCRIPT_WORK_DIR + "/" +scriptName + "/" + runId;
        String filePath = scriptWorkDir + "/" + fileName;
        File file = new File(filePath);
        if(!file.exists())
            return "file not found";
        String cmd = fileName;
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
        	LOGGER.error(e);
            return "error";
        }
        return "open successfully";
    }

    public void deleteScriptHistory(String runId) {
        // TODO Auto-generated method stub
        //删除本地文件
        CustomScriptRunHistory find = customScriptRunHistoryRepository.findByRunId(runId);
        String newRunId = find.getRunId()+"_D";
        find.setRunId(newRunId);
        customScriptRunHistoryRepository.save(find);
//        String basedProjectId = find.getBasedProjectId();
//        String scriptName = find.getScriptName();
//        String projectPath = projectRepository.findOne(String.valueOf(basedProjectId)).getPath();
//        String scriptWorkDir = projectPath.replace("\\", "/") + SCRIPT_WORK_DIR + "/" +scriptName + "/" + runId;
//        try {
//            FileUtils.deleteDirectory(new File(scriptWorkDir));
//        } catch (IOException e) {
//        	LOGGER.error(e);
//        }
//        customScriptRunHistoryRepository.deleteByRunId(runId);
    }

    public ScriptHistoryDetail scriptHistoryDetail(String runId) {
        // TODO Auto-generated method stub
        CustomScriptRunHistory find = customScriptRunHistoryRepository.findByRunId(runId);

        String scriptName = find.getScriptName();
        String basedProjectId = find.getBasedProjectId();
        String projectPath = projectRepository.findOne(String.valueOf(basedProjectId)).getPath();
        String scriptWorkDir = projectPath.replace("\\", "/") + SCRIPT_WORK_DIR + "/" +scriptName + "/" + runId;
        String scriptLogDir = scriptWorkDir + SCRIPT_OUTPUT_DIR;
        
        String commandLineOptions = find.getCommandLineOptions();
        String log="";
        List<String> outPuts = getOutputList(runId);
        File file = new File(scriptLogDir + CONSOLELOG);
        if (!file.exists())
            return new ScriptHistoryDetail(scriptName, commandLineOptions, log, outPuts);
        try {
//            FileReader reader = new FileReader(file);
//            BufferedReader bReader = new BufferedReader(reader);
//            String s = "";
//            while ((s = bReader.readLine()) != null) {
//                log = log + s + "\n";
//            }
//            bReader.close();
        	log = FileUtils.readFileToString(file);
        } catch (IOException e) {
        	LOGGER.error(e);
        }

        return new ScriptHistoryDetail(scriptName, commandLineOptions, log, outPuts);

    }
    
    public void projectIdToName(List<CustomScriptRunHistory> customScriptRunHistories){
        for(int i=0;i<customScriptRunHistories.size();i++){
            String name = projectRepository.findByProjectId(customScriptRunHistories.get(i).getBasedProjectId()).getName();
            customScriptRunHistories.get(i).setBasedProjectId(name);
        }
    }

    public String downloadFile(String runId, String fileName) {
        // TODO Auto-generated method stub
        CustomScriptRunHistory find = customScriptRunHistoryRepository.findByRunId(runId);
        String basedProjectId = find.getBasedProjectId();
        String scriptName = find.getScriptName();
        String projectPath = projectRepository.findOne(String.valueOf(basedProjectId)).getPath();
        String scriptWorkDir = projectPath.replace("\\", "/") + SCRIPT_WORK_DIR + "/" +scriptName + "/" + runId;
        String filePath = scriptWorkDir + "/" + fileName;
        
        return filePath;
    }

    public boolean deleteScript(String scriptName) {
        // TODO Auto-generated method stub
        File script = new File(FilePathUtil.getScriptPath() + "/" + scriptName);
        try{
        FileUtils.deleteDirectory(script);
        }catch(IOException e){
        	LOGGER.error(e);
            return false;
        }
        // 目录此时为空，可以删除
        return true;
    }
}
