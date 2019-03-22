package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date Jun 19, 2018
 */
public class FileDetailItem implements Serializable {

	private static final long serialVersionUID = 1L;
	private String nodeId;
	private String name;
	private String type;
	private String comment;
	private String cpyName;

	public FileDetailItem() {
		super();
	}

	public FileDetailItem(String nodeId, String name, String type) {
		super();
		this.nodeId = nodeId;
		this.name = name;
		this.type = type;
	}

	public FileDetailItem(String nodeId, String name, String type, String cpyName) {
		super();
		this.nodeId = nodeId;
		this.name = name;
		this.type = type;
		this.cpyName = cpyName;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCpyName() {
		return cpyName;
	}

	public void setCpyName(String cpyName) {
		this.cpyName = cpyName;
	}
}
