package org.generationcp.commons.ruleengine.naming.expression;


public abstract class BaseExpression implements Expression {
    protected void replaceExpressionWithValue(final StringBuilder container, final String value) {
       final int startIndex = container.toString().toUpperCase().indexOf(getExpressionKey());
       final int endIndex = startIndex + getExpressionKey().length();

       final String replaceValue = value == null ? "" : value;
       container.replace(startIndex, endIndex, replaceValue);
    }
}