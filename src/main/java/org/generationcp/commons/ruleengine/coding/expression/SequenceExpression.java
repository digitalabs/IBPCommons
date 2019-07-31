package org.generationcp.commons.ruleengine.coding.expression;

import org.generationcp.commons.service.GermplasmNamingService;
import org.generationcp.middleware.pojos.naming.NamingConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SequenceExpression extends BaseCodingExpression {

	// Insert double black slash since we're replacing by regular expressions
	public static final String KEY = "\\[SEQUENCE\\]";

	@Autowired
	protected GermplasmNamingService germplasmNamingService;

	// This setter is only used to inject this service only in test
	public void setGermplasmNamingService(final GermplasmNamingService germplasmNamingService) {
		this.germplasmNamingService = germplasmNamingService;
	}

	@Override
	public void apply(final List<StringBuilder> values, final String capturedText, final NamingConfiguration namingConfiguration) {
		final String prefix = namingConfiguration.getPrefix();
		for (final StringBuilder container : values) {
			this.generateNextNameInSequence(prefix, container, 1);
		}
	}

	void generateNextNameInSequence(final String prefix, final StringBuilder container, final Integer numberOfDigits) {
		final Integer lastUsedSequence = this.germplasmNamingService.getNextNumberAndIncrementSequence(prefix);
		final String numberString = this.germplasmNamingService.getNumberWithLeadingZeroesAsString(lastUsedSequence, numberOfDigits);
		this.replaceRegularExpressionKeyWithValue(container, numberString);
	}

	@Override
	public String getExpressionKey() {
		return SequenceExpression.KEY;
	}



}
