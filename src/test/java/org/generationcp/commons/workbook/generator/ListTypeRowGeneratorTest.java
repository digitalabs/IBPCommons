
package org.generationcp.commons.workbook.generator;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.generationcp.commons.data.initializer.UserDefinedFieldTestDataInitializer;
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
	private final HSSFWorkbook wb = new HSSFWorkbook();
	private final HSSFSheet codesSheet = this.wb.createSheet("Codes");
	private final ExcelCellStyleBuilder sheetStyles = new ExcelCellStyleBuilder(this.wb);

	@Mock
	GermplasmDataManager germplasmDataManager;

	@InjectMocks
	ListTypeRowGenerator listTypeRowGenerator;

	@Before
	public void setUp() {
		Mockito.when(this.germplasmDataManager.getUserDefinedFieldByFieldTableNameAndType(Matchers.anyString(), Matchers.anyString()))
				.thenReturn(UserDefinedFieldTestDataInitializer.createUserDefinedFieldList());
	}

	@Test
	public void testAddListTypeRowsToCodesSheet() {
		this.listTypeRowGenerator.addRowsToCodesSheet(this.codesSheet, this.sheetStyles);
		final HSSFRow row = this.codesSheet.getRow(1);
		Assert.assertEquals("First cell's content should be " + RowColumnType.LIST_TYPE.getSection(), RowColumnType.LIST_TYPE.getSection(),
				row.getCell(0).toString());
		Assert.assertEquals("Second cell's content should be " + RowColumnType.LIST_TYPE.toString(), RowColumnType.LIST_TYPE.toString(),
				row.getCell(1).toString());
		Assert.assertEquals("Third cell's content should be LST", ListTypeRowGeneratorTest.LST, row.getCell(2).toString());
		Assert.assertEquals("Fourth cell's content should be Generic List", ListTypeRowGeneratorTest.GENERIC_LIST,
				row.getCell(3).toString());
	}
}
