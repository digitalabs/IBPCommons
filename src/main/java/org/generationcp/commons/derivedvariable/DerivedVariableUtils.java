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

	private static final String TERM_LEFT_DELIMITER = "\\{\\{";
	private static final String TERM_RIGHT_DELIMITER = "\\}\\}";
	public static final String TERM_INSIDE_DELIMITERS_REGEX = TERM_LEFT_DELIMITER + "(.*?)" + TERM_RIGHT_DELIMITER;
	public static final Pattern TERM_INSIDE_DELIMITERS_PATTERN = Pattern.compile(TERM_INSIDE_DELIMITERS_REGEX);

	/**
	 * We use braces externally for clarity and replace them internally as they are map literals in jexl
	 */
	private static final String TERM_INTERNAL_DELIMITER = "__";

	/**
	 * Extract term names from formula
	 */
	public static Map<String, Object> extractTerms(String formula) {
		Map<String, Object> inputVariables = new HashMap<>();
		Matcher matcher = TERM_INSIDE_DELIMITERS_PATTERN.matcher(formula);
		while (matcher.find()) {
			String term = matcher.group(1);
			term = StringUtils.deleteWhitespace(term);
			inputVariables.put(wrapTerm(term), "");
		}
		return inputVariables;
	}

	/**
	 * @see DerivedVariableUtils#extractValues(Map, MeasurementRow, Set)
	 */
	public static void extractValues(Map<String, Object> terms, MeasurementRow measurementRow) {
		extractValues(terms, measurementRow, new HashSet<String>());
	}

	/**
	 * Extract values of terms from the measurement
	 * @param termMissingData list to be filled with term labels with missing data
	 */
	public static void extractValues(Map<String, Object> terms, MeasurementRow measurementRow, final Set<String> termMissingData) {
		if (measurementRow != null && measurementRow.getDataList() != null) {
			for (MeasurementData measurementData : measurementRow.getDataList()) {
				String term = String.valueOf(measurementData.getMeasurementVariable().getTermId());
				term = StringUtils.deleteWhitespace(term);
				term = wrapTerm(term);
				if (terms.containsKey(term)) {
					terms.put(term, getMeasurementValue(measurementData, termMissingData));
				}
			}
		}
	}

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
	 * @return formula with internal delimiters
	 */
	public static String replaceDelimiters(String formula) {
		String updatedFormula = formula;
		if (updatedFormula != null) {
			updatedFormula = updatedFormula.replaceAll(TERM_LEFT_DELIMITER, TERM_INTERNAL_DELIMITER);
			updatedFormula = updatedFormula.replaceAll(TERM_RIGHT_DELIMITER, TERM_INTERNAL_DELIMITER);
		}
		return updatedFormula;
	}

	/**
	 * Wrap term to be used as engine parameter
	 */
	static String wrapTerm(String term) {
		return TERM_INTERNAL_DELIMITER + term + TERM_INTERNAL_DELIMITER;
	}
}
