package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：May 23, 2018 11:15:40 AM
 */
@SuppressWarnings("serial")
public class SummaryDetailItem implements Serializable {

	private String detailName;
	private String detailData;

	public SummaryDetailItem() {
	}

	public SummaryDetailItem(String detailName, String detailData) {
		super();
		this.detailName = detailName;
		this.detailData = detailData;
	}

	public String getDetailName() {
		return detailName;
	}

	public void setDetailName(String detailName) {
		this.detailName = detailName;
	}

	public String getDetailData() {
		return detailData;
	}

	public void setDetailData(String detailData) {
		this.detailData = detailData;
	}

}
