
package org.generationcp.commons.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.generationcp.commons.constant.ColumnLabels;
import org.generationcp.commons.exceptions.GermplasmListExporterException;
import org.generationcp.commons.pojo.ExportColumnHeader;
import org.generationcp.commons.pojo.ExportColumnValue;
import org.generationcp.commons.pojo.GermplasmListExportInputValues;
import org.generationcp.commons.pojo.GermplasmParents;
import org.generationcp.commons.service.ExportService;
import org.generationcp.commons.service.FileService;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.interfaces.GermplasmExportSource;
import org.generationcp.middleware.pojos.GermplasmList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;

public class ExportServiceImpl implements ExportService {

	private static final Logger LOG = LoggerFactory.getLogger(ExportServiceImpl.class);

	// List Details
	public static final String LIST_NAME = "LIST NAME";
	public static final String LIST_DESCRIPTION = "LIST DESCRIPTION";
	public static final String LIST_TYPE = "LIST TYPE";
	public static final String LIST_DATE = "LIST DATE";

	// Condition
	public static final String CONDITION = "CONDITION";
	public static final String DESCRIPTION = "DESCRIPTION";
	public static final String PROPERTY = "PROPERTY";
	public static final String SCALE = "SCALE";
	public static final String METHOD = "METHOD";
	public static final String DATA_TYPE = "DATA TYPE";
	public static final String NESTED_IN = "NESTED IN";
	public static final String VALUE = "VALUE";
	public static final String COMMENTS = "COMMENTS";

	// Factor
	public static final String FACTOR = "FACTOR";

	// Values
	public static final String ASSIGNED = "ASSIGNED";
	public static final String PERSON = "PERSON";

	private static final String INVENTORY = "INVENTORY";
	private static final String VARIATE = "VARIATE";

	// Styles
	public static final String LABEL_STYLE = "labelStyle";
	public static final String HEADING_STYLE = "headingStyle";
	public static final String NUMERIC_STYLE = "numericStyle";
	public static final String TEXT_STYLE = "textStyle";
	public static final String LABEL_STYLE_CONDITION = "labelStyleCondition";
	public static final String LABEL_STYLE_FACTOR = "labelStyleFactor";
	public static final String LABEL_STYLE_INVENTORY = "labelStyleInventory";
	public static final String LABEL_STYLE_VARIATE = "labelStyleVariate";
	public static final String HEADING_STYLE_FACTOR = "headingStyleFactor";
	public static final String HEADIING_STYLE_INVENTORY = "headingStyleInventory";
	public static final String HEADING_STYLE_VARIATE = "headingStyleVariate";
	public static final String SHEET_STYLE = "sheetStyle";
	public static final String TEXT_HIGHLIGHT_STYLE_FACTOR = "textHightlightFactor";
	public static final String COLUMN_HIGHLIGHT_STYLE_FACTOR = "columnHighlightFactor";

	@Resource
	private FileService fileService;

	private File templateFile;

	@Override
	public File generateCSVFile(List<Map<Integer, ExportColumnValue>> exportColumnValues, List<ExportColumnHeader> exportColumnHeaders,
			String fileNameFullPath) throws IOException {
		return this.generateCSVFile(exportColumnValues, exportColumnHeaders, fileNameFullPath, true);
	}

	@Override
	public File generateCSVFile(List<Map<Integer, ExportColumnValue>> exportColumnValues, List<ExportColumnHeader> exportColumnHeaders,
			String fileNameFullPath, boolean includeHeader) throws IOException {
		File newFile = new File(fileNameFullPath);

		CSVWriter writer =
				new CSVWriter(new OutputStreamWriter(new FileOutputStream(fileNameFullPath), "UTF-8"), ',', CSVWriter.NO_QUOTE_CHARACTER);

		// feed in your array (or convert your data to an array)
		List<String[]> rowValues = new ArrayList<String[]>();
		if (includeHeader) {
			rowValues.add(this.getColumnHeaderNames(exportColumnHeaders));
		}
		for (int i = 0; i < exportColumnValues.size(); i++) {
			rowValues.add(this.getColumnValues(exportColumnValues.get(i), exportColumnHeaders));
		}
		writer.writeAll(rowValues);
		writer.close();
		return newFile;
	}

	protected String[] getColumnValues(Map<Integer, ExportColumnValue> exportColumnMap, List<ExportColumnHeader> exportColumnHeaders) {
		List<String> values = new ArrayList<String>();
		for (int i = 0; i < exportColumnHeaders.size(); i++) {
			ExportColumnHeader exportColumnHeader = exportColumnHeaders.get(i);
			if (exportColumnHeader.isDisplay()) {
				ExportColumnValue exportColumnValue = exportColumnMap.get(exportColumnHeader.getId());
				String colName = "";
				if (exportColumnValue != null) {
					String value = exportColumnValue.getValue();
					colName = this.cleanNameValueCommas(value);
				}
				values.add(colName);
			}
		}
		return values.toArray(new String[0]);
	}

	protected String[] getColumnHeaderNames(List<ExportColumnHeader> exportColumnHeaders) {
		List<String> values = new ArrayList<String>();
		for (int i = 0; i < exportColumnHeaders.size(); i++) {
			ExportColumnHeader exportColumnHeader = exportColumnHeaders.get(i);
			if (exportColumnHeader.isDisplay()) {
				values.add(this.cleanNameValueCommas(exportColumnHeader.getName()));
			}
		}
		return values.toArray(new String[0]);
	}

	protected String cleanNameValueCommas(String param) {
		if (param != null) {
			return param.replaceAll(",", "_");
		}
		return "";
	}

	@Override
	public FileOutputStream generateExcelFileForSingleSheet(List<Map<Integer, ExportColumnValue>> exportColumnValues,
			List<ExportColumnHeader> exportColumnHeaders, String filename, String sheetName) throws IOException {

		HSSFWorkbook wb = this.createWorkbookForSingleSheet(exportColumnValues, exportColumnHeaders, sheetName);

		try {
			// write the excel file
			FileOutputStream fileOutputStream = new FileOutputStream(filename);
			wb.write(fileOutputStream);
			fileOutputStream.close();
			return fileOutputStream;
		} catch (IOException ex) {
			throw new IOException("Error with writing to: " + filename, ex);
		}
	}

