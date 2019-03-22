package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class MenuResponse implements Serializable{

	private String name;
	private List<SubMenu> submenus = new ArrayList<>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<SubMenu> getSubmenus() {
		return submenus;
	}
	public void setSubmenus(List<SubMenu> submenus) {
		this.submenus = submenus;
	}
	
	
}
