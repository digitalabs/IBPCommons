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