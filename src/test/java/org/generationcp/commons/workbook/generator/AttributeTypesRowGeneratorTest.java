package org.generationcp.commons.workbook.generator;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.generationcp.commons.data.initializer.UserDefinedFieldDataInitializer;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AttributeTypesRowGeneratorTest {
	private static final String NOTES = "NOTES";
	private static final String NOTE = "NOTE";
	
	private HSSFWorkbook wb = new HSSFWorkbook();
	private HSSFSheet codesSheet = wb.createSheet("Codes");
	private ExcelCellStyleBuilder sheetStyles = new ExcelCellStyleBuilder(wb);
	
	@Mock
	GermplasmDataManager germplasmDataManager;
	
	@InjectMocks
	AttributeTypesRowGenerator attributeTypesRowGenerator;
			
	@Before
	public void setUp(){
		Mockito.when(germplasmDataManager.getUserDefinedFieldByFieldTableNameAndType(Matchers.anyString(), Matchers.anyString())).thenReturn(UserDefinedFieldDataInitializer.createUserDefinedFieldList(NOTE, NOTES));
	}
	
	@Test
	public void testNameTypesRowsToCodesSheet(){
		this.attributeTypesRowGenerator.addRowsToCodesSheet(codesSheet, sheetStyles);
		HSSFRow row = codesSheet.getRow(1);
		Assert.assertEquals("First cell's content should be " + RowColumnType.ATTRIBUTE_TYPES.getSection(), RowColumnType.ATTRIBUTE_TYPES.getSection(), row.getCell(0).toString());
		Assert.assertEquals("Second cell's content should be " + RowColumnType.ATTRIBUTE_TYPES.toString(), RowColumnType.ATTRIBUTE_TYPES.toString(), row.getCell(1).toString());
		Assert.assertEquals("Third cell's content should be NOTE", NOTE, row.getCell(2).toString());
		Assert.assertEquals("Fourth cell's content should be NOTES", NOTES, row.getCell(3).toString());
	}
}
