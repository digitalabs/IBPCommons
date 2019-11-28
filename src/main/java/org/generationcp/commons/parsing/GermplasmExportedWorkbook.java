
package org.generationcp.commons.parsing;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.generationcp.commons.pojo.GermplasmListExportInputValues;
import org.generationcp.commons.pojo.GermplasmParents;
import org.generationcp.commons.workbook.generator.CodesSheetGenerator;
import org.generationcp.commons.workbook.generator.GermplasmAttributesWorkbookExporter;
import org.generationcp.commons.workbook.generator.GermplasmNamesWorkbookExporter;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.interfaces.GermplasmExportSource;
import org.generationcp.middleware.pojos.GermplasmList;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Germplasm workbook which gets exported as a file. This file uses the ExcelWorkbookRow and the ExcelCellStyleBuilder to construct a
 * workbook instance to export.
 *
 */
public class GermplasmExportedWorkbook {

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
	public static final String VALUE = "VALUE";
	public static final String COMMENTS = "COMMENTS";
	public static final String INVENTORY = "INVENTORY";
	public static final String VARIATE = "VARIATE";

	// Values
	public static final String ASSIGNED = "ASSIGNED";
	public static final String PERSON = "PERSON";

	// Factor
	public static final String FACTOR = "FACTOR";

	// Codes
	public static final String USER = "USER";
	public static final String BREEDING_METHOD = "BREEDING METHOD";

	private ExcelCellStyleBuilder sheetStyles;
	private CellStyle textStyle;
	private CellStyle headingStyle;

	private HSSFWorkbook wb;
	private GermplasmListExportInputValues input;

	// Sheet Generators
	@Resource
	private CodesSheetGenerator codesSheetGenerator;

	@Resource
	private GermplasmAttributesWorkbookExporter attributesGenerator;

	@Resource
	private GermplasmNamesWorkbookExporter namesGenerator;
	
	/**
	 * Default constructor
	 */
	public GermplasmExportedWorkbook() {
	}

