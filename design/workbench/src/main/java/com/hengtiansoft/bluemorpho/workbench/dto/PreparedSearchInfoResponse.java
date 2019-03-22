package com.hengtiansoft.bluemorpho.workbench.dto;

import java.util.ArrayList;
import java.util.List;

public class PreparedSearchInfoResponse {
	public String size = "10";
	public String projectPath;
	public List<String> addFiles = new ArrayList<>();
	public List<String> deleteFiles = new ArrayList<>();
	public String outputPath;
	public String flag;
	public String organization;
	public String businessDomain;
	public String system;
	public String type;
	public String dburl;
	public String rootPath;
	
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}

	public String getProjectPath() {
		return projectPath;
	}
	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}
	public List<String> getAddFiles() {
		return addFiles;
	}
	public void setAddFiles(List<String> addFiles) {
		this.addFiles = addFiles;
	}
	public List<String> getDeleteFiles() {
		return deleteFiles;
	}
	public void setDeleteFiles(List<String> deleteFiles) {
		this.deleteFiles = deleteFiles;
	}
	public String getOutputPath() {
		return outputPath;
	}
	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	public String getBusinessDomain() {
		return businessDomain;
	}
	public void setBusinessDomain(String businessDomain) {
		this.businessDomain = businessDomain;
	}
	public String getSystem() {
		return system;
	}
	public void setSystem(String system) {
		this.system = system;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDburl() {
		return dburl;
	}
	public void setDburl(String dburl) {
		this.dburl = dburl;
	}
	public String getRootPath() {
		return rootPath;
	}
	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
}
