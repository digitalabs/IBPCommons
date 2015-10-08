
package org.generationcp.commons.service.impl;

import junit.framework.Assert;
import org.generationcp.commons.service.FileService;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FileServiceTest {

	private FileService unitUnderTest;

	@Before
	public void setUp() {
		unitUnderTest = new FileServiceImpl();
		((FileServiceImpl) unitUnderTest).init();
	}

	@Test
	public void testFileSaveWithCleanup() throws IOException {
		InputStream inputStream =
				Thread.currentThread().getContextClassLoader().getResourceAsStream("templates/GermplasmImportTemplate-Expanded-rev5a.xls");
		String tempFilename = unitUnderTest.saveTemporaryFile(inputStream);

		Assert.assertNotNull("Service must be able to output the saved temporary filename of the given input", tempFilename);
		File savedFile = unitUnderTest.retrieveFileFromFileName(tempFilename);
		Assert.assertNotNull("Service should be able to retrieve a saved file given the temporary file name");

		savedFile.delete();
	}

}
