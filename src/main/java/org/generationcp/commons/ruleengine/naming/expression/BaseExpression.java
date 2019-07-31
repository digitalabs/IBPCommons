package org.generationcp.commons.ruleengine.naming.expression;

import org.generationcp.commons.ruleengine.ExpressionUtils;

public abstract class BaseExpression implements Expression {

    protected void replaceExpressionWithValue(final StringBuilder container, final String value) {
        ExpressionUtils.replaceExpressionWithValue(this.getExpressionKey(), container, value);
    }
}