package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class TagInfoRequest implements Serializable{

	private String projectId;
	private List<String> selectedNames = new ArrayList<String>();
	private List<String> addTags = new ArrayList<String>();
	private List<String> deleteTags = new ArrayList<String>();
	private String type;
	private String fromPage;
	private String action;
	
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public List<String> getAddTags() {
		return addTags;
	}
	public void setAddTags(List<String> addTags) {
		this.addTags = addTags;
	}
	public List<String> getDeleteTags() {
		return deleteTags;
	}
	public void setDeleteTags(List<String> deleteTags) {
		this.deleteTags = deleteTags;
	}
	public List<String> getSelectedNames() {
		return selectedNames;
	}
	public void setSelectedNames(List<String> selectedNames) {
		this.selectedNames = selectedNames;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFromPage() {
		return fromPage;
	}
	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
}
