package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：May 23, 2018 11:15:40 AM
 */
@SuppressWarnings("serial")
public class SummaryTabMap implements Serializable {

	private String nodeName;
	private String tableColumn;
	private String summaryName;
	private String tabName;

	public SummaryTabMap() {
	}

	public SummaryTabMap(String nodeName, String tableColumn, String tabName) {
		super();
		this.nodeName = nodeName;
		this.tableColumn = tableColumn;
		this.tabName = tabName;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getTableColumn() {
		return tableColumn;
	}

	public void setTableColumn(String tableColumn) {
		this.tableColumn = tableColumn;
	}

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	
	public String getSummaryName() {
		return summaryName;
	}

	public void setSummaryName(String summaryName) {
		this.summaryName = summaryName;
	}

	public String toString(){
		return nodeName + " " + tableColumn + " " + summaryName + " " +tabName; 
	}
}
