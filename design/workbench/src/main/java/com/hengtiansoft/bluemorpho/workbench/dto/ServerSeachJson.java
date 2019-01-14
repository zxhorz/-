package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

public class ServerSeachJson  implements Serializable{
	private static final long serialVersionUID = 1L;
	private ServerSearchIndexResult index_message;
	private ServerSearchResult search_message;
	public ServerSearchIndexResult getIndex_message() {
		return index_message;
	}
	public void setIndex_message(ServerSearchIndexResult index_message) {
		this.index_message = index_message;
	}
	public ServerSearchResult getSearch_message() {
		return search_message;
	}
	public void setSearch_message(ServerSearchResult search_message) {
		this.search_message = search_message;
	}
}
