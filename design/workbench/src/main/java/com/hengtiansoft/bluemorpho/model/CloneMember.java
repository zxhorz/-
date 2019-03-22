package com.hengtiansoft.bluemorpho.model;

import java.io.Serializable;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：May 23, 2018 9:32:40 PM
 */
@SuppressWarnings("serial")
public class CloneMember implements Serializable {

	private int id;
	private int line;
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
