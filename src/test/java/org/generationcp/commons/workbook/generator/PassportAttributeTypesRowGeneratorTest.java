
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
public class PassportAttributeTypesRowGeneratorTest {

	private static final String TAXONOMY = "Taxonomy";
	private static final String TAXNO = "TAXNO";

	private final HSSFWorkbook wb = new HSSFWorkbook();
	private final HSSFSheet codesSheet = this.wb.createSheet("Codes");
	private final ExcelCellStyleBuilder sheetStyles = new ExcelCellStyleBuilder(this.wb);

	@Mock
	GermplasmDataManager germplasmDataManager;

	@InjectMocks
	PassportAttributeTypesRowGenerator passportAttributeTypesRowGenerator;

	@Before
	public void setUp() {
		Mockito.when(this.germplasmDataManager.getUserDefinedFieldByFieldTableNameAndType(Matchers.anyString(), Matchers.anyString()))
				.thenReturn(UserDefinedFieldTestDataInitializer.createUserDefinedFieldList(PassportAttributeTypesRowGeneratorTest.TAXNO,
						PassportAttributeTypesRowGeneratorTest.TAXONOMY));
	}

	@Test
	public void testNameTypesRowsToCodesSheet() {
		this.passportAttributeTypesRowGenerator.addRowsToCodesSheet(this.codesSheet, this.sheetStyles);
		final HSSFRow row = this.codesSheet.getRow(1);
		Assert.assertEquals("First cell's content should be " + RowColumnType.PASSPORT_ATTRIBUTE_TYPES.getSection(),
				RowColumnType.PASSPORT_ATTRIBUTE_TYPES.getSection(), row.getCell(0).toString());
		Assert.assertEquals("Second cell's content should be " + RowColumnType.PASSPORT_ATTRIBUTE_TYPES.toString(),
				RowColumnType.PASSPORT_ATTRIBUTE_TYPES.toString(), row.getCell(1).toString());
		Assert.assertEquals("Third cell's content should be TAXNO", PassportAttributeTypesRowGeneratorTest.TAXNO,
				row.getCell(2).toString());
		Assert.assertEquals("Fourth cell's content should be Taxonomy", PassportAttributeTypesRowGeneratorTest.TAXONOMY,
				row.getCell(3).toString());
	}
}
