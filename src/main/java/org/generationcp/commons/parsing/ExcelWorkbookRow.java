package org.generationcp.commons.parsing;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.generationcp.middleware.domain.ontology.Variable;

/**
 * A wrapper for a {@link org.apache.poi.hssf.usermodel.HSSFRow} with functionality specific to workbook rows
 */
public class ExcelWorkbookRow {

	private final HSSFRow row;

	public ExcelWorkbookRow(final HSSFRow row) {
		this.row = row;
	}

	public void writeListDetailsRow(final Sheet descriptionSheet, final String labelName, final String text, final String defaultText,
			final CellStyle labelStyle, final CellStyle textStyle) {
		this.createCell(0, labelStyle, labelName);
		this.createCellRange(descriptionSheet, 1, 2, textStyle, text);
		this.createCellRange(descriptionSheet, 3, 6, textStyle, defaultText);
	}

	public Cell createCell(final int column, final CellStyle cellStyle, final String value) {
		final HSSFCell cell = this.row.createCell(column);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(value);
		return cell;
	}

	/**
	 * We need this method to store numbers as numbers to the excel workbook, otherwise it gives warning message "Number stored as text"
	 * @param column column number
	 * @param cellStyle the cell style
	 * @param value numeric value to store
	 * @return the cell created
	 */
	public Cell createCell(final int column, final CellStyle cellStyle, final double value) {
		final HSSFCell cell = this.row.createCell(column);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(value);
		return cell;
	}

	public Cell createCellRange(final Sheet sheet, final int start, final int end, final CellStyle cellStyle,
			final String value) {

		for (int x = start; x <= end; x++) {
			final HSSFCell cell = this.row.createCell(x);
			cell.setCellStyle(cellStyle);
		}

		sheet.addMergedRegion(new CellRangeAddress(this.row.getRowNum(), this.row.getRowNum(), start, end));

		final HSSFCell cell = this.row.getCell(start);
		cell.setCellValue(value);

		return cell;
	}

	public void writeStandardVariableToRow(final CellStyle labelStyleFactor, final CellStyle textStyle,
			final Variable standardVariable) {

		this.createCell(0, labelStyleFactor, standardVariable.getName().toUpperCase());
		this.createCell(1, textStyle, standardVariable.getDefinition());
		this.createCell(2, textStyle, standardVariable.getProperty().getName().toUpperCase());
		this.createCell(3, textStyle, standardVariable.getScale().getName().toUpperCase());
		this.createCell(4, textStyle, standardVariable.getMethod().getName().toUpperCase());
		this.createCell(5, textStyle, standardVariable.getScale().getDataType().getName().substring(0, 1).toUpperCase());
		this.createCell(6, textStyle, "");
		this.createCell(7, textStyle, "");

	}

	public HSSFRow getRow() {
		return this.row;
	}

}
