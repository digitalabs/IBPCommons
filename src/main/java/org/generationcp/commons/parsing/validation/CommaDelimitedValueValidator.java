
package org.generationcp.commons.parsing.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class CommaDelimitedValueValidator extends ParsingValidator {

	private List<String> acceptedList;
	public static final String GENERIC_INVALID_VALUE_MESSAGE = "common.parse.validation.error.unaccepted.value";

	public CommaDelimitedValueValidator(List<String> acceptedList) {
		this(acceptedList, true);
	}

	public CommaDelimitedValueValidator(List<String> acceptedList, boolean skipIfEmpty) {
		super(skipIfEmpty);

		this.acceptedList = acceptedList;

		this.setValidationErrorMessage(CommaDelimitedValueValidator.GENERIC_INVALID_VALUE_MESSAGE);
	}

	@Override
	public boolean isParsedValueValid(String value, Map<String, Object> additionalParams) {
		if (StringUtils.isEmpty(value)) {
			return this.isSkipIfEmpty();
		} else if (this.acceptedList == null || this.acceptedList.isEmpty()) {
			return false;
		} else {
			List<String> valuesToValidate = new ArrayList<String>();
			if (!this.convertCommaDelimitedValueToList(value, valuesToValidate)) {
				return false;
			}
			return this.acceptedList.containsAll(valuesToValidate);
		}
	}

	public List<String> getAcceptedList() {
		return this.acceptedList;
	}

	public void setAcceptedList(List<String> acceptedList) {
		this.acceptedList = acceptedList;
	}

	private boolean convertCommaDelimitedValueToList(String value, List<String> valuesToValidate) {
		String[] valuesArray = value.split(",");
		for (String parsedValue : valuesArray) {
			String trimmedValue = parsedValue.trim();
			if (valuesToValidate.contains(trimmedValue)) {
				return false;
			}
			valuesToValidate.add(trimmedValue);
		}
		return true;
	}

}
