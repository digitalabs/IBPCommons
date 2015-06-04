
package org.generationcp.commons.service.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.generationcp.commons.constant.ColumnLabels;
import org.generationcp.commons.exceptions.GermplasmListExporterException;
import org.generationcp.commons.pojo.ExportColumnHeader;
import org.generationcp.commons.pojo.ExportColumnValue;
import org.generationcp.commons.pojo.GermplasmListExportInputValues;
import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import au.com.bytecode.opencsv.CSVReader;

public class ExportServiceImplTest {

	private static final String CURRENT_USER_NAME = "User User";
	private static final int CURRENT_USER_ID = 1;
	private static final Integer USER_ID = 1;
	private static final int NO_OF_LIST_ENTRIES = 10;

	private ExportServiceImpl exportService;
	private List<ExportColumnHeader> columnsHeaders;
	private List<Map<Integer, ExportColumnValue>> columnValues;
	private String testFileName;
	private String sheetName;
	private GermplasmListExportInputValues input;
	private GermplasmList germplasmList;

	@Before
	public void setUp() {
		this.exportService = new ExportServiceImpl();
		this.columnsHeaders = this.generateSampleExportColumnHeader(14);
		this.columnValues = this.generateSampleExportColumns(10, 14);
		this.testFileName = "test.csv";
		this.sheetName = "List";

		this.germplasmList = this.generateGermplasmList();
		this.input = this.generateGermplasmListExportInputValues();
	}

	@After
	public void tearDown() {
		File file = new File(this.testFileName);
		file.deleteOnExit();
	}

