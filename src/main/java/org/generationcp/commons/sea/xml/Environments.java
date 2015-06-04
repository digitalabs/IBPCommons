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

package org.generationcp.commons.sea.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Environments")
public class Environments implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String trialname;
	private List<Environment> environments;

	@XmlAttribute
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name = "trialname")
	public String getTrialName() {
		return this.trialname;
	}

	public void setTrialName(String trialname) {
		this.trialname = trialname;
	}

	public void add(Environment environment) {

		if (this.getEnvironments() == null) {
			this.setEnvironments(new ArrayList<Environment>());
			this.getEnvironments().add(environment);
		} else {
			this.getEnvironments().add(environment);
		}

	}

	@XmlElement(name = "Environment")
	public List<Environment> getEnvironments() {
		return this.environments;
	}

	public void setEnvironments(List<Environment> environments) {
		this.environments = environments;
	}

}
