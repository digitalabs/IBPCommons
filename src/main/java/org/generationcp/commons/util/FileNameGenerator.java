package org.generationcp.commons.util;

import org.generationcp.commons.security.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileNameGenerator {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hhmmss");
	private static final int MAX_SIZE = 255;
	private static final int MAX_SIZE_NAME = 200;
	private static final Logger LOG = LoggerFactory.getLogger(FileNameGenerator.class);


	public static String generateFileName(final String origFinalName, final String fileExt, final boolean... isAppendExtension) {

		final String extension = fileExt.contains(".") ? fileExt : "." + fileExt;
		final boolean isAppend = isAppendExtension.length == 1 ? isAppendExtension[0]: true;
		final StringBuilder fileName = new StringBuilder();
		fileName.append(FileNameGenerator.getFormattedFileName(origFinalName));
		if (isAppend) {
			fileName.append(extension);
		}
		return FileNameGenerator.truncateIfNecessary(fileName.toString(), FileNameGenerator.getFileNameMaxSize(isAppend, extension));
	}

	public static String generateFileName(final String origFinalName) {
		if (origFinalName.contains(".")) {
			final String[] fNames = origFinalName.split("\\.");
			if (fNames.length >= 2) {
				final String fileExtension = fNames[fNames.length - 1];
				String fileName = origFinalName.replaceAll(fileExtension, "");
				if (fileName.endsWith(".")) {
					fileName = fileName.substring(0, fileName.length() - 1);
				}
				return FileNameGenerator.generateFileName(fileName, fileExtension);
			}
		}
		return FileNameGenerator.generateFileName(origFinalName, "", false);
	}

	private static String truncateIfNecessary(final String name, final int maxSize) {
		String truncatedName = name;
		if (name.length() > maxSize) {
			final int excludeNoChar = name.length() - maxSize;
			truncatedName = name.substring(excludeNoChar, name.length());
		}
		return truncatedName;
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

	/**
	 * Check if filename contains username
	 * Check if filename contains date
	 * Check if filename contains timestamp
	 * Check if filename exceeds max_file_name
	 * @param fileName
	 * @return String formatted fileName
	 */
	private static String getFormattedFileName(final String fileName) {
		final Date timeStamp = new Date();
		final StringBuilder sb = new StringBuilder();
		sb.append(fileName);
		if (!hasUserName(fileName)) {
			sb.append("_");
			sb.append(SecurityUtil.getLoggedInUserName());
		}
		if (!hasDate(fileName)) {
			sb.append("_");
			sb.append(FileNameGenerator.DATE_FORMAT.format(timeStamp));
		}
		if (!hasTimeStamp(fileName)) {
			sb.append("_");
			sb.append(FileNameGenerator.TIME_FORMAT.format(timeStamp));
		}
		if (sb.toString().length() > FileNameGenerator.MAX_SIZE_NAME) {
			return FileNameGenerator.truncateIfNecessary(sb.toString(), FileNameGenerator.MAX_SIZE_NAME);
		} else {
			return sb.toString();
		}
	}

	private static boolean hasUserName(final String fileName) {
		return fileName.contains(SecurityUtil.getLoggedInUserName());
	}

	private static boolean hasDate(final String fileName) {
		final String[] fNames = fileName.split("_");
		if (fNames.length >= 3) {
			final String sDate = fNames[fNames.length - 2];
			try {
				FileNameGenerator.DATE_FORMAT.parse(sDate);
				return true;
			} catch (final ParseException parseException) {
				FileNameGenerator.LOG.debug(parseException.getMessage(), parseException);
				return false;
			}

		}
		return false;
	}

	private static boolean hasTimeStamp(final String fileName) {
		final String[] fNames = fileName.split("_");
		if (fNames.length >= 3) {
			final String sTime = fNames[fNames.length - 1];
			try {
				FileNameGenerator.TIME_FORMAT.parse(sTime);
				return true;
			} catch (final ParseException parseException) {
				FileNameGenerator.LOG.debug(parseException.getMessage(), parseException);
				return false;
			}

		}
		return false;
	}
}
