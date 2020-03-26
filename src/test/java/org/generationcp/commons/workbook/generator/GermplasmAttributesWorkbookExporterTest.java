package org.generationcp.commons.workbook.generator;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.generationcp.commons.data.initializer.UserDefinedFieldTestDataInitializer;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.commons.parsing.GermplasmExportTestHelper;
import org.generationcp.middleware.domain.gms.GermplasmListNewColumnsInfo;
import org.generationcp.middleware.interfaces.GermplasmExportSource;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class GermplasmAttributesWorkbookExporterTest {

	private static final String NOTE = "NOTE";
	private static final String NOTES = "NOTES";
	private ExcelCellStyleBuilder styleBuilder;
	private HSSFWorkbook wb;
	@Mock
	private GermplasmDataManager germplasmManager;

	@Mock
	private GermplasmListNewColumnsInfo columnsInfo;

	@InjectMocks
	private GermplasmAttributesWorkbookExporter attributesWorkbookExporter;

	@Before
	public void setUp() {
		this.wb = new HSSFWorkbook();
		this.styleBuilder = new ExcelCellStyleBuilder(this.wb);
		this.attributesWorkbookExporter.setSheetStyles(this.styleBuilder);
		this.attributesWorkbookExporter.setColumnsInfo(this.columnsInfo);
		Mockito.when(this.germplasmManager.getAllAttributesTypes()).thenReturn(Collections.singletonList(UserDefinedFieldTestDataInitializer.createUserDefinedField(
				GermplasmAttributesWorkbookExporterTest.NOTE, GermplasmAttributesWorkbookExporterTest.NOTES)));
		Mockito.when(this.columnsInfo.getColumns()).thenReturn(Collections.singletonList(GermplasmAttributesWorkbookExporterTest.NOTE));
	}
	@Test
	public void testGetSourceItems() {
		final List<UserDefinedField> attributes =  this.attributesWorkbookExporter.getSourceItems();
		Assert.assertFalse(attributes.isEmpty());
		final UserDefinedField attribute = attributes.get(0);
		Assert.assertEquals(GermplasmAttributesWorkbookExporterTest.NOTES, attribute.getFname());
		Assert.assertEquals(GermplasmAttributesWorkbookExporterTest.NOTE, attribute.getFcode());
	}

	@Test
	public void testGenerateAddedColumnHeader() {
		Mockito.when(this.germplasmManager.getAllAttributesTypes()).thenReturn(Collections.singletonList(UserDefinedFieldTestDataInitializer.createUserDefinedField(
				GermplasmAttributesWorkbookExporterTest.NOTES, GermplasmAttributesWorkbookExporterTest.NOTES)));
		this.attributesWorkbookExporter.setColumnsInfo(GermplasmExportTestHelper.generateAddedColumnsInfo());
		final HSSFSheet observationSheet = this.wb.createSheet("Observation");
		final HSSFRow headerRow = observationSheet.createRow(0);
		this.attributesWorkbookExporter.generateAddedColumnHeader(headerRow, 0);
		Assert.assertEquals(GermplasmAttributesWorkbookExporterTest.NOTES, headerRow.getCell(0).getStringCellValue());
		Assert.assertEquals(this.styleBuilder.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.HEADING_STYLE_FACTOR), headerRow.getCell(0).getCellStyle());
	}

	@Test
	public void testGenerateAddedColumnValue() {
		this.attributesWorkbookExporter.setColumnsInfo(GermplasmExportTestHelper.generateAddedColumnsInfo());
		this.attributesWorkbookExporter.setAddedAttributeColumns(Collections.singletonList(GermplasmAttributesWorkbookExporterTest.NOTES));
		final HSSFSheet observationSheet = this.wb.createSheet("Observation");
		final HSSFRow row = observationSheet.createRow(0);
		final GermplasmExportSource data = GermplasmExportTestHelper.generateListEntries(1).get(0);
		this.attributesWorkbookExporter.generateAddedColumnValue(row, data,0);
		Assert.assertEquals("NOTES:1", row.getCell(0).getStringCellValue());
	}

	@Test
	public void testAddRowsToDescriptionSheet() {
		final HSSFSheet descriptionSheet = this.wb.createSheet("Description");
		this.attributesWorkbookExporter.addRowsToDescriptionSheet(descriptionSheet, 0, this.styleBuilder, this.columnsInfo);
		final HSSFRow attributeRow = descriptionSheet.getRow(1);

		final UserDefinedField attribute =  this.attributesWorkbookExporter.getSourceItems().get(0);
		Assert.assertEquals(this.attributesWorkbookExporter.getName(attribute), attributeRow.getCell(0).getStringCellValue());
		Assert.assertEquals(this.attributesWorkbookExporter.getDescription(attribute), attributeRow.getCell(1).getStringCellValue());
		Assert.assertEquals(this.attributesWorkbookExporter.getProperty(attribute), attributeRow.getCell(2).getStringCellValue());
		Assert.assertEquals(this.attributesWorkbookExporter.getScale(attribute), attributeRow.getCell(3).getStringCellValue());
		Assert.assertEquals(this.attributesWorkbookExporter.getMethod(attribute), attributeRow.getCell(4).getStringCellValue());
		Assert.assertEquals(this.attributesWorkbookExporter.getDatatype(attribute), attributeRow.getCell(5).getStringCellValue());
		Assert.assertEquals(this.attributesWorkbookExporter.getValue(attribute), attributeRow.getCell(6).getStringCellValue());
		Assert.assertEquals(this.attributesWorkbookExporter.getComments(attribute), attributeRow.getCell(7).getStringCellValue());
	}
}
