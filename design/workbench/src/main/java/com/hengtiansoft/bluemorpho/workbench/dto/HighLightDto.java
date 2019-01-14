package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;
import java.util.List;

public class HighLightDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<String> full_text;

	public List<String> getFull_text() {
		return full_text;
	}

	public void setFull_text(List<String> full_text) {
		this.full_text = full_text;
	}
	
}
