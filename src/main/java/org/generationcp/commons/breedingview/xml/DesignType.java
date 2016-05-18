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

package org.generationcp.commons.breedingview.xml;

public enum DesignType {
	RANDOMIZED_BLOCK_DESIGN("Randomized block design"), INCOMPLETE_BLOCK_DESIGN("Incomplete block design"), RESOLVABLE_INCOMPLETE_BLOCK_DESIGN(
			"Resolvable incomplete block design"), ROW_COLUMN_DESIGN("Row-column design"), RESOLVABLE_ROW_COLUMN_DESIGN(
			"Resolvable row-column design"), P_REP_DESIGN("P-rep design");

	private final String name;

	private DesignType(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	/**
	 * Gets the design type by name.
	 * @param name
	 * @return
     */
	public static DesignType getDesignTypeByName(String name) {

		for (DesignType designType: DesignType.values()){
			if (designType.getName().equals(name)) {
				return designType;
			}
		}

		// if there is no match found in enum constants, throw an exception
		throw new IllegalArgumentException("No DesignType constant found for name: \"" + name + "\"");
	}

	/**
	 * Gets the design type name to be used in Breeding View application.
	 * @return
     */
	public String resolveDesignTypeNameForBreedingView() {

		if (this.name.equals(DesignType.INCOMPLETE_BLOCK_DESIGN.getName())) {
			return DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName();
		} else if (this.name.equals(DesignType.ROW_COLUMN_DESIGN.getName())) {
			return DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName();
		} else {
			return this.name;
		}

	}
}
