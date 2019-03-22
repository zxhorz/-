package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date Jun 7, 2018
 */
public class ParagraphDetailItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private String paragraphName;
	private String lines;
	private String complexity;
	private String clonePercentage;
	private String tags;
	private String paragraphId;
	private String programId;
	private String programName;
	// program 源代码所在路径，用于查看源码
	private String programLocation;
	private String startLine;
	private String endLine;

	public ParagraphDetailItem() {
		super();
	}

	public ParagraphDetailItem(String paragraphName, String paragraphId,
			String programId, String programName, String programLocation,
			String startLine, String endLine) {
		super();
		this.paragraphName = paragraphName;
		this.paragraphId = paragraphId;
		this.programId = programId;
		this.programName = programName;
		this.programLocation = programLocation;
		this.startLine = startLine;
		this.endLine = endLine;
	}

	public ParagraphDetailItem(String paragraphName, String lines, String tags,
			String paragraphId, String programId, String programName,
			String programLocation, String startLine, String endLine,
			int complexity, String clone) {
		super();
		this.paragraphName = paragraphName;
		this.lines = lines;
		this.tags = tags;
		this.paragraphId = paragraphId;
		this.programId = programId;
		this.programName = programName;
		this.programLocation = programLocation;
		this.startLine = startLine;
		this.endLine = endLine;
		this.complexity = String.valueOf(complexity);
		this.complexity = this.complexity.equals("0") ? "" : this.complexity;
		this.clonePercentage = clone;
	}

	public ParagraphDetailItem(String paragraphName, String lines,
			String complexity, String clonePercentage, String tags) {
		super();
		this.paragraphName = paragraphName;
		this.lines = lines;
		this.complexity = complexity;
		this.clonePercentage = clonePercentage;
		this.tags = tags;
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

	public String getParagraphId() {
		return paragraphId;
	}

	public void setParagraphId(String paragraphId) {
		this.paragraphId = paragraphId;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getParagraphName() {
		return paragraphName;
	}

	public void setParagraphName(String paragraphName) {
		this.paragraphName = paragraphName;
	}

	public String getProgramLocation() {
		return programLocation;
	}

	public void setProgramLocation(String programLocation) {
		this.programLocation = programLocation;
	}

	public String getStartLine() {
		return startLine;
	}

	public void setStartLine(String startLine) {
		this.startLine = startLine;
	}

	public String getEndLine() {
		return endLine;
	}

	public void setEndLine(String endLine) {
		this.endLine = endLine;
	}
}
