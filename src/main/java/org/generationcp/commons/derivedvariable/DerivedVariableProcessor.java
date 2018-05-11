package org.generationcp.commons.derivedvariable;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DerivedVariableProcessor {

	public static class Functions {

		@SuppressWarnings("unused")
		public String concat(final String... args) {
			final StringBuilder sb = new StringBuilder();
			for (String arg : args) {
				sb.append(arg);
			}
			return sb.toString();
		}
	}

	private static final String TERM_INSIDE_BRACES_REGEX = "\\{(.*?)\\}";

	private final JexlEngine engine;
	private final MapContext context;

	public DerivedVariableProcessor() {
		Map<String, Object> functions = new HashMap<>();
		functions.put("f", new Functions());
		this.engine = new JexlBuilder().namespaces(functions).create();
		this.context = new MapContext();
	}

	/**
	 * @param formula String
	 * @return Map of <String,String>
	 *
	 *         Extract term names from formula then store them in a map with null as the default value
	 */
	public Map<String, Object> extractTermsFromFormula(String formula) {
		Map<String, Object> inputVariables = new HashMap<>();
		Pattern pattern = Pattern.compile(DerivedVariableProcessor.TERM_INSIDE_BRACES_REGEX);
		Matcher matcher = pattern.matcher(formula);
		while (matcher.find()) {
			String term = this.removeAllInvalidCharacters(matcher.group(1));
			term = this.removeWhitespace(term);
			inputVariables.put(term, null);
		}
		return inputVariables;
	}

	/**
	 * @param text String
	 * @return String text with no invalid characters
	 *
	 *         Remove invalid characters from text: percent, double quotes
	 */
	public String removeAllInvalidCharacters(final String text) {
		String newText = text;
		if (newText != null) {
			newText = newText.replaceAll("%", "");
			newText = newText.replaceAll("\"", "");
		}
		return newText;
	}

	public String removeWhitespace(final String text) {
		if (text != null) {
			return text.replaceAll("\\s", "");
		}
		return "";
	}

	/**
	 * @param terms Map<String, String>
	 * @param measurementRow MeasurementRow
	 *
	 *        Update values of terms from the measurement
	 */
	public void fetchTermValuesFromMeasurement(Map<String, Object> terms, MeasurementRow measurementRow) {
		if (measurementRow != null && measurementRow.getDataList() != null) {
			for (MeasurementData measurementData : measurementRow.getDataList()) {
				String term = this.removeAllInvalidCharacters(measurementData.getLabel());
				term = this.removeWhitespace(term);
				if (terms.containsKey(term)) {
					terms.put(term, this.getMeasurementValue(measurementData));
				}
			}
		}
	}

	/**
	 * @param measurementData MeasurementData
	 * @return String value of measurementData
	 *
	 *         Update values of terms from the measurement
	 */
	private Object getMeasurementValue(MeasurementData measurementData) {
		String value = measurementData.getcValueId();
		if (value == null) {
			value = measurementData.getValue();
		}
		return value;
	}

	/**
	 * @param formula String
	 * @return String formula with no curly braces
	 *
	 *         Replace curly braces in formula
	 */
	public String replaceBraces(String formula) {
		String updatedFormula = formula;
		if (updatedFormula != null) {
			updatedFormula = updatedFormula.replaceAll("\\{", "");
			updatedFormula = updatedFormula.replaceAll("\\}", "");
		}
		return updatedFormula;
	}

	/**
	 * @param formula String
	 * @param terms Map<String,String>
	 * @return result of evaluating the formula from term values
	 *
	 *         Evaluate formula from the value of input variables
	 */
	public String evaluateFormula(String formula, Map<String, Object> terms) {
		String newFormula = this.formatFormula(formula);

		JexlExpression expr = this.engine.createExpression(newFormula);
		for (Map.Entry<String, Object> term : terms.entrySet()) {
			this.context.set(term.getKey(), term.getValue());
		}

		String result = expr.evaluate(this.context).toString();

		if (NumberUtils.isNumber(result)) {
			return new BigDecimal(result).setScale(4, RoundingMode.HALF_DOWN).stripTrailingZeros().toPlainString();
		}
		return result;
	}

	private String formatFormula(String formula) {
		String newFormula = this.replaceBraces(formula);
		newFormula = this.removeAllInvalidCharacters(newFormula);
		return newFormula;
	}

	/**
	 * @param formula String
	 * @param measurementRow MeasurementRow
	 * @return String result of evaluating the formula from measurementRow
	 *
	 *         Get the value of the derived variable from a formula and values of input variables
	 */
	public String getDerivedVariableValue(String formula, MeasurementRow measurementRow) {
		Map<String, Object> terms = this.extractTermsFromFormula(formula);
		this.fetchTermValuesFromMeasurement(terms, measurementRow);
		return this.evaluateFormula(formula, terms);
	}
}
