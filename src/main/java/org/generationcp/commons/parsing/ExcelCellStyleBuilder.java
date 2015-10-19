package org.generationcp.commons.parsing;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Constructs the cell styles for excel file and services the request for a particular style from the list of excel cell styles
 */

public class ExcelCellStyleBuilder {

	// Styles
	public enum ExcelCellStyle {
		LABEL_STYLE,
		HEADING_STYLE,
		NUMERIC_STYLE,
		TEXT_STYLE,
		LABEL_STYLE_CONDITION,
		LABEL_STYLE_FACTOR,
		LABEL_STYLE_INVENTORY,
		LABEL_STYLE_VARIATE,
		HEADING_STYLE_FACTOR,
		HEADING_STYLE_INVENTORY,
		HEADING_STYLE_VARIATE,
		SHEET_STYLE,
		NUMBER_DATA_FORMAT_STYLE,
		TEXT_DATA_FORMAT_STYLE,
		TEXT_HIGHLIGHT_STYLE_FACTOR,
		COLUMN_HIGHLIGHT_STYLE_FACTOR,
		NUMBER_COLUMN_HIGHLIGHT_STYLE_FACTOR,
		DECIMAL_NUMBER_DATA_FORMAT_STYLE
	}

	private final Map<ExcelCellStyle, CellStyle> stylesMap;

	public ExcelCellStyleBuilder(final HSSFWorkbook wb) {
		this.stylesMap = createStyles(wb);
	}

	public CellStyle getCellStyle(final ExcelCellStyle style) {
		return this.stylesMap.get(style);
	}

