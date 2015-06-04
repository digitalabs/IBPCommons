/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 * @author Kevin L. Manansala
 *
 *         This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of
 *         Part F of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 **************************************************************/

package org.generationcp.commons.gxe.xml;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

public class GxeEnvironmentLabel implements Serializable {

	private static final long serialVersionUID = 6406971480443840786L;

	private String name;
	private String trial;
	private boolean active;

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
	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
