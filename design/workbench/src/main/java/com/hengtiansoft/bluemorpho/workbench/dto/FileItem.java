package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date Jun 19, 2018
 */
public class FileItem implements Serializable {

	private static final long serialVersionUID = 1L;
	private String nodeId;
	private String name;
	private String tags;
	private String pgmId;
	private String pgmFileName;
	private String pgmLocation;
	private String openType;
	private String definitionStart;
	private String definitionEnd;

	public FileItem() {
		super();
	}

	public FileItem(String nodeId, String name, String tags, String openType, String definitionStart, String definitionEnd) {
		super();
		this.nodeId = nodeId;
		this.name = name;
		this.tags = tags;
		this.openType = openType;
		this.definitionStart = definitionStart;
		this.definitionEnd = definitionEnd;
	}

	public FileItem(String nodeId, String name, String tags) {
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

	public String getPgmId() {
		return pgmId;
	}

	public void setPgmId(String pgmId) {
		this.pgmId = pgmId;
	}

	public String getPgmFileName() {
		return pgmFileName;
	}

	public void setPgmFileName(String pgmFileName) {
		this.pgmFileName = pgmFileName;
	}

	public String getPgmLocation() {
		return pgmLocation;
	}

	public void setPgmLocation(String pgmLocation) {
		this.pgmLocation = pgmLocation;
	}

	public String getOpenType() {
		return openType;
	}

	public void setOpenType(String openType) {
		this.openType = openType;
	}

	public String getDefinitionStart() {
		return definitionStart;
	}

	public void setDefinitionStart(String definitionStart) {
		this.definitionStart = definitionStart;
	}

	public String getDefinitionEnd() {
		return definitionEnd;
	}

	public void setDefinitionEnd(String definitionEnd) {
		this.definitionEnd = definitionEnd;
	}
}
