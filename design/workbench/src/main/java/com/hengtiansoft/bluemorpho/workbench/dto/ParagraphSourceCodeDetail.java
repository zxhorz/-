package com.hengtiansoft.bluemorpho.workbench.dto;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date Jun 12, 2018
 */
public class ParagraphSourceCodeDetail {
	private String sourceCode;
	private int startLine;
	private int endLine;

	public ParagraphSourceCodeDetail(String sourceCode, int startLine,
			int endLine) {
		super();
		this.sourceCode = sourceCode;
		this.startLine = startLine;
		this.endLine = endLine;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
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

}
