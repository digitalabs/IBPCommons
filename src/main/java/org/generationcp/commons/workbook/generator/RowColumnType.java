
package org.generationcp.commons.workbook.generator;

public enum RowColumnType {
	LIST_TYPE("LIST HEADER", "LISTNMS", "LISTTYPE"), USER("CONDITION", "", ""), NAME_TYPES("FACTOR", "NAMES",
			"NAME"), SCALES_FOR_INVENTORY_UNITS("INVENTORY", "", ""), ATTRIBUTE_TYPES("VARIATE", "ATRIBUTS",
					"ATTRIBUTE"), PASSPORT_ATTRIBUTE_TYPES("VARIATE", "ATRIBUTS", "PASSPORT");

	String section;
	String ftable;
	String ftype;

	private RowColumnType(final String section, final String ftable, final String ftype) {
		this.section = section;
		this.ftable = ftable;
		this.ftype = ftype;
	}

	public String getSection() {
		return this.section;
	}

	public String getFtable() {
		return this.ftable;
	}

	public String getFtype() {
		return this.ftype;
	}
}
