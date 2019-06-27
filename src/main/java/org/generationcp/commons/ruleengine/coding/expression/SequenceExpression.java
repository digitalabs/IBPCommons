package org.generationcp.commons.ruleengine.coding.expression;

import org.generationcp.commons.service.GermplasmNamingService;
import org.generationcp.middleware.pojos.naming.NamingConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SequenceExpression extends BaseCodingExpression {

	public static final String KEY = "[SEQUENCE]";

	@Autowired
	protected GermplasmNamingService germplasmNamingService;

	// This setter is only used to inject this service only in test
	public void setGermplasmNamingService(final GermplasmNamingService germplasmNamingService) {
		this.germplasmNamingService = germplasmNamingService;
	}

	@Override
	public void apply(List<StringBuilder> values, final String capturedText, final NamingConfiguration namingConfiguration) {

		final String prefix = namingConfiguration.getPrefix();
		for (StringBuilder container : values) {
			final int lastUsedSequence = this.germplasmNamingService.getNextNumberAndIncrementSequence(prefix);
			replaceExpressionWithValue(container, String.valueOf(lastUsedSequence));
		}
	}

	@Override
	public String getExpressionKey() {
		return SequenceExpression.KEY;
	}

}
