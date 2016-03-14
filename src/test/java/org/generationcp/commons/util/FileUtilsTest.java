
package org.generationcp.commons.util;

import java.io.File;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class FileUtilsTest {

	private static final String FILE_NAME = "Maize Basic-Template.2015.01.01";
	private static final String EXTENSION = "xls";
	private static final String COMPLETE_FILE_NAME = FileUtilsTest.FILE_NAME + "." + FileUtilsTest.EXTENSION;
	public static final String TEST_INVALID_FILE_NAME = "abc?12.xls";

	private static File file;

	@BeforeClass
	public static void setup() {
		FileUtilsTest.file = Mockito.mock(File.class);
		Mockito.doReturn(FileUtilsTest.COMPLETE_FILE_NAME).when(FileUtilsTest.file).getName();
	}

	@Test
	public void testIsFilenameValidInvalidInput() {
		String testFileName = TEST_INVALID_FILE_NAME;

		Assert.assertFalse("Utils unable to properly validate invalid file name", FileUtils.isFilenameValid(testFileName));
	}

	@Test
	public void testFileSanitizationEndingDot() {
		String testFileName = "abc.xls.";

		Assert.assertEquals("Utils unable to properly sanitize invalid file name", "abc.xls_", FileUtils.sanitizeFileName(testFileName));
	}

	@Test
	public void testFileSanitizationInvalidCharacters() {
		String testFileName = TEST_INVALID_FILE_NAME;

		Assert.assertEquals("Utils unable to properly sanitize invalid file name", "abc_12.xls", FileUtils.sanitizeFileName(testFileName));
	}

	@Test
	public void testFileSanitizationInvalidCharactersMultiple() {
		String testFileName = "abc?12?yay.xls";

		Assert.assertEquals("Utils unable to properly sanitize invalid file name", "abc_12_yay.xls",
				FileUtils.sanitizeFileName(testFileName));
	}

	@Test
	public void testGetFileeExtension() {
		String fileExtension = FileUtils.getExtension(FileUtilsTest.file);
		Assert.assertTrue(FileUtilsTest.EXTENSION.equals(fileExtension));
	}

	@Test
	public void testGetFIlenameWithoutExtensionFromFile() {
		String fileName = FileUtils.getFilenameWithoutExtension(FileUtilsTest.file);
		Assert.assertTrue(FileUtilsTest.FILE_NAME.equals(fileName));
	}

	@Test
	public void testGetFIlenameWithoutExtension() {
		String fileName = FileUtils.getFilenameWithoutExtension(FileUtilsTest.COMPLETE_FILE_NAME);
		Assert.assertTrue(FileUtilsTest.FILE_NAME.equals(fileName));
	}

	@Test
	public void testEncodeFilenameForDownload() {

		String filename = "testFilename<>";
		String convertedFilename = "testFilename__";

		Assert.assertEquals("Should be the same character as the converted filename", convertedFilename,
				FileUtils.encodeFilenameForDownload(filename));
	}
}
