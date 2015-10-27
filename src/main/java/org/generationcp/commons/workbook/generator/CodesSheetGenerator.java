package org.generationcp.commons.workbook.generator;

import javax.annotation.Resource;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.commons.parsing.ExcelWorkbookRow;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.service.api.OntologyService;

public class CodesSheetGenerator {
	
	private static final int FNAME_WIDTH = 110 * 256 + 200;
	private static final int FCODE_WIDTH = 21 * 256 + 200;
	private static final int INFORMATION_TYPE_WIDTH = 28 * 256 + 200;
	private static final int SECTION_WIDTH = 13 * 256 + 200;

	private static final String SECTION = "Section";
	private static final String INFORMATION_TYPE = "Information Type";
	private static final String FCODE = "fcode";
	private static final String FNAME = "fname";

	@Resource
	private GermplasmDataManager germplasmDataManager;
	
	@Resource
	private ContextUtil contextUtil;
	
	@Resource
	private WorkbenchDataManager workbenchDataManager;
	
	@Resource
	private OntologyService ontologyService;
	
	@Resource
	private ListTypeRowGenerator listTypeRowGenerator;
	
	@Resource
	private UserRowGenerator userRowGenerator;
	
	@Resource
	private NameTypesRowGenerator nameTypesRowGenerator;
	
	@Resource
	private InventoryScalesRowGenerator inventoryScalesRowGenerator;
	
	@Resource
	private AttributeTypesRowGenerator attributeTypesRowGenerator;
	
	@Resource
	private PassportAttributeTypesRowGenerator passportAttributeTypesRowGenerator;
	
	private HSSFWorkbook wb;
	private HSSFSheet codesSheet;
	
	private ExcelCellStyleBuilder sheetStyles;
	
	public void generateCodesSheet(HSSFWorkbook wb) {
		this.wb = wb;
		codesSheet = wb.createSheet("Codes");
		this.sheetStyles = new ExcelCellStyleBuilder(wb);
		
		Font defaultFont = wb.getFontAt((short) 0);
		defaultFont.setFontHeightInPoints((short) 8);
		this.codesSheet.setDefaultRowHeightInPoints(13);
		this.codesSheet.setZoom(10, 8);
		
		this.setCodeSheetHeaders(codesSheet);
		listTypeRowGenerator.addRowsToCodesSheet(codesSheet, sheetStyles);
		userRowGenerator.addRowsToCodesSheet(codesSheet, sheetStyles);
		nameTypesRowGenerator.addRowsToCodesSheet(codesSheet, sheetStyles);
		inventoryScalesRowGenerator.addRowsToCodesSheet(codesSheet, sheetStyles);
		attributeTypesRowGenerator.addRowsToCodesSheet(codesSheet, sheetStyles);
		passportAttributeTypesRowGenerator.addRowsToCodesSheet(codesSheet, sheetStyles);
		this.setCodesColumnsWidth(codesSheet);
	}
	
	private void setCodesColumnsWidth(HSSFSheet sheet) {
		sheet.setColumnWidth(0, SECTION_WIDTH);
		sheet.setColumnWidth(1, INFORMATION_TYPE_WIDTH);
		sheet.setColumnWidth(2, FCODE_WIDTH);
		sheet.setColumnWidth(3, FNAME_WIDTH);
	}

	private void setCodeSheetHeaders(HSSFSheet codesSheet){
		CellStyle headingStyle = sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.CODES_HEADER_STYLE);
		CellStyle headingStyleCenter = sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.CODES_HEADER_STYLE_CENTER);
		
		final ExcelWorkbookRow codesSheetHeader = new ExcelWorkbookRow(codesSheet.createRow(0));
		codesSheetHeader.createCell(0, headingStyle, SECTION);
		codesSheetHeader.createCell(1, headingStyle, INFORMATION_TYPE);
		codesSheetHeader.createCell(2, headingStyleCenter, FCODE);
		codesSheetHeader.createCell(3, headingStyleCenter, FNAME);
	}
	
	HSSFSheet getCodesSheet(){
		return this.codesSheet;
	}
}
