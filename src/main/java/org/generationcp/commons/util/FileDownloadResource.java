/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * @author Kevin L. Manansala
 * 
 * This software is licensed for use under the terms of the 
 * GNU General Public License (http://bit.ly/8Ztv8M) and the 
 * provisions of Part F of the Generation Challenge Programme 
 * Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 **************************************************************/
package org.generationcp.commons.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import com.vaadin.Application;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.FileResource;


/**
 * This class has utility methods used by other classes.
 * 
 * @author Dennis Billano
 * 
 */

public class FileDownloadResource extends FileResource{
	
	private static final long serialVersionUID = 1L;
	private String filename = "";
	
	/**
	 * This resource can be used to stream a file and let the browser detect it as an attachment
	 * @param sourceFile
	 * @param application
	 */
	public FileDownloadResource(File sourceFile, Application application) {
		super(sourceFile, application); 
		filename = getFilename();
	}
	
	/**
	 * Use this method to set the downloaded filename, if not used, the downloaded filename will be 
	 * the same as the filename of the sourceFile 
	 * 
	 * @param filename
	 */
	public void setFilename(String filename){
		this.filename = filename;
	}
	
	public DownloadStream getStream() {
		try {
			final DownloadStream ds = new DownloadStream(new FileInputStream(
			getSourceFile()), getMIMEType(), getFilename());
			ds.setParameter("Content-Disposition", "attachment; filename="+filename);
			ds.setCacheTime(getCacheTime());
			return ds;
		} catch (final FileNotFoundException e) {
			return null;
		}
	}
}