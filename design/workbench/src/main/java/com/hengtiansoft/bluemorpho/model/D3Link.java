package com.hengtiansoft.bluemorpho.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class D3Link implements Serializable {

	private String source;
	private String target;
	private String value;

	public D3Link() {
		super();
	}

	public D3Link(String source, String target, String value) {
		super();
		this.source = source;
		this.target = target;
		this.value = value;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
