package com.hengtiansoft.bluemorpho.workbench.quartz.job.datastructure;


/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 7, 2018 10:20:25 AM
 */
public class JobInfo {

	private String projectId;
	private String analysisTypeId;
	private String analysisName;
	// 是否是增量分析
	private boolean isIncremental = false;
	private String incrementBaseVersion;
	// projectId_userId_requestReceivedTime
	private String jobGroup;
	// projectId_userId_analysisTypeId_requestReceivedTime
	private String jobName;
	private String codeVersion;
	private String userId;
	// 标记当一个分析结点有多个父节点时，
	// 其是否由'所有父节点完成分析'事件而触发过自身分析。
	// 避免错误情况：多个父节点线程都完成，各自判断时，触发当前结点进行多次分析
	private boolean triggeredByParent = false;
	
	public JobInfo() {
	}

	public JobInfo(String projectId, String analysisTypeId, String analysisName, String jobGroup,
			String jobName, String codeVersion) {
		super();
		this.projectId = projectId;
		this.analysisTypeId = analysisTypeId;
		this.analysisName = analysisName;
		this.jobGroup = jobGroup;
		this.jobName = jobName;
		this.codeVersion = codeVersion;
	}
	
	public JobInfo(String projectId, String analysisTypeId, String analysisName, 
			boolean isIncremental, String incrementBaseVersion, String jobGroup,
			String jobName, String codeVersion) {
		super();
		this.projectId = projectId;
		this.analysisTypeId = analysisTypeId;
		this.analysisName = analysisName;
		this.isIncremental = isIncremental;
		this.incrementBaseVersion = incrementBaseVersion;
		this.jobGroup = jobGroup;
		this.jobName = jobName;
		this.codeVersion = codeVersion;
	}

	public String getAnalysisTypeId() {
		return analysisTypeId;
	}

	public void setAnalysisTypeId(String analysisTypeId) {
		this.analysisTypeId = analysisTypeId;
	}

	public String getAnalysisName() {
		return analysisName;
	}

	public void setAnalysisName(String analysisName) {
		this.analysisName = analysisName;
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getCodeVersion() {
		return codeVersion;
	}

	public void setCodeVersion(String codeVersion) {
		this.codeVersion = codeVersion;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isTriggeredByParent() {
		return triggeredByParent;
	}

	public void setTriggeredByParent(boolean triggeredByParent) {
		this.triggeredByParent = triggeredByParent;
	}

	public boolean isIncremental() {
		return isIncremental;
	}

	public void setIncremental(boolean isIncremental) {
		this.isIncremental = isIncremental;
	}

	public String getIncrementBaseVersion() {
		return incrementBaseVersion;
	}

	public void setIncrementBaseVersion(String incrementBaseVersion) {
		this.incrementBaseVersion = incrementBaseVersion;
	}
	
}
