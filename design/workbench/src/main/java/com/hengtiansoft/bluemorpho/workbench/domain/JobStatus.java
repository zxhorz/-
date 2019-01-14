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
 * @version 创建时间：May 15, 2018 4:36:52 PM
 */
@Entity
@Table(name = "job_status")
public class JobStatus implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	private String id;

	@Column(name = "projectId")
	private String projectId;

	@Column(name = "job_name")
	private String jobName;

	@Column(name = "analysis_type_id")
	private String analysisTypeId;

	@Column(name = "is_incremental")
	private String isIncremental;
	
	@Column(name = "increment_base_version")
	private String incrementBaseVersion;
	
	@Column(name = "start_time")
	private Date startTime;

	@Column(name = "stop_time")
	private Date stopTime;

	@Column(name = "code_version")
	private String codeVersion;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "description")
	private String description;
	
	public JobStatus() {
		super();
	}

	public JobStatus(String projectId, String jobName, String analysisTypeId,
			Date startTime, Date stopTime, String codeVersion, String status, String description) {
		super();
		this.projectId = projectId;
		this.jobName = jobName;
		this.analysisTypeId = analysisTypeId;
		this.startTime = startTime;
		this.stopTime = stopTime;
		this.codeVersion = codeVersion;
		this.status = status;
		this.description = description;
	}
	
	public JobStatus(String projectId, String jobName, String analysisTypeId,
			String isIncremental, String incrementBaseVersion, Date startTime,
			Date stopTime, String codeVersion, String status, String description) {
		super();
		this.projectId = projectId;
		this.jobName = jobName;
		this.analysisTypeId = analysisTypeId;
		this.isIncremental = isIncremental;
		this.incrementBaseVersion = incrementBaseVersion;
		this.startTime = startTime;
		this.stopTime = stopTime;
		this.codeVersion = codeVersion;
		this.status = status;
		this.description = description;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getAnalysisTypeId() {
		return analysisTypeId;
	}

	public void setAnalysisTypeId(String analysisTypeId) {
		this.analysisTypeId = analysisTypeId;
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

	public String getCodeVersion() {
		return codeVersion;
	}

	public void setCodeVersion(String codeVersion) {
		this.codeVersion = codeVersion;
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

	public String getIsIncremental() {
		return isIncremental;
	}

	public void setIsIncremental(String isIncremental) {
		this.isIncremental = isIncremental;
	}

	public String getIncrementBaseVersion() {
		return incrementBaseVersion;
	}

	public void setIncrementBaseVersion(String incrementBaseVersion) {
		this.incrementBaseVersion = incrementBaseVersion;
	}

}
