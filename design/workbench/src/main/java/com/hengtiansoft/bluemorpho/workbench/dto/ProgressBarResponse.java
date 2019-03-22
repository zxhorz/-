package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 26, 2018 3:16:27 PM
 */
@SuppressWarnings("serial")
public class ProgressBarResponse implements Serializable {
	private String jobName;
	private String analysisName;
	private boolean isIncremental = false;
	private String userName;
	private String codeVersion;
	private String startTime;
	private String status;
	// 以下进度条排序用
//	private String analysisId;
//	private int level;// 在一组分析对应的jobTree中的深度
	
	public ProgressBarResponse() {
		super();
	}

	public ProgressBarResponse(String jobName, String analysisName,
			String userName, String codeVersion, String startTime, String status) {
		super();
		this.jobName = jobName;
		this.analysisName = analysisName;
		this.userName = userName;
		this.codeVersion = codeVersion;
		this.startTime = startTime;
		this.status = status;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getAnalysisName() {
		return analysisName;
	}

	public void setAnalysisName(String analysisName) {
		this.analysisName = analysisName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCodeVersion() {
		return codeVersion;
	}

	public void setCodeVersion(String codeVersion) {
		this.codeVersion = codeVersion;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isIncremental() {
		return isIncremental;
	}

	public void setIncremental(boolean isIncremental) {
		this.isIncremental = isIncremental;
	}

//	public String getAnalysisId() {
//		return analysisId;
//	}
//
//	public void setAnalysisId(String analysisId) {
//		this.analysisId = analysisId;
//	}
//
//	public int getLevel() {
//		return level;
//	}
//
//	public void setLevel(int level) {
//		this.level = level;
//	}
	
}
