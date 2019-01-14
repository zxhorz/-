package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class TagResponse implements Serializable{
	
	private List<String> confirmedTags = new ArrayList<>();
	private List<String> deniedTags = new ArrayList<>();
	
	public List<String> getConfirmedTags() {
		return confirmedTags;
	}
	public void setConfirmedTags(List<String> confirmedTags) {
		this.confirmedTags = confirmedTags;
	}
	public List<String> getDeniedTags() {
		return deniedTags;
	}
	public void setDeniedTags(List<String> deniedTags) {
		this.deniedTags = deniedTags;
	}
}
