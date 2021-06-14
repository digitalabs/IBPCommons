package org.generationcp.commons.workbook.generator;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.commons.parsing.GermplasmExportTestHelper;
import org.generationcp.middleware.domain.gms.GermplasmListNewColumnsInfo;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.interfaces.GermplasmExportSource;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
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
public class GermplasmAttributesWorkbookExporterTest {

	private static final String NOTE = "NOTE";
	private static final String NOTES = "NOTES";
	private ExcelCellStyleBuilder styleBuilder;
	private HSSFWorkbook wb;

	@Mock
	private OntologyVariableDataManager ontologyVariableDataManager;

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
		Mockito.when(this.ontologyVariableDataManager.getWithFilter(Mockito.any())).thenReturn(this.getAttributeVariables());
		Mockito.when(this.columnsInfo.getColumns()).thenReturn(Collections.singletonList(GermplasmAttributesWorkbookExporterTest.NOTE));
	}

	@Test
	public void testGetSourceItems() {
		final List<Variable> attributes = this.attributesWorkbookExporter.getSourceItems();
		Assert.assertFalse(attributes.isEmpty());
		final Variable attribute = attributes.get(0);
		Assert.assertEquals(GermplasmAttributesWorkbookExporterTest.NOTES, attribute.getDefinition());
		Assert.assertEquals(GermplasmAttributesWorkbookExporterTest.NOTE, attribute.getName());
	}

	@Test
	public void testGenerateAddedColumnHeader() {
		final Variable variable1 =
			this.getVariable(1, GermplasmAttributesWorkbookExporterTest.NOTES, GermplasmAttributesWorkbookExporterTest.NOTES);

		Mockito.when(this.ontologyVariableDataManager.getWithFilter(Mockito.any())).thenReturn(Collections.singletonList(variable1));
		this.attributesWorkbookExporter.setColumnsInfo(GermplasmExportTestHelper.generateAddedColumnsInfo());
		final HSSFSheet observationSheet = this.wb.createSheet("Observation");
		final HSSFRow headerRow = observationSheet.createRow(0);
		this.attributesWorkbookExporter.generateAddedColumnHeader(headerRow, 0);
		Assert.assertEquals(GermplasmAttributesWorkbookExporterTest.NOTES, headerRow.getCell(0).getStringCellValue());
		Assert.assertEquals(this.styleBuilder.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.HEADING_STYLE_FACTOR),
			headerRow.getCell(0).getCellStyle());
	}

	@Test
	public void testGenerateAddedColumnValue() {
		this.attributesWorkbookExporter.setColumnsInfo(GermplasmExportTestHelper.generateAddedColumnsInfo());
		this.attributesWorkbookExporter.setAddedAttributeColumns(Collections.singletonList(GermplasmAttributesWorkbookExporterTest.NOTES));
		final HSSFSheet observationSheet = this.wb.createSheet("Observation");
		final HSSFRow row = observationSheet.createRow(0);
		final GermplasmExportSource data = GermplasmExportTestHelper.generateListEntries(1).get(0);
		this.attributesWorkbookExporter.generateAddedColumnValue(row, data, 0);
		Assert.assertEquals("NOTES:1", row.getCell(0).getStringCellValue());
	}

	@Test
	public void testAddRowsToDescriptionSheet() {
		final HSSFSheet descriptionSheet = this.wb.createSheet("Description");
		this.attributesWorkbookExporter.addRowsToDescriptionSheet(descriptionSheet, 0, this.styleBuilder, this.columnsInfo);
		final HSSFRow attributeRow = descriptionSheet.getRow(1);

		final Variable attribute = this.attributesWorkbookExporter.getSourceItems().get(0);
		Assert.assertEquals(this.attributesWorkbookExporter.getName(attribute), attributeRow.getCell(0).getStringCellValue());
		Assert.assertEquals(this.attributesWorkbookExporter.getDescription(attribute), attributeRow.getCell(1).getStringCellValue());
		Assert.assertEquals(this.attributesWorkbookExporter.getProperty(attribute), attributeRow.getCell(2).getStringCellValue());
		Assert.assertEquals(this.attributesWorkbookExporter.getScale(attribute), attributeRow.getCell(3).getStringCellValue());
		Assert.assertEquals(this.attributesWorkbookExporter.getMethod(attribute), attributeRow.getCell(4).getStringCellValue());
		Assert.assertEquals(this.attributesWorkbookExporter.getDatatype(attribute), attributeRow.getCell(5).getStringCellValue());
		Assert.assertEquals(this.attributesWorkbookExporter.getValue(attribute), attributeRow.getCell(6).getStringCellValue());
		Assert.assertEquals(this.attributesWorkbookExporter.getComments(attribute), attributeRow.getCell(7).getStringCellValue());
	}

	@Test
	public void testGetSourceItemsDuplicate() {
		final Variable variable1 =
			this.getVariable(1, GermplasmAttributesWorkbookExporterTest.NOTE, GermplasmAttributesWorkbookExporterTest.NOTES);
		final Variable variable2 = this.getVariable(1, GermplasmAttributesWorkbookExporterTest.NOTE, "-");
		Mockito.when(this.ontologyVariableDataManager.getWithFilter(Mockito.any())).thenReturn(Arrays.asList(variable1, variable2));
		Mockito.when(this.columnsInfo.getColumns()).thenReturn(Collections.singletonList(GermplasmAttributesWorkbookExporterTest.NOTE));
		final List<Variable> attributes = this.attributesWorkbookExporter.getSourceItems();
		Assert.assertFalse(attributes.isEmpty());
		Assert.assertEquals("Returned value is 1 ", 1, attributes.size());
		final Variable attribute = attributes.get(0);
		Assert.assertEquals(GermplasmAttributesWorkbookExporterTest.NOTES, attribute.getDefinition());
		Assert.assertEquals(GermplasmAttributesWorkbookExporterTest.NOTE, attribute.getName());
	}

	private List<Variable> getAttributeVariables() {
		final Variable variable1 =
			this.getVariable(1, GermplasmAttributesWorkbookExporterTest.NOTE, GermplasmAttributesWorkbookExporterTest.NOTES);
		return Arrays.asList(variable1);
	}

	private Variable getVariable(final int id, final String name, final String definition) {
		final Variable variable1 = new Variable();
		variable1.setId(id);
		variable1.setName(name);
		variable1.setDefinition(definition);
		variable1.setProperty(this.getProperty());
		variable1.setScale(this.getScale());
		variable1.setMethod(this.getMethod());
		return variable1;
	}

	private Property getProperty() {
		final Property property = new Property();
		property.setName(RandomStringUtils.randomAlphabetic(20));
		return property;
	}

	private Scale getScale() {
		final Scale scale = new Scale();
		scale.setName(RandomStringUtils.randomAlphabetic(20));
		scale.setDataType(DataType.NUMERIC_VARIABLE);
		return scale;
	}

	private Method getMethod() {
		final Method method = new Method();
		method.setName(RandomStringUtils.randomAlphabetic(20));
		return method;
	}
}
