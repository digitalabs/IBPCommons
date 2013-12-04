package org.generationcp.commons.sea.xml;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.generationcp.commons.breedingview.xml.Genotypes;

public class DataConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Environments environments;
	private MegaEnvironments megaEnvironments;
	private Traits traits;
	private Design design;
	private Genotypes genotypes;
	private Heritabilities heritabilities;
	private String name;
	
	@XmlElement(name = "Environments")
	public Environments getEnvironments() {
		return environments;
	}
	public void setEnvironments(Environments environments) {
		this.environments = environments;
	}
	
	@XmlElement(name = "Traits")
	public Traits getTraits() {
		return traits;
	}
	public void setTraits(Traits traits) {
		this.traits = traits;
	}
	
	@XmlElement(name = "Design")
	public Design getDesign() {
		return design;
	}
	public void setDesign(Design design) {
		this.design = design;
	}
	
	@XmlElement(name = "Genotypes")
	public Genotypes getGenotypes() {
		return genotypes;
	}
	public void setGenotypes(Genotypes genotypes) {
		this.genotypes = genotypes;
	}
	
	@XmlAttribute
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name = "MegaEnvironments")
	public MegaEnvironments getMegaEnvironments() {
		return megaEnvironments;
	}
	public void setMegaEnvironments(MegaEnvironments megaEnvironments) {
		this.megaEnvironments = megaEnvironments;
	}
	
	@XmlElement(name = "Heritability")
	public Heritabilities getHeritabilities() {
		return heritabilities;
	}
	public void setHeritabilities(Heritabilities heritabilities) {
		this.heritabilities = heritabilities;
	}
	

}
