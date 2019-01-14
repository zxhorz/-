package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Sep 10, 2018 3:38:24 PM
 */
public class TableUsedInItem implements Serializable {

	private static final long serialVersionUID = 1L;
	private String nodeId;
	private String name;
	private String tags;
	private List<ParagraphUseTableInfo> usedIn;

	public TableUsedInItem() {
		super();
	}

	public TableUsedInItem(String nodeId, String name, String tags,
			List<ParagraphUseTableInfo> usedIn) {
		super();
		this.nodeId = nodeId;
		this.name = name;
		this.tags = tags;
		this.usedIn = usedIn;
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

	public List<ParagraphUseTableInfo> getUsedIn() {
		return usedIn;
	}

	public void setUsedIn(List<ParagraphUseTableInfo> usedIn) {
		this.usedIn = usedIn;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
