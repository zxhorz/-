package com.hengtiansoft.bluemorpho.workbench.enums;

public enum SqlOperationType {
	// 目前只记录以下四种情况
	DECLARE_CURSOR("DC"), SELECT("R"), UPDATE("U"), DELETE("D"), INSERT("C");
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private SqlOperationType(String type) {
		this.type = type;
	}
}
