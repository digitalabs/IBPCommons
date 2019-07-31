package org.generationcp.commons.ruleengine.coding.expression;

import org.generationcp.middleware.pojos.naming.NamingConfiguration;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaddedSequenceExpression extends SequenceExpression {

	private static final String PADSEQ_BASE = "PADSEQ";
	private static final String PATTERN_KEY = "\\[" + PADSEQ_BASE + "(\\.[0-9]+)*\\]";
	static final Integer DEFAULT_LENGTH = 3;

	@Override
	public void apply(final List<StringBuilder> values, final String capturedText, final NamingConfiguration namingConfiguration) {

		final String prefix = namingConfiguration.getPrefix();
		for (final StringBuilder container : values) {
			// If no digit specified, use default number of digits
			Integer numberOfDigits = PaddedSequenceExpression.DEFAULT_LENGTH;

			final Pattern pattern = Pattern.compile(PaddedSequenceExpression.PATTERN_KEY);
			final Matcher matcher = pattern.matcher(container.toString());
			if (matcher.find()) {
				final String processCode = matcher.group();
				// Look for a digit withing the process code, if present
				final Pattern patternDigit = Pattern.compile("[[0-9]*]");
				final Matcher matcherDigit = patternDigit.matcher(processCode);
				if (matcherDigit.find()) {
					numberOfDigits = Integer.valueOf(matcherDigit.group());
				}
			}
			this.generateNextNameInSequence(prefix, container, numberOfDigits);
		}
	}

	@Override
	public String getExpressionKey() {
		return PaddedSequenceExpression.PATTERN_KEY;
	}

}
