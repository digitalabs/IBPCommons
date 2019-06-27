
package org.generationcp.commons.ruleengine.naming.expression;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.service.GermplasmNamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.generationcp.commons.pojo.AdvancingSource;

@Component
public class SequenceExpression extends NumberSequenceExpression implements Expression {

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
		if (source.isBulk()) {
			this.applyNumberSequenceForBulking(values, source);
		} else {
			final List<StringBuilder> newNames = new ArrayList<>();

			for (final StringBuilder value : values) {
				if (source.getPlantsSelected() != null && source.getPlantsSelected() > 0) {
					synchronized (SequenceExpression.class) {
						for (int i = 0; i < source.getPlantsSelected(); i++) {
							final StringBuilder newName = new StringBuilder(value);
							final String keyPrefix = value.substring(0, value.indexOf(this.getExpressionKey()));
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
	}

	@Override
	public String getExpressionKey() {
		return SequenceExpression.KEY;
	}
}
