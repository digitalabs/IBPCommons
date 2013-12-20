package org.generationcp.commons.sea.xml;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.generationcp.commons.breedingview.xml.SSAParameters;

@XmlRootElement(name = "Session")
@XmlType(propOrder = {"ibws", "dataFile", "breedingViewProject"})
public class BreedingViewSession implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private SSAParameters ibws;
	private DataFile dataFile;
	private BreedingViewProject breedingViewProject;
	
	@XmlElement(name = "IBWS")
	public SSAParameters getIbws() {
		return ibws;
	}
	public void setIbws(SSAParameters ibws) {
		this.ibws = ibws;
	}
	
	@XmlElement(name = "Datafile")
	public DataFile getDataFile() {
		return dataFile;
	}
	public void setDataFile(DataFile dataFile) {
		this.dataFile = dataFile;
	}
	
	@XmlElement(name = "BreedingViewProject")
	public BreedingViewProject getBreedingViewProject() {
		return breedingViewProject;
	}
	
	public void setBreedingViewProject(BreedingViewProject breedingViewProject) {
		this.breedingViewProject = breedingViewProject;
	}
	
	
	
}
