package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: auto tag算法返回的tag结果
 * @author gaochaodeng
 * @date Aug 21, 2018
 */
public class AutoTagResult implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String type;
	private List<String> tags = new ArrayList<String>();

	public AutoTagResult() {
		super();
	}

	public AutoTagResult(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}

	public AutoTagResult(String name, String type, List<String> tags) {
		super();
		this.name = name;
		this.type = type;
		this.tags = tags;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

}
