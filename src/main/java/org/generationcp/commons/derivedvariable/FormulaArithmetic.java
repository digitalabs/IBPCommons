package org.generationcp.commons.derivedvariable;
import org.apache.commons.jexl3.*;

public class FormulaArithmetic extends JexlArithmetic {
    public FormulaArithmetic(boolean lenient) {
        super(lenient);
    }

    public FormulaArithmetic() {
        this(false);
    }

    @Override
    public Object divide(final Object left, final Object right) {
        return super.divide(((Number) left).doubleValue(), right);
    }
}
