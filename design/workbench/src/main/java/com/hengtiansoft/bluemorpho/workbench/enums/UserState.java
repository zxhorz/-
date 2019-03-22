package com.hengtiansoft.bluemorpho.workbench.enums;

/**
 * @author <a href="chendonghuang@hengtiansoft.com">chendonghuang</a> Date: Jan
 *         4, 2019 2:06:21 PM
 */
public enum UserState {

	ACTIVE("active"), NON_ACTIVE("nonactive");
	private String state;

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	private UserState(String state) {
		this.state = state;
	}
	
}
