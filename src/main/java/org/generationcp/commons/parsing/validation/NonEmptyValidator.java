
package org.generationcp.commons.parsing.validation;

import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 2/26/2015 Time: 5:42 PM
 */
public class NonEmptyValidator extends ParsingValidator {

	public static final String GENERIC_EMPTY_VALUE_MESSAGE = "common.parser.validation.error.empty.value";

	public NonEmptyValidator() {
		super(false);
		this.setValidationErrorMessage(NonEmptyValidator.GENERIC_EMPTY_VALUE_MESSAGE);
	}

	@Override
	public boolean isParsedValueValid(String value, Map<String, Object> additionalParams) {
		return value != null && !value.isEmpty();
	}
}
