package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Sep 10, 2018 5:30:21 PM
 */
@SuppressWarnings("serial")
public class CopyBookDetail implements Serializable {

	private String cpyNodeId;
	private String name;
	private String type;
	private String tags;
	private List<ProgramDetailItem> usedInPrograms;

	public CopyBookDetail() {
		super();
	}

	public CopyBookDetail(String cpyNodeId, String name, String type,
			String tags, List<ProgramDetailItem> usedInPrograms) {
		super();
		this.cpyNodeId = cpyNodeId;
		this.name = name;
		this.type = type;
		this.tags = tags;
		this.usedInPrograms = usedInPrograms;
	}

	public String getCpyNodeId() {
		return cpyNodeId;
	}

	public void setCpyNodeId(String cpyNodeId) {
		this.cpyNodeId = cpyNodeId;
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

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public List<ProgramDetailItem> getUsedInPrograms() {
		return usedInPrograms;
	}

	public void setUsedInPrograms(List<ProgramDetailItem> usedInPrograms) {
		this.usedInPrograms = usedInPrograms;
	}

}
