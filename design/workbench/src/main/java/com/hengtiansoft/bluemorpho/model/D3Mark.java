package com.hengtiansoft.bluemorpho.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class D3Mark implements Serializable {

	private String name;
	private String group;

	public D3Mark() {
		super();
	}

	public D3Mark(String name, String group) {
		super();
		this.name = name;
		this.group = group;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

}
