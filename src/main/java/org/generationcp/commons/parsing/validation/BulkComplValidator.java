
package org.generationcp.commons.parsing.validation;

import org.apache.commons.lang.StringUtils;

import java.util.Map;

public class BulkComplValidator extends ParsingValidator {

	private static final String BULK_COMPL_ERROR_KEY = "error.import.bulk.compl.invalid.value";
	private static final String VALID_VALUE = "Y";


	public BulkComplValidator(final boolean skipIfEmpty) {
		super(skipIfEmpty);
	}

	public BulkComplValidator(final int bulkWithColumnIndex) {
		super(true);
		this.setPairedColumnIndex(bulkWithColumnIndex);
		this.setValidationErrorMessage(BulkComplValidator.BULK_COMPL_ERROR_KEY);
	}

	public boolean isParsedValueValid(final String bulkCompValue, final String bulkWithValue) {
		if (!StringUtils.isEmpty(bulkCompValue)) {
			if (StringUtils.isEmpty(bulkWithValue)) {
				return false;
			}
			if (!BulkComplValidator.VALID_VALUE.equalsIgnoreCase(bulkCompValue)) {
				return false;
			}
		} else if (!StringUtils.isEmpty(bulkWithValue)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isParsedValueValid(final String bulkComplValue, final Map<String, Object> additionalParams) {
		final String bulkWithValue = (String) additionalParams.get(ParsingValidator.PAIRED_COLUMN_VALUE_KEY);
		return this.isParsedValueValid(bulkComplValue, bulkWithValue);
	}

}
