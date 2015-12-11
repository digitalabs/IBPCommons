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
			"Resolvable row-column design"), ALPHA_LATTICE("Alpha lattice");

	private final String name;

	private DesignType(final String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
