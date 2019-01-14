package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CodeStatusResponse  implements Serializable{

	public String codeVersion;
	public String lastModifiedTime;
	public String path;
	public String getCodeVersion() {
		return codeVersion;
	}
	public void setCodeVersion(String codeVersion) {
		this.codeVersion = codeVersion;
	}
	public String getLastModifiedTime() {
		return lastModifiedTime;
	}
	public void setLastModifiedTime(String lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
}
