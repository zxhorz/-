package com.hengtiansoft.bluemorpho.workbench.dto;

import java.util.List;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date Jun 21, 2018
 */
public class SearchTagRequest {
	private String projectId;
	private List<String> condition;

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public List<String> getCondition() {
		return condition;
	}

	public void setCondition(List<String> condition) {
		this.condition = condition;
	}

}
