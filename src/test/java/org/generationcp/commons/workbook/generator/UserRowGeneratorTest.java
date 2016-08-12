
package org.generationcp.commons.workbook.generator;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.generationcp.commons.data.initializer.ProjectTestDataInitializer;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.data.initializer.PersonTestDataInitializer;
import org.generationcp.middleware.data.initializer.UserTestDataInitializer;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
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
public class UserRowGeneratorTest {

	private static final String TEST_PERSON = "Test Person";
	private static final String USER_ID = "1";
	private final HSSFWorkbook wb = new HSSFWorkbook();
	private final HSSFSheet codesSheet = this.wb.createSheet("Codes");
	private final ExcelCellStyleBuilder sheetStyles = new ExcelCellStyleBuilder(this.wb);

	@Mock
	ContextUtil contextUtil;

	@Mock
	WorkbenchDataManager workbenchDataManager;

	@InjectMocks
	private UserRowGenerator userRowGenerator;
	
	private PersonTestDataInitializer personTDI;
	
	private UserTestDataInitializer userTDI;
	
	@Before
	public void setUp() {
		this.personTDI = new PersonTestDataInitializer();
		this.userTDI = new UserTestDataInitializer();
		Mockito.when(this.contextUtil.getProjectInContext()).thenReturn(ProjectTestDataInitializer.createProject());
		Mockito.when(this.workbenchDataManager.getUsersByProjectId(Matchers.anyLong())).thenReturn(this.userTDI.createUserList());
		Mockito.when(this.workbenchDataManager.getPersonById(Matchers.anyInt())).thenReturn(this.personTDI.createPerson());
	}

	@Test
	public void testAddListTypeRowsToCodesSheet() {
		this.userRowGenerator.addRowsToCodesSheet(this.codesSheet, this.sheetStyles);
		final HSSFRow row = this.codesSheet.getRow(1);
		Assert.assertEquals("First cell's content should be " + RowColumnType.USER.getSection(), RowColumnType.USER.getSection(),
				row.getCell(0).toString());
		Assert.assertEquals("Second cell's content should be " + RowColumnType.USER.toString(), RowColumnType.USER.toString(),
				row.getCell(1).toString());
		Assert.assertEquals("Third cell's content should be 1", UserRowGeneratorTest.USER_ID, row.getCell(2).toString());
		Assert.assertEquals("Fourth cell's content should be Test Person", UserRowGeneratorTest.TEST_PERSON, row.getCell(3).toString());
	}
}
