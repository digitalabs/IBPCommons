
package org.generationcp.commons.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import org.apache.poi.ss.usermodel.DataFormat;
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
import org.generationcp.commons.service.GermplasmExportService;
import org.generationcp.commons.service.FileService;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.interfaces.GermplasmExportSource;
import org.generationcp.middleware.pojos.GermplasmList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;

public class GermplasmExportServiceImpl implements GermplasmExportService {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmExportServiceImpl.class);

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
	public static final String NUMBER_DATA_FORMAT_STYLE = "numberDataFormatStyle";
	public static final String TEXT_DATA_FORMAT_STYLE = "textDataFormatStyle";
	public static final String TEXT_HIGHLIGHT_STYLE_FACTOR = "textHightlightFactor";
	public static final String COLUMN_HIGHLIGHT_STYLE_FACTOR = "columnHighlightFactor";
	public static final String NUMBER_COLUMN_HIGHLIGHT_STYLE_FACTOR = "numberColumnHighlightFactor";
	public static final String DECIMAL_NUMBER_DATA_FORMAT_STYLE = "decimalNumberDataFormatStyle";

	@Resource
	private FileService fileService;

	private String templateFile;

	@Override
	public File generateCSVFile(final List<Map<Integer, ExportColumnValue>> exportColumnValues,
			final List<ExportColumnHeader> exportColumnHeaders, final String fileNameFullPath) throws IOException {
		return this.generateCSVFile(exportColumnValues, exportColumnHeaders, fileNameFullPath, true);
	}

	@Override
	public File generateCSVFile(final List<Map<Integer, ExportColumnValue>> exportColumnValues,
			final List<ExportColumnHeader> exportColumnHeaders, final String fileNameFullPath, final boolean includeHeader)
			throws IOException {
		final File newFile = new File(fileNameFullPath);

		final CSVWriter writer =
				new CSVWriter(new OutputStreamWriter(new FileOutputStream(fileNameFullPath), "UTF-8"), ',', CSVWriter.NO_QUOTE_CHARACTER);

		// feed in your array (or convert your data to an array)
		final List<String[]> rowValues = new ArrayList<String[]>();
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

	protected String[] getColumnValues(final Map<Integer, ExportColumnValue> exportColumnMap,
			final List<ExportColumnHeader> exportColumnHeaders) {
		final List<String> values = new ArrayList<String>();
		for (int i = 0; i < exportColumnHeaders.size(); i++) {
			final ExportColumnHeader exportColumnHeader = exportColumnHeaders.get(i);
			if (exportColumnHeader.isDisplay()) {
				final ExportColumnValue exportColumnValue = exportColumnMap.get(exportColumnHeader.getId());
				String colName = "";
				if (exportColumnValue != null) {
					final String value = exportColumnValue.getValue();
					colName = this.cleanNameValueCommas(value);
				}
				values.add(colName);
			}
		}
		return values.toArray(new String[0]);
	}

	protected String[] getColumnHeaderNames(final List<ExportColumnHeader> exportColumnHeaders) {
		final List<String> values = new ArrayList<String>();
		for (int i = 0; i < exportColumnHeaders.size(); i++) {
			final ExportColumnHeader exportColumnHeader = exportColumnHeaders.get(i);
			if (exportColumnHeader.isDisplay()) {
				values.add(this.cleanNameValueCommas(exportColumnHeader.getName()));
			}
		}
		return values.toArray(new String[0]);
	}

	protected String cleanNameValueCommas(final String param) {
		if (param != null) {
			return param.replaceAll(",", "_");
		}
		return "";
	}

	@Override
	public FileOutputStream generateExcelFileForSingleSheet(final List<Map<Integer, ExportColumnValue>> exportColumnValues,
			final List<ExportColumnHeader> exportColumnHeaders, final String filename, final String sheetName) throws IOException {

		final HSSFWorkbook wb = this.createWorkbookForSingleSheet(exportColumnValues, exportColumnHeaders, sheetName);

		try {
			// write the excel file
			final FileOutputStream fileOutputStream = new FileOutputStream(filename);
			wb.write(fileOutputStream);
			fileOutputStream.close();
			return fileOutputStream;
		} catch (final IOException ex) {
			throw new IOException("Error with writing to: " + filename, ex);
		}
	}

	protected HSSFWorkbook createWorkbookForSingleSheet(final List<Map<Integer, ExportColumnValue>> exportColumnValues,
			final List<ExportColumnHeader> exportColumnHeaders, final String sheetName) {
		final HSSFWorkbook wb = new HSSFWorkbook();
		final HSSFSheet sheet = wb.createSheet(sheetName);

		int rowIndex = 0;
		this.writeColumHeaders(exportColumnHeaders, wb, sheet, rowIndex);
		rowIndex++;

		rowIndex = this.writeColumnValues(exportColumnHeaders, exportColumnValues, sheet, rowIndex);

		for (int ctr = 0; ctr < rowIndex; ctr++) {
			sheet.autoSizeColumn(rowIndex);
		}
		return wb;
	}

	protected int writeColumnValues(final List<ExportColumnHeader> exportColumnHeaders,
			final List<Map<Integer, ExportColumnValue>> exportColumnValues, final HSSFSheet sheet, final int rowIndex) {
		int currentRowIndex = rowIndex;
		for (final Map<Integer, ExportColumnValue> exportRowValue : exportColumnValues) {
			final HSSFRow row = sheet.createRow(currentRowIndex);

			int columnIndex = 0;
			for (final ExportColumnHeader columnHeader : exportColumnHeaders) {
				final ExportColumnValue columnValue = exportRowValue.get(columnHeader.getId());
				row.createCell(columnIndex).setCellValue(columnValue.getValue());
				columnIndex++;
			}
			currentRowIndex++;

		}
		return currentRowIndex;
	}

	protected void writeColumHeaders(final List<ExportColumnHeader> exportColumnHeaders, final HSSFWorkbook xlsBook, final HSSFSheet sheet,
			final int rowIndex) {
		final int noOfColumns = exportColumnHeaders.size();
		final HSSFRow header = sheet.createRow(rowIndex);
		final CellStyle greenBg = this.getHeaderStyle(xlsBook, 51, 153, 102);
		final CellStyle blueBg = this.getHeaderStyle(xlsBook, 51, 51, 153);
		for (int i = 0; i < noOfColumns; i++) {
			final ExportColumnHeader columnHeader = exportColumnHeaders.get(i);
			final Cell cell = header.createCell(i);
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

	private CellStyle getHeaderStyle(final HSSFWorkbook xlsBook, final int c1, final int c2, final int c3) {
		final HSSFPalette palette = xlsBook.getCustomPalette();
		final HSSFColor color = palette.findSimilarColor(c1, c2, c3);
		final short colorIndex = color.getIndex();

		final HSSFFont whiteFont = xlsBook.createFont();
		whiteFont.setColor(new HSSFColor.WHITE().getIndex());

		final CellStyle cellStyle = xlsBook.createCellStyle();
		cellStyle.setFillForegroundColor(colorIndex);
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cellStyle.setFont(whiteFont);

		return cellStyle;
	}

	@Override
	public FileOutputStream generateGermplasmListExcelFile(final GermplasmListExportInputValues input)
			throws GermplasmListExporterException {

		// create workbook
		final HSSFWorkbook wb;
		try {
			wb = (HSSFWorkbook) this.fileService.retrieveWorkbookTemplate(this.templateFile);
		} catch (InvalidFormatException | IOException e) {
			GermplasmExportServiceImpl.LOG.error(e.getMessage(), e);
			throw new GermplasmListExporterException();
		}

		final Map<String, CellStyle> sheetStyles = this.createStyles(wb);

		// create two worksheets - Description and Observations
		this.generateDescriptionSheet(wb, sheetStyles, input);
		this.generateObservationSheet(wb, sheetStyles, input);

		wb.setSheetOrder("Codes", 2);

		final String filename = input.getFileName();
		try {
			// write the excel file
			final FileOutputStream fileOutputStream = new FileOutputStream(filename);
			wb.write(fileOutputStream);
			fileOutputStream.close();
			return fileOutputStream;
		} catch (final Exception ex) {
			GermplasmExportServiceImpl.LOG.error(ex.getMessage(), ex);
			throw new GermplasmListExporterException();
		}
	}

	protected int getNoOfVisibleColumns(final Map<String, Boolean> visibleColumnMap) {
		int count = 0;
		for (final Map.Entry<String, Boolean> column : visibleColumnMap.entrySet()) {
			final Boolean isVisible = column.getValue();
			if (isVisible) {
				count++;
			}
		}
		return count;
	}

	protected void generateObservationSheet(final HSSFWorkbook wb, final Map<String, CellStyle> sheetStyles,
			final GermplasmListExportInputValues input) throws GermplasmListExporterException {

		final HSSFSheet observationSheet = wb.createSheet("Observation");
		this.writeObservationSheet(sheetStyles, observationSheet, input);

		// adjust column widths of observation sheet to fit contents
		final int noOfVisibleColumns = this.getNoOfVisibleColumns(input.getVisibleColumnMap());
		for (int ctr = 0; ctr < noOfVisibleColumns; ctr++) {
			observationSheet.autoSizeColumn(ctr);
		}
	}

	@Override
	public void writeObservationSheet(final Map<String, CellStyle> styles, final HSSFSheet observationSheet,
			final GermplasmListExportInputValues input) throws GermplasmListExporterException {

		final Map<String, Boolean> visibleColumnMap = input.getVisibleColumnMap();
		final Map<Integer, Variable> inventoryStandardVariableMap = input.getInventoryVariableMap();
		input.getGermplasmList();
		final List<? extends GermplasmExportSource> listData = input.getListData();
		final Map<Integer, GermplasmParents> germplasmParentsMap = input.getGermplasmParents();

		this.createListEntriesHeaderRow(styles, observationSheet, input);

		int i = 1;
		for (final GermplasmExportSource data : listData) {
			final HSSFRow listEntry = observationSheet.createRow(i);

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

			if (inventoryStandardVariableMap.containsKey(TermId.STOCKID.getId())) {
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

	public void createListEntriesHeaderRow(final Map<String, CellStyle> styles, final HSSFSheet observationSheet,
			final GermplasmListExportInputValues input) {

		final Map<String, Boolean> visibleColumnMap = input.getVisibleColumnMap();
		final Map<Integer, Term> columnTermMap = input.getColumnTermMap();
		final Map<Integer, Variable> inventoryStandardVariableMap = input.getInventoryVariableMap();
		final Map<Integer, Variable> variateStandardVariableMap = input.getVariateVariableMap();
		final HSSFRow listEntriesHeader = observationSheet.createRow(0);
		listEntriesHeader.setHeightInPoints(18);

		int columnIndex = 0;
		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.ENTRY_ID))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.ENTRY_ID))) {
			final Cell entryIdCell = listEntriesHeader.createCell(columnIndex);
			entryIdCell.setCellValue(this.getTermNameOrDefaultLabel(ColumnLabels.ENTRY_ID, columnTermMap));
			entryIdCell.setCellStyle(styles.get(GermplasmExportServiceImpl.HEADING_STYLE_FACTOR));
			observationSheet.setDefaultColumnStyle(columnIndex, styles.get(GermplasmExportServiceImpl.NUMBER_COLUMN_HIGHLIGHT_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.GID))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.GID))) {
			final Cell gidCell = listEntriesHeader.createCell(columnIndex);
			gidCell.setCellValue(this.getTermNameOrDefaultLabel(ColumnLabels.GID, columnTermMap));
			gidCell.setCellStyle(styles.get(GermplasmExportServiceImpl.HEADING_STYLE_FACTOR));
			observationSheet.setDefaultColumnStyle(columnIndex, styles.get(GermplasmExportServiceImpl.NUMBER_DATA_FORMAT_STYLE));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.ENTRY_CODE))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.ENTRY_CODE))) {
			final Cell entryCodeCell = listEntriesHeader.createCell(columnIndex);
			entryCodeCell.setCellValue(this.getTermNameOrDefaultLabel(ColumnLabels.ENTRY_CODE, columnTermMap));
			entryCodeCell.setCellStyle(styles.get(GermplasmExportServiceImpl.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.DESIGNATION))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.DESIGNATION))) {
			final Cell designationCell = listEntriesHeader.createCell(columnIndex);
			designationCell.setCellValue(this.getTermNameOrDefaultLabel(ColumnLabels.DESIGNATION, columnTermMap));
			designationCell.setCellStyle(styles.get(GermplasmExportServiceImpl.HEADING_STYLE_FACTOR));
			observationSheet.setDefaultColumnStyle(columnIndex, styles.get(GermplasmExportServiceImpl.COLUMN_HIGHLIGHT_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.PARENTAGE))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.PARENTAGE))) {
			final Cell crossCell = listEntriesHeader.createCell(columnIndex);
			crossCell.setCellValue(this.getTermNameOrDefaultLabel(ColumnLabels.PARENTAGE, columnTermMap));
			crossCell.setCellStyle(styles.get(GermplasmExportServiceImpl.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.FEMALE_PARENT))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.FEMALE_PARENT))) {
			final Cell crossCell = listEntriesHeader.createCell(columnIndex);
			crossCell.setCellValue(this.getTermNameOrDefaultLabel(ColumnLabels.FEMALE_PARENT, columnTermMap));
			crossCell.setCellStyle(styles.get(GermplasmExportServiceImpl.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.MALE_PARENT))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.MALE_PARENT))) {
			final Cell crossCell = listEntriesHeader.createCell(columnIndex);
			crossCell.setCellValue(this.getTermNameOrDefaultLabel(ColumnLabels.MALE_PARENT, columnTermMap));
			crossCell.setCellStyle(styles.get(GermplasmExportServiceImpl.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.FGID))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.FGID))) {
			final Cell crossCell = listEntriesHeader.createCell(columnIndex);
			crossCell.setCellValue(this.getTermNameOrDefaultLabel(ColumnLabels.FGID, columnTermMap));
			crossCell.setCellStyle(styles.get(GermplasmExportServiceImpl.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.MGID))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.MGID))) {
			final Cell crossCell = listEntriesHeader.createCell(columnIndex);
			crossCell.setCellValue(this.getTermNameOrDefaultLabel(ColumnLabels.MGID, columnTermMap));
			crossCell.setCellStyle(styles.get(GermplasmExportServiceImpl.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.SEED_SOURCE))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.SEED_SOURCE))) {
			final Cell sourceCell = listEntriesHeader.createCell(columnIndex);
			sourceCell.setCellValue(this.getTermNameOrDefaultLabel(ColumnLabels.SEED_SOURCE, columnTermMap));
			sourceCell.setCellStyle(styles.get(GermplasmExportServiceImpl.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.ENTRY_TYPE))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.ENTRY_TYPE))) {
			final Cell entryTypeCell = listEntriesHeader.createCell(columnIndex);
			entryTypeCell.setCellValue(this.getTermNameOrDefaultLabel(ColumnLabels.ENTRY_TYPE, columnTermMap));
			entryTypeCell.setCellStyle(styles.get(GermplasmExportServiceImpl.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		if (inventoryStandardVariableMap.containsKey(TermId.STOCKID.getId())) {
			final Cell stockIDCell = listEntriesHeader.createCell(columnIndex);
			stockIDCell.setCellValue(input.getInventoryVariableMap().get(TermId.STOCKID.getId()).getName().toUpperCase());
			stockIDCell.setCellStyle(styles.get(GermplasmExportServiceImpl.HEADIING_STYLE_INVENTORY));
			observationSheet.setDefaultColumnStyle(columnIndex, styles.get(GermplasmExportServiceImpl.NUMBER_DATA_FORMAT_STYLE));
			columnIndex++;
		}

		if (inventoryStandardVariableMap.containsKey(TermId.SEED_AMOUNT_G.getId())) {
			final Cell seedAmountCell = listEntriesHeader.createCell(columnIndex);
			seedAmountCell.setCellValue(input.getInventoryVariableMap().get(TermId.SEED_AMOUNT_G.getId()).getName().toUpperCase());
			seedAmountCell.setCellStyle(styles.get(GermplasmExportServiceImpl.HEADIING_STYLE_INVENTORY));
			observationSheet.setDefaultColumnStyle(columnIndex, styles.get(GermplasmExportServiceImpl.DECIMAL_NUMBER_DATA_FORMAT_STYLE));
			columnIndex++;
		}

		if (variateStandardVariableMap.containsKey(TermId.NOTES.getId())) {
			final Cell notesCell = listEntriesHeader.createCell(columnIndex);
			notesCell.setCellValue(variateStandardVariableMap.get(TermId.NOTES.getId()).getName().toUpperCase());
			notesCell.setCellStyle(styles.get(GermplasmExportServiceImpl.HEADIING_STYLE_INVENTORY));
			observationSheet.setDefaultColumnStyle(columnIndex, styles.get(GermplasmExportServiceImpl.TEXT_DATA_FORMAT_STYLE));
			columnIndex++;
		}

	}

	protected String getTermNameOrDefaultLabel(final ColumnLabels columnLabel, final Map<Integer, Term> columnTermMap) {

		final Term term = columnTermMap.get(columnLabel.getTermId().getId());

		if (term != null && !term.getName().isEmpty()) {
			return term.getName().toUpperCase();
		} else {
			return columnLabel.getName().toUpperCase();
		}

	}

	@Override
	public void generateDescriptionSheet(final HSSFWorkbook wb, final Map<String, CellStyle> sheetStyles,
			final GermplasmListExportInputValues input) throws GermplasmListExporterException {

		final Font defaultFont = wb.getFontAt((short) 0);
		defaultFont.setFontHeightInPoints((short) 9);
		final HSSFSheet descriptionSheet = wb.createSheet("Description");
		descriptionSheet.setDefaultRowHeightInPoints(18);
		descriptionSheet.setZoom(10, 8);

		int nextRow = 1;

		nextRow = this.writeListDetailsSection(sheetStyles, descriptionSheet, nextRow, input.getGermplasmList());

		nextRow = this.writeListConditionSection(sheetStyles, descriptionSheet, nextRow + 2, input);

		nextRow = this.writeListFactorSection(sheetStyles, descriptionSheet, nextRow + 2, input);

		nextRow = this.writeListInventorySection(sheetStyles, descriptionSheet, nextRow + 2, input);

		this.writeListVariateSection(sheetStyles, descriptionSheet, nextRow + 2, input);

		this.fillSheetWithCellStyle(sheetStyles.get(GermplasmExportServiceImpl.SHEET_STYLE), descriptionSheet);
		this.setDescriptionColumnsWidth(descriptionSheet);

	}

	public int writeListFactorSection(final Map<String, CellStyle> styles, final HSSFSheet descriptionSheet, final int startingRow,
			final GermplasmListExportInputValues input) {

		final CellStyle headingStyle = styles.get(GermplasmExportServiceImpl.HEADING_STYLE);
		final CellStyle labelStyleFactor = styles.get(GermplasmExportServiceImpl.LABEL_STYLE_FACTOR);
		final CellStyle textStyle = styles.get(GermplasmExportServiceImpl.TEXT_STYLE);

		final Map<String, Boolean> visibleColumnMap = input.getVisibleColumnMap();
		final Map<Integer, Term> columnTermMap = input.getColumnTermMap();

		int actualRow = startingRow - 1;

		final HSSFRow factorDetailsHeader = descriptionSheet.createRow(actualRow);
		this.createCell(0, factorDetailsHeader, headingStyle, GermplasmExportServiceImpl.FACTOR);
		this.createCell(1, factorDetailsHeader, headingStyle, GermplasmExportServiceImpl.DESCRIPTION);
		this.createCell(2, factorDetailsHeader, headingStyle, GermplasmExportServiceImpl.PROPERTY);
		this.createCell(3, factorDetailsHeader, headingStyle, GermplasmExportServiceImpl.SCALE);
		this.createCell(4, factorDetailsHeader, headingStyle, GermplasmExportServiceImpl.METHOD);
		this.createCell(5, factorDetailsHeader, headingStyle, GermplasmExportServiceImpl.DATA_TYPE);
		this.createCell(6, factorDetailsHeader, headingStyle, "");
		this.createCell(7, factorDetailsHeader, headingStyle, GermplasmExportServiceImpl.COMMENTS);

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.ENTRY_ID))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.ENTRY_ID))) {

			final Term termEntry = columnTermMap.get(ColumnLabels.ENTRY_ID.getTermId().getId());
			final HSSFRow entryIdRow = descriptionSheet.createRow(++actualRow);

			if (termEntry != null && Objects.equals(termEntry.getVocabularyId(), CvId.VARIABLES.getId())) {
				final Variable variable = (Variable) termEntry;
				this.writeStandardVariableToRow(entryIdRow, labelStyleFactor, styles.get(
						GermplasmExportServiceImpl.TEXT_HIGHLIGHT_STYLE_FACTOR),
						variable);
				this.createCell(7, entryIdRow, textStyle, "Sequence number - mandatory");

			}

		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.GID))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.GID))) {

			final Variable gid = (Variable) columnTermMap.get(ColumnLabels.GID.getTermId().getId());
			final HSSFRow gidRow = descriptionSheet.createRow(++actualRow);

			if (gid != null) {

				this.writeStandardVariableToRow(gidRow, labelStyleFactor, textStyle, gid);
				this.createCell(7, gidRow, textStyle, "GID value if known (or leave blank)");

			}

		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.ENTRY_CODE))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.ENTRY_CODE))) {

			final Variable entryCode = (Variable)  columnTermMap.get(ColumnLabels.ENTRY_CODE.getTermId().getId());
			final HSSFRow entryCodeRow = descriptionSheet.createRow(++actualRow);

			if (entryCode != null) {

				this.writeStandardVariableToRow(entryCodeRow, labelStyleFactor, textStyle, entryCode);
				this.createCell(7, entryCodeRow, textStyle, "Text giving a local entry code - optional");

			}

		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.DESIGNATION))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.DESIGNATION))) {

			final Variable designation = (Variable) columnTermMap.get(ColumnLabels.DESIGNATION.getTermId().getId());
			final HSSFRow designationRow = descriptionSheet.createRow(++actualRow);

			if (designation != null) {

				this.writeStandardVariableToRow(designationRow, labelStyleFactor,
						styles.get(GermplasmExportServiceImpl.TEXT_HIGHLIGHT_STYLE_FACTOR), designation);
				this.createCell(7, designationRow, textStyle, "Germplasm name - mandatory");

			}

		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.PARENTAGE))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.PARENTAGE))) {

			final Variable parentage = (Variable) columnTermMap.get(ColumnLabels.PARENTAGE.getTermId().getId());
			final HSSFRow crossRow = descriptionSheet.createRow(++actualRow);

			if (parentage != null) {

				this.writeStandardVariableToRow(crossRow, labelStyleFactor, textStyle, parentage);
				this.createCell(7, crossRow, textStyle, "Cross string showing parentage - optional");

			}
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.FEMALE_PARENT))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.FEMALE_PARENT))) {

			final Term femaleParent = columnTermMap.get(ColumnLabels.FEMALE_PARENT.getTermId().getId());
			final HSSFRow sourceRow = descriptionSheet.createRow(++actualRow);

			if (femaleParent != null) {
				this.createCell(0, sourceRow, labelStyleFactor, femaleParent.getName());
				this.createCell(1, sourceRow, textStyle, femaleParent.getDefinition());
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

			final Term maleParent = columnTermMap.get(ColumnLabels.MALE_PARENT.getTermId().getId());
			final HSSFRow sourceRow = descriptionSheet.createRow(++actualRow);

			if (maleParent != null) {
				this.createCell(0, sourceRow, labelStyleFactor, maleParent.getName());
				this.createCell(1, sourceRow, textStyle, maleParent.getDefinition());
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

			final Term fgid = columnTermMap.get(ColumnLabels.FGID.getTermId().getId());
			final HSSFRow sourceRow = descriptionSheet.createRow(++actualRow);

			if (fgid != null) {
				this.createCell(0, sourceRow, labelStyleFactor, fgid.getName());
				this.createCell(1, sourceRow, textStyle, fgid.getDefinition());
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

			final Term mgid = columnTermMap.get(ColumnLabels.MGID.getTermId().getId());
			final HSSFRow sourceRow = descriptionSheet.createRow(++actualRow);

			if (mgid != null) {
				this.createCell(0, sourceRow, labelStyleFactor, mgid.getName());
				this.createCell(1, sourceRow, textStyle, mgid.getDefinition());
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

			final Variable seedSource = (Variable) columnTermMap.get(ColumnLabels.SEED_SOURCE.getTermId().getId());
			final HSSFRow sourceRow = descriptionSheet.createRow(++actualRow);

			if (seedSource != null) {

				this.writeStandardVariableToRow(sourceRow, labelStyleFactor, textStyle, seedSource);
				this.createCell(7, sourceRow, textStyle, "Text giving seed source - optional");

			}
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.ENTRY_TYPE))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.ENTRY_TYPE))) {

			final Variable entryType = (Variable) columnTermMap.get(ColumnLabels.ENTRY_TYPE.getTermId().getId());
			final HSSFRow sourceRow = descriptionSheet.createRow(++actualRow);

			if (entryType != null) {

				this.writeStandardVariableToRow(sourceRow, labelStyleFactor, textStyle, entryType);
				this.createCell(7, sourceRow, textStyle, "");

			}
		}

		return actualRow;
	}

	public int writeListConditionSection(final Map<String, CellStyle> styles, final HSSFSheet descriptionSheet, final int startingRow,
			final GermplasmListExportInputValues input) throws GermplasmListExporterException {

		final CellStyle headingStyle = styles.get(GermplasmExportServiceImpl.HEADING_STYLE);
		final CellStyle textStyle = styles.get(GermplasmExportServiceImpl.TEXT_STYLE);
		final CellStyle labelStyleCondition = styles.get(GermplasmExportServiceImpl.LABEL_STYLE_CONDITION);
		final CellStyle numberStyle = styles.get(GermplasmExportServiceImpl.NUMERIC_STYLE);

		// prepare inputs
		final GermplasmList germplasmList = input.getGermplasmList();
		final String ownerName = input.getOwnerName();
		final String exporterName = input.getExporterName();
		final Integer currentLocalIbdbUserId = input.getCurrentLocalIbdbUserId();

		int actualRow = startingRow - 1;

		// write user details
		final HSSFRow conditionDetailsHeading = descriptionSheet.createRow(actualRow);
		this.createCell(0, conditionDetailsHeading, headingStyle, GermplasmExportServiceImpl.CONDITION);
		this.createCell(1, conditionDetailsHeading, headingStyle, GermplasmExportServiceImpl.DESCRIPTION);
		this.createCell(2, conditionDetailsHeading, headingStyle, GermplasmExportServiceImpl.PROPERTY);
		this.createCell(3, conditionDetailsHeading, headingStyle, GermplasmExportServiceImpl.SCALE);
		this.createCell(4, conditionDetailsHeading, headingStyle, GermplasmExportServiceImpl.METHOD);
		this.createCell(5, conditionDetailsHeading, headingStyle, GermplasmExportServiceImpl.DATA_TYPE);
		this.createCell(6, conditionDetailsHeading, headingStyle, GermplasmExportServiceImpl.VALUE);
		this.createCell(7, conditionDetailsHeading, headingStyle, GermplasmExportServiceImpl.COMMENTS);

		final HSSFRow listUserRow = descriptionSheet.createRow(++actualRow);
		this.createCell(0, listUserRow, labelStyleCondition, "LIST USER");
		this.createCell(1, listUserRow, textStyle, "PERSON WHO MADE THE LIST");
		this.createCell(2, listUserRow, textStyle, GermplasmExportServiceImpl.PERSON);
		this.createCell(3, listUserRow, textStyle, "DBCV");
		this.createCell(4, listUserRow, textStyle, GermplasmExportServiceImpl.ASSIGNED);
		this.createCell(5, listUserRow, textStyle, "C");
		this.createCell(6, listUserRow, textStyle, ownerName.trim());
		this.createCell(7, listUserRow, textStyle, "See valid user names and IDs on Codes sheet (or leave blank)");

		final HSSFRow listUserIdRow = descriptionSheet.createRow(++actualRow);
		this.createCell(0, listUserIdRow, labelStyleCondition, "LIST USER ID");
		this.createCell(1, listUserIdRow, textStyle, "ID OF LIST OWNER");
		this.createCell(2, listUserIdRow, textStyle, GermplasmExportServiceImpl.PERSON);
		this.createCell(3, listUserIdRow, textStyle, "DBID");
		this.createCell(4, listUserIdRow, textStyle, GermplasmExportServiceImpl.ASSIGNED);
		this.createCell(5, listUserIdRow, textStyle, "N");
		this.createCell(6, listUserIdRow, numberStyle, germplasmList.getUserId());
		this.createCell(7, listUserIdRow, textStyle, "");

		final HSSFRow listExporterRow = descriptionSheet.createRow(++actualRow);
		this.createCell(0, listExporterRow, labelStyleCondition, "LIST EXPORTER");
		this.createCell(1, listExporterRow, textStyle, "PERSON EXPORTING THE LIST");
		this.createCell(2, listExporterRow, textStyle, GermplasmExportServiceImpl.PERSON);
		this.createCell(3, listExporterRow, textStyle, "DBCV");
		this.createCell(4, listExporterRow, textStyle, GermplasmExportServiceImpl.ASSIGNED);
		this.createCell(5, listExporterRow, textStyle, "C");
		this.createCell(6, listExporterRow, textStyle, exporterName.trim());
		this.createCell(7, listExporterRow, textStyle, "");

		final HSSFRow listExporterIdRow = descriptionSheet.createRow(++actualRow);
		this.createCell(0, listExporterIdRow, labelStyleCondition, "LIST EXPORTER ID");
		this.createCell(1, listExporterIdRow, textStyle, "ID OF LIST EXPORTER");
		this.createCell(2, listExporterIdRow, textStyle, GermplasmExportServiceImpl.PERSON);
		this.createCell(3, listExporterIdRow, textStyle, "DBID");
		this.createCell(4, listExporterIdRow, textStyle, GermplasmExportServiceImpl.ASSIGNED);
		this.createCell(5, listExporterIdRow, textStyle, "N");
		this.createCell(6, listExporterIdRow, numberStyle, currentLocalIbdbUserId);
		this.createCell(7, listExporterIdRow, textStyle, "");

		descriptionSheet.addMergedRegion(new CellRangeAddress(actualRow - 3, actualRow, 7, 7));

		return ++actualRow;
	}

	public int writeListInventorySection(final Map<String, CellStyle> styles, final HSSFSheet descriptionSheet, final int startingRow,
			final GermplasmListExportInputValues input) throws GermplasmListExporterException {

		final CellStyle labelStyleInventory = styles.get(GermplasmExportServiceImpl.LABEL_STYLE_INVENTORY);
		final CellStyle textStyle = styles.get(GermplasmExportServiceImpl.TEXT_STYLE);
		final CellStyle headingStyle = styles.get(GermplasmExportServiceImpl.HEADING_STYLE);

		int actualRow = startingRow;

		if (!input.getInventoryVariableMap().isEmpty()) {

			final HSSFRow conditionDetailsHeading = descriptionSheet.createRow(actualRow);
			this.createCell(0, conditionDetailsHeading, headingStyle, GermplasmExportServiceImpl.INVENTORY);
			this.createCell(1, conditionDetailsHeading, headingStyle, GermplasmExportServiceImpl.DESCRIPTION);
			this.createCell(2, conditionDetailsHeading, headingStyle, GermplasmExportServiceImpl.PROPERTY);
			this.createCell(3, conditionDetailsHeading, headingStyle, GermplasmExportServiceImpl.SCALE);
			this.createCell(4, conditionDetailsHeading, headingStyle, GermplasmExportServiceImpl.METHOD);
			this.createCell(5, conditionDetailsHeading, headingStyle, GermplasmExportServiceImpl.DATA_TYPE);
			this.createCell(6, conditionDetailsHeading, headingStyle, "");
			this.createCell(7, conditionDetailsHeading, headingStyle, GermplasmExportServiceImpl.COMMENTS);

			for (final Variable stdVar : input.getInventoryVariableMap().values()) {
				final HSSFRow row = descriptionSheet.createRow(++actualRow);
				this.writeStandardVariableToRow(row, labelStyleInventory, textStyle, stdVar);

				if (stdVar.getId() == TermId.STOCKID.getId()) {
					this.createCell(7, row, textStyle, "Existing StockID value if known (or leave blank)");
				} else if (stdVar.getId() == TermId.SEED_AMOUNT_G.getId()) {
					this.createCell(7, row, textStyle, "Weight of seed lot in grams - optional; see Codes sheet for more options");
				} else {
					this.createCell(7, row, textStyle, "");
				}

			}

		}

		return actualRow;
	}

	public void writeListVariateSection(final Map<String, CellStyle> styles, final HSSFSheet descriptionSheet, final int startingRow,
			final GermplasmListExportInputValues input) throws GermplasmListExporterException {

		final CellStyle labelStyleVariate = styles.get(GermplasmExportServiceImpl.LABEL_STYLE_VARIATE);
		final CellStyle textStyle = styles.get(GermplasmExportServiceImpl.TEXT_STYLE);
		final CellStyle headingStyle = styles.get(GermplasmExportServiceImpl.HEADING_STYLE);

		int actualRow = startingRow;

		if (!input.getVariateVariableMap().isEmpty()) {

			final HSSFRow conditionDetailsHeading = descriptionSheet.createRow(actualRow);
			this.createCell(0, conditionDetailsHeading, headingStyle, GermplasmExportServiceImpl.VARIATE);
			this.createCell(1, conditionDetailsHeading, headingStyle, GermplasmExportServiceImpl.DESCRIPTION);
			this.createCell(2, conditionDetailsHeading, headingStyle, GermplasmExportServiceImpl.PROPERTY);
			this.createCell(3, conditionDetailsHeading, headingStyle, GermplasmExportServiceImpl.SCALE);
			this.createCell(4, conditionDetailsHeading, headingStyle, GermplasmExportServiceImpl.METHOD);
			this.createCell(5, conditionDetailsHeading, headingStyle, GermplasmExportServiceImpl.DATA_TYPE);
			this.createCell(6, conditionDetailsHeading, headingStyle, "");
			this.createCell(7, conditionDetailsHeading, headingStyle, GermplasmExportServiceImpl.COMMENTS);

			for (final Variable stdVar : input.getVariateVariableMap().values()) {
				final HSSFRow row = descriptionSheet.createRow(++actualRow);
				this.writeStandardVariableToRow(row, labelStyleVariate, textStyle, stdVar);
				if (stdVar.getId() == TermId.NOTES.getId()) {
					this.createCell(7, row, textStyle, "Optional");
				}
			}
		}
	}

	public int writeListDetailsSection(final Map<String, CellStyle> styles, final Sheet descriptionSheet, final int startingRow,
			final GermplasmList germplasmList) {
		int actualRow = startingRow - 1;

		this.writeListDetailsRow(descriptionSheet, styles, actualRow, GermplasmExportServiceImpl.LIST_NAME, germplasmList.getName(),
				"Enter a list name here, or add it when saving in the BMS");

		this.writeListDetailsRow(descriptionSheet, styles, ++actualRow, GermplasmExportServiceImpl.LIST_DESCRIPTION, germplasmList.getDescription(),
				"Enter a list description here, or add it when saving in the BMS");

		this.writeListDetailsRow(descriptionSheet, styles, ++actualRow, GermplasmExportServiceImpl.LIST_TYPE,
				germplasmList.getType(), "See valid list types on Codes sheet for more options");

		this.writeListDetailsRow(descriptionSheet, styles, ++actualRow, GermplasmExportServiceImpl.LIST_DATE, String.valueOf(
				germplasmList.getDate()), "Accepted formats: YYYYMMDD or YYYYMM or YYYY or blank");

		return ++actualRow;
	}

	protected void writeListDetailsRow(final Sheet descriptionSheet, final Map<String, CellStyle> styles, final int rowNumber,
			final String labelName, final String text, final String defaultText) {
		final CellStyle labelStyle = styles.get(GermplasmExportServiceImpl.LABEL_STYLE);
		final CellStyle textStyle = styles.get(GermplasmExportServiceImpl.TEXT_STYLE);

		final HSSFRow row = (HSSFRow) descriptionSheet.createRow(rowNumber);
		this.createCell(0, row, labelStyle, labelName);
		this.createCellRange(descriptionSheet, 1, 2, row, textStyle, text);
		this.createCellRange(descriptionSheet, 3, 6, row, textStyle, defaultText);
	}

	protected void writeStandardVariableToRow(final HSSFRow hssfRow, final CellStyle labelStyleFactor, final CellStyle textStyle,
			final Variable standardVariable) {

		this.createCell(0, hssfRow, labelStyleFactor, standardVariable.getName().toUpperCase());
		this.createCell(1, hssfRow, textStyle, standardVariable.getDefinition());
		this.createCell(2, hssfRow, textStyle, standardVariable.getProperty().getName().toUpperCase());
		this.createCell(3, hssfRow, textStyle, standardVariable.getScale().getName().toUpperCase());
		this.createCell(4, hssfRow, textStyle, standardVariable.getMethod().getName().toUpperCase());
		this.createCell(5, hssfRow, textStyle, standardVariable.getScale().getDataType().getName().substring(0, 1).toUpperCase());
		this.createCell(6, hssfRow, textStyle, "");
		this.createCell(7, hssfRow, textStyle, "");

	}

	@Override
	public Map<String, CellStyle> createStyles(final Workbook wb) {
		final Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		final DataFormat format = wb.createDataFormat();

		this.setCustomColorAtIndex((HSSFWorkbook) wb, IndexedColors.LIGHT_ORANGE, 253, 233, 217);
		this.setCustomColorAtIndex((HSSFWorkbook) wb, IndexedColors.VIOLET, 228, 223, 236);
		this.setCustomColorAtIndex((HSSFWorkbook) wb, IndexedColors.OLIVE_GREEN, 235, 241, 222);
		this.setCustomColorAtIndex((HSSFWorkbook) wb, IndexedColors.BLUE, 197, 217, 241);
		this.setCustomColorAtIndex((HSSFWorkbook) wb, IndexedColors.AQUA, 218, 238, 243);
		this.setCustomColorAtIndex((HSSFWorkbook) wb, IndexedColors.GREY_50_PERCENT, 192, 192, 192);
		this.setCustomColorAtIndex((HSSFWorkbook) wb, IndexedColors.RED, 242, 220, 219);

		// default style for all cells in a sheet
		final CellStyle sheetStyle = this.createStyle(wb);
		sheetStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
		styles.put(GermplasmExportServiceImpl.SHEET_STYLE, sheetStyle);

		// numeric data format for Numeric values
		final CellStyle numberDataFormatStyle = wb.createCellStyle();
		numberDataFormatStyle.setDataFormat(format.getFormat("0"));
		styles.put(GermplasmExportServiceImpl.NUMBER_DATA_FORMAT_STYLE, numberDataFormatStyle);

		// numeric data format for Numeric values with two decimal points
		final CellStyle decimalNumberDataFormatStyle = wb.createCellStyle();
		decimalNumberDataFormatStyle.setDataFormat(format.getFormat("0.00"));
		styles.put(GermplasmExportServiceImpl.DECIMAL_NUMBER_DATA_FORMAT_STYLE, decimalNumberDataFormatStyle);

		// numeric data format for Entry No column with highlight color
		final CellStyle numberHighlightColumnStyle = this.createStyle(wb);
		numberHighlightColumnStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
		numberHighlightColumnStyle.setDataFormat(format.getFormat("0"));
		styles.put(GermplasmExportServiceImpl.NUMBER_COLUMN_HIGHLIGHT_STYLE_FACTOR, numberHighlightColumnStyle);

		// text data format for Text values
		final CellStyle textDataFormatStyle = wb.createCellStyle();
		textDataFormatStyle.setDataFormat(format.getFormat("@"));
		styles.put(GermplasmExportServiceImpl.TEXT_DATA_FORMAT_STYLE, textDataFormatStyle);

		// cell style for labels in the description sheet
		final CellStyle labelStyle = this.createStyleWithBorder(wb);
		labelStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
		final Font labelFont = wb.createFont();
		labelFont.setColor(IndexedColors.BLACK.getIndex());
		labelFont.setFontHeightInPoints((short) 9);
		labelFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		labelStyle.setFont(labelFont);
		styles.put(GermplasmExportServiceImpl.LABEL_STYLE, labelStyle);

		// cell style for CONDITION labels
		final CellStyle conditionStyle = this.createStyleWithBorder(wb);
		conditionStyle.setFillForegroundColor(IndexedColors.VIOLET.getIndex());
		styles.put(GermplasmExportServiceImpl.LABEL_STYLE_CONDITION, conditionStyle);

		// cell style for FACTOR labels
		final CellStyle factorStyle = this.createStyleWithBorder(wb);
		factorStyle.setFillForegroundColor(IndexedColors.OLIVE_GREEN.getIndex());
		styles.put(GermplasmExportServiceImpl.LABEL_STYLE_FACTOR, factorStyle);

		// cell style for FACTOR header in Observation sheet
		final CellStyle headingFactorStyle = this.createStyleWithBorder(wb);
		this.setHeadingFont(wb, headingFactorStyle);
		headingFactorStyle.setFillForegroundColor(IndexedColors.OLIVE_GREEN.getIndex());
		styles.put(GermplasmExportServiceImpl.HEADING_STYLE_FACTOR, headingFactorStyle);

		// cell style to highlight the Entry No and Designation
		final CellStyle highlightFactorStyle = this.createStyleWithBorder(wb);
		highlightFactorStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
		styles.put(GermplasmExportServiceImpl.TEXT_HIGHLIGHT_STYLE_FACTOR, highlightFactorStyle);

		// cell style to highlight the Designation for Column
		final CellStyle highlightColumnStyle = this.createStyle(wb);
		highlightColumnStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
		styles.put(GermplasmExportServiceImpl.COLUMN_HIGHLIGHT_STYLE_FACTOR, highlightColumnStyle);

		// cell style for INVENTORY labels
		final CellStyle inventoryStyle = this.createStyleWithBorder(wb);
		inventoryStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
		styles.put(GermplasmExportServiceImpl.LABEL_STYLE_INVENTORY, inventoryStyle);

		// cell style for INVENTORY header in Observation sheet
		final CellStyle headingInventoryStyle = this.createStyleWithBorder(wb);
		this.setHeadingFont(wb, headingInventoryStyle);
		headingInventoryStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
		styles.put(GermplasmExportServiceImpl.HEADIING_STYLE_INVENTORY, headingInventoryStyle);

		// cell style for VARIATE labels
		final CellStyle variateStyle = this.createStyleWithBorder(wb);
		variateStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
		styles.put(GermplasmExportServiceImpl.LABEL_STYLE_VARIATE, variateStyle);

		// cell style for VARIATE header in Observation sheet
		final CellStyle headingVariateStyle = this.createStyleWithBorder(wb);
		this.setHeadingFont(wb, headingVariateStyle);
		headingVariateStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
		styles.put(GermplasmExportServiceImpl.HEADING_STYLE_VARIATE, headingVariateStyle);

		// cell style for headings in the description sheet
		final CellStyle headingStyle = this.createStyleWithBorder(wb);
		this.setHeadingFont(wb, headingStyle);
		styles.put(GermplasmExportServiceImpl.HEADING_STYLE, headingStyle);

		// cell style for numeric values (left alignment)
		final CellStyle numericStyle = this.createStyleWithBorder(wb);
		numericStyle.setAlignment(CellStyle.ALIGN_LEFT);
		numericStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		numericStyle.setDataFormat(format.getFormat("0"));
		styles.put(GermplasmExportServiceImpl.NUMERIC_STYLE, numericStyle);

		// cell style for text
		final CellStyle textStyle = this.createStyleWithBorder(wb);
		textStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		styles.put(GermplasmExportServiceImpl.TEXT_STYLE, textStyle);

		return styles;
	}

	public Cell createCell(final int column, final HSSFRow row, final CellStyle cellStyle, final String value) {
		final HSSFCell cell = row.createCell(column);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(value);
		return cell;
	}

	/**
	 * We need this method to store numbers as numbers to the excel workbook, otherwise it gives warning message "Number stored as text"
	 * @param column column number
	 * @param row row number
	 * @param cellStyle the cell style
	 * @param value numeric value to store
	 * @return the cell created
	 */
	public Cell createCell(final int column, final HSSFRow row, final CellStyle cellStyle, final double value) {
		final HSSFCell cell = row.createCell(column);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(value);
		return cell;
	}

	public Cell createCellRange(final Sheet sheet, final int start, final int end, final HSSFRow row, final CellStyle cellStyle,
			final String value) {

		for (int x = start; x <= end; x++) {
			final HSSFCell cell = row.createCell(x);
			cell.setCellStyle(cellStyle);
		}

		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), start, end));

		final HSSFCell cell = row.getCell(start);
		cell.setCellValue(value);

		return cell;
	}

	protected CellStyle createStyle(Workbook wb) {
		final CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
		cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		return cellStyle;
	}

	protected CellStyle createStyleWithBorder(final Workbook wb) {
		final CellStyle cellStyle = this.createStyle(wb);
		cellStyle.setBorderTop(CellStyle.BORDER_THIN);
		cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
		cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
		cellStyle.setBorderRight(CellStyle.BORDER_THIN);
		return cellStyle;
	}

	protected void setCustomColorAtIndex(final HSSFWorkbook wb, final IndexedColors indexedColor, final int red, final int green,
			final int blue) {

		final HSSFPalette customPalette = wb.getCustomPalette();
		customPalette.setColorAtIndex(indexedColor.index, (byte) red, (byte) green, (byte) blue);

	}

	protected void fillSheetWithCellStyle(final CellStyle cellStyle, final HSSFSheet sheet) {

		int lastColumnIndex = 0;
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			if (sheet.getRow(i) != null) {
				final short lastCell = sheet.getRow(i).getLastCellNum();
				if (lastCell > lastColumnIndex) {
					lastColumnIndex = lastCell;
				}
			}
		}

		for (int i = 0; i <= lastColumnIndex; i++) {
			sheet.setDefaultColumnStyle(i, cellStyle);
		}

	}

	protected void setHeadingFont(final Workbook wb, final CellStyle headingStyle) {
		final Font headingFont = wb.createFont();
		headingFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headingStyle.setFont(headingFont);
		headingStyle.setAlignment(CellStyle.ALIGN_CENTER);
		headingStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	}

	private void setDescriptionColumnsWidth(final Sheet sheet) {

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

	public void setTemplateFile(final String templateFile) {
		this.templateFile = templateFile;
	}

	private String getColumnNamesTermId(final ColumnLabels columnLabel) {
		if (columnLabel.getTermId() != null) {
			return String.valueOf(columnLabel.getTermId().getId());
		}
		return "";
	}

}
