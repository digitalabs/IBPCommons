
package org.generationcp.commons.util;

import com.vaadin.Application;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;

public class FileDownloadResourceTest {
    private static final String INVALID_FILE_NAME = "abc<>";

	@Test
	public void testGetDownloadFilename() {
		String filename = "testFilename";
		String utfConversion = "=?UTF-8?Q?=74=65=73=74=46=69=6C=65=6E=61=6D=65?=";
		Assert.assertEquals("Should be the same character as the converted UTF-8", utfConversion,
				FileDownloadResource.getDownloadFileName(filename, null));
	}

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
