package org.generationcp.commons.parsing;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.generationcp.commons.constant.ColumnLabels;
import org.generationcp.commons.exceptions.GermplasmListExporterException;
import org.generationcp.commons.pojo.GermplasmListExportInputValues;
import org.generationcp.commons.workbook.generator.CodesSheetGenerator;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.junit.Test;
import org.mockito.Mockito;

import junit.framework.Assert;

public class GermplasmExportedWorkbookTest {

	private final GermplasmListExportInputValues input =  GermplasmExportTestHelper.generateGermplasmListExportInputValues();

	@Test
	public void testGetNoOfVisibleColumns() {
		final GermplasmExportedWorkbook germplasmExportedWorkbook = new GermplasmExportedWorkbook(Mockito.mock(CodesSheetGenerator.class));
		germplasmExportedWorkbook.init(input);
		Assert.assertTrue("Expected that the number of visibleColums = " + this.input.getVisibleColumnMap().size(),
				germplasmExportedWorkbook.getNoOfVisibleColumns(this.input.getVisibleColumnMap()) == this.input.getVisibleColumnMap()
						.size());

		this.input.getVisibleColumnMap().put(ColumnLabels.SEED_SOURCE.getName(), false);
		final int visibleColumns = germplasmExportedWorkbook.getNoOfVisibleColumns(this.input.getVisibleColumnMap());
		Assert.assertTrue("Expected that the number of visibleColums = " + (this.input.getVisibleColumnMap().size() - 1),
				visibleColumns == this.input.getVisibleColumnMap().size() - 1);
	}

	@Test
	public void testGenerateObservationSheet() throws GermplasmListExporterException {
		// input data
		final HSSFWorkbook wb = GermplasmExportTestHelper.createWorkbook();
		final GermplasmList germplasmList = this.input.getGermplasmList();
		final List<GermplasmListData> listDatas = germplasmList.getListData();
		final Map<String, Boolean> visibleColumnMap = this.input.getVisibleColumnMap();
		final ExcelCellStyleBuilder styles = new ExcelCellStyleBuilder(wb);

		// to test
		final GermplasmExportedWorkbook germplasmExportedWorkbook = new GermplasmExportedWorkbook(Mockito.mock(CodesSheetGenerator.class));
		germplasmExportedWorkbook.init(input);

		final HSSFSheet observationSheet = germplasmExportedWorkbook.getWorkbook().getSheet("Observation");

		HSSFRow row;
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
		}

		// Assert Row Values
		int rowIndex = 1;
		for (final GermplasmListData listData : listDatas) {
			row = observationSheet.getRow(rowIndex);

			columnIndex = 0;
			if (visibleColumnMap.get(String.valueOf(TermId.ENTRY_NO.getId()))) {
				Assert.assertEquals("Expecting " + row.getCell(columnIndex).getNumericCellValue() + " equals to "
								+ listData.getEntryId() + " but didn't.", row.getCell(columnIndex).getNumericCellValue(),
						listData.getEntryId().doubleValue());
				Assert.assertEquals(styles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.NUMBER_COLUMN_HIGHLIGHT_STYLE_FACTOR),
						row.getCell(columnIndex).getCellStyle());
				columnIndex++;
			}
			if (visibleColumnMap.get(String.valueOf(TermId.GID.getId()))) {
				Assert.assertEquals("Expecting " + row.getCell(columnIndex).getNumericCellValue() + " equals to "
								+ listData.getEntryId() + " but didn't.", row.getCell(columnIndex).getNumericCellValue(),
						listData.getGid().doubleValue());
				Assert.assertEquals(styles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.NUMBER_DATA_FORMAT_STYLE),
						row.getCell(columnIndex).getCellStyle());
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
			}
			rowIndex++;
		}
	}

	@Test
	public void testGenerateDescriptionSheet() throws GermplasmListExporterException {

		// input data
		final HSSFWorkbook wb = new HSSFWorkbook();
		final GermplasmList germplasmList = this.input.getGermplasmList();
		final Map<String, Boolean> visibleColumnMap = this.input.getVisibleColumnMap();

		final Map<Integer, Term> columnTerms = this.input.getColumnTermMap();
		final Map<Integer, Variable> inventoryVariables = this.input.getInventoryVariableMap();
		final Map<Integer, Variable> variateVariables = this.input.getVariateVariableMap();

		// to test
		final GermplasmExportedWorkbook germplasmExportedWorkbook = new GermplasmExportedWorkbook(Mockito.mock(CodesSheetGenerator.class));
		germplasmExportedWorkbook.init(input);

		final HSSFSheet descriptionSheet = germplasmExportedWorkbook.getWorkbook().getSheet("Description");

		Assert.assertNotNull("Expected to successfully generated the description sheet.", descriptionSheet);
		Assert.assertTrue("The sheet name is Description.", "Description".equalsIgnoreCase(descriptionSheet.getSheetName()));

		HSSFRow row;
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
		Assert.assertEquals(row.getCell(1).getStringCellValue(), germplasmList.getDate().toString());

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
		Assert.assertEquals("Expecting " + row.getCell(5).getStringCellValue() + " equals to N but didn't.",
				row.getCell(5).getStringCellValue(), "N");
		Assert.assertTrue("Expecting " + row.getCell(6).getNumericCellValue() + " equals to " + germplasmList.getUserId()
				+ " but didn't.", row.getCell(6).getNumericCellValue() == germplasmList.getUserId());

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
		Assert.assertEquals("Expecting " + row.getCell(5).getStringCellValue() + " equals to N but didn't.",
				row.getCell(5).getStringCellValue(), "N");
		Assert.assertTrue("Expecting " + row.getCell(6).getNumericCellValue() + " equals to " + this.input.getCurrentLocalIbdbUserId()
				+ "R but didn't.", row.getCell(6).getNumericCellValue() == this.input.getCurrentLocalIbdbUserId());

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

		for (final Map.Entry<String, Boolean> entry : visibleColumnMap.entrySet()) {

			if (entry.getValue()) {
				final Term term = columnTerms.get(Integer.valueOf(entry.getKey()));

				row = descriptionSheet.getRow(++rowIndex);
				Assert.assertEquals("Expecting " + row.getCell(0).getStringCellValue(), row.getCell(0).getStringCellValue(),
						term.getName().toUpperCase());
				Assert.assertEquals("Expecting " + row.getCell(1).getStringCellValue(), row.getCell(1).getStringCellValue(),
						term.getDefinition());
			}

		}

		rowIndex = rowIndex + 2;

		for (final Variable stdVariable : inventoryVariables.values()) {

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

		for (final Variable stdVariable : variateVariables.values()) {

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

}