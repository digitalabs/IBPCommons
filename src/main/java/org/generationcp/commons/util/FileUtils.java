/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.commons.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class FileUtils {

	private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

	public static final String INVALID_CHARACTER_REGEX_PATTERN = "[\\\\/:*?|<>\"]";
	public static final Character INVALID_FILE_CHARACTER_REPLACEMENT = '_';

	public static final String MIME_MS_EXCEL = "application/vnd.ms-excel";
	public static final String MIME_CSV = "text/csv";
	public static final String MIME_ZIP = "application/zip";
	public static final String MIME_PDF = "application/pdf";
	public static final String MIME_DEFAULT = "application/octet-stream";

	private FileUtils() {
		// hide public constructor for this utility class
	}

	/**
	 * Delete the specified file recursively.
	 * 
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 */
	public static boolean deleteRecursive(File path) throws FileNotFoundException {
		if (!path.exists()) {
			throw new FileNotFoundException(path.getAbsolutePath());
		}
		boolean ret = true;
		if (path.isDirectory()) {
			for (File f : path.listFiles()) {
				ret = ret && FileUtils.deleteRecursive(f);
			}
		}
		return ret && path.delete();
	}

	public static boolean isFilenameValid(String proposedFileName) {
		// blank file names are invalid regardless of OS
		if (StringUtils.isEmpty(proposedFileName)) {
			return false;
		}

		// files ending with dot are invalid
		if (proposedFileName.endsWith(".")) {
			return false;
		}

		return !proposedFileName.matches(".*" + INVALID_CHARACTER_REGEX_PATTERN + ".*");

	}

	/**
	 * Removes all forbidden characters in a file name.
	 * 
	 * @param fileName
	 * @return
	 */
	public static String sanitizeFileName(String fileName) {

		String sanitizedFileName = fileName;

		sanitizedFileName = sanitizedFileName.replaceAll(INVALID_CHARACTER_REGEX_PATTERN, INVALID_FILE_CHARACTER_REPLACEMENT.toString());

		if (sanitizedFileName.endsWith(".")) {
			int index = sanitizedFileName.lastIndexOf('.');
			sanitizedFileName = sanitizedFileName.substring(0, index) + INVALID_FILE_CHARACTER_REPLACEMENT.toString();
		}

		return sanitizedFileName;
	}

	/**
	 * Encodes the file name to ASCII String
	 * 
	 * @param filename
	 * @return
	 */
	public static String encodeFilenameForDownload(String filename) {

		try {

			URI uri = new URI(null, null, sanitizeFileName(filename), null);

			return uri.toASCIIString();

		} catch (URISyntaxException e) {
			FileUtils.LOG.error(e.getMessage(), e);

		}
		return filename;
	}

	public static byte[] contentsOfFile(File file) throws IOException {
		BufferedInputStream bis = null;

		try {
			bis = new BufferedInputStream(new FileInputStream(file));

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			byte[] buff = new byte[1024 * 100];
			int numRead = -1;
			while ((numRead = bis.read(buff)) != -1) {
				baos.write(buff, 0, numRead);
			}

			return baos.toByteArray();
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
			} catch (IOException e) {
				FileUtils.LOG.debug("Error closing file", e);
			}
		}
	}

	public static void writeToFile(File file, byte[] contents) throws IOException {
		FileOutputStream fis = null;

		try {
			fis = new FileOutputStream(file);
			fis.write(contents, 0, contents.length);
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				FileUtils.LOG.debug("Error closing file", e);
			}
		}
	}

	/**
	 * Get filename Extension
	 * 
	 * @param f
	 * @return
	 */
	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}

		if (ext == null) {
			return "";
		}
		return ext;
	}

	/**
	 * Get filename Extension
	 * 
	 * @param f
	 * @return
	 */
	public static String getFilenameWithoutExtension(File f) {
		return FileUtils.getFilenameWithoutExtension(f.getName());
	}

	/**
	 * Get filename Extension
	 * 
	 * @param f
	 * @return
	 */
	public static String getFilenameWithoutExtension(String completeFileName) {
		String filename = null;
		int i = completeFileName.lastIndexOf('.');

		if (i > 0 && i < completeFileName.length() - 1) {
			filename = completeFileName.substring(0, i);
		}

		if (filename == null) {
			return "";
		}
		return filename;
	}

	/*
	 * Gets the MIME Type of the file based on name
	 */
	public static String detectMimeType(String fileName) {

		String extension = FilenameUtils.getExtension(fileName);

		switch (extension) {
			case "xls":
				return MIME_MS_EXCEL;
			case "xlsx":
				return MIME_MS_EXCEL;
			case "zip":
				return MIME_ZIP;
			case "pdf":
				return MIME_PDF;
			default:
				return MIME_DEFAULT;
		}

	}

}
