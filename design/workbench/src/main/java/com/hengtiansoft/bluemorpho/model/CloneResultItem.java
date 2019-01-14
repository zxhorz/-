package com.hengtiansoft.bluemorpho.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：May 23, 2018 9:22:20 PM
 */
@SuppressWarnings("serial")
public class CloneResultItem implements Serializable {

	private List<CloneRelation> cloneRelations = new ArrayList<CloneRelation>();
	private int groupNo;
	private List<CloneMember> members = new ArrayList<CloneMember>();
	private CloneLinesInGroup cloneLines;

	public int getGroupNo() {
		return groupNo;
	}

	public void setGroupNo(int groupNo) {
		this.groupNo = groupNo;
	}

	public List<CloneRelation> getCloneRelations() {
		return cloneRelations;
	}

	public void setCloneRelations(List<CloneRelation> cloneRelations) {
		this.cloneRelations = cloneRelations;
	}

	public List<CloneMember> getMembers() {
		return members;
	}

	public void setMembers(List<CloneMember> members) {
		this.members = members;
	}

	public CloneLinesInGroup getCloneLines() {
		return cloneLines;
	}

	public void setCloneLines(CloneLinesInGroup cloneLines) {
		this.cloneLines = cloneLines;
	}

}
