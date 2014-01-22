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
import javax.xml.bind.annotation.XmlElement;

public class Pipeline implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String type = "SEA";
	private DataConfiguration dataConfiguration;

	@XmlAttribute
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlElement(name = "DataConfiguration")
	public DataConfiguration getDataConfiguration() {
		return dataConfiguration;
	}

	public void setDataConfiguration(DataConfiguration dataConfiguration) {
		this.dataConfiguration = dataConfiguration;
	}
	
}