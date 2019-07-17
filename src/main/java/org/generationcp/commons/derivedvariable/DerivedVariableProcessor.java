package org.generationcp.commons.derivedvariable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.lang3.math.NumberUtils;

public class DerivedVariableProcessor {

	public static class Functions {

		public String concat(final Object... args) {
			final StringBuilder sb = new StringBuilder();
			for (final Object arg : args) {
				sb.append(arg);
			}
			return sb.toString();
		}

		public Double avg(final List<Double>... args) {
			double sum = 0;
			int size = 0;
			for (final List<Double> arg : args) {
				for (final Object val : arg) {
					sum += Double.parseDouble(val.toString());
				}
				size += arg.size();
			}
			return sum / size;
		}
		
		public Integer daysdiff(final Date date1, final Date date2) {
			final boolean isNegative = date2.before(date1);
			long diffInMillies = Math.abs(date2.getTime() - date1.getTime());
		    Integer diffInDays =  Long.valueOf(TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)).intValue();
		    return isNegative? ((-1) * diffInDays) : diffInDays;
		}
	}

	private final JexlEngine engine;
	private final MapContext context;

	/**
	 * The processor should be a request bean as it contains data that should be initialized for each execution<br/>
	 * Terms -> scope: current evaluation<br/>
	 * Data  -> scope: current execution (shared accross evaluations)
	 */
	public DerivedVariableProcessor() {
		final Map<String, Object> functions = new HashMap<>();
		functions.put("fn", new Functions());
		this.engine = new JexlBuilder().namespaces(functions).arithmetic(new FormulaArithmetic()).create();
		this.context = new MapContext();
	}

	/**
	 * Evaluate the formula using an expression engine
	 *
	 * @param terms arguments for the formula
	 */
	public String evaluateFormula(final String formula, final Map<String, Object> terms) {
		final JexlExpression expr = this.engine.createExpression(formula);

		if (terms != null) {
			for (final Map.Entry<String, Object> term : terms.entrySet()) {
				this.context.set(term.getKey(), term.getValue());
			}
		}

		final String result = expr.evaluate(this.context).toString();

		if (NumberUtils.isNumber(result)) {
			return new BigDecimal(result).setScale(4, RoundingMode.HALF_DOWN).stripTrailingZeros().toPlainString();
		}
		return result;
	}

	/**
	 * @param data data for aggregations.
	 */
	public void setData(final Map<String, List<Object>> data) {
		if (data != null) {
			for (final Map.Entry<String, List<Object>> term : data.entrySet()) {
				this.context.set(term.getKey(), term.getValue());
			}
		}
	}

}
