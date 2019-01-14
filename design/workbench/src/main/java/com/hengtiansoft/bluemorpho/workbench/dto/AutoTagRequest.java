package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: auto tag 请求信息
 * @author gaochaodeng
 * @date Aug 22, 2018
 */
public class AutoTagRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	private String projectId;
	private List<AutoTagResult> params = new ArrayList<AutoTagResult>();
	private String neo4jUri;
	private String outputPath;
	private String organization;
	private String businesDomain;
	private String system;

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public List<AutoTagResult> getParams() {
		return params;
	}

	public void setParams(List<AutoTagResult> params) {
		this.params = params;
	}

	public String getNeo4jUri() {
		return neo4jUri;
	}

	public void setNeo4jUri(String neo4jUri) {
		this.neo4jUri = neo4jUri;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
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

}
