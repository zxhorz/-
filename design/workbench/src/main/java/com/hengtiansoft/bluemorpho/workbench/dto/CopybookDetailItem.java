package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date Jun 8, 2018
 */
public class CopybookDetailItem implements Serializable {
	private static final long serialVersionUID = 1L;
	private String nodeId;
	private String cpyName;
	private String cobolName;
	private String type;
	// copybook 文件所在目录，用于查看源码
	private String location;
	private String comment;
	private String tags;

	public CopybookDetailItem() {
		super();
	}

	public CopybookDetailItem(String nodeId, String cpyName, String type,
			String location, String tags) {
		super();
		this.nodeId = nodeId;
		this.cpyName = cpyName;
		if ("D".equals(type)) {
			this.type = "Data";
		} else if ("T".equals(type)) {
			this.type = "Table";
		} else if ("C".equals(type)) {
			this.type = "Code";
		}
		this.location = location;
		this.tags = tags;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getCpyName() {
		return cpyName;
	}

	public void setCpyName(String cpyName) {
		this.cpyName = cpyName;
	}

	public String getCobolName() {
		return cobolName;
	}

	public void setCobolName(String cobolName) {
		this.cobolName = cobolName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

}
