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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Heritability")
public class Heritabilities implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Heritability> heritabilities;

	public void add(Heritability heritability) {

		if (this.getHeritabilities() == null) {
			this.setHeritabilities(new ArrayList<Heritability>());
			this.getHeritabilities().add(heritability);
		} else {
			this.getHeritabilities().add(heritability);
		}
	}

	@XmlElement(name = "H2")
	public List<Heritability> getHeritabilities() {
		return this.heritabilities;
	}

	public void setHeritabilities(List<Heritability> heritabilities) {
		this.heritabilities = heritabilities;
	}

}
