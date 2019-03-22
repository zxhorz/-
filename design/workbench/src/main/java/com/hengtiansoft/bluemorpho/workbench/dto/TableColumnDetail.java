package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Sep 10, 2018 3:50:34 PM
 */
@SuppressWarnings("serial")
public class TableColumnDetail implements Serializable {

	private String tableNodeId;
	private String tableName;
	private String tableTags;
	List<TableDetailItem> allTableDetailItems;

	public TableColumnDetail() {
		super();
	}

	public TableColumnDetail(String tableNodeId, String tableName,
			String tableTags, List<TableDetailItem> allTableDetailItems) {
		super();
		this.tableNodeId = tableNodeId;
		this.tableName = tableName;
		this.tableTags = tableTags;
		this.allTableDetailItems = allTableDetailItems;
	}

	public String getTableNodeId() {
		return tableNodeId;
	}

	public void setTableNodeId(String tableNodeId) {
		this.tableNodeId = tableNodeId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableTags() {
		return tableTags;
	}

	public void setTableTags(String tableTags) {
		this.tableTags = tableTags;
	}

	public List<TableDetailItem> getAllTableDetailItems() {
		return allTableDetailItems;
	}

	public void setAllTableDetailItems(List<TableDetailItem> allTableDetailItems) {
		this.allTableDetailItems = allTableDetailItems;
	}

}
