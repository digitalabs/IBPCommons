
package org.generationcp.commons.vaadin.ui.fields.test;

import java.util.Date;

import org.generationcp.commons.vaadin.ui.fields.BmsDateField;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.vaadin.data.Validator.InvalidValueException;

public class BmsDateFieldTest {
	
	private static BmsDateField dateField;
	private static Date date;
	
	@BeforeClass
	public static void beforeClass() {
		dateField = new BmsDateField();
		date = new Date();
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testIsValidFalse() {
		date.setYear(-1); // 1899
		dateField.setValue(date);

		Assert.assertFalse("The date should be valid since the year is less than 1900.", dateField.isValid());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testIsValidTrue() {
		date.setYear(0); // 1900
		dateField.setValue(date);

		Assert.assertTrue("The date should be valid since the year is equal to 1900.", dateField.isValid());

		// current date
		date = new Date();
		dateField.setValue(date);

		Assert.assertTrue("The date should be valid since the year greater than 1900.", dateField.isValid());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testValidateTrue() {
		date.setYear(0); // 1900
		dateField.setValue(date);

		try {
			dateField.validate();
		} catch (InvalidValueException e) {
			Assert.fail("The date should be valid since the year is equal to 1900.");
		}

		// current date
		date = new Date();
		dateField.setValue(date);

		try {
			dateField.validate();
		} catch (InvalidValueException e) {
			Assert.fail("The date should be valid since the year is greater than 1900.");
		}
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testValidateWithError() {
		date.setYear(-1); // 1899
		dateField.setValue(date);
		
		try {
			dateField.validate();
			Assert.fail("The date should be invalid since the year is less than 1900.");
		} catch (InvalidValueException e) {
			String errorMessage = e.getMessage();
			Assert.assertEquals("The error message should be " + BmsDateField.INVALID_YEAR, BmsDateField.INVALID_YEAR, errorMessage);
		}
	}
}
