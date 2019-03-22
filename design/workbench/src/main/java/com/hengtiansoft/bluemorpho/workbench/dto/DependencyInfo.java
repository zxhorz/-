package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class DependencyInfo implements Serializable{
	
	private String projectId;
	private List<String> selectedName;
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public List<String> getSelectedName() {
		return selectedName;
	}
	public void setSelectedName(List<String> selectedName) {
		this.selectedName = selectedName;
	}
	
}
