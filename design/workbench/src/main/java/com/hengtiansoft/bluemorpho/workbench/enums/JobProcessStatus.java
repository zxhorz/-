package com.hengtiansoft.bluemorpho.workbench.enums;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date May 17, 2018
 */
public enum JobProcessStatus {
	// keep UPDATING
	S("Successed"),F("Failed"),P("Processing"),NS("Not Start"),I("Interrupted");
	
	private String message;
	
	JobProcessStatus(String message){
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
