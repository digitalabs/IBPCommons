
package org.generationcp.commons.help.document;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelpDocumentUtil {

	private static final Logger LOG = LoggerFactory.getLogger(HelpDocumentUtil.class);

	private HelpDocumentUtil() {
		// do not implement
	}

	public static boolean isIBPDomainReachable(String onlineLink) {

		if (onlineLink.length() == 0) {
			return false;
		}

		try {
			final URL url = new URL(onlineLink);
			final URLConnection conn = url.openConnection();
			conn.connect();
			return true;
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return false;
		}
	}

	public static boolean isDocumentsFolderFound() {
		String docsDirectory = "Documents" + File.separator;
		File docsDirectoryFile = new File(docsDirectory);
		if (docsDirectoryFile.exists() && docsDirectoryFile.isDirectory()) {
			return true;
		}
		return false;
	}

	
	public static String getOnLineLink(String link) {
		String onlineLink = "";
		if (!link.isEmpty()) {
			onlineLink = "https://".concat(link);
		}

		return onlineLink;
	}

	public static String getOffLineLink(URL currentURL, String link) {
		String offlineLink = "";
		if (!link.isEmpty()) {
			StringBuilder offlineLinkBuilder = new StringBuilder();
			offlineLinkBuilder.append("BMS_HTML/");
			offlineLinkBuilder.append(link);
			offlineLinkBuilder.append(".html");

			String host = currentURL.getHost();
			Integer port = currentURL.getPort();
			offlineLink = "http://" + host + ":" + port + "/" + offlineLinkBuilder.toString();
		}
		return offlineLink;
	}
}
