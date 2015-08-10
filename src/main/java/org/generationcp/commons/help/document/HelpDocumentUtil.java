
package org.generationcp.commons.help.document;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelpDocumentUtil {

	private static final Logger LOG = LoggerFactory.getLogger(HelpDocumentUtil.class);

	private HelpDocumentUtil() {
		// do not implement
	}

	public static boolean isIBPDomainReachable(String onlineLink) {
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

	public static boolean isDocumentsFolderFound(WorkbenchDataManager workbenchDataManager) {
		String installationDirectory = getInstallationDirectory(workbenchDataManager);
		String docsDirectory = installationDirectory + File.separator + "Documents" + File.separator;
		File docsDirectoryFile = new File(docsDirectory);
		if (docsDirectoryFile.exists() && docsDirectoryFile.isDirectory()) {
			return true;
		}
		return false;
	}

	public static String getInstallationDirectory(WorkbenchDataManager workbenchDataManager) {
		WorkbenchSetting setting = null;
		try {
			setting = workbenchDataManager.getWorkbenchSetting();
		} catch (MiddlewareQueryException e) {
			LOG.error(e.getMessage(), e);
		}
		String installationDirectory = "";
		if (setting != null) {
			installationDirectory = setting.getInstallationDirectory();
		}
		return installationDirectory;
	}
}
