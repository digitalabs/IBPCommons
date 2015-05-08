package org.generationcp.commons.ruleengine.stockid;

import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 * Date: 4/9/2015
 * Time: 5:32 PM
 */

@Component
public class UserSpecifiedBreederIdentifierGenerationStrategy implements BreederIdentifierGenerationStrategy{
	@Override public String generateBreederIdentifier() {
		throw new IllegalStateException("User specified breeder identifiers should be set into the RuleExecutionContext "
				+ "programatically before executing this rule");
	}
}
