package com.hengtiansoft.bluemorpho.model;

import java.io.Serializable;

/**
 * @Description: group中的每类tier的clone代码行数, 便于界面展示group信息时使用
 * @author gaochaodeng
 * @date Aug 17, 2018
 */
public class CloneLinesInGroup implements Serializable {

	private static final long serialVersionUID = 1L;
	private int tier_1_lines;
	private int tier_2_lines;
	private int tier_3_lines;
	private int tier_4_lines;

	public int getTier_1_lines() {
		return tier_1_lines;
	}

	public void setTier_1_lines(int tier_1_lines) {
		this.tier_1_lines = tier_1_lines;
	}

	public int getTier_2_lines() {
		return tier_2_lines;
	}

	public void setTier_2_lines(int tier_2_lines) {
		this.tier_2_lines = tier_2_lines;
	}

	public int getTier_3_lines() {
		return tier_3_lines;
	}

	public void setTier_3_lines(int tier_3_lines) {
		this.tier_3_lines = tier_3_lines;
	}

	public int getTier_4_lines() {
		return tier_4_lines;
	}

	public void setTier_4_lines(int tier_4_lines) {
		this.tier_4_lines = tier_4_lines;
	}

}
