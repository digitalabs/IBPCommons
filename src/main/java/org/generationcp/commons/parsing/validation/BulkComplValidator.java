
package org.generationcp.commons.parsing.validation;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class BulkComplValidator extends ParsingValidator {

	public static final String BULK_COMPL_ERROR_KEY = "error.import.bulk.compl.invalid.value";
	private static final String VALID_VALUE = "Y";
	private static final String BULK_WITH_VALUE_KEY = "BULK_WITH_VALUE";

	private int bulkComplColumnIndex;
	private int bulkWithColumnIndex;

	public BulkComplValidator(boolean skipIfEmpty) {
		super(true);
	}

	public BulkComplValidator(int bulkComplColumnIndex, int bulkWithColumnIndex) {
		super(true);
		this.bulkComplColumnIndex = bulkComplColumnIndex;
		this.bulkWithColumnIndex = bulkWithColumnIndex;
		this.setValidationErrorMessage(BulkComplValidator.BULK_COMPL_ERROR_KEY);
	}

	public boolean isParsedValueValid(String bulkCompValue, String bulkWithValue) {
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

	public int getBulkComplColumnIndex() {
		return this.bulkComplColumnIndex;
	}

	public void setBulkComplColumnIndex(int bulkComplColumnIndex) {
		this.bulkComplColumnIndex = bulkComplColumnIndex;
	}

	public int getBulkWithColumnIndex() {
		return this.bulkWithColumnIndex;
	}

	public void setBulkWithColumnIndex(int bulkWithColumnIndex) {
		this.bulkWithColumnIndex = bulkWithColumnIndex;
	}

	@Override
	public boolean isParsedValueValid(String bulkComplValue, Map<String, Object> additionalParams) {
		String bulkWithValue = (String) additionalParams.get(BulkComplValidator.BULK_WITH_VALUE_KEY);
		return this.isParsedValueValid(bulkComplValue, bulkWithValue);
	}

	public static Map<String, Object> createAdditionalParams(String bulkWithValue) {
		Map<String, Object> additionalParams = new HashMap<String, Object>();
		additionalParams.put(BulkComplValidator.BULK_WITH_VALUE_KEY, bulkWithValue);
		return additionalParams;
	}

}
