
package org.generationcp.commons.vaadin.ui.fields;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.generationcp.commons.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.ui.VDateField;
import com.vaadin.ui.DateField;

@Configurable
public class BmsDateField extends DateField {

	private static final Logger LOG = LoggerFactory.getLogger(BmsDateField.class);
	public static final String DATE_FORMAT = "yyyy-MM-dd";

	private static final long serialVersionUID = 8109945056202208596L;
	private static final String DEFAULT_LABEL = "Date";
	private static final String INVALID_FORMAT = " must be specified in the YYYY-MM-DD format";
	private static final String INVALID_YEAR = "Year must be between 1900 and 9999";
	private static final String MSEC = "msec";
	private static final String SEC = "sec";
	private static final String MIN = "min";
	private static final String HOUR = "hour";
	private static final String DAY = "day";
	private static final String MONTH = "month";
	private static final String YEAR = "year";
	private static final Object DATE_STRING = "dateString";

	private String dateString = null;

	public BmsDateField() {
		super();
		this.initializeFormat();
	}

	private void initializeFormat() {
		this.setLocale(Locale.getDefault(Locale.Category.DISPLAY));
		this.setResolution(DateField.RESOLUTION_DAY);
		this.setDateFormat(BmsDateField.DATE_FORMAT);
		this.setParseErrorMessage(BmsDateField.DEFAULT_LABEL + BmsDateField.INVALID_FORMAT);
		this.setImmediate(true);
	}

	@Override
	public void validate() {

		try {
			super.validate();
		} catch (final UnparsableDateString e) {
			throw new InvalidValueException(BmsDateField.DEFAULT_LABEL + BmsDateField.INVALID_FORMAT);
		}

		final Date date = (Date) this.getValue();
		if (date != null && !DateUtil.isValidYear(date)) {
			throw new InvalidValueException(BmsDateField.INVALID_YEAR);
		}
	}

	@Override
	public boolean isValid() {
		boolean isValidYear = false;
		final Date date = (Date) this.getValue();
		if (date != null && DateUtil.isValidYear(date)) {
			isValidYear = true;
		}
		// Added validation for possible minimum year for date
		return super.isValid() && isValidYear;
	}

	@Override
	public void paintContent(final PaintTarget target) throws PaintException {
		super.paintContent(target);
		final Locale l = this.getLocale();
		if (l != null) {
			target.addAttribute("locale", l.toString());
		}
		if (this.getDateFormat() != null) {
			target.addAttribute("format", this.getDateFormat());
		}
		if (!this.isLenient()) {
			target.addAttribute("strict", true);
		}
		target.addAttribute(VDateField.WEEK_NUMBERS, this.isShowISOWeekNumbers());
		target.addAttribute("parsable", this.isValid());
		this.paintCalendar(target);
	}

	private void paintCalendar(final PaintTarget target) throws PaintException {
		final Calendar calendar = this.getCalendar();
		final Date currentDate = (Date) this.getValue();
		for (int r = this.getResolution(); r <= DateField.RESOLUTION_YEAR; r++) {
			this.paintCalendarByResolution(target, r, calendar, currentDate);
		}
	}

	private void paintCalendarByResolution(final PaintTarget target, final int resolution, final Calendar calendar, final Date currentDate)
			throws PaintException {
		switch (resolution) {
			case RESOLUTION_MSEC:
				this.addPaintTargetVariable(target, BmsDateField.MSEC, calendar.get(Calendar.MILLISECOND), currentDate);
				break;
			case RESOLUTION_SEC:
				this.addPaintTargetVariable(target, BmsDateField.SEC, calendar.get(Calendar.SECOND), currentDate);
				break;
			case RESOLUTION_MIN:
				this.addPaintTargetVariable(target, BmsDateField.MIN, calendar.get(Calendar.MINUTE), currentDate);
				break;
			case RESOLUTION_HOUR:
				this.addPaintTargetVariable(target, BmsDateField.HOUR, calendar.get(Calendar.HOUR_OF_DAY), currentDate);
				break;
			case RESOLUTION_DAY:
				this.addPaintTargetVariable(target, BmsDateField.DAY, calendar.get(Calendar.DAY_OF_MONTH), currentDate);
				break;
			case RESOLUTION_MONTH:
				this.addPaintTargetVariable(target, BmsDateField.MONTH, calendar.get(Calendar.MONTH) + 1, currentDate);
				break;
			case RESOLUTION_YEAR:
				this.addPaintTargetVariable(target, BmsDateField.YEAR, calendar.get(Calendar.YEAR), currentDate);
				break;
			default:
				break;

		}
	}

	private void addPaintTargetVariable(final PaintTarget target, final String name, final int value, final Date currentDate)
			throws PaintException {
		target.addVariable(this, name, currentDate != null ? value : -1);
	}

