package org.generationcp.commons.breedingview.xml;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by Aldrin Batac on 5/18/16.
 */
public class DesignTypeTest {


    @Test
    public void testResolveDesignTypeNameForBreedingView() {

        Assert.assertEquals("When resolving the design type for use in Breeding View, the Incomplete Block Design should become Resolvable Incomplete Block Design", DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName(), DesignType.INCOMPLETE_BLOCK_DESIGN.resolveDesignTypeNameForBreedingView());
        Assert.assertEquals("When resolving the design type for use in Breeding View, the Row-and-Column Design should become Resolvable Row-and-Column Design", DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName(), DesignType.ROW_COLUMN_DESIGN.resolveDesignTypeNameForBreedingView());
        Assert.assertEquals(DesignType.RANDOMIZED_BLOCK_DESIGN.getName(), DesignType.RANDOMIZED_BLOCK_DESIGN.resolveDesignTypeNameForBreedingView());
        Assert.assertEquals(DesignType.P_REP_DESIGN.getName(), DesignType.P_REP_DESIGN.resolveDesignTypeNameForBreedingView());
        Assert.assertEquals(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName(), DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.resolveDesignTypeNameForBreedingView());
        Assert.assertEquals(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName(), DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.resolveDesignTypeNameForBreedingView());

    }

    @Test
    public void testDesignTypeFromString() {

       Assert.assertEquals(DesignType.RANDOMIZED_BLOCK_DESIGN, DesignType.fromString(DesignType.RANDOMIZED_BLOCK_DESIGN.getName()));
       Assert.assertEquals(DesignType.INCOMPLETE_BLOCK_DESIGN, DesignType.fromString(DesignType.INCOMPLETE_BLOCK_DESIGN.getName()));
       Assert.assertEquals(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN, DesignType.fromString(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName()));
       Assert.assertEquals(DesignType.ROW_COLUMN_DESIGN, DesignType.fromString(DesignType.ROW_COLUMN_DESIGN.getName()));
       Assert.assertEquals(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN, DesignType.fromString(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName()));
       Assert.assertEquals(DesignType.P_REP_DESIGN, DesignType.fromString(DesignType.P_REP_DESIGN.getName()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDesignTypeFromStringException() {

        // This will throw an exception because an empty string doesn't match any of the enum constants in DesignType.
        DesignType.fromString("");

    }


}
