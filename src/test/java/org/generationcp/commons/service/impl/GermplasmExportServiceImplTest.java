
package org.generationcp.commons.service.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.generationcp.commons.exceptions.GermplasmListExporterException;
import org.generationcp.commons.parsing.GermplasmExportTestHelper;
import org.generationcp.commons.parsing.GermplasmExportedWorkbook;
import org.generationcp.commons.pojo.ExportColumnHeader;
import org.generationcp.commons.pojo.ExportColumnValue;
import org.generationcp.commons.pojo.GermplasmListExportInputValues;
import org.generationcp.commons.service.FileService;
import org.generationcp.commons.util.StringUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import au.com.bytecode.opencsv.CSVReader;
import junit.framework.Assert;

public class GermplasmExportServiceImplTest {

	@Mock
	private FileService fileService;

	@InjectMocks
	private final GermplasmExportServiceImpl germplasmExportService  = new GermplasmExportServiceImpl(Mockito.mock(GermplasmExportedWorkbook.class));

	private List<ExportColumnHeader> columnsHeaders;
	private List<Map<Integer, ExportColumnValue>> columnValues;
	private String sheetName;
	private GermplasmListExportInputValues input;

	@Before
	public void setUp() throws InvalidFormatException, IOException {
		MockitoAnnotations.initMocks(this);

		this.columnsHeaders = this.generateSampleExportColumnHeader(14);
		this.columnValues = this.generateSampleExportColumns(10, 14);
		this.sheetName = "List";

		this.input = GermplasmExportTestHelper.generateGermplasmListExportInputValues();

		this.germplasmExportService.setTemplateFile(GermplasmExportTestHelper.TEST_FILE_NAME);
		Mockito.doReturn(GermplasmExportTestHelper.createWorkbook()).when(this.fileService)
				.retrieveWorkbookTemplate(GermplasmExportTestHelper.TEST_FILE_NAME);
	}

	@After
	public void tearDown() {
		final File file = new File(GermplasmExportTestHelper.TEST_FILE_NAME);
		file.deleteOnExit();
	}

