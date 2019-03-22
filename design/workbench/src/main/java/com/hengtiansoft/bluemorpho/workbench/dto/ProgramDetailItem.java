package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date Jun 7, 2018
 */
public class ProgramDetailItem implements Serializable {
	private static final long serialVersionUID = 1L;
	private String nodeId;
	private String name;
	private String type;
	private String lines;
	private String complexity;
	private String clonePercentage;
	private String tags;
	private String location;

	public ProgramDetailItem() {
		super();
	}

	public ProgramDetailItem(String nodeId, String name, String location) {
		super();
		this.nodeId = nodeId;
		this.name = name;
		this.location = location;
	}

	public ProgramDetailItem(String nodeId, String name, String location,
			String lines) {
		super();
		this.nodeId = nodeId;
		this.name = name;
		this.location = location;
		this.lines = lines;
	}

	public ProgramDetailItem(String nodeId, String name, String type,
			String lines, String tags, String location) {
		super();
		this.nodeId = nodeId;
		this.name = name;
		this.type = type;
		this.lines = lines;
		this.tags = tags;
		this.location = location;
	}

	public ProgramDetailItem(String nodeId, String name, String type,
			String lines, String tags, String location, String complexity,
			String clone) {
		super();
		this.nodeId = nodeId;
		this.name = name;
		this.type = type;
		this.lines = lines;
		this.tags = tags;
		this.location = location;
		this.complexity = complexity.equals("0") ? "" : complexity;
		this.clonePercentage = clone;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
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

	public String getLines() {
		return lines;
	}

	public void setLines(String lines) {
		this.lines = lines;
	}

	public String getComplexity() {
		return complexity;
	}

	public void setComplexity(String complexity) {
		this.complexity = complexity;
	}

	public String getClonePercentage() {
		return clonePercentage;
	}

	public void setClonePercentage(String clonePercentage) {
		this.clonePercentage = clonePercentage;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
