/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.commons.sea.xml;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

public class MegaEnvironment implements Serializable{

    private static final long serialVersionUID = 1L;

    private String name;
    private Boolean active;
    
    @XmlAttribute
	public String getName() {
		return name;
	}
	
    public void setName(String name) {
		this.name = name;
	}
	
	@XmlAttribute
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}

}
