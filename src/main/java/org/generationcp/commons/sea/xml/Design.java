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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.generationcp.commons.breedingview.xml.Blocks;
import org.generationcp.commons.breedingview.xml.Columns;
import org.generationcp.commons.breedingview.xml.Plot;
import org.generationcp.commons.breedingview.xml.Replicates;
import org.generationcp.commons.breedingview.xml.Rows;

public class Design implements Serializable {

	private static final long serialVersionUID = 1L;

	private String type;
	private Replicates replicates;
	private Blocks blocks;
	private Rows rows;
	private Columns columns;
	private Plot plot;

	@XmlAttribute
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlElement(name = "Replicates")
	public Replicates getReplicates() {
		return this.replicates;
	}

	public void setReplicates(Replicates replicates) {
		this.replicates = replicates;
	}

	@XmlElement(name = "Blocks")
	public Blocks getBlocks() {
		return this.blocks;
	}

	public void setBlocks(Blocks blocks) {
		this.blocks = blocks;
	}

	@XmlElement(name = "Rows")
	public Rows getRows() {
		return this.rows;
	}

	public void setRows(Rows rows) {
		this.rows = rows;
	}

	@XmlElement(name = "Columns")
	public Columns getColumns() {
		return this.columns;
	}

	public void setColumns(Columns columns) {
		this.columns = columns;
	}

	@XmlElement(name = "PlotNo")
	public Plot getPlot() {
		return this.plot;
	}

	public void setPlot(Plot plot) {
		this.plot = plot;
	}

}
