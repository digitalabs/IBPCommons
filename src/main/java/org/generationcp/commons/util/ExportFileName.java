package org.generationcp.commons.util;

import org.generationcp.commons.security.SecurityUtil;
import org.springframework.security.access.method.P;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExportFileName {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hhmmss");
	private static final int MAX_SIZE = 256;
	private static final int MAX_SIZE_NAME = 200;
	private static ExportFileName instance;
	private ExportFileName() {

	}
	public static ExportFileName getInstance() {
		if (instance == null) {
			instance = new ExportFileName();
		}
		return instance;
	}

	public String generateFileName(final String origFinalName, final String fileExt, final boolean... isAppendExtension) {
		final String extension = fileExt.contains(".") ? fileExt : "." + fileExt;
		final boolean isAppend = isAppendExtension.length == 1 ? isAppendExtension[0]: true;
		final StringBuilder fileName = new StringBuilder();
		fileName.append(origFinalName);
		fileName.append("_");
		fileName.append(SecurityUtil.getLoggedInUserName());
		fileName.append("_");
		fileName.append(this.getTimeStamp());
		if (isAppend) {
			fileName.append(extension);
		}
		return this.truncateIfNecessary(fileName.toString(), this.getFileNameMaxSize(isAppend, extension));
	}

	public String generateFileName(final String origFinalName) {

		final int maxSize;
		String fileExt = "";
		String oFName = origFinalName;
		if (origFinalName.contains(".")) {
			final String oFileName[] = origFinalName.split("\\.");
			oFName = oFileName[0];
			if (oFileName.length >= 2) {
				// Get the Last Group
				fileExt = oFileName[oFileName.length - 1];
			}
			maxSize = this.getFileNameMaxSize(true, fileExt);
		} else {
			maxSize = ExportFileName.MAX_SIZE_NAME;
		}

		final StringBuilder fileName = new StringBuilder();
		fileName.append(oFName);
		fileName.append("_");
		fileName.append(SecurityUtil.getLoggedInUserName());
		fileName.append("_");
		fileName.append(this.getTimeStamp());
		if (!fileExt.contains("\\.") && !fileExt.equals("")) {
			fileName.append(".");
		}
		fileName.append(fileExt);
		return this.truncateIfNecessary(fileName.toString(), maxSize);
	}

	private String truncateIfNecessary(final String name, final int maxSize) {
		String truncatedName = name;
		if (name.length() > maxSize) {
			final int excludeNoChar = name.length() - maxSize;
			truncatedName = name.substring(excludeNoChar, name.length());
		}
		return truncatedName;
	}
	private String getTimeStamp() {
		final Date timeStamp = new Date();
		final StringBuilder sb = new StringBuilder();
		sb.append(ExportFileName.DATE_FORMAT.format(timeStamp));
		sb.append("_");
		sb.append(ExportFileName.TIME_FORMAT.format(timeStamp));
		return sb.toString();
	}

	private int getFileNameMaxSize(final boolean extensionIncluded, final String extension) {
		if (StringUtil.isEmpty(extension)) {
			return ExportFileName.MAX_SIZE_NAME;
		} else if (extensionIncluded) {
			return ExportFileName.MAX_SIZE;
		} else {
			return ExportFileName.MAX_SIZE - extension.length();
		}
	}
}
