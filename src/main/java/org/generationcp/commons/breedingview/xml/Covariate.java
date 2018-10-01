package org.generationcp.commons.breedingview.xml;

import javax.xml.bind.annotation.XmlAttribute;
import java.io.Serializable;

public class Covariate implements Serializable {

	private static final long serialVersionUID = 6088046336216001029L;

	private String name;
	private boolean active;

	@XmlAttribute
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute
	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
