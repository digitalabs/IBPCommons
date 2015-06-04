
package org.generationcp.commons.parsing;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.generationcp.commons.parsing.pojo.ImportedCondition;
import org.generationcp.commons.parsing.pojo.ImportedConstant;
import org.generationcp.commons.parsing.pojo.ImportedDescriptionDetails;
import org.generationcp.commons.parsing.pojo.ImportedFactor;
import org.generationcp.commons.parsing.pojo.ImportedVariate;
import org.generationcp.commons.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cyrus on 4/24/15.
 */
public class DescriptionSheetParser<T extends ImportedDescriptionDetails> extends AbstractExcelFileParser<T> {

	public static final int DESCRIPTION_SHEET_NO = 0;
	public static final int CONDITION_ROW_NO = 5;
	public static final int DESCRIPTION_SHEET_COL_SIZE = 8;

	private static final Logger LOG = LoggerFactory.getLogger(DescriptionSheetParser.class);
	public static final String TEMPLATE_LIST_TYPE = "LST";
	public static final String LIST_DATE = "LIST DATE";
	public static final String LIST_TYPE = "LIST TYPE";

	protected enum DescriptionHeaders {
		CONDITION("CONDITION"), DESCRIPTION("DESCRIPTION"), PROPERTY("PROPERTY"), SCALE("SCALE"), METHOD("METHOD"), DATA_TYPE("DATA TYPE"), VALUE(
				"VALUE"), FACTOR("FACTOR"), CONSTANT("CONSTANT"), VARIATE("VARIATE");

		private String label;

		DescriptionHeaders(String label) {
			this.label = label;
		}

		public String getLabel() {
			return this.label;
		}

		public static String[] names() {
			DescriptionHeaders[] values = DescriptionHeaders.values();
			String[] names = new String[values.length];

			for (int i = 0; i < values.length; i++) {
				names[i] = values[i].name();
			}

			return names;
		}
	}

	private final T importedList;

	private int currentRow = 0;
	private final boolean importFileIsValid = true;

	private boolean doParseDetails, doParseConditions, doParseFactors, doParseConstants, doParseVariates;

	public DescriptionSheetParser(T importedList) {
		this.importedList = importedList;
		this.doParseDetails = this.doParseConditions = this.doParseFactors = this.doParseConstants = this.doParseVariates = true;
	}

	public void parseDescriptionSheet() throws FileParsingException, ParseException {
		this.parseDescriptionSheet(this.doParseDetails, this.doParseConditions, this.doParseFactors, this.doParseConstants,
				this.doParseVariates);
	}

	private void parseDescriptionSheet(boolean doParseDetails, boolean doParseConditions, boolean doParseFactors, boolean doParseConstants,
			boolean doParseVariates) throws FileParsingException, ParseException {

		if (doParseDetails) {
			this.parseListDetails();
		}

		if (doParseConditions) {
			this.parseConditions();
		}

		if (doParseFactors) {
			this.parseFactors();
		}

		if (doParseConstants) {
			this.parseConstants();
		}

		if (doParseVariates) {
			this.parseVariate();
		}
	}

	protected void parseListDetails() throws FileParsingException, ParseException {
		String listName = this.getCellStringValue(DescriptionSheetParser.DESCRIPTION_SHEET_NO, 0, 1);
		String listTitle = this.getCellStringValue(DescriptionSheetParser.DESCRIPTION_SHEET_NO, 1, 1);

		String labelId = this.getCellStringValue(DescriptionSheetParser.DESCRIPTION_SHEET_NO, 2, 0);

		int listDateColNo = DescriptionSheetParser.LIST_DATE.equalsIgnoreCase(labelId) ? 2 : 3;
		int listTypeColNo = DescriptionSheetParser.LIST_TYPE.equalsIgnoreCase(labelId) ? 2 : 3;

		Date listDate = DateUtil.parseDate(this.getCellStringValue(DescriptionSheetParser.DESCRIPTION_SHEET_NO, listDateColNo, 1));
		String listType = this.getCellStringValue(DescriptionSheetParser.DESCRIPTION_SHEET_NO, listTypeColNo, 1);

		if (!DescriptionSheetParser.TEMPLATE_LIST_TYPE.equalsIgnoreCase(listType)) {
			throw new FileParsingException("Error parsing details : List type is invalid");
		}

		this.importedList.setName(listName);
		this.importedList.setTitle(listTitle);
		this.importedList.setType(listType);
		this.importedList.setDate(listDate);
	}

