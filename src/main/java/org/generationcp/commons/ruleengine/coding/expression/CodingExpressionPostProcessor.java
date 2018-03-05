package org.generationcp.commons.ruleengine.coding.expression;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class CodingExpressionPostProcessor implements BeanPostProcessor {

	private CodingExpressionFactory codingExpressionFactory;

	@Override
	public Object postProcessBeforeInitialization(final Object o, final String s) throws BeansException {
		return o;
	}

	@Override
	public Object postProcessAfterInitialization(final Object o, final String s) throws BeansException {
		if (o instanceof BaseCodingExpression) {
			codingExpressionFactory.addExpression((Expression) o);
		}
		return o;
	}

	public void setCodingExpressionFactory(final CodingExpressionFactory codingExpressionFactory) {
		this.codingExpressionFactory = codingExpressionFactory;
	}
}
