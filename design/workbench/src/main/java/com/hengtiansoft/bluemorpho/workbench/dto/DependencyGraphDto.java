package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DependencyGraphDto implements Serializable {

	private String programName;
	private String base64Str;

	public DependencyGraphDto() {
		super();
	}

	public DependencyGraphDto(String programName, String base64Str) {
		super();
		this.programName = programName;
		this.base64Str = base64Str;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getBase64Str() {
		return base64Str;
	}

	public void setBase64Str(String base64Str) {
		this.base64Str = base64Str;
	}

}
