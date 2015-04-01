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
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

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
public class FileDownloadResource extends FileResource{
	private static final char[] HEX_CHARS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B',
        'C', 'D', 'E', 'F' };
	private static final long serialVersionUID = 1L;
	private String filename = "";
	private static final Logger LOG = LoggerFactory.getLogger(FileDownloadResource.class);
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
	
	public static String getDownloadFileName(String filename, HttpServletRequest request) {
		String newFilename = filename;
	      try{
	    	  if (request != null && (request.getHeader("User-Agent").indexOf("Chrome") != -1 ||request.getHeader("User-Agent").indexOf("MSIE") != -1 || request.getHeader("User-Agent").indexOf("Trident") != -1)) {
	              URI uri = new URI(null, null, filename, null);
	        	  	newFilename = uri.toASCIIString();
	              return newFilename;
	            }
	          byte[] bytes = filename.getBytes("UTF-8");
	          StringBuilder buff = new StringBuilder(bytes.length << 2);
	          buff.append("=?UTF-8?Q?");
	          for (byte b : bytes) {
	              int unsignedByte = b & 0xFF;
	              buff.append('=').append(HEX_CHARS[unsignedByte >> 4]).append(HEX_CHARS[unsignedByte & 0xF]);
	          }
	          return buff.append("?=").toString();
	      }catch(UnsupportedEncodingException e){
	    	  LOG.error(e.getMessage(), e);
	      } catch (URISyntaxException e) {
	    	  LOG.error(e.getMessage(), e);
	    	  
		}
	      return newFilename;
	  }
	  
	public DownloadStream getStream() {
		try {
			final DownloadStream ds = new DownloadStream(new FileInputStream(
			getSourceFile()), getMIMEType(), getFilename());
			ds.setParameter("Content-Disposition", "attachment; filename="+filename);			
			ds.setCacheTime(getCacheTime());
			return ds;
		} catch (final FileNotFoundException e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}
}