package org.generationcp.commons.parsing;

import junit.framework.Assert;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.generationcp.commons.parsing.pojo.ImportedCrossesList;
import org.generationcp.commons.util.DateUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.text.ParseException;
import java.util.Date;

public class DescriptionSheetParserTest {

	public final String CROSSES_LIST_NO_LIST_DATE = "CrossesListNoListDate.xls";
	public final String CROSSES_LIST = "CrossesList.xls";

	private final ImportedCrossesList crossesList = new ImportedCrossesList();
	private final DescriptionSheetParser descriptionSheetParser = new DescriptionSheetParser(crossesList);
	private Workbook workbookNoListDate;
	private Workbook workbook;
	private Date today;

	@Before
	public void setUp() throws Exception {
		today = new Date();
		File workbookFile1 = new File(ClassLoader.getSystemClassLoader().getResource(CROSSES_LIST_NO_LIST_DATE).toURI());
		File workbookFile2 = new File(ClassLoader.getSystemClassLoader().getResource(CROSSES_LIST).toURI());

		assert workbookFile1.exists();
		assert workbookFile2.exists();
		this.workbookNoListDate = WorkbookFactory.create(workbookFile1);
		this.workbook = WorkbookFactory.create(workbookFile2);

	}

	@Test
	public void testEmptyListDate() throws ParseException, FileParsingException {
		descriptionSheetParser.parseWorkbook(this.workbookNoListDate, null);
		Assert.assertTrue(descriptionSheetParser.getImportedList().getDate().after(today));
	}

	@Test
	public void testWithListDate() throws ParseException, FileParsingException {
		descriptionSheetParser.parseWorkbook(this.workbook, null);
		Assert.assertTrue(descriptionSheetParser.getImportedList().getDate().equals(DateUtil.parseDate("20150505")));
	}

}