	protected void parseConditions() {
		// condition headers start at row = 5 (+ 1 : count starts from 0 )
		this.currentRow = DescriptionSheetParser.CONDITION_ROW_NO;

		if (!this.isConditionHeadersInvalid(DescriptionSheetParser.CONDITION_ROW_NO) && this.importFileIsValid) {
			this.currentRow++;

			while (!this.isRowEmpty(DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow,
					DescriptionSheetParser.DESCRIPTION_SHEET_COL_SIZE)) {
				this.importedList.addImportedCondition(new ImportedCondition(this.getCellStringValue(
						DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 0), this.getCellStringValue(
						DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 1), this.getCellStringValue(
						DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 2), this.getCellStringValue(
						DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 3), this.getCellStringValue(
						DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 4), this.getCellStringValue(
						DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 5), this.getCellStringValue(
						DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 6), ""));

				this.currentRow++;
			}
		}

		while (this.isRowEmpty(DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow,
				DescriptionSheetParser.DESCRIPTION_SHEET_COL_SIZE)) {
			this.currentRow++;
		}
	}

	protected void parseFactors() throws FileParsingException {

		if (!this.isFactorHeadersInvalid(this.currentRow) && this.importFileIsValid) {
			this.currentRow++;

			while (!this.isRowEmpty(DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow,
					DescriptionSheetParser.DESCRIPTION_SHEET_COL_SIZE)) {
				final ImportedFactor factor =
						new ImportedFactor(this.getCellStringValue(DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 0),
								this.getCellStringValue(DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 1),
								this.getCellStringValue(DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 2),
								this.getCellStringValue(DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 3),
								this.getCellStringValue(DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 4),
								this.getCellStringValue(DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 5), "");

				this.importedList.addImportedFactor(factor);

				this.currentRow++;
			}

			this.currentRow++;

		} else {
			throw new FileParsingException("Error parsing on factors header: Incorrect headers for factors.");
		}

		while (this.isRowEmpty(DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow,
				DescriptionSheetParser.DESCRIPTION_SHEET_COL_SIZE)) {
			this.currentRow++;
		}
	}

	protected void parseConstants() throws FileParsingException {
		if (!this.isConstantsHeaderInvalid(this.currentRow) && this.importFileIsValid) {
			this.currentRow++;
			while (!this.isRowEmpty(DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow,
					DescriptionSheetParser.DESCRIPTION_SHEET_COL_SIZE)) {
				this.importedList.addImportedConstant(new ImportedConstant(this.getCellStringValue(
						DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 0), this.getCellStringValue(
						DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 1), this.getCellStringValue(
						DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 2), this.getCellStringValue(
						DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 3), this.getCellStringValue(
						DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 4), this.getCellStringValue(
						DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 5), this.getCellStringValue(
						DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 6)));

				this.currentRow++;
			}
			this.currentRow++;

		} else {
			// Incorrect headers for factors.
			throw new FileParsingException("Error parsing on constants header: Incorrect headers for constants.");
		}

		while (this.isRowEmpty(DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow,
				DescriptionSheetParser.DESCRIPTION_SHEET_COL_SIZE)) {
			this.currentRow++;
		}
	}

	protected void parseVariate() throws FileParsingException {
		if (!this.isVariateHeaderInvalid(this.currentRow) && this.importFileIsValid) {
			this.currentRow++;
			while (!this.isRowEmpty(DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow,
					DescriptionSheetParser.DESCRIPTION_SHEET_COL_SIZE)) {
				this.importedList.addImportedVariate(new ImportedVariate(this.getCellStringValue(
						DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 0), this.getCellStringValue(
						DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 1), this.getCellStringValue(
						DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 2), this.getCellStringValue(
						DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 3), this.getCellStringValue(
						DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 4), this.getCellStringValue(
						DescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 5)));
				this.currentRow++;
			}

		} else {
			throw new FileParsingException("Error parsing on variates header: Incorrect headers for variates.");
		}
	}

