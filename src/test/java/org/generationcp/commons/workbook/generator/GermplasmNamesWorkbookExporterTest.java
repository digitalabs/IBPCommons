package org.generationcp.commons.workbook.generator;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.commons.parsing.GermplasmExportTestHelper;
import org.generationcp.middleware.data.initializer.UserDefinedFieldTestDataInitializer;
import org.generationcp.middleware.domain.gms.GermplasmListNewColumnsInfo;
import org.generationcp.middleware.interfaces.GermplasmExportSource;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class GermplasmNamesWorkbookExporterTest {
	private static final String CODE1 = "CODE1";
	private ExcelCellStyleBuilder styleBuilder;
	private HSSFWorkbook wb;

	@Mock
	private GermplasmListManager germplasmListManager;

	@Mock
	private GermplasmListNewColumnsInfo columnsInfo;

	@InjectMocks
	private GermplasmNamesWorkbookExporter namesWorkbookExporter;

	@Before
	public void setUp() {
		this.wb = new HSSFWorkbook();
		this.styleBuilder = new ExcelCellStyleBuilder(this.wb);
		this.namesWorkbookExporter.setSheetStyles(this.styleBuilder);
		this.namesWorkbookExporter.setColumnsInfo(this.columnsInfo);
		Mockito.when(this.germplasmListManager.getGermplasmNameTypes()).thenReturn(Collections.singletonList(UserDefinedFieldTestDataInitializer.createUserDefinedField(GermplasmNamesWorkbookExporterTest.CODE1, GermplasmNamesWorkbookExporterTest.CODE1)));
		Mockito.when(this.columnsInfo.getColumns()).thenReturn(Collections.singletonList(GermplasmNamesWorkbookExporterTest.CODE1));
	}
	@Test
	public void testGetSourceItems() {
		final List<UserDefinedField> nameTypesColumns = this.namesWorkbookExporter.getSourceItems();
		Assert.assertEquals(nameTypesColumns.get(0).getFcode(), GermplasmNamesWorkbookExporterTest.CODE1);
		Assert.assertEquals(nameTypesColumns.get(0).getFname(), GermplasmNamesWorkbookExporterTest.CODE1);
	}

	@Test
	public void testGenerateAddedColumnHeader() {
		this.namesWorkbookExporter.setColumnsInfo(GermplasmExportTestHelper.generateAddedColumnsInfo());
		final HSSFSheet observationSheet = this.wb.createSheet("Observation");
		final HSSFRow headerRow = observationSheet.createRow(0);
		this.namesWorkbookExporter.generateAddedColumnHeader(headerRow, 0);
		Assert.assertEquals(GermplasmNamesWorkbookExporterTest.CODE1, headerRow.getCell(0).getStringCellValue());
		Assert.assertEquals(this.styleBuilder.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.HEADING_STYLE_FACTOR), headerRow.getCell(0).getCellStyle());
	}

	@Test
	public void testGenerateAddedColumnValue() {
		this.namesWorkbookExporter.setColumnsInfo(GermplasmExportTestHelper.generateAddedColumnsInfo());
		this.namesWorkbookExporter.setAddedNameTypesColumns(Collections.singletonList(GermplasmNamesWorkbookExporterTest.CODE1));
		final HSSFSheet observationSheet = this.wb.createSheet("Observation");
		final HSSFRow row = observationSheet.createRow(0);
		final GermplasmExportSource data = GermplasmExportTestHelper.generateListEntries(1).get(0);
		this.namesWorkbookExporter.generateAddedColumnValue(row, data,0);
		Assert.assertEquals("CODE1:1", row.getCell(0).getStringCellValue());
	}

	@Test
	public void testAddRowsToDescriptionSheet() {
		final HSSFSheet descriptionSheet = this.wb.createSheet("Description");
		this.namesWorkbookExporter.addRowsToDescriptionSheet(descriptionSheet, 0, this.styleBuilder, this.columnsInfo);
		final HSSFRow nameRow = descriptionSheet.getRow(1);

		final UserDefinedField name =  this.namesWorkbookExporter.getSourceItems().get(0);
		Assert.assertEquals(this.namesWorkbookExporter.getName(name), nameRow.getCell(0).getStringCellValue());
		Assert.assertEquals(this.namesWorkbookExporter.getDescription(name), nameRow.getCell(1).getStringCellValue());
		Assert.assertEquals(this.namesWorkbookExporter.getProperty(name), nameRow.getCell(2).getStringCellValue());
		Assert.assertEquals(this.namesWorkbookExporter.getScale(name), nameRow.getCell(3).getStringCellValue());
		Assert.assertEquals(this.namesWorkbookExporter.getMethod(name), nameRow.getCell(4).getStringCellValue());
		Assert.assertEquals(this.namesWorkbookExporter.getDatatype(name), nameRow.getCell(5).getStringCellValue());
		Assert.assertEquals(this.namesWorkbookExporter.getValue(name), nameRow.getCell(6).getStringCellValue());
		Assert.assertEquals(this.namesWorkbookExporter.getComments(name), nameRow.getCell(7).getStringCellValue());
	}
}
