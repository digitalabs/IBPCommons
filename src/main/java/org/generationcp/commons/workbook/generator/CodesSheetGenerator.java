
package org.generationcp.commons.workbook.generator;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.commons.parsing.ExcelWorkbookRow;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CodesSheetGenerator {

	private static final int START_ROW = 0;
	private static final int FNAME_WIDTH = 110 * 256 + 200;
	private static final int FCODE_WIDTH = 21 * 256 + 200;
	private static final int INFORMATION_TYPE_WIDTH = 28 * 256 + 200;
	private static final int SECTION_WIDTH = 13 * 256 + 200;

	private static final String SECTION = "Section";
	private static final String INFORMATION_TYPE = "Information Type";
	private static final String FCODE = "fcode";
	private static final String FNAME = "fname";

	@Resource
	private CodesSheetListTypeRowGenerator listTypeRowGenerator;

	@Resource
	private CodesSheetUserRowGenerator userRowGenerator;

	@Resource
	private CodesSheetNameTypesRowGenerator nameTypesRowGenerator;

	@Resource
	private CodesSheetInventoryScalesRowGenerator inventoryScalesRowGenerator;

	private HSSFWorkbook wb;
	private HSSFSheet codesSheet;

	private ExcelCellStyleBuilder sheetStyles;

	public void generateCodesSheet(final HSSFWorkbook wb) {
		this.wb = wb;
		this.codesSheet = wb.createSheet("Codes");
		this.sheetStyles = new ExcelCellStyleBuilder(wb);

		final Font defaultFont = wb.getFontAt((short) 0);
		defaultFont.setFontHeightInPoints((short) 8);
		this.codesSheet.setDefaultRowHeightInPoints(13);
		this.codesSheet.setZoom(10, 8);

		this.setCodeSheetHeaders(this.codesSheet);
		this.listTypeRowGenerator.addRowsToCodesSheet(this.codesSheet, this.sheetStyles);
		this.userRowGenerator.addRowsToCodesSheet(this.codesSheet, this.sheetStyles);
		this.nameTypesRowGenerator.addRowsToCodesSheet(this.codesSheet, this.sheetStyles);
		this.inventoryScalesRowGenerator.addRowsToCodesSheet(this.codesSheet, this.sheetStyles);
		this.setCodesColumnsWidth(this.codesSheet);
	}

	private void setCodesColumnsWidth(final HSSFSheet sheet) {
		sheet.setColumnWidth(0, CodesSheetGenerator.SECTION_WIDTH);
		sheet.setColumnWidth(1, CodesSheetGenerator.INFORMATION_TYPE_WIDTH);
		sheet.setColumnWidth(2, CodesSheetGenerator.FCODE_WIDTH);
		sheet.setColumnWidth(3, CodesSheetGenerator.FNAME_WIDTH);
	}

	private void setCodeSheetHeaders(final HSSFSheet codesSheet) {
		final CellStyle headingStyle = this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.CODES_HEADER_STYLE);
		final CellStyle headingStyleCenter = this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.CODES_HEADER_STYLE_CENTER);

		final ExcelWorkbookRow codesSheetHeader = new ExcelWorkbookRow(codesSheet.createRow(START_ROW));
		codesSheetHeader.createCell(0, headingStyle, CodesSheetGenerator.SECTION);
		codesSheetHeader.createCell(1, headingStyle, CodesSheetGenerator.INFORMATION_TYPE);
		codesSheetHeader.createCell(2, headingStyleCenter, CodesSheetGenerator.FCODE);
		codesSheetHeader.createCell(3, headingStyleCenter, CodesSheetGenerator.FNAME);
	}

	HSSFSheet getCodesSheet() {
		return this.codesSheet;
	}
}
