package com.hengtiansoft.bluemorpho.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class CloneDiffInfo implements Serializable{

	private List<CloneMemberDetail> memberTier = new ArrayList<>();
	private List<CloneMember> memberInfo = new ArrayList<>();
	public List<CloneMemberDetail> getMemberTier() {
		return memberTier;
	}
	public void setMemberTier(List<CloneMemberDetail> memberTier) {
		this.memberTier = memberTier;
	}
	public List<CloneMember> getMemberInfo() {
		return memberInfo;
	}
	public void setMemberInfo(List<CloneMember> memberInfo) {
		this.memberInfo = memberInfo;
	}
}
