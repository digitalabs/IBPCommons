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
import org.generationcp.commons.breedingview.xml.ColPos;
import org.generationcp.commons.breedingview.xml.Columns;
import org.generationcp.commons.breedingview.xml.Plot;
import org.generationcp.commons.breedingview.xml.Replicates;
import org.generationcp.commons.breedingview.xml.RowPos;
import org.generationcp.commons.breedingview.xml.Rows;

public class Design implements Serializable {

	private static final long serialVersionUID = 1L;

	private String type;
	private Replicates replicates;
	private Blocks blocks;
	private Rows rows;
	private Columns columns;
	private Plot plot;
	private ColPos colPos;

	@XmlAttribute
	public String getType() {
		return this.type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	@XmlElement(name = "Replicates")
	public Replicates getReplicates() {
		return this.replicates;
	}

	public void setReplicates(final Replicates replicates) {
		this.replicates = replicates;
	}

	@XmlElement(name = "Blocks")
	public Blocks getBlocks() {
		return this.blocks;
	}

	public void setBlocks(final Blocks blocks) {
		this.blocks = blocks;
	}

	@XmlElement(name = "Rows")
	public Rows getRows() {
		return this.rows;
	}

	public void setRows(final Rows rows) {
		this.rows = rows;
	}

	@XmlElement(name = "Columns")
	public Columns getColumns() {
		return this.columns;
	}

	public void setColumns(final Columns columns) {
		this.columns = columns;
	}

	@XmlElement(name = "PlotNo")
	public Plot getPlot() {
		return this.plot;
	}

	public void setPlot(final Plot plot) {
		this.plot = plot;
	}

	@XmlElement(name = "RowPos")
	public RowPos getRowPos() {
		return rowPos;
	}

	public void setRowPos(final RowPos rowPos) {
		this.rowPos = rowPos;
	}

	private RowPos rowPos;

	@XmlElement(name = "ColPos")
	public ColPos getColPos() {
		return colPos;
	}

	public void setColPos(final ColPos colPos) {
		this.colPos = colPos;
	}

}
