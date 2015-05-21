package org.generationcp.commons.parsing.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

public class BulkComplValidatorTest {

	private BulkComplValidator validator = new BulkComplValidator(7,6);
	
	@Test
	public void testIsParsedValueValid_NullValue() {
		assertTrue(validator.isParsedValueValid(null, 
				createAdditionalParams(null)));
	}
	
	@Test
	public void testIsParsedValueValid_EmptyValue() {
		assertTrue(validator.isParsedValueValid("", 
				createAdditionalParams(null)));
	}
	
	@Test
	public void testIsParsedValueValid_InvalidValue() {
		assertFalse(validator.isParsedValueValid("wrong", 
				createAdditionalParams(null)));
	}
	
	private Map<String,Object> createAdditionalParams(String bulkWithValue) {
		return BulkComplValidator.createAdditionalParams(bulkWithValue);
	}
	
	@Test
	public void testIsParsedValueValid_WithBulkWithValue_InvalidValue() {
		assertFalse(validator.isParsedValueValid("any", "SID1-2"));
	}
	
	@Test
	public void testIsParsedValueValid_WithBulkWithValue_ValidValue() {
		assertTrue(validator.isParsedValueValid("Y", "SID1-2"));
	}
	
	@Test
	public void testIsParsedValueValid_WithBulkWithValue_NullValue() {
		assertFalse(validator.isParsedValueValid(null, "SID1-2"));
	}
	
	@Test
	public void testIsParsedValueValid_WithBulkWithValue_EmptyValue() {
		assertFalse(validator.isParsedValueValid("", "SID1-2"));
	}
	
	@Test
	public void testIsParsedValueValid_EmptyBulkWithValue_NullValue() {
		assertTrue(validator.isParsedValueValid(null, ""));
	}
	
	@Test
	public void testIsParsedValueValid_EmptyBulkWithValue_EmptyValue() {
		assertTrue(validator.isParsedValueValid("", ""));
	}
	
	@Test
	public void testIsParsedValueValid_EmptyBulkWithValue_WithBulkComplValue() {
		assertFalse(validator.isParsedValueValid("Y", ""));
	}
	
	
}