	private Map<ExcelCellStyle, CellStyle> createStyles(final HSSFWorkbook wb) {
		final Map<ExcelCellStyle, CellStyle> styles = new HashMap<>();
		final DataFormat format = wb.createDataFormat();

		this.setCustomColorAtIndex(wb, IndexedColors.LIGHT_ORANGE, 253, 233, 217);
		this.setCustomColorAtIndex(wb, IndexedColors.VIOLET, 228, 223, 236);
		this.setCustomColorAtIndex(wb, IndexedColors.OLIVE_GREEN, 235, 241, 222);
		this.setCustomColorAtIndex(wb, IndexedColors.BLUE, 197, 217, 241);
		this.setCustomColorAtIndex(wb, IndexedColors.AQUA, 218, 238, 243);
		this.setCustomColorAtIndex(wb, IndexedColors.GREY_50_PERCENT, 192, 192, 192);
		this.setCustomColorAtIndex(wb, IndexedColors.RED, 242, 220, 219);

		// default style for all cells in a sheet
		final CellStyle sheetStyle = this.createStyle(wb);
		sheetStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
		styles.put(ExcelCellStyle.SHEET_STYLE, sheetStyle);

		// numeric data format for Numeric values
		final CellStyle numberDataFormatStyle = wb.createCellStyle();
		numberDataFormatStyle.setDataFormat(format.getFormat("0"));
		styles.put(ExcelCellStyle.NUMBER_DATA_FORMAT_STYLE, numberDataFormatStyle);

		// numeric data format for Numeric values with two decimal points
		final CellStyle decimalNumberDataFormatStyle = wb.createCellStyle();
		decimalNumberDataFormatStyle.setDataFormat(format.getFormat("0.00"));
		styles.put(ExcelCellStyle.DECIMAL_NUMBER_DATA_FORMAT_STYLE, decimalNumberDataFormatStyle);

		// numeric data format for Entry No column with highlight color
		final CellStyle numberHighlightColumnStyle = this.createStyle(wb);
		numberHighlightColumnStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
		numberHighlightColumnStyle.setDataFormat(format.getFormat("0"));
		styles.put(ExcelCellStyle.NUMBER_COLUMN_HIGHLIGHT_STYLE_FACTOR, numberHighlightColumnStyle);

		// text data format for Text values
		final CellStyle textDataFormatStyle = wb.createCellStyle();
		textDataFormatStyle.setDataFormat(format.getFormat("@"));
		styles.put(ExcelCellStyle.TEXT_DATA_FORMAT_STYLE, textDataFormatStyle);

		// cell style for labels in the description sheet
		final CellStyle labelStyle = this.createStyleWithBorder(wb);
		labelStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
		final Font labelFont = wb.createFont();
		labelFont.setColor(IndexedColors.BLACK.getIndex());
		labelFont.setFontHeightInPoints((short) 9);
		labelFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		labelStyle.setFont(labelFont);
		styles.put(ExcelCellStyle.LABEL_STYLE, labelStyle);

		// cell style for CONDITION labels
		final CellStyle conditionStyle = this.createStyleWithBorder(wb);
		conditionStyle.setFillForegroundColor(IndexedColors.VIOLET.getIndex());
		styles.put(ExcelCellStyle.LABEL_STYLE_CONDITION, conditionStyle);

		// cell style for FACTOR labels
		final CellStyle factorStyle = this.createStyleWithBorder(wb);
		factorStyle.setFillForegroundColor(IndexedColors.OLIVE_GREEN.getIndex());
		styles.put(ExcelCellStyle.LABEL_STYLE_FACTOR, factorStyle);

		// cell style for FACTOR header in Observation sheet
		final CellStyle headingFactorStyle = this.createStyleWithBorder(wb);
		this.setHeadingFont(wb, headingFactorStyle);
		headingFactorStyle.setFillForegroundColor(IndexedColors.OLIVE_GREEN.getIndex());
		styles.put(ExcelCellStyle.HEADING_STYLE_FACTOR, headingFactorStyle);

		// cell style to highlight the Entry No and Designation
		final CellStyle highlightFactorStyle = this.createStyleWithBorder(wb);
		highlightFactorStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
		styles.put(ExcelCellStyle.TEXT_HIGHLIGHT_STYLE_FACTOR, highlightFactorStyle);

		// cell style to highlight the Designation for Column
		final CellStyle highlightColumnStyle = this.createStyle(wb);
		highlightColumnStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
		styles.put(ExcelCellStyle.COLUMN_HIGHLIGHT_STYLE_FACTOR, highlightColumnStyle);

		// cell style for INVENTORY labels
		final CellStyle inventoryStyle = this.createStyleWithBorder(wb);
		inventoryStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
		styles.put(ExcelCellStyle.LABEL_STYLE_INVENTORY, inventoryStyle);

		// cell style for INVENTORY header in Observation sheet
		final CellStyle headingInventoryStyle = this.createStyleWithBorder(wb);
		this.setHeadingFont(wb, headingInventoryStyle);
		headingInventoryStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
		styles.put(ExcelCellStyle.HEADING_STYLE_INVENTORY, headingInventoryStyle);

		// cell style for VARIATE labels
		final CellStyle variateStyle = this.createStyleWithBorder(wb);
		variateStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
		styles.put(ExcelCellStyle.LABEL_STYLE_VARIATE, variateStyle);

		// cell style for VARIATE header in Observation sheet
		final CellStyle headingVariateStyle = this.createStyleWithBorder(wb);
		this.setHeadingFont(wb, headingVariateStyle);
		headingVariateStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
		styles.put(ExcelCellStyle.HEADING_STYLE_VARIATE, headingVariateStyle);

		// cell style for headings in the description sheet
		final CellStyle headingStyle = this.createStyleWithBorder(wb);
		this.setHeadingFont(wb, headingStyle);
		styles.put(ExcelCellStyle.HEADING_STYLE, headingStyle);

		// cell style for numeric values (left alignment)
		final CellStyle numericStyle = this.createStyleWithBorder(wb);
		numericStyle.setAlignment(CellStyle.ALIGN_LEFT);
		numericStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		numericStyle.setDataFormat(format.getFormat("0"));
		styles.put(ExcelCellStyle.NUMERIC_STYLE, numericStyle);

		// cell style for text
		final CellStyle textStyle = this.createStyleWithBorder(wb);
		textStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		styles.put(ExcelCellStyle.TEXT_STYLE, textStyle);

		return styles;
	}

	private CellStyle createStyle(final Workbook wb) {
		final CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
		cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		return cellStyle;
	}

	private CellStyle createStyleWithBorder(final Workbook wb) {
		final CellStyle cellStyle = this.createStyle(wb);
		cellStyle.setBorderTop(CellStyle.BORDER_THIN);
		cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
		cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
		cellStyle.setBorderRight(CellStyle.BORDER_THIN);
		return cellStyle;
	}

	private void setHeadingFont(final Workbook wb, final CellStyle headingStyle) {
		final Font headingFont = wb.createFont();
		headingFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headingStyle.setFont(headingFont);
		headingStyle.setAlignment(CellStyle.ALIGN_CENTER);
		headingStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	}

	private void setCustomColorAtIndex(final HSSFWorkbook wb, final IndexedColors indexedColor, final int red, final int green,
			final int blue) {

		final HSSFPalette customPalette = wb.getCustomPalette();
		customPalette.setColorAtIndex(indexedColor.index, (byte) red, (byte) green, (byte) blue);

	}

}
