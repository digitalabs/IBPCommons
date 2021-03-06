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
		return this.ibws;
	}

	public void setIbws(SSAParameters ibws) {
		this.ibws = ibws;
	}

	@XmlElement(name = "Datafile")
	public DataFile getDataFile() {
		return this.dataFile;
	}

	public void setDataFile(DataFile dataFile) {
		this.dataFile = dataFile;
	}

	@XmlElement(name = "BreedingViewProject")
	public BreedingViewProject getBreedingViewProject() {
		return this.breedingViewProject;
	}

	public void setBreedingViewProject(BreedingViewProject breedingViewProject) {
		this.breedingViewProject = breedingViewProject;
	}

}
