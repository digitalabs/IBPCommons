
package org.generationcp.commons.ruleengine.cross;

import org.generationcp.commons.ruleengine.ProcessCodeOrderedRule;
import org.generationcp.commons.ruleengine.RuleException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.springframework.stereotype.Component;

@Component
public class RecurringParentSuffixRule extends ProcessCodeOrderedRule<CrossingRuleExecutionContext> {

	static final String KEY = "RECURRINGPARENTSUFFIX";
	static final String MALE_RECURRENT_SUFFIX = "M";
	static final String FEMALE_RECURRENT_SUFFIX = "F";
	static final String PROCESS_CODE = "[RCRPRNT]";

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Object runRule(final CrossingRuleExecutionContext context) throws RuleException {
		try {
			final int computation = context.getPedigreeDataManager().calculateRecurrentParent(context.getMaleGid(), context.getFemaleGid());

			String output = context.getCurrentCrossName() == null ? "" : context.getCurrentCrossName();
			if (PedigreeDataManager.FEMALE_RECURRENT == computation) {
				output += FEMALE_RECURRENT_SUFFIX;
			} else if (PedigreeDataManager.MALE_RECURRENT == computation) {
				output += MALE_RECURRENT_SUFFIX;
			}

			context.setCurrentCrossName(output);

			return output;
		} catch (MiddlewareQueryException e) {
			throw new RuleException(e.getMessage(), e);
		}

	}

	@Override
	public String getProcessCode() {
		return PROCESS_CODE;
	}
}
