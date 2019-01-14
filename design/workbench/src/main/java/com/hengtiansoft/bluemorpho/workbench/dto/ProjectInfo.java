package com.hengtiansoft.bluemorpho.workbench.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

@SuppressWarnings("serial")
public class ProjectInfo implements Serializable{
	
	@NotBlank(message = "Project is empty")
	private String projectName;
	
	@NotBlank(message = "Description is empty")
	private String description;

	private String projectId;
	
	@ApiModelProperty(value = "", required=true)
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@ApiModelProperty(value = "")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
}
