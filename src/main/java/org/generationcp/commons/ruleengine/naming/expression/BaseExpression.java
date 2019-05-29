package org.generationcp.commons.ruleengine.naming.expression;

import org.generationcp.commons.ruleengine.naming.expression.Expression;

public abstract class BaseExpression implements Expression {
    protected void replaceExpressionWithValue(StringBuilder container, String value) {
        int startIndex = container.toString().toUpperCase().indexOf(getExpressionKey());
        int endIndex = startIndex + getExpressionKey().length();

        String replaceValue = value == null ? "" : value;
        container.replace(startIndex, endIndex, replaceValue);
    }
}