package org.generationcp.commons.parsing;

import junit.framework.Assert;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.generationcp.commons.parsing.pojo.ImportedCrossesList;
import org.generationcp.commons.util.DateUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;

public class DescriptionSheetParserTest {

	private final static String CROSSES_LIST_NO_LIST_DATE = "CrossesListNoListDate.xls";
	private final static String CROSSES_LIST = "CrossesList.xls";
	public static final String CROSS_LIST_TYPE = "CROSS";

	private final ImportedCrossesList crossesList = new ImportedCrossesList();
	private final DescriptionSheetParser<ImportedCrossesList> descriptionSheetParser = new DescriptionSheetParser<>(this.crossesList);
	private Workbook workbookNoListDate;
	private Workbook workbook;
	private Date today;

	@Before
	public void setUp() throws Exception {
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
		this.descriptionSheetParser.parseWorkbook(this.workbookNoListDate, null);
		Assert.assertTrue(this.descriptionSheetParser.getImportedList().getDate().after(this.today));
	}

	@Test
	public void testListType() throws ParseException, FileParsingException {
		this.descriptionSheetParser.parseWorkbook(this.workbookNoListDate, null);
		Assert.assertTrue(this.descriptionSheetParser.getImportedList().getType().equals(CROSS_LIST_TYPE));
	}

	@Test
	public void testWithListDate() throws ParseException, FileParsingException {
		this.descriptionSheetParser.parseWorkbook(this.workbook, null);
		Assert.assertTrue(this.descriptionSheetParser.getImportedList().getDate().equals(DateUtil.parseDate("20150505")));
	}

}
