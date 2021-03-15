package org.generationcp.commons.util;

import org.generationcp.commons.security.SecurityUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FileNameGenerator {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hhmmss");
	public static final int MAX_SIZE = 205;
	public static final int MAX_SIZE_WO_EXTENSION = 200;

	/**
	 *
	 * @param origFinalName
	 * @param fileExt
	 * @return Generated Filename filename_username_date_time_.fileextension
	 */
	public static String generateFileName(final String origFinalName, final String fileExt) {

		final StringBuilder fileName = new StringBuilder();
		fileName.append(origFinalName);
		fileName.append("_");
		fileName.append(FileNameGenerator.getUserNameTimeStamp());

		if (!StringUtil.isEmpty(fileExt)) {
			final String extension = fileExt.contains(".") ? fileExt : "." + fileExt;
			fileName.append(extension);
			return StringUtil.truncate(fileName.toString(), MAX_SIZE, false);
		}
		return StringUtil.truncate(fileName.toString(), MAX_SIZE_WO_EXTENSION, false);
	}

	private static String getUserNameTimeStamp() {
		final Date timeStamp = new Date();
		final StringBuilder sb = new StringBuilder();
		sb.append(SecurityUtil.getLoggedInUserName());
		sb.append("_");
		sb.append(FileNameGenerator.DATE_FORMAT.format(timeStamp));
		sb.append("_");
		sb.append(FileNameGenerator.TIME_FORMAT.format(timeStamp));
		return sb.toString();
	}
}
