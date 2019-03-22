package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date Jun 19, 2018
 */
public class TableItem implements Serializable {

	private static final long serialVersionUID = 1L;
	private String nodeId;
	private String name;
	private String tags;

	public TableItem() {
		super();
	}

	public TableItem(String nodeId, String name, String tags) {
		super();
		this.nodeId = nodeId;
		this.name = name;
		this.tags = tags;
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

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
}
