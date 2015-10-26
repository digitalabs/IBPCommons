package org.generationcp.commons.workbook.generator;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.WordUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.commons.parsing.ExcelWorkbookRow;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.service.api.OntologyService;

public class CodesSheetGenerator {
	
	private static final int FNAME_WIDTH = 110 * 256 + 200;

	private static final int FCODE_WIDTH = 21 * 256 + 200;

	private static final int INFORMATION_TYPE_WIDTH = 28 * 256 + 200;

	private static final String SECTION = "Section";

	private static final int SECTION_WIDTH = 13 * 256 + 200;

	private static final String INFORMATION_TYPE = "Information Type";

	private static final String FCODE = "fcode";

	private static final String FNAME = "fname";

	private static final String LISTTYPE = "LISTTYPE";

	private static final String LISTNMS = "LISTNMS";

	private static final String LIST_HEADER = "LIST HEADER";

	private static final String LIST_TYPE = "LIST TYPE";

	private static final String CONDITION = "CONDITION";

	private static final String USER = "USER";

	private static final String NAME = "NAME";

	private static final String NAMES = "NAMES";

	private static final String FACTOR = "FACTOR";

	private static final String NAME_TYPE = "NAME TYPE";

	private static final String INVENTORY = "INVENTORY";

	private static final String SCALES_FOR_INVENTORY_UNITS = "SCALES FOR INVENTORY UNITS";

	private static final String ATTRIBUTE = "ATTRIBUTE";

	private static final String PASSPORT = "PASSPORT";

	private static final String ATRIBUTS = "ATRIBUTS";

	private static final String ATTRIBUTE_TYPE = "ATTRIBUTE TYPE";

	private static final String PASSPORT_ATTRIBUTE_TYPE = "PASSPORT ATTRIBUTE TYPE";

	private static final String VARIATE = "VARIATE";

		@Resource
	private GermplasmDataManager germplasmDataManager;
	
	@Resource
	private ContextUtil contextUtil;
	
	@Resource
	private WorkbenchDataManager workbenchDataManager;
	
	@Resource
	private OntologyService ontologyService;
	
	
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
		
		int currentRow = 0;
		
