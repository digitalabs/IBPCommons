package org.generationcp.commons.pojo;

public class ExportColumnHeader {

	private Integer id;
	private String name;
	private boolean isDisplay;
	
	public ExportColumnHeader() {
		super();
	}

	public ExportColumnHeader(Integer id, String name, boolean isDisplay) {
		super();
		this.id = id;
		this.name = name;
		this.isDisplay = isDisplay;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDisplay() {
		return isDisplay;
	}

	public void setDisplay(boolean isDisplay) {
		this.isDisplay = isDisplay;
	}
	
	
}
