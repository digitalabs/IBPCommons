
package org.generationcp.commons.util;

import org.junit.Assert;
import org.junit.Test;

public class FileDownloadResourceTest {

	@Test
	public void testGetDownloadFilename() {
		String filename = "testFilename";
		String utfConversion = "=?UTF-8?Q?=74=65=73=74=46=69=6C=65=6E=61=6D=65?=";
		Assert.assertEquals("Should be the same character as the converted UTF-8", utfConversion,
				FileDownloadResource.getDownloadFileName(filename, null));
	}
}
