package org.generationcp.commons.parsing;

import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.generationcp.commons.parsing.pojo.ImportedCrossesList;
import org.generationcp.commons.util.DateUtil;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.pojos.Person;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import junit.framework.Assert;

@RunWith(MockitoJUnitRunner.class)
public class CrossesListDescriptionSheetParserTest {

	private final static String CROSSES_LIST_NO_LIST_DATE = "CrossesListNoListDate.xls";
	private final static String CROSSES_LIST = "CrossesList.xls";
	private static final String LIST_DATE_IN_XLS_TEST_FILE = "20160722";

	private final ImportedCrossesList crossesList = new ImportedCrossesList();

	@Mock
	private UserDataManager userDataManager;

	private CrossesListDescriptionSheetParser<ImportedCrossesList>
			crossesListDescriptionSheetParser = new CrossesListDescriptionSheetParser<>(this.crossesList, this.userDataManager);

	private Workbook workbookNoListDate;
	private Workbook workbook;
	private Date today;

	@Before
	public void setUp() throws Exception {
		final Person personTest = new Person("Test", "Test", "Test");
		personTest.setId(1);
		Mockito.when(this.userDataManager.getPersonByName(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
						.thenReturn(personTest);

		this.crossesListDescriptionSheetParser = new CrossesListDescriptionSheetParser<>(this.crossesList, this.userDataManager);

		this.today = new Date();
		final URL crossesListWithoutDateURL = ClassLoader.getSystemClassLoader().getResource(CROSSES_LIST_NO_LIST_DATE);
		if (crossesListWithoutDateURL != null) {
			final File workbookFile1 = new File(crossesListWithoutDateURL.toURI());
			assert workbookFile1.exists();
			this.workbookNoListDate = WorkbookFactory.create(workbookFile1);
		}
		final URL crossesListURL = ClassLoader.getSystemClassLoader().getResource(CROSSES_LIST);
		if (crossesListURL != null) {
			final File workbookFile2 = new File(crossesListURL.toURI());
			assert workbookFile2.exists();
			this.workbook = WorkbookFactory.create(workbookFile2);
		}
	}

	@Test
	public void testEmptyListDate() throws ParseException, FileParsingException {
		this.crossesListDescriptionSheetParser.parseWorkbook(this.workbookNoListDate, null);
		Assert.assertTrue("The list date should be " + this.today, this.crossesListDescriptionSheetParser.getImportedList().getDate().after(this.today));
	}

	@Test
	public void testListType() throws ParseException, FileParsingException {
		this.crossesListDescriptionSheetParser.parseWorkbook(this.workbookNoListDate, null);
		Assert.assertTrue("The list type should be " + CrossesListDescriptionSheetParser.TEMPLATE_LIST_TYPE, this.crossesListDescriptionSheetParser.getImportedList().getType().equals(CrossesListDescriptionSheetParser.TEMPLATE_LIST_TYPE));
	}

	@Test
	public void testWithListDate() throws ParseException, FileParsingException {
		this.crossesListDescriptionSheetParser.parseWorkbook(this.workbook, null);
		Assert.assertTrue("The list date should be " + LIST_DATE_IN_XLS_TEST_FILE, this.crossesListDescriptionSheetParser.getImportedList().getDate().equals(DateUtil.parseDate(LIST_DATE_IN_XLS_TEST_FILE)));
	}

}
