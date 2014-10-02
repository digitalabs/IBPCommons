package org.generationcp.commons.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.generationcp.commons.constant.VaadinMessage;
import org.generationcp.commons.exceptions.InvalidDateException;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class DateUtil {
	
	public static final String DATE_AS_NUMBER_FORMAT = "yyyyMMdd";
	
	/**
     * Returns the current date in format "yyyyMMdd"
     * 
     * @param
     * @return
     */
    public static Integer getCurrentDate(){
        Calendar now = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_AS_NUMBER_FORMAT);
        String dateNowStr = formatter.format(now.getTime());
        Integer dateNowInt = Integer.valueOf(dateNowStr);
        return dateNowInt;

    }
    
    /**
     * Returns in format "yyyyMMdd"
     * 
     * @param time - the date in long format
     * @return
     */
    public static Integer getIBPDate(long time){
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_AS_NUMBER_FORMAT);
        String dateStr = formatter.format(time);
        Integer dateInt = Integer.valueOf(dateStr);
        return dateInt;
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
    	if (month < 0 || month > 12) {
    		throw new InvalidDateException("Month out of range", VaadinMessage.ERROR_MONTH_OUT_OF_RANGE);
        }
        if (month == 2){
           if (isLeapYear(year)){
               if (day < 0 || day > 29){
            	   throw new InvalidDateException("Day out of range", VaadinMessage.ERROR_DAY_OUT_OF_RANGE);
               }
           } else {
               if (day < 0 || day > 28){
            	   throw new InvalidDateException("Day out of range", VaadinMessage.ERROR_DAY_OUT_OF_RANGE);
               }               
           }
        } else if (((month == 4 || month == 6 || month == 9 || month == 11) && (day > 30))  || (day < 0 || day > 31)){
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
    	if(!isValidYear(year)){
    		 return false;
        }
    	if (month < 0 || month > 12) {
    		return false;        
        }
        if (month == 2){
           if (isLeapYear(year)){
               if (day < 0 || day > 29){
            	   return false;
               }
           } else {
               if (day < 0 || day > 28){
            	   return false;
               }               
           }
        } else if (((month == 4 || month == 6 || month == 9 || month == 11) && (day > 30))  || (day < 0 || day > 31)){
        	return false;                    
        }
        return true;
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
    	Calendar calendar = new GregorianCalendar();
    	calendar.setTime(date);
    	Integer year = calendar.get(Calendar.YEAR);
    	
        return year;
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
    	}
    	else if(year > 9999){
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
}
