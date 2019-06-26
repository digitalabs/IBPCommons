
package org.generationcp.commons.workbook.generator;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.data.initializer.PersonTestDataInitializer;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.data.initializer.UserTestDataInitializer;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
@RunWith(MockitoJUnitRunner.class)
public class CodesSheetUserRowGeneratorTest {

	private static final String TEST_PERSON = "Test Person";
	private static final String USER_ID = "1";
	private static final String CROP_NAME = "maize";
	private final HSSFWorkbook wb = new HSSFWorkbook();
	private final HSSFSheet codesSheet = this.wb.createSheet("Codes");
	private final ExcelCellStyleBuilder sheetStyles = new ExcelCellStyleBuilder(this.wb);

	@Mock
	ContextUtil contextUtil;

	@Mock
	WorkbenchDataManager workbenchDataManager;

	@InjectMocks
	private CodesSheetUserRowGenerator userRowGenerator;

	private List<WorkbenchUser> userList;


	@Before
	public void setUp() {
		Mockito.when(this.contextUtil.getProjectInContext()).thenReturn(ProjectTestDataInitializer.createProject());

		this.userList = UserTestDataInitializer.createWorkbenchUserList();
		Mockito.when(this.workbenchDataManager.getUsersByProjectId(Matchers.anyLong(), Matchers.anyString())).thenReturn(this.userList);

		Mockito.when(this.workbenchDataManager.getPersonById(Matchers.anyInt())).thenReturn(PersonTestDataInitializer.createPerson());
	}

	@Test
	public void testAddListTypeRowsToCodesSheet() {
		this.userRowGenerator.addRowsToCodesSheet(this.codesSheet, this.sheetStyles, CROP_NAME);
		final HSSFRow row = this.codesSheet.getRow(1);
		Assert.assertEquals("First cell's content should be " + RowColumnType.USER.getSection(), RowColumnType.USER.getSection(),
				row.getCell(0).toString());
		Assert.assertEquals("Second cell's content should be " + RowColumnType.USER.toString(), RowColumnType.USER.toString(),
				row.getCell(1).toString());
		Assert.assertEquals("Third cell's content should be 1", CodesSheetUserRowGeneratorTest.USER_ID, row.getCell(2).toString());
		Assert.assertEquals("Fourth cell's content should be Test Person", CodesSheetUserRowGeneratorTest.TEST_PERSON, row.getCell(3).toString());
	}

	@Test
	public void testGetFname() {
		// Test data - Make user id not equal to person id
		final WorkbenchUser user = this.userList.get(0);
		final int personId = user.getUserid() + 1;
		user.setPersonid(personId);

		this.userRowGenerator.getFname(user);

		// Verify that person id, not user id, was the one used to get Person record
		Mockito.verify(this.workbenchDataManager).getPersonById(personId);
	}

	@Test
	public void testGetFcode() {
		final WorkbenchUser user = this.userList.get(0);

		final String fcode = this.userRowGenerator.getFcode(user);

		Assert.assertEquals(user.getUserid().toString(), fcode);
	}
}