	@Test
	public void testGenerateCSVFile() throws IOException {

		final File generatedFile = this.germplasmExportService.generateCSVFile(this.columnValues, this.columnsHeaders,
				GermplasmExportTestHelper.TEST_FILE_NAME);

		final CSVReader reader = new CSVReader(new FileReader(generatedFile), ',');

		int index = 0;
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			// Verifying the read data here
			final String[] actualData;
			if (index == 0) {
				// get the columns
				actualData = this.germplasmExportService.getColumnHeaderNames(this.columnsHeaders);
			} else {
				// the actual data
				actualData = this.germplasmExportService.getColumnValues(this.columnValues.get(index - 1), this.columnsHeaders);
			}
			Assert.assertEquals("Should have the same value in the file and the java representation of the string arrays",
					Arrays.toString(actualData), Arrays.toString(nextLine));
			index++;
		}
		reader.close();
	}

	@Test
	public void testGenerateCSVFileWithHeader() throws IOException {

		final File generatedFile = this.germplasmExportService.generateCSVFile(this.columnValues, this.columnsHeaders,
				GermplasmExportTestHelper.TEST_FILE_NAME, true);

		final CSVReader reader = new CSVReader(new FileReader(generatedFile), ',');

		int index = 0;
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			// Verifying the read data here
			final String[] actualData;
			if (index == 0) {
				// get the columns
				actualData = this.germplasmExportService.getColumnHeaderNames(this.columnsHeaders);
			} else {
				// the actual data
				actualData = this.germplasmExportService.getColumnValues(this.columnValues.get(index - 1), this.columnsHeaders);
			}
			Assert.assertEquals("Should have the same value in the file and the java representation of the string arrays",
					Arrays.toString(actualData), Arrays.toString(nextLine));
			index++;
		}
		reader.close();
	}

	@Test
	public void testGenerateCSVFileWithoutHeader() throws IOException {

		final File generatedFile = this.germplasmExportService.generateCSVFile(this.columnValues, this.columnsHeaders,
				GermplasmExportTestHelper.TEST_FILE_NAME, false);

		final CSVReader reader = new CSVReader(new FileReader(generatedFile), ',');

		int index = 0;
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			// Verifying the read data here
			final String[] actualData;
			// we don't inclde the header in the checking
			// the actual data
			actualData = this.germplasmExportService.getColumnValues(this.columnValues.get(index), this.columnsHeaders);

			Assert.assertEquals("Should have the same value in the file and the java representation of the string arrays",
					Arrays.toString(actualData), Arrays.toString(nextLine));
			index++;
		}
		reader.close();
	}

	@Test
	public void testGetColumnValues() {
		String actualData[];
		for (final Map<Integer, ExportColumnValue> columnValue : this.columnValues) {
			actualData = this.germplasmExportService.getColumnValues(columnValue, this.columnsHeaders);
			Assert.assertEquals("Should have the same size of column values", actualData.length, this.columnsHeaders.size());

		}
	}

	@Test
	public void testGetColumnHeaderNames() {
		final String[] actualData = this.germplasmExportService.getColumnHeaderNames(this.columnsHeaders);
		Assert.assertEquals("Should have the same size of column names", actualData.length, this.columnsHeaders.size());
	}

	private List<Map<Integer, ExportColumnValue>> generateSampleExportColumns(final int rows, final int columnHeaders) {
		final List<Map<Integer, ExportColumnValue>> exportColumnValues = new ArrayList<>();
		for (int i = 0; i < rows; i++) {
			final Map<Integer, ExportColumnValue> mapData = new HashMap<>();
			for (int x = 0; x < columnHeaders; x++) {
				final Integer id = x;
				mapData.put(id, new ExportColumnValue(id, i + ": , Value -" + x));
			}
			exportColumnValues.add(mapData);
		}
		return exportColumnValues;
	}

	private List<ExportColumnHeader> generateSampleExportColumnHeader(final int columnHeaders) {
		final List<ExportColumnHeader> exportColumnHeaders = new ArrayList<>();
		for (int x = 0; x < columnHeaders; x++) {
			final Integer id = x;
			final boolean isDisplay = true;

			exportColumnHeaders.add(new ExportColumnHeader(id, "Column Name -" + x, isDisplay));
		}
		return exportColumnHeaders;
	}

	@Test
	public void testCreateWorkbookForSingleSheet() {
		final HSSFWorkbook wb = this.germplasmExportService.createWorkbookForSingleSheet(this.columnValues, this.columnsHeaders, this.sheetName);
		final HSSFSheet sheet = wb.getSheetAt(0);

		Assert.assertTrue("Expected to return a sheetName = " + this.sheetName, sheet.getSheetName().equalsIgnoreCase(this.sheetName));

		final HSSFRow header = sheet.getRow(0);
		for (int i = 0; i < this.columnsHeaders.size(); i++) {
			Assert.assertTrue("Expected that the column headers are placed in order.", this.columnsHeaders.get(i).getName()
					.equalsIgnoreCase(header.getCell(i).getStringCellValue()));
		}

		Assert.assertTrue("Expected to have a total of " + this.columnValues.size() + " rows excluding the columnHeader.",
				sheet.getLastRowNum() == this.columnValues.size());

		int rowCount = 1;
		for (final Map<Integer, ExportColumnValue> rowEntry : this.columnValues) {
			final HSSFRow row = sheet.getRow(rowCount);

			for (final Map.Entry<Integer, ExportColumnValue> rowValue : rowEntry.entrySet()) {
				final Integer id = rowValue.getKey();
				final String value = rowValue.getValue().getValue();
				Assert.assertEquals("Expected that the row values corresponds to their respective columns.", value, row.getCell(id)
						.getStringCellValue());
			}

			rowCount++;
		}

	}

	@Test
	public void testGenerateExcelFileForSingleSheet() throws IOException {
		this.germplasmExportService.generateExcelFileForSingleSheet(this.columnValues, this.columnsHeaders,
				GermplasmExportTestHelper.TEST_FILE_NAME, this.sheetName);
	}

	@Test
	public void testGenerateGermplasmListExcelFile() throws GermplasmListExporterException {
		this.germplasmExportService.generateGermplasmListExcelFile(this.input);
	}

}
