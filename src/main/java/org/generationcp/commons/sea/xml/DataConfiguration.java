/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.commons.sea.xml;

import org.generationcp.commons.breedingview.xml.Covariate;
import org.generationcp.commons.breedingview.xml.Genotypes;
import org.generationcp.commons.breedingview.xml.Trait;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.io.Serializable;
import java.util.List;

public class DataConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;

	private Environments environments;
	private MegaEnvironments megaEnvironments;
	private List<Trait> traits;
	private List<Covariate> covariates;
	private Design design;
	private Genotypes genotypes;
	private Heritabilities heritabilities;
	private String name;

	@XmlElement(name = "Environments")
	public Environments getEnvironments() {
		return this.environments;
	}

	public void setEnvironments(Environments environments) {
		this.environments = environments;
	}

	@XmlElementWrapper(name = "Traits")
	@XmlElement(name = "Trait")
	public List<Trait> getTraits() {
		return this.traits;
	}

	public void setTraits(List<Trait> traits) {
		this.traits = traits;
	}

	@XmlElementWrapper(name = "Covariates")
	@XmlElement(name = "Covariate")
	public List<Covariate> getCovariates() {
		return covariates;
	}

	public void setCovariates(final List<Covariate> covariates) {
		this.covariates = covariates;
	}

	@XmlElement(name = "Design")
	public Design getDesign() {
		return this.design;
	}

	public void setDesign(Design design) {
		this.design = design;
	}

	@XmlElement(name = "Genotypes")
	public Genotypes getGenotypes() {
		return this.genotypes;
	}

	public void setGenotypes(Genotypes genotypes) {
		this.genotypes = genotypes;
	}

	@XmlAttribute
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "MegaEnvironments")
	public MegaEnvironments getMegaEnvironments() {
		return this.megaEnvironments;
	}

	public void setMegaEnvironments(MegaEnvironments megaEnvironments) {
		this.megaEnvironments = megaEnvironments;
	}

	@XmlElement(name = "Heritability")
	public Heritabilities getHeritabilities() {
		return this.heritabilities;
	}

	public void setHeritabilities(Heritabilities heritabilities) {
		this.heritabilities = heritabilities;
	}

}
