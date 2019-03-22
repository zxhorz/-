package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date Jun 11, 2018
 */
public class FileStructureNode implements Serializable, Cloneable {
    private static final Logger LOGGER = Logger
            .getLogger(FileStructureNode.class);
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private int startLine;
	private int endLine;
	private String divison;
	private String codeFileId;
	@JSONField(name = "pId")
	private String pid;
	private ArrayList<FileStructureNode> children;
	private int childrenCount;
	private int performStartLine;
	private int performEndLine;
	private int depth;
	private String picSize;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStartLine() {
		return startLine;
	}

	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}

	public int getEndLine() {
		return endLine;
	}

	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}

	public int getChildrenCount() {
		return childrenCount;
	}

	public void setChildrenCount(int childrenCount) {
		this.childrenCount = childrenCount;
	}

	public ArrayList<FileStructureNode> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<FileStructureNode> children) {
		this.children = children;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	/**
	 * @return Returns the codeFileId.
	 */
	public String getCodeFileId() {
		return codeFileId;
	}

	/**
	 * @param codeFileId
	 *            The codeFileId to set.
	 */
	public void setCodeFileId(String codeFileId) {
		this.codeFileId = codeFileId;
	}

	/**
	 * @return Returns the divison.
	 */
	public String getDivison() {
		return divison;
	}

	/**
	 * @param divison
	 *            The divison to set.
	 */
	public void setDivison(String divison) {
		this.divison = divison;
	}

	/**
	 * @return Returns the performStartLine.
	 */
	public int getPerformStartLine() {
		return performStartLine;
	}

	/**
	 * @param performStartLine
	 *            The performStartLine to set.
	 */
	public void setPerformStartLine(int performStartLine) {
		this.performStartLine = performStartLine;
	}

	/**
	 * @return Returns the performEndLine.
	 */
	public int getPerformEndLine() {
		return performEndLine;
	}

	/**
	 * @param performEndLine
	 *            The performEndLine to set.
	 */
	public void setPerformEndLine(int performEndLine) {
		this.performEndLine = performEndLine;
	}

	@Override
	public Object clone() {
		FileStructureNode outLineNode = null;
		try {
			outLineNode = (FileStructureNode) super.clone();
		} catch (CloneNotSupportedException e) {
			LOGGER.error(e);
		}
		return outLineNode;
	}

	/**
	 * @return Returns the depth.
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * @param depth
	 *            The depth to set.
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}

	public String getPicSize() {
		return picSize;
	}

	public void setPicSize(String picSize) {
		this.picSize = picSize;
	}
	
}
