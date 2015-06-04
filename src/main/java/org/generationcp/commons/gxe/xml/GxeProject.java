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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.generationcp.commons.breedingview.xml.BreedingViewProjectType;
import org.generationcp.commons.breedingview.xml.SSAParameters;

@XmlRootElement(name = "BreedingViewProject")
@XmlType(propOrder = {"name", "version", "type", "phenotypic", "ssaParameters"})
public class GxeProject implements Serializable {

	private static final long serialVersionUID = -1125312445342191068L;

	private String name;
	private String version;
	private BreedingViewProjectType type;
	private GxePhenotypic phenotypic;
	private SSAParameters ssaParameters;

	@XmlAttribute
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute
	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@XmlElement(name = "ProjectType")
	public BreedingViewProjectType getType() {
		return this.type;
	}

	public void setType(BreedingViewProjectType type) {
		this.type = type;
	}

	@XmlElement(name = "Phenotypic")
	public GxePhenotypic getPhenotypic() {
		return this.phenotypic;
	}

	public void setPhenotypic(GxePhenotypic phenotypic) {
		this.phenotypic = phenotypic;
	}

	@XmlElement(name = "IBWS")
	public SSAParameters getSsaParameters() {
		return this.ssaParameters;
	}

	public void setSsaParameters(SSAParameters ssaParameters) {
		this.ssaParameters = ssaParameters;
	}
}