	@Test
	public void testGenerateCSVFile() throws IOException {

		File generatedFile = this.exportService.generateCSVFile(this.columnValues, this.columnsHeaders, this.testFileName);

		CSVReader reader = new CSVReader(new FileReader(generatedFile), ',');

		int index = 0;
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			if (nextLine != null) {
				// Verifying the read data here
				String actualData[];
				if (index == 0) {
					// get the columns
					actualData = this.exportService.getColumnHeaderNames(this.columnsHeaders);
				} else {
					// the actual data
					actualData = this.exportService.getColumnValues(this.columnValues.get(index - 1), this.columnsHeaders);
				}
				Assert.assertEquals("Should have the same value in the file and the java representation of the string arrays",
						Arrays.toString(actualData), Arrays.toString(nextLine));
			}
			index++;
		}
		reader.close();
	}

	@Test
	public void testGenerateCSVFileWithHeader() throws IOException {

		File generatedFile = this.exportService.generateCSVFile(this.columnValues, this.columnsHeaders, this.testFileName, true);

		CSVReader reader = new CSVReader(new FileReader(generatedFile), ',');

		int index = 0;
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			if (nextLine != null) {
				// Verifying the read data here
				String actualData[];
				if (index == 0) {
					// get the columns
					actualData = this.exportService.getColumnHeaderNames(this.columnsHeaders);
				} else {
					// the actual data
					actualData = this.exportService.getColumnValues(this.columnValues.get(index - 1), this.columnsHeaders);
				}
				Assert.assertEquals("Should have the same value in the file and the java representation of the string arrays",
						Arrays.toString(actualData), Arrays.toString(nextLine));
			}
			index++;
		}
		reader.close();
	}

	@Test
	public void testGenerateCSVFileWithoutHeader() throws IOException {

		File generatedFile = this.exportService.generateCSVFile(this.columnValues, this.columnsHeaders, this.testFileName, false);

		CSVReader reader = new CSVReader(new FileReader(generatedFile), ',');

		int index = 0;
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			if (nextLine != null) {
				// Verifying the read data here
				String actualData[];
				// we don't inclde the header in the checking
				// the actual data
				actualData = this.exportService.getColumnValues(this.columnValues.get(index), this.columnsHeaders);

				Assert.assertEquals("Should have the same value in the file and the java representation of the string arrays",
						Arrays.toString(actualData), Arrays.toString(nextLine));
			}
			index++;
		}
		reader.close();
	}

	@Test
	public void testGetColumnValues() {
		String actualData[];
		for (int i = 0; i < this.columnValues.size(); i++) {
			actualData = this.exportService.getColumnValues(this.columnValues.get(i), this.columnsHeaders);
			Assert.assertEquals("Should have the same size of column values", actualData.length, this.columnsHeaders.size());

		}
	}

	@Test
	public void testGetColumnHeaderNames() {
		String actualData[];
		actualData = this.exportService.getColumnHeaderNames(this.columnsHeaders);
		Assert.assertEquals("Should have the same size of column names", actualData.length, this.columnsHeaders.size());
	}

	@Test
	public void testCleanNameValueCommasWithNoComma() {
		String param = "Test Value";
		Assert.assertEquals("Should be still the same string since there is no comma character",
				this.exportService.cleanNameValueCommas(param), param);
	}

	@Test
	public void testCleanNameValueCommasWithAComma() {
		String param = "Test, Value";
		String paramNew = "Test_ Value";
		Assert.assertEquals("The comma character in the string should be change to a _ character",
				this.exportService.cleanNameValueCommas(param), paramNew);
	}

	@Test
	public void testCleanNameValueCommasWithNullParameter() {
		String param = null;
		Assert.assertEquals("Should be empty string since param passed was null", this.exportService.cleanNameValueCommas(param), "");
	}

	private List<Map<Integer, ExportColumnValue>> generateSampleExportColumns(int rows, int columnHeaders) {
		List<Map<Integer, ExportColumnValue>> exportColumnValues = new ArrayList<Map<Integer, ExportColumnValue>>();
		for (int i = 0; i < rows; i++) {
			Map<Integer, ExportColumnValue> mapData = new HashMap<Integer, ExportColumnValue>();
			for (int x = 0; x < columnHeaders; x++) {
				Integer id = new Integer(x);
				mapData.put(id, new ExportColumnValue(id, i + ": , Value -" + x));
			}
			exportColumnValues.add(mapData);
		}
		return exportColumnValues;
	}

	private List<ExportColumnHeader> generateSampleExportColumnHeader(int columnHeaders) {
		List<ExportColumnHeader> exportColumnHeaders = new ArrayList<ExportColumnHeader>();
		for (int x = 0; x < columnHeaders; x++) {
			Integer id = new Integer(x);
			boolean isDisplay = true;

			exportColumnHeaders.add(new ExportColumnHeader(id, "Column Name -" + x, isDisplay));
		}
		return exportColumnHeaders;
	}

	@Test
	public void testCreateWorkbookForSingleSheet() {
		HSSFWorkbook wb = this.exportService.createWorkbookForSingleSheet(this.columnValues, this.columnsHeaders, this.sheetName);
		HSSFSheet sheet = wb.getSheetAt(0);

		Assert.assertTrue("Expected to return a sheetName = " + this.sheetName, sheet.getSheetName().equalsIgnoreCase(this.sheetName));

		HSSFRow header = sheet.getRow(0);
		for (int i = 0; i < this.columnsHeaders.size(); i++) {
			Assert.assertTrue("Expected that the column headers are placed in order.", this.columnsHeaders.get(i).getName()
					.equalsIgnoreCase(header.getCell(i).getStringCellValue()));
		}

		Assert.assertTrue("Expected to have a total of " + this.columnValues.size() + " rows excluding the columnHeader.",
				sheet.getLastRowNum() == this.columnValues.size());

		int rowCount = 1;
		for (Map<Integer, ExportColumnValue> rowEntry : this.columnValues) {
			HSSFRow row = sheet.getRow(rowCount);

			for (Map.Entry<Integer, ExportColumnValue> rowValue : rowEntry.entrySet()) {
				Integer id = Integer.valueOf(rowValue.getKey());
				String value = rowValue.getValue().getValue();
				Assert.assertEquals("Expected that the row values corresponds to their respective columns.", value, row.getCell(id)
						.getStringCellValue());
			}

			rowCount++;
		}

	}

	@Test
	public void testGenerateExcelFileForSingleSheet() {
		try {
			this.exportService.generateExcelFileForSingleSheet(this.columnValues, this.columnsHeaders, this.testFileName, this.sheetName);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetNoOfVisibleColumns() {
		int visibleColumns = this.exportService.getNoOfVisibleColumns(this.input.getVisibleColumnMap());
		Assert.assertTrue("Expected that the number of visibleColums = " + this.input.getVisibleColumnMap().size(),
				visibleColumns == this.input.getVisibleColumnMap().size());

		this.input.getVisibleColumnMap().put(ColumnLabels.SEED_SOURCE.getName(), false);
		visibleColumns = this.exportService.getNoOfVisibleColumns(this.input.getVisibleColumnMap());
		Assert.assertTrue("Expected that the number of visibleColums = " + (this.input.getVisibleColumnMap().size() - 1),
				visibleColumns == this.input.getVisibleColumnMap().size() - 1);
	}

	@Test
	public void testGenerateObservationSheet() {
		// input data
		HSSFWorkbook wb = new HSSFWorkbook();
		Map<String, CellStyle> sheetStyles = this.exportService.createStyles(wb);
		GermplasmList germplasmList = this.input.getGermplasmList();
		List<GermplasmListData> listDatas = germplasmList.getListData();
		Map<String, Boolean> visibleColumnMap = this.input.getVisibleColumnMap();

		// to test

		try {
			this.exportService.generateObservationSheet(wb, sheetStyles, this.input);
		} catch (GermplasmListExporterException e) {
			Assert.fail(e.getMessage());
		}

		HSSFSheet observationSheet = wb.getSheet("Observation");

		HSSFRow row = null;
		int columnIndex = 0;

		// Assert Header Row
		row = observationSheet.getRow(0);

		if (visibleColumnMap.get(ColumnLabels.ENTRY_ID.getName())) {
			Assert.assertEquals("Expecting " + row.getCell(columnIndex).getStringCellValue() + " equals to ENTRY but didn't.",
					row.getCell(columnIndex).getStringCellValue(), "ENTRY_ID");
			columnIndex++;
		}
		if (visibleColumnMap.get(ColumnLabels.GID.getName())) {
			Assert.assertEquals("Expecting " + row.getCell(columnIndex).getStringCellValue() + " equals to GID but didn't.",
					row.getCell(columnIndex).getStringCellValue(), "GID");
			columnIndex++;
		}
		if (visibleColumnMap.get(ColumnLabels.ENTRY_CODE.getName())) {
			Assert.assertEquals("Expecting " + row.getCell(columnIndex).getStringCellValue() + " equals to ENTRY CODE but didn't.", row
					.getCell(columnIndex).getStringCellValue(), "ENTRY CODE");
			columnIndex++;
		}
		if (visibleColumnMap.get(ColumnLabels.DESIGNATION.getName())) {
			Assert.assertEquals("Expecting " + row.getCell(columnIndex).getStringCellValue() + " equals to DESIGNATION but didn't.", row
					.getCell(columnIndex).getStringCellValue(), "DESIGNATION");
			columnIndex++;
		}
		if (visibleColumnMap.get(ColumnLabels.PARENTAGE.getName())) {
			Assert.assertEquals("Expecting " + row.getCell(columnIndex).getStringCellValue() + " equals to CROSS but didn't.",
					row.getCell(columnIndex).getStringCellValue(), "PARENTAGE");
			columnIndex++;
		}
		if (visibleColumnMap.get(ColumnLabels.SEED_SOURCE.getName())) {
			Assert.assertEquals("Expecting " + row.getCell(columnIndex).getStringCellValue() + " equals to SOURCE but didn't.", row
					.getCell(columnIndex).getStringCellValue(), "SEED SOURCE");
			columnIndex++;
		}

		// Assert Row Values
		int rowIndex = 1;
		for (GermplasmListData listData : listDatas) {
			row = observationSheet.getRow(rowIndex);

			columnIndex = 0;
			if (visibleColumnMap.get(ColumnLabels.ENTRY_ID.getName())) {
				Assert.assertEquals("Expecting " + this.getInteger(row.getCell(columnIndex).getNumericCellValue()) + " equals to "
						+ listData.getEntryId() + " but didn't.", this.getInteger(row.getCell(columnIndex).getNumericCellValue()),
						listData.getEntryId());
				columnIndex++;
			}
			if (visibleColumnMap.get(ColumnLabels.GID.getName())) {
				Assert.assertEquals("Expecting " + this.getInteger(row.getCell(columnIndex).getNumericCellValue()) + " equals to "
						+ listData.getEntryId() + " but didn't.", this.getInteger(row.getCell(columnIndex).getNumericCellValue()),
						listData.getGid());
				columnIndex++;
			}
			if (visibleColumnMap.get(ColumnLabels.ENTRY_CODE.getName())) {
				Assert.assertEquals("Expecting " + row.getCell(columnIndex).getStringCellValue() + " equals to " + listData.getEntryCode()
						+ " but didn't.", row.getCell(columnIndex).getStringCellValue(), listData.getEntryCode());
				columnIndex++;
			}
			if (visibleColumnMap.get(ColumnLabels.DESIGNATION.getName())) {
				Assert.assertEquals(
						"Expecting " + row.getCell(columnIndex).getStringCellValue() + " equals to " + listData.getDesignation()
								+ " but didn't.", row.getCell(columnIndex).getStringCellValue(), listData.getDesignation());
				columnIndex++;
			}
			if (visibleColumnMap.get(ColumnLabels.PARENTAGE.getName())) {
				Assert.assertEquals("Expecting " + row.getCell(columnIndex).getStringCellValue() + " equals to " + listData.getGroupName()
						+ " but didn't.", row.getCell(columnIndex).getStringCellValue(), listData.getGroupName());
				columnIndex++;
			}
			if (visibleColumnMap.get(ColumnLabels.SEED_SOURCE.getName())) {
				Assert.assertEquals("Expecting " + row.getCell(columnIndex).getStringCellValue() + " equals to " + listData.getSeedSource()
						+ " but didn't.", row.getCell(columnIndex).getStringCellValue(), listData.getSeedSource());
				columnIndex++;
			}
			rowIndex++;
		}
	}

	@Test
	public void testGenerateDescriptionSheet() {

		// input data
		HSSFWorkbook wb = new HSSFWorkbook();
		Map<String, CellStyle> sheetStyles = this.exportService.createStyles(wb);
		GermplasmList germplasmList = this.input.getGermplasmList();
		Map<String, Boolean> visibleColumnMap = this.input.getVisibleColumnMap();

		// to test
		try {
			this.exportService.generateDescriptionSheet(wb, sheetStyles, this.input);
		} catch (GermplasmListExporterException e) {
			Assert.fail(e.getMessage());
		}

		HSSFSheet descriptionSheet = wb.getSheet("Description");

		Assert.assertNotNull("Expected to successfully generated the description sheet.", descriptionSheet);
		Assert.assertTrue("The sheet name is Description.", "Description".equalsIgnoreCase(descriptionSheet.getSheetName()));

		HSSFRow row = null;
		// Assert List Details Section
		row = descriptionSheet.getRow(0);
		Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to LIST NAME but didn't.", row.getCell(0)
				.getStringCellValue(), "LIST NAME");
		Assert.assertEquals(row.getCell(1).getStringCellValue(), germplasmList.getName());

		row = descriptionSheet.getRow(1);
		Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to LIST DESCRIPTION but didn't.", row.getCell(0)
				.getStringCellValue(), "LIST DESCRIPTION");
		Assert.assertEquals(row.getCell(1).getStringCellValue(), germplasmList.getDescription());

		row = descriptionSheet.getRow(2);
		Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to LIST TYPE but didn't.", row.getCell(0)
				.getStringCellValue(), "LIST TYPE");
		Assert.assertEquals(row.getCell(1).getStringCellValue(), germplasmList.getType());

		row = descriptionSheet.getRow(3);
		Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to LIST DATE but didn't.", row.getCell(0)
				.getStringCellValue(), "LIST DATE");
		Assert.assertEquals(this.getLong(row.getCell(1).getNumericCellValue()), germplasmList.getDate());

		// Assert List Condition Section
		row = descriptionSheet.getRow(5);
		Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to CONDITION but didn't.", row.getCell(0)
				.getStringCellValue(), "CONDITION");
		Assert.assertEquals("Expecting " + row.getCell(1).getStringCellValue() + " equals to DESCRIPTION but didn't.", row.getCell(1)
				.getStringCellValue(), "DESCRIPTION");
		Assert.assertEquals("Expecting " + row.getCell(2).getStringCellValue() + " equals to PROPERTY but didn't.", row.getCell(2)
				.getStringCellValue(), "PROPERTY");
		Assert.assertEquals("Expecting " + row.getCell(3).getStringCellValue() + " equals to SCALE but didn't.", row.getCell(3)
				.getStringCellValue(), "SCALE");
		Assert.assertEquals("Expecting " + row.getCell(4).getStringCellValue() + " equals to METHOD but didn't.", row.getCell(4)
				.getStringCellValue(), "METHOD");
		Assert.assertEquals("Expecting " + row.getCell(5).getStringCellValue() + " equals to DATA TYPE but didn't.", row.getCell(5)
				.getStringCellValue(), "DATA TYPE");
		Assert.assertEquals("Expecting " + row.getCell(6).getStringCellValue() + " equals to VALUE but didn't.", row.getCell(6)
				.getStringCellValue(), "VALUE");

		row = descriptionSheet.getRow(6);
		Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to LIST USER but didn't.", row.getCell(0)
				.getStringCellValue(), "LIST USER");
		Assert.assertEquals("Expecting " + row.getCell(1).getStringCellValue() + " equals to PERSON WHO MADE THE LIST but didn't.", row
				.getCell(1).getStringCellValue(), "PERSON WHO MADE THE LIST");
		Assert.assertEquals("Expecting " + row.getCell(2).getStringCellValue() + " equals to PERSON but didn't.", row.getCell(2)
				.getStringCellValue(), "PERSON");
		Assert.assertEquals("Expecting " + row.getCell(3).getStringCellValue() + " equals to DBCV but didn't.", row.getCell(3)
				.getStringCellValue(), "DBCV");
		Assert.assertEquals("Expecting " + row.getCell(4).getStringCellValue() + " equals to ASSIGNED but didn't.", row.getCell(4)
				.getStringCellValue(), "ASSIGNED");
		Assert.assertEquals("Expecting " + row.getCell(5).getStringCellValue() + " equals to C but didn't.", row.getCell(5)
				.getStringCellValue(), "C");
		Assert.assertEquals(
				"Expecting " + row.getCell(6).getStringCellValue() + " equals to " + this.input.getOwnerName() + " but didn't.", row
						.getCell(6).getStringCellValue(), this.input.getOwnerName());

		row = descriptionSheet.getRow(7);
		Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to LIST USER ID but didn't.", row.getCell(0)
				.getStringCellValue(), "LIST USER ID");
		Assert.assertEquals("Expecting " + row.getCell(1).getStringCellValue() + " equals to ID OF LIST OWNER but didn't.", row.getCell(1)
				.getStringCellValue(), "ID OF LIST OWNER");
		Assert.assertEquals("Expecting " + row.getCell(2).getStringCellValue() + " equals to PERSON but didn't.", row.getCell(2)
				.getStringCellValue(), "PERSON");
		Assert.assertEquals("Expecting " + row.getCell(3).getStringCellValue() + " equals to DBID but didn't.", row.getCell(3)
				.getStringCellValue(), "DBID");
		Assert.assertEquals("Expecting " + row.getCell(4).getStringCellValue() + " equals to ASSIGNED but didn't.", row.getCell(4)
				.getStringCellValue(), "ASSIGNED");
		Assert.assertEquals("Expecting " + row.getCell(5).getStringCellValue() + " equals to N but didn't.", row.getCell(5)
				.getStringCellValue(), "N");
		Assert.assertEquals(
				"Expecting " + this.getInteger(row.getCell(6).getNumericCellValue()) + " equals to " + germplasmList.getUserId()
						+ " but didn't.", this.getInteger(row.getCell(6).getNumericCellValue()), germplasmList.getUserId());

		row = descriptionSheet.getRow(8);
		Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to LIST EXPORTER but didn't.", row.getCell(0)
				.getStringCellValue(), "LIST EXPORTER");
		Assert.assertEquals("Expecting " + row.getCell(1).getStringCellValue() + " equals to PERSON EXPORTING THE LIST but didn't.", row
				.getCell(1).getStringCellValue(), "PERSON EXPORTING THE LIST");
		Assert.assertEquals("Expecting " + row.getCell(2).getStringCellValue() + " equals to PERSON but didn't.", row.getCell(2)
				.getStringCellValue(), "PERSON");
		Assert.assertEquals("Expecting " + row.getCell(3).getStringCellValue() + " equals to DBCV but didn't.", row.getCell(3)
				.getStringCellValue(), "DBCV");
		Assert.assertEquals("Expecting " + row.getCell(4).getStringCellValue() + " equals to ASSIGNED but didn't.", row.getCell(4)
				.getStringCellValue(), "ASSIGNED");
		Assert.assertEquals("Expecting " + row.getCell(5).getStringCellValue() + " equals to C but didn't.", row.getCell(5)
				.getStringCellValue(), "C");
		Assert.assertEquals("Expecting " + row.getCell(6).getStringCellValue() + " equals to " + this.input.getExporterName()
				+ " but didn't.", row.getCell(6).getStringCellValue(), this.input.getExporterName());

		row = descriptionSheet.getRow(9);
		Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to LIST EXPORTER ID but didn't.", row.getCell(0)
				.getStringCellValue(), "LIST EXPORTER ID");
		Assert.assertEquals("Expecting " + row.getCell(1).getStringCellValue() + " equals to ID OF LIST EXPORTER but didn't.",
				row.getCell(1).getStringCellValue(), "ID OF LIST EXPORTER");
		Assert.assertEquals("Expecting " + row.getCell(2).getStringCellValue() + " equals to PERSON but didn't.", row.getCell(2)
				.getStringCellValue(), "PERSON");
		Assert.assertEquals("Expecting " + row.getCell(3).getStringCellValue() + " equals to DBID but didn't.", row.getCell(3)
				.getStringCellValue(), "DBID");
		Assert.assertEquals("Expecting " + row.getCell(4).getStringCellValue() + " equals to ASSIGNED but didn't.", row.getCell(4)
				.getStringCellValue(), "ASSIGNED");
		Assert.assertEquals("Expecting " + row.getCell(5).getStringCellValue() + " equals to N but didn't.", row.getCell(5)
				.getStringCellValue(), "N");
		Assert.assertEquals(
				"Expecting " + this.getInteger(row.getCell(6).getNumericCellValue()) + " equals to "
						+ this.input.getCurrentLocalIbdbUserId() + "R but didn't.", this.getInteger(row.getCell(6).getNumericCellValue()),
				this.input.getCurrentLocalIbdbUserId());

		// Assert List Factor Section
		row = descriptionSheet.getRow(11);
		Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to FACTOR but didn't.", row.getCell(0)
				.getStringCellValue(), "FACTOR");
		Assert.assertEquals("Expecting " + row.getCell(1).getStringCellValue() + " equals to DESCRIPTION but didn't.", row.getCell(1)
				.getStringCellValue(), "DESCRIPTION");
		Assert.assertEquals("Expecting " + row.getCell(2).getStringCellValue() + " equals to PROPERTY but didn't.", row.getCell(2)
				.getStringCellValue(), "PROPERTY");
		Assert.assertEquals("Expecting " + row.getCell(3).getStringCellValue() + " equals to SCALE but didn't.", row.getCell(3)
				.getStringCellValue(), "SCALE");
		Assert.assertEquals("Expecting " + row.getCell(4).getStringCellValue() + " equals to METHOD but didn't.", row.getCell(4)
				.getStringCellValue(), "METHOD");
		Assert.assertEquals("Expecting " + row.getCell(5).getStringCellValue() + " equals to DATA TYPE but didn't.", row.getCell(5)
				.getStringCellValue(), "DATA TYPE");
		Assert.assertEquals("Expecting " + row.getCell(6).getStringCellValue() + " equals to NESTED IN but didn't.", row.getCell(6)
				.getStringCellValue(), "NESTED IN");

		int rowIndex = 11;
		if (visibleColumnMap.get(ColumnLabels.ENTRY_ID.getName())) {
			row = descriptionSheet.getRow(++rowIndex);
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to ENTRY but didn't.", row.getCell(0)
					.getStringCellValue(), "ENTRY");
			Assert.assertEquals("Expecting " + row.getCell(1).getStringCellValue() + " equals to The germplasm entry number but didn't.",
					row.getCell(1).getStringCellValue(), "The germplasm entry number");
			Assert.assertEquals("Expecting " + row.getCell(2).getStringCellValue() + " equals to GERMPLASM ENTRY but didn't.",
					row.getCell(2).getStringCellValue(), "GERMPLASM ENTRY");
			Assert.assertEquals("Expecting " + row.getCell(3).getStringCellValue() + " equals to NUMBER but didn't.", row.getCell(3)
					.getStringCellValue(), "NUMBER");
			Assert.assertEquals("Expecting " + row.getCell(4).getStringCellValue() + " equals to ENUMERATED but didn't.", row.getCell(4)
					.getStringCellValue(), "ENUMERATED");
			Assert.assertEquals("Expecting " + row.getCell(5).getStringCellValue() + " equals to N but didn't.", row.getCell(5)
					.getStringCellValue(), "N");
			Assert.assertEquals("Expecting " + row.getCell(6).getStringCellValue() + " equals to '' but didn't.", row.getCell(6)
					.getStringCellValue(), "");
		}
		if (visibleColumnMap.get(ColumnLabels.GID.getName())) {
			row = descriptionSheet.getRow(++rowIndex);
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to GID but didn't.", row.getCell(0)
					.getStringCellValue(), "GID");
			Assert.assertEquals("Expecting " + row.getCell(1).getStringCellValue() + " equals to The GID of the germplasm but didn't.", row
					.getCell(1).getStringCellValue(), "The GID of the germplasm");
			Assert.assertEquals("Expecting " + row.getCell(2).getStringCellValue() + " equals to GERMPLASM ID but didn't.", row.getCell(2)
					.getStringCellValue(), "GERMPLASM ID");
			Assert.assertEquals("Expecting " + row.getCell(3).getStringCellValue() + " equals to DBID but didn't.", row.getCell(3)
					.getStringCellValue(), "DBID");
			Assert.assertEquals("Expecting " + row.getCell(4).getStringCellValue() + " equals to ASSIGNED but didn't.", row.getCell(4)
					.getStringCellValue(), "ASSIGNED");
			Assert.assertEquals("Expecting " + row.getCell(5).getStringCellValue() + " equals to N but didn't.", row.getCell(5)
					.getStringCellValue(), "N");
			Assert.assertEquals("Expecting " + row.getCell(6).getStringCellValue() + " equals to '' but didn't.", row.getCell(6)
					.getStringCellValue(), "");
		}
		if (visibleColumnMap.get(ColumnLabels.ENTRY_CODE.getName())) {
			row = descriptionSheet.getRow(++rowIndex);
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to ENTRY CODE but didn't.", row.getCell(0)
					.getStringCellValue(), "ENTRY CODE");
			Assert.assertEquals("Expecting " + row.getCell(1).getStringCellValue() + " equals to Germplasm entry code but didn't.", row
					.getCell(1).getStringCellValue(), "Germplasm entry code");
			Assert.assertEquals("Expecting " + row.getCell(2).getStringCellValue() + " equals to GERMPLASM ENTRY but didn't.",
					row.getCell(2).getStringCellValue(), "GERMPLASM ENTRY");
			Assert.assertEquals("Expecting " + row.getCell(3).getStringCellValue() + " equals to CODE but didn't.", row.getCell(3)
					.getStringCellValue(), "CODE");
			Assert.assertEquals("Expecting " + row.getCell(4).getStringCellValue() + " equals to ASSIGNED but didn't.", row.getCell(4)
					.getStringCellValue(), "ASSIGNED");
			Assert.assertEquals("Expecting " + row.getCell(5).getStringCellValue() + " equals to C but didn't.", row.getCell(5)
					.getStringCellValue(), "C");
			Assert.assertEquals("Expecting " + row.getCell(6).getStringCellValue() + " equals to '' but didn't.", row.getCell(6)
					.getStringCellValue(), "");
		}
		if (visibleColumnMap.get(ColumnLabels.DESIGNATION.getName())) {
			row = descriptionSheet.getRow(++rowIndex);
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to DESIGNATION but didn't.", row.getCell(0)
					.getStringCellValue(), "DESIGNATION");
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to The name of the germplasm but didn't.",
					row.getCell(1).getStringCellValue(), "The name of the germplasm");
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to GERMPLASM ID but didn't.", row.getCell(2)
					.getStringCellValue(), "GERMPLASM ID");
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to DBCV but didn't.", row.getCell(3)
					.getStringCellValue(), "DBCV");
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to ASSIGNED but didn't.", row.getCell(4)
					.getStringCellValue(), "ASSIGNED");
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to C but didn't.", row.getCell(5)
					.getStringCellValue(), "C");
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to '' but didn't.", row.getCell(6)
					.getStringCellValue(), "");
		}
		if (visibleColumnMap.get(ColumnLabels.PARENTAGE.getName())) {
			row = descriptionSheet.getRow(++rowIndex);
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to CROSS but didn't.", row.getCell(0)
					.getStringCellValue(), "CROSS");
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue()
					+ " equals to The pedigree string of the germplasm but didn't.", row.getCell(1).getStringCellValue(),
					"The pedigree string of the germplasm");
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to CROSS NAME but didn't.", row.getCell(2)
					.getStringCellValue(), "CROSS NAME");
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to NAME but didn't.", row.getCell(3)
					.getStringCellValue(), "NAME");
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to ASSIGNED but didn't.", row.getCell(4)
					.getStringCellValue(), "ASSIGNED");
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to C but didn't.", row.getCell(5)
					.getStringCellValue(), "C");
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to '' but didn't.", row.getCell(6)
					.getStringCellValue(), "");
		}
		if (visibleColumnMap.get(ColumnLabels.SEED_SOURCE.getName())) {
			row = descriptionSheet.getRow(++rowIndex);
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to SOURCE but didn't.", row.getCell(0)
					.getStringCellValue(), "SOURCE");
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue()
					+ " equals to The seed source of the germplasm but didn't.", row.getCell(1).getStringCellValue(),
					"The seed source of the germplasm");
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to SEED SOURCE but didn't.", row.getCell(2)
					.getStringCellValue(), "SEED SOURCE");
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to NAME but didn't.", row.getCell(3)
					.getStringCellValue(), "NAME");
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to Seed Source but didn't.", row.getCell(4)
					.getStringCellValue(), "Seed Source");
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to C but didn't.", row.getCell(5)
					.getStringCellValue(), "C");
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue() + " equals to '' but didn't.", row.getCell(6)
					.getStringCellValue(), "");
		}
	}

	private Long getLong(double numericCellValue) {
		return Math.round(numericCellValue);
	}

	private Integer getInteger(double numericCellValue) {
		return Integer.valueOf(String.valueOf(this.getLong(numericCellValue)));
	}

	@Test
	public void testGenerateGermplasmListExcelFile() {
		try {
			this.exportService.generateGermplasmListExcelFile(this.input);
		} catch (GermplasmListExporterException e) {
			Assert.fail(e.getMessage());
		}
	}

	private GermplasmListExportInputValues generateGermplasmListExportInputValues() {
		GermplasmListExportInputValues input = new GermplasmListExportInputValues();

		input.setFileName(this.testFileName);
		input.setGermplasmList(this.germplasmList);
		input.setOwnerName(ExportServiceImplTest.CURRENT_USER_NAME);
		input.setCurrentLocalIbdbUserId(ExportServiceImplTest.CURRENT_USER_ID);
		input.setExporterName(ExportServiceImplTest.CURRENT_USER_NAME);
		input.setVisibleColumnMap(this.getVisibleColumnMap());
		return input;
	}

	private Map<String, Boolean> getVisibleColumnMap() {
		Map<String, Boolean> visibleColumnMap = new HashMap<String, Boolean>();

		visibleColumnMap.put(ColumnLabels.ENTRY_ID.getName(), true);
		visibleColumnMap.put(ColumnLabels.DESIGNATION.getName(), true);
		visibleColumnMap.put(ColumnLabels.PARENTAGE.getName(), true);
		visibleColumnMap.put(ColumnLabels.SEED_SOURCE.getName(), true);
		visibleColumnMap.put(ColumnLabels.GID.getName(), true);
		visibleColumnMap.put(ColumnLabels.ENTRY_CODE.getName(), true);

		return visibleColumnMap;
	}

	private GermplasmList generateGermplasmList() {
		GermplasmList germplasmList = new GermplasmList();
		germplasmList.setName("Sample List");
		germplasmList.setUserId(ExportServiceImplTest.USER_ID);
		germplasmList.setDescription("Sample description");
		germplasmList.setType("LST");
		germplasmList.setDate(20141112L);
		germplasmList.setNotes("Sample Notes");
		germplasmList.setListData(ExportServiceImplTest.generateListEntries());

		return germplasmList;
	}

	private static List<GermplasmListData> generateListEntries() {
		List<GermplasmListData> entries = new ArrayList<>();

		for (int x = 1; x <= ExportServiceImplTest.NO_OF_LIST_ENTRIES; x++) {
			GermplasmListData germplasmListData = new GermplasmListData();
			germplasmListData.setId(x);
			germplasmListData.setEntryId(x);
			germplasmListData.setDesignation(ColumnLabels.DESIGNATION.getName() + x);
			germplasmListData.setGroupName(ColumnLabels.PARENTAGE.getName() + x);
			ListDataInventory inventoryInfo = new ListDataInventory(x, x);
			inventoryInfo.setLotCount(1);
			inventoryInfo.setReservedLotCount(1);
			inventoryInfo.setActualInventoryLotCount(1);
			germplasmListData.setInventoryInfo(inventoryInfo);
			germplasmListData.setEntryCode(ColumnLabels.ENTRY_CODE.getName() + x);
			germplasmListData.setSeedSource(ColumnLabels.SEED_SOURCE.getName() + x);
			germplasmListData.setGid(x);
			entries.add(germplasmListData);
		}

		return entries;
	}

}
