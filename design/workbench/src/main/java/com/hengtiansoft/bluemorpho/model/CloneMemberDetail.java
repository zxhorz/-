package com.hengtiansoft.bluemorpho.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: TODO
 * @author gaochaodeng
 * @date Jun 10, 2018
 */
public class CloneMemberDetail {
	private int memberId;
	private String memberName;
	private List<String> tier_1 = new ArrayList<String>();
	private List<String> tier_2 = new ArrayList<String>();
	private List<String> tier_3 = new ArrayList<String>();
	private List<String> tier_4 = new ArrayList<String>();

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public List<String> getTier_1() {
		return tier_1;
	}

	public void setTier_1(List<String> tier_1) {
		this.tier_1 = tier_1;
	}

	public List<String> getTier_2() {
		return tier_2;
	}

	public void setTier_2(List<String> tier_2) {
		this.tier_2 = tier_2;
	}

	public List<String> getTier_3() {
		return tier_3;
	}

	public void setTier_3(List<String> tier_3) {
		this.tier_3 = tier_3;
	}

	public List<String> getTier_4() {
		return tier_4;
	}

	public void setTier_4(List<String> tier_4) {
		this.tier_4 = tier_4;
	}
}
