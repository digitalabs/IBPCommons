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

@XmlRootElement(name = "Environments")
public class MegaEnvironments implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<MegaEnvironment> megaEnvironments;

	public void add(MegaEnvironment megaEnvironment) {

		if (this.getMegaEnvironments() == null) {
			this.setMegaEnvironments(new ArrayList<MegaEnvironment>());
			this.getMegaEnvironments().add(megaEnvironment);
		} else {
			this.getMegaEnvironments().add(megaEnvironment);
		}

	}

	@XmlElement(name = "MegaEnvironment")
	public List<MegaEnvironment> getMegaEnvironments() {
		return this.megaEnvironments;
	}

	public void setMegaEnvironments(List<MegaEnvironment> environments) {
		this.megaEnvironments = environments;
	}

}
