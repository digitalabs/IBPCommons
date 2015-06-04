
package org.generationcp.commons.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.generationcp.commons.parsing.validation.BulkComplValidator;
import org.generationcp.commons.parsing.validation.ParseValidationMap;
import org.generationcp.commons.parsing.validation.ParsingValidator;
import org.generationcp.commons.util.Util;
import org.generationcp.middleware.util.PoiUtil;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 2/26/2015 Time: 11:20 AM
 */
public abstract class WorkbookRowConverter<T> {

	private Workbook workbook;
	protected int currentIndex;
	private int targetSheetIndex;
	private int columnCount;
	private ParseValidationMap validationMap;
	private String[] columnLabels;

	public WorkbookRowConverter(Workbook workbook, int startingIndex, int targetSheetIndex, int columnCount, String[] columnLabels) {
		this(workbook, startingIndex, targetSheetIndex, columnCount, columnLabels, true);
	}

	public WorkbookRowConverter(Workbook workbook, int startingIndex, int targetSheetIndex, int columnCount, String[] columnLabels,
			boolean strictColumns) {
		this.workbook = workbook;
		this.currentIndex = startingIndex;
		this.targetSheetIndex = targetSheetIndex;
		this.columnCount = columnCount;
		this.columnLabels = columnLabels;

		if (columnLabels == null) {
			throw new IllegalArgumentException("Column labels must not be null");
		} else if (columnCount != columnLabels.length && strictColumns) {
			throw new IllegalArgumentException("There should be a column label for each column to be converted");
		}
	}

	public List<T> convertWorkbookRowsToObject(ContinueExpression continueExpression) throws FileParsingException {
		Map<Integer, String> currentRowValue;
		List<T> valueList = new ArrayList<>();

		do {
			if (this.isRowEmpty(this.targetSheetIndex, this.currentIndex, this.columnCount)) {
				currentRowValue = null;
				continue;
			}

			currentRowValue = new HashMap<>();

			BulkComplValidator bulkComplValidator = this.getBulkComplValidator(this.validationMap, this.columnCount);
			String bulkWithValue = null;
			for (int i = 0; i < this.columnCount; i++) {
				String value = this.getCellStringValue(this.targetSheetIndex, this.currentIndex, i);

				if (value == null) {
					value = "";
				}

				if (bulkComplValidator != null && bulkComplValidator.getBulkWithColumnIndex() == i) {
					bulkWithValue = value;
				}

				if (bulkComplValidator != null && bulkComplValidator.getBulkComplColumnIndex() == i) {
					this.applyValidation(value, BulkComplValidator.createAdditionalParams(bulkWithValue), this.columnLabels[i],
							this.validationMap.getValidations(i));
				} else if (this.validationMap != null && this.validationMap.getValidations(i) != null) {
					this.applyValidation(value, null, this.columnLabels[i], this.validationMap.getValidations(i));
				}

				currentRowValue.put(i, value);
			}

			this.currentIndex++;
			valueList.add(this.convertToObject(currentRowValue));

		} while (continueExpression.shouldContinue(currentRowValue));

		return valueList;
	}

	private BulkComplValidator getBulkComplValidator(ParseValidationMap validatorMap, int columnCount) {
		if (validatorMap != null) {
			for (int i = 0; i < columnCount; i++) {
				List<ParsingValidator> parsingValidators = validatorMap.getValidations(i);
				ParsingValidator parsingValidator = Util.getInstance(parsingValidators, BulkComplValidator.class);
				if (parsingValidator != null) {
					return (BulkComplValidator) parsingValidator;
				}
			}
		}
		return null;
	}

	public void applyValidation(String value, Map<String, Object> additionalParams, String columnLabel,
			List<ParsingValidator> parsingValidators) throws FileParsingException {
		for (ParsingValidator validator : parsingValidators) {
			if (!validator.isParsedValueValid(value, additionalParams)) {
				// +1 is added to the current index since index is 0 based
				throw new FileParsingException(validator.getValidationErrorMessage(), this.getCurrentIndex() + 1, value, columnLabel);
			}
		}
	}

	public int getCurrentIndex() {
		return this.currentIndex;
	}

	public abstract T convertToObject(Map<Integer, String> rowValues) throws FileParsingException;

	public String getCellStringValue(int sheetNo, int rowNo, Integer columnNo) {
		String out = null == columnNo ? "" : PoiUtil.getCellStringValue(this.workbook, sheetNo, rowNo, columnNo);
		return null == out ? "" : out;
	}

	public boolean isRowEmpty(int sheetNo, int rowNo, int maxColumns) {
		return PoiUtil.rowIsEmpty(this.workbook.getSheetAt(sheetNo), rowNo, 0, maxColumns);
	}

	public void setValidationMap(ParseValidationMap validationMap) {
		this.validationMap = validationMap;
	}

	// continue expression used to determine whether converter should continue in parsing row values or not
	public static interface ContinueExpression {

		public boolean shouldContinue(Map<Integer, String> currentRowValue);
	}

	// default implementation, tells the converter to stop when the current row is blank
	public static class ContinueTillBlank implements ContinueExpression {

		@Override
		public boolean shouldContinue(Map<Integer, String> currentRowValue) {
			return currentRowValue != null && !currentRowValue.isEmpty();
		}
	}
}
