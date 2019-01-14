package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractExcelView;

@SuppressWarnings({ "deprecation" })
public class DetailExcelView extends AbstractExcelView {

	@Override
	protected void buildExcelDocument(Map<String, Object> model,
			HSSFWorkbook book, HttpServletRequest request,
			HttpServletResponse resp) throws Exception {
		DetailExcelDto data = (DetailExcelDto) model.get("data");
		List<SummaryDetailItem> summaryDetails = data.getSummaryDetails();
		List<ProgramDetailItem> programDetails = data.getProgramDetails();
		List<ParagraphDetailItem> paraDetails = data.getParaDetails();
		List<TableItem> tableItems = data.getTableItems();
		List<FileItem> fileItems = data.getFileItems();
		List<CopybookDetailItem> copybookDetails = data.getCopybookDetails();
		List<JclDetailItem> jclDetailItems = data.getJclDetailItems();
		List<ParagraphUseTableInfo> sqlLogicItems = data.getSqlLogicItems();
		
		resp.setContentType("application/vnd.ms-excel");
		resp.setHeader("Content-Disposition", "attachment;filename="
				+ new String(("SummaryDetail.xls").getBytes(), "ISO8859-1"));
		
		HSSFSheet systemSheet = book.createSheet("System");
		List<String> summaryHeaders = new ArrayList<String>();
		List<String> summaryContents = new ArrayList<String>();
		for (SummaryDetailItem summaryDetail : summaryDetails) {
			String detailName = summaryDetail.getDetailName();
			String detailData = summaryDetail.getDetailData();
			summaryHeaders.add(detailName);
			summaryContents.add(detailData);
		}
		HSSFRow summaryRow1 = systemSheet.createRow(0);
		for (int i = 0; i < summaryHeaders.size(); i++) {
			summaryRow1.createCell(i).setCellValue(summaryHeaders.get(i));
		}
		HSSFRow summaryRow2 = systemSheet.createRow(1);
		for (int i = 0; i < summaryContents.size(); i++) {
			summaryRow2.createCell(i).setCellValue(summaryContents.get(i));
		}
		
		HSSFSheet programSheet = book.createSheet("Programs");
		HSSFRow programRow1 = programSheet.createRow(0);
		programRow1.createCell(0).setCellValue("Name");
		programRow1.createCell(1).setCellValue("Type");
		programRow1.createCell(2).setCellValue("Lines");
		programRow1.createCell(3).setCellValue("Complexity");
		programRow1.createCell(4).setCellValue("ClonePercentage");
		programRow1.createCell(5).setCellValue("Tags");
		int programRow = 1;
		for (ProgramDetailItem programDetail : programDetails) {
			HSSFRow row = programSheet.createRow(programRow);
			row.createCell(0).setCellValue(programDetail.getName());
			row.createCell(1).setCellValue(programDetail.getType());
			row.createCell(2).setCellValue(programDetail.getLines());
			row.createCell(3).setCellValue(programDetail.getComplexity());
			row.createCell(4).setCellValue(programDetail.getClonePercentage());
			row.createCell(5).setCellValue(programDetail.getTags());
			programRow++;
		}
		
		HSSFSheet paragraphSheet = book.createSheet("Paragraphs");
		HSSFRow paragraphRow1 = paragraphSheet.createRow(0);
		paragraphRow1.createCell(0).setCellValue("Name");
		paragraphRow1.createCell(1).setCellValue("Lines");
		paragraphRow1.createCell(2).setCellValue("Complexity");
		paragraphRow1.createCell(3).setCellValue("ClonePercentage");
		paragraphRow1.createCell(4).setCellValue("Tags");
		int paragraphRow = 1;
		for (ParagraphDetailItem paraDetail : paraDetails) {
			HSSFRow row = paragraphSheet.createRow(paragraphRow);
			row.createCell(0).setCellValue(
					StringUtils.substringAfterLast(
							paraDetail.getProgramLocation(), "/")
							+ "."
							+ StringUtils.substringAfterLast(
									paraDetail.getParagraphName(), "."));
			row.createCell(1).setCellValue(paraDetail.getLines());
			row.createCell(2).setCellValue(paraDetail.getComplexity());
			row.createCell(3).setCellValue(paraDetail.getClonePercentage());
			row.createCell(4).setCellValue(paraDetail.getTags());
			paragraphRow++;
		}
		
		HSSFSheet tableSheet = book.createSheet("Tables");
		HSSFRow tableSheetRow1 = tableSheet.createRow(0);
		tableSheetRow1.createCell(0).setCellValue("Name");
		tableSheetRow1.createCell(1).setCellValue("Tags");
		int tableRow = 1;
		for (TableItem tableItem : tableItems) {
			HSSFRow row = tableSheet.createRow(tableRow);
			row.createCell(0).setCellValue(tableItem.getName());
			row.createCell(1).setCellValue(tableItem.getTags());
			tableRow++;
		}
		
		HSSFSheet fileSheet = book.createSheet("Files");
		HSSFRow fileSheetRow1 = fileSheet.createRow(0);
		fileSheetRow1.createCell(0).setCellValue("Name");
		fileSheetRow1.createCell(1).setCellValue("IO-Type");
		fileSheetRow1.createCell(2).setCellValue("Program");
		fileSheetRow1.createCell(3).setCellValue("Tags");
		int fileRow = 1;
		for (FileItem fileItem : fileItems) {
			HSSFRow row = fileSheet.createRow(fileRow);
			row.createCell(0).setCellValue(fileItem.getName());
			row.createCell(1).setCellValue(fileItem.getOpenType());
			row.createCell(2).setCellValue(fileItem.getPgmFileName());
			row.createCell(3).setCellValue(fileItem.getTags());
			fileRow++;
		}
		
		HSSFSheet copybookSheet = book.createSheet("Copybooks");
		HSSFRow cpySheetRow1 = copybookSheet.createRow(0);
		cpySheetRow1.createCell(0).setCellValue("Name");
		cpySheetRow1.createCell(1).setCellValue("Type");
		cpySheetRow1.createCell(2).setCellValue("Tags");
		int cpyRow = 1;
		for (CopybookDetailItem copybookDetail : copybookDetails) {
			HSSFRow row = copybookSheet.createRow(cpyRow);
			row.createCell(0).setCellValue(copybookDetail.getCpyName());
			row.createCell(1).setCellValue(copybookDetail.getType());
			row.createCell(2).setCellValue(copybookDetail.getTags());
			cpyRow++;
		}
		
		HSSFSheet jclSheet = book.createSheet("Jcls");
		HSSFRow jclSheetRow1 = jclSheet.createRow(0);
		jclSheetRow1.createCell(0).setCellValue("Name");
		jclSheetRow1.createCell(1).setCellValue("Type");
		jclSheetRow1.createCell(2).setCellValue("Tags");
		int jclRow = 1;
		for (JclDetailItem jclDetailItem : jclDetailItems) {
			HSSFRow row = jclSheet.createRow(jclRow);
			row.createCell(0).setCellValue(jclDetailItem.getName());
			row.createCell(1).setCellValue(jclDetailItem.getType());
			row.createCell(2).setCellValue(jclDetailItem.getTags());
			jclRow++;
		}
		
		HSSFSheet sqlLogicSheet = book.createSheet("Sql Logics");
		HSSFRow sqlLogicSheetRow1 = sqlLogicSheet.createRow(0);
		sqlLogicSheetRow1.createCell(0).setCellValue("Program");
		sqlLogicSheetRow1.createCell(1).setCellValue("Paragraph");
		sqlLogicSheetRow1.createCell(2).setCellValue("Command");
		sqlLogicSheetRow1.createCell(3).setCellValue("Table");
		int sqlLogicRow = 1;
		for (ParagraphUseTableInfo sqlLogicItem : sqlLogicItems) {
			HSSFRow row = sqlLogicSheet.createRow(sqlLogicRow);
			row.createCell(0).setCellValue(sqlLogicItem.getProgramName());
			row.createCell(1).setCellValue(
					StringUtils.substringAfterLast(
							sqlLogicItem.getParagraphName(), "."));
			row.createCell(2).setCellValue(sqlLogicItem.getOperation());
			row.createCell(3).setCellValue(sqlLogicItem.getTableName());
			sqlLogicRow++;
		}
		
		OutputStream ouputStream = resp.getOutputStream();
		book.write(ouputStream);
		ouputStream.flush();
		ouputStream.close();
	}

}
