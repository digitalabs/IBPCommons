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
package org.generationcp.commons.breedingview.xml;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;


public class Trait implements Serializable{

    private static final long serialVersionUID = 6088046336216001029L;

    private String name;
    private boolean active;
    private String blues;
    private String blups;
    private int id;
    
    @XmlAttribute
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @XmlAttribute
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }

    @XmlAttribute(name = "BLUEs")
	public String getBlues() {
		return blues;
	}

	public void setBlues(String blues) {
		this.blues = blues;
	}

	@XmlAttribute(name = "BLUPs")
	public String getBlups() {
		return blups;
	}

	public void setBlups(String blups) {
		this.blups = blups;
	}
	
	@XmlTransient
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
    
}
