package org.generationcp.commons.vaadin.ui.fields.test;

import java.util.Date;

import org.generationcp.commons.vaadin.ui.fields.BmsDateField;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.Validator.InvalidValueException;

public class BmsDateFieldTest {
	
	@SuppressWarnings("deprecation")
	@Test
	public void testIsValidMethodReturnsFalseWhenYearIsLessThan1900(){
		//input
		Date date = new Date();
		date.setYear(-1); //1899

		BmsDateField dateField = new BmsDateField();
		dateField.setValue(date);
		
		Assert.assertFalse("Expecting a false return value when the year is less than 1900.", dateField.isValid());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testIsValidMethodReturnsTrueWhenYearIsGreaterThanOrEqualTo1900(){
		//input
		BmsDateField dateField = new BmsDateField();
		Date date = new Date();
		
		// Equal to 1900
		date.setYear(0); //1900
		dateField.setValue(date);
		
		Assert.assertTrue("Expecting a true return value when the year is equal to 1900.", dateField.isValid());
		
		// Greater than 1900
		date.setYear(114); //2014
		dateField.setValue(date);
		
		Assert.assertTrue("Expecting a true return value when the year greater than 1900.", dateField.isValid());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testValidateMethodReturnsFalseWhenYearIsLessThan1900(){
		//input
		BmsDateField dateField = new BmsDateField();
		Date date = new Date();
		boolean isValid=true;
		
		date.setYear(-1); //1899
		dateField.setValue(date);
		
		try {
			dateField.validate();
		} catch(InvalidValueException e) {
			isValid = false;
		}
		
		Assert.assertFalse("Expecting a false return value when the year is less than 1900.", isValid);
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testValidateMethodReturnsTrueWhenYearIsGreaterThanOrEqualTo1900(){
		//input
		BmsDateField dateField = new BmsDateField();
		Date date = new Date();
		boolean isValid = true;
		
		// Equal to 1900
		date.setYear(0); //1900
		dateField.setValue(date);
		
		try {
			dateField.validate();
		} catch(InvalidValueException e) {
			isValid = false;
		}
		
		Assert.assertTrue("Expecting a true return value when the year is equal to 1900.", isValid);
		
		// Greater than 1900
		date.setYear(114); //2014
		dateField.setValue(date);
		
		isValid = true;
		try {
			dateField.validate();
		} catch(InvalidValueException e) {
			isValid = false;
		}
		
		Assert.assertTrue("Expecting a true return value when the year greater than 1900.", isValid);
	}
}
