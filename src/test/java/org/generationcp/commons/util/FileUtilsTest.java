package org.generationcp.commons.util;

import java.io.File;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class FileUtilsTest {
	
	private static final String FILE_NAME = "Maize Basic-Template.2015.01.01";
	private static final String EXTENSION = "xls";
	private static final String COMPLETE_FILE_NAME = FILE_NAME + "." + EXTENSION;

	private static File file;
	
	@BeforeClass
	public static void setup(){
		file = Mockito.mock(File.class);
		Mockito.doReturn(COMPLETE_FILE_NAME).when(file).getName();
	}
	
	@Test
	public void testGetFileeExtension(){
		String fileExtension = FileUtils.getExtension(file);
		Assert.assertTrue(EXTENSION.equals(fileExtension));
	}
	
	@Test
	public void testGetFIlenameWithoutExtensionFromFile(){
		String fileName = FileUtils.getFilenameWithoutExtension(file);
		Assert.assertTrue(FILE_NAME.equals(fileName));
	}
	
	@Test
	public void testGetFIlenameWithoutExtension(){
		String fileName = FileUtils.getFilenameWithoutExtension(COMPLETE_FILE_NAME);
		Assert.assertTrue(FILE_NAME.equals(fileName));
	}
	

}
