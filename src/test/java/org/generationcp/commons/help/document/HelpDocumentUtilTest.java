
package org.generationcp.commons.help.document;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class HelpDocumentUtilTest {

	private static final String INSTALLATION_PATH = "C:\\BMS 4";

	@Test
	public void testIsIBPDomainReachable_ReturnsFalseForEmptyURL() {
		Assert.assertFalse("Expecting to return false for empty url.", HelpDocumentUtil.isIBPDomainReachable(""));
	}

	@Test
	public void testIsIBPDomainReachable_ReturnsFalseForInvalidURL() {
		Assert.assertFalse("Expecting to return false for malformed url.", HelpDocumentUtil.isIBPDomainReachable("google.com"));
	}

	@Test
	public void testIsDocumentsFolderFound_ReturnsFalseForInvalidPath() {
		Assert.assertFalse("Expecting to return false for an invalid path.", HelpDocumentUtil.isDocumentsFolderFound(""));
	}

	@Test
	public void testIsDocumentsFolderFound_ForValidPath() {

		if (new File(INSTALLATION_PATH.concat("\\Documents")).exists()) {
			Assert.assertTrue("Expecting to return true when the 'Documents' folder does exists.",
					HelpDocumentUtil.isDocumentsFolderFound(INSTALLATION_PATH));
		} else {
			Assert.assertFalse("Expecting to return false when the 'Documents' folder does not exists.",
					HelpDocumentUtil.isDocumentsFolderFound(INSTALLATION_PATH));
		}

	}

	@Test
	public void testGetInstallationDirectory_ReturnsEmptyForNullWorkbenchSetting() throws MiddlewareQueryException {
		WorkbenchDataManager workbenchDataManager = Mockito.mock(WorkbenchDataManager.class);
		Mockito.when(workbenchDataManager.getWorkbenchSetting()).thenReturn(null);
		Assert.assertEquals("Expecting to return an empty string for installation directory when the workbench setting is null.", "",
				HelpDocumentUtil.getInstallationDirectory(workbenchDataManager));
	}

	@Test
	public void testGetInstallationDirectory_ReturnsValidValueForNullWorkbenchSetting() throws MiddlewareQueryException {
		WorkbenchDataManager workbenchDataManager = Mockito.mock(WorkbenchDataManager.class);
		WorkbenchSetting workbenchSetting = Mockito.mock(WorkbenchSetting.class);
		Mockito.when(workbenchDataManager.getWorkbenchSetting()).thenReturn(workbenchSetting);
		Mockito.when(workbenchSetting.getInstallationDirectory()).thenReturn(INSTALLATION_PATH);
		Assert.assertEquals("Expecting to return the proper installation path but didn't.", INSTALLATION_PATH,
				HelpDocumentUtil.getInstallationDirectory(workbenchDataManager));
	}

	@Test
	public void testGetOnLineLink_ReturnsAnEmptyStringForEmptyLink() {
		Assert.assertTrue("Expecting to return an empty link for empty url but didn't.",
				"".equalsIgnoreCase(HelpDocumentUtil.getOnLineLink("")));
	}

	@Test
	public void testGetOnLineLink_ForValidURL() {
		String link = "www.google.com";
		Assert.assertTrue("Expecting to return a link with https:// but didn't.",
				HelpDocumentUtil.getOnLineLink(link).startsWith("https://"));
	}

	@Test
	public void testGetOffLineLink_ReturnsAnEmptyStringForEmptyLink() throws MalformedURLException {
		URL currentURL = new URL("https:\\localhost:8080\\www.google.com");
		String offlineLink = HelpDocumentUtil.getOffLineLink(currentURL, "");
		Assert.assertTrue("Expecting to return an empty link for empty url but didn't.", "".equalsIgnoreCase(offlineLink));
	}

	@Test
	public void testGetOffLineLink_ForValidURL() throws MalformedURLException {
		URL currentURL = new URL("https:\\localhost:8080\\www.google.com");
		String link = "www.google.com";
		String offlineLink = HelpDocumentUtil.getOffLineLink(currentURL, link);

		Assert.assertTrue("Expecting to contain a folder 'BMS_HTML' inside the url path. ", offlineLink.contains("BMS_HTML"));
		Assert.assertTrue("Expecting to end with .html but didn't.", offlineLink.endsWith(".html"));
		Assert.assertTrue("Expecting that the host and port of the current URL is used in the offline link. ",
				offlineLink.startsWith("http://" + currentURL.getHost() + ":" + currentURL.getPort()));
	}
}
