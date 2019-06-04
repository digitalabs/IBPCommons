
package org.generationcp.commons.help.document;

public class HelpDocumentUtil {

	private HelpDocumentUtil() {
		// do not implement
	}

	public static String getOnLineLink(String link) {
		String onlineLink = "";
		if (!link.isEmpty()) {
			onlineLink = "https://".concat(link);
		}

		return onlineLink;
	}

}
