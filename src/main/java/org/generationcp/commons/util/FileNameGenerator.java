package org.generationcp.commons.util;

import org.generationcp.commons.security.SecurityUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileNameGenerator {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hhmmss");
	public static final int MAX_SIZE = 100;
	public static final int MAX_SIZE_WO_EXTENSION = 96;
	public static final String CSV_DATE_TIME_PATTERN = "_[0-9]{8}_[0-9]{6}.csv$";
	public static final String XLS_DATE_TIME_PATTERN = "_[0-9]{8}_[0-9]{6}.xls$";
	public static final String XLSX_DATE_TIME_PATTERN = "_[0-9]{8}_[0-9]{6}.xlsx$";
	public static final String XML_DATE_TIME_PATTERN = "_[0-9]{8}_[0-9]{6}.xml$";
	public static final String ZIP_DATE_TIME_PATTERN = "_[0-9]{8}_[0-9]{6}.zip$";

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

	public static String generateFileName(final String origFinalName) {
		return generateFileName(origFinalName, "");
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

	public static boolean isValidFileNameFormat(final String fileName, final String pattern) {
		final Pattern pattern1 = Pattern.compile(pattern);
		final Matcher matcher = pattern1.matcher(fileName);
		return matcher.find();
	}
}
