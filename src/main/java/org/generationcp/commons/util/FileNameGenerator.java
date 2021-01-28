package org.generationcp.commons.util;

import org.generationcp.commons.security.SecurityUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FileNameGenerator {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hhmmss");
	private static final int MAX_SIZE = 256;
	private static final int MAX_SIZE_NAME = 200;

	public static String generateFileName(final String origFinalName, final String fileExt, final boolean... isAppendExtension) {
		final String extension = fileExt.contains(".") ? fileExt : "." + fileExt;
		final boolean isAppend = isAppendExtension.length == 1 ? isAppendExtension[0]: true;
		final StringBuilder fileName = new StringBuilder();
		fileName.append(origFinalName);
		fileName.append("_");
		fileName.append(SecurityUtil.getLoggedInUserName());
		fileName.append("_");
		fileName.append(FileNameGenerator.getTimeStamp());
		if (isAppend) {
			fileName.append(extension);
		}
		return FileNameGenerator.truncateIfNecessary(fileName.toString(), FileNameGenerator.getFileNameMaxSize(isAppend, extension));
	}

	public static String generateFileName(final String origFinalName) {

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
			maxSize = FileNameGenerator.getFileNameMaxSize(true, fileExt);
		} else {
			maxSize = FileNameGenerator.MAX_SIZE_NAME;
		}

		final StringBuilder fileName = new StringBuilder();
		fileName.append(oFName);
		fileName.append("_");
		fileName.append(SecurityUtil.getLoggedInUserName());
		fileName.append("_");
		fileName.append(FileNameGenerator.getTimeStamp());
		if (!fileExt.contains("\\.") && !fileExt.equals("")) {
			fileName.append(".");
		}
		fileName.append(fileExt);
		return FileNameGenerator.truncateIfNecessary(fileName.toString(), maxSize);
	}

	private static String truncateIfNecessary(final String name, final int maxSize) {
		String truncatedName = name;
		if (name.length() > maxSize) {
			final int excludeNoChar = name.length() - maxSize;
			truncatedName = name.substring(excludeNoChar, name.length());
		}
		return truncatedName;
	}
	private static String getTimeStamp() {
		final Date timeStamp = new Date();
		final StringBuilder sb = new StringBuilder();
		sb.append(FileNameGenerator.DATE_FORMAT.format(timeStamp));
		sb.append("_");
		sb.append(FileNameGenerator.TIME_FORMAT.format(timeStamp));
		return sb.toString();
	}

	private static int getFileNameMaxSize(final boolean extensionIncluded, final String extension) {
		if (StringUtil.isEmpty(extension)) {
			return FileNameGenerator.MAX_SIZE_NAME;
		} else if (extensionIncluded) {
			return FileNameGenerator.MAX_SIZE;
		} else {
			return FileNameGenerator.MAX_SIZE - extension.length();
		}
	}
}
