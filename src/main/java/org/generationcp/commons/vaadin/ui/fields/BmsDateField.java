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
		this.setParseErrorMessage(BmsDateField.DEFAULT_LABEL
				+ BmsDateField.INVALID_FORMAT);
		this.setImmediate(true);
	}

	@Override
	public void validate() {
		super.validate();
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
		super.paintContent(target);
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
        paintCalendar(target);
	}
	
	private void paintCalendar(PaintTarget target) throws PaintException {
		final Calendar calendar = this.getCalendar();
        final Date currentDate = (Date) getValue();
        for (int r = this.getResolution(); r <= RESOLUTION_YEAR; r++) {
        	paintCalendarByResolution(target,r,calendar,currentDate);
        }
	}

	private void paintCalendarByResolution(PaintTarget target, 
			int resolution, Calendar calendar, Date currentDate) throws PaintException {
		switch (resolution) {
	        case RESOLUTION_MSEC:
	        	addPaintTargetVariable(target,MSEC,calendar.get(Calendar.MILLISECOND),currentDate);
	        	break;
	        case RESOLUTION_SEC:
	        	addPaintTargetVariable(target,SEC,calendar.get(Calendar.SECOND),currentDate);
	        	break;
	        case RESOLUTION_MIN:
	        	addPaintTargetVariable(target,MIN,calendar.get(Calendar.MINUTE),currentDate);
	        	break;
	        case RESOLUTION_HOUR:
	        	addPaintTargetVariable(target,HOUR,calendar.get(Calendar.HOUR_OF_DAY),currentDate);
	        	break;
	        case RESOLUTION_DAY:
	        	addPaintTargetVariable(target,DAY,calendar.get(Calendar.DAY_OF_MONTH),currentDate);
	        	break;
	        case RESOLUTION_MONTH:
	        	addPaintTargetVariable(target,MONTH,calendar.get(Calendar.MONTH)+1,currentDate);
	        	break;
	        case RESOLUTION_YEAR:
	        	addPaintTargetVariable(target,YEAR,calendar.get(Calendar.YEAR),currentDate);
	        	break;
	        default: break;
	        
	    }
	}

	private void addPaintTargetVariable(PaintTarget target, 
			String name, int value, Date currentDate) 
			throws PaintException {
		target.addVariable(this,name,currentDate != null ? value : -1);
	}

	@Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        super.changeVariables(source, variables);
        if (!isReadOnly() && hasDateChanges(variables)) {
        	final Date oldDate = (Date) getValue();
            Date newDate = getNewDate(variables);
            dateString = (String) variables.get(DATE_STRING);
            if (newDate == null && dateString != null && !"".equals(dateString)) {
                setValueAndRepaint(dateString,oldDate);
            } else if (newDate != oldDate
                    && (newDate == null || !newDate.equals(oldDate))) {
                setValue(newDate, true);
            } else if (!isValid()) {
                setValue(null);
            }
        }
        fireEvents(variables);
    }

	private void setValueAndRepaint(String newValue, Date oldValue) {
		try {
            setValue(handleUnparsableDateString(dateString), true);
            
        } catch (ConversionException e) {
        	LOG.debug(e.getMessage(),e);
            if (oldValue != null) {
                setValue(null);
            }
        }
		requestRepaint();
	}

	private void fireEvents(Map<String, Object> variables) {
		if (variables.containsKey(FocusEvent.EVENT_ID)) {
            fireEvent(new FocusEvent(this));
        }
        if (variables.containsKey(BlurEvent.EVENT_ID)) {
            fireEvent(new BlurEvent(this));
        }
	}

	private boolean hasDateChanges(Map<String, Object> variables) {
		boolean returnVal = false;
		if(variables.containsKey(YEAR)
                || variables.containsKey(MONTH)
                || variables.containsKey(DAY)
                || variables.containsKey(HOUR)) {
			returnVal = true;
		}
        if(variables.containsKey(MIN)
                || variables.containsKey(SEC)
                || variables.containsKey(MSEC)
                || variables.containsKey(DATE_STRING)) {
			returnVal = true;
        }
        return returnVal;
	}

	private Date getNewDate(Map<String, Object> variables) {
		int year = getVariableValue(variables,YEAR);
        int month = getVariableValue(variables,MONTH);
        int day = getVariableValue(variables,DAY);
        int hour = getVariableValue(variables,HOUR);
        int min = getVariableValue(variables,MIN);
        int sec = getVariableValue(variables,SEC);
        int msec = getVariableValue(variables,MSEC);

        if (hasDateChanges(year,month,day,hour,min,sec,msec)) {
            Calendar cal = this.getCalendar();
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

	private boolean hasDateChanges(int year, int month, int day, int hour,
			int min, int sec, int msec) {
		if(year >= 0 || month >= 0 || day >= 0) {
			return true;
		} else if(hour >= 0 || min >= 0 || sec >= 0 || msec >= 0) {
			return true;
		}
		return false;
	}

	private int getVariableValue(Map<String, Object> variables, String key) {
		if(!variables.containsKey(key) || variables.get(key) == null) {
			return -1;
		}
		int value = ((Integer) variables.get(key)).intValue();
		if(MONTH.equals(key)) {
			return value - 1;
		}
		return value;
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
