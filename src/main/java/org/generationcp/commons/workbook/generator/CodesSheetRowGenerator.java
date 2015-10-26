package org.generationcp.commons.workbook.generator;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.CellStyle;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.commons.parsing.ExcelWorkbookRow;

abstract class CodesSheetRowGenerator<SOURCE> {
	
	protected ExcelCellStyleBuilder sheetStyles;
	
	public void addRowsToCodesSheet(HSSFSheet codesSheet, ExcelCellStyleBuilder sheetStyles){
		this.sheetStyles = sheetStyles;
		
		CellStyle labelStyle = this.getLabelStyle();
		CellStyle dataStyle = this.getDataStyle();
		
		List<SOURCE> items = getSourceItem();
		ExcelWorkbookRow itemRow;
		int currentRow = codesSheet.getLastRowNum() + 1;
		
		for(SOURCE source: items){
			itemRow = new ExcelWorkbookRow(codesSheet.createRow(currentRow));
			itemRow.createCell(0, labelStyle, this.getSection());
			itemRow.createCell(1, labelStyle, this.getInfoType());
			itemRow.createCell(2, dataStyle, this.getFcode(source));
			itemRow.createCell(3, dataStyle, this.getFname(source));
			currentRow++;
		}
		
	}
	
	abstract List<SOURCE> getSourceItem();
	abstract CellStyle getLabelStyle();
	abstract CellStyle getDataStyle();
	abstract String getSection();
	abstract String getInfoType();
	abstract String getFcode(SOURCE source);
	abstract String getFname(SOURCE source);
	
}
