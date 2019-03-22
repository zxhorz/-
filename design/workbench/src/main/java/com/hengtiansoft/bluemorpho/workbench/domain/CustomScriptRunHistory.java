package com.hengtiansoft.bluemorpho.workbench.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Sep 25, 2018 2:21:19 PM
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "custom_script_run_history")
public class CustomScriptRunHistory implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	private String id;

	@Column(name = "run_id")
	private String runId;
	
	@Column(name = "script_name")
	private String scriptName;

	@Column(name = "script_path")
	private String scriptPath;

	@Column(name = "command_line_options")
	private String commandLineOptions;

	@Column(name = "based_projectId")
	private String basedProjectId;
	
	@Column(name = "start_time")
	private Date startTime;

	@Column(name = "stop_time")
	private Date stopTime;

	@Column(name = "status")
	private String status;

	@Column(name = "description")
	private String description;

	public CustomScriptRunHistory() {
		super();
	}

	public CustomScriptRunHistory(String scriptName, String scriptPath,
			String commandLineOptions, String basedProjectId, Date startTime,
			Date stopTime, String status, String description) {
		super();
		this.scriptName = scriptName;
		this.scriptPath = scriptPath;
		this.commandLineOptions = commandLineOptions;
		this.basedProjectId = basedProjectId;
		this.startTime = startTime;
		this.stopTime = stopTime;
		this.status = status;
		this.description = description;
	}
	
    public CustomScriptRunHistory(String runId,String scriptName, String scriptPath,
            String commandLineOptions, String basedProjectId, Date startTime,
            Date stopTime, String status, String description) {
        super();
        this.runId = runId;
        this.scriptName = scriptName;
        this.scriptPath = scriptPath;
        this.commandLineOptions = commandLineOptions;
        this.basedProjectId = basedProjectId;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.status = status;
        this.description = description;
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

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getStopTime() {
		return stopTime;
	}

	public void setStopTime(Date stopTime) {
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

	public String getBasedProjectId() {
		return basedProjectId;
	}

	public void setBasedProjectId(String basedProjectId) {
		this.basedProjectId = basedProjectId;
	}

}
