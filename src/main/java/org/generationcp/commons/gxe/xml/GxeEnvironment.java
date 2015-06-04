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
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class GxeEnvironment implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private List<GxeEnvironmentLabel> labels;

	@XmlAttribute
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "Label")
	public List<GxeEnvironmentLabel> getLabels() {
		return this.labels;
	}

	public void setLabel(List<GxeEnvironmentLabel> labels) {
		this.labels = labels;
	}

}
