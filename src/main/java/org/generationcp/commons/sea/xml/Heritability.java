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
import javax.xml.bind.annotation.XmlTransient;

public class Heritability implements Serializable{

    private static final long serialVersionUID = 1L;

    private String environmentId;
    private String environmentName;
    private String traitId;
    private String traitName;
    private String value;
    
    @XmlAttribute(name = "e")
	public String getEnvironmentId() {
		return environmentId;
	}
	public void setEnvironmentId(String environmentId) {
		this.environmentId = environmentId;
	}
	
	@XmlAttribute(name = "t")
	public String getTraitId() {
		return traitId;
	}
	public void setTraitId(String traitId) {
		this.traitId = traitId;
	}
	
	@XmlAttribute(name = "v")
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@XmlTransient
	public String getEnvironmentName() {
		return environmentName;
	}
	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}
	
	@XmlTransient
	public String getTraitName() {
		return traitName;
	}
	public void setTraitName(String traitName) {
		this.traitName = traitName;
	}

}
