package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

/**
 * @Description: Table column info
 * @author gaochaodeng
 * @date Jun 19, 2018
 */
public class TableDetailItem implements Serializable {

	private static final long serialVersionUID = 1L;
	private String nodeId;
	private String name;
	private String type;
	private String comment;

	public TableDetailItem() {
		super();
	}

	public TableDetailItem(String nodeId, String name, String type) {
		super();
		this.nodeId = nodeId;
		this.name = name;
		this.type = type;
	}

	public TableDetailItem(String nodeId, String name, String type,
			String comment) {
		super();
		this.nodeId = nodeId;
		this.name = name;
		this.type = type;
		this.comment = comment;
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
}
