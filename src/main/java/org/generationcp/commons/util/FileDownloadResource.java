/***************************************************************
 * F * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * @author Kevin L. Manansala
 * 
 *         This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of
 *         Part F of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 **************************************************************/

package org.generationcp.commons.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.Application;
import com.vaadin.service.FileTypeResolver;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.FileResource;

/**
 * This class has utility methods used by other classes.
 * 
 * @author Dennis Billano
 * 
 */
public class FileDownloadResource extends FileResource {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(FileDownloadResource.class);
	private String filename = "";
	private String userAgent = "";

	/**
	 * This resource can be used to stream a file and let the browser detect it as an attachment
	 * 
	 * @param sourceFile
	 * @param application
	 */
	public FileDownloadResource(File sourceFile, Application application, String userAgent) {
		super(sourceFile, application);
		this.setFilename(super.getFilename());
		this.userAgent = userAgent;
	}

	@Override
	public String getFilename() {
		return this.filename;
	}

	/**
	 * Use this method to set the downloaded filename, if not used, the downloaded filename will be the same as the filename of the
	 * sourceFile
	 * 
	 * @param filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
	public DownloadStream getStream() {
		try {
			final DownloadStream ds =
					new DownloadStream(new FileInputStream(this.getSourceFile()), FileTypeResolver.getMIMEType(this.filename),
							this.getFilename());

			if (this.userAgent.indexOf("MSIE") != -1 || this.userAgent.indexOf("Trident") != -1) {
				// Internet Explorer has problems reading the Content-disposition header if it contains "filename*"
				ds.setParameter("Content-Disposition", "attachment; filename=\"" + this.filename + "\";");
			} else {
				// Those user agents that do not support the RFC 5987 encoding ignore "filename*" when it occurs after "filename".
				ds.setParameter("Content-Disposition", "attachment; filename=\"" + this.filename + "\"; filename*=\"UTF-8''"
						+ this.filename + "\";");
			}

			ds.setCacheTime(this.getCacheTime());
			return ds;
		} catch (final FileNotFoundException e) {
			FileDownloadResource.LOG.error(e.getMessage(), e);
			return null;
		}
	}
}
