package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: search后返回的结果
 * @author gaochaodeng
 * @date Jun 12, 2018
 */
public class SearchResult implements Serializable {
	private static final long serialVersionUID = 1L;
	private String programId;
	private String paragraphId;
	private String program;
	private String paragraph;
	private List<String> programTags = new ArrayList<String>();
	private String match_Score;
	private List<String> paragraphTags = new ArrayList<String>();
	// type =1,2,3;分别代表program, paragraphs, tables(目前没有tables)
	private String type;
	private boolean checked = false;
	private String snippet;
	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public String getParagraph() {
		return paragraph;
	}

	public void setParagraph(String paragraph) {
		this.paragraph = paragraph;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMatch_Score() {
		return match_Score;
	}

	public void setMatch_Score(String match_Score) {
		this.match_Score = match_Score;
	}

	public List<String> getProgramTags() {
		return programTags;
	}

	public void setProgramTags(List<String> programTags) {
		this.programTags = programTags;
	}

	public List<String> getParagraphTags() {
		return paragraphTags;
	}

	public void setParagraphTags(List<String> paragraphTags) {
		this.paragraphTags = paragraphTags;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public String getParagraphId() {
		return paragraphId;
	}

	public void setParagraphId(String paragraphId) {
		this.paragraphId = paragraphId;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}
}
