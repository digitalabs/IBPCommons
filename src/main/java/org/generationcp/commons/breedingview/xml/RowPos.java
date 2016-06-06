/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 *
 * @author Aldrin Batac
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of
 * Part F of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 **************************************************************/

package org.generationcp.commons.breedingview.xml;

import javax.xml.bind.annotation.XmlAttribute;
import java.io.Serializable;

public class RowPos implements Serializable {

	private static final long serialVersionUID = 286337837811318046L;

	private String name;

	@XmlAttribute
	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

}
