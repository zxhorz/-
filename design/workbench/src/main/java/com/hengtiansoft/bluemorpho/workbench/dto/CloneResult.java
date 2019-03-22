package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：May 23, 2018 10:48:07 PM
 */
@SuppressWarnings("serial")
public class CloneResult implements Serializable {

	private int groupCount;
	private int programCount;
	private String percent;
	private String percent1;
	private String percent2;
	private String percent3;
	private String percent4;

	public CloneResult() {
	}

	public CloneResult(int groupCount, int programCount, String percent, String percent1, String percent2, String percent3, String percent4) {
		super();
		this.groupCount = groupCount;
		this.programCount = programCount;
		this.percent = percent;
		this.percent1 = percent1;
		this.percent2 = percent2;
		this.percent3 = percent3;
		this.percent4 = percent4;
	}

	public int getGroupCount() {
		return groupCount;
	}

	public void setGroupCount(int groupCount) {
		this.groupCount = groupCount;
	}

	public int getProgramCount() {
		return programCount;
	}

	public void setProgramCount(int programCount) {
		this.programCount = programCount;
	}

	public String getPercent() {
		return percent;
	}

	public void setPercent(String percent) {
		this.percent = percent;
	}

	public String getPercent1() {
		return percent1;
	}

	public void setPercent1(String percent1) {
		this.percent1 = percent1;
	}

	public String getPercent2() {
		return percent2;
	}

	public void setPercent2(String percent2) {
		this.percent2 = percent2;
	}

	public String getPercent3() {
		return percent3;
	}

	public void setPercent3(String percent3) {
		this.percent3 = percent3;
	}

	public String getPercent4() {
		return percent4;
	}

	public void setPercent4(String percent4) {
		this.percent4 = percent4;
	}
}
