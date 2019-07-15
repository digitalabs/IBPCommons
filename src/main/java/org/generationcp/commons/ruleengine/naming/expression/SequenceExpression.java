
package org.generationcp.commons.ruleengine.naming.expression;

import org.generationcp.commons.pojo.AdvancingSource;
import org.generationcp.commons.service.GermplasmNamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SequenceExpression extends BaseExpression implements Expression {

	public static final String KEY = "[SEQUENCE]";

	@Autowired
	protected GermplasmNamingService germplasmNamingService;

	// This setter is only used to inject this service only in test
	public void setGermplasmNamingService(final GermplasmNamingService germplasmNamingService) {
		this.germplasmNamingService = germplasmNamingService;
	}

	public SequenceExpression() {
	}

	@Override
	public void apply(final List<StringBuilder> values, final AdvancingSource source, final String capturedText) {

		final List<StringBuilder> newNames = new ArrayList<>();

		for (final StringBuilder value : values) {
			if (source.getPlantsSelected() != null && source.getPlantsSelected() > 0) {
				synchronized (SequenceExpression.class) {
					final int iterationCount = source.isBulk() ? 1 : source.getPlantsSelected();
					for (int i = 0; i < iterationCount; i++) {
						final StringBuilder newName = new StringBuilder(value);
						final String upperCaseValue = value.toString().toUpperCase();
						final String keyPrefix = upperCaseValue.substring(0, upperCaseValue.indexOf(this.getExpressionKey()));
						// Get last sequence number for KeyPrefix with synchronization at class level
						final int lastUsedSequence = this.germplasmNamingService.getNextNumberAndIncrementSequence(keyPrefix);
						this.replaceExpressionWithValue(newName, String.valueOf(lastUsedSequence));
						newNames.add(newName);
					}
				}

			} else {
				this.replaceExpressionWithValue(value, "");
				newNames.add(value);
			}
		}

		values.clear();
		values.addAll(newNames);
	}

	@Override
	public String getExpressionKey() {
		return SequenceExpression.KEY;
	}
}
