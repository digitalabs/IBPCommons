
package org.generationcp.commons.help.document;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

public class HelpDocumentUtilTest {

	@Test
	public void testIsIBPDomainReachableReturnsFalseForEmptyURL() {
		Assert.assertFalse("Expecting to return false for empty url.", HelpDocumentUtil.isIBPDomainReachable(""));
	}

	@Test
	public void testIsIBPDomainReachableReturnsFalseForInvalidURL() {
		Assert.assertFalse("Expecting to return false for malformed url.", HelpDocumentUtil.isIBPDomainReachable("google.com"));
	}

	@Test
	public void testIsDocumentsFolderFoundReturnsFalseForInvalidPath() {
		Assert.assertFalse("Expecting to return false for an invalid path.", HelpDocumentUtil.isDocumentsFolderFound());
	}

	@Test
	public void testGetOnLineLinkReturnsAnEmptyStringForEmptyLink() {
		Assert.assertTrue("Expecting to return an empty link for empty url but didn't.",
				"".equalsIgnoreCase(HelpDocumentUtil.getOnLineLink("")));
	}

	@Test
	public void testGetOnLineLinkForValidURL() {
		String link = "www.google.com";
		Assert.assertTrue("Expecting to return a link with https:// but didn't.",
				HelpDocumentUtil.getOnLineLink(link).startsWith("https://"));
	}

	@Test
	public void testGetOffLineLinkReturnsAnEmptyStringForEmptyLink() throws MalformedURLException {
		URL currentURL = new URL("https:\\localhost:8080\\www.google.com");
		String offlineLink = HelpDocumentUtil.getOffLineLink(currentURL, "");
		Assert.assertTrue("Expecting to return an empty link for empty url but didn't.", "".equalsIgnoreCase(offlineLink));
	}

	@Test
	public void testGetOffLineLinkForValidURL() throws MalformedURLException {
		URL currentURL = new URL("https:\\localhost:8080\\www.google.com");
		String link = "www.google.com";
		String offlineLink = HelpDocumentUtil.getOffLineLink(currentURL, link);

		Assert.assertTrue("Expecting to contain a folder 'BMS_HTML' inside the url path. ", offlineLink.contains("BMS_HTML"));
		Assert.assertTrue("Expecting to end with .html but didn't.", offlineLink.endsWith(".html"));
		Assert.assertTrue("Expecting that the host and port of the current URL is used in the offline link. ",
				offlineLink.startsWith("http://" + currentURL.getHost() + ":" + currentURL.getPort()));
	}
}
