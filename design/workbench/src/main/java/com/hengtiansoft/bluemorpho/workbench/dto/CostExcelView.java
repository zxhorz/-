package com.hengtiansoft.bluemorpho.workbench.dto;

import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractExcelView;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jul 4, 2018 4:06:34 PM
 */
@SuppressWarnings("deprecation")
public class CostExcelView extends AbstractExcelView {

	@Override
	protected void buildExcelDocument(Map<String, Object> model,
			HSSFWorkbook book, HttpServletRequest request,
			HttpServletResponse resp) throws Exception {
		CostEstimationResult data = (CostEstimationResult) model.get("data");

		resp.setContentType("application/vnd.ms-excel");
		resp.setHeader("Content-Disposition", "attachment;filename="
				+ new String(("CostEstimation.xls").getBytes(), "ISO8859-1"));

		HSSFSheet programsSheet = book.createSheet("Programs");
		HSSFRow row1 = programsSheet.createRow(0);
		row1.createCell(0).setCellValue("Program");
		row1.createCell(1).setCellValue("Paragraph");
		row1.createCell(2).setCellValue("LOC");
		row1.createCell(3).setCellValue("Loop");
		row1.createCell(4).setCellValue("Conditional Statements");
		row1.createCell(5).setCellValue("Tables");
		row1.createCell(6).setCellValue("Variables");
		row1.createCell(7).setCellValue("Complexity Ratio");
		row1.createCell(8).setCellValue("Clone Group");
		row1.createCell(9).setCellValue("Clone Tier");
		row1.createCell(10).setCellValue("Cost Point");
		row1.createCell(11).setCellValue("Man Hour");
		int programsRow = 1;
		for (CostProgramResult ele : data.getProgramResults()) {
			HSSFRow row = programsSheet.createRow(programsRow);
			row.createCell(0).setCellValue(ele.getName());
			row.createCell(1).setCellValue(ele.getParaCount());
			row.createCell(2).setCellValue(ele.getLoc());
			row.createCell(3).setCellValue(ele.getLoop());
			row.createCell(4).setCellValue(ele.getConditionalStatements());
			row.createCell(5).setCellValue(ele.getTables());
			row.createCell(6).setCellValue(ele.getVariables());
			row.createCell(7).setCellValue(ele.getComplexityRatio());
			row.createCell(8).setCellValue("N/A");
			row.createCell(9).setCellValue("N/A");
			row.createCell(10).setCellValue(ele.getCostPoint());
			row.createCell(11).setCellValue(ele.getManHour());
			programsRow++;
			
			HSSFSheet oriSheet = book.getSheet(ele.getName());
			if (oriSheet == null) {
				HSSFSheet programDetailSheet = book.createSheet(ele.getName());
				HSSFRow pRow1 = programDetailSheet.createRow(0);
				pRow1.createCell(0).setCellValue("Paragraph");
				pRow1.createCell(1).setCellValue("LOC");
				pRow1.createCell(2).setCellValue("Loop");
				pRow1.createCell(3).setCellValue("Conditional Statements");
				pRow1.createCell(4).setCellValue("Tables");
				pRow1.createCell(5).setCellValue("Variables");
				pRow1.createCell(6).setCellValue("Complexity Ratio");
				pRow1.createCell(7).setCellValue("Clone Group");
				pRow1.createCell(8).setCellValue("Clone Tier");
				pRow1.createCell(9).setCellValue("Cost Point");
				pRow1.createCell(10).setCellValue("Man Hour");
				int pRow = 1;
				for (CostParagraphResult item : ele.getParagraphResults()) {
					HSSFRow rrow = programDetailSheet.createRow(pRow);
					rrow.createCell(0).setCellValue(item.getName());
					rrow.createCell(1).setCellValue(item.getLoc());
					rrow.createCell(2).setCellValue(item.getLoop());
					rrow.createCell(3).setCellValue(item.getConditionalStatements());
					rrow.createCell(4).setCellValue(item.getTables());
					rrow.createCell(5).setCellValue(item.getVariables());
					rrow.createCell(6).setCellValue(item.getComplexityRatio());
					rrow.createCell(7).setCellValue("N/A");
					rrow.createCell(8).setCellValue("N/A");
					rrow.createCell(9).setCellValue(item.getCostPoint());
					rrow.createCell(10).setCellValue(item.getManHour());
					pRow++;
				}
			}
		}
		
		HSSFSheet cloneSheet = book.createSheet("Clone");
		HSSFRow cloneRow1 = cloneSheet.createRow(0);
		cloneRow1.createCell(0).setCellValue("Clone Groups");
		cloneRow1.createCell(1).setCellValue("Number of Paragraphs");
		cloneRow1.createCell(2).setCellValue("Combined Output");
		cloneRow1.createCell(3).setCellValue("Cost Point");
		cloneRow1.createCell(4).setCellValue("Man Hour");
		int cloneRow = 1;
		for (CostCloneResult ele : data.getCloneResults()) {
			HSSFRow cRow = cloneSheet.createRow(cloneRow);
			cRow.createCell(0).setCellValue("group" + ele.getGroupNo());
			cRow.createCell(1).setCellValue(ele.getParaCount());
			cRow.createCell(2).setCellValue(ele.getCombinedOutput());
			cRow.createCell(3).setCellValue(ele.getCostPoint());
			cRow.createCell(4).setCellValue(ele.getManHour());
			cloneRow++;
		}
		
		HSSFSheet totalAndBudgetSheet = book.createSheet("GrandTotal_WithBudget");
		HSSFRow totalAndBudgetSheetRow0 = totalAndBudgetSheet.createRow(0);
		totalAndBudgetSheetRow0.createCell(0).setCellValue("Grand Total");
		totalAndBudgetSheetRow0.createCell(1).setCellValue("Within Budget");
		HSSFRow totalAndBudgetSheetRow1 = totalAndBudgetSheet.createRow(1);
		totalAndBudgetSheetRow1.createCell(0).setCellValue(data.getGrandTotal());
		totalAndBudgetSheetRow1.createCell(1).setCellValue(data.getWithBudget());
		
		OutputStream ouputStream = resp.getOutputStream();
		book.write(ouputStream);
		ouputStream.flush();
		ouputStream.close();
	}

}
