
package org.generationcp.commons.service.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import au.com.bytecode.opencsv.CSVReader;
import junit.framework.Assert;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.generationcp.commons.constant.ColumnLabels;
import org.generationcp.commons.exceptions.GermplasmListExporterException;
import org.generationcp.commons.pojo.ExportColumnHeader;
import org.generationcp.commons.pojo.ExportColumnValue;
import org.generationcp.commons.pojo.GermplasmListExportInputValues;
import org.generationcp.commons.service.FileService;
import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class GermplasmExportServiceImplTest {

	@Mock
	private FileService fileService;

	@InjectMocks
	private GermplasmExportServiceImpl germplasmExportService  = Mockito.spy(new GermplasmExportServiceImpl());

	private static final String CURRENT_USER_NAME = "User User";
	private static final int CURRENT_USER_ID = 1;
	private static final Integer USER_ID = 1;
	private static final int NO_OF_LIST_ENTRIES = 10;

	private List<ExportColumnHeader> columnsHeaders;
	private List<Map<Integer, ExportColumnValue>> columnValues;
	private String testFileName;
	private String sheetName;
	private GermplasmListExportInputValues input;
	private GermplasmList germplasmList;

	@Before
	public void setUp() throws InvalidFormatException, IOException {
		MockitoAnnotations.initMocks(this);

		this.columnsHeaders = this.generateSampleExportColumnHeader(14);
		this.columnValues = this.generateSampleExportColumns(10, 14);
		this.testFileName = "test.csv";
		this.sheetName = "List";

		this.germplasmList = this.generateGermplasmList();
		this.input = this.generateGermplasmListExportInputValues();

		this.germplasmExportService.setTemplateFile(this.testFileName);
		Mockito.doReturn(this.createWorkbook()).when(this.fileService).retrieveWorkbookTemplate(this.testFileName);
	}

	@After
	public void tearDown() {
		File file = new File(this.testFileName);
		file.deleteOnExit();
	}

	@Test
	public void testGenerateCSVFile() throws IOException {

		File generatedFile = this.germplasmExportService.generateCSVFile(this.columnValues, this.columnsHeaders, this.testFileName);

		CSVReader reader = new CSVReader(new FileReader(generatedFile), ',');

		int index = 0;
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			if (nextLine != null) {
				// Verifying the read data here
				String actualData[];
				if (index == 0) {
					// get the columns
					actualData = this.germplasmExportService.getColumnHeaderNames(this.columnsHeaders);
				} else {
					// the actual data
					actualData = this.germplasmExportService.getColumnValues(this.columnValues.get(index - 1), this.columnsHeaders);
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

		File generatedFile = this.germplasmExportService.generateCSVFile(this.columnValues, this.columnsHeaders, this.testFileName, true);

		CSVReader reader = new CSVReader(new FileReader(generatedFile), ',');

		int index = 0;
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			if (nextLine != null) {
				// Verifying the read data here
				String actualData[];
				if (index == 0) {
					// get the columns
					actualData = this.germplasmExportService.getColumnHeaderNames(this.columnsHeaders);
				} else {
					// the actual data
					actualData = this.germplasmExportService.getColumnValues(this.columnValues.get(index - 1), this.columnsHeaders);
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

		File generatedFile = this.germplasmExportService.generateCSVFile(this.columnValues, this.columnsHeaders, this.testFileName, false);

		CSVReader reader = new CSVReader(new FileReader(generatedFile), ',');

		int index = 0;
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			if (nextLine != null) {
				// Verifying the read data here
				String actualData[];
				// we don't inclde the header in the checking
				// the actual data
				actualData = this.germplasmExportService.getColumnValues(this.columnValues.get(index), this.columnsHeaders);

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
			actualData = this.germplasmExportService.getColumnValues(this.columnValues.get(i), this.columnsHeaders);
			Assert.assertEquals("Should have the same size of column values", actualData.length, this.columnsHeaders.size());

		}
	}

	@Test
	public void testGetColumnHeaderNames() {
		String actualData[];
		actualData = this.germplasmExportService.getColumnHeaderNames(this.columnsHeaders);
		Assert.assertEquals("Should have the same size of column names", actualData.length, this.columnsHeaders.size());
	}

	@Test
	public void testCleanNameValueCommasWithNoComma() {
		String param = "Test Value";
		Assert.assertEquals("Should be still the same string since there is no comma character",
				this.germplasmExportService.cleanNameValueCommas(param), param);
	}

	@Test
	public void testCleanNameValueCommasWithAComma() {
		String param = "Test, Value";
		String paramNew = "Test_ Value";
		Assert.assertEquals("The comma character in the string should be change to a _ character",
				this.germplasmExportService.cleanNameValueCommas(param), paramNew);
	}

	@Test
	public void testCleanNameValueCommasWithNullParameter() {
		String param = null;
		Assert.assertEquals("Should be empty string since param passed was null", this.germplasmExportService.cleanNameValueCommas(param), "");
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
		HSSFWorkbook wb = this.germplasmExportService.createWorkbookForSingleSheet(this.columnValues, this.columnsHeaders, this.sheetName);
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
			this.germplasmExportService.generateExcelFileForSingleSheet(this.columnValues, this.columnsHeaders, this.testFileName, this.sheetName);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetNoOfVisibleColumns() {
		int visibleColumns = this.germplasmExportService.getNoOfVisibleColumns(this.input.getVisibleColumnMap());
		Assert.assertTrue("Expected that the number of visibleColums = " + this.input.getVisibleColumnMap().size(),
				visibleColumns == this.input.getVisibleColumnMap().size());

		this.input.getVisibleColumnMap().put(ColumnLabels.SEED_SOURCE.getName(), false);
		visibleColumns = this.germplasmExportService.getNoOfVisibleColumns(this.input.getVisibleColumnMap());
		Assert.assertTrue("Expected that the number of visibleColums = " + (this.input.getVisibleColumnMap().size() - 1),
				visibleColumns == this.input.getVisibleColumnMap().size() - 1);
	}

	@Test
	public void testGenerateObservationSheet() {
		// input data
		HSSFWorkbook wb = this.createWorkbook();
		Map<String, CellStyle> sheetStyles = this.germplasmExportService.createStyles(wb);
		GermplasmList germplasmList = this.input.getGermplasmList();
		List<GermplasmListData> listDatas = germplasmList.getListData();
		Map<String, Boolean> visibleColumnMap = this.input.getVisibleColumnMap();

		// to test

		try {
			this.germplasmExportService.generateObservationSheet(wb, sheetStyles, this.input);
		} catch (GermplasmListExporterException e) {
			Assert.fail(e.getMessage());
		}

		HSSFSheet observationSheet = wb.getSheet("Observation");

		HSSFRow row = null;
		int columnIndex = 0;

		// Assert Header Row
		row = observationSheet.getRow(0);

		if (visibleColumnMap.get(String.valueOf(TermId.ENTRY_NO.getId()))) {
			Assert.assertEquals("Expecting " + row.getCell(columnIndex).getStringCellValue() + " equals to ENTRY but didn't.",
					row.getCell(columnIndex).getStringCellValue(), "ENTRY_NO");
			columnIndex++;
		}
		if (visibleColumnMap.get(String.valueOf(TermId.GID.getId()))) {
			Assert.assertEquals("Expecting " + row.getCell(columnIndex).getStringCellValue() + " equals to GID but didn't.",
					row.getCell(columnIndex).getStringCellValue(), "GID");
			columnIndex++;
		}
		if (visibleColumnMap.get(String.valueOf(TermId.ENTRY_CODE.getId()))) {
			Assert.assertEquals("Expecting " + row.getCell(columnIndex).getStringCellValue() + " equals to ENTRY CODE but didn't.", row
					.getCell(columnIndex).getStringCellValue(), "ENTRY_CODE");
			columnIndex++;
		}
		if (visibleColumnMap.get(String.valueOf(TermId.DESIG.getId()))) {
			Assert.assertEquals("Expecting " + row.getCell(columnIndex).getStringCellValue() + " equals to DESIGNATION but didn't.", row
					.getCell(columnIndex).getStringCellValue(), "DESIGNATION");
			columnIndex++;
		}
		if (visibleColumnMap.get(String.valueOf(TermId.CROSS.getId()))) {
			Assert.assertEquals("Expecting " + row.getCell(columnIndex).getStringCellValue() + " equals to CROSS but didn't.",
					row.getCell(columnIndex).getStringCellValue(), "CROSS");
			columnIndex++;
		}
		if (visibleColumnMap.get(String.valueOf(TermId.SEED_SOURCE.getId()))) {
			Assert.assertEquals("Expecting " + row.getCell(columnIndex).getStringCellValue() + " equals to SOURCE but didn't.", row
					.getCell(columnIndex).getStringCellValue(), "SEED_SOURCE");
			columnIndex++;
		}

		// Assert Row Values
		int rowIndex = 1;
		for (GermplasmListData listData : listDatas) {
			row = observationSheet.getRow(rowIndex);

			columnIndex = 0;
			if (visibleColumnMap.get(String.valueOf(TermId.ENTRY_NO.getId()))) {
				Assert.assertEquals("Expecting " + this.getInteger(row.getCell(columnIndex).getNumericCellValue()) + " equals to "
						+ listData.getEntryId() + " but didn't.", this.getInteger(row.getCell(columnIndex).getNumericCellValue()),
						listData.getEntryId());
				Assert.assertEquals(sheetStyles.get(GermplasmExportServiceImpl.NUMBER_COLUMN_HIGHLIGHT_STYLE_FACTOR),
						row.getCell(columnIndex).getCellStyle());
				columnIndex++;
			}
			if (visibleColumnMap.get(String.valueOf(TermId.GID.getId()))) {
				Assert.assertEquals("Expecting " + this.getInteger(row.getCell(columnIndex).getNumericCellValue()) + " equals to "
						+ listData.getEntryId() + " but didn't.", this.getInteger(row.getCell(columnIndex).getNumericCellValue()),
						listData.getGid());
				Assert.assertEquals(sheetStyles.get(GermplasmExportServiceImpl.NUMBER_DATA_FORMAT_STYLE), row.getCell(columnIndex).getCellStyle());
				columnIndex++;
			}
			if (visibleColumnMap.get(String.valueOf(TermId.ENTRY_CODE.getId()))) {
				Assert.assertEquals("Expecting " + row.getCell(columnIndex).getStringCellValue() + " equals to " + listData.getEntryCode()
						+ " but didn't.", row.getCell(columnIndex).getStringCellValue(), listData.getEntryCode());
				columnIndex++;
			}
			if (visibleColumnMap.get(String.valueOf(TermId.DESIG.getId()))) {
				Assert.assertEquals(
						"Expecting " + row.getCell(columnIndex).getStringCellValue() + " equals to " + listData.getDesignation()
								+ " but didn't.", row.getCell(columnIndex).getStringCellValue(), listData.getDesignation());
				columnIndex++;
			}
			if (visibleColumnMap.get(String.valueOf(TermId.CROSS.getId()))) {
				Assert.assertEquals("Expecting " + row.getCell(columnIndex).getStringCellValue() + " equals to " + listData.getGroupName()
						+ " but didn't.", row.getCell(columnIndex).getStringCellValue(), listData.getGroupName());
				columnIndex++;
			}
			if (visibleColumnMap.get(String.valueOf(TermId.SEED_SOURCE.getId()))) {
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
		Map<String, CellStyle> sheetStyles = this.germplasmExportService.createStyles(wb);
		GermplasmList germplasmList = this.input.getGermplasmList();
		Map<String, Boolean> visibleColumnMap = this.input.getVisibleColumnMap();

		Map<Integer, Term> columnTerms = this.input.getColumnTermMap();
		Map<Integer, Variable> inventoryVariables = this.input.getInventoryVariableMap();
		Map<Integer, Variable> variateVariables = this.input.getVariateVariableMap();

		// to test
		try {
			this.germplasmExportService.generateDescriptionSheet(wb, sheetStyles, this.input);
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
		Assert.assertEquals(this.getLong(Integer.valueOf(row.getCell(1).getStringCellValue())), germplasmList.getDate());

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
		Assert.assertEquals("Expecting " + Integer.valueOf(row.getCell(6).getStringCellValue()) + " equals to " + germplasmList.getUserId()
				+ " but didn't.", Integer.valueOf(row.getCell(6).getStringCellValue()), germplasmList.getUserId());

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
				"Expecting " + Integer.valueOf(row.getCell(6).getStringCellValue()) + " equals to "
						+ this.input.getCurrentLocalIbdbUserId() + "R but didn't.", Integer.valueOf(row.getCell(6).getStringCellValue()),
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
		Assert.assertEquals("Expecting " + row.getCell(6).getStringCellValue() + " equals to blank but didn't.", row.getCell(6)
				.getStringCellValue(), "");

		int rowIndex = 11;

		for (Entry<String, Boolean> entry : visibleColumnMap.entrySet()) {

			if (entry.getValue()) {
				Term term = columnTerms.get(Integer.valueOf(entry.getKey()));

				row = descriptionSheet.getRow(++rowIndex);
				Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue(), row.getCell(0).getStringCellValue(),
						term.getName().toUpperCase());
				Assert.assertEquals("Expecting " + row.getCell(1).getStringCellValue(), row.getCell(1).getStringCellValue(),
						term.getDefinition());
			}

		}

		rowIndex = rowIndex + 2;

		for (Variable stdVariable : inventoryVariables.values()) {

			row = descriptionSheet.getRow(++rowIndex);
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue(), row.getCell(0).getStringCellValue(), stdVariable
					.getName().toUpperCase());
			Assert.assertEquals("Expecting " + row.getCell(1).getStringCellValue(), row.getCell(1).getStringCellValue(),
					stdVariable.getDefinition());
			Assert.assertEquals("Expecting " + row.getCell(2).getStringCellValue(), row.getCell(2).getStringCellValue(), stdVariable
					.getProperty().getName().toUpperCase());
			Assert.assertEquals("Expecting " + row.getCell(3).getStringCellValue(), row.getCell(3).getStringCellValue(), stdVariable
					.getScale().getName().toUpperCase());
			Assert.assertEquals("Expecting " + row.getCell(4).getStringCellValue(), row.getCell(4).getStringCellValue(), stdVariable
					.getMethod().getName().toUpperCase());
			Assert.assertEquals("Expecting " + row.getCell(5).getStringCellValue(), row.getCell(5).getStringCellValue(), stdVariable
					.getScale().getDataType().getName().substring(0, 1).toUpperCase());
			Assert.assertEquals("Expecting " + row.getCell(6).getStringCellValue(), row.getCell(6).getStringCellValue(), "");

		}

		rowIndex = rowIndex + 2;

		for (Variable stdVariable : variateVariables.values()) {

			row = descriptionSheet.getRow(++rowIndex);
			Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue(), row.getCell(0).getStringCellValue(), stdVariable
					.getName().toUpperCase());
			Assert.assertEquals("Expecting " + row.getCell(1).getStringCellValue(), row.getCell(1).getStringCellValue(),
					stdVariable.getDefinition());
			Assert.assertEquals("Expecting " + row.getCell(2).getStringCellValue(), row.getCell(2).getStringCellValue(), stdVariable
					.getProperty().getName().toUpperCase());
			Assert.assertEquals("Expecting " + row.getCell(3).getStringCellValue(), row.getCell(3).getStringCellValue(), stdVariable
					.getScale().getName().toUpperCase());
			Assert.assertEquals("Expecting " + row.getCell(4).getStringCellValue(), row.getCell(4).getStringCellValue(), stdVariable
					.getMethod().getName().toUpperCase());
			Assert.assertEquals("Expecting " + row.getCell(5).getStringCellValue(), row.getCell(5).getStringCellValue(), stdVariable
					.getScale().getDataType().getName().substring(0, 1).toUpperCase());
			Assert.assertEquals("Expecting " + row.getCell(6).getStringCellValue(), row.getCell(6).getStringCellValue(), "");

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
			this.germplasmExportService.generateGermplasmListExcelFile(this.input);
		} catch (GermplasmListExporterException e) {
			Assert.fail(e.getMessage());
		}
	}

	private GermplasmListExportInputValues generateGermplasmListExportInputValues() {
		GermplasmListExportInputValues input = new GermplasmListExportInputValues();

		input.setFileName(this.testFileName);
		input.setGermplasmList(this.germplasmList);
		input.setOwnerName(GermplasmExportServiceImplTest.CURRENT_USER_NAME);
		input.setCurrentLocalIbdbUserId(GermplasmExportServiceImplTest.CURRENT_USER_ID);
		input.setExporterName(GermplasmExportServiceImplTest.CURRENT_USER_NAME);
		input.setVisibleColumnMap(this.getVisibleColumnMap());
		input.setColumnTermMap(this.getColumnTerms());
		input.setInventoryVariableMap(this.getInventoryVariables());
		input.setVariateVariableMap(this.getVariateVariables());
		input.setListData(this.generateListEntries());
		return input;
	}

	private Map<Integer, Variable> getVariateVariables() {
		Map<Integer, Variable> standardVariableMap = new LinkedHashMap<>();

		standardVariableMap.put(TermId.NOTES.getId(),
				this.createVariable(TermId.NOTES.getId(), "NOTES", "Field notes - observed (text)", "Comment", "Text", "Observed",
						DataType.CHARACTER_VARIABLE));

		return standardVariableMap;
	}

	private Map<Integer, Variable> getInventoryVariables() {

		Map<Integer, Variable> standardVariableMap = new LinkedHashMap<>();

		standardVariableMap.put(TermId.STOCKID.getId(),
				this.createVariable(TermId.STOCKID.getId(), "stockID", "ID of an inventory deposit", "Germplasm stock id", "DBCV",
						"Assigned", DataType.CHARACTER_VARIABLE));
		standardVariableMap.put(TermId.SEED_AMOUNT_G.getId(),
				this.createVariable(TermId.SEED_AMOUNT_G.getId(), "SEED_AMOUNT_g", "Seed inventory amount deposited or withdrawn (g)",
						"Inventory amount", "g", "Weighed", DataType.CHARACTER_VARIABLE));

		return standardVariableMap;
	}

	private Map<Integer, Term> getColumnTerms() {

		Map<Integer, Term> termMap = new LinkedHashMap<>();

		termMap.put(TermId.ENTRY_NO.getId(),
				this.createVariable(TermId.ENTRY_NO.getId(), "ENTRY_NO", "Germplasm entry - enumerated (number)", "Germplasm entry",
						"Number", "Enumerated", DataType.NUMERIC_VARIABLE));
		termMap.put(TermId.GID.getId(),
				this.createVariable(TermId.GID.getId(), "GID", "Germplasm identifier - assigned (DBID)", "Germplasm id", "DBID", "Assigned",
						DataType.NUMERIC_VARIABLE));
		termMap.put(TermId.CROSS.getId(),
				this.createVariable(TermId.CROSS.getId(), "CROSS", "The pedigree string of the germplasm", "Cross history", "Text",
						"Assigned", DataType.CHARACTER_VARIABLE));
		termMap.put(TermId.ENTRY_CODE.getId(),
				this.createVariable(TermId.ENTRY_CODE.getId(), "ENTRY_CODE", "Germplasm ID - Assigned (Code)", "Germplasm entry", "Code",
						"Assigned", DataType.CHARACTER_VARIABLE));
		termMap.put(TermId.DESIG.getId(),
				this.createVariable(TermId.DESIG.getId(), "DESIGNATION", "Germplasm identifier - assigned (DBCV)", "Germplasm id", "DBCV",
						"Assigned", DataType.CHARACTER_VARIABLE));
		termMap.put(TermId.SEED_SOURCE.getId(),
				this.createVariable(TermId.SEED_SOURCE.getId(), "SEED_SOURCE", "Seed source - Selected (Code)", "Seed source", "Code",
						"Selected", DataType.CHARACTER_VARIABLE));

		return termMap;
	}

	private Variable createVariable(int termId, String name, String description, String property, String scale, String method,
			DataType dataType) {
		Variable stdvariable = new Variable();
		stdvariable.setId(termId);
		stdvariable.setName(name);
		stdvariable.setDefinition(description);
		stdvariable.setProperty(new Property(new Term(0, property, "")));
		stdvariable.setScale(new Scale(new Term(0, scale, "")));
		stdvariable.setMethod(new Method(new Term(0, method, "")));
		stdvariable.getScale().setDataType(dataType);
		return stdvariable;
	}

	private Map<String, Boolean> getVisibleColumnMap() {
		Map<String, Boolean> visibleColumnMap = new LinkedHashMap<String, Boolean>();

		visibleColumnMap.put(String.valueOf(ColumnLabels.ENTRY_ID.getTermId().getId()), true);
		visibleColumnMap.put(String.valueOf(ColumnLabels.GID.getTermId().getId()), true);
		visibleColumnMap.put(String.valueOf(ColumnLabels.ENTRY_CODE.getTermId().getId()), true);
		visibleColumnMap.put(String.valueOf(ColumnLabels.DESIGNATION.getTermId().getId()), true);
		visibleColumnMap.put(String.valueOf(ColumnLabels.PARENTAGE.getTermId().getId()), true);
		visibleColumnMap.put(String.valueOf(ColumnLabels.SEED_SOURCE.getTermId().getId()), true);

		return visibleColumnMap;
	}

	private GermplasmList generateGermplasmList() {
		GermplasmList germplasmList = new GermplasmList();
		germplasmList.setName("Sample List");
		germplasmList.setUserId(GermplasmExportServiceImplTest.USER_ID);
		germplasmList.setDescription("Sample description");
		germplasmList.setType("LST");
		germplasmList.setDate(20141112L);
		germplasmList.setNotes("Sample Notes");
		germplasmList.setListData(this.generateListEntries());

		return germplasmList;
	}

	private List<GermplasmListData> generateListEntries() {
		List<GermplasmListData> entries = new ArrayList<>();

		for (int x = 1; x <= GermplasmExportServiceImplTest.NO_OF_LIST_ENTRIES; x++) {
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

	private HSSFWorkbook createWorkbook() {
		HSSFWorkbook wb = new HSSFWorkbook();
		wb.createSheet("Codes");
		return wb;
	}

}
