
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

		String filename = "你好abcABCæøåÆØÅäöüïëêîâéíáóúýñ½§!:*?|<>,\"/\\\\#¤%&()=`@£$€{[]}+´¨^~'-_,;";
		String convertedFilename = "%E4%BD%A0%E5%A5%BDabcABC%C3%A6%C3%B8%C3%A5%C3%86%C3%98%C3%85%C3%A4%C3%B6%C3%BC%C3%AF%C3%AB%C3%AA%C3%AE%C3%A2%C3%A9%C3%AD%C3%A1%C3%B3%C3%BA%C3%BD%C3%B1%C2%BD%C2%A7%21:*%3F%7C%3C%3E%2C%22/%5C%5C%23%C2%A4%25%26%28%29%3D%60%40%C2%A3%24%E2%82%AC%7B%5B%5D%7D%2B%C2%B4%C2%A8%5E%7E%27-_%2C%3B";

		Assert.assertEquals("Should be the same character as the converted filename", convertedFilename,
				FileUtils.encodeFilenameForDownload(filename));
	}

	public void testDetectMimeType() throws Exception {
		String xlsMimeResult = FileUtils.detectMimeType("file.xls");
		String xlsxMimeResult = FileUtils.detectMimeType("file.xlsx");
		String zipMimeResult = FileUtils.detectMimeType("file.zip");
		String pdfMimeResult = FileUtils.detectMimeType("file.pdf");
		String defaultMimeResult = FileUtils.detectMimeType("file.bin");

		Assert.assertEquals("Should return MIME_MS_EXCEL type", FileUtils.MIME_MS_EXCEL,xlsMimeResult);
		Assert.assertEquals("Should return MIME_MS_EXCEL type", FileUtils.MIME_MS_EXCEL,xlsxMimeResult);
		Assert.assertEquals("Should return MIME_ZIP type", FileUtils.MIME_ZIP,zipMimeResult);
		Assert.assertEquals("Should return MIME_PDF type", FileUtils.MIME_PDF,pdfMimeResult);
		Assert.assertEquals("Should return MIME_DEFAULT type", FileUtils.MIME_DEFAULT,defaultMimeResult);

	}

}
