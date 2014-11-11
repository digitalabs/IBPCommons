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

import org.generationcp.commons.pojo.ExportColumnHeader;
import org.generationcp.commons.pojo.ExportColumnValue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import au.com.bytecode.opencsv.CSVReader;

public class ExportServiceImplTest {
	private ExportServiceImpl exportService;
	private List<ExportColumnHeader> columnsHeaders;
	private List<Map<Integer, ExportColumnValue>> columnValues;
	private String testFileName;

	@Before
	public void setUp() {
		exportService = new ExportServiceImpl();
		columnsHeaders = generateSampleExportColumnHeader(14);
		columnValues = generateSampleExportColumns(10, 14);
		testFileName = "test.csv";
	}

	@After
	public void tearDown() {
		File file = new File(testFileName);
		file.deleteOnExit();
	}

	@Test
	public void testGenerateCSVFile() throws IOException {

		File generatedFile = exportService.generateCSVFile(columnValues, columnsHeaders,
				testFileName);

		CSVReader reader = new CSVReader(new FileReader(generatedFile), ',');

		int index = 0;
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			if (nextLine != null) {
				// Verifying the read data here
				String actualData[];
				if (index == 0) {
					// get the columns
					actualData = exportService.getColumnHeaderNames(columnsHeaders);
				} else {
					// the actual data
					actualData = exportService.getColumnValues(columnValues.get(index - 1),
							columnsHeaders);
				}
				Assert.assertEquals(
						"Should have the same value in the file and the java representation of the string arrays",
						Arrays.toString(actualData), Arrays.toString(nextLine));
			}
			index++;
		}
		reader.close();
	}

	@Test
	public void testGetColumnValues() {
		String actualData[];
		for (int i = 0; i < columnValues.size(); i++) {
			actualData = exportService.getColumnValues(columnValues.get(i), columnsHeaders);
			Assert.assertEquals("Should have the same size of column values", actualData.length,
					columnsHeaders.size());

		}
	}

	@Test
	public void testGetColumnHeaderNames() {
		String actualData[];
		actualData = exportService.getColumnHeaderNames(columnsHeaders);
		Assert.assertEquals("Should have the same size of column names", actualData.length,
				columnsHeaders.size());
	}

	@Test
	public void testCleanNameValueCommasWithNoComma() {
		String param = "Test Value";
		Assert.assertEquals("Should be still the same string since there is no comma character",
				exportService.cleanNameValueCommas(param), param);
	}

	@Test
	public void testCleanNameValueCommasWithAComma() {
		String param = "Test, Value";
		String paramNew = "Test_ Value";
		Assert.assertEquals("The comma character in the string should be change to a _ character",
				exportService.cleanNameValueCommas(param), paramNew);
	}

	@Test
	public void testCleanNameValueCommasWithNullParameter() {
		String param = null;
		Assert.assertEquals("Should be empty string since param passed was null",
				exportService.cleanNameValueCommas(param), "");
	}

	private List<Map<Integer, ExportColumnValue>> generateSampleExportColumns(int rows,
			int columnHeaders) {
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
}
