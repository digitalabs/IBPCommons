
package org.generationcp.commons.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.generationcp.commons.exceptions.InvalidDateException;
import org.generationcp.middleware.domain.oms.TermId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DateUtilTest {

	private SimpleDateFormat dateFormat;
	private Calendar calendar;

	@Before
	public void setUp() {
		this.dateFormat = DateUtil.getSimpleDateFormat(DateUtil.DATE_AS_NUMBER_FORMAT);
		this.calendar = DateUtil.getCalendarInstance();
	}

	@Test
	public void testGetCurrentDateAsStringValue() {
		String dateString = this.dateFormat.format(this.calendar.getTime());
		String actual = DateUtil.getCurrentDateAsStringValue();
		Assert.assertEquals("Expected " + dateString + " but got " + actual, dateString, actual);
	}

	@Test
	public void testGetIBPDateViaTime() {
		// input
		Date currDate = new Date();
		Long currDateInLong = currDate.getTime();
		SimpleDateFormat formatter = DateUtil.getSimpleDateFormat(DateUtil.DATE_AS_NUMBER_FORMAT);
		Assert.assertEquals(
				"Expecting the value returned by getIBPDate() is equal to current date from Date object in this format yyyyMMdd.", DateUtil
						.getIBPDate(currDateInLong).toString(), formatter.format(currDate));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetIBPDateViaYearMonthDay() {
		// input
		Date currDate = new Date();

		Integer year = currDate.getYear() + 1900;
		Integer month = currDate.getMonth() + 1;
		Integer day = currDate.getDate();

		String outputDate;
		try {
			outputDate = DateUtil.getIBPDate(year, month, day).toString();

			SimpleDateFormat formatter = DateUtil.getSimpleDateFormat(DateUtil.DATE_AS_NUMBER_FORMAT);

			Assert.assertEquals(
					"Expecting the value returned by getIBPDate() is equal to current date from Date object in this format yyyyMMdd.",
					outputDate, formatter.format(currDate));
		} catch (InvalidDateException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testIsValidDate() {
		// input
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

		// valid year, invalid month, invalid day
		year = 2014;
		day = 32;
		month = 0;
		Assert.assertFalse("Expecting a false return from the method isValidDate for invalid month and day input",
				DateUtil.isValidDate(year, month, day));

		// invalid valid year, valid month, invalid day
		year = 0;
		day = 0;
		month = 13;
		Assert.assertFalse("Expecting a false return from the method isValidDate for invalid year, month and day input",
				DateUtil.isValidDate(year, month, day));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testValidateDate() {
		// input
		Date currDate = new Date();

		Integer year = currDate.getYear() + 1900;
		Integer month = currDate.getMonth() + 1;
		Integer day = currDate.getDate();

		String errorMessage = "";

		// valid year
		try {
			DateUtil.validateDate(year, month, day);
		} catch (InvalidDateException e) {
			errorMessage = e.getMessage();
		}
		Assert.assertEquals("Expecting no error message return from the method validateDate for valid year", "", errorMessage);

		// invalid year exception
		errorMessage = "";
		year = 1888;
		try {
			DateUtil.validateDate(year, month, day);
		} catch (InvalidDateException e) {
			errorMessage = e.getMessage();
		}

		// invalid date due to invalid year
		Assert.assertEquals("Expecting an error message return from the method validateDate for invalid year",
				"Year must be greater than or equal to 1900", errorMessage);

		// invalid month exception
		errorMessage = "";
		year = 2014;
		month = 13;
		try {
			DateUtil.validateDate(year, month, day);
		} catch (InvalidDateException e) {
			errorMessage = e.getMessage();
		}

		// invalid date due to invalid month
		Assert.assertEquals("Expecting an error message return from the method validateDate for invalid month", "Month out of range",
				errorMessage);

		// invalid month exception
		errorMessage = "";
		year = 2014;
		month = 0;
		try {
			DateUtil.validateDate(year, month, day);
		} catch (InvalidDateException e) {
			errorMessage = e.getMessage();
		}

		// invalid date due to invalid month
		Assert.assertEquals("Expecting an error message return from the method validateDate for invalid month", "Month out of range",
				errorMessage);

		// invalid day exception
		errorMessage = "";
		year = 2014;
		month = 4;
		day = 31;
		try {
			DateUtil.validateDate(year, month, day);
		} catch (InvalidDateException e) {
			errorMessage = e.getMessage();
		}

		// invalid date due to invalid day
		Assert.assertEquals("Expecting an error message return from the method validateDate for invalid day", "Day out of range",
				errorMessage);

		// invalid day exception
		errorMessage = "";
		year = 2014;
		month = 4;
		day = 0;
		try {
			DateUtil.validateDate(year, month, day);
		} catch (InvalidDateException e) {
			errorMessage = e.getMessage();
		}

		// invalid date due to invalid day
		Assert.assertEquals("Expecting an error message return from the method validateDate for invalid day", "Day out of range",
				errorMessage);

		// invalid day exception for February
		errorMessage = "";
		year = 2015;
		month = 2;
		day = 29;
		try {
			DateUtil.validateDate(year, month, day);
		} catch (InvalidDateException e) {
			errorMessage = e.getMessage();
		}

		// invalid date due to invalid day
		Assert.assertEquals("Expecting an error message return from the method validateDate for invalid day", "Day out of range",
				errorMessage);

		// valid day for February because of leap year
		errorMessage = "";
		year = 2016;
		month = 2;
		day = 29;
		try {
			DateUtil.validateDate(year, month, day);
		} catch (InvalidDateException e) {
			errorMessage = e.getMessage();
		}

		Assert.assertEquals("Expecting no error message return from the method validateDate for valid day for February in a leap year", "",
				errorMessage);
	}

	@Test
	public void testIsLeapYear() {
		int year = 2016;

		// year is a valid leap year
		Assert.assertTrue("Expecting a true return from the method isLeapYear for valid year input", DateUtil.isLeapYear(year));

		// year is not a valid leap year
		year = 2010;
		Assert.assertFalse("Expecting a false return from the method isLeapYear for valid year input", DateUtil.isLeapYear(year));

		// year is a valid leap year (divisible by 400)
		year = 2000;
		Assert.assertTrue("Expecting a true return from the method isLeapYear for valid year input", DateUtil.isLeapYear(year));
	}

	@Test
	public void testGetYear() {
		Integer year = 1989;
		Integer month = 9;
		Integer day = 21;
		Calendar calendar = DateUtil.getCalendarInstance();
		calendar.set(year, month, day);

		Date date = new Date();
		date.setTime(calendar.getTimeInMillis());

		// year returned will be the year set on date
		Assert.assertEquals("Expecting the actual year is returned from getYear method", year, DateUtil.getYear(date));
	}

	@Test
	public void testGetIBPDateNoZeroes() {
		Integer year = 1989;
		Integer month = 9;
		Integer day = 21;
		Integer expectedValue = 19890921;

		// year returned will be the year set on date
		Assert.assertEquals("Expecting an integer in yyyymmdd format is returned from getIBPDateNoZeroes method", expectedValue,
				DateUtil.getIBPDateNoZeroes(year, month, day));
	}

	@Test
	public void testIsValidYearViaYearInput() {
		Integer year = 1900;

		// equal to 1900
		Assert.assertTrue("Expecting a true return from the method IsValidYear for valid year input", DateUtil.isValidYear(year));

		// year > 1900
		year = 2014;
		Assert.assertTrue("Expecting a true return from the method IsValidYear for valid year input", DateUtil.isValidYear(year));

		// invalid year: year < 1900
		year = 1899;
		Assert.assertFalse("Expecting a false return from the method IsValidYear for invalid year input", DateUtil.isValidYear(year));

		// invalid year: year > 9999
		year = 10000;
		Assert.assertFalse("Expecting a false return from the method IsValidYear for invalid year input", DateUtil.isValidYear(year));
	}

	@Test
	public void testIsValidYearViaDateInput() {
		Integer year = 2014;
		Integer month = 9;
		Integer day = 21;
		Calendar calendar = DateUtil.getCalendarInstance();
		calendar.set(year, month, day);

		Date date = new Date();
		date.setTime(calendar.getTimeInMillis());

		// year > 1900
		Assert.assertTrue("Expecting a true return from the method IsValidYear for date with valid year input", DateUtil.isValidYear(date));

		// year equal to 1900
		year = 1900;
		month = 9;
		day = 21;
		calendar.set(year, month, day);
		date.setTime(calendar.getTimeInMillis());

		Assert.assertTrue("Expecting a true return from the method IsValidYear for date with valid year input", DateUtil.isValidYear(date));

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

	@Test
	public void testGetCurrentDateAsIntegerValue() {
		String dateString = this.dateFormat.format(this.calendar.getTime());
		Integer actual = DateUtil.getCurrentDateAsIntegerValue();
		Assert.assertEquals("Expected " + dateString + " but got " + actual, Integer.valueOf(dateString), actual);
	}

	@Test
	public void testGetCurrentDateAsLongValue() {
		String dateString = this.dateFormat.format(this.calendar.getTime());
		Long actual = DateUtil.getCurrentDateAsLongValue();
		Assert.assertEquals("Expected " + dateString + " but got " + actual, Long.valueOf(dateString), actual);
	}

	@Test
	public void testGetCurrentDate() {
		Date date = new Date();
		Date actual = DateUtil.getCurrentDate();
		Assert.assertEquals("Expected " + date + " but got " + actual, date, actual);
	}

	@Test
	public void testGetCurrentDateAsStringValueInSpecifiedFormat() {
		String expectedDate = DateUtil.getCurrentDateInUIFormat();
		String actualDate = DateUtil.getCurrentDateAsStringValue(DateUtil.FRONTEND_DATE_FORMAT);
		Assert.assertEquals("Expected " + expectedDate + " but got " + actualDate, expectedDate, actualDate);
	}

	@Test
	public void testFormatDateAsStringValue() {
		Date date = new Date();
		String actual = DateUtil.formatDateAsStringValue(date, DateUtil.DATE_AS_NUMBER_FORMAT);
		String expected = this.dateFormat.format(date);
		Assert.assertEquals("Expected " + expected + " but got " + actual, expected, actual);
	}

	@Test
	public void testParseDate() {
		String dateString = "2015-08-18";
		try {
			DateUtil.parseDate(dateString, DateUtil.FRONTEND_DATE_FORMAT);
		} catch (ParseException e) {
			Assert.fail(dateString + " should be parsed successfully but failed");
		}
	}

	@Test(expected = ParseException.class)
	public void testParseDateFormatMismatched() throws ParseException {
		String dateString = "2015-08-18";
		DateUtil.parseDate(dateString, DateUtil.FRONTEND_DATE_FORMAT_2);
	}

	@Test
	public void testGetSimpleDateFormat() {
		SimpleDateFormat actual = DateUtil.getSimpleDateFormat(DateUtil.DATE_AS_NUMBER_FORMAT);
		Assert.assertEquals("Expected " + this.dateFormat + " but got " + actual, this.dateFormat, actual);
		Date currentDate = new Date();
		String actualDate = actual.format(currentDate);
		String expectedDate = this.dateFormat.format(currentDate);
		Assert.assertEquals("Expected " + expectedDate + " but got " + actualDate, expectedDate, actualDate);
		String dateInCorrectFormat = "20150818";
		try {
			actual.parse(dateInCorrectFormat);
		} catch (ParseException e) {
			Assert.fail(dateInCorrectFormat + " should be parsed successfully but failed");
		}
	}

	@Test(expected = ParseException.class)
	public void testGetSimpleDateFormatThrowsException() throws ParseException {
		SimpleDateFormat actual = DateUtil.getSimpleDateFormat(DateUtil.DATE_AS_NUMBER_FORMAT);
		actual.parse("20151832");
	}

	@Test
	public void testConvertDate() {
		String oldDate = "20150421";
		String expectedNewDate = "2015-04-21";
		try {
			String actualNewDate = DateUtil.convertDate(oldDate, DateUtil.DATE_AS_NUMBER_FORMAT, DateUtil.FRONTEND_DATE_FORMAT);
			Assert.assertEquals("Expected " + expectedNewDate + " but got " + actualNewDate, expectedNewDate, actualNewDate);
		} catch (ParseException e) {
			Assert.fail(oldDate + " should be parsed successfully but failed");
		}
	}

	@Test
	public void testGetDateInUIFormat() {
		Date date = this.calendar.getTime();
		SimpleDateFormat uiFormat = DateUtil.getSimpleDateFormat(DateUtil.FRONTEND_DATE_FORMAT);
		String expectedDate = uiFormat.format(date);
		String actualDate = DateUtil.getDateInUIFormat(date);
		Assert.assertEquals("Expected " + expectedDate + " but got " + actualDate, expectedDate, actualDate);
	}

	@Test
	public void testGetDateInUIFormat_Null() {
		String expectedDate = "";
		String actualDate = DateUtil.getDateInUIFormat(null);
		Assert.assertEquals("Expected " + expectedDate + " but got " + actualDate, expectedDate, actualDate);
	}

	@Test
	public void testGetCurrentDateInUIFormat() {
		Date date = this.calendar.getTime();
		SimpleDateFormat uiFormat = DateUtil.getSimpleDateFormat(DateUtil.FRONTEND_DATE_FORMAT);
		String expectedDate = uiFormat.format(date);
		String actualDate = DateUtil.getCurrentDateInUIFormat();
		Assert.assertEquals("Expected " + expectedDate + " but got " + actualDate, expectedDate, actualDate);
	}

	@Test
	public void testConvertToDBDateFormat() {
		String uiDate = "2015-04-25";
		String actualDBDate = DateUtil.convertToDBDateFormat(TermId.DATE_VARIABLE.getId(), uiDate);
		String expectedDBDate = "20150425";
		Assert.assertEquals("Expected " + expectedDBDate + " but got " + actualDBDate, expectedDBDate, actualDBDate);
	}

	@Test
	public void testConvertToDBDateFormat_WrongFromDateFormat() {
		String uiDate = "2015/06/30";
		String actualDBDate = DateUtil.convertToDBDateFormat(TermId.DATE_VARIABLE.getId(), uiDate);
		String expectedDBDate = "";
		Assert.assertEquals("Expected " + expectedDBDate + " but got " + actualDBDate, expectedDBDate, actualDBDate);
	}

	@Test
	public void testConvertToDBDateFormat_FromDBDateFormat() {
		String uiDate = "20150630";
		String actualDBDate = DateUtil.convertToDBDateFormat(TermId.DATE_VARIABLE.getId(), uiDate);
		String expectedDBDate = "20150630";
		Assert.assertEquals("Expected " + expectedDBDate + " but got " + actualDBDate, expectedDBDate, actualDBDate);
	}

	@Test
	public void testConvertToDBDateFormat_NullFromDateFormat() {
		String uiDate = null;
		String actualDBDate = DateUtil.convertToDBDateFormat(TermId.DATE_VARIABLE.getId(), uiDate);
		String expectedDBDate = uiDate;
		Assert.assertEquals("Expected " + expectedDBDate + " but got " + actualDBDate, expectedDBDate, actualDBDate);
	}

	@Test
	public void testConvertToDBDateFormat_EmptyFromDateFormat() {
		String uiDate = "";
		String actualDBDate = DateUtil.convertToDBDateFormat(TermId.DATE_VARIABLE.getId(), uiDate);
		String expectedDBDate = uiDate;
		Assert.assertEquals("Expected " + expectedDBDate + " but got " + actualDBDate, expectedDBDate, actualDBDate);
	}

	@Test
	public void testConvertToDBDateFormat_WrongDataTypeId() {
		String uiDate = "2015-07-30";
		String actualDBDate = DateUtil.convertToDBDateFormat(2, uiDate);
		String expectedDBDate = uiDate;
		Assert.assertEquals("Expected " + expectedDBDate + " but got " + actualDBDate, expectedDBDate, actualDBDate);
	}

	@Test
	public void testConvertToDBDateFormat_NullDataTypeId() {
		String uiDate = "2015-09-30";
		String actualDBDate = DateUtil.convertToDBDateFormat(null, uiDate);
		String expectedDBDate = uiDate;
		Assert.assertEquals("Expected " + expectedDBDate + " but got " + actualDBDate, expectedDBDate, actualDBDate);
	}

	@Test
	public void testConvertToUIDateFormat() {
		String dbDate = "20150430";
		String actualUIDate = DateUtil.convertToUIDateFormat(TermId.DATE_VARIABLE.getId(), dbDate);
		String expectedUIDate = "2015-04-30";
		Assert.assertEquals("Expected " + expectedUIDate + " but got " + actualUIDate, expectedUIDate, actualUIDate);
	}

	@Test
	public void testConvertToUIDateFormat_WrongFromDateFormat() {
		String dbDate = "2015-04-30";
		String actualUIDate = DateUtil.convertToUIDateFormat(TermId.DATE_VARIABLE.getId(), dbDate);
		String expectedUIDate = "";
		Assert.assertEquals("Expected " + expectedUIDate + " but got " + actualUIDate, expectedUIDate, actualUIDate);
	}

	@Test
	public void testConvertToUIDateFormat_NullFromDateFormat() {
		String dbDate = null;
		String actualUIDate = DateUtil.convertToUIDateFormat(TermId.DATE_VARIABLE.getId(), dbDate);
		String expectedUIDate = dbDate;
		Assert.assertEquals("Expected " + expectedUIDate + " but got " + actualUIDate, expectedUIDate, actualUIDate);
	}

	@Test
	public void testConvertToUIDateFormat_EmptyFromDateFormat() {
		String dbDate = "";
		String actualUIDate = DateUtil.convertToUIDateFormat(TermId.DATE_VARIABLE.getId(), dbDate);
		String expectedUIDate = dbDate;
		Assert.assertEquals("Expected " + expectedUIDate + " but got " + actualUIDate, expectedUIDate, actualUIDate);
	}

	@Test
	public void testConvertToUIDateFormat_WrongDataTypeId() {
		String dbDate = "20150426";
		String actualUIDate = DateUtil.convertToUIDateFormat(1, dbDate);
		String expectedUIDate = dbDate;
		Assert.assertEquals("Expected " + expectedUIDate + " but got " + actualUIDate, expectedUIDate, actualUIDate);
	}

	@Test
	public void testConvertToUIDateFormat_NullDataTypeId() {
		String dbDate = "20150726";
		String actualUIDate = DateUtil.convertToUIDateFormat(null, dbDate);
		String expectedUIDate = dbDate;
		Assert.assertEquals("Expected " + expectedUIDate + " but got " + actualUIDate, expectedUIDate, actualUIDate);
	}

	@Test
	public void testIsValidDateString() {
		String dateString = "20150422";
		boolean isValid = DateUtil.isValidDate(dateString);
		Assert.assertTrue(dateString + " should be a valid date", isValid);
	}

	@Test
	public void testIsValidDateString_Null() {
		String dateString = null;
		boolean isValid = DateUtil.isValidDate(dateString);
		Assert.assertFalse(dateString + " should not be a valid date", isValid);
	}

	@Test
	public void testIsValidDateString_WrongFormat() {
		String dateString = "2015-12-21";
		boolean isValid = DateUtil.isValidDate(dateString);
		Assert.assertFalse(dateString + " should not be a valid date", isValid);
	}

	@Test
	public void testIsValidDateString_NotANumber() {
		String dateString = "abcdefgh";
		boolean isValid = DateUtil.isValidDate(dateString);
		Assert.assertFalse(dateString + " should not be a valid date", isValid);
	}

	@Test
	public void testIsValidDateString_Invalid() {
		String dateString = "20151322";
		boolean isValid = DateUtil.isValidDate(dateString);
		Assert.assertFalse(dateString + " should not be a valid date", isValid);
	}

	@Test
	public void testDaysInMonth() {
		int year = 2015;
		int actualDaysInMonth = 0;
		int expectedDaysInMonth = 0;
		for (int month = 1; month <= 12; month++) {
			actualDaysInMonth = DateUtil.daysInMonth(year, month);
			if (month == 2) {
				expectedDaysInMonth = 28;
			} else if (month == 4 || month == 6 || month == 9 || month == 11) {
				expectedDaysInMonth = 30;
			} else {
				expectedDaysInMonth = 31;
			}
			Assert.assertEquals("Expected " + expectedDaysInMonth + " but got " + actualDaysInMonth, expectedDaysInMonth, actualDaysInMonth);
		}
	}

	@Test
	public void testDaysInMonthFebLeapYear() {
		int month = 2;
		int year = 2016;
		int actualDaysInMonth = DateUtil.daysInMonth(year, month);
		int expectedDaysInMonth = 29;
		Assert.assertEquals("Expected " + expectedDaysInMonth + " but got " + actualDaysInMonth, expectedDaysInMonth, actualDaysInMonth);

		year = 2100;
		actualDaysInMonth = DateUtil.daysInMonth(year, month);
		expectedDaysInMonth = 28;
		Assert.assertEquals("Expected " + expectedDaysInMonth + " but got " + actualDaysInMonth, expectedDaysInMonth, actualDaysInMonth);
	}
}
