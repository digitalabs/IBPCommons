
package org.generationcp.commons.pojo;

public class ExportColumnHeader {

	public static Integer GREEN = 1;
	public static Integer BLUE = 2;

	private Integer id;
	private String name;
	private boolean isDisplay;
	private Integer headerColor;

	public ExportColumnHeader() {
		super();
	}

	public ExportColumnHeader(Integer id, String name, boolean isDisplay) {
		super();
		this.id = id;
		this.name = name;
		this.isDisplay = isDisplay;
	}

	public ExportColumnHeader(Integer id, String name, boolean isDisplay, Integer headerColor) {
		super();
		this.id = id;
		this.name = name;
		this.isDisplay = isDisplay;
		this.headerColor = headerColor;
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
		return this.isDisplay;
	}

	public void setDisplay(boolean isDisplay) {
		this.isDisplay = isDisplay;
	}

	public Integer getHeaderColor() {
		return this.headerColor;
	}

	public void setHeaderColor(Integer headerColor) {
		this.headerColor = headerColor;
	}

}
