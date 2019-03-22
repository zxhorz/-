package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * @Description: auto tag feedback
 * @author gaochaodeng
 * @date Sep 4, 2018
 */
public class AutoTagFeedback implements Serializable {

	private static final long serialVersionUID = 1L;
	private String projectId;
	// 更改的文件
	private List<AutoTagResult> updateFiles = new ArrayList<AutoTagResult>();
	// 手动打tag后的文件
	private List<AutoTagResult> updateTags = new ArrayList<AutoTagResult>();
	// 删除的文件
	private List<AutoTagResult> deleteFiles = new ArrayList<AutoTagResult>();
	private String neo4jUri;
	private String organization;
	private String businesDomain;
	private String system;
	private String projectPath;
	private String codeVersion;

	public AutoTagFeedback() {
		super();
	}

	public AutoTagFeedback(String projectId, String organization,
			String businesDomain, String system, String projectPath,
			String codeVersion) {
		super();
		this.projectId = projectId;
		this.organization = organization;
		this.businesDomain = businesDomain;
		this.system = system;
		this.projectPath = StringUtils.replace(projectPath, "\\", "/");
		this.codeVersion = codeVersion;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public List<AutoTagResult> getUpdateFiles() {
		return updateFiles;
	}

	public void setUpdateFiles(List<AutoTagResult> updateFiles) {
		this.updateFiles = updateFiles;
	}

	public List<AutoTagResult> getUpdateTags() {
		return updateTags;
	}

	public void setUpdateTags(List<AutoTagResult> updateTags) {
		this.updateTags = updateTags;
	}

	public List<AutoTagResult> getDeleteFiles() {
		return deleteFiles;
	}

	public void setDeleteFiles(List<AutoTagResult> deleteFiles) {
		this.deleteFiles = deleteFiles;
	}

	public String getNeo4jUri() {
		return neo4jUri;
	}

	public void setNeo4jUri(String neo4jUri) {
		this.neo4jUri = neo4jUri;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getBusinesDomain() {
		return businesDomain;
	}

	public void setBusinesDomain(String businesDomain) {
		this.businesDomain = businesDomain;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getProjectPath() {
		return projectPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

	public String getCodeVersion() {
		return codeVersion;
	}

	public void setCodeVersion(String codeVersion) {
		this.codeVersion = codeVersion;
	}
}
