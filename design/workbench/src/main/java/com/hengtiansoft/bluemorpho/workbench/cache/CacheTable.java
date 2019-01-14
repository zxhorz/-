package com.hengtiansoft.bluemorpho.workbench.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.hengtiansoft.bluemorpho.workbench.dto.TableDetailItem;
import com.hengtiansoft.bluemorpho.workbench.dto.TableItem;

/**
 * @Description: cache table
 * @author gaochaodeng
 * @date Jul 18, 2018
 */
public class CacheTable implements Serializable {

	private static final long serialVersionUID = 1L;
	private TableItem tableItem;
	private List<TableDetailItem> tableDetailItems = new ArrayList<TableDetailItem>();

	public CacheTable() {
		super();
	}

	public CacheTable(TableItem tableItem,
			List<TableDetailItem> tableDetailItems) {
		super();
		this.tableItem = tableItem;
		this.tableDetailItems = tableDetailItems;
	}

	public TableItem getTableItem() {
		return tableItem;
	}

	public void setTableItem(TableItem tableItem) {
		this.tableItem = tableItem;
	}

	public List<TableDetailItem> getTableDetailItems() {
		return tableDetailItems;
	}

	public void setTableDetailItems(List<TableDetailItem> tableDetailItems) {
		this.tableDetailItems = tableDetailItems;
	}
}