	protected HSSFWorkbook createWorkbookForSingleSheet(List<Map<Integer, ExportColumnValue>> exportColumnValues,
			List<ExportColumnHeader> exportColumnHeaders, String sheetName) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(sheetName);

		int rowIndex = 0;
		this.writeColumHeaders(exportColumnHeaders, wb, sheet, rowIndex);
		rowIndex++;

		rowIndex = this.writeColumnValues(exportColumnHeaders, exportColumnValues, sheet, rowIndex);

		for (int ctr = 0; ctr < rowIndex; ctr++) {
			sheet.autoSizeColumn(rowIndex);
		}
		return wb;
	}

	protected int writeColumnValues(List<ExportColumnHeader> exportColumnHeaders, List<Map<Integer, ExportColumnValue>> exportColumnValues,
			HSSFSheet sheet, int rowIndex) {
		int currentRowIndex = rowIndex;
		for (Map<Integer, ExportColumnValue> exportRowValue : exportColumnValues) {
			HSSFRow row = sheet.createRow(currentRowIndex);

			int columnIndex = 0;
			for (ExportColumnHeader columnHeader : exportColumnHeaders) {
				ExportColumnValue columnValue = exportRowValue.get(columnHeader.getId());
				row.createCell(columnIndex).setCellValue(columnValue.getValue());
				columnIndex++;
			}
			currentRowIndex++;

		}
		return currentRowIndex;
	}

	protected void writeColumHeaders(List<ExportColumnHeader> exportColumnHeaders, HSSFWorkbook xlsBook, HSSFSheet sheet, int rowIndex) {
		int noOfColumns = exportColumnHeaders.size();
		HSSFRow header = sheet.createRow(rowIndex);
		CellStyle greenBg = this.getHeaderStyle(xlsBook, 51, 153, 102);
		CellStyle blueBg = this.getHeaderStyle(xlsBook, 51, 51, 153);
		for (int i = 0; i < noOfColumns; i++) {
			ExportColumnHeader columnHeader = exportColumnHeaders.get(i);
			Cell cell = header.createCell(i);
			if (columnHeader.getHeaderColor() != null) {
				if (columnHeader.getHeaderColor().intValue() == ExportColumnHeader.GREEN.intValue()) {
					cell.setCellStyle(greenBg);
				} else if (columnHeader.getHeaderColor().intValue() == ExportColumnHeader.BLUE.intValue()) {
					cell.setCellStyle(blueBg);
				}
			}
			cell.setCellValue(columnHeader.getName().toUpperCase());
		}
	}

	private CellStyle getHeaderStyle(HSSFWorkbook xlsBook, int c1, int c2, int c3) {
		HSSFPalette palette = xlsBook.getCustomPalette();
		HSSFColor color = palette.findSimilarColor(c1, c2, c3);
		short colorIndex = color.getIndex();

		HSSFFont whiteFont = xlsBook.createFont();
		whiteFont.setColor(new HSSFColor.WHITE().getIndex());

		CellStyle cellStyle = xlsBook.createCellStyle();
		cellStyle.setFillForegroundColor(colorIndex);
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cellStyle.setFont(whiteFont);

		return cellStyle;
	}

	@Override
	public FileOutputStream generateGermplasmListExcelFile(GermplasmListExportInputValues input) throws GermplasmListExporterException {

		// create workbook
		HSSFWorkbook wb;
		try {
			wb = (HSSFWorkbook) this.retrieveTemplate();
		} catch (InvalidFormatException | IOException e) {
			ExportServiceImpl.LOG.error(e.getMessage(), e);
			throw new GermplasmListExporterException();
		}

		Map<String, CellStyle> sheetStyles = this.createStyles(wb);

		// create two worksheets - Description and Observations
		this.generateDescriptionSheet(wb, sheetStyles, input);
		this.generateObservationSheet(wb, sheetStyles, input);

		wb.setSheetOrder("Codes", 2);

		String filename = input.getFileName();
		try {
			// write the excel file
			FileOutputStream fileOutputStream = new FileOutputStream(filename);
			wb.write(fileOutputStream);
			fileOutputStream.close();
			return fileOutputStream;
		} catch (Exception ex) {
			ExportServiceImpl.LOG.error(ex.getMessage(), ex);
			throw new GermplasmListExporterException();
		}
	}

	protected int getNoOfVisibleColumns(Map<String, Boolean> visibleColumnMap) {
		int count = 0;
		for (Map.Entry<String, Boolean> column : visibleColumnMap.entrySet()) {
			Boolean isVisible = column.getValue();
			if (isVisible) {
				count++;
			}
		}
		return count;
	}

	protected void generateObservationSheet(HSSFWorkbook wb, Map<String, CellStyle> sheetStyles, GermplasmListExportInputValues input)
			throws GermplasmListExporterException {

		HSSFSheet observationSheet = wb.createSheet("Observation");
		this.writeObservationSheet(sheetStyles, observationSheet, input);

		// adjust column widths of observation sheet to fit contents
		int noOfVisibleColumns = this.getNoOfVisibleColumns(input.getVisibleColumnMap());
		for (int ctr = 0; ctr < noOfVisibleColumns; ctr++) {
			observationSheet.autoSizeColumn(ctr);
		}
	}

	@Override
	public void writeObservationSheet(Map<String, CellStyle> styles, HSSFSheet observationSheet, GermplasmListExportInputValues input)
			throws GermplasmListExporterException {

		Map<String, Boolean> visibleColumnMap = input.getVisibleColumnMap();
		Map<Integer, StandardVariable> inventoryStandardVariableMap = input.getInventoryStandardVariableMap();
		input.getGermplasmList();
		List<? extends GermplasmExportSource> listData = input.getListData();
		Map<Integer, GermplasmParents> germplasmParentsMap = input.getGermplasmParents();

		this.createListEntriesHeaderRow(styles, observationSheet, input);

		int i = 1;
		for (GermplasmExportSource data : listData) {
			HSSFRow listEntry = observationSheet.createRow(i);

			int j = 0;
			if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.ENTRY_ID))
					&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.ENTRY_ID))) {
				listEntry.createCell(j).setCellValue(data.getEntryId());
				j++;
			}

			if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.GID))
					&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.GID))) {
				listEntry.createCell(j).setCellValue(data.getGermplasmId());
				j++;
			}

			if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.ENTRY_CODE))
					&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.ENTRY_CODE))) {
				listEntry.createCell(j).setCellValue(data.getEntryCode());
				j++;
			}

			if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.DESIGNATION))
					&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.DESIGNATION))) {
				listEntry.createCell(j).setCellValue(data.getDesignation());
				j++;
			}

			if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.PARENTAGE))
					&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.PARENTAGE))) {
				listEntry.createCell(j).setCellValue(data.getGroupName());
				j++;
			}

			if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.FEMALE_PARENT))
					&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.FEMALE_PARENT))) {
				listEntry.createCell(j).setCellValue(germplasmParentsMap.get(data.getGermplasmId()).getFemaleParentName());
				j++;
			}

			if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.MALE_PARENT))
					&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.MALE_PARENT))) {
				listEntry.createCell(j).setCellValue(germplasmParentsMap.get(data.getGermplasmId()).getMaleParentName());
				j++;
			}

			if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.FGID))
					&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.FGID))) {
				listEntry.createCell(j).setCellValue(germplasmParentsMap.get(data.getGermplasmId()).getFgid());
				j++;
			}

			if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.MGID))
					&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.MGID))) {
				listEntry.createCell(j).setCellValue(germplasmParentsMap.get(data.getGermplasmId()).getMgid());
				j++;
			}

			if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.SEED_SOURCE))
					&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.SEED_SOURCE))) {
				listEntry.createCell(j).setCellValue(data.getSeedSource());
				j++;
			}

			if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.ENTRY_TYPE))
					&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.ENTRY_TYPE))) {
				listEntry.createCell(j).setCellValue(data.getCheckTypeDescription());
				j++;
			}

			if (inventoryStandardVariableMap.containsKey(8269)) {
				listEntry.createCell(j).setCellValue(data.getStockIDs());
				j++;
			}

			if (inventoryStandardVariableMap.containsKey(TermId.SEED_AMOUNT_G.getId())) {
				listEntry.createCell(j).setCellValue(data.getSeedAmount());
				j++;
			}

			i += 1;
		}

	}

	public void createListEntriesHeaderRow(Map<String, CellStyle> styles, HSSFSheet observationSheet, GermplasmListExportInputValues input) {

		Map<String, Boolean> visibleColumnMap = input.getVisibleColumnMap();
		Map<Integer, StandardVariable> columnStandardVariableMap = input.getColumnStandardVariableMap();
		Map<Integer, StandardVariable> inventoryStandardVariableMap = input.getInventoryStandardVariableMap();
		HSSFRow listEntriesHeader = observationSheet.createRow(0);
		listEntriesHeader.setHeightInPoints(18);

		int columnIndex = 0;
		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.ENTRY_ID))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.ENTRY_ID))) {
			Cell entryIdCell = listEntriesHeader.createCell(columnIndex);
			entryIdCell.setCellValue(this.getTermNameFromStandardVariable(ColumnLabels.ENTRY_ID, columnStandardVariableMap));
			entryIdCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE_FACTOR));
			observationSheet.setDefaultColumnStyle(columnIndex, styles.get(ExportServiceImpl.COLUMN_HIGHLIGHT_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.GID))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.GID))) {
			Cell gidCell = listEntriesHeader.createCell(columnIndex);
			gidCell.setCellValue(this.getTermNameFromStandardVariable(ColumnLabels.GID, columnStandardVariableMap));
			gidCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.ENTRY_CODE))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.ENTRY_CODE))) {
			Cell entryCodeCell = listEntriesHeader.createCell(columnIndex);
			entryCodeCell.setCellValue(this.getTermNameFromStandardVariable(ColumnLabels.ENTRY_CODE, columnStandardVariableMap));
			entryCodeCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.DESIGNATION))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.DESIGNATION))) {
			Cell designationCell = listEntriesHeader.createCell(columnIndex);
			designationCell.setCellValue(this.getTermNameFromStandardVariable(ColumnLabels.DESIGNATION, columnStandardVariableMap));
			designationCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE_FACTOR));
			observationSheet.setDefaultColumnStyle(columnIndex, styles.get(ExportServiceImpl.COLUMN_HIGHLIGHT_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.PARENTAGE))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.PARENTAGE))) {
			Cell crossCell = listEntriesHeader.createCell(columnIndex);
			crossCell.setCellValue(this.getTermNameFromStandardVariable(ColumnLabels.PARENTAGE, columnStandardVariableMap));
			crossCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.FEMALE_PARENT))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.FEMALE_PARENT))) {
			Cell crossCell = listEntriesHeader.createCell(columnIndex);
			crossCell.setCellValue(this.getTermNameFromStandardVariable(ColumnLabels.FEMALE_PARENT, columnStandardVariableMap));
			crossCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.MALE_PARENT))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.MALE_PARENT))) {
			Cell crossCell = listEntriesHeader.createCell(columnIndex);
			crossCell.setCellValue(this.getTermNameFromStandardVariable(ColumnLabels.MALE_PARENT, columnStandardVariableMap));
			crossCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.FGID))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.FGID))) {
			Cell crossCell = listEntriesHeader.createCell(columnIndex);
			crossCell.setCellValue(this.getTermNameFromStandardVariable(ColumnLabels.FGID, columnStandardVariableMap));
			crossCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.MGID))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.MGID))) {
			Cell crossCell = listEntriesHeader.createCell(columnIndex);
			crossCell.setCellValue(this.getTermNameFromStandardVariable(ColumnLabels.MGID, columnStandardVariableMap));
			crossCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.SEED_SOURCE))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.SEED_SOURCE))) {
			Cell sourceCell = listEntriesHeader.createCell(columnIndex);
			sourceCell.setCellValue(this.getTermNameFromStandardVariable(ColumnLabels.SEED_SOURCE, columnStandardVariableMap));
			sourceCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.ENTRY_TYPE))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.ENTRY_TYPE))) {
			Cell entryTypeCell = listEntriesHeader.createCell(columnIndex);
			entryTypeCell.setCellValue(this.getTermNameFromStandardVariable(ColumnLabels.ENTRY_TYPE, columnStandardVariableMap));
			entryTypeCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		if (inventoryStandardVariableMap.containsKey(8269)) {
			Cell stockIDCell = listEntriesHeader.createCell(columnIndex);
			stockIDCell.setCellValue(input.getInventoryStandardVariableMap().get(8269).getName());
			stockIDCell.setCellStyle(styles.get(ExportServiceImpl.HEADIING_STYLE_INVENTORY));
			columnIndex++;
		}

		if (inventoryStandardVariableMap.containsKey(TermId.SEED_AMOUNT_G.getId())) {
			Cell seedAmountCell = listEntriesHeader.createCell(columnIndex);
			seedAmountCell.setCellValue(input.getInventoryStandardVariableMap().get(TermId.SEED_AMOUNT_G.getId()).getName());
			seedAmountCell.setCellStyle(styles.get(ExportServiceImpl.HEADIING_STYLE_INVENTORY));
			columnIndex++;
		}

	}

	protected String getTermNameFromStandardVariable(ColumnLabels columnLabel, Map<Integer, StandardVariable> columnStandardVariableMap) {

		StandardVariable standardVariable = columnStandardVariableMap.get(columnLabel.getTermId().getId());

		if (standardVariable != null && !standardVariable.getName().isEmpty()) {
			return standardVariable.getName();
		} else {
			return columnLabel.getName();
		}

	}

	@Override
	public void generateDescriptionSheet(HSSFWorkbook wb, Map<String, CellStyle> sheetStyles, GermplasmListExportInputValues input)
			throws GermplasmListExporterException {

		Font defaultFont = wb.getFontAt((short) 0);
		defaultFont.setFontHeightInPoints((short) 9);
		HSSFSheet descriptionSheet = wb.createSheet("Description");
		descriptionSheet.setDefaultRowHeightInPoints(18);
		descriptionSheet.setZoom(10, 8);

		this.writeListDetailsSection(sheetStyles, descriptionSheet, 1, input.getGermplasmList());

		this.writeListConditionSection(sheetStyles, descriptionSheet, 6, input);

		this.writeListFactorSection(sheetStyles, descriptionSheet, 12, input);

		int nextStartingRow = 12 + input.getColumnStandardVariableMap().size() + 2;
		this.writeListInventorySection(sheetStyles, descriptionSheet, nextStartingRow, input);

		nextStartingRow = nextStartingRow + input.getInventoryStandardVariableMap().size() + 2;
		this.writeListVariateSection(sheetStyles, descriptionSheet, nextStartingRow, input);

		this.fillSheetWithCellStyle(sheetStyles.get(ExportServiceImpl.SHEET_STYLE), descriptionSheet);
		this.setDescriptionColumnsWidth(descriptionSheet);

	}

	public void writeListFactorSection(Map<String, CellStyle> styles, HSSFSheet descriptionSheet, int startingRow,
			GermplasmListExportInputValues input) {

		CellStyle headingStyle = styles.get(ExportServiceImpl.HEADING_STYLE);
		CellStyle labelStyleFactor = styles.get(ExportServiceImpl.LABEL_STYLE_FACTOR);
		CellStyle textStyle = styles.get(ExportServiceImpl.TEXT_STYLE);

		Map<String, Boolean> visibleColumnMap = input.getVisibleColumnMap();
		Map<Integer, StandardVariable> columnStandardVariables = input.getColumnStandardVariableMap();

		int actualRow = startingRow - 1;

		HSSFRow factorDetailsHeader = descriptionSheet.createRow(actualRow);
		this.createCell(0, factorDetailsHeader, headingStyle, ExportServiceImpl.FACTOR);
		this.createCell(1, factorDetailsHeader, headingStyle, ExportServiceImpl.DESCRIPTION);
		this.createCell(2, factorDetailsHeader, headingStyle, ExportServiceImpl.PROPERTY);
		this.createCell(3, factorDetailsHeader, headingStyle, ExportServiceImpl.SCALE);
		this.createCell(4, factorDetailsHeader, headingStyle, ExportServiceImpl.METHOD);
		this.createCell(5, factorDetailsHeader, headingStyle, ExportServiceImpl.DATA_TYPE);
		this.createCell(6, factorDetailsHeader, headingStyle, "");
		this.createCell(7, factorDetailsHeader, headingStyle, ExportServiceImpl.COMMENTS);

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.ENTRY_ID))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.ENTRY_ID))) {

			StandardVariable entryNumber = columnStandardVariables.get(ColumnLabels.ENTRY_ID.getTermId().getId());
			HSSFRow entryIdRow = descriptionSheet.createRow(++actualRow);

			if (entryNumber != null) {

				this.writeStandardVariableToRow(entryIdRow, labelStyleFactor, styles.get(ExportServiceImpl.TEXT_HIGHLIGHT_STYLE_FACTOR),
						entryNumber);
				this.createCell(7, entryIdRow, textStyle, "Sequence number - mandatory");

			}

		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.GID))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.GID))) {

			StandardVariable gid = columnStandardVariables.get(ColumnLabels.GID.getTermId().getId());
			HSSFRow gidRow = descriptionSheet.createRow(++actualRow);

			if (gid != null) {

				this.writeStandardVariableToRow(gidRow, labelStyleFactor, textStyle, gid);
				this.createCell(7, gidRow, textStyle, "GID value if known (or leave blank)");

			}

		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.ENTRY_CODE))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.ENTRY_CODE))) {

			StandardVariable entryCode = columnStandardVariables.get(ColumnLabels.ENTRY_CODE.getTermId().getId());
			HSSFRow entryCodeRow = descriptionSheet.createRow(++actualRow);

			if (entryCode != null) {

				this.writeStandardVariableToRow(entryCodeRow, labelStyleFactor, textStyle, entryCode);
				this.createCell(7, entryCodeRow, textStyle, "Text giving a local entry code - optional");

			}

		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.DESIGNATION))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.DESIGNATION))) {

			StandardVariable designation = columnStandardVariables.get(ColumnLabels.DESIGNATION.getTermId().getId());
			HSSFRow designationRow = descriptionSheet.createRow(++actualRow);

			if (designation != null) {

				this.writeStandardVariableToRow(designationRow, labelStyleFactor,
						styles.get(ExportServiceImpl.TEXT_HIGHLIGHT_STYLE_FACTOR), designation);
				this.createCell(7, designationRow, textStyle, "Germplasm name - mandatory");

			}

		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.PARENTAGE))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.PARENTAGE))) {

			StandardVariable parentage = columnStandardVariables.get(ColumnLabels.PARENTAGE.getTermId().getId());
			HSSFRow crossRow = descriptionSheet.createRow(++actualRow);

			if (parentage != null) {

				this.writeStandardVariableToRow(crossRow, labelStyleFactor, textStyle, parentage);
				this.createCell(7, crossRow, textStyle, "Cross string showing parentage - optional");

			}
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.FEMALE_PARENT))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.FEMALE_PARENT))) {

			StandardVariable femaleParent = columnStandardVariables.get(ColumnLabels.FEMALE_PARENT.getTermId().getId());
			HSSFRow sourceRow = descriptionSheet.createRow(++actualRow);

			if (femaleParent != null) {
				this.createCell(0, sourceRow, labelStyleFactor, femaleParent.getName());
				this.createCell(1, sourceRow, textStyle, femaleParent.getDescription());
			} else {
				this.createCell(0, sourceRow, labelStyleFactor, "FEMALE PARENT");
				this.createCell(1, sourceRow, textStyle, "NAME OF FEMALE PARENT");
			}

			this.createCell(2, sourceRow, textStyle, "GERMPLASM ID");
			this.createCell(3, sourceRow, textStyle, "DBCV");
			this.createCell(4, sourceRow, textStyle, "FEMALE SELECTED");
			this.createCell(5, sourceRow, textStyle, "C");
			this.createCell(6, sourceRow, textStyle, "");
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.MALE_PARENT))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.MALE_PARENT))) {

			StandardVariable maleParent = columnStandardVariables.get(ColumnLabels.MALE_PARENT.getTermId().getId());
			HSSFRow sourceRow = descriptionSheet.createRow(++actualRow);

			if (maleParent != null) {
				this.createCell(0, sourceRow, labelStyleFactor, maleParent.getName());
				this.createCell(1, sourceRow, textStyle, maleParent.getDescription());
			} else {
				this.createCell(0, sourceRow, labelStyleFactor, "MALE PARENT");
				this.createCell(1, sourceRow, textStyle, "NAME OF MALE PARENT");
			}
			this.createCell(2, sourceRow, textStyle, "GERMPLASM ID");
			this.createCell(3, sourceRow, textStyle, "DBCV");
			this.createCell(4, sourceRow, textStyle, "MALE SELECTED");
			this.createCell(5, sourceRow, textStyle, "C");
			this.createCell(6, sourceRow, textStyle, "");
		}

		if (visibleColumnMap.containsKey(ColumnLabels.FGID.getName()) && visibleColumnMap.get(ColumnLabels.FGID.getName())) {

			StandardVariable fgid = columnStandardVariables.get(ColumnLabels.FGID.getTermId().getId());
			HSSFRow sourceRow = descriptionSheet.createRow(++actualRow);

			if (fgid != null) {
				this.createCell(0, sourceRow, labelStyleFactor, fgid.getName());
				this.createCell(1, sourceRow, textStyle, fgid.getDescription());
			} else {
				this.createCell(0, sourceRow, labelStyleFactor, "FGID");
				this.createCell(1, sourceRow, textStyle, "GID OF FEMALE PARENT");
			}
			this.createCell(2, sourceRow, textStyle, "GERMPLASM ID");
			this.createCell(3, sourceRow, textStyle, "DBCV");
			this.createCell(4, sourceRow, textStyle, "FEMALE SELECTED");
			this.createCell(5, sourceRow, textStyle, "C");
			this.createCell(6, sourceRow, textStyle, "");
		}

		if (visibleColumnMap.containsKey(ColumnLabels.MGID.getName()) && visibleColumnMap.get(ColumnLabels.MGID.getName())) {

			StandardVariable mgid = columnStandardVariables.get(ColumnLabels.MGID.getTermId().getId());
			HSSFRow sourceRow = descriptionSheet.createRow(++actualRow);

			if (mgid != null) {
				this.createCell(0, sourceRow, labelStyleFactor, mgid.getName());
				this.createCell(1, sourceRow, textStyle, mgid.getDescription());
			} else {
				this.createCell(0, sourceRow, labelStyleFactor, "MGID");
				this.createCell(1, sourceRow, textStyle, "GID OF MALE PARENT");
			}
			this.createCell(2, sourceRow, textStyle, "GERMPLASM ID");
			this.createCell(3, sourceRow, textStyle, "DBCV");
			this.createCell(4, sourceRow, textStyle, "MALE SELECTED");
			this.createCell(5, sourceRow, textStyle, "C");
			this.createCell(6, sourceRow, textStyle, "");
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.SEED_SOURCE))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.SEED_SOURCE))) {

			StandardVariable seedSource = columnStandardVariables.get(ColumnLabels.SEED_SOURCE.getTermId().getId());
			HSSFRow sourceRow = descriptionSheet.createRow(++actualRow);

			if (seedSource != null) {

				this.writeStandardVariableToRow(sourceRow, labelStyleFactor, textStyle, seedSource);
				this.createCell(7, sourceRow, textStyle, "Text giving seed source - optional");

			}
		}
	}

	public void writeListConditionSection(Map<String, CellStyle> styles, HSSFSheet descriptionSheet, int startingRow,
			GermplasmListExportInputValues input) throws GermplasmListExporterException {

		CellStyle headingStyle = styles.get(ExportServiceImpl.HEADING_STYLE);
		CellStyle textStyle = styles.get(ExportServiceImpl.TEXT_STYLE);
		CellStyle labelStyleCondition = styles.get(ExportServiceImpl.LABEL_STYLE_CONDITION);

		// prepare inputs
		GermplasmList germplasmList = input.getGermplasmList();
		String ownerName = input.getOwnerName();
		String exporterName = input.getExporterName();
		Integer currentLocalIbdbUserId = input.getCurrentLocalIbdbUserId();

		int actualRow = startingRow - 1;

		// write user details
		HSSFRow conditionDetailsHeading = descriptionSheet.createRow(actualRow);
		this.createCell(0, conditionDetailsHeading, headingStyle, ExportServiceImpl.CONDITION);
		this.createCell(1, conditionDetailsHeading, headingStyle, ExportServiceImpl.DESCRIPTION);
		this.createCell(2, conditionDetailsHeading, headingStyle, ExportServiceImpl.PROPERTY);
		this.createCell(3, conditionDetailsHeading, headingStyle, ExportServiceImpl.SCALE);
		this.createCell(4, conditionDetailsHeading, headingStyle, ExportServiceImpl.METHOD);
		this.createCell(5, conditionDetailsHeading, headingStyle, ExportServiceImpl.DATA_TYPE);
		this.createCell(6, conditionDetailsHeading, headingStyle, ExportServiceImpl.VALUE);
		this.createCell(7, conditionDetailsHeading, headingStyle, ExportServiceImpl.COMMENTS);

		HSSFRow listUserRow = descriptionSheet.createRow(actualRow + 1);
		this.createCell(0, listUserRow, labelStyleCondition, "LIST USER");
		this.createCell(1, listUserRow, textStyle, "PERSON WHO MADE THE LIST");
		this.createCell(2, listUserRow, textStyle, ExportServiceImpl.PERSON);
		this.createCell(3, listUserRow, textStyle, "DBCV");
		this.createCell(4, listUserRow, textStyle, ExportServiceImpl.ASSIGNED);
		this.createCell(5, listUserRow, textStyle, "C");
		this.createCell(6, listUserRow, textStyle, ownerName.trim());
		this.createCell(7, listUserRow, textStyle, "See valid user names and IDs on Codes sheet (or leave blank)");

		HSSFRow listUserIdRow = descriptionSheet.createRow(actualRow + 2);
		this.createCell(0, listUserIdRow, labelStyleCondition, "LIST USER ID");
		this.createCell(1, listUserIdRow, textStyle, "ID OF LIST OWNER");
		this.createCell(2, listUserIdRow, textStyle, ExportServiceImpl.PERSON);
		this.createCell(3, listUserIdRow, textStyle, "DBID");
		this.createCell(4, listUserIdRow, textStyle, ExportServiceImpl.ASSIGNED);
		this.createCell(5, listUserIdRow, textStyle, "N");
		this.createCell(6, listUserIdRow, textStyle, String.valueOf(germplasmList.getUserId()));
		this.createCell(7, listUserIdRow, textStyle, "");

		HSSFRow listExporterRow = descriptionSheet.createRow(actualRow + 3);
		this.createCell(0, listExporterRow, labelStyleCondition, "LIST EXPORTER");
		this.createCell(1, listExporterRow, textStyle, "PERSON EXPORTING THE LIST");
		this.createCell(2, listExporterRow, textStyle, ExportServiceImpl.PERSON);
		this.createCell(3, listExporterRow, textStyle, "DBCV");
		this.createCell(4, listExporterRow, textStyle, ExportServiceImpl.ASSIGNED);
		this.createCell(5, listExporterRow, textStyle, "C");
		this.createCell(6, listExporterRow, textStyle, exporterName.trim());
		this.createCell(7, listExporterRow, textStyle, "");

		HSSFRow listExporterIdRow = descriptionSheet.createRow(actualRow + 4);
		this.createCell(0, listExporterIdRow, labelStyleCondition, "LIST EXPORTER ID");
		this.createCell(1, listExporterIdRow, textStyle, "ID OF LIST EXPORTER");
		this.createCell(2, listExporterIdRow, textStyle, ExportServiceImpl.PERSON);
		this.createCell(3, listExporterIdRow, textStyle, "DBID");
		this.createCell(4, listExporterIdRow, textStyle, ExportServiceImpl.ASSIGNED);
		this.createCell(5, listExporterIdRow, textStyle, "N");
		this.createCell(6, listExporterIdRow, textStyle, String.valueOf(currentLocalIbdbUserId));
		this.createCell(7, listExporterIdRow, textStyle, "");

		descriptionSheet.addMergedRegion(new CellRangeAddress(actualRow + 1, actualRow + 4, 7, 7));
	}

	public void writeListInventorySection(Map<String, CellStyle> styles, HSSFSheet descriptionSheet, int startingRow,
			GermplasmListExportInputValues input) throws GermplasmListExporterException {

		CellStyle labelStyleInventory = styles.get(ExportServiceImpl.LABEL_STYLE_INVENTORY);
		CellStyle textStyle = styles.get(ExportServiceImpl.TEXT_STYLE);
		CellStyle headingStyle = styles.get(ExportServiceImpl.HEADING_STYLE);

		int actualRow = startingRow;

		if (!input.getInventoryStandardVariableMap().isEmpty()) {

			HSSFRow conditionDetailsHeading = descriptionSheet.createRow(actualRow);
			this.createCell(0, conditionDetailsHeading, headingStyle, ExportServiceImpl.INVENTORY);
			this.createCell(1, conditionDetailsHeading, headingStyle, ExportServiceImpl.DESCRIPTION);
			this.createCell(2, conditionDetailsHeading, headingStyle, ExportServiceImpl.PROPERTY);
			this.createCell(3, conditionDetailsHeading, headingStyle, ExportServiceImpl.SCALE);
			this.createCell(4, conditionDetailsHeading, headingStyle, ExportServiceImpl.METHOD);
			this.createCell(5, conditionDetailsHeading, headingStyle, ExportServiceImpl.DATA_TYPE);
			this.createCell(6, conditionDetailsHeading, headingStyle, "");
			this.createCell(7, conditionDetailsHeading, headingStyle, ExportServiceImpl.COMMENTS);

			for (StandardVariable stdVar : input.getInventoryStandardVariableMap().values()) {
				HSSFRow row = descriptionSheet.createRow(++actualRow);
				this.writeStandardVariableToRow(row, labelStyleInventory, textStyle, stdVar);

				if (stdVar.getId() == 8269) {
					this.createCell(7, row, textStyle, "Existing StockID value if known (or leave blank)");
				} else if (stdVar.getId() == TermId.SEED_AMOUNT_G.getId()) {
					this.createCell(7, row, textStyle, "Weight of seed lot in grams - optional; see Codes sheet for more options");
				} else {
					this.createCell(7, row, textStyle, "");
				}

			}
		}
	}

	public void writeListVariateSection(Map<String, CellStyle> styles, HSSFSheet descriptionSheet, int startingRow,
			GermplasmListExportInputValues input) throws GermplasmListExporterException {

		CellStyle labelStyleVariate = styles.get(ExportServiceImpl.LABEL_STYLE_VARIATE);
		CellStyle textStyle = styles.get(ExportServiceImpl.TEXT_STYLE);
		CellStyle headingStyle = styles.get(ExportServiceImpl.HEADING_STYLE);

		int actualRow = startingRow;

		if (!input.getVariateStandardVariableMap().isEmpty()) {

			HSSFRow conditionDetailsHeading = descriptionSheet.createRow(actualRow);
			this.createCell(0, conditionDetailsHeading, headingStyle, ExportServiceImpl.VARIATE);
			this.createCell(1, conditionDetailsHeading, headingStyle, ExportServiceImpl.DESCRIPTION);
			this.createCell(2, conditionDetailsHeading, headingStyle, ExportServiceImpl.PROPERTY);
			this.createCell(3, conditionDetailsHeading, headingStyle, ExportServiceImpl.SCALE);
			this.createCell(4, conditionDetailsHeading, headingStyle, ExportServiceImpl.METHOD);
			this.createCell(5, conditionDetailsHeading, headingStyle, ExportServiceImpl.DATA_TYPE);
			this.createCell(6, conditionDetailsHeading, headingStyle, "");
			this.createCell(7, conditionDetailsHeading, headingStyle, ExportServiceImpl.COMMENTS);

			for (StandardVariable stdVar : input.getVariateStandardVariableMap().values()) {
				HSSFRow row = descriptionSheet.createRow(++actualRow);
				this.writeStandardVariableToRow(row, labelStyleVariate, textStyle, stdVar);
				if (stdVar.getId() == TermId.NOTES.getId()) {
					this.createCell(7, row, textStyle, "Optional");
				}
			}
		}
	}

	public void writeListDetailsSection(Map<String, CellStyle> styles, Sheet descriptionSheet, int startingRow, GermplasmList germplasmList) {
		int actualRow = startingRow - 1;

		CellStyle labelStyle = styles.get(ExportServiceImpl.LABEL_STYLE);
		CellStyle textStyle = styles.get(ExportServiceImpl.TEXT_STYLE);

		HSSFRow nameRow = (HSSFRow) descriptionSheet.createRow(actualRow);
		this.createCell(0, nameRow, labelStyle, ExportServiceImpl.LIST_NAME);
		this.createCellRange(descriptionSheet, 1, 2, nameRow, textStyle, germplasmList.getName());
		this.createCellRange(descriptionSheet, 3, 6, nameRow, textStyle, "Enter a list name here, or add it when saving in the BMS");

		HSSFRow titleRow = (HSSFRow) descriptionSheet.createRow(actualRow + 1);
		this.createCell(0, titleRow, labelStyle, ExportServiceImpl.LIST_DESCRIPTION);
		this.createCellRange(descriptionSheet, 1, 2, titleRow, textStyle, germplasmList.getDescription());
		this.createCellRange(descriptionSheet, 3, 6, titleRow, textStyle, "Enter a list description here, or add it when saving in the BMS");

		HSSFRow typeRow = (HSSFRow) descriptionSheet.createRow(actualRow + 2);
		this.createCell(0, typeRow, labelStyle, ExportServiceImpl.LIST_TYPE);
		this.createCellRange(descriptionSheet, 1, 2, typeRow, textStyle, germplasmList.getType());
		this.createCellRange(descriptionSheet, 3, 6, typeRow, textStyle, "Accepted formats: YYYYMMDD or YYYYMM or YYYY or blank");

		HSSFRow dateRow = (HSSFRow) descriptionSheet.createRow(actualRow + 3);
		this.createCell(0, dateRow, labelStyle, ExportServiceImpl.LIST_DATE);
		this.createCellRange(descriptionSheet, 1, 2, dateRow, textStyle, String.valueOf(germplasmList.getDate()));
		this.createCellRange(descriptionSheet, 3, 6, dateRow, textStyle, "See valid list types on Codes sheet for more options");

	}

	protected void writeStandardVariableToRow(HSSFRow hssfRow, CellStyle labelStyleFactor, CellStyle textStyle,
			StandardVariable standardVariable) {

		this.createCell(0, hssfRow, labelStyleFactor, standardVariable.getName().toUpperCase());
		this.createCell(1, hssfRow, textStyle, standardVariable.getDescription());
		this.createCell(2, hssfRow, textStyle, standardVariable.getProperty().getName().toUpperCase());
		this.createCell(3, hssfRow, textStyle, standardVariable.getScale().getName().toUpperCase());
		this.createCell(4, hssfRow, textStyle, standardVariable.getMethod().getName().toUpperCase());
		this.createCell(5, hssfRow, textStyle, standardVariable.getDataType().getName().substring(0, 1).toUpperCase());
		this.createCell(6, hssfRow, textStyle, "");
		this.createCell(7, hssfRow, textStyle, "");

	}

	@Override
	public Map<String, CellStyle> createStyles(Workbook wb) {
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();

		this.setCustomColorAtIndex((HSSFWorkbook) wb, IndexedColors.LIGHT_ORANGE, 253, 233, 217);
		this.setCustomColorAtIndex((HSSFWorkbook) wb, IndexedColors.VIOLET, 228, 223, 236);
		this.setCustomColorAtIndex((HSSFWorkbook) wb, IndexedColors.OLIVE_GREEN, 235, 241, 222);
		this.setCustomColorAtIndex((HSSFWorkbook) wb, IndexedColors.BLUE, 197, 217, 241);
		this.setCustomColorAtIndex((HSSFWorkbook) wb, IndexedColors.AQUA, 218, 238, 243);
		this.setCustomColorAtIndex((HSSFWorkbook) wb, IndexedColors.GREY_50_PERCENT, 192, 192, 192);
		this.setCustomColorAtIndex((HSSFWorkbook) wb, IndexedColors.RED, 242, 220, 219);

		// default style for all cells in a sheet
		CellStyle sheetStyle = this.createStyle(wb);
		sheetStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
		styles.put(ExportServiceImpl.SHEET_STYLE, sheetStyle);

		// cell style for labels in the description sheet
		CellStyle labelStyle = this.createStyleWithBorder(wb);
		labelStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
		Font labelFont = wb.createFont();
		labelFont.setColor(IndexedColors.BLACK.getIndex());
		labelFont.setFontHeightInPoints((short) 9);
		labelFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		labelStyle.setFont(labelFont);
		styles.put(ExportServiceImpl.LABEL_STYLE, labelStyle);

		// cell style for CONDITION labels
		CellStyle conditionStyle = this.createStyleWithBorder(wb);
		conditionStyle.setFillForegroundColor(IndexedColors.VIOLET.getIndex());
		styles.put(ExportServiceImpl.LABEL_STYLE_CONDITION, conditionStyle);

		// cell style for FACTOR labels
		CellStyle factorStyle = this.createStyleWithBorder(wb);
		factorStyle.setFillForegroundColor(IndexedColors.OLIVE_GREEN.getIndex());
		styles.put(ExportServiceImpl.LABEL_STYLE_FACTOR, factorStyle);

		// cell style for FACTOR header in Observation sheet
		CellStyle headingFactorStyle = this.createStyleWithBorder(wb);
		this.setHeadingFont(wb, headingFactorStyle);
		headingFactorStyle.setFillForegroundColor(IndexedColors.OLIVE_GREEN.getIndex());
		styles.put(ExportServiceImpl.HEADING_STYLE_FACTOR, headingFactorStyle);

		// cell style to highlight the Entry No and Designation
		CellStyle highlightFactorStyle = this.createStyleWithBorder(wb);
		highlightFactorStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
		styles.put(ExportServiceImpl.TEXT_HIGHLIGHT_STYLE_FACTOR, highlightFactorStyle);

		// cell style to highlight the Entry No and Designation for Column
		CellStyle highlightColumnStyle = this.createStyle(wb);
		highlightFactorStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
		styles.put(ExportServiceImpl.COLUMN_HIGHLIGHT_STYLE_FACTOR, highlightColumnStyle);

		// cell style for INVENTORY labels
		CellStyle inventoryStyle = this.createStyleWithBorder(wb);
		inventoryStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
		styles.put(ExportServiceImpl.LABEL_STYLE_INVENTORY, inventoryStyle);

		// cell style for INVENTORY header in Observation sheet
		CellStyle headingInventoryStyle = this.createStyleWithBorder(wb);
		this.setHeadingFont(wb, headingInventoryStyle);
		headingInventoryStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
		styles.put(ExportServiceImpl.HEADIING_STYLE_INVENTORY, headingInventoryStyle);

		// cell style for VARIATE labels
		CellStyle variateStyle = this.createStyleWithBorder(wb);
		variateStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
		styles.put(ExportServiceImpl.LABEL_STYLE_VARIATE, variateStyle);

		// cell style for VARIATE header in Observation sheet
		CellStyle headingVariateStyle = this.createStyleWithBorder(wb);
		this.setHeadingFont(wb, headingVariateStyle);
		headingVariateStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
		styles.put(ExportServiceImpl.HEADING_STYLE_VARIATE, headingVariateStyle);

		// cell style for headings in the description sheet
		CellStyle headingStyle = this.createStyleWithBorder(wb);
		this.setHeadingFont(wb, headingStyle);
		styles.put(ExportServiceImpl.HEADING_STYLE, headingStyle);

		// cell style for numeric values (left alignment)
		CellStyle numericStyle = this.createStyleWithBorder(wb);
		numericStyle.setAlignment(CellStyle.ALIGN_LEFT);
		numericStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		styles.put(ExportServiceImpl.NUMERIC_STYLE, numericStyle);

		// cell style for text
		CellStyle textStyle = this.createStyleWithBorder(wb);
		textStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		styles.put(ExportServiceImpl.TEXT_STYLE, textStyle);

		return styles;
	}

	public Cell createCell(int column, HSSFRow row, CellStyle cellStyle, String value) {
		HSSFCell cell = row.createCell(column);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(value);
		return cell;
	}

	public Cell createCellRange(Sheet sheet, int start, int end, HSSFRow row, CellStyle cellStyle, String value) {

		for (int x = start; x <= end; x++) {
			HSSFCell cell = row.createCell(x);
			cell.setCellStyle(cellStyle);
		}

		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), start, end));

		HSSFCell cell = row.getCell(start);
		cell.setCellValue(value);

		return cell;
	}

	protected CellStyle createStyle(Workbook wb) {
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
		cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		return cellStyle;
	}

	protected CellStyle createStyleWithBorder(Workbook wb) {
		CellStyle cellStyle = this.createStyle(wb);
		cellStyle.setBorderTop(CellStyle.BORDER_THIN);
		cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
		cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
		cellStyle.setBorderRight(CellStyle.BORDER_THIN);
		return cellStyle;
	}

	protected void setCustomColorAtIndex(HSSFWorkbook wb, IndexedColors indexedColor, int red, int green, int blue) {

		HSSFPalette customPalette = wb.getCustomPalette();
		customPalette.setColorAtIndex(indexedColor.index, (byte) red, (byte) green, (byte) blue);

	}

	protected void fillSheetWithCellStyle(CellStyle cellStyle, HSSFSheet sheet) {

		int lastColumnIndex = 0;
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			if (sheet.getRow(i) != null) {
				short lastCell = sheet.getRow(i).getLastCellNum();
				if (lastCell > lastColumnIndex) {
					lastColumnIndex = lastCell;
				}
			}
		}

		for (int i = 0; i <= lastColumnIndex; i++) {
			sheet.setDefaultColumnStyle(i, cellStyle);
		}

	}

	protected void setHeadingFont(Workbook wb, CellStyle headingStyle) {
		Font headingFont = wb.createFont();
		headingFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headingStyle.setFont(headingFont);
		headingStyle.setAlignment(CellStyle.ALIGN_CENTER);
		headingStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	}

	private void setDescriptionColumnsWidth(Sheet sheet) {

		// column width = ([number of characters] * 256) + 200
		// this is just an approximation

		sheet.setColumnWidth(0, 20 * 256 + 200);
		sheet.setColumnWidth(1, 24 * 256 + 200);
		sheet.setColumnWidth(2, 30 * 256 + 200);
		sheet.setColumnWidth(3, 15 * 256 + 200);
		sheet.setColumnWidth(4, 15 * 256 + 200);
		sheet.setColumnWidth(5, 15 * 256 + 200);
		sheet.setColumnWidth(6, 15 * 256 + 200);
		sheet.setColumnWidth(7, 55 * 256 + 200);
	}

	public Workbook retrieveTemplate() throws IOException, InvalidFormatException {
		try (InputStream is = new FileInputStream(this.templateFile)) {
			String tempFile = this.fileService.saveTemporaryFile(is);

			return this.fileService.retrieveWorkbook(tempFile);
		}
	}

	public void setTemplateFile(File templateFile) {
		this.templateFile = templateFile;
	}

	private String getColumnNamesTermId(ColumnLabels columnLabel) {
		if (columnLabel.getTermId() != null) {
			return String.valueOf(columnLabel.getTermId().getId());
		}
		return "";
	}

}
