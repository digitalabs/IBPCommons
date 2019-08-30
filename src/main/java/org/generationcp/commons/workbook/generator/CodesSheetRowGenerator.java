
package org.generationcp.commons.workbook.generator;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.CellStyle;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.commons.parsing.ExcelWorkbookRow;

import java.util.List;

abstract class CodesSheetRowGenerator<SOURCE> {

	protected ExcelCellStyleBuilder sheetStyles;

	public void addRowsToCodesSheet(final HSSFSheet codesSheet, final ExcelCellStyleBuilder sheetStyles) {
		this.sheetStyles = sheetStyles;

		final CellStyle labelStyle = this.getLabelStyle();
		final CellStyle dataStyle = this.getDataStyle();

		final List<SOURCE> items = this.getSourceItem();
		ExcelWorkbookRow itemRow;
		int currentRow = codesSheet.getLastRowNum() + 1;

		for (final SOURCE source : items) {
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
