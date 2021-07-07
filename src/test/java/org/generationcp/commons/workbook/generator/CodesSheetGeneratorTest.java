
package org.generationcp.commons.workbook.generator;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
@RunWith(MockitoJUnitRunner.class)
public class CodesSheetGeneratorTest {

	private static final String FNAME = "fname";
	private static final String FCODE = "fcode";
	private static final String INFORMATION_TYPE = "Information Type";
	private static final String SECTION = "Section";

	HSSFWorkbook wb = new HSSFWorkbook();
	ExcelCellStyleBuilder sheetStyles = new ExcelCellStyleBuilder(this.wb);

	@Mock
	CodesSheetListTypeRowGenerator listTypeRowGenerator;

	@Mock
	CodesSheetUserRowGenerator userRowGenerator;

	@Mock
	CodesSheetNameTypesRowGenerator nameTypesRowGenerator;

	@Mock
	CodesSheetInventoryScalesRowGenerator inventoryScalesRowGenerator;

	@InjectMocks
	private CodesSheetGenerator codesSheetGenerator;

	@Test
	public void testGenerateCodesSheet() {
		this.codesSheetGenerator.generateCodesSheet(this.wb);
		final HSSFSheet codesSheet = this.codesSheetGenerator.getCodesSheet();
		this.assertHeadersRow(codesSheet.getRow(0));
	}

	private void assertHeadersRow(final HSSFRow row) {
		Assert.assertEquals("Header's first cell should be \"Section\"", CodesSheetGeneratorTest.SECTION, row.getCell(0).toString());
		Assert.assertEquals("Header's second cell should be \"Information Type\"", CodesSheetGeneratorTest.INFORMATION_TYPE,
				row.getCell(1).toString());
		Assert.assertEquals("Header's third cell should be \"fcode\"", CodesSheetGeneratorTest.FCODE, row.getCell(2).toString());
		Assert.assertEquals("Header's fourth cell should be \"fname\"", CodesSheetGeneratorTest.FNAME, row.getCell(3).toString());
	}
}
