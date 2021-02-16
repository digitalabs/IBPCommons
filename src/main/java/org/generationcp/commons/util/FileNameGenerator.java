package org.generationcp.commons.util;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.security.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.html.Option;
import javax.validation.constraints.Null;
import java.io.File;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileNameGenerator {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hhmmss");
	private static final int MAX_SIZE = 255;
	private static final int MAX_SIZE_NAME = 200;
	private static final Logger LOG = LoggerFactory.getLogger(FileNameGenerator.class);
	private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9]*$");

	public static String generateFileName(final String directory, final String extension, final String... fileNames) {
		final int maxLengthPerName = MAX_SIZE_NAME / fileNames.length;
		final StringBuilder filename = new StringBuilder();
		Optional<String> hasExt = Optional.empty();
		for (final String name : fileNames) {
			final Optional<String> nameExt = hasExtension(name);
			if (nameExt.isPresent()) {
				hasExt = nameExt;
			}
			filename.append(truncateIfNecessary(removeExtension(name), maxLengthPerName, true));
		}
		final String ext;
		if (StringUtils.isEmpty(extension)) {
			if (hasExt.isPresent()) {
				ext = preppend(hasExt.get(), ".");
			} else {
				ext = extension;
			}
		} else {
			ext = preppend(extension, ".");
		}
		String genFileName = getFormattedFileName(filename.toString()) + ext;
		genFileName = fileNameChecker(directory, genFileName);
		return truncateIfNecessary(genFileName, MAX_SIZE, false);
	}

	public static String generateFileName(final String filename) {
		final String genName = FileNameGenerator.getFormattedFileName(filename);
		return genName;
	}

	public static String removeExtension(final String filename) {
		final Optional<String> extension = hasExtension(filename);
		if (extension.isPresent()) {
			final String ext = preppend(extension.get(), ".");
			final String woFilename = filename.replace(ext, "");
			return woFilename;
		}
		return filename;
	}

	public static String removeSuffixIfApplicable(final String filename) {

		final Optional<String> date = hasDate(filename);
		final Optional<String> time = hasTimeStamp(filename);
		String newFileName = filename;
		try {
			final Optional<String> username = hasUserName(filename);
			if (username.isPresent()) {
				newFileName = newFileName.replace("_" + username.get(), "");
			}
		} catch (final NullPointerException e){
			FileNameGenerator.LOG.debug(e.getMessage(), e);
		}
		if (date.isPresent()) {
			newFileName = newFileName.replace("_" +date.get(), "");
		}
		if (time.isPresent()) {
			newFileName = newFileName.replace("_" +time.get(), "");
		}
		return newFileName;
	}

	/**
	 * Checks if File name already exists in directory. If exists change file time stamp
	 * If file provided doesn't have extension, this will check all file names within folder and check by name only
	 * @param directory
	 * @param fileName
	 * @return
	 */
	private static String fileNameChecker(final String directory, final String fileName) {
		if (!StringUtils.isEmpty(directory)) {
			if (hasExtension(fileName).isPresent()) {
				File file = new File(directory + File.separator + fileName);
				while (file.exists()) {
					final String newFileName = getFormattedFileName(removeTimeStamp(fileName));
					file = new File(directory + File.separator + newFileName);
				}
				return file.getName();
			} else {
				final File file = new File(directory);
				File[] files = file.listFiles((d, s) -> {
					return s.contains(fileName);
				});
				if (files != null){
					String newFileName = fileName;
					while (files.length > 0) {
						final String newName = getFormattedFileName(removeTimeStamp(fileName));
						files = file.listFiles((d, s) -> {
							return s.contains(newName);
						});
						newFileName = newName;
					}
					return newFileName;
				}

			}
		}
		return fileName;
	}

	private static String preppend(final String extension, final String character) {
		if (!extension.contains(character) && extension.trim().length() > 1) {
			return character + extension;
		}
		return extension;
	}

	private static String truncateIfNecessary(final String name, final int maxSize, final boolean isForward) {
		String truncatedName = name;
		if (!StringUtils.isEmpty(name) && name.length() > maxSize) {
			if (isForward) {
				truncatedName = name.substring(0, maxSize);
			} else {
				final int excludeNoChar = name.length() - maxSize;
				truncatedName = name.substring(excludeNoChar, name.length());
			}

		}
		return truncatedName;
	}

	/**
	 * Check if filename contains username
	 * Check if filename contains date
	 * Check if filename contains timestamp
	 * Check if filename has extension, if true remove initially then finally append
	 * @param fileName
	 * @return String formatted fileName
	 */
	private static String getFormattedFileName(final String fileName) {
		final Date timeStamp = new Date();
		final StringBuilder sb = new StringBuilder();
		final Optional<String> extension = hasExtension(fileName);
		sb.append(removeExtension(fileName));
		try {
			if (!hasUserName(fileName).isPresent()) {

				sb.append("_");
				sb.append(SecurityUtil.getLoggedInUserName());
			}
		} catch (final NullPointerException e) {
			FileNameGenerator.LOG.debug(e.getMessage(), e);
		}

		if (!hasDate(fileName).isPresent()) {
			sb.append("_");
			sb.append(FileNameGenerator.DATE_FORMAT.format(timeStamp));
		}

		if (!hasTimeStamp(fileName).isPresent()) {
			sb.append("_");
			sb.append(FileNameGenerator.TIME_FORMAT.format(timeStamp));
		}

		String genName = truncateIfNecessary(sb.toString(), MAX_SIZE_NAME, false);
		if (extension.isPresent()) {
			genName = genName +  preppend(extension.get(), ".");
		}

		return genName;
	}

	public static Optional<String> hasUserName(final String fileName) {
		final String username = SecurityUtil.getLoggedInUserName();
		if (fileName.contains("_" + username)) {
			return Optional.of(username);
		}
		return Optional.empty();
	}

	public static Optional<String> hasDate(final String fileName) {
		final String[] fNames = fileName.split("_");
		if (fNames.length >= 3) {
			final String sDate = fNames[fNames.length - 2];
			try {
				FileNameGenerator.DATE_FORMAT.parse(sDate);
				return Optional.of(sDate);
			} catch (final ParseException parseException) {
				FileNameGenerator.LOG.debug(parseException.getMessage(), parseException);
			}

		}
		return Optional.empty();
	}

	public static Optional<String> hasTimeStamp(final String fileName) {
		final String[] fNames = fileName.split("_");
		if (fNames.length >= 3) {
			final String sTime = removeExtension(fNames[fNames.length - 1]);
			try {
				FileNameGenerator.TIME_FORMAT.parse(sTime);
				return Optional.of(sTime);
			} catch (final ParseException parseException) {
				FileNameGenerator.LOG.debug(parseException.getMessage(), parseException);
			}

		}
		return Optional.empty();
	}

	private static Optional<String> hasExtension(final String fileName) {
		if (!StringUtils.isEmpty(fileName) && fileName.contains(".")) {
			final String[] fileNames = fileName.split("\\.");
			if (fileNames.length >= 1) {
				final String extension = fileNames[fileNames.length - 1];
				final Matcher matcher = ALPHANUMERIC_PATTERN.matcher(extension);
				if (matcher.matches()) {
					return Optional.of(preppend(fileNames[fileNames.length - 1], "."));
				}
			}
		}
		return Optional.empty();
	}

	private static String removeTimeStamp(final String filename) {
		final Optional<String> timestamp = hasTimeStamp(filename);
		if (timestamp.isPresent()) {
			final String timeToRem = preppend(timestamp.get(), "_");
			return filename.replaceAll(timeToRem, "");
		}
		return filename;
	}

}
