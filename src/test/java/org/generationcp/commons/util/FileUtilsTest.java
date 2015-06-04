
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

	private static File file;

	@BeforeClass
	public static void setup() {
		FileUtilsTest.file = Mockito.mock(File.class);
		Mockito.doReturn(FileUtilsTest.COMPLETE_FILE_NAME).when(FileUtilsTest.file).getName();
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

}
