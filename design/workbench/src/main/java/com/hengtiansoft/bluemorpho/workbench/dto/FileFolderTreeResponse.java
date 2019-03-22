package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class FileFolderTreeResponse implements Serializable{

	private String name;
	private String fileType;
	private String type;
	private List<FileFolderTreeResponse> children = new ArrayList<>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public List<FileFolderTreeResponse> getChildren() {
		return children;
	}
	public void setChildren(List<FileFolderTreeResponse> children) {
		this.children = children;
	}
}
