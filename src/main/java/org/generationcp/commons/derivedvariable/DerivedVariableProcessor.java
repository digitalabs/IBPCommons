package org.generationcp.commons.derivedvariable;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DerivedVariableProcessor {

	public static class Functions {

		@SuppressWarnings("unused")
		public String concat(final Object... args) {
			final StringBuilder sb = new StringBuilder();
			for (Object arg : args) {
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

	private final JexlEngine engine;
	private final MapContext context;

	public DerivedVariableProcessor() {
		Map<String, Object> functions = new HashMap<>();
		functions.put("fn", new Functions());
		this.engine = new JexlBuilder().namespaces(functions).create();
		this.context = new MapContext();
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
	public String evaluateFormula(final String formula, final Map<String, Object> terms, final Map<String, List<Object>> data) {
		String newFormula = DerivedVariableUtils.formatFormula(formula);

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

}
