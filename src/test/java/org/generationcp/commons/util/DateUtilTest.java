package org.generationcp.commons.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.generationcp.commons.exceptions.InvalidDateException;
import org.junit.Assert;
import org.junit.Test;

public class DateUtilTest {
	
	@Test
	public void testGetCurrentDate(){
		//input
		Date currDate = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(DateUtil.DATE_AS_NUMBER_FORMAT);

		Assert.assertEquals("Expecting the value returned by getCurrentDate() is equal to current date from Date object.",
				DateUtil.getCurrentDate().toString(), formatter.format(currDate));
	}
	
	@Test
	public void testGetIBPDateViaTime(){
		//input
		Date currDate = new Date();
		Long currDateInLong = currDate.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat(DateUtil.DATE_AS_NUMBER_FORMAT);
		
		Assert.assertEquals("Expecting the value returned by getIBPDate() is equal to current date from Date object in this format yyyyMMdd.", 
				DateUtil.getIBPDate(currDateInLong).toString(), formatter.format(currDate));
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetIBPDateViaYearMonthDay(){
		//input
		Date currDate = new Date();
		
		Integer year = currDate.getYear() + 1900;
		Integer month = currDate.getMonth() + 1;
		Integer day = currDate.getDate();
		
		String outputDate;
		try {
			outputDate = DateUtil.getIBPDate(year,month,day).toString();
			
			SimpleDateFormat formatter = new SimpleDateFormat(DateUtil.DATE_AS_NUMBER_FORMAT);
			
			Assert.assertEquals("Expecting the value returned by getIBPDate() is equal to current date from Date object in this format yyyyMMdd.", 
					outputDate, formatter.format(currDate));
		} catch (InvalidDateException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testIsValidDate(){
		//input
		Date currDate = new Date();
		
		Integer year = currDate.getYear() + 1900;
		Integer month = currDate.getMonth() + 1;
		Integer day = currDate.getDate();
		
		
		// valid year, valid month, valid day
		Assert.assertTrue("Expecting a true return from the method isValidDate for valid year, month and day inputs",
				DateUtil.isValidDate(year, month, day));
		
		// invalid year, valid month, valid day
		year = 1899;
		Assert.assertFalse("Expecting a false return from the method isValidDate for invalid year input", 
				DateUtil.isValidDate(year, month, day));
		
		// valid year, valid month, invalid day
		year = 2014;
		day = 32;
		Assert.assertFalse("Expecting a false return from the method isValidDate for invalid day input", 
				DateUtil.isValidDate(year, month, day));
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testValidateDate(){
		//input
		Date currDate = new Date();
		
		Integer year = currDate.getYear() + 1900;
		Integer month = currDate.getMonth() + 1;
		Integer day = currDate.getDate();
		
		String errorMessage = "";
		
		//valid year
		try{
			DateUtil.validateDate(year, month, day);
		} catch(InvalidDateException e){
			errorMessage = e.getMessage();
		}
		Assert.assertEquals("Expecting no error message return from the method validateDate for valid year",
				"", errorMessage);
		
		//invalid year exception
		year = 1888;
		try{
			DateUtil.validateDate(year, month, day);
		} catch(InvalidDateException e){
			errorMessage = e.getMessage();
		}
		
		// invalid date due to invalid year
		Assert.assertEquals("Expecting an error message return from the method validateDate for invalid year",
				"Year must be greater than or equal to 1900", errorMessage);
		
		
		//invalid month exception
		year = 2014;
		month = 13;
		try{
			DateUtil.validateDate(year, month, day);
		} catch(InvalidDateException e){
			errorMessage = e.getMessage();
		}
		
		// invalid date due to invalid year
		Assert.assertEquals("Expecting an error message return from the method validateDate for invalid month",
				"Month out of range", errorMessage);
		
		//invalid month exception
		year = 2014;
		month = 1;
		day = 32;
		try{
			DateUtil.validateDate(year, month, day);
		} catch(InvalidDateException e){
			errorMessage = e.getMessage();
		}
		
		// invalid date due to invalid year
		Assert.assertEquals("Expecting an error message return from the method validateDate for invalid day",
				"Day out of range", errorMessage);
	}
	
	@Test
	public void testIsLeapYear(){
		int year = 2016;
		
		// year is a valid leap year
		Assert.assertTrue("Expecting a true return from the method isLeapYear for valid year input",
				DateUtil.isLeapYear(year));
		
		// year is not a valid leap year
		year = 2010;
		Assert.assertFalse("Expecting a false return from the method isLeapYear for valid year input",
				DateUtil.isLeapYear(year));
	}
	
	@Test
	public void testGetYear(){
		Integer year = 1989;
		Integer month = 9;
		Integer day = 21;
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		
		Date date = new Date();
		date.setTime(calendar.getTimeInMillis());
		
		// year returned will be the year set on date
		Assert.assertEquals("Expecting the actual year is returned from getYear method", year,
				DateUtil.getYear(date));
	}
	
	@Test
	public void testGetIBPDateNoZeroes(){
		Integer year = 1989;
		Integer month = 9;
		Integer day = 21;
		Integer expectedValue = 19890921;
		
		// year returned will be the year set on date
		Assert.assertEquals("Expecting an integer in yyyymmdd format is returned from getIBPDateNoZeroes method", expectedValue,
				DateUtil.getIBPDateNoZeroes(year, month, day));
	}
	
	@Test
	public void testIsValidYearViaYearInput(){
		Integer year = 1900;
		
		// equal to 1900
		Assert.assertTrue("Expecting a true return from the method IsValidYear for valid year input",
				DateUtil.isValidYear(year));
		
		// year > 1900
		year = 2014;
		Assert.assertTrue("Expecting a true return from the method IsValidYear for valid year input",
				DateUtil.isValidYear(year));
		
		// invalid year: year < 1900
		year = 1899;
		Assert.assertFalse("Expecting a false return from the method IsValidYear for invalid year input",
				DateUtil.isValidYear(year));
		
		// invalid year: year > 9999
		year = 10000;
		Assert.assertFalse("Expecting a false return from the method IsValidYear for invalid year input",
				DateUtil.isValidYear(year));
	}
	
	@Test
	public void testIsValidYearViaDateInput(){
		Integer year = 2014;
		Integer month = 9;
		Integer day = 21;
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		
		Date date = new Date();
		date.setTime(calendar.getTimeInMillis());
		
		// year > 1900
		Assert.assertTrue("Expecting a true return from the method IsValidYear for date with valid year input",
				DateUtil.isValidYear(date));
		
		
		// year equal to 1900
		year = 1900;
		month = 9;
		day = 21;
		calendar.set(year, month, day);
		date.setTime(calendar.getTimeInMillis());
		
		Assert.assertTrue("Expecting a true return from the method IsValidYear for date with valid year input",
				DateUtil.isValidYear(date));
		
		
		// year < 1900
		year = 1888;
		month = 9;
		day = 21;
		calendar.set(year, month, day);
		date.setTime(calendar.getTimeInMillis());
		
		Assert.assertFalse("Expecting a false return from the method IsValidYear for date with invalid year input",
				DateUtil.isValidYear(date));
		
		// year > 9999
		year = 10000;
		month = 9;
		day = 21;
		calendar.set(year, month, day);
		date.setTime(calendar.getTimeInMillis());
		
		Assert.assertFalse("Expecting a false return from the method IsValidYear for date with invalid year input",
				DateUtil.isValidYear(date));
	}
}
