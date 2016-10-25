/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 *
 * @author Kevin L. Manansala
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of
 * Part F of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 **************************************************************/

package org.generationcp.commons.breedingview.xml;

public enum DesignType {
	RANDOMIZED_BLOCK_DESIGN("Randomized block design"), RESOLVABLE_INCOMPLETE_BLOCK_DESIGN(
			"Resolvable incomplete block design"), RESOLVABLE_ROW_COLUMN_DESIGN("Resolvable row-column design"), P_REP_DESIGN(
			"P-rep design"),
	// Augmented design is just a variation of the Incomplete Block Design type.
	AUGMENTED_RANDOMIZED_BLOCK("Incomplete block design");

	private final String name;

	private DesignType(final String name) {
		this.name = name;
	}

	/**
	 * The name of the design that the system sends to Breeding View. This name is constant and defined by the Breeding View application.
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the design type by name.
	 * @param name
	 * @return
	 */
	public static DesignType getDesignTypeByName(final String name) {

		for (final DesignType designType : DesignType.values()) {
			if (designType.getName().equals(name)) {
				return designType;
			}
		}

		// if there is no match found in enum constants, throw an exception
		throw new IllegalArgumentException("No DesignType constant found for name: \"" + name + "\"");
	}

}
