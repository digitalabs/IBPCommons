package org.generationcp.commons.derivedvariable;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
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

		@SuppressWarnings("unused")
		public Double avg(List<Double>... args) {
			double sum = 0;
			int size = 0;
			for (List<Double> arg : args) {
				for (Double val : arg) {
					sum += val;
				}
				size += arg.size();
			}
			return sum / size;
		}
	}

	private static final String TERM_INSIDE_BRACES_REGEX = "\\{(.*?)\\}";

	private final JexlEngine engine;
	private final MapContext context;

	public DerivedVariableProcessor() {
		Map<String, Object> functions = new HashMap<>();
		functions.put("fn", new Functions());
		this.engine = new JexlBuilder().namespaces(functions).create();
		this.context = new MapContext();
	}

	/**
	 * Extract term names from formula then store them in a map with null as the default value
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
	 * Remove invalid characters from text: percent, double quotes
	 */
	public String removeAllInvalidCharacters(final String text) {
		String newText = text;
		if (newText != null) {
			newText = newText.replaceAll("%", "");
			newText = newText.replaceAll("\"", "");
		}
		return newText;
	}

	private String removeWhitespace(final String text) {
		if (text != null) {
			return text.replaceAll("\\s", "");
		}
		return "";
	}

	// FIXME this should not be a responsibility of the processor
	/**
	 * Update values of terms from the measurement
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

	// FIXME this should not be a responsibility of the processor
	/**
	 * Update values of terms from the measurement
	 */
	private Object getMeasurementValue(MeasurementData measurementData) {
		String value = measurementData.getcValueId();
		if (value == null) {
			value = measurementData.getValue();
		}
		return value;
	}

	/**
	 * Replace curly braces in formula
	 *
	 * @return formula with no curly braces
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
	 * @see DerivedVariableProcessor#evaluateFormula(String, Map, HashMap)
	 */
	public String evaluateFormula(String formula, Map<String, Object> terms) {
		return this.evaluateFormula(formula, terms, new HashMap<String, List<Object>>());
	}

	/**
	 * Evaluate formula from the value of input variables
	 *
	 * @param formula
	 * @param terms   arguments for the formula
	 * @param data    data for aggregations.
	 * @return result of evaluating the formula from term values
	 */
	public String evaluateFormula(final String formula, final Map<String, Object> terms, final HashMap<String, List<Object>> data) {
		String newFormula = this.formatFormula(formula);

		JexlExpression expr = this.engine.createExpression(newFormula);

		if (terms != null) {
			for (Map.Entry<String, Object> term : terms.entrySet()) {
				this.context.set(term.getKey(), term.getValue());
			}
		}

		if (data != null) {
			for (Map.Entry<String, List<Object>> term : data.entrySet()) {
				this.context.set(term.getKey(), term.getValue());
			}
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

	// FIXME this should not be a responsibility of the processor
	/**
	 * Get the value of the derived variable from a formula and values of input variables
	 *
	 * @return String result of evaluating the formula from measurementRow
	 */
	public String getDerivedVariableValue(String formula, MeasurementRow measurementRow) {
		Map<String, Object> terms = this.extractTermsFromFormula(formula);
		this.fetchTermValuesFromMeasurement(terms, measurementRow);
		return this.evaluateFormula(formula, terms);
	}
}
