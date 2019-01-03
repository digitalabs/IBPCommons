
package org.generationcp.commons.help.document;

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

}
