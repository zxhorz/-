package com.hengtiansoft.bluemorpho.workbench.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.hengtiansoft.bluemorpho.workbench.domain.CustomScriptRunHistory;

public class CustomScriptRunHistoryResult {

    private String id;
    
    private String runId;

    private String scriptName;

    private String scriptPath;

    private String commandLineOptions;

    private String basedProjectId;

    private String startTime;

    private String stopTime;

    private String status;

    private String description;

    private String projectName;

    public CustomScriptRunHistoryResult() {
        super();
    }

    public CustomScriptRunHistoryResult(CustomScriptRunHistory customScriptRunHistory,String projectName){
        this.id = customScriptRunHistory.getId();
        this.runId = customScriptRunHistory.getRunId();
        this.scriptName = customScriptRunHistory.getScriptName();
        this.scriptPath = customScriptRunHistory.getScriptPath();
        this.commandLineOptions = customScriptRunHistory.getCommandLineOptions();
        this.basedProjectId = customScriptRunHistory.getBasedProjectId();
        this.status = customScriptRunHistory.getStatus();
        this.startTime = getTime(customScriptRunHistory.getStartTime());
        this.stopTime = getTime(customScriptRunHistory.getStopTime());
        this.description = customScriptRunHistory.getDescription();
        this.projectName = projectName;
    } 
    
    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public String getCommandLineOptions() {
        return commandLineOptions;
    }

    public void setCommandLineOptions(String commandLineOptions) {
        this.commandLineOptions = commandLineOptions;
    }

    public String getBasedProjectId() {
        return basedProjectId;
    }

    public void setBasedProjectId(String basedProjectId) {
        this.basedProjectId = basedProjectId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(this.status == null)
            return "Failed";
        if(this.status.equals("P"))
            return "Running";
        if(this.status.equals("F"))
            return "Failed";
        if(this.status.equals("NS"))
            return "Failed";
        if(this.status.equals("S")){
            String time = sdf.format(date);
            return time;
        }
        return "Failed";
    }   

}
