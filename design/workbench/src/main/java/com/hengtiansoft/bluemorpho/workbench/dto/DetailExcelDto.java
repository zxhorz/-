package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class DetailExcelDto implements Serializable {

	private List<SummaryDetailItem> summaryDetails;
	private List<ProgramDetailItem> programDetails;
	private List<ParagraphDetailItem> paraDetails;
	private List<TableItem> tableItems;
	private List<FileItem> fileItems;
	private List<CopybookDetailItem> copybookDetails;
	private List<JclDetailItem> jclDetailItems;
	private List<ParagraphUseTableInfo> sqlLogicItems;
	
	public DetailExcelDto() {
	}

	public DetailExcelDto(List<SummaryDetailItem> summaryDetails,
			List<ProgramDetailItem> programDetails,
			List<ParagraphDetailItem> paraDetails, List<TableItem> tableItems,
			List<FileItem> fileItems, List<CopybookDetailItem> copybookDetails,
			List<JclDetailItem> jclDetailItems,
			List<ParagraphUseTableInfo> sqlLogicItems) {
		super();
		this.summaryDetails = summaryDetails;
		this.programDetails = programDetails;
		this.paraDetails = paraDetails;
		this.setTableItems(tableItems);
		this.setFileItems(fileItems);
		this.copybookDetails = copybookDetails;
		this.setJclDetailItems(jclDetailItems);
		this.setSqlLogicItems(sqlLogicItems);
	}


	public List<SummaryDetailItem> getSummaryDetails() {
		return summaryDetails;
	}

	public void setSummaryDetails(List<SummaryDetailItem> summaryDetails) {
		this.summaryDetails = summaryDetails;
	}

	public List<ProgramDetailItem> getProgramDetails() {
		return programDetails;
	}

	public void setProgramDetails(List<ProgramDetailItem> programDetails) {
		this.programDetails = programDetails;
	}

	public List<ParagraphDetailItem> getParaDetails() {
		return paraDetails;
	}

	public void setParaDetails(List<ParagraphDetailItem> paraDetails) {
		this.paraDetails = paraDetails;
	}

	public List<CopybookDetailItem> getCopybookDetails() {
		return copybookDetails;
	}

	public void setCopybookDetails(List<CopybookDetailItem> copybookDetails) {
		this.copybookDetails = copybookDetails;
	}

	public List<TableItem> getTableItems() {
		return tableItems;
	}

	public void setTableItems(List<TableItem> tableItems) {
		this.tableItems = tableItems;
	}

	public List<FileItem> getFileItems() {
		return fileItems;
	}

	public void setFileItems(List<FileItem> fileItems) {
		this.fileItems = fileItems;
	}

	public List<JclDetailItem> getJclDetailItems() {
		return jclDetailItems;
	}

	public void setJclDetailItems(List<JclDetailItem> jclDetailItems) {
		this.jclDetailItems = jclDetailItems;
	}

	public List<ParagraphUseTableInfo> getSqlLogicItems() {
		return sqlLogicItems;
	}

	public void setSqlLogicItems(List<ParagraphUseTableInfo> sqlLogicItems) {
		this.sqlLogicItems = sqlLogicItems;
	}

}
