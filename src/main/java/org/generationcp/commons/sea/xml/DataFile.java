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
package org.generationcp.commons.sea.xml;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;


/**
 * The Class Fieldbook.
 */
public class DataFile implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4157597335127419264L;

    /** The file. */
    private String name;
    
    /** The summary stats file. */
    private String summarystats;
    
    /**
     * Gets the file.
     *
     * @return the file
     */
    @XmlAttribute
    public String getName() {
        return name;
    }
    
    /**
     * Sets the file.
     *
     * @param file the new file
     */
    public void setName(String file) {
        this.name = file;
    }

    @XmlAttribute
	public String getSummarystats() {
		return summarystats;
	}

	public void setSummarystats(String summarystats) {
		this.summarystats = summarystats;
	}
    
}
