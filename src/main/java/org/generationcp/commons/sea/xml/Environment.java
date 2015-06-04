
package org.generationcp.commons.sea.xml;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

public class Environment implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String trial;
	private String trialno;
	private Boolean active;
	private int id;

	@XmlAttribute
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute
	public String getTrial() {
		return this.trial;
	}

	public void setTrial(String trial) {
		this.trial = trial;
	}

	@XmlAttribute
	public Boolean getActive() {
		return this.active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@XmlTransient
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@XmlTransient
	public String getTrialno() {
		return this.trialno;
	}

	public void setTrialno(String trialno) {
		this.trialno = trialno;
	}

}
