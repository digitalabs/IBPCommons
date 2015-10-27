package org.generationcp.commons.workbook.generator;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.generationcp.commons.data.initializer.PersonDataInitializer;
import org.generationcp.commons.data.initializer.ProjectDataInitializer;
import org.generationcp.commons.data.initializer.UserDataInitializer;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.commons.spring.util.ContextUtil;
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
	private HSSFWorkbook wb = new HSSFWorkbook();
	private HSSFSheet codesSheet = wb.createSheet("Codes");
	private ExcelCellStyleBuilder sheetStyles = new ExcelCellStyleBuilder(wb);
	
	@Mock
	ContextUtil contextUtil;
	
	@Mock
	WorkbenchDataManager workbenchDataManager;
	
	
	@InjectMocks
	private UserRowGenerator userRowGenerator;
	
	@Before
	public void setUp(){
		Mockito.when(contextUtil.getProjectInContext()).thenReturn(ProjectDataInitializer.createProject());
		Mockito.when(workbenchDataManager.getUsersByProjectId(Matchers.anyLong())).thenReturn(UserDataInitializer.createUserList());
		Mockito.when(workbenchDataManager.getPersonById(Matchers.anyInt())).thenReturn(PersonDataInitializer.createPerson());
	}
	
	@Test
	public void testAddListTypeRowsToCodesSheet(){
		this.userRowGenerator.addRowsToCodesSheet(codesSheet, sheetStyles);
		HSSFRow row = codesSheet.getRow(1);
		Assert.assertEquals("First cell's content should be " + RowColumnType.USER.getSection(), RowColumnType.USER.getSection(), row.getCell(0).toString());
		Assert.assertEquals("Second cell's content should be " + RowColumnType.USER.toString(), RowColumnType.USER.toString(), row.getCell(1).toString());
		Assert.assertEquals("Third cell's content should be 1", USER_ID, row.getCell(2).toString());
		Assert.assertEquals("Fourth cell's content should be Test Person", TEST_PERSON, row.getCell(3).toString());
	}
}
