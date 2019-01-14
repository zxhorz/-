package com.hengtiansoft.bluemorpho.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: program clone结果
 * @author gaochaodeng
 * @date Aug 13, 2018
 */
public class ProgramClone implements Serializable {
	private static final long serialVersionUID = 1L;
	// 主program name
	private String name;
	// 主program id
	private String nodeId;
	// 次program name
	private String otherName;
	// 次program id
	private String otherId;
	// 相似代码行数
	private String clone;
	// 相似度
	private String percentage;
	// 两两program中的相似paragraph对
	private List<ParagraphClone> paragraphPairs = new ArrayList<ParagraphClone>();

	public ProgramClone(String name, String nodeId, String otherName,
			String otherId) {
		super();
		this.name = name;
		this.nodeId = nodeId;
		this.otherName = otherName;
		this.otherId = otherId;
	}

	public ProgramClone() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOtherName() {
		return otherName;
	}

	public void setOtherName(String otherName) {
		this.otherName = otherName;
	}

	public List<ParagraphClone> getParagraphPairs() {
		return paragraphPairs;
	}

	public void setParagraphPairs(List<ParagraphClone> paragraphPairs) {
		this.paragraphPairs = paragraphPairs;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getOtherId() {
		return otherId;
	}

	public void setOtherId(String otherId) {
		this.otherId = otherId;
	}

	public String getClone() {
		return clone;
	}

	public void setClone(String clone) {
		this.clone = clone;
	}

	public String getPercentage() {
		return percentage;
	}

	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}
}
