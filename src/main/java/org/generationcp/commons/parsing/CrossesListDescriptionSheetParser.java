
package org.generationcp.commons.parsing;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.generationcp.commons.parsing.pojo.ImportedCondition;
import org.generationcp.commons.parsing.pojo.ImportedDescriptionDetails;
import org.generationcp.commons.parsing.pojo.ImportedFactor;
import org.generationcp.commons.parsing.pojo.ImportedVariate;
import org.generationcp.commons.util.DateUtil;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.pojos.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrossesListDescriptionSheetParser<T extends ImportedDescriptionDetails> extends AbstractExcelFileParser<T> {

	static final String INVALID_LIST_USER = "The List User's value is invalid. See valid list user names on Codes sheet or leave it blank";
	private static final int DESCRIPTION_SHEET_NO = 0;
	private static final int CONDITION_ROW_NO = 4;
	private static final int DESCRIPTION_SHEET_COL_SIZE = 8;

	private static final Logger LOG = LoggerFactory.getLogger(CrossesListDescriptionSheetParser.class);
	private static final String TEMPLATE_LIST_TYPE = "CROSS";
	public static final String LIST_DATE = "LIST DATE";
	public static final String LIST_TYPE = "LIST TYPE";
	public static final String EMPTY_STRING = "";

	private enum DescriptionHeaders {
		CONDITION("CONDITION"), DESCRIPTION("DESCRIPTION"), PROPERTY("PROPERTY"), SCALE("SCALE"), METHOD("METHOD"), DATA_TYPE(
				"DATA TYPE"), VALUE("VALUE"), FACTOR("FACTOR"), CONSTANT("CONSTANT"), VARIATE("VARIATE");

		private final String label;

		DescriptionHeaders(final String label) {
			this.label = label;
		}

		public String getLabel() {
			return this.label;
		}

		public static String[] names() {
			final DescriptionHeaders[] values = DescriptionHeaders.values();
			final String[] names = new String[values.length];

			for (int i = 0; i < values.length; i++) {
				names[i] = values[i].name();
			}

			return names;
		}
	}

	private final T importedList;

	private int currentRow = 0;

	private final boolean doParseDetails;
	private final boolean doParseConditions;
	private final boolean doParseFactors;
	private final boolean doParseVariates;

	private final UserDataManager userDataManager;

	public CrossesListDescriptionSheetParser(final T importedList, final UserDataManager userDataManager) {
		this.importedList = importedList;
		this.doParseDetails = true;
		this.doParseConditions = true;
		this.doParseFactors = true;
		this.doParseVariates = true;
		this.userDataManager = userDataManager;
	}

	private void parseDescriptionSheet() throws FileParsingException, ParseException {
		this.parseDescriptionSheet(this.doParseDetails, this.doParseConditions, this.doParseFactors, this.doParseVariates);
	}

	private void parseDescriptionSheet(final boolean doParseDetails, final boolean doParseConditions, final boolean doParseFactors,
			final boolean doParseVariates) throws FileParsingException, ParseException {

		if (doParseDetails) {
			this.parseListDetails();
		}

		if (doParseConditions) {
			this.parseConditions();
		}

		if (doParseFactors) {
			this.parseFactors();
		}

		if (doParseVariates) {
			this.parseVariate();
		}
	}

	private void parseListDetails() throws FileParsingException, ParseException {
		final String listName = this.getCellStringValue(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, 0, 1);
		this.importedList.setName(listName);

		final String listTitle = this.getCellStringValue(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, 1, 1);
		this.importedList.setTitle(listTitle);

		// The list type for the crosses import will always be CROSS list type
		this.importedList.setType(CrossesListDescriptionSheetParser.TEMPLATE_LIST_TYPE);

		final String labelId = this.getCellStringValue(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, 2, 0);
		final int listDateColNo = CrossesListDescriptionSheetParser.LIST_DATE.equalsIgnoreCase(labelId) ? 2 : 3;
		final Date listDate;
		final Double listDateNotParsed = this.getCellNumericValue(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, listDateColNo, 1);
		if (listDateNotParsed.equals(0d)) {
			listDate = DateUtil.getCurrentDate();
		} else {
			listDate = DateUtil.parseDate(String.valueOf(listDateNotParsed.intValue()));
		}
		this.importedList.setDate(listDate);

		final String listUserName = this.getCellStringValue(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, 5, 6);
		this.validateListUserName(listUserName.trim());
	}

	void validateListUserName(final String listUserName) throws FileParsingException {
		if (StringUtils.isNotEmpty(listUserName)) {
			final Person person = this.userDataManager.getPersonByFullName(listUserName);
			if (person != null) {
				this.importedList.setUserId(person.getId());
			} else {
				throw new FileParsingException(CrossesListDescriptionSheetParser.INVALID_LIST_USER);
			}
		}
	}

	private void parseConditions() {
		// condition headers start at row = 4 (5 - 1 : count starts from 0 )
		this.currentRow = CrossesListDescriptionSheetParser.CONDITION_ROW_NO;

		if (!this.isConditionHeadersInvalid(CrossesListDescriptionSheetParser.CONDITION_ROW_NO)) {
			this.currentRow++;

			while (!this.isRowEmpty(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow,
					CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_COL_SIZE)) {
				this.importedList.addImportedCondition(new ImportedCondition(
						this.getCellStringValue(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 0),
						this.getCellStringValue(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 1),
						this.getCellStringValue(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 2),
						this.getCellStringValue(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 3),
						this.getCellStringValue(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 4),
						this.getCellStringValue(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 5),
						this.getCellStringValue(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 6), ""));

				this.currentRow++;
			}
		}

		while (this.isRowEmpty(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow,
				CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_COL_SIZE)) {
			this.currentRow++;
		}
	}

	private void parseFactors() throws FileParsingException {

		if (!this.isFactorHeadersInvalid(this.currentRow)) {
			this.currentRow++;

			while (!this.isRowEmpty(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow,
					CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_COL_SIZE)) {
				final ImportedFactor factor = new ImportedFactor(
						this.getCellStringValue(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 0),
						this.getCellStringValue(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 1),
						this.getCellStringValue(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 2),
						this.getCellStringValue(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 3),
						this.getCellStringValue(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 4),
						this.getCellStringValue(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 5), "");

				this.importedList.addImportedFactor(factor);

				this.currentRow++;
			}

			this.currentRow++;

		} else {
			throw new FileParsingException("Error parsing on factors header: Incorrect headers for factors.");
		}

		while (this.isRowEmpty(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow,
				CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_COL_SIZE)) {
			this.currentRow++;
		}
	}

	private void parseVariate() throws FileParsingException {
		if (!this.isVariateHeaderInvalid(this.currentRow)) {
			this.currentRow++;
			while (!this.isRowEmpty(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow,
					CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_COL_SIZE)) {
				this.importedList.addImportedVariate(new ImportedVariate(
						this.getCellStringValue(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 0),
						this.getCellStringValue(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 1),
						this.getCellStringValue(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 2),
						this.getCellStringValue(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 3),
						this.getCellStringValue(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 4),
						this.getCellStringValue(CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, this.currentRow, 5)));
				this.currentRow++;
			}

		} else {
			throw new FileParsingException("Error parsing on variates header: Incorrect headers for variates.");
		}
	}

	private boolean isConditionHeadersInvalid(final int conditionHeaderRowNo) {
		final String[] headers = {DescriptionHeaders.CONDITION.getLabel(), DescriptionHeaders.DESCRIPTION.getLabel(),
				DescriptionHeaders.PROPERTY.getLabel(), DescriptionHeaders.SCALE.getLabel(), DescriptionHeaders.METHOD.getLabel(),
				DescriptionHeaders.DATA_TYPE.getLabel(), DescriptionHeaders.VALUE.getLabel()};

		return this.isHeaderInvalid(conditionHeaderRowNo, CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, headers);
	}

	private boolean isFactorHeadersInvalid(final int factorHeaderRowNo) {
		final String[] headers =
				{DescriptionHeaders.FACTOR.getLabel(), DescriptionHeaders.DESCRIPTION.getLabel(), DescriptionHeaders.PROPERTY.getLabel(),
						DescriptionHeaders.SCALE.getLabel(), DescriptionHeaders.METHOD.getLabel(), DescriptionHeaders.DATA_TYPE.getLabel()};

		return this.isHeaderInvalid(factorHeaderRowNo, CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, headers);
	}

	private boolean isConstantsHeaderInvalid(final int constantHeaderRowNo) {
		final String[] headers = {DescriptionHeaders.CONSTANT.getLabel(), DescriptionHeaders.DESCRIPTION.getLabel(),
				DescriptionHeaders.PROPERTY.getLabel(), DescriptionHeaders.SCALE.getLabel(), DescriptionHeaders.METHOD.getLabel(),
				DescriptionHeaders.DATA_TYPE.getLabel(), DescriptionHeaders.VALUE.getLabel()};

		return this.isHeaderInvalid(constantHeaderRowNo, CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, headers);
	}

	private boolean isVariateHeaderInvalid(final int variateHeaderRowNo) {
		final String[] headers =
				{DescriptionHeaders.VARIATE.getLabel(), DescriptionHeaders.DESCRIPTION.getLabel(), DescriptionHeaders.PROPERTY.getLabel(),
						DescriptionHeaders.SCALE.getLabel(), DescriptionHeaders.METHOD.getLabel(), DescriptionHeaders.DATA_TYPE.getLabel()};

		return this.isHeaderInvalid(variateHeaderRowNo, CrossesListDescriptionSheetParser.DESCRIPTION_SHEET_NO, headers);
	}

	T getImportedList() {
		return this.importedList;
	}

	@Override
	public T parseWorkbook(final Workbook workbook, final Map<String, Object> addtlParams) throws FileParsingException {
		try {
			this.workbook = workbook;

			this.parseDescriptionSheet();
			return this.importedList;
		} catch (final ParseException e) {
			CrossesListDescriptionSheetParser.LOG.debug(e.getMessage(), e);
			throw new FileParsingException(
					this.messageSource.getMessage(AbstractExcelFileParser.FILE_INVALID, new Object[] {}, Locale.getDefault()));
		}
	}
}
