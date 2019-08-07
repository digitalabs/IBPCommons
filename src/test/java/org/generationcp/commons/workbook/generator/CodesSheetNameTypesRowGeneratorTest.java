
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
import org.mockito.junit.MockitoJUnitRunner;
@RunWith(MockitoJUnitRunner.class)
public class CodesSheetNameTypesRowGeneratorTest {

	private static final String CROSS_NAME = "CROSS NAME";
	private static final String CRSNM = "CRSNM";
	private static final String CROP_NAME = "maize";

	private final HSSFWorkbook wb = new HSSFWorkbook();
	private final HSSFSheet codesSheet = this.wb.createSheet("Codes");
	private final ExcelCellStyleBuilder sheetStyles = new ExcelCellStyleBuilder(this.wb);

	@Mock
	GermplasmDataManager germplasmDataManager;

	@InjectMocks
	CodesSheetNameTypesRowGenerator nameTypesRowGenerator;

	@Before
	public void setUp() {
		Mockito.when(this.germplasmDataManager.getUserDefinedFieldByFieldTableNameAndType(Matchers.anyString(), Matchers.anyString()))
				.thenReturn(UserDefinedFieldTestDataInitializer.createUserDefinedFieldList(CodesSheetNameTypesRowGeneratorTest.CRSNM,
						CodesSheetNameTypesRowGeneratorTest.CROSS_NAME));
	}

	@Test
	public void testNameTypesRowsToCodesSheet() {
		this.nameTypesRowGenerator.addRowsToCodesSheet(this.codesSheet, this.sheetStyles);
		final HSSFRow row = this.codesSheet.getRow(1);
		Assert.assertEquals("First cell's content should be " + RowColumnType.NAME_TYPES.getSection(),
				RowColumnType.NAME_TYPES.getSection(), row.getCell(0).toString());
		Assert.assertEquals("Second cell's content should be " + RowColumnType.NAME_TYPES.toString(), RowColumnType.NAME_TYPES.toString(),
				row.getCell(1).toString());
		Assert.assertEquals("Third cell's content should be CRSNM", CodesSheetNameTypesRowGeneratorTest.CRSNM, row.getCell(2).toString());
		Assert.assertEquals("Fourth cell's content should be CROSS NAME", CodesSheetNameTypesRowGeneratorTest.CROSS_NAME, row.getCell(3).toString());
	}
}
