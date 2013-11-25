package org.generationcp.commons.sea.xml;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.generationcp.commons.breedingview.xml.Blocks;
import org.generationcp.commons.breedingview.xml.Columns;
import org.generationcp.commons.breedingview.xml.Replicates;
import org.generationcp.commons.breedingview.xml.Rows;

public class Design implements Serializable {

	private String type;
	private Replicates  replicates;
	private Blocks blocks;
	private Rows rows;
	private Columns columns;
	
	@XmlAttribute
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@XmlElement(name ="Replicates")
	public Replicates getReplicates() {
		return replicates;
	}
	public void setReplicates(Replicates replicates) {
		this.replicates = replicates;
	}
	
	@XmlElement(name ="Blocks")
	public Blocks getBlocks() {
		return blocks;
	}
	public void setBlocks(Blocks blocks) {
		this.blocks = blocks;
	}
	
	@XmlElement(name ="Rows")
	public Rows getRows() {
		return rows;
	}
	public void setRows(Rows rows) {
		this.rows = rows;
	}
	
	@XmlElement(name ="Columns")
	public Columns getColumns() {
		return columns;
	}
	public void setColumns(Columns columns) {
		this.columns = columns;
	}
	
	

}
