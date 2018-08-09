package org.generationcp.commons.derivedvariable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FormulaArithmeticTest {
    private FormulaArithmetic formulaArithmetic;

    @Before
    public void setUp() {
        this.formulaArithmetic = new FormulaArithmetic();
    }

    @Test
    public void testDivide() {
        final Object result = this.formulaArithmetic.divide(6, 4);
        Assert.assertEquals("1.5", result.toString());
    }
}
