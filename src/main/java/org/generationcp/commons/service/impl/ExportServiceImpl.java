
package org.generationcp.commons.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
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
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
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

	// Factor
	public static final String FACTOR = "FACTOR";

	// Values
	public static final String ASSIGNED = "ASSIGNED";
	public static final String PERSON = "PERSON";

	// Styles
	public static final String LABEL_STYLE = "labelStyle";
	public static final String HEADING_STYLE = "headingStyle";
	public static final String NUMERIC_STYLE = "numericStyle";
	private static final String TEXT_STYLE = "textStyle";

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
		HSSFWorkbook wb = new HSSFWorkbook();

		Map<String, CellStyle> sheetStyles = this.createStyles(wb);

		// create two worksheets - Description and Observations
		this.generateDescriptionSheet(wb, sheetStyles, input);
		this.generateObservationSheet(wb, sheetStyles, input);

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
		GermplasmList germplasmList = input.getGermplasmList();
		List<GermplasmListData> listDatas = germplasmList.getListData();
		Map<Integer, GermplasmParents> germplasmParentsMap = input.getGermplasmParents();

		this.createListEntriesHeaderRow(styles, observationSheet, input);

		int i = 1;
		for (GermplasmListData listData : listDatas) {
			HSSFRow listEntry = observationSheet.createRow(i);

			int j = 0;
			if (visibleColumnMap.containsKey(ColumnLabels.ENTRY_ID.getName()) && visibleColumnMap.get(ColumnLabels.ENTRY_ID.getName())) {
				listEntry.createCell(j).setCellValue(listData.getEntryId());
				j++;
			}

			if (visibleColumnMap.containsKey(ColumnLabels.GID.getName()) && visibleColumnMap.get(ColumnLabels.GID.getName())) {
				listEntry.createCell(j).setCellValue(listData.getGid());
				j++;
			}

			if (visibleColumnMap.containsKey(ColumnLabels.ENTRY_CODE.getName()) && visibleColumnMap.get(ColumnLabels.ENTRY_CODE.getName())) {
				listEntry.createCell(j).setCellValue(listData.getEntryCode());
				j++;
			}

			if (visibleColumnMap.containsKey(ColumnLabels.DESIGNATION.getName())
					&& visibleColumnMap.get(ColumnLabels.DESIGNATION.getName())) {
				listEntry.createCell(j).setCellValue(listData.getDesignation());
				j++;
			}

			if (visibleColumnMap.containsKey(ColumnLabels.PARENTAGE.getName()) && visibleColumnMap.get(ColumnLabels.PARENTAGE.getName())) {
				listEntry.createCell(j).setCellValue(listData.getGroupName());
				j++;
			}

			if (visibleColumnMap.containsKey(ColumnLabels.FEMALE_PARENT.getName())
					&& visibleColumnMap.get(ColumnLabels.FEMALE_PARENT.getName())) {
				listEntry.createCell(j).setCellValue(germplasmParentsMap.get(listData.getGid()).getFemaleParentName());
				j++;
			}

			if (visibleColumnMap.containsKey(ColumnLabels.MALE_PARENT.getName())
					&& visibleColumnMap.get(ColumnLabels.MALE_PARENT.getName())) {
				listEntry.createCell(j).setCellValue(germplasmParentsMap.get(listData.getGid()).getMaleParentName());
				j++;
			}

			if (visibleColumnMap.containsKey(ColumnLabels.FGID.getName()) && visibleColumnMap.get(ColumnLabels.FGID.getName())) {
				listEntry.createCell(j).setCellValue(germplasmParentsMap.get(listData.getGid()).getFgid());
				j++;
			}

			if (visibleColumnMap.containsKey(ColumnLabels.MGID.getName()) && visibleColumnMap.get(ColumnLabels.MGID.getName())) {
				listEntry.createCell(j).setCellValue(germplasmParentsMap.get(listData.getGid()).getMgid());
				j++;
			}

			if (visibleColumnMap.containsKey(ColumnLabels.SEED_SOURCE.getName())
					&& visibleColumnMap.get(ColumnLabels.SEED_SOURCE.getName())) {
				listEntry.createCell(j).setCellValue(listData.getSeedSource());
				j++;
			}

			i += 1;
		}

	}

	public void createListEntriesHeaderRow(Map<String, CellStyle> styles, HSSFSheet observationSheet, GermplasmListExportInputValues input) {

		Map<String, Boolean> visibleColumnMap = input.getVisibleColumnMap();
		Map<Integer, StandardVariable> columnStandardVariableMap = input.getColumnStandardVariableMap();
		HSSFRow listEntriesHeader = observationSheet.createRow(0);

		int columnIndex = 0;
		if (visibleColumnMap.containsKey(ColumnLabels.ENTRY_ID.getName()) && visibleColumnMap.get(ColumnLabels.ENTRY_ID.getName())) {
			Cell entryIdCell = listEntriesHeader.createCell(columnIndex);
			entryIdCell.setCellValue(this.getTermNameFromStandardVariable(ColumnLabels.ENTRY_ID, columnStandardVariableMap));
			entryIdCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(ColumnLabels.GID.getName()) && visibleColumnMap.get(ColumnLabels.GID.getName())) {
			Cell gidCell = listEntriesHeader.createCell(columnIndex);
			gidCell.setCellValue(this.getTermNameFromStandardVariable(ColumnLabels.GID, columnStandardVariableMap));
			gidCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(ColumnLabels.ENTRY_CODE.getName()) && visibleColumnMap.get(ColumnLabels.ENTRY_CODE.getName())) {
			Cell entryCodeCell = listEntriesHeader.createCell(columnIndex);
			entryCodeCell.setCellValue(this.getTermNameFromStandardVariable(ColumnLabels.ENTRY_CODE, columnStandardVariableMap));
			entryCodeCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(ColumnLabels.DESIGNATION.getName()) && visibleColumnMap.get(ColumnLabels.DESIGNATION.getName())) {
			Cell designationCell = listEntriesHeader.createCell(columnIndex);
			designationCell.setCellValue(this.getTermNameFromStandardVariable(ColumnLabels.DESIGNATION, columnStandardVariableMap));
			designationCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(ColumnLabels.PARENTAGE.getName()) && visibleColumnMap.get(ColumnLabels.PARENTAGE.getName())) {
			Cell crossCell = listEntriesHeader.createCell(columnIndex);
			crossCell.setCellValue(this.getTermNameFromStandardVariable(ColumnLabels.PARENTAGE, columnStandardVariableMap));
			crossCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(ColumnLabels.FEMALE_PARENT.getName())
				&& visibleColumnMap.get(ColumnLabels.FEMALE_PARENT.getName())) {
			Cell crossCell = listEntriesHeader.createCell(columnIndex);
			crossCell.setCellValue(this.getTermNameFromStandardVariable(ColumnLabels.FEMALE_PARENT, columnStandardVariableMap));
			crossCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(ColumnLabels.MALE_PARENT.getName()) && visibleColumnMap.get(ColumnLabels.MALE_PARENT.getName())) {
			Cell crossCell = listEntriesHeader.createCell(columnIndex);
			crossCell.setCellValue(this.getTermNameFromStandardVariable(ColumnLabels.MALE_PARENT, columnStandardVariableMap));
			crossCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(ColumnLabels.FGID.getName()) && visibleColumnMap.get(ColumnLabels.FGID.getName())) {
			Cell crossCell = listEntriesHeader.createCell(columnIndex);
			crossCell.setCellValue(this.getTermNameFromStandardVariable(ColumnLabels.FGID, columnStandardVariableMap));
			crossCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(ColumnLabels.MGID.getName()) && visibleColumnMap.get(ColumnLabels.MGID.getName())) {
			Cell crossCell = listEntriesHeader.createCell(columnIndex);
			crossCell.setCellValue(this.getTermNameFromStandardVariable(ColumnLabels.MGID, columnStandardVariableMap));
			crossCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(ColumnLabels.SEED_SOURCE.getName()) && visibleColumnMap.get(ColumnLabels.SEED_SOURCE.getName())) {
			Cell sourceCell = listEntriesHeader.createCell(columnIndex);
			sourceCell.setCellValue(this.getTermNameFromStandardVariable(ColumnLabels.SEED_SOURCE, columnStandardVariableMap));
			sourceCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE));
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

		HSSFSheet descriptionSheet = wb.createSheet("Description");

		this.writeListDetailsSection(sheetStyles, descriptionSheet, 1, input.getGermplasmList());
		this.writeListConditionSection(sheetStyles, descriptionSheet, 6, input);
		this.writeListFactorSection(sheetStyles, descriptionSheet, 12, input);

		// adjust column widths of description sheet to fit contents
		int noOfVisibleColumns = this.getNoOfVisibleColumns(input.getVisibleColumnMap());
		for (int ctr = 0; ctr < noOfVisibleColumns; ctr++) {
			descriptionSheet.autoSizeColumn(ctr);
		}
	}

	public void writeListFactorSection(Map<String, CellStyle> styles, HSSFSheet descriptionSheet, int startingRow,
			GermplasmListExportInputValues input) {

		Map<String, Boolean> visibleColumnMap = input.getVisibleColumnMap();
		Map<Integer, StandardVariable> columnStandardVariables = input.getColumnStandardVariableMap();

		int actualRow = startingRow - 1;

		HSSFRow factorDetailsHeader = descriptionSheet.createRow(actualRow);
		Cell factorCell = factorDetailsHeader.createCell(0);
		factorCell.setCellValue(ExportServiceImpl.FACTOR);
		factorCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE));
		Cell descriptionCell = factorDetailsHeader.createCell(1);
		descriptionCell.setCellValue(ExportServiceImpl.DESCRIPTION);
		descriptionCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE));
		Cell propertyCell = factorDetailsHeader.createCell(2);
		propertyCell.setCellValue(ExportServiceImpl.PROPERTY);
		propertyCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE));
		Cell scaleCell = factorDetailsHeader.createCell(3);
		scaleCell.setCellValue(ExportServiceImpl.SCALE);
		scaleCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE));
		Cell methodCell = factorDetailsHeader.createCell(4);
		methodCell.setCellValue(ExportServiceImpl.METHOD);
		methodCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE));
		Cell dataTypeCell = factorDetailsHeader.createCell(5);
		dataTypeCell.setCellValue(ExportServiceImpl.DATA_TYPE);
		dataTypeCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE));
		Cell spaceCell = factorDetailsHeader.createCell(6);
		spaceCell.setCellValue(ExportServiceImpl.NESTED_IN);
		spaceCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE));

		if (visibleColumnMap.containsKey(ColumnLabels.ENTRY_ID.getName()) && visibleColumnMap.get(ColumnLabels.ENTRY_ID.getName())) {

			StandardVariable entryNumber = columnStandardVariables.get(ColumnLabels.ENTRY_ID.getTermId().getId());
			HSSFRow entryIdRow = descriptionSheet.createRow(++actualRow);

			if (entryNumber != null) {

				this.writeStandardVariableToRow(entryIdRow, entryNumber);

			} else {
				entryIdRow.createCell(0).setCellValue("ENTRY");
				entryIdRow.createCell(1).setCellValue("The germplasm entry number");
				entryIdRow.createCell(2).setCellValue("GERMPLASM ENTRY");
				entryIdRow.createCell(3).setCellValue("NUMBER");
				entryIdRow.createCell(4).setCellValue("ENUMERATED");
				entryIdRow.createCell(5).setCellValue("N");
				entryIdRow.createCell(6).setCellValue("");
			}

		}

		if (visibleColumnMap.containsKey(ColumnLabels.GID.getName()) && visibleColumnMap.get(ColumnLabels.GID.getName())) {

			StandardVariable gid = columnStandardVariables.get(ColumnLabels.GID.getTermId().getId());
			HSSFRow gidRow = descriptionSheet.createRow(++actualRow);

			if (gid != null) {

				this.writeStandardVariableToRow(gidRow, gid);

			} else {
				gidRow.createCell(0).setCellValue("GID");
				gidRow.createCell(1).setCellValue("The GID of the germplasm");
				gidRow.createCell(2).setCellValue("GERMPLASM ID");
				gidRow.createCell(3).setCellValue("DBID");
				gidRow.createCell(4).setCellValue(ExportServiceImpl.ASSIGNED);
				gidRow.createCell(5).setCellValue("N");
				gidRow.createCell(6).setCellValue("");
			}

		}

		if (visibleColumnMap.containsKey(ColumnLabels.ENTRY_CODE.getName()) && visibleColumnMap.get(ColumnLabels.ENTRY_CODE.getName())) {

			StandardVariable entryCode = columnStandardVariables.get(ColumnLabels.ENTRY_CODE.getTermId().getId());
			HSSFRow entryCodeRow = descriptionSheet.createRow(++actualRow);

			if (entryCode != null) {

				this.writeStandardVariableToRow(entryCodeRow, entryCode);

			} else {
				entryCodeRow.createCell(0).setCellValue("ENTRY CODE");
				entryCodeRow.createCell(1).setCellValue("Germplasm entry code");
				entryCodeRow.createCell(2).setCellValue("GERMPLASM ENTRY");
				entryCodeRow.createCell(3).setCellValue("CODE");
				entryCodeRow.createCell(4).setCellValue(ExportServiceImpl.ASSIGNED);
				entryCodeRow.createCell(5).setCellValue("C");
				entryCodeRow.createCell(6).setCellValue("");
			}

		}

		if (visibleColumnMap.containsKey(ColumnLabels.DESIGNATION.getName()) && visibleColumnMap.get(ColumnLabels.DESIGNATION.getName())) {

			StandardVariable designation = columnStandardVariables.get(ColumnLabels.DESIGNATION.getTermId().getId());
			HSSFRow designationRow = descriptionSheet.createRow(++actualRow);

			if (designation != null) {

				this.writeStandardVariableToRow(designationRow, designation);

			} else {
				designationRow.createCell(0).setCellValue("DESIGNATION");
				designationRow.createCell(1).setCellValue("The name of the germplasm");
				designationRow.createCell(2).setCellValue("GERMPLASM ID");
				designationRow.createCell(3).setCellValue("DBCV");
				designationRow.createCell(4).setCellValue(ExportServiceImpl.ASSIGNED);
				designationRow.createCell(5).setCellValue("C");
				designationRow.createCell(6).setCellValue("");
			}

		}

		if (visibleColumnMap.containsKey(ColumnLabels.PARENTAGE.getName()) && visibleColumnMap.get(ColumnLabels.PARENTAGE.getName())) {

			StandardVariable parentage = columnStandardVariables.get(ColumnLabels.PARENTAGE.getTermId().getId());
			HSSFRow crossRow = descriptionSheet.createRow(++actualRow);

			if (parentage != null) {

				this.writeStandardVariableToRow(crossRow, parentage);

			} else {
				crossRow.createCell(0).setCellValue("CROSS");
				crossRow.createCell(1).setCellValue("The pedigree string of the germplasm");
				crossRow.createCell(2).setCellValue("CROSS NAME");
				crossRow.createCell(3).setCellValue("NAME");
				crossRow.createCell(4).setCellValue(ExportServiceImpl.ASSIGNED);
				crossRow.createCell(5).setCellValue("C");
				crossRow.createCell(6).setCellValue("");
			}

		}

		if (visibleColumnMap.containsKey(ColumnLabels.FEMALE_PARENT.getName())
				&& visibleColumnMap.get(ColumnLabels.FEMALE_PARENT.getName())) {

			StandardVariable femaleParent = columnStandardVariables.get(ColumnLabels.FEMALE_PARENT.getTermId().getId());
			HSSFRow sourceRow = descriptionSheet.createRow(++actualRow);

			if (femaleParent != null) {
				sourceRow.createCell(0).setCellValue(femaleParent.getName());
				sourceRow.createCell(1).setCellValue(femaleParent.getDescription());
			} else {
				sourceRow.createCell(0).setCellValue("FEMALE PARENT");
				sourceRow.createCell(1).setCellValue("NAME OF FEMALE PARENT");
			}

			sourceRow.createCell(2).setCellValue("GERMPLASM ID");
			sourceRow.createCell(3).setCellValue("DBCV");
			sourceRow.createCell(4).setCellValue("FEMALE SELECTED");
			sourceRow.createCell(5).setCellValue("C");
			sourceRow.createCell(6).setCellValue("");
		}

		if (visibleColumnMap.containsKey(ColumnLabels.MALE_PARENT.getName()) && visibleColumnMap.get(ColumnLabels.MALE_PARENT.getName())) {

			StandardVariable maleParent = columnStandardVariables.get(ColumnLabels.MALE_PARENT.getTermId().getId());
			HSSFRow sourceRow = descriptionSheet.createRow(++actualRow);

			if (maleParent != null) {
				sourceRow.createCell(0).setCellValue(maleParent.getName());
				sourceRow.createCell(1).setCellValue(maleParent.getDescription());
			} else {
				sourceRow.createCell(0).setCellValue("MALE PARENT");
				sourceRow.createCell(1).setCellValue("NAME OF MALE PARENT");
			}
			sourceRow.createCell(2).setCellValue("GERMPLASM ID");
			sourceRow.createCell(3).setCellValue("DBCV");
			sourceRow.createCell(4).setCellValue("MALE SELECTED");
			sourceRow.createCell(5).setCellValue("C");
			sourceRow.createCell(6).setCellValue("");
		}

		if (visibleColumnMap.containsKey(ColumnLabels.FGID.getName()) && visibleColumnMap.get(ColumnLabels.FGID.getName())) {

			StandardVariable fgid = columnStandardVariables.get(ColumnLabels.FGID.getTermId().getId());
			HSSFRow sourceRow = descriptionSheet.createRow(++actualRow);

			if (fgid != null) {
				sourceRow.createCell(0).setCellValue(fgid.getName());
				sourceRow.createCell(1).setCellValue(fgid.getDescription());
			} else {
				sourceRow.createCell(0).setCellValue("FGID");
				sourceRow.createCell(1).setCellValue("GID OF FEMALE PARENT");
			}
			sourceRow.createCell(2).setCellValue("GERMPLASM ID");
			sourceRow.createCell(3).setCellValue("DBCV");
			sourceRow.createCell(4).setCellValue("FEMALE SELECTED");
			sourceRow.createCell(5).setCellValue("C");
			sourceRow.createCell(6).setCellValue("");
		}

		if (visibleColumnMap.containsKey(ColumnLabels.MGID.getName()) && visibleColumnMap.get(ColumnLabels.MGID.getName())) {

			StandardVariable mgid = columnStandardVariables.get(ColumnLabels.MGID.getTermId().getId());
			HSSFRow sourceRow = descriptionSheet.createRow(++actualRow);

			if (mgid != null) {
				sourceRow.createCell(0).setCellValue(mgid.getName());
				sourceRow.createCell(1).setCellValue(mgid.getDescription());
			} else {
				sourceRow.createCell(0).setCellValue("MGID");
				sourceRow.createCell(1).setCellValue("GID OF MALE PARENT");
			}
			sourceRow.createCell(2).setCellValue("GERMPLASM ID");
			sourceRow.createCell(3).setCellValue("DBCV");
			sourceRow.createCell(4).setCellValue("MALE SELECTED");
			sourceRow.createCell(5).setCellValue("C");
			sourceRow.createCell(6).setCellValue("");
		}

		if (visibleColumnMap.containsKey(ColumnLabels.SEED_SOURCE.getName()) && visibleColumnMap.get(ColumnLabels.SEED_SOURCE.getName())) {

			StandardVariable seedSource = columnStandardVariables.get(ColumnLabels.SEED_SOURCE.getTermId().getId());
			HSSFRow sourceRow = descriptionSheet.createRow(++actualRow);

			if (seedSource != null) {

				this.writeStandardVariableToRow(sourceRow, seedSource);

			} else {
				sourceRow.createCell(0).setCellValue("SOURCE");
				sourceRow.createCell(1).setCellValue("The seed source of the germplasm");
				sourceRow.createCell(2).setCellValue("SEED SOURCE");
				sourceRow.createCell(3).setCellValue("NAME");
				sourceRow.createCell(4).setCellValue("Seed Source");
				sourceRow.createCell(5).setCellValue("C");
				sourceRow.createCell(6).setCellValue("");
			}
		}
	}

	protected void writeStandardVariableToRow(HSSFRow hssfRow, StandardVariable standardVariable) {
		hssfRow.createCell(0).setCellValue(standardVariable.getName().toUpperCase());
		hssfRow.createCell(1).setCellValue(standardVariable.getDescription());
		hssfRow.createCell(2).setCellValue(standardVariable.getProperty().getName().toUpperCase());
		hssfRow.createCell(3).setCellValue(standardVariable.getScale().getName().toUpperCase());
		hssfRow.createCell(4).setCellValue(standardVariable.getMethod().getName().toUpperCase());
		hssfRow.createCell(5).setCellValue(standardVariable.getDataType().getName().substring(0, 1).toUpperCase());
		hssfRow.createCell(6).setCellValue("");
	}

	protected void writeListConditionSection(Map<String, CellStyle> styles, HSSFSheet descriptionSheet, int startingRow,
			GermplasmListExportInputValues input) throws GermplasmListExporterException {

		// prepare inputs
		GermplasmList germplasmList = input.getGermplasmList();
		String ownerName = input.getOwnerName();
		String exporterName = input.getExporterName();
		Integer currentLocalIbdbUserId = input.getCurrentLocalIbdbUserId();

		int actualRow = startingRow - 1;

		// write user details
		HSSFRow conditionDetailsHeading = descriptionSheet.createRow(actualRow);
		Cell conditionCell = conditionDetailsHeading.createCell(0);
		conditionCell.setCellValue(ExportServiceImpl.CONDITION);
		conditionCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE));
		Cell descriptionCell = conditionDetailsHeading.createCell(1);
		descriptionCell.setCellValue(ExportServiceImpl.DESCRIPTION);
		descriptionCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE));
		Cell propertyCell = conditionDetailsHeading.createCell(2);
		propertyCell.setCellValue(ExportServiceImpl.PROPERTY);
		propertyCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE));
		Cell scaleCell = conditionDetailsHeading.createCell(3);
		scaleCell.setCellValue(ExportServiceImpl.SCALE);
		scaleCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE));
		Cell methodCell = conditionDetailsHeading.createCell(4);
		methodCell.setCellValue(ExportServiceImpl.METHOD);
		methodCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE));
		Cell dataTypeCell = conditionDetailsHeading.createCell(5);
		dataTypeCell.setCellValue(ExportServiceImpl.DATA_TYPE);
		dataTypeCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE));
		Cell valueCell = conditionDetailsHeading.createCell(6);
		valueCell.setCellValue("VALUE");
		valueCell.setCellStyle(styles.get(ExportServiceImpl.HEADING_STYLE));

		HSSFRow listUserRow = descriptionSheet.createRow(actualRow + 1);
		listUserRow.createCell(0).setCellValue("LIST USER");
		listUserRow.createCell(1).setCellValue("PERSON WHO MADE THE LIST");
		listUserRow.createCell(2).setCellValue(ExportServiceImpl.PERSON);
		listUserRow.createCell(3).setCellValue("DBCV");
		listUserRow.createCell(4).setCellValue(ExportServiceImpl.ASSIGNED);
		listUserRow.createCell(5).setCellValue("C");
		listUserRow.createCell(6).setCellValue(ownerName.trim());

		HSSFRow listUserIdRow = descriptionSheet.createRow(actualRow + 2);
		listUserIdRow.createCell(0).setCellValue("LIST USER ID");
		listUserIdRow.createCell(1).setCellValue("ID OF LIST OWNER");
		listUserIdRow.createCell(2).setCellValue(ExportServiceImpl.PERSON);
		listUserIdRow.createCell(3).setCellValue("DBID");
		listUserIdRow.createCell(4).setCellValue(ExportServiceImpl.ASSIGNED);
		listUserIdRow.createCell(5).setCellValue("N");
		Cell userIdCell = listUserIdRow.createCell(6);
		userIdCell.setCellValue(germplasmList.getUserId());
		userIdCell.setCellStyle(styles.get(ExportServiceImpl.NUMERIC_STYLE));

		HSSFRow listExporterRow = descriptionSheet.createRow(actualRow + 3);
		listExporterRow.createCell(0).setCellValue("LIST EXPORTER");
		listExporterRow.createCell(1).setCellValue("PERSON EXPORTING THE LIST");
		listExporterRow.createCell(2).setCellValue(ExportServiceImpl.PERSON);
		listExporterRow.createCell(3).setCellValue("DBCV");
		listExporterRow.createCell(4).setCellValue(ExportServiceImpl.ASSIGNED);
		listExporterRow.createCell(5).setCellValue("C");
		listExporterRow.createCell(6).setCellValue(exporterName.trim());

		HSSFRow listExporterIdRow = descriptionSheet.createRow(actualRow + 4);
		listExporterIdRow.createCell(0).setCellValue("LIST EXPORTER ID");
		listExporterIdRow.createCell(1).setCellValue("ID OF LIST EXPORTER");
		listExporterIdRow.createCell(2).setCellValue(ExportServiceImpl.PERSON);
		listExporterIdRow.createCell(3).setCellValue("DBID");
		listExporterIdRow.createCell(4).setCellValue(ExportServiceImpl.ASSIGNED);
		listExporterIdRow.createCell(5).setCellValue("N");
		Cell localIdCell = listExporterIdRow.createCell(6);
		localIdCell.setCellValue(currentLocalIbdbUserId);
		localIdCell.setCellStyle(styles.get(ExportServiceImpl.NUMERIC_STYLE));
	}

	public void writeListDetailsSection(Map<String, CellStyle> styles, Sheet descriptionSheet, int startingRow, GermplasmList germplasmList) {
		int actualRow = startingRow - 1;

		Row nameRow = descriptionSheet.createRow(actualRow);
		descriptionSheet.addMergedRegion(new CellRangeAddress(actualRow, actualRow, 1, 7));
		Cell nameLabel = nameRow.createCell(0);
		nameLabel.setCellValue(ExportServiceImpl.LIST_NAME);
		nameLabel.setCellStyle(styles.get(ExportServiceImpl.LABEL_STYLE));

		Cell nameVal = nameRow.createCell(1);
		nameVal.setCellValue(germplasmList.getName());
		nameVal.setCellStyle(styles.get(ExportServiceImpl.TEXT_STYLE));

		Row titleRow = descriptionSheet.createRow(actualRow + 1);
		descriptionSheet.addMergedRegion(new CellRangeAddress(actualRow + 1, actualRow + 1, 1, 7));
		Cell titleLabel = titleRow.createCell(0);
		titleLabel.setCellValue(ExportServiceImpl.LIST_DESCRIPTION);
		titleLabel.setCellStyle(styles.get(ExportServiceImpl.LABEL_STYLE));

		Cell titleVal = titleRow.createCell(1);
		titleVal.setCellValue(germplasmList.getDescription());
		titleVal.setCellStyle(styles.get(ExportServiceImpl.TEXT_STYLE));

		Row typeRow = descriptionSheet.createRow(actualRow + 2);
		descriptionSheet.addMergedRegion(new CellRangeAddress(actualRow + 2, actualRow + 2, 1, 7));
		Cell typeLabel = typeRow.createCell(0);
		typeLabel.setCellValue(ExportServiceImpl.LIST_TYPE);
		typeLabel.setCellStyle(styles.get(ExportServiceImpl.LABEL_STYLE));

		Cell typeVal = typeRow.createCell(1);
		typeVal.setCellValue(germplasmList.getType());
		typeVal.setCellStyle(styles.get(ExportServiceImpl.TEXT_STYLE));

		Row dateRow = descriptionSheet.createRow(actualRow + 3);
		descriptionSheet.addMergedRegion(new CellRangeAddress(actualRow + 3, actualRow + 3, 1, 7));
		Cell dateLabel = dateRow.createCell(0);
		dateLabel.setCellValue(ExportServiceImpl.LIST_DATE);
		dateLabel.setCellStyle(styles.get(ExportServiceImpl.LABEL_STYLE));

		Cell dateCell = dateRow.createCell(1);
		dateCell.setCellValue(germplasmList.getDate());
		dateCell.setCellStyle(styles.get(ExportServiceImpl.NUMERIC_STYLE));
	}

	@Override
	public Map<String, CellStyle> createStyles(Workbook wb) {
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();

		// set cell style for labels in the description sheet
		CellStyle labelStyle = wb.createCellStyle();
		labelStyle.setFillForegroundColor(IndexedColors.BROWN.getIndex());
		labelStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		Font labelFont = wb.createFont();
		labelFont.setColor(IndexedColors.WHITE.getIndex());
		labelFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		labelStyle.setFont(labelFont);
		styles.put(ExportServiceImpl.LABEL_STYLE, labelStyle);

		// set cell style for headings in the description sheet
		CellStyle headingStyle = wb.createCellStyle();
		headingStyle.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
		headingStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		Font headingFont = wb.createFont();
		headingFont.setColor(IndexedColors.WHITE.getIndex());
		headingFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headingStyle.setFont(headingFont);
		styles.put(ExportServiceImpl.HEADING_STYLE, headingStyle);

		// set cell style for numeric values (left alignment)
		CellStyle numericStyle = wb.createCellStyle();
		numericStyle.setAlignment(CellStyle.ALIGN_LEFT);
		styles.put(ExportServiceImpl.NUMERIC_STYLE, numericStyle);

		CellStyle textStyle = wb.createCellStyle();
		textStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		textStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		styles.put(ExportServiceImpl.TEXT_STYLE, textStyle);

		return styles;
	}

}