	@Override
	public void changeVariables(final Object source, final Map<String, Object> variables) {
		super.changeVariables(source, variables);
		if (!this.isReadOnly() && this.hasDateChanges(variables)) {
			final Date oldDate = (Date) this.getValue();
			final Date newDate = this.getNewDate(variables);
			this.dateString = (String) variables.get(BmsDateField.DATE_STRING);
			if (newDate == null && this.dateString != null && !"".equals(this.dateString)) {
				this.setValueAndRepaint(this.dateString, oldDate);
			} else if (newDate != oldDate && (newDate == null || !newDate.equals(oldDate))) {
				this.setValue(newDate, true);
			} else if (!this.isValid()) {
				this.setValue(null);
			}
		}
		this.fireEvents(variables);
	}

	private void setValueAndRepaint(final String newValue, final Date oldValue) {
		try {
			this.setValue(this.handleUnparsableDateString(this.dateString), true);

		} catch (final ConversionException e) {
			BmsDateField.LOG.debug(e.getMessage(), e);
			if (oldValue != null) {
				this.setValue(null);
			}
		}
		this.requestRepaint();
	}

	private void fireEvents(final Map<String, Object> variables) {
		if (variables.containsKey(FocusEvent.EVENT_ID)) {
			this.fireEvent(new FocusEvent(this));
		}
		if (variables.containsKey(BlurEvent.EVENT_ID)) {
			this.fireEvent(new BlurEvent(this));
		}
	}

	private boolean hasDateChanges(final Map<String, Object> variables) {
		boolean returnVal = false;
		if (variables.containsKey(BmsDateField.YEAR) || variables.containsKey(BmsDateField.MONTH)
				|| variables.containsKey(BmsDateField.DAY) || variables.containsKey(BmsDateField.HOUR)) {
			returnVal = true;
		} else if (variables.containsKey(BmsDateField.MIN) || variables.containsKey(BmsDateField.SEC)
				|| variables.containsKey(BmsDateField.MSEC) || variables.containsKey(BmsDateField.DATE_STRING)) {
			returnVal = true;
		}
		return returnVal;
	}

	private Date getNewDate(final Map<String, Object> variables) {
		final int year = this.getVariableValue(variables, BmsDateField.YEAR);
		final int month = this.getVariableValue(variables, BmsDateField.MONTH);
		final int day = this.getVariableValue(variables, BmsDateField.DAY);
		final int hour = this.getVariableValue(variables, BmsDateField.HOUR);
		final int min = this.getVariableValue(variables, BmsDateField.MIN);
		final int sec = this.getVariableValue(variables, BmsDateField.SEC);
		final int msec = this.getVariableValue(variables, BmsDateField.MSEC);

		if (this.hasDateChanges(year, month, day, hour, min, sec, msec)) {
			final Calendar cal = this.getCalendar();
			cal.set(Calendar.YEAR, year < 0 ? cal.get(Calendar.YEAR) : year);
			cal.set(Calendar.MONTH, month < 0 ? cal.get(Calendar.MONTH) : month);
			cal.set(Calendar.DAY_OF_MONTH, day < 0 ? cal.get(Calendar.DAY_OF_MONTH) : day);
			cal.set(Calendar.HOUR_OF_DAY, hour < 0 ? cal.get(Calendar.HOUR_OF_DAY) : hour);
			cal.set(Calendar.MINUTE, min < 0 ? cal.get(Calendar.MINUTE) : min);
			cal.set(Calendar.SECOND, sec < 0 ? cal.get(Calendar.SECOND) : sec);
			cal.set(Calendar.MILLISECOND, msec < 0 ? cal.get(Calendar.MILLISECOND) : msec);
			return cal.getTime();
		}

		return null;
	}

	private boolean hasDateChanges(final int year, final int month, final int day, final int hour, final int min, final int sec,
			final int msec) {
		if (year >= 0 || month >= 0 || day >= 0) {
			return true;
		} else if (hour >= 0 || min >= 0 || sec >= 0 || msec >= 0) {
			return true;
		}
		return false;
	}

	private int getVariableValue(final Map<String, Object> variables, final String key) {
		if (!variables.containsKey(key) || variables.get(key) == null) {
			return -1;
		}
		final int value = ((Integer) variables.get(key)).intValue();
		if (BmsDateField.MONTH.equals(key)) {
			return value - 1;
		}
		return value;
	}

	public Calendar getCalendar() {
		final Calendar calendar = Calendar.getInstance(this.getLocale());
		final Date currentDate = (Date) this.getValue();
		if (currentDate != null) {
			calendar.setTime(currentDate);
		}
		final TimeZone timezone = this.getTimeZone();
		if (timezone != null) {
			calendar.setTimeZone(timezone);
		}
		return calendar;
	}

	@Override
	protected void setInternalValue(final Object newValue) {
		super.setInternalValue(newValue);
		if (newValue != null) {
			this.dateString = newValue.toString();
		} else {
			this.dateString = null;
		}
	}
}
