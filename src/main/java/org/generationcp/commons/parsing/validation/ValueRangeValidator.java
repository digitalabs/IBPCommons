
package org.generationcp.commons.parsing.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 2/26/2015 Time: 5:20 PM
 */
public class ValueRangeValidator extends ParsingValidator {

	private final List<String> acceptedValues;
	public static final String GENERIC_INVALID_VALUE_MESSAGE = "common.parse.validation.error.unaccepted.value";

	public ValueRangeValidator(List<String> acceptedValues) {
		this(acceptedValues, true);
	}

	public ValueRangeValidator(List<String> acceptedValueParam, boolean skipIfEmpty) {
		super(skipIfEmpty);

		this.acceptedValues = new ArrayList<>();
		if (acceptedValueParam != null) {
			this.processAcceptedValues(acceptedValueParam);
		}

		this.setValidationErrorMessage(ValueRangeValidator.GENERIC_INVALID_VALUE_MESSAGE);
	}

	protected void processAcceptedValues(List<String> acceptedValueParam) {
		for (String acceptedValue : acceptedValueParam) {
			if (acceptedValue != null) {
				this.acceptedValues.add(acceptedValue.toUpperCase());
			}
		}
	}

	@Override
	public boolean isParsedValueValid(String value, Map<String, Object> additionalParams) {

		if (StringUtils.isEmpty(value)) {
			return this.isSkipIfEmpty();
		} else if (this.acceptedValues.isEmpty()) {
			return true;
		} else {
			return this.acceptedValues.contains(value.toUpperCase());
		}
	}
}
