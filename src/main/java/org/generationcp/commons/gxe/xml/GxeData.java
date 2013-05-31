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
package org.generationcp.commons.gxe.xml;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;


/**
 * The Class Fieldbook.
 */
public class GxeData implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4157597335127419264L;

    /** The file. */
    private String fieldBookFile;
    
    private String csvFile;
    
    /**
     * Gets the file.
     *
     * @return the file
     */
    @XmlAttribute(name="fieldbook")
    public String getFieldBookFile() {
        return fieldBookFile;
    }
    
    /**
     * Sets the file.
     *
     * @param file the new file
     */
    public void setFieldBookFile(String file) {
        this.fieldBookFile = file;
    }

    
    @XmlAttribute(name="file")
	public String getCsvFile() {
		return csvFile;
	}

	
	public void setCsvFile(String csvFile) {
		this.csvFile = csvFile;
	}
    
}
