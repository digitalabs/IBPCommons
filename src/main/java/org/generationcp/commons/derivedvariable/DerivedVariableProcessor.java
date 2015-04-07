package org.generationcp.commons.derivedvariable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.mvel2.MVEL;

public class DerivedVariableProcessor {

	private static final String TERM_INSIDE_BRACES_REGEX = "\\{(.*?)\\}";
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
			inputVariables.put(removeAllWhiteSpaces(matcher.group(1)),null);
		}
		return inputVariables;
	}
	
	/**
	 * @param text String
	 * @return String text with no white spaces
	 * 
	 * Remove whites spaces from text
	 */
	public String removeAllWhiteSpaces(String text) {
		if(text!=null) {
			return text.replaceAll("\\s", "");
		}
		return null;
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
				String term = removeAllWhiteSpaces(measurementData.getLabel());
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
	 * @param formula String
	 * @param terms Map<String,String>
	 * @return
	 * 
	 * Evaluate formula from the value of input variables
	 */
	public String evaluateFormula(String formula, Map<String,String> terms) {
		Object result = MVEL.eval(formula, terms);
		if(result instanceof Integer) {
			return Integer.toString((Integer)result);
		} else if(result instanceof Double) {
			return Double.toString((Double)result);
		} else if(result instanceof String) {
			return (String)result;
		}
		return formula;
	}
	
	/**
	 * @param formula String
	 * @param measurementRow MeasurementRow
	 * @return String result of evaluating the formula from measurementRow
	 * 
	 * Evaluate formula from the value of input variables
	 */
	public String getDerivedVariableValue(String formula, MeasurementRow measurementRow) {
		Map<String,String> terms = extractTermsFromFormula(formula);
		fetchTermValuesFromMeasurement(terms,measurementRow);
		return evaluateFormula(removeCurlyBracesFromFormula(formula),terms);
	}
}
