package com.hengtiansoft.bluemorpho.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：May 23, 2018 9:31:23 PM
 */
@SuppressWarnings("serial")
public class CloneRelation implements Serializable {

	private int memberId;
	private List<Integer> tier_1 = new ArrayList<Integer>();
	private List<Integer> tier_2 = new ArrayList<Integer>();
	private List<Integer> tier_3 = new ArrayList<Integer>();
	private List<Integer> tier_4 = new ArrayList<Integer>();

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public List<Integer> getTier_1() {
		return tier_1;
	}

	public void setTier_1(List<Integer> tier_1) {
		this.tier_1 = tier_1;
	}

	public List<Integer> getTier_2() {
		return tier_2;
	}

	public void setTier_2(List<Integer> tier_2) {
		this.tier_2 = tier_2;
	}

	public List<Integer> getTier_3() {
		return tier_3;
	}

	public void setTier_3(List<Integer> tier_3) {
		this.tier_3 = tier_3;
	}

	public List<Integer> getTier_4() {
		return tier_4;
	}

	public void setTier_4(List<Integer> tier_4) {
		this.tier_4 = tier_4;
	}
}
