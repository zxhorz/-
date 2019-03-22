package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CloneDiffRequest implements Serializable{

	private String projectId;
	private String leftParaName;
	private String rightParaName;
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getLeftParaName() {
		return leftParaName;
	}
	public void setLeftParaName(String leftParaName) {
		this.leftParaName = leftParaName;
	}
	public String getRightParaName() {
		return rightParaName;
	}
	public void setRightParaName(String rightParaName) {
		this.rightParaName = rightParaName;
	}
}
