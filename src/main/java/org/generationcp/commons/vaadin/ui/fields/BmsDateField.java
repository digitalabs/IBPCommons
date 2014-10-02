package org.generationcp.commons.vaadin.ui.fields;

import java.util.Date;

import org.generationcp.commons.util.DateUtil;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.DateField;

@Configurable
public class BmsDateField extends DateField {

	public static final String DATE_FORMAT = "yyyy-MM-dd";

	private static final long serialVersionUID = 8109945056202208596L;
	private static final String DEFAULT_LABEL = "Date";
	private static final String INVALID_FORMAT = " must be specified in the YYYY-MM-DD format";
	private static final String INVALID_YEAR = "Year must be between 1900 and 9999";

	public BmsDateField() {
		super();
		this.initializeFormat();
	}

	private void initializeFormat() {
		this.setResolution(DateField.RESOLUTION_DAY);
		this.setDateFormat(BmsDateField.DATE_FORMAT);
		this.setParseErrorMessage(BmsDateField.DEFAULT_LABEL
				+ BmsDateField.INVALID_FORMAT);
	}

	@Override
	public void validate() throws InvalidValueException {
		super.validate();

		// Since setRangeStart not working on Vaadin 6,
		// temporarily make a workaround here for setting a minimum value for
		// year
		Date date = (Date) this.getValue();
		if (date != null && !DateUtil.isValidYear(date)) {
			throw new InvalidValueException(BmsDateField.INVALID_YEAR);
		}
	}
	
	@Override
	public boolean isValid() {
		boolean isValidYear = false;
		Date date = (Date) this.getValue();
		if(date != null && DateUtil.isValidYear(date)){
			isValidYear = true;
		}
		// Added validation for possible minimum year for date
		return super.isValid() && isValidYear;
	}
}
