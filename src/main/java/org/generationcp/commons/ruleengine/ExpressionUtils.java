package org.generationcp.commons.ruleengine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ExpressionUtils {

	private ExpressionUtils() {
		// utility class
	}

	public static void replaceExpressionWithValue(final String expressionKey, final StringBuilder container, final String value) {
		final int startIndex = container.toString().toUpperCase().indexOf(expressionKey);
		final int endIndex = startIndex + expressionKey.length();

		final String replaceValue = value == null ? "" : value;
		container.replace(startIndex, endIndex, replaceValue);
	}

	public static void replaceRegularExpressionKeyWithValue (final String expressionKey, final StringBuilder container, final String value) {
		final Pattern pattern = Pattern.compile(expressionKey);
		final Matcher matcher = pattern.matcher(container.toString());
		if (matcher.find()) {
			String replaceValue = value == null ? "" : value;
			container.replace(matcher.start(), matcher.end(), replaceValue);
		}

	}

}
