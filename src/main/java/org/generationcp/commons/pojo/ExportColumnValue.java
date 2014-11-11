package org.generationcp.commons.pojo;

public class ExportColumnValue {
	private Integer id;
	private String value;
	
	public ExportColumnValue() {
		super();
	
	}
	
	public ExportColumnValue(Integer id, String value) {
		super();
		this.id = id;
		this.value = value;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
