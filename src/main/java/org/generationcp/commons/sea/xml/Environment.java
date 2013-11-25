package org.generationcp.commons.sea.xml;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

public class Environment implements Serializable{

    private static final long serialVersionUID = 1L;

    private String name;
    private String trial;
    
    @XmlAttribute
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlAttribute
	public String getTrial() {
		return trial;
	}
	public void setTrial(String trial) {
		this.trial = trial;
	}

}
