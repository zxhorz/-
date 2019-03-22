package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;
import java.util.List;

public class ServerSearchIndexResult implements Serializable{
	private static final long serialVersionUID = 1L;
	private List<String> add_files_failure;
	private List<String> delete_files_failure;
	private List<String> reindex_failture;
	public List<String> getAdd_files_failure() {
		return add_files_failure;
	}
	public void setAdd_files_failure(List<String> add_files_failure) {
		this.add_files_failure = add_files_failure;
	}
	public List<String> getDelete_files_failure() {
		return delete_files_failure;
	}
	public void setDelete_files_failure(List<String> delete_files_failure) {
		this.delete_files_failure = delete_files_failure;
	}
	public List<String> getReindex_failture() {
		return reindex_failture;
	}
	public void setReindex_failture(List<String> reindex_failture) {
		this.reindex_failture = reindex_failture;
	}
}
