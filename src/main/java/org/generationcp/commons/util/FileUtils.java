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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {

	private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

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

}
