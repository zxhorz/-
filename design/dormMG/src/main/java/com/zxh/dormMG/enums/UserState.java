package com.zxh.dormMG.enums;


public enum UserState {

	ACTIVE("active"), NON_ACTIVE("nonactive");
	private String state;

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	UserState(String state) {
		this.state = state;
	}
	
}
