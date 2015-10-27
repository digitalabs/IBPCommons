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
public class ListTypeRowGeneratorTest {
	private static final String GENERIC_LIST = "Generic List";
		private static final String LST = "LST";
		private HSSFWorkbook wb = new HSSFWorkbook();
	private HSSFSheet codesSheet = wb.createSheet("Codes");
	private ExcelCellStyleBuilder sheetStyles = new ExcelCellStyleBuilder(wb);
	
	@Mock
	GermplasmDataManager germplasmDataManager;
	
	@InjectMocks
	ListTypeRowGenerator listTypeRowGenerator;
			
	@Before
	public void setUp(){
		Mockito.when(germplasmDataManager.getUserDefinedFieldByFieldTableNameAndType(Matchers.anyString(), Matchers.anyString())).thenReturn(UserDefinedFieldDataInitializer.createUserDefinedFieldList());
	}
	
	@Test
	public void testAddListTypeRowsToCodesSheet(){
		this.listTypeRowGenerator.addRowsToCodesSheet(codesSheet, sheetStyles);
		HSSFRow row = codesSheet.getRow(1);
		Assert.assertEquals("First cell's content should be " + RowColumnType.LIST_TYPE.getSection(), RowColumnType.LIST_TYPE.getSection(), row.getCell(0).toString());
		Assert.assertEquals("Second cell's content should be " + RowColumnType.LIST_TYPE.toString(), RowColumnType.LIST_TYPE.toString(), row.getCell(1).toString());
		Assert.assertEquals("Third cell's content should be LST", LST, row.getCell(2).toString());
		Assert.assertEquals("Fourth cell's content should be Generic List", GENERIC_LIST, row.getCell(3).toString());
	}
}