	public void init(final GermplasmListExportInputValues input) {
		this.wb = new HSSFWorkbook();
		this.input = input;

		// set styles
		this.sheetStyles = new ExcelCellStyleBuilder(this.wb);
		this.textStyle = this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.TEXT_STYLE);
		this.headingStyle = this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.HEADING_STYLE);

		// create three worksheets - Description, Observations, and Codes
		this.generateDescriptionSheet();
		this.generateObservationSheet();

		// Generate Sheets
		this.codesSheetGenerator.generateCodesSheet(this.wb);

	}

	public void write(final FileOutputStream fos) throws IOException {
		this.wb.write(fos);
	}

	public HSSFWorkbook getWorkbook() {
		return this.wb;
	}

	void generateDescriptionSheet() {

		final Font defaultFont = this.wb.getFontAt((short) 0);
		defaultFont.setFontHeightInPoints((short) 9);
		final HSSFSheet descriptionSheet = this.wb.createSheet("Description");
		descriptionSheet.setDefaultRowHeightInPoints(18);
		descriptionSheet.setZoom(10, 8);

		int nextRow = 1;

		nextRow = this.writeListDetailsSection(descriptionSheet, nextRow, this.input.getGermplasmList());

		nextRow = this.writeListConditionSection(descriptionSheet, nextRow + 2);

		nextRow = this.writeListFactorSection(descriptionSheet, nextRow + 2);

		nextRow = this.writeListInventorySection(descriptionSheet, nextRow + 2);

		this.writeListVariateSection(descriptionSheet, nextRow + 2);

		this.fillSheetWithCellStyle(descriptionSheet);

		this.setDescriptionColumnsWidth(descriptionSheet);

	}

	void generateObservationSheet() {

		final HSSFSheet observationSheet = this.wb.createSheet("Observation");
		this.writeObservationSheet(observationSheet);

		// adjust column widths of observation sheet to fit contents
		final int noOfVisibleColumns = this.getNoOfVisibleColumns(this.input.getVisibleColumnMap());
		for (int ctr = 0; ctr < noOfVisibleColumns; ctr++) {
			observationSheet.autoSizeColumn(ctr);
		}
	}

	private int writeListDetailsSection(final Sheet descriptionSheet, final int startingRow, final GermplasmList germplasmList) {
		final CellStyle labelStyle = this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.LABEL_STYLE);

		int actualRow = startingRow - 1;
		new ExcelWorkbookRow((HSSFRow) descriptionSheet.createRow(actualRow)).writeListDetailsRow(descriptionSheet,
				GermplasmExportedWorkbook.LIST_NAME, germplasmList.getName(), "Enter a list name here, or add it when saving in the BMS",
				labelStyle, this.textStyle);

		new ExcelWorkbookRow((HSSFRow) descriptionSheet.createRow(++actualRow)).writeListDetailsRow(descriptionSheet,
				GermplasmExportedWorkbook.LIST_DESCRIPTION, germplasmList.getDescription(),
				"Enter a list description here, or add it when saving in the BMS", labelStyle, this.textStyle);

		new ExcelWorkbookRow((HSSFRow) descriptionSheet.createRow(++actualRow)).writeListDetailsRow(descriptionSheet,
				GermplasmExportedWorkbook.LIST_TYPE, germplasmList.getType(), "See valid list types on Codes sheet for more options",
				labelStyle, this.textStyle);

		new ExcelWorkbookRow((HSSFRow) descriptionSheet.createRow(++actualRow)).writeListDetailsRow(descriptionSheet,
				GermplasmExportedWorkbook.LIST_DATE, String.valueOf(germplasmList.getDate()),
				"Accepted formats: YYYYMMDD or YYYYMM or YYYY or blank", labelStyle, this.textStyle);

		return ++actualRow;
	}

	private int writeListConditionSection(final HSSFSheet descriptionSheet, final int startingRow) {
		final CellStyle labelStyleCondition = this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.LABEL_STYLE_CONDITION);
		final CellStyle numberStyle = this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.NUMERIC_STYLE);

		// prepare inputs
		final GermplasmList germplasmList = this.input.getGermplasmList();
		final String ownerName = this.input.getOwnerName();
		final String exporterName = this.input.getExporterName();
		final Integer currentLocalIbdbUserId = this.input.getCurrentLocalIbdbUserId();

		int actualRow = startingRow - 1;

		// write user details
		final ExcelWorkbookRow conditionDetailsHeading = new ExcelWorkbookRow(descriptionSheet.createRow(actualRow));
		conditionDetailsHeading.createCell(0, this.headingStyle, GermplasmExportedWorkbook.CONDITION);
		conditionDetailsHeading.createCell(1, this.headingStyle, GermplasmExportedWorkbook.DESCRIPTION);
		conditionDetailsHeading.createCell(2, this.headingStyle, GermplasmExportedWorkbook.PROPERTY);
		conditionDetailsHeading.createCell(3, this.headingStyle, GermplasmExportedWorkbook.SCALE);
		conditionDetailsHeading.createCell(5, this.headingStyle, GermplasmExportedWorkbook.DATA_TYPE);
		conditionDetailsHeading.createCell(6, this.headingStyle, GermplasmExportedWorkbook.VALUE);
		conditionDetailsHeading.createCell(4, this.headingStyle, GermplasmExportedWorkbook.METHOD);
		conditionDetailsHeading.createCell(7, this.headingStyle, GermplasmExportedWorkbook.COMMENTS);

		final ExcelWorkbookRow listUserRow = new ExcelWorkbookRow(descriptionSheet.createRow(++actualRow));
		listUserRow.createCell(0, labelStyleCondition, "LIST USER");
		listUserRow.createCell(1, this.textStyle, "PERSON WHO MADE THE LIST");
		listUserRow.createCell(2, this.textStyle, GermplasmExportedWorkbook.PERSON);
		listUserRow.createCell(3, this.textStyle, "DBCV");
		listUserRow.createCell(4, this.textStyle, GermplasmExportedWorkbook.ASSIGNED);
		listUserRow.createCell(5, this.textStyle, "C");
		listUserRow.createCell(6, this.textStyle, ownerName.trim());
		listUserRow.createCell(7, this.textStyle, "See valid user names and IDs on Codes sheet (or leave blank)");

		final ExcelWorkbookRow listUserIdRow = new ExcelWorkbookRow(descriptionSheet.createRow(++actualRow));
		listUserIdRow.createCell(0, labelStyleCondition, "LIST USER ID");
		listUserIdRow.createCell(1, this.textStyle, "ID OF LIST OWNER");
		listUserIdRow.createCell(2, this.textStyle, GermplasmExportedWorkbook.PERSON);
		listUserIdRow.createCell(3, this.textStyle, "DBID");
		listUserIdRow.createCell(4, this.textStyle, GermplasmExportedWorkbook.ASSIGNED);
		listUserIdRow.createCell(5, this.textStyle, "N");
		listUserIdRow.createCell(6, numberStyle, germplasmList.getUserId());
		listUserIdRow.createCell(7, this.textStyle, "");

		final ExcelWorkbookRow listExporterRow = new ExcelWorkbookRow(descriptionSheet.createRow(++actualRow));
		listExporterRow.createCell(0, labelStyleCondition, "LIST EXPORTER");
		listExporterRow.createCell(1, this.textStyle, "PERSON EXPORTING THE LIST");
		listExporterRow.createCell(2, this.textStyle, GermplasmExportedWorkbook.PERSON);
		listExporterRow.createCell(3, this.textStyle, "DBCV");
		listExporterRow.createCell(4, this.textStyle, GermplasmExportedWorkbook.ASSIGNED);
		listExporterRow.createCell(5, this.textStyle, "C");
		listExporterRow.createCell(6, this.textStyle, exporterName.trim());
		listExporterRow.createCell(7, this.textStyle, "");

		final ExcelWorkbookRow listExporterIdRow = new ExcelWorkbookRow(descriptionSheet.createRow(++actualRow));
		listExporterIdRow.createCell(0, labelStyleCondition, "LIST EXPORTER ID");
		listExporterIdRow.createCell(1, this.textStyle, "ID OF LIST EXPORTER");
		listExporterIdRow.createCell(2, this.textStyle, GermplasmExportedWorkbook.PERSON);
		listExporterIdRow.createCell(3, this.textStyle, "DBID");
		listExporterIdRow.createCell(4, this.textStyle, GermplasmExportedWorkbook.ASSIGNED);
		listExporterIdRow.createCell(5, this.textStyle, "N");
		listExporterIdRow.createCell(6, numberStyle, currentLocalIbdbUserId);
		listExporterIdRow.createCell(7, this.textStyle, "");

		descriptionSheet.addMergedRegion(new CellRangeAddress(actualRow - 3, actualRow, 7, 7));

		return ++actualRow;
	}

	private void writeObservationSheet(final HSSFSheet observationSheet) {

		final Map<String, Boolean> visibleColumnMap = this.input.getVisibleColumnMap();
		final Map<Integer, Variable> inventoryStandardVariableMap = this.input.getInventoryVariableMap();
		final List<? extends GermplasmExportSource> listData = this.input.getListData();
		final Map<Integer, GermplasmParents> germplasmParentsMap = this.input.getGermplasmParents();

		this.createListEntriesHeaderRow(observationSheet);

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
					&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.FGID))
                    && germplasmParentsMap!=null && germplasmParentsMap.containsKey(data.getGermplasmId())) {
				listEntry.createCell(j).setCellValue(germplasmParentsMap.get(data.getGermplasmId()).getFgid());
				j++;
			}

			if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.MGID))
					&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.MGID))
                    && germplasmParentsMap!=null && germplasmParentsMap.containsKey(data.getGermplasmId())) {
				listEntry.createCell(j).setCellValue(germplasmParentsMap.get(data.getGermplasmId()).getMgid());
				j++;
			}

			if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.SEED_SOURCE))
					&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.SEED_SOURCE))) {
				listEntry.createCell(j).setCellValue(data.getSeedSource());
				j++;
			}

			if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.GROUPGID))
					&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.GROUPGID))) {
				listEntry.createCell(j).setCellValue(data.getGroupId());
				j++;
			}

			if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.ENTRY_TYPE))
					&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.ENTRY_TYPE))) {
				listEntry.createCell(j).setCellValue(data.getCheckTypeDescription());
				j++;
			}
			
			j = this.namesGenerator.generateAddedColumnValue(listEntry, data, j);

			if (inventoryStandardVariableMap.containsKey(TermId.STOCKID.getId())) {
				listEntry.createCell(j).setCellValue(data.getStockIDs());
				j++;
			}

			if (inventoryStandardVariableMap.containsKey(TermId.SEED_AMOUNT_G.getId())) {
				listEntry.createCell(j).setCellValue(data.getSeedAmount());
				j++;
			}

			this.attributesGenerator.generateAddedColumnValue(listEntry, data, j);
			
			i += 1;
		}

	}

	private int writeListFactorSection(final HSSFSheet descriptionSheet, final int startingRow) {
		final CellStyle labelStyleFactor = this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.LABEL_STYLE_FACTOR);

		final Map<String, Boolean> visibleColumnMap = this.input.getVisibleColumnMap();
		final Map<Integer, Term> columnTermMap = this.input.getColumnTermMap();

		int actualRow = startingRow - 1;

		final ExcelWorkbookRow factorDetailsHeader = new ExcelWorkbookRow(descriptionSheet.createRow(actualRow));
		factorDetailsHeader.createCell(0, this.headingStyle, GermplasmExportedWorkbook.FACTOR);
		factorDetailsHeader.createCell(1, this.headingStyle, GermplasmExportedWorkbook.DESCRIPTION);
		factorDetailsHeader.createCell(2, this.headingStyle, GermplasmExportedWorkbook.PROPERTY);
		factorDetailsHeader.createCell(3, this.headingStyle, GermplasmExportedWorkbook.SCALE);
		factorDetailsHeader.createCell(4, this.headingStyle, GermplasmExportedWorkbook.METHOD);
		factorDetailsHeader.createCell(5, this.headingStyle, GermplasmExportedWorkbook.DATA_TYPE);
		factorDetailsHeader.createCell(6, this.headingStyle, "");
		factorDetailsHeader.createCell(7, this.headingStyle, GermplasmExportedWorkbook.COMMENTS);

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.ENTRY_ID))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.ENTRY_ID))) {

			final Term termEntry = columnTermMap.get(ColumnLabels.ENTRY_ID.getTermId().getId());
			final ExcelWorkbookRow entryIdRow = new ExcelWorkbookRow(descriptionSheet.createRow(++actualRow));

			if (termEntry != null && Objects.equals(termEntry.getVocabularyId(), CvId.VARIABLES.getId())) {
				final Variable variable = (Variable) termEntry;
				entryIdRow.writeStandardVariableToRow(labelStyleFactor,
						this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.TEXT_HIGHLIGHT_STYLE_FACTOR), variable);
				entryIdRow.createCell(7, this.textStyle, "Sequence number - mandatory");

			}

		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.GID))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.GID))) {

			final Variable gid = (Variable) columnTermMap.get(ColumnLabels.GID.getTermId().getId());
			final ExcelWorkbookRow gidRow = new ExcelWorkbookRow(descriptionSheet.createRow(++actualRow));

			if (gid != null) {
				gidRow.writeStandardVariableToRow(labelStyleFactor, this.textStyle, gid);
				gidRow.createCell(7, this.textStyle, "GID value if known (or leave blank)");
			}
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.ENTRY_CODE))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.ENTRY_CODE))) {

			final Variable entryCode = (Variable) columnTermMap.get(ColumnLabels.ENTRY_CODE.getTermId().getId());
			final ExcelWorkbookRow entryCodeRow = new ExcelWorkbookRow(descriptionSheet.createRow(++actualRow));

			if (entryCode != null) {
				entryCodeRow.writeStandardVariableToRow(labelStyleFactor, this.textStyle, entryCode);
				entryCodeRow.createCell(7, this.textStyle, "Text giving a local entry code - optional");
			}
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.DESIGNATION))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.DESIGNATION))) {

			final Variable designation = (Variable) columnTermMap.get(ColumnLabels.DESIGNATION.getTermId().getId());
			final ExcelWorkbookRow designationRow = new ExcelWorkbookRow(descriptionSheet.createRow(++actualRow));

			if (designation != null) {
				designationRow.writeStandardVariableToRow(labelStyleFactor,
						this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.TEXT_HIGHLIGHT_STYLE_FACTOR), designation);
				designationRow.createCell(7, this.textStyle, "Germplasm name - mandatory");
			}
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.PARENTAGE))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.PARENTAGE))) {

			final Variable parentage = (Variable) columnTermMap.get(ColumnLabels.PARENTAGE.getTermId().getId());
			final ExcelWorkbookRow crossRow = new ExcelWorkbookRow(descriptionSheet.createRow(++actualRow));

			if (parentage != null) {
				crossRow.writeStandardVariableToRow(labelStyleFactor, this.textStyle, parentage);
				crossRow.createCell(7, this.textStyle, "Cross string showing parentage - optional");
			}
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.FEMALE_PARENT))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.FEMALE_PARENT))) {

			final Term femaleParent = columnTermMap.get(ColumnLabels.FEMALE_PARENT.getTermId().getId());
			final ExcelWorkbookRow sourceRow = new ExcelWorkbookRow(descriptionSheet.createRow(++actualRow));

			if (femaleParent != null) {
				sourceRow.createCell(0, labelStyleFactor, femaleParent.getName());
				sourceRow.createCell(1, this.textStyle, femaleParent.getDefinition());
			} else {
				sourceRow.createCell(0, labelStyleFactor, "FEMALE PARENT");
				sourceRow.createCell(1, this.textStyle, "NAME OF FEMALE PARENT");
			}

			sourceRow.createCell(2, this.textStyle, "GERMPLASM ID");
			sourceRow.createCell(3, this.textStyle, "DBCV");
			sourceRow.createCell(4, this.textStyle, "FEMALE SELECTED");
			sourceRow.createCell(5, this.textStyle, "C");
			sourceRow.createCell(6, this.textStyle, "");
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.MALE_PARENT))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.MALE_PARENT))) {

			final Term maleParent = columnTermMap.get(ColumnLabels.MALE_PARENT.getTermId().getId());
			final ExcelWorkbookRow sourceRow = new ExcelWorkbookRow(descriptionSheet.createRow(++actualRow));

			if (maleParent != null) {
				sourceRow.createCell(0, labelStyleFactor, maleParent.getName());
				sourceRow.createCell(1, this.textStyle, maleParent.getDefinition());
			} else {
				sourceRow.createCell(0, labelStyleFactor, "MALE PARENT");
				sourceRow.createCell(1, this.textStyle, "NAME OF MALE PARENT");
			}
			sourceRow.createCell(2, this.textStyle, "GERMPLASM ID");
			sourceRow.createCell(3, this.textStyle, "DBCV");
			sourceRow.createCell(4, this.textStyle, "MALE SELECTED");
			sourceRow.createCell(5, this.textStyle, "C");
			sourceRow.createCell(6, this.textStyle, "");
		}

		if (visibleColumnMap.containsKey(ColumnLabels.FGID.getName()) && visibleColumnMap.get(ColumnLabels.FGID.getName())) {

			final Term fgid = columnTermMap.get(ColumnLabels.FGID.getTermId().getId());
			final ExcelWorkbookRow sourceRow = new ExcelWorkbookRow(descriptionSheet.createRow(++actualRow));

			if (fgid != null) {
				sourceRow.createCell(0, labelStyleFactor, fgid.getName());
				sourceRow.createCell(1, this.textStyle, fgid.getDefinition());
			} else {
				sourceRow.createCell(0, labelStyleFactor, "FGID");
				sourceRow.createCell(1, this.textStyle, "GID OF FEMALE PARENT");
			}
			sourceRow.createCell(2, this.textStyle, "GERMPLASM ID");
			sourceRow.createCell(3, this.textStyle, "DBCV");
			sourceRow.createCell(4, this.textStyle, "FEMALE SELECTED");
			sourceRow.createCell(5, this.textStyle, "C");
			sourceRow.createCell(6, this.textStyle, "");
		}

		if (visibleColumnMap.containsKey(ColumnLabels.MGID.getName()) && visibleColumnMap.get(ColumnLabels.MGID.getName())) {

			final Term mgid = columnTermMap.get(ColumnLabels.MGID.getTermId().getId());
			final ExcelWorkbookRow sourceRow = new ExcelWorkbookRow(descriptionSheet.createRow(++actualRow));

			if (mgid != null) {
				sourceRow.createCell(0, labelStyleFactor, mgid.getName());
				sourceRow.createCell(1, this.textStyle, mgid.getDefinition());
			} else {
				sourceRow.createCell(0, labelStyleFactor, "MGID");
				sourceRow.createCell(1, this.textStyle, "GID OF MALE PARENT");
			}
			sourceRow.createCell(2, this.textStyle, "GERMPLASM ID");
			sourceRow.createCell(3, this.textStyle, "DBCV");
			sourceRow.createCell(4, this.textStyle, "MALE SELECTED");
			sourceRow.createCell(5, this.textStyle, "C");
			sourceRow.createCell(6, this.textStyle, "");
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.SEED_SOURCE))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.SEED_SOURCE))) {

			final Variable seedSource = (Variable) columnTermMap.get(ColumnLabels.SEED_SOURCE.getTermId().getId());
			final ExcelWorkbookRow sourceRow = new ExcelWorkbookRow(descriptionSheet.createRow(++actualRow));

			if (seedSource != null) {
				sourceRow.writeStandardVariableToRow(labelStyleFactor, this.textStyle, seedSource);
				sourceRow.createCell(7, this.textStyle, "Text giving seed source - optional");
			}
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.ENTRY_TYPE))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.ENTRY_TYPE))) {

			final Variable entryType = (Variable) columnTermMap.get(ColumnLabels.ENTRY_TYPE.getTermId().getId());
			final ExcelWorkbookRow sourceRow = new ExcelWorkbookRow(descriptionSheet.createRow(++actualRow));

			if (entryType != null) {
				sourceRow.writeStandardVariableToRow(labelStyleFactor, this.textStyle, entryType);
				sourceRow.createCell(7, this.textStyle, "");
			}
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.GROUPGID))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.GROUPGID))) {

			final Variable groupId = (Variable) columnTermMap.get(ColumnLabels.GROUPGID.getTermId().getId());
			final ExcelWorkbookRow groupIdRow = new ExcelWorkbookRow(descriptionSheet.createRow(++actualRow));

			if (groupId != null) {
				groupIdRow.writeStandardVariableToRow(labelStyleFactor, this.textStyle, groupId);
				groupIdRow.createCell(8, this.textStyle, "Group ID of a germplasm");
			}
		}

		return this.namesGenerator.addRowsToDescriptionSheet(descriptionSheet, actualRow, this.sheetStyles, this.input.getCurrentColumnsInfo());
	}

	private int writeListInventorySection(final HSSFSheet descriptionSheet, final int startingRow) {
		final CellStyle labelStyleInventory = this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.LABEL_STYLE_INVENTORY);

		int actualRow = startingRow;

		if (!this.input.getInventoryVariableMap().isEmpty()) {

			final ExcelWorkbookRow conditionDetailsHeading = new ExcelWorkbookRow(descriptionSheet.createRow(actualRow));
			conditionDetailsHeading.createCell(0, this.headingStyle, GermplasmExportedWorkbook.INVENTORY);
			conditionDetailsHeading.createCell(1, this.headingStyle, GermplasmExportedWorkbook.DESCRIPTION);
			conditionDetailsHeading.createCell(2, this.headingStyle, GermplasmExportedWorkbook.PROPERTY);
			conditionDetailsHeading.createCell(3, this.headingStyle, GermplasmExportedWorkbook.SCALE);
			conditionDetailsHeading.createCell(4, this.headingStyle, GermplasmExportedWorkbook.METHOD);
			conditionDetailsHeading.createCell(5, this.headingStyle, GermplasmExportedWorkbook.DATA_TYPE);
			conditionDetailsHeading.createCell(6, this.headingStyle, "");
			conditionDetailsHeading.createCell(7, this.headingStyle, GermplasmExportedWorkbook.COMMENTS);

			for (final Variable stdVar : this.input.getInventoryVariableMap().values()) {
				final ExcelWorkbookRow row = new ExcelWorkbookRow(descriptionSheet.createRow(++actualRow));
				row.writeStandardVariableToRow(labelStyleInventory, this.textStyle, stdVar);

				if (stdVar.getId() == TermId.STOCKID.getId()) {
					row.createCell(7, this.textStyle, "Existing StockID value if known (or leave blank)");
				} else if (stdVar.getId() == TermId.SEED_AMOUNT_G.getId()) {
					row.createCell(7, this.textStyle, "Weight of seed lot in grams - optional; see Codes sheet for more options");
				} else {
					row.createCell(7, this.textStyle, "");
				}

			}

		}

		return actualRow;
	}

	private void writeListVariateSection(final HSSFSheet descriptionSheet, final int startingRow) {
		final int actualRow = startingRow;
		this.attributesGenerator.setColumnsInfo(this.input.getCurrentColumnsInfo());
		if (this.attributesGenerator.hasItems()) {
			final ExcelWorkbookRow conditionDetailsHeading = new ExcelWorkbookRow(descriptionSheet.createRow(actualRow));
			conditionDetailsHeading.createCell(0, this.headingStyle, GermplasmExportedWorkbook.VARIATE);
			conditionDetailsHeading.createCell(1, this.headingStyle, GermplasmExportedWorkbook.DESCRIPTION);
			conditionDetailsHeading.createCell(2, this.headingStyle, GermplasmExportedWorkbook.PROPERTY);
			conditionDetailsHeading.createCell(3, this.headingStyle, GermplasmExportedWorkbook.SCALE);
			conditionDetailsHeading.createCell(4, this.headingStyle, GermplasmExportedWorkbook.METHOD);
			conditionDetailsHeading.createCell(5, this.headingStyle, GermplasmExportedWorkbook.DATA_TYPE);
			conditionDetailsHeading.createCell(6, this.headingStyle, "");
			conditionDetailsHeading.createCell(7, this.headingStyle, GermplasmExportedWorkbook.COMMENTS);
			this.attributesGenerator.addRowsToDescriptionSheet(descriptionSheet, actualRow, this.sheetStyles, this.input.getCurrentColumnsInfo());
		}
	}

	private void createListEntriesHeaderRow(final HSSFSheet observationSheet) {

		final Map<String, Boolean> visibleColumnMap = this.input.getVisibleColumnMap();
		final Map<Integer, Term> columnTermMap = this.input.getColumnTermMap();
		final Map<Integer, Variable> inventoryStandardVariableMap = this.input.getInventoryVariableMap();
		final HSSFRow listEntriesHeader = observationSheet.createRow(0);
		listEntriesHeader.setHeightInPoints(18);

		int columnIndex = 0;
		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.ENTRY_ID))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.ENTRY_ID))) {
			final Cell entryIdCell = listEntriesHeader.createCell(columnIndex);
			entryIdCell.setCellValue(this.getTermNameOrDefaultLabel(ColumnLabels.ENTRY_ID, columnTermMap));
			entryIdCell.setCellStyle(this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.HEADING_STYLE_FACTOR));
			observationSheet.setDefaultColumnStyle(columnIndex,
					this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.NUMBER_COLUMN_HIGHLIGHT_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.GID))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.GID))) {
			final Cell gidCell = listEntriesHeader.createCell(columnIndex);
			gidCell.setCellValue(this.getTermNameOrDefaultLabel(ColumnLabels.GID, columnTermMap));
			gidCell.setCellStyle(this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.HEADING_STYLE_FACTOR));
			observationSheet.setDefaultColumnStyle(columnIndex,
					this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.NUMBER_DATA_FORMAT_STYLE));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.ENTRY_CODE))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.ENTRY_CODE))) {
			final Cell entryCodeCell = listEntriesHeader.createCell(columnIndex);
			entryCodeCell.setCellValue(this.getTermNameOrDefaultLabel(ColumnLabels.ENTRY_CODE, columnTermMap));
			entryCodeCell.setCellStyle(this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.DESIGNATION))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.DESIGNATION))) {
			final Cell designationCell = listEntriesHeader.createCell(columnIndex);
			designationCell.setCellValue(this.getTermNameOrDefaultLabel(ColumnLabels.DESIGNATION, columnTermMap));
			designationCell.setCellStyle(this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.HEADING_STYLE_FACTOR));
			observationSheet.setDefaultColumnStyle(columnIndex,
					this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.COLUMN_HIGHLIGHT_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.PARENTAGE))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.PARENTAGE))) {
			final Cell crossCell = listEntriesHeader.createCell(columnIndex);
			crossCell.setCellValue(this.getTermNameOrDefaultLabel(ColumnLabels.PARENTAGE, columnTermMap));
			crossCell.setCellStyle(this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.FEMALE_PARENT))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.FEMALE_PARENT))) {
			final Cell crossCell = listEntriesHeader.createCell(columnIndex);
			crossCell.setCellValue(this.getTermNameOrDefaultLabel(ColumnLabels.FEMALE_PARENT, columnTermMap));
			crossCell.setCellStyle(this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.MALE_PARENT))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.MALE_PARENT))) {
			final Cell crossCell = listEntriesHeader.createCell(columnIndex);
			crossCell.setCellValue(this.getTermNameOrDefaultLabel(ColumnLabels.MALE_PARENT, columnTermMap));
			crossCell.setCellStyle(this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.FGID))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.FGID))) {
			final Cell crossCell = listEntriesHeader.createCell(columnIndex);
			crossCell.setCellValue(this.getTermNameOrDefaultLabel(ColumnLabels.FGID, columnTermMap));
			crossCell.setCellStyle(this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.MGID))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.MGID))) {
			final Cell crossCell = listEntriesHeader.createCell(columnIndex);
			crossCell.setCellValue(this.getTermNameOrDefaultLabel(ColumnLabels.MGID, columnTermMap));
			crossCell.setCellStyle(this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.SEED_SOURCE))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.SEED_SOURCE))) {
			final Cell sourceCell = listEntriesHeader.createCell(columnIndex);
			sourceCell.setCellValue(this.getTermNameOrDefaultLabel(ColumnLabels.SEED_SOURCE, columnTermMap));
			sourceCell.setCellStyle(this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.GROUPGID))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.GROUPGID))) {
			final Cell entryTypeCell = listEntriesHeader.createCell(columnIndex);
			entryTypeCell.setCellValue(this.getTermNameOrDefaultLabel(ColumnLabels.GROUPGID, columnTermMap));
			entryTypeCell.setCellStyle(this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		if (visibleColumnMap.containsKey(this.getColumnNamesTermId(ColumnLabels.ENTRY_TYPE))
				&& visibleColumnMap.get(this.getColumnNamesTermId(ColumnLabels.ENTRY_TYPE))) {
			final Cell entryTypeCell = listEntriesHeader.createCell(columnIndex);
			entryTypeCell.setCellValue(this.getTermNameOrDefaultLabel(ColumnLabels.ENTRY_TYPE, columnTermMap));
			entryTypeCell.setCellStyle(this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.HEADING_STYLE_FACTOR));
			columnIndex++;
		}

		columnIndex = this.namesGenerator.generateAddedColumnHeader(listEntriesHeader, columnIndex);

		if (inventoryStandardVariableMap.containsKey(TermId.STOCKID.getId())) {
			final Cell stockIDCell = listEntriesHeader.createCell(columnIndex);
			stockIDCell.setCellValue(this.input.getInventoryVariableMap().get(TermId.STOCKID.getId()).getName().toUpperCase());
			stockIDCell.setCellStyle(this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.HEADING_STYLE_INVENTORY));
			observationSheet.setDefaultColumnStyle(columnIndex,
					this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.NUMBER_DATA_FORMAT_STYLE));
			columnIndex++;
		}

		if (inventoryStandardVariableMap.containsKey(TermId.SEED_AMOUNT_G.getId())) {
			final Cell seedAmountCell = listEntriesHeader.createCell(columnIndex);
			seedAmountCell.setCellValue(this.input.getInventoryVariableMap().get(TermId.SEED_AMOUNT_G.getId()).getName().toUpperCase());
			seedAmountCell.setCellStyle(this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.HEADING_STYLE_INVENTORY));
			observationSheet.setDefaultColumnStyle(columnIndex,
					this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.DECIMAL_NUMBER_DATA_FORMAT_STYLE));
			columnIndex++;
		}

		this.attributesGenerator.generateAddedColumnHeader(listEntriesHeader, columnIndex);
	}

	private void fillSheetWithCellStyle(final HSSFSheet sheet) {
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
			sheet.setDefaultColumnStyle(i, this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.SHEET_STYLE));
		}

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

	private String getTermNameOrDefaultLabel(final ColumnLabels columnLabel, final Map<Integer, Term> columnTermMap) {

		final Term term = columnTermMap.get(columnLabel.getTermId().getId());

		if (term != null && !term.getName().isEmpty()) {
			return term.getName().toUpperCase();
		} else {
			return columnLabel.getName().toUpperCase();
		}

	}

	int getNoOfVisibleColumns(final Map<String, Boolean> visibleColumnMap) {
		int count = 0;
		for (final Map.Entry<String, Boolean> column : visibleColumnMap.entrySet()) {
			final Boolean isVisible = column.getValue();
			if (isVisible) {
				count++;
			}
		}
		return count;
	}

	private String getColumnNamesTermId(final ColumnLabels columnLabel) {
		if (columnLabel.getTermId() != null) {
			return String.valueOf(columnLabel.getTermId().getId());
		}
		return "";
	}
}
