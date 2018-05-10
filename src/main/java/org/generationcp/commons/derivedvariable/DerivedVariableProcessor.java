package org.generationcp.commons.derivedvariable;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DerivedVariableProcessor {

	private static final String TERM_INSIDE_BRACES_REGEX = "\\{(.*?)\\}";

	private static final String CONCAT = "concat";


	public DerivedVariableProcessor() {
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
		List<Argument> args = Lists.transform(new ArrayList<>(terms.entrySet()), new Function<Map.Entry<String, Object>, Argument>() {

			@Nullable
			@Override
			public Argument apply(@Nullable final Map.Entry<String, Object> term) {
				return new Argument(term.getKey() + "=" + term.getValue());
			}
		});

		double result = new Expression(newFormula, args.toArray(new Argument[0])).calculate();
		return new BigDecimal(result).setScale(4, RoundingMode.HALF_DOWN).stripTrailingZeros().toPlainString();
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
