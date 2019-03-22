package com.hengtiansoft.bluemorpho.workbench.enums;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date May 17, 2018
 */
public enum Neo4jServerStatus {
	// UPDATING暂时未使用
	INVALID(0), IDLE(1), ACTIVE(2), UPDATING(3), STOPPING(4), STOPPED(5);
	// 定义私有变量
	private int statusCode = 0;

	// 构造函数，枚举类型只能为私有
	private Neo4jServerStatus(int statusCode) {
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return this.statusCode;
	}
}
