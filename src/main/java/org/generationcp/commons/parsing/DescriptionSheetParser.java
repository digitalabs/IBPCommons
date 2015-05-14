package org.generationcp.commons.parsing;

import org.apache.poi.ss.usermodel.Workbook;
import org.generationcp.commons.parsing.pojo.*;
import org.generationcp.commons.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Created by cyrus on 4/24/15.
 */
public class DescriptionSheetParser<T extends ImportedDescriptionDetails> extends
		AbstractExcelFileParser<T> {
	public static final int DESCRIPTION_SHEET_NO = 0;
	public static final int CONDITION_ROW_NO = 5;
	public static final int DESCRIPTION_SHEET_COL_SIZE = 8;

	private static final Logger LOG = LoggerFactory.getLogger(DescriptionSheetParser.class);
	public static final String TEMPLATE_LIST_TYPE = "LST";
	public static final String LIST_DATE = "LIST DATE";
	public static final String LIST_TYPE = "LIST TYPE";

	protected enum DescriptionHeaders {
		CONDITION("CONDITION"),
		DESCRIPTION("DESCRIPTION"),
		PROPERTY("PROPERTY"),
		SCALE("SCALE"),
		METHOD("METHOD"),
		DATA_TYPE("DATA TYPE"),
		VALUE("VALUE"),
		FACTOR("FACTOR"),
		CONSTANT("CONSTANT"),
		VARIATE("VARIATE");

		private String label;

		DescriptionHeaders(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}

		public static String[] names() {
			DescriptionHeaders[] values = values();
			String[] names = new String[values.length];

			for (int i = 0; i < values.length; i++) {
				names[i] = values[i].name();
			}

			return names;
		}
	}


	private T importedList;

	private int currentRow = 0;
	private boolean importFileIsValid = true;

	private boolean doParseDetails, doParseConditions, doParseFactors, doParseConstants, doParseVariates;

	public DescriptionSheetParser(T importedList) {
		this.importedList = importedList;
		doParseDetails = doParseConditions = doParseFactors = doParseConstants = doParseVariates = true;
	}

	public void parseDescriptionSheet() throws FileParsingException, ParseException {
		parseDescriptionSheet(doParseDetails,doParseConditions,doParseFactors,doParseConstants,doParseVariates);
	}

	private void parseDescriptionSheet(boolean doParseDetails,boolean doParseConditions,boolean doParseFactors,boolean doParseConstants,boolean doParseVariates) throws FileParsingException, ParseException {

		if (doParseDetails) {
			parseListDetails();
		}

		if (doParseConditions) {
			parseConditions();
		}

		if (doParseFactors) {
			parseFactors();
		}

		if (doParseConstants) {
			parseConstants();
		}

		if (doParseVariates) {
			parseVariate();
		}
	}


	protected void parseListDetails() throws FileParsingException, ParseException {
		String listName = getCellStringValue(DESCRIPTION_SHEET_NO, 0, 1);
		String listTitle = getCellStringValue(DESCRIPTION_SHEET_NO, 1, 1);

		String labelId = getCellStringValue(DESCRIPTION_SHEET_NO, 2, 0);

		int listDateColNo = LIST_DATE.equalsIgnoreCase(labelId) ? 2 : 3;
		int listTypeColNo = LIST_TYPE.equalsIgnoreCase(labelId) ? 2 : 3;

		Date listDate = DateUtil.parseDate(
				getCellStringValue(DESCRIPTION_SHEET_NO, listDateColNo, 1));
		String listType = getCellStringValue(DESCRIPTION_SHEET_NO, listTypeColNo, 1);

		if (!TEMPLATE_LIST_TYPE.equalsIgnoreCase(listType)) {
			throw new FileParsingException("Error parsing details : List type is invalid");
		}

		importedList.setName(listName);
		importedList.setTitle(listTitle);
		importedList.setType(listType);
		importedList.setDate(listDate);
	}

	protected void parseConditions() {
		// condition headers start at row = 5 (+ 1 : count starts from 0 )
		currentRow = CONDITION_ROW_NO;

		if (!isConditionHeadersInvalid(CONDITION_ROW_NO) && importFileIsValid) {
			currentRow++;

			while (!isRowEmpty(DESCRIPTION_SHEET_NO, currentRow, DESCRIPTION_SHEET_COL_SIZE)) {
				this.importedList.addImportedCondition(
						new ImportedCondition(
								getCellStringValue(DESCRIPTION_SHEET_NO,
										currentRow, 0)
								, getCellStringValue(DESCRIPTION_SHEET_NO,
								currentRow, 1)
								, getCellStringValue(DESCRIPTION_SHEET_NO,
								currentRow, 2)
								, getCellStringValue(DESCRIPTION_SHEET_NO,
								currentRow, 3)
								, getCellStringValue(DESCRIPTION_SHEET_NO,
								currentRow, 4)
								, getCellStringValue(DESCRIPTION_SHEET_NO,
								currentRow, 5)
								, getCellStringValue(DESCRIPTION_SHEET_NO,
								currentRow, 6),
								""
						)
				);

				currentRow++;
			}
		}

		while (isRowEmpty(DESCRIPTION_SHEET_NO, currentRow, DESCRIPTION_SHEET_COL_SIZE)) {
			currentRow++;
		}
	}

	protected void parseFactors() throws FileParsingException {

		if (!isFactorHeadersInvalid(currentRow) && importFileIsValid) {
			currentRow++;

			while (!isRowEmpty(DESCRIPTION_SHEET_NO, currentRow, DESCRIPTION_SHEET_COL_SIZE)) {
				final ImportedFactor factor = new ImportedFactor(
						getCellStringValue(DESCRIPTION_SHEET_NO, currentRow,
								0)
						,
						getCellStringValue(DESCRIPTION_SHEET_NO, currentRow,
								1)
						,
						getCellStringValue(DESCRIPTION_SHEET_NO, currentRow,
								2)
						,
						getCellStringValue(DESCRIPTION_SHEET_NO, currentRow,
								3)
						,
						getCellStringValue(DESCRIPTION_SHEET_NO, currentRow,
								4)
						,
						getCellStringValue(DESCRIPTION_SHEET_NO, currentRow,
								5)
						, "");

				importedList.addImportedFactor(factor);

				currentRow++;
			}

			currentRow++;

		} else {
			throw new FileParsingException("Error parsing on factors header: Incorrect headers for factors.");
		}

		while (isRowEmpty(DESCRIPTION_SHEET_NO, currentRow, DESCRIPTION_SHEET_COL_SIZE)) {
			currentRow++;
		}
	}

	protected void parseConstants() throws FileParsingException{
		if (!isConstantsHeaderInvalid(currentRow) && importFileIsValid) {
			currentRow++;
			while (!isRowEmpty(DESCRIPTION_SHEET_NO, currentRow, DESCRIPTION_SHEET_COL_SIZE)) {
				importedList.addImportedConstant(new ImportedConstant(
						getCellStringValue(DESCRIPTION_SHEET_NO, currentRow,
								0)
						,
						getCellStringValue(DESCRIPTION_SHEET_NO, currentRow,
								1)
						,
						getCellStringValue(DESCRIPTION_SHEET_NO, currentRow,
								2)
						,
						getCellStringValue(DESCRIPTION_SHEET_NO, currentRow,
								3)
						,
						getCellStringValue(DESCRIPTION_SHEET_NO, currentRow,
								4)
						,
						getCellStringValue(DESCRIPTION_SHEET_NO, currentRow,
								5)
						,
						getCellStringValue(DESCRIPTION_SHEET_NO, currentRow,
								6)));

				currentRow++;
			}
			currentRow++;

		} else {
			// Incorrect headers for factors.
			throw new FileParsingException("Error parsing on constants header: Incorrect headers for constants.");
		}

		while (isRowEmpty(DESCRIPTION_SHEET_NO, currentRow, DESCRIPTION_SHEET_COL_SIZE)) {
			currentRow++;
		}
	}

	protected void parseVariate() throws FileParsingException{
		if (!isVariateHeaderInvalid(currentRow) && importFileIsValid) {
			currentRow++;
			while (!isRowEmpty(DESCRIPTION_SHEET_NO, currentRow, DESCRIPTION_SHEET_COL_SIZE)) {
				importedList.addImportedVariate(
						new ImportedVariate(
								getCellStringValue(DESCRIPTION_SHEET_NO,
										currentRow, 0)
								, getCellStringValue(DESCRIPTION_SHEET_NO,
								currentRow, 1)
								, getCellStringValue(DESCRIPTION_SHEET_NO,
								currentRow, 2)
								, getCellStringValue(DESCRIPTION_SHEET_NO,
								currentRow, 3)
								, getCellStringValue(DESCRIPTION_SHEET_NO,
								currentRow, 4)
								, getCellStringValue(DESCRIPTION_SHEET_NO,
								currentRow, 5)));
				currentRow++;
			}

		} else {
			throw new FileParsingException("Error parsing on variates header: Incorrect headers for variates.");
		}
	}

	protected boolean isConditionHeadersInvalid(int conditionHeaderRowNo) {
		String[] headers = {
				DescriptionHeaders.CONDITION.getLabel(),
				DescriptionHeaders.DESCRIPTION.getLabel(),
				DescriptionHeaders.PROPERTY.getLabel(),
				DescriptionHeaders.SCALE.getLabel(),
				DescriptionHeaders.METHOD.getLabel(),
				DescriptionHeaders.DATA_TYPE.getLabel(),
				DescriptionHeaders.VALUE.getLabel()
		};

		return isHeaderInvalid(conditionHeaderRowNo, DESCRIPTION_SHEET_NO, headers);
	}

	protected boolean isFactorHeadersInvalid(int factorHeaderRowNo) {
		String[] headers = {
				DescriptionHeaders.FACTOR.getLabel(),
				DescriptionHeaders.DESCRIPTION.getLabel(),
				DescriptionHeaders.PROPERTY.getLabel(),
				DescriptionHeaders.SCALE.getLabel(),
				DescriptionHeaders.METHOD.getLabel(),
				DescriptionHeaders.DATA_TYPE.getLabel()
		};

		return isHeaderInvalid(factorHeaderRowNo,DESCRIPTION_SHEET_NO, headers);
	}

	protected boolean isConstantsHeaderInvalid(int constantHeaderRowNo) {
		String[] headers = {
				DescriptionHeaders.CONSTANT.getLabel(),
				DescriptionHeaders.DESCRIPTION.getLabel(),
				DescriptionHeaders.PROPERTY.getLabel(),
				DescriptionHeaders.SCALE.getLabel(),
				DescriptionHeaders.METHOD.getLabel(),
				DescriptionHeaders.DATA_TYPE.getLabel(),
				DescriptionHeaders.VALUE.getLabel()
		};

		return isHeaderInvalid(constantHeaderRowNo, DESCRIPTION_SHEET_NO,headers);
	}

	protected boolean isVariateHeaderInvalid(int variateHeaderRowNo) {
		String[] headers = {
				DescriptionHeaders.VARIATE.getLabel(),
				DescriptionHeaders.DESCRIPTION.getLabel(),
				DescriptionHeaders.PROPERTY.getLabel(),
				DescriptionHeaders.SCALE.getLabel(),
				DescriptionHeaders.METHOD.getLabel(),
				DescriptionHeaders.DATA_TYPE.getLabel()
		};

		return isHeaderInvalid(variateHeaderRowNo, DESCRIPTION_SHEET_NO, headers);
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
	public T parseWorkbook(Workbook workbook, Map<String,Object> addtlParams)
			throws FileParsingException {
		try {
			this.workbook = workbook;

			this.parseDescriptionSheet();
			return importedList;
		} catch (ParseException e) {
			LOG.debug(e.getMessage(), e);
			throw new FileParsingException(messageSource.getMessage(FILE_INVALID, new Object[]{}, Locale
					.getDefault()));
		}
	}
}
