package com.hengtiansoft.bluemorpho.workbench.domain;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PicIndexAndBase64 implements Serializable {

	private String ctrlFlowPicIndex;
	private String base64Str;
	private String picWidth;
	private String name;

	public PicIndexAndBase64() {
		super();
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PicIndexAndBase64(String ctrlFlowPicIndex, String base64Str, String picWidth, String name) {
        super();
        this.ctrlFlowPicIndex = ctrlFlowPicIndex;
        this.base64Str = base64Str;
        this.picWidth = picWidth;
        this.name = name;
    }

    public PicIndexAndBase64(String ctrlFlowPicIndex, String base64Str, String picWidth) {
		super();
		this.ctrlFlowPicIndex = ctrlFlowPicIndex;
		this.base64Str = base64Str;
		this.picWidth = picWidth;
	}

	public String getCtrlFlowPicIndex() {
		return ctrlFlowPicIndex;
	}

	public void setCtrlFlowPicIndex(String ctrlFlowPicIndex) {
		this.ctrlFlowPicIndex = ctrlFlowPicIndex;
	}

	public String getBase64Str() {
		return base64Str;
	}

	public void setBase64Str(String base64Str) {
		this.base64Str = base64Str;
	}

	public String getPicWidth() {
		return picWidth;
	}

	public void setPicWidth(String picWidth) {
		this.picWidth = picWidth;
	}

}
