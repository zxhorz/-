package com.hengtiansoft.bluemorpho.workbench.enums;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Sep 25, 2018 2:35:12 PM
 */
public enum ScriptProcessStatus {
	
	S("Successed"), F("Failed"), P("Processing"), NS("Not Start");

	private String message;

	ScriptProcessStatus(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
