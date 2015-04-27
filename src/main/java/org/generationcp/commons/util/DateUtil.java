package org.generationcp.commons.util;

import org.generationcp.commons.constant.VaadinMessage;
import org.generationcp.commons.exceptions.InvalidDateException;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Configurable
public class DateUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(DateUtil.class);
	
	public static final String DATE_AS_NUMBER_FORMAT = Util.DATE_AS_NUMBER_FORMAT;
	public static final String FRONTEND_DATE_FORMAT = Util.FRONTEND_DATE_FORMAT;
	public static final String FRONTEND_DATE_FORMAT_2 = Util.FRONTEND_DATE_FORMAT_2;
	public static final String FRONTEND_TIMESTAMP_FORMAT = Util.FRONTEND_TIMESTAMP_FORMAT;
	
	private DateUtil() {
		//use a private constructor to hide the implicit public one
	}
	
	/**
     * Returns the current date in format "yyyyMMdd" as Integer
     * @return current date as Integer
     */
    public static Integer getCurrentDateAsIntegerValue(){
    	return Util.getCurrentDateAsIntegerValue();
    }
    
    /**
     * Returns the current date in format "yyyyMMdd" as Long
     * @return current date as Long
     */
    public static Long getCurrentDateAsLongValue(){
    	return Util.getCurrentDateAsLongValue();
    }
    
    /**
     * Returns the current date in format "yyyyMMdd" as String
     * @return current date as String
     */
    public static String getCurrentDateAsStringValue(){
    	return Util.getCurrentDateAsStringValue();
    }
    
    /**
     * Returns the current date
     * @return current date as Date
     */
    public static Date getCurrentDate(){
    	return Util.getCurrentDate();
    }
    
    /**
     * Returns the calendar instance
     * @return calendar instance
     */
    public static Calendar getCalendarInstance(){
    	return Util.getCalendarInstance();
    }
    
    /**
     * Returns the current date in the specified format as String
     * @return current date as String
     */
    public static String getCurrentDateAsStringValue(String format){
    	return Util.getCurrentDateAsStringValue(format);
    }
    
    /**
     * Returns the date in the specified format as String
     * @return date in the specified format as String
     */
    public static String formatDateAsStringValue(Date date, String format){
    	return Util.formatDateAsStringValue(date, format);
    }
    
    /**
     * Returns the date object from the specified format
     * @return date object
     * @throws ParseException 
     */
    public static Date parseDate(String date, String format) throws ParseException{
    	return Util.parseDate(date, format);
    }

    /**
     * Parses the date given default format
     *
     * @param date the date
     * @return the date
     * @throws ParseException the parse exception
     */
    public static Date parseDate(String date) throws ParseException{
        return Util.parseDate(date,Util.DATE_AS_NUMBER_FORMAT);
    }

    /**
     * Returns the SimpleDateFormat of the current display locale
     * @return SimpleDateFormat
     */
    public static SimpleDateFormat getSimpleDateFormat(String format){
    	return Util.getSimpleDateFormat(format);
    }
    
    /**
     * Returns in format "yyyyMMdd"
     * 
     * @param time - the date in long format
     * @return
     */
    public static Integer getIBPDate(long time){
        SimpleDateFormat formatter = getSimpleDateFormat(DATE_AS_NUMBER_FORMAT);
        String dateStr = formatter.format(time);
        return Integer.valueOf(dateStr);
    }

    /** 
     * Returns in format "yyyyMMdd"
     * 
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static Integer getIBPDate(Integer year, Integer month, Integer day) throws InvalidDateException{
        validateDate(year, month, day);
        return Integer.valueOf(year * 10000 + month * 100 + day);
    }
    
    /**
     * Checks if a given date is valid.
     * 
     * @param year
     * @param month
     * @param day
     * @return VaadinMessage Enum that is to be interpreted in specific web application
     */
    public static void validateDate(int year, int month, int day) throws InvalidDateException{
    	if(!isValidYear(year)){
    		throw new InvalidDateException("Year must be greater than or equal to 1900", VaadinMessage.INVALID_YEAR);
        }
    	if (month <= 0 || month > 12) {
    		throw new InvalidDateException("Month out of range", VaadinMessage.ERROR_MONTH_OUT_OF_RANGE);
        }
    	if (day <= 0 || day > daysInMonth(year, month)){
           throw new InvalidDateException("Day out of range", VaadinMessage.ERROR_DAY_OUT_OF_RANGE);
        }
    }

	/**
     * Checks if a given date is valid.
     * 
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static boolean isValidDate(int year, int month, int day) {
    	boolean yearOk = isValidYear(year);
	    boolean monthOk = (month >= 1) && (month <= 12);
	    boolean dayOk = (day >= 1) && (day <= daysInMonth(year, month));
	    return yearOk && monthOk && dayOk;
    }
    
    /**
     * Checks if the given year is a leap year.
     * 
     * @param year
     * @return
     */
    public static boolean isLeapYear(int year){
        boolean isLeapYear = false;
        if (year % 400 == 0) {
            isLeapYear = true;
        } else if (year % 100 == 0) {
            isLeapYear = false;
        } else if (year % 4 == 0 ) {
            isLeapYear = true;
        } else {
            isLeapYear = false;
        }
        return isLeapYear;
    } 
    
	/**
     * Returns the actual year given a date object
     * 
     * @param Date object
     * @return Integer year
     */
	public static Integer getYear(Date date){	
		String year = getSimpleDateFormat("yyyy").format(date);
		return Integer.parseInt(year);
    }
    
    /** 
     * Returns integer value of year month date concatenation
     * if no date value, yyyymm
     * eg. if month = 1, and year = 2005 => 200501
     * if no month and date, yyyy
     * 
     * Return 0 if no year, month and date
     * 
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static Integer getIBPDateNoZeroes(int year, int month, int day){
        String dayString = (day == 0) ? "" : String.valueOf(day);
        if (!dayString.isEmpty() && dayString.length() == 1){
        	dayString = "0" + dayString;
        }
        String monthString = (month == 0) ? "" : String.valueOf(month);
        if (!monthString.isEmpty() && monthString.length() == 1){
        	monthString = "0" + monthString;
        }
        String yearString = (year == 0) ? "" : String.valueOf(year);
        String fulldate = yearString + monthString + dayString;
        if (!fulldate.isEmpty()){
        	return new Integer(fulldate);
        }
        return 0;
    }
    
    /***
     * Returns true if year is greater than or equal to 1900
     * 
     * @param Integer year
     * @return boolean
     * */
    public static boolean isValidYear(Integer year){
    	if(year < 1900){
    		return false;
    	} else if(year > 9999){
    		return false;
    	}
    	return true;
    }
    
    /***
     * Returns true if year is greater than or equal to 1900
     * 
     * @param Date date
     * @return boolean
     * */
    public static boolean isValidYear(Date date){
    	return isValidYear(getYear(date));
    }
    
    /**
     * Converts the date from the old format to the new format
     * 
     * @param date
     * @param oldFormat
     * @param newFormat
     * @return String converted date from old format to new format
     * @throws ParseException 
     */
    public static String convertDate(String date, String oldFormat, String newFormat) throws ParseException {
    	SimpleDateFormat sdf = getSimpleDateFormat(oldFormat);
        Date d = sdf.parse(date);
        sdf.applyPattern(newFormat);
        return sdf.format(d);
    }
    
    /**
     * Return current date in "yyyy-MM-dd" format
     * @return String
     */
    public static String getCurrentDateInUIFormat() {
    	return Util.getCurrentDateAsStringValue(
                Util.FRONTEND_DATE_FORMAT);
    }
    
    /**
     * Return specified date in "yyyy-MM-dd" format
     * @param date to convert
     * @return String
     */
    public static String getDateInUIFormat(Date date) {
    	if(date == null){
    		return "";
    	}
    	return Util.formatDateAsStringValue(date,
                Util.FRONTEND_DATE_FORMAT);
    }
    
    
    public static String convertToDBDateFormat(
    		Integer dataTypeId, String value) {
    	String returnVal = value;
    	if(dataTypeId != null && dataTypeId == TermId.DATE_VARIABLE.getId() && 
    			value != null && !"".equalsIgnoreCase(value)) {
    		try {
    			return convertDate(value, FRONTEND_DATE_FORMAT, DATE_AS_NUMBER_FORMAT);
			} catch (ParseException e) {
				LOG.error(e.getMessage(),e);
				returnVal = "";
			}
    	}
    	return returnVal;
    }
    
    public static String convertToUIDateFormat(
    		Integer dataTypeId, String value) {
    	String returnVal = value;
    	if(dataTypeId != null && dataTypeId == TermId.DATE_VARIABLE.getId() && 
    			value != null && !"".equalsIgnoreCase(value)) {
    		try {
				return convertDate(value, DATE_AS_NUMBER_FORMAT, FRONTEND_DATE_FORMAT);
			} catch (ParseException e) {
				LOG.error(e.getMessage(),e);
				returnVal = "";
			}
    	}
    	return returnVal;
    }
    
    public static boolean isValidDate(String dateString) {
	    if (dateString == null || dateString.length() != 
	    		DateUtil.DATE_AS_NUMBER_FORMAT.length()) {
	        return false;
	    }

	    int date;
	    try {
	        date = Integer.parseInt(dateString);
	    } catch (NumberFormatException e) {
	        return false;
	    }

	    int year = date / 10000;
	    int month = (date % 10000) / 100;
	    int day = date % 100;

	    return isValidDate(year, month, day);
	}

    public static boolean isValidFieldbookDate(String dateString) {
        if (dateString == null || dateString.length() != Util.DATE_AS_NUMBER_FORMAT.length()) {
            return false;
        }

        int date;
        try {
            date = Integer.parseInt(dateString);
        } catch (NumberFormatException e) {
            return false;
        }

        int year = date / 10000;
        int month = (date % 10000) / 100;
        int day = date % 100;

        // leap years calculation not valid before 1581
        boolean yearOk = (year >= 1581);
        boolean monthOk = (month >= 1) && (month <= 12);
        boolean dayOk = (day >= 1) && (day <= daysInMonth(year, month));

        return (yearOk && monthOk && dayOk);
    }
    
    public static int daysInMonth(int year, int month) {
	    int daysInMonth;
	    if(month == 2) {
	    	if (isLeapYear(year)) {
                daysInMonth = 29;
            } else {
                daysInMonth = 28;
            }
	    } else if (month == 4 || month == 6 || month == 9 || month == 11){
	    	daysInMonth = 30;
	    } else {
	    	daysInMonth = 31;
	    }
	    return daysInMonth;
	}
}
