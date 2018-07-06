package org.generationcp.commons.derivedvariable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DerivedVariableUtils {

	private DerivedVariableUtils() {
		// utility class
	}

	/**
	 * We use braces externally for clarity and replace them internally as they are map literals in jexl
	 */
	public static final String TERM_INTERNAL_WRAPPER = "__";
	public static final String TERM_INSIDE_BRACES_REGEX = "\\{\\{(.*?)\\}\\}";
	public static final Pattern TERM_INSIDE_BRACES_PATTERN = Pattern.compile(TERM_INSIDE_BRACES_REGEX);

	public static String wrapTerm(String term) {
		return TERM_INTERNAL_WRAPPER + term + TERM_INTERNAL_WRAPPER;
	}

	/**
	 * Extract term names from formula then store them in a map with null as the default value
	 */
	public static Map<String, Object> extractTerms(String formula) {
		Map<String, Object> inputVariables = new HashMap<>();
		Matcher matcher = TERM_INSIDE_BRACES_PATTERN.matcher(formula);
		while (matcher.find()) {
			String term = matcher.group(1);
			term = removeWhitespace(term);
			inputVariables.put(wrapTerm(term), "");
		}
		return inputVariables;
	}

	/**
	 * Extract values of terms from the measurement
	 */
	public static void extractValues(Map<String, Object> terms, MeasurementRow measurementRow) {
		extractValues(terms, measurementRow, new HashSet<String>());
	}

	/**
	 * Extract values of terms from the measurement
	 * @param termMissingData list of term labels with missing data
	 */
	public static void extractValues(Map<String, Object> terms, MeasurementRow measurementRow, final Set<String> termMissingData) {
		if (measurementRow != null && measurementRow.getDataList() != null) {
			for (MeasurementData measurementData : measurementRow.getDataList()) {
				String term = String.valueOf(measurementData.getMeasurementVariable().getTermId());
				term = removeWhitespace(term);
				term = wrapTerm(term);
				if (terms.containsKey(term)) {
					terms.put(term, getMeasurementValue(measurementData, termMissingData));
				}
			}
		}
	}

	/**
	 * Update values of terms from the measurement
	 */
	private static Object getMeasurementValue(MeasurementData measurementData, final Set<String> termMissingData) {
		String value = null;
		if (!StringUtils.isBlank(measurementData.getcValueId())) {
			value = measurementData.getDisplayValueForCategoricalData().getName();
		}
		if (StringUtils.isBlank(value)) {
			value = measurementData.getValue();
		}
		if (StringUtils.isBlank(value) && termMissingData != null) {
			termMissingData.add(measurementData.getLabel());
		}
		if (NumberUtils.isNumber(value)) {
			return new BigDecimal(value);
		}
		return value;
	}

	/**
	 * Replace curly braces in formula
	 *
	 * @return formula with no curly braces
	 */
	public static String replaceBraces(String formula) {
		String updatedFormula = formula;
		if (updatedFormula != null) {
			updatedFormula = updatedFormula.replaceAll("\\{\\{", TERM_INTERNAL_WRAPPER);
			updatedFormula = updatedFormula.replaceAll("\\}\\}", TERM_INTERNAL_WRAPPER);
		}
		return updatedFormula;
	}

	public static String formatFormula(String formula) {
		String newFormula = replaceBraces(formula);
		return newFormula;
	}

	private static String removeWhitespace(final String text) {
		if (text != null) {
			return text.replaceAll("\\s", "");
		}
		return "";
	}
}