		this.setCodeSheetHeaders(codesSheet, currentRow);
		currentRow = this.addListTypesToCodesSheet(codesSheet, ++currentRow);
		currentRow = this.addUsersToCodesSheet(codesSheet, currentRow);
		currentRow = this.addNameTypesToCodesSheet(codesSheet, currentRow);
		currentRow = this.addInventoryScalesToCodesSheet(codesSheet, currentRow);
		currentRow = this.addAttributeTypesToCodesSheet(codesSheet, currentRow);
		currentRow = this.addPassportAttributeTypesToCodesSheet(codesSheet, currentRow);
		this.setCodesColumnsWidth(codesSheet);
	}
	
	private void setCodesColumnsWidth(HSSFSheet sheet) {
		sheet.setColumnWidth(0, SECTION_WIDTH);
		sheet.setColumnWidth(1, INFORMATION_TYPE_WIDTH);
		sheet.setColumnWidth(2, FCODE_WIDTH);
		sheet.setColumnWidth(3, FNAME_WIDTH);
	}

	private void setCodeSheetHeaders(HSSFSheet codesSheet, int currentRow){
		CellStyle headingStyle = sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.CODES_HEADER_STYLE);
		CellStyle headingStyleCenter = sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.CODES_HEADER_STYLE_CENTER);
		
		final ExcelWorkbookRow codesSheetHeader = new ExcelWorkbookRow(codesSheet.createRow(currentRow));
		codesSheetHeader.createCell(0, headingStyle, SECTION);
		codesSheetHeader.createCell(1, headingStyle, INFORMATION_TYPE);
		codesSheetHeader.createCell(2, headingStyleCenter, FCODE);
		codesSheetHeader.createCell(3, headingStyleCenter, FNAME);
	}
	
	private int addListTypesToCodesSheet(HSSFSheet codesSheet, int currentRow) {
		CellStyle labelStyle = sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.LIST_HEADER_STYLE);
		CellStyle textDataStyle = sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.TEXT_DATA_FORMAT_STYLE);
		
		List<UserDefinedField> listHeaders = this.germplasmDataManager.getUserDefinedFieldByFieldTableNameAndType(LISTNMS, LISTTYPE);
		ExcelWorkbookRow listHeaderRow;
		
		for(UserDefinedField udField: listHeaders){
			listHeaderRow = new ExcelWorkbookRow(codesSheet.createRow(currentRow));
			listHeaderRow.createCell(0, labelStyle, LIST_HEADER);
			listHeaderRow.createCell(1, labelStyle, LIST_TYPE);
			listHeaderRow.createCell(2, textDataStyle, udField.getFcode());
			listHeaderRow.createCell(3, textDataStyle, WordUtils.capitalizeFully(udField.getFname()));
			currentRow++;
		}
		
		return currentRow;
	}
	
	private int addUsersToCodesSheet(HSSFSheet codesSheet, int currentRow) {
		CellStyle labelStyle = sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.USER_STYLE);
		CellStyle textDataStyle = sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.TEXT_DATA_FORMAT_STYLE);
		
		Project project = contextUtil.getProjectInContext();
		List<User> users = workbenchDataManager.getUsersByProjectId(project.getProjectId());
		ExcelWorkbookRow userRow;
		Person person;
		for(User user: users){
			person = workbenchDataManager.getPersonById(user.getUserid());
			userRow = new ExcelWorkbookRow(codesSheet.createRow(currentRow));
			userRow.createCell(0, labelStyle, CONDITION);
			userRow.createCell(1, labelStyle, USER);
			userRow.createCell(2, textDataStyle, user.getUserid().toString());
			userRow.createCell(3, textDataStyle, person.getDisplayName());
			currentRow++;
		}
		
		return currentRow;
	}
	
	private int addNameTypesToCodesSheet(HSSFSheet codesSheet, int currentRow) {
		CellStyle labelStyle = sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.LABEL_STYLE_FACTOR);
		CellStyle textDataStyle = sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.TEXT_DATA_FORMAT_STYLE);

		List<UserDefinedField> listHeaders = this.germplasmDataManager.getUserDefinedFieldByFieldTableNameAndType(NAMES, NAME);
		ExcelWorkbookRow factorsRow;
		
		for(UserDefinedField udField: listHeaders){
			factorsRow = new ExcelWorkbookRow(codesSheet.createRow(currentRow));
			factorsRow.createCell(0, labelStyle, FACTOR);
			factorsRow.createCell(1, labelStyle, NAME_TYPE);
			factorsRow.createCell(2, textDataStyle, udField.getFcode());
			factorsRow.createCell(3, textDataStyle, udField.getFname());
			currentRow++;
		}
		
		return currentRow;
	}
	
	private int addInventoryScalesToCodesSheet(HSSFSheet codesSheet, int currentRow) {
		CellStyle labelStyle = sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.LABEL_STYLE_INVENTORY);
		CellStyle textDataStyle = sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.TEXT_DATA_FORMAT_STYLE);

		List<org.generationcp.middleware.domain.oms.Scale> inventoryScales = ontologyService.getAllInventoryScales();
		ExcelWorkbookRow factorsRow;
		
		for(org.generationcp.middleware.domain.oms.Scale scale: inventoryScales){
			factorsRow = new ExcelWorkbookRow(codesSheet.createRow(currentRow));
			factorsRow.createCell(0, labelStyle, INVENTORY);
			factorsRow.createCell(1, labelStyle, SCALES_FOR_INVENTORY_UNITS);
			factorsRow.createCell(2, textDataStyle, scale.getDisplayName());
			factorsRow.createCell(3, textDataStyle, scale.getDefinition());
			currentRow++;
		}
		return currentRow;
	}
	
	private int addAttributeTypesToCodesSheet(HSSFSheet codesSheet, int currentRow) {
		CellStyle labelStyle = sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.LABEL_STYLE_VARIATE);
		CellStyle textDataStyle = sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.TEXT_DATA_FORMAT_STYLE);

		List<UserDefinedField> listHeaders = this.germplasmDataManager.getUserDefinedFieldByFieldTableNameAndType(ATRIBUTS, ATTRIBUTE);
		ExcelWorkbookRow attributeTypesRow;
		
		for(UserDefinedField udField: listHeaders){
			attributeTypesRow = new ExcelWorkbookRow(codesSheet.createRow(currentRow));
			attributeTypesRow.createCell(0, labelStyle, VARIATE);
			attributeTypesRow.createCell(1, labelStyle, ATTRIBUTE_TYPE);
			attributeTypesRow.createCell(2, textDataStyle, udField.getFcode());
			attributeTypesRow.createCell(3, textDataStyle, udField.getFname());
			currentRow++;
		}
		
		return currentRow;
	}
	
	private int addPassportAttributeTypesToCodesSheet(HSSFSheet codesSheet, int currentRow) {
		CellStyle labelStyle = sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.LABEL_STYLE_VARIATE);
		CellStyle textDataStyle = sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.TEXT_DATA_FORMAT_STYLE);

		List<UserDefinedField> listHeaders = this.germplasmDataManager.getUserDefinedFieldByFieldTableNameAndType(ATRIBUTS, PASSPORT);
		ExcelWorkbookRow passportAttributeTypesRow;
		
		for(UserDefinedField udField: listHeaders){
			passportAttributeTypesRow = new ExcelWorkbookRow(codesSheet.createRow(currentRow));
			passportAttributeTypesRow.createCell(0, labelStyle, VARIATE);
			passportAttributeTypesRow.createCell(1, labelStyle, PASSPORT_ATTRIBUTE_TYPE);
			passportAttributeTypesRow.createCell(2, textDataStyle, udField.getFcode());
			passportAttributeTypesRow.createCell(3, textDataStyle, udField.getFname());
			currentRow++;
		}
		
		return currentRow;
	}
	
}
