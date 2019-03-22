package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DependencyDto implements Serializable {

	private String programId;
	private String jsonString;
	private String jsonFilePath;

	public DependencyDto() {
		super();
	}

	public DependencyDto(String programId, String jsonString, String jsonFilePath) {
		super();
		this.programId = programId;
		this.jsonString = jsonString;
		this.jsonFilePath = jsonFilePath;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	public String getJsonFilePath() {
		return jsonFilePath;
	}

	public void setJsonFilePath(String jsonFilePath) {
		this.jsonFilePath = jsonFilePath;
	}

}
