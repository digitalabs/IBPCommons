package org.generationcp.commons.breedingview.xml;

import junit.framework.Assert;
import org.junit.Test;

public class DesignTypeTest {

	@Test
	public void testDesignTypeFromString() {

		Assert.assertEquals(DesignType.RANDOMIZED_BLOCK_DESIGN,
				DesignType.getDesignTypeByName(DesignType.RANDOMIZED_BLOCK_DESIGN.getName()));
		Assert.assertEquals(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN,
				DesignType.getDesignTypeByName(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName()));
		Assert.assertEquals(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN,
				DesignType.getDesignTypeByName(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName()));
		Assert.assertEquals(DesignType.P_REP_DESIGN, DesignType.getDesignTypeByName(DesignType.P_REP_DESIGN.getName()));
		Assert.assertEquals(DesignType.AUGMENTED_RANDOMIZED_BLOCK,
				DesignType.getDesignTypeByName(DesignType.AUGMENTED_RANDOMIZED_BLOCK.getName()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDesignTypeFromStringException() {

		// This will throw an exception because an empty string doesn't match any of the enum constants in DesignType.
		DesignType.getDesignTypeByName("");

	}

}
