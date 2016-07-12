/***************************************************************
 * F * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 * @author Kevin L. Manansala
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of
 * Part F of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 **************************************************************/

package org.generationcp.commons.util;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.Application;
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

	private String downloadFileName;

	public FileDownloadResource(File sourceFile, Application application) {
		super(sourceFile, application);
	}

	public FileDownloadResource(File sourceFile, String downloadFileName, Application application) {
		super(sourceFile, application);
		this.downloadFileName = FileUtils.sanitizeFileName(downloadFileName);
	}

	@Override
	public String getFilename() {
		return downloadFileName == null ? super.getFilename() : downloadFileName;
	}

	@Override
	public DownloadStream getStream() {
		final DownloadStream ds = super.getStream();
		final String mimeType = FileUtils.detectMimeType(this.getFilename());

		ds.setParameter("Content-Type", String.format("%s;charset=utf-8", mimeType));
		ds.setParameter("Content-Disposition", String.format("attachment; filename=\"%s\"; filename*=utf-8''%s", this.getFilename(),
				FileUtils.encodeFilenameForDownload(this.getFilename())));

		return ds;
	}

}
