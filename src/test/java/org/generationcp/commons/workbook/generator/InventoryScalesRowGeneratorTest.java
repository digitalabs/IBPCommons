
package org.generationcp.commons.workbook.generator;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.generationcp.commons.data.initializer.ScaleTestDataInitializer;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.middleware.service.api.OntologyService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InventoryScalesRowGeneratorTest {

	private static final String FOR_KG_WEIGHED = "for kg - Weighed";
	private static final String SEED_AMOUNT_KG = "SEED_AMOUNT_kg";
	private final HSSFWorkbook wb = new HSSFWorkbook();
	private final HSSFSheet codesSheet = this.wb.createSheet("Codes");
	private final ExcelCellStyleBuilder sheetStyles = new ExcelCellStyleBuilder(this.wb);

	@Mock
	OntologyService ontologyService;

	@InjectMocks
	InventoryScalesRowGenerator inventoryScalesRowGenerator;

	@Before
	public void setUp() {
		Mockito.when(this.ontologyService.getAllInventoryScales()).thenReturn(ScaleTestDataInitializer.createScaleList());
	}

	@Test
	public void testNameTypesRowsToCodesSheet() {
		this.inventoryScalesRowGenerator.addRowsToCodesSheet(this.codesSheet, this.sheetStyles);
		final HSSFRow row = this.codesSheet.getRow(1);
		Assert.assertEquals("First cell's content should be " + RowColumnType.SCALES_FOR_INVENTORY_UNITS.getSection(),
				RowColumnType.SCALES_FOR_INVENTORY_UNITS.getSection(), row.getCell(0).toString());
		Assert.assertEquals("Second cell's content should be " + RowColumnType.SCALES_FOR_INVENTORY_UNITS.toString(),
				RowColumnType.SCALES_FOR_INVENTORY_UNITS.toString(), row.getCell(1).toString());
		Assert.assertEquals("Third cell's content should be " + InventoryScalesRowGeneratorTest.SEED_AMOUNT_KG,
				InventoryScalesRowGeneratorTest.SEED_AMOUNT_KG, row.getCell(2).toString());
		Assert.assertEquals("Fourth cell's content should be " + InventoryScalesRowGeneratorTest.FOR_KG_WEIGHED,
				InventoryScalesRowGeneratorTest.FOR_KG_WEIGHED, row.getCell(3).toString());
	}
}
