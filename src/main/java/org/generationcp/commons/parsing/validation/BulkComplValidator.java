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
		setValidationErrorMessage(BULK_COMPL_ERROR_KEY);
	}
	
	public boolean isParsedValueValid(String bulkCompValue, String bulkWithValue) {
		if (!StringUtils.isEmpty(bulkCompValue)) {
			if(StringUtils.isEmpty(bulkWithValue)) {
				return false;
			}
			if(!VALID_VALUE.equals(bulkCompValue)) {
				return false;
			}
		} else if(!StringUtils.isEmpty(bulkWithValue)){
			return false;
		}
		return true;
	}

	public int getBulkComplColumnIndex() {
		return bulkComplColumnIndex;
	}

	public void setBulkComplColumnIndex(int bulkComplColumnIndex) {
		this.bulkComplColumnIndex = bulkComplColumnIndex;
	}

	public int getBulkWithColumnIndex() {
		return bulkWithColumnIndex;
	}

	public void setBulkWithColumnIndex(int bulkWithColumnIndex) {
		this.bulkWithColumnIndex = bulkWithColumnIndex;
	}

	@Override
	public boolean isParsedValueValid(String bulkComplValue,
			Map<String, Object> additionalParams) {
		String bulkWithValue = (String) additionalParams.get(BULK_WITH_VALUE_KEY);
		return isParsedValueValid(bulkComplValue,bulkWithValue);
	}
	
	public static Map<String,Object> createAdditionalParams(String bulkWithValue) {
		Map<String,Object> additionalParams = new HashMap<String, Object>();
		additionalParams.put(BULK_WITH_VALUE_KEY, bulkWithValue);
		return additionalParams;
	}
	
	
}
