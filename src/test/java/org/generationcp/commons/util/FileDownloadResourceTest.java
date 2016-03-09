
package org.generationcp.commons.util;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.Application;

public class FileDownloadResourceTest {

	private static final String INVALID_FILE_NAME = "abc<>";

	@Test
	public void testFileNameSanitizationManualInput() {
		FileDownloadResource resource = new FileDownloadResource(Mockito.mock(File.class), Mockito.mock(Application.class));
		resource.setFilename(INVALID_FILE_NAME);

		Assert.assertTrue("Object does not sanitize the manually set file name", FileUtils.isFilenameValid(resource.getFilename()));
	}

	@Test
	public void testFileNameSanitizationFileInput() {
		FileDownloadResource resource = new FileDownloadResource(new File(INVALID_FILE_NAME), Mockito.mock(Application.class));

		Assert.assertTrue("Object does not sanitize the file name", FileUtils.isFilenameValid(resource.getFilename()));
	}
}
