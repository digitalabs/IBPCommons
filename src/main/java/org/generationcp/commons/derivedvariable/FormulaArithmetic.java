package org.generationcp.commons.derivedvariable;
import org.apache.commons.jexl3.*;

public class FormulaArithmetic extends JexlArithmetic {
    public FormulaArithmetic(final boolean strict) {
        super(strict);
    }

    public FormulaArithmetic() {
        this(true);
    }

    @Override
    public Object divide(final Object left, final Object right) {
        return super.divide(this.toDouble(left), right);
    }
}
