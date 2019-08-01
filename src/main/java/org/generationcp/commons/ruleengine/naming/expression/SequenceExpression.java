
package org.generationcp.commons.ruleengine.naming.expression;

import org.generationcp.commons.pojo.AdvancingSource;
import org.generationcp.commons.ruleengine.ExpressionUtils;
import org.generationcp.commons.service.GermplasmNamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SequenceExpression extends BaseExpression implements Expression {

	// Insert double black slash since we're replacing by regular expressions
	private static final String KEY = "\\[SEQUENCE\\]";

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

						final Pattern pattern = Pattern.compile(this.getExpressionKey());
						final Matcher matcher = pattern.matcher(upperCaseValue);
						if (matcher.find()) {
							final String keyPrefix = upperCaseValue.substring(0, matcher.start());
							// Get last sequence number for KeyPrefix with synchronization at class level
							final int lastUsedSequence = this.germplasmNamingService.getNextNumberAndIncrementSequence(keyPrefix);
							final String numberString = this.germplasmNamingService.getNumberWithLeadingZeroesAsString(lastUsedSequence, this.getNumberOfDigits(value));
							this.replaceRegularExpressionKeyWithValue(newName, numberString);
							newNames.add(newName);
						}

					}
				}

			} else {
				this.replaceRegularExpressionKeyWithValue(value, "");
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

	public Integer getNumberOfDigits(final StringBuilder container) {
		return 1;
	}

	protected void replaceRegularExpressionKeyWithValue(final StringBuilder container, final String value) {
		ExpressionUtils.replaceRegularExpressionKeyWithValue(this.getExpressionKey(), container, value);
	}
}
