package org.generationcp.commons.derivedvariable;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DerivedVariableProcessor {

	private static final String TERM_INSIDE_BRACES_REGEX = "\\{(.*?)\\}";
	private static final String TYPES_REGEX = ".*T\\((.*?)\\)";

	public static final String PLACEHOLDER = "terms";
	private static final String CONCAT = "concat";

	private final SpelExpressionParser parser;
	private final StandardEvaluationContext context;

	public DerivedVariableProcessor() {
		this.parser = new SpelExpressionParser();
		this.context = new StandardEvaluationContext();
		try {
			this.context.registerFunction(CONCAT, DerivedVariableFunctions.class.getDeclaredMethod(CONCAT, new Class[] {String[].class}));
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
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
			inputVariables.put(this.removeAllInvalidCharacters(matcher.group(1)), null);
		}
		return inputVariables;
	}

	/**
	 * @param text String
	 * @return String text with no invalid characters
	 *
	 *         Remove invalid characters from text: whites spaces, percent, double quotes
	 */
	public String removeAllInvalidCharacters(final String text) {
		String newText = text;
		if (newText != null) {
			newText = newText.replaceAll("%", "");
			newText = newText.replaceAll("\"", "");
			newText = this.removeArbitraryCodeExecution(newText);
		}
		return newText;
	}

	private String removeArbitraryCodeExecution(final String text) {
		String newText = text;
		while (newText.matches(TYPES_REGEX)) {
			newText = newText.replaceAll(TYPES_REGEX, "");
		}
		return newText;
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
		if (NumberUtils.isNumber(value)) {
			return new BigDecimal(value);
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
			updatedFormula = updatedFormula.replaceAll("\\{", "#" + PLACEHOLDER + "['");
			updatedFormula = updatedFormula.replaceAll("\\}", "']");
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
		this.context.setVariable(PLACEHOLDER, terms);
		String result = this.parser.parseExpression(newFormula).getValue(context, String.class);
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
