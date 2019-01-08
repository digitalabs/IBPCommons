
package org.generationcp.commons.help.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelpDocumentUtil {

	private static final Logger LOG = LoggerFactory.getLogger(HelpDocumentUtil.class);

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
