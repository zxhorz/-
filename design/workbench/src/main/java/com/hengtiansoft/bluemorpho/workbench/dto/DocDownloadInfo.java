package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DocDownloadInfo implements Serializable {

	private String projectId;
	private String programName;
	private String base64Str;
	private String controlFlowName;
	private DependencyGraphDto[] dependencies;
	private String filePath;

	public DocDownloadInfo() {
		super();
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getBase64Str() {
		return base64Str;
	}

	public void setBase64Str(String base64Str) {
		this.base64Str = base64Str;
	}

	public String getControlFlowName() {
		return controlFlowName;
	}

	public void setControlFlowName(String controlFlowName) {
		this.controlFlowName = controlFlowName;
	}

	public DependencyGraphDto[] getDependencies() {
		return dependencies;
	}

	public void setDependencies(DependencyGraphDto[] dependencies) {
		this.dependencies = dependencies;
	}

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

	
	
}
