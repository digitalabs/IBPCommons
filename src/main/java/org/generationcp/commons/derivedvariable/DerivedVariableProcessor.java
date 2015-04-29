package org.generationcp.commons.derivedvariable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.mvel2.MVEL;

public class DerivedVariableProcessor {

	private static final String TERM_INSIDE_BRACES_REGEX = "\\{(.*?)\\}";
	public static final String MVEL_BIG_DECIMAL_POSTFIX = "B";
	private static DerivedVariableProcessor instance;
	
	private DerivedVariableProcessor() {
		//private constructor
	}
	
	public static DerivedVariableProcessor getInstance() {
		if(instance==null) {
			instance = new DerivedVariableProcessor();
		}
		return instance;
	}
	/**
	 * @param formula String
	 * @return Map of <String,String>
	 * 
	 * Extract term names from formula then store them in a map with null as the default value
	 */
	public Map<String,String> extractTermsFromFormula(String formula) {
		Map<String,String> inputVariables = new HashMap<String, String>();
		Pattern pattern = Pattern.compile(TERM_INSIDE_BRACES_REGEX);
		Matcher matcher = pattern.matcher(formula);
		while(matcher.find()) {
			inputVariables.put(removeAllInvalidCharacters(matcher.group(1)),null);
		}
		return inputVariables;
	}
	
	/**
	 * @param text String
	 * @return String text with no invalid characters
	 * 
	 * Remove invalid characters from text: whites spaces, percent, double quotes
	 */
	public String removeAllInvalidCharacters(String text) {
		String newText = text;
		if(newText!=null) {
			newText = newText.replaceAll("\\s", "");
			newText = newText.replaceAll("%", "");
			newText = newText.replaceAll("\"", "");
		}
		return newText;
	}

	/**
	 * @param terms Map<String, String>
	 * @param measurementRow MeasurementRow
	 * 
	 * Update values of terms from the measurement
	 */
	public void fetchTermValuesFromMeasurement(Map<String, String> terms, MeasurementRow measurementRow) {
		if(measurementRow!=null && measurementRow.getDataList()!=null) {
			for (MeasurementData measurementData : measurementRow.getDataList()) {
				String term = removeAllInvalidCharacters(measurementData.getLabel());
				if(terms.containsKey(term)) {
					terms.put(term, getMeasurementValue(measurementData));
				}
			}
		}
	}
	
	
	/**
	 * @param measurementData MeasurementData
	 * @return String value of measurementData
	 * 
	 * Update values of terms from the measurement
	 */
	private String getMeasurementValue(MeasurementData measurementData) {
		String value = measurementData.getcValueId();
		if(value==null) {
			value = measurementData.getValue();
		}
		return value;
	}

	/**
	 * @param formula String
	 * @return String formula with no curly braces
	 * 
	 * Remove curly braces from formula
	 */
	public String removeCurlyBracesFromFormula(String formula) {
		String updatedFormula = formula;
		if(updatedFormula!=null) {
			updatedFormula = updatedFormula.replaceAll("\\{", "");
			updatedFormula = updatedFormula.replaceAll("\\}", "");
		}
		return updatedFormula;
	}
	
	/**
	 * @param value String
	 * @return String new value with the postfix appended after numbers
	 * 
	 * Append postfix to numbers
	 */
	public String addPostfixToNumbers(String value, String postfix) {
		StringBuilder newValue = new StringBuilder();
		Pattern pattern = Pattern.compile("\\d[.\\d]*");
		Matcher matcher = pattern.matcher(value);
		int start = 0;
		while(matcher.find()) {
			int end = matcher.end();
			newValue.append(value.substring(start,end));
			newValue.append(postfix);
			start = end;
		}
		newValue.append(value.substring(start));
		return newValue.toString();
	}

	/**
	 * @param formula String
	 * @param terms Map<String,String>
	 * @return result of evaluating the formula from term values
	 * 
	 * Evaluate formula from the value of input variables
	 */
	public String evaluateFormula(String formula, Map<String,String> terms) {
		String newFormula = formatFormula(formula);
		Object result = MVEL.eval(
				newFormula, terms);
		if(result instanceof BigDecimal) {
			return ((BigDecimal)result).setScale(4,RoundingMode.HALF_DOWN)
					.stripTrailingZeros().toPlainString();
		} else if(result instanceof String) {
			return (String)result;
		}
		return formula;
	}
	
	private String formatFormula(String formula) {
		String newFormula = removeCurlyBracesFromFormula(formula);
		newFormula = removeAllInvalidCharacters(newFormula);
		newFormula = addPostfixToNumbers(newFormula,MVEL_BIG_DECIMAL_POSTFIX);
		return newFormula;
	}

	/**
	 * @param formula String
	 * @param measurementRow MeasurementRow
	 * @return String result of evaluating the formula from measurementRow
	 * 
	 * Get the value of the derived variable from a formula and values of input variables
	 */
	public String getDerivedVariableValue(String formula, MeasurementRow measurementRow) {
		Map<String,String> terms = extractTermsFromFormula(formula);
		fetchTermValuesFromMeasurement(terms,measurementRow);
		return evaluateFormula(formula,terms);
	}
}
