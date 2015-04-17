package org.generationcp.commons.vaadin.ui.fields;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.generationcp.commons.util.DateUtil;
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

	public static final String DATE_FORMAT = "yyyy-MM-dd";

	private static final long serialVersionUID = 8109945056202208596L;
	private static final String DEFAULT_LABEL = "Date";
	private static final String INVALID_FORMAT = " must be specified in the YYYY-MM-DD format";
	private static final String INVALID_YEAR = "Year must be between 1900 and 9999";
	
	private String dateString = null;

	public BmsDateField() {
		super();
		this.initializeFormat();
	}

	private void initializeFormat() {
		this.setLocale(Locale.getDefault(Locale.Category.DISPLAY));
		this.setResolution(DateField.RESOLUTION_DAY);
		this.setDateFormat(BmsDateField.DATE_FORMAT);
		this.setParseErrorMessage(BmsDateField.DEFAULT_LABEL
				+ BmsDateField.INVALID_FORMAT);
		this.setImmediate(true);
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
	
	@Override
	public void paintContent(PaintTarget target) throws PaintException {
		// TODO Auto-generated method stub
		super.paintContent(target);
		
		
        // Adds the locale as attribute
        final Locale l = getLocale();
        if (l != null) {
            target.addAttribute("locale", l.toString());
        }

        if (getDateFormat() != null) {
            target.addAttribute("format", getDateFormat());
        }

        if (!isLenient()) {
            target.addAttribute("strict", true);
        }

        target.addAttribute(VDateField.WEEK_NUMBERS, isShowISOWeekNumbers());
        target.addAttribute("parsable", isValid());
        
        // Gets the calendar
        final Calendar calendar = this.getCalendar();
        final Date currentDate = (Date) getValue();

        for (int r = this.getResolution(); r <= RESOLUTION_YEAR; r++) {
            switch (r) {
            case RESOLUTION_MSEC:
                target.addVariable(
                        this,
                        "msec",
                        currentDate != null ? calendar
                                .get(Calendar.MILLISECOND) : -1);
                break;
            case RESOLUTION_SEC:
                target.addVariable(this, "sec",
                        currentDate != null ? calendar.get(Calendar.SECOND)
                                : -1);
                break;
            case RESOLUTION_MIN:
                target.addVariable(this, "min",
                        currentDate != null ? calendar.get(Calendar.MINUTE)
                                : -1);
                break;
            case RESOLUTION_HOUR:
                target.addVariable(
                        this,
                        "hour",
                        currentDate != null ? calendar
                                .get(Calendar.HOUR_OF_DAY) : -1);
                break;
            case RESOLUTION_DAY:
                target.addVariable(
                        this,
                        "day",
                        currentDate != null ? calendar
                                .get(Calendar.DAY_OF_MONTH) : -1);
                break;
            case RESOLUTION_MONTH:
                target.addVariable(this, "month",
                        currentDate != null ? calendar.get(Calendar.MONTH) + 1
                                : -1);
                break;
            case RESOLUTION_YEAR:
                target.addVariable(this, "year",
                        currentDate != null ? calendar.get(Calendar.YEAR) : -1);
                break;
            }
        }

	}
	
	/*
     * Invoked when a variable of the component changes. Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        super.changeVariables(source, variables);

        if (!isReadOnly()
                && (variables.containsKey("year")
                        || variables.containsKey("month")
                        || variables.containsKey("day")
                        || variables.containsKey("hour")
                        || variables.containsKey("min")
                        || variables.containsKey("sec")
                        || variables.containsKey("msec") || variables
                        .containsKey("dateString"))) {

            // Old and new dates
            final Date oldDate = (Date) getValue();
            Date newDate = null;

            // this enables analyzing invalid input on the server
            final String newDateString = (String) variables.get("dateString");
            dateString = newDateString;

            // Gets the new date in parts
            // Null values are converted to negative values.
            int year = variables.containsKey("year") ? (variables.get("year") == null ? -1
                    : ((Integer) variables.get("year")).intValue())
                    : -1;
            int month = variables.containsKey("month") ? (variables
                    .get("month") == null ? -1 : ((Integer) variables
                    .get("month")).intValue() - 1) : -1;
            int day = variables.containsKey("day") ? (variables.get("day") == null ? -1
                    : ((Integer) variables.get("day")).intValue())
                    : -1;
            int hour = variables.containsKey("hour") ? (variables.get("hour") == null ? -1
                    : ((Integer) variables.get("hour")).intValue())
                    : -1;
            int min = variables.containsKey("min") ? (variables.get("min") == null ? -1
                    : ((Integer) variables.get("min")).intValue())
                    : -1;
            int sec = variables.containsKey("sec") ? (variables.get("sec") == null ? -1
                    : ((Integer) variables.get("sec")).intValue())
                    : -1;
            int msec = variables.containsKey("msec") ? (variables.get("msec") == null ? -1
                    : ((Integer) variables.get("msec")).intValue())
                    : -1;

            // If all of the components is < 0 use the previous value
            if (year < 0 && month < 0 && day < 0 && hour < 0 && min < 0
                    && sec < 0 && msec < 0) {
                newDate = null;
            } else {

                // Clone the calendar for date operation
                final Calendar cal = this.getCalendar();

                // Make sure that meaningful values exists
                // Use the previous value if some of the variables
                // have not been changed.
                year = year < 0 ? cal.get(Calendar.YEAR) : year;
                month = month < 0 ? cal.get(Calendar.MONTH) : month;
                day = day < 0 ? cal.get(Calendar.DAY_OF_MONTH) : day;
                hour = hour < 0 ? cal.get(Calendar.HOUR_OF_DAY) : hour;
                min = min < 0 ? cal.get(Calendar.MINUTE) : min;
                sec = sec < 0 ? cal.get(Calendar.SECOND) : sec;
                msec = msec < 0 ? cal.get(Calendar.MILLISECOND) : msec;

                // Sets the calendar fields
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, day);
                cal.set(Calendar.HOUR_OF_DAY, hour);
                cal.set(Calendar.MINUTE, min);
                cal.set(Calendar.SECOND, sec);
                cal.set(Calendar.MILLISECOND, msec);

                // Assigns the date
                newDate = cal.getTime();
            }

            if (newDate == null && dateString != null && !"".equals(dateString)) {
                try {
                    Date parsedDate = handleUnparsableDateString(dateString);
                    setValue(parsedDate, true);
                    requestRepaint();
                } catch (ConversionException e) {

                    if (oldDate != null) {
                        setValue(null);
                        dateString = newDateString;
                    }
                    requestRepaint();
                }
            } else if (newDate != oldDate
                    && (newDate == null || !newDate.equals(oldDate))) {
                setValue(newDate, true); // Don't require a repaint, client
                // updates itself
            } else if (!isValid()) { // oldDate ==
                                                // newDate == null
                // Empty value set, previously contained unparsable date string,
                // clear related internal fields
                setValue(null);
            }
        }

        if (variables.containsKey(FocusEvent.EVENT_ID)) {
            fireEvent(new FocusEvent(this));
        }

        if (variables.containsKey(BlurEvent.EVENT_ID)) {
            fireEvent(new BlurEvent(this));
        }
    }

	public Calendar getCalendar() {
		Calendar calendar = Calendar.getInstance(getLocale());
		Date currentDate = (Date)getValue();
		if(currentDate!=null) {
			calendar.setTime(currentDate);
		}
		TimeZone timezone = getTimeZone();
		if(timezone!=null) {
			calendar.setTimeZone(timezone);
		}
		return calendar;	
	}
	
	@Override
    protected void setInternalValue(Object newValue) {
		super.setInternalValue(newValue);
        if (newValue != null) {
            dateString = newValue.toString();
        } else {
            dateString = null;
        }
    }
}
