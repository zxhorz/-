package com.hengtiansoft.bluemorpho.model;

import java.io.Serializable;

/**
 * @Description: 从clonePercentage的两两paragraph clone结果中，得到每个paragraph对应的最大相似度
 * 
 * @author gaochaodeng
 * @date Aug 13, 2018
 */
public class ParagraphClone implements Serializable {

	private static final long serialVersionUID = 1L;
	// 段名
	private String name;
	// 另一段名（主要用于program clone中）
	private String otherName;
	// 相似行数
	private String cloneLines;
	// 相似度，小数形式
	private String clonePercentage;

	public ParagraphClone() {
		super();
	}

	public ParagraphClone(String name, String cloneLines, String clonePercentage) {
		super();
		this.name = name;
		this.cloneLines = cloneLines;
		this.clonePercentage = clonePercentage;
	}

	public ParagraphClone(String name, String otherName, String cloneLines,
			String clonePercentage) {
		super();
		this.name = name;
		this.otherName = otherName;
		this.cloneLines = cloneLines;
		this.clonePercentage = clonePercentage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCloneLines() {
		return cloneLines;
	}

	public void setCloneLines(String cloneLines) {
		this.cloneLines = cloneLines;
	}

	public String getClonePercentage() {
		return clonePercentage;
	}

	public void setClonePercentage(String clonePercentage) {
		this.clonePercentage = clonePercentage;
	}

	public String getOtherName() {
		return otherName;
	}

	public void setOtherName(String otherName) {
		this.otherName = otherName;
	}

}
