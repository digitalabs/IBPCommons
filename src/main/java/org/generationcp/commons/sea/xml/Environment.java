package org.generationcp.commons.sea.xml;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

public class Environment implements Serializable{

    private static final long serialVersionUID = 1L;

    private String name;
    private String trial;
    private String trialno;
    private Boolean active;
    private int id;
    
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
	
	@XmlAttribute
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	@XmlTransient
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	@XmlTransient
	public String getTrialno() {
		return trialno;
	}
	
	
	public void setTrialno(String trialno) {
		this.trialno = trialno;
	}

}
