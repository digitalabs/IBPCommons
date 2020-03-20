
package org.generationcp.commons.parsing;

import org.apache.poi.ss.usermodel.Workbook;
import org.generationcp.commons.parsing.validation.ParseValidationMap;
import org.generationcp.commons.parsing.validation.ParsingValidator;
import org.generationcp.middleware.util.PoiUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 2/26/2015 Time: 11:20 AM
 */
public abstract class WorkbookRowConverter<T> {

	private final Workbook workbook;
	protected int currentIndex;
	private final int targetSheetIndex;
	private final int columnCount;
	private ParseValidationMap validationMap;
	private final String[] columnLabels;

	public WorkbookRowConverter(final Workbook workbook, final int startingIndex, final int targetSheetIndex, final int columnCount, final String[] columnLabels) {
		this(workbook, startingIndex, targetSheetIndex, columnCount, columnLabels, true);
	}

	public WorkbookRowConverter(final Workbook workbook, final int startingIndex, final int targetSheetIndex, final int columnCount, final String[] columnLabels,
			final boolean strictColumns) {
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

	public List<T> convertWorkbookRowsToObject(final ContinueExpression continueExpression) throws FileParsingException {
		Map<Integer, String> currentRowValue;
		final List<T> valueList = new ArrayList<>();

		do {
			if (this.isRowEmpty(this.targetSheetIndex, this.currentIndex, this.columnCount)) {
				currentRowValue = null;
				continue;
			}
			currentRowValue = new HashMap<>();
			for (int i = 0; i < this.columnCount; i++) {
				String value = this.getCellStringValue(this.targetSheetIndex, this.currentIndex, i);

				if (value == null) {
					value = "";
				}

				if (this.validationMap != null && this.validationMap.getValidations(i) != null) {
					this.applyValidation(value, this.columnLabels[i], this.validationMap.getValidations(i), currentRowValue);
				}

				currentRowValue.put(i, value);
			}

			this.currentIndex++;
			valueList.add(this.convertToObject(currentRowValue));

		} while (continueExpression.shouldContinue(currentRowValue));

		return valueList;
	}

	private void applyValidation(final String value, final String columnLabel,
			final List<ParsingValidator> parsingValidators, final Map<Integer, String> currentRowValue) throws FileParsingException {
		for (final ParsingValidator validator : parsingValidators) {
			final Map<String, Object> additionalParams = validator.getPairedColumnIndex() != null ?
				Collections.singletonMap(ParsingValidator.PAIRED_COLUMN_VALUE_KEY, currentRowValue.get(validator.getPairedColumnIndex())) :
				Collections.emptyMap();
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

	public String getCellStringValue(final int sheetNo, final int rowNo, final Integer columnNo) {
		final String out = null == columnNo ? "" : PoiUtil.getCellStringValue(this.workbook, sheetNo, rowNo, columnNo);
		return null == out ? "" : out;
	}

	public boolean isRowEmpty(final int sheetNo, final int rowNo, final int maxColumns) {
		return PoiUtil.rowIsEmpty(this.workbook.getSheetAt(sheetNo), rowNo, 0, maxColumns);
	}

	public void setValidationMap(final ParseValidationMap validationMap) {
		this.validationMap = validationMap;
	}

	// continue expression used to determine whether converter should continue in parsing row values or not
	public interface ContinueExpression {

		boolean shouldContinue(Map<Integer, String> currentRowValue);
	}

	// default implementation, tells the converter to stop when the current row is blank
	public static class ContinueTillBlank implements ContinueExpression {

		@Override
		public boolean shouldContinue(final Map<Integer, String> currentRowValue) {
			return currentRowValue != null && !currentRowValue.isEmpty();
		}
	}
}
