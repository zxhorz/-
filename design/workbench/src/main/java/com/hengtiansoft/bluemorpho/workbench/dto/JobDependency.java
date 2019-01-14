package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class JobDependency implements Serializable{

	private String id ;
	private String dependId;
	
	public JobDependency(String id, String dependId){
		this.id = id;
		this.dependId = dependId;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDependId() {
		return dependId;
	}
	public void setDependId(String dependId) {
		this.dependId = dependId;
	}
	
	
}
