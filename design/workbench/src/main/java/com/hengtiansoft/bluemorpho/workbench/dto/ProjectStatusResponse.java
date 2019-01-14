package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ProjectStatusResponse implements Serializable{
	private String projectStatus;
	private List<JobStatusResponse> jobStatus = new ArrayList<>();
	public String getProjectStatus() {
		return projectStatus;
	}
	public void setProjectStatus(String projectStatus) {
		this.projectStatus = projectStatus;
	}
	public List<JobStatusResponse> getJobStatus() {
		return jobStatus;
	}
	public void setJobStatus(List<JobStatusResponse> jobStatus) {
		this.jobStatus = jobStatus;
	}
}
