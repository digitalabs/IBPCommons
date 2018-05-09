package org.generationcp.commons.derivedvariable;

import org.apache.commons.lang3.StringUtils;

/**
 * {@link DerivedVariableProcessor} utility functions
 */
public abstract class DerivedVariableFunctions {

	public static String concat(final String... args) {
		final StringBuilder sb = new StringBuilder();
		for (String arg : args) {
			sb.append(arg);
		}
		return sb.toString();
	}
}