	protected boolean isConditionHeadersInvalid(int conditionHeaderRowNo) {
		String[] headers =
				{DescriptionHeaders.CONDITION.getLabel(), DescriptionHeaders.DESCRIPTION.getLabel(),
						DescriptionHeaders.PROPERTY.getLabel(), DescriptionHeaders.SCALE.getLabel(), DescriptionHeaders.METHOD.getLabel(),
						DescriptionHeaders.DATA_TYPE.getLabel(), DescriptionHeaders.VALUE.getLabel()};

		return this.isHeaderInvalid(conditionHeaderRowNo, DescriptionSheetParser.DESCRIPTION_SHEET_NO, headers);
	}

	protected boolean isFactorHeadersInvalid(int factorHeaderRowNo) {
		String[] headers =
				{DescriptionHeaders.FACTOR.getLabel(), DescriptionHeaders.DESCRIPTION.getLabel(), DescriptionHeaders.PROPERTY.getLabel(),
						DescriptionHeaders.SCALE.getLabel(), DescriptionHeaders.METHOD.getLabel(), DescriptionHeaders.DATA_TYPE.getLabel()};

		return this.isHeaderInvalid(factorHeaderRowNo, DescriptionSheetParser.DESCRIPTION_SHEET_NO, headers);
	}

	protected boolean isConstantsHeaderInvalid(int constantHeaderRowNo) {
		String[] headers =
				{DescriptionHeaders.CONSTANT.getLabel(), DescriptionHeaders.DESCRIPTION.getLabel(), DescriptionHeaders.PROPERTY.getLabel(),
						DescriptionHeaders.SCALE.getLabel(), DescriptionHeaders.METHOD.getLabel(), DescriptionHeaders.DATA_TYPE.getLabel(),
						DescriptionHeaders.VALUE.getLabel()};

		return this.isHeaderInvalid(constantHeaderRowNo, DescriptionSheetParser.DESCRIPTION_SHEET_NO, headers);
	}

	protected boolean isVariateHeaderInvalid(int variateHeaderRowNo) {
		String[] headers =
				{DescriptionHeaders.VARIATE.getLabel(), DescriptionHeaders.DESCRIPTION.getLabel(), DescriptionHeaders.PROPERTY.getLabel(),
						DescriptionHeaders.SCALE.getLabel(), DescriptionHeaders.METHOD.getLabel(), DescriptionHeaders.DATA_TYPE.getLabel()};

		return this.isHeaderInvalid(variateHeaderRowNo, DescriptionSheetParser.DESCRIPTION_SHEET_NO, headers);
	}

	public void setDoParseDetails(boolean doParseDetails) {
		this.doParseDetails = doParseDetails;
	}

	public void setDoParseConditions(boolean doParseConditions) {
		this.doParseConditions = doParseConditions;
	}

	public void setDoParseFactors(boolean doParseFactors) {
		this.doParseFactors = doParseFactors;
	}

	public void setDoParseConstants(boolean doParseConstants) {
		this.doParseConstants = doParseConstants;
	}

	public void setDoParseVariates(boolean doParseVariates) {
		this.doParseVariates = doParseVariates;
	}

	@Override
	public T parseWorkbook(Workbook workbook, Map<String, Object> addtlParams) throws FileParsingException {
		try {
			this.workbook = workbook;

			this.parseDescriptionSheet();
			return this.importedList;
		} catch (ParseException e) {
			DescriptionSheetParser.LOG.debug(e.getMessage(), e);
			throw new FileParsingException(this.messageSource.getMessage(AbstractExcelFileParser.FILE_INVALID, new Object[] {},
					Locale.getDefault()));
		}
	}
}
