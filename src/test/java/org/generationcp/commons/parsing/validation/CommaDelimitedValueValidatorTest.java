package org.generationcp.commons.parsing.validation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.parsing.validation.CommaDelimitedValueValidator;
import org.junit.Test;

public class CommaDelimitedValueValidatorTest {

	private CommaDelimitedValueValidator validator;
	
	@Test
	public void testIsParsedValueValid_NullValue() {
		validator = new CommaDelimitedValueValidator(new ArrayList<String>());
		assertTrue(validator.isParsedValueValid(null, null));
	}
	
	@Test
	public void testIsParsedValueValid_EmptyValue() {
		validator = new CommaDelimitedValueValidator(new ArrayList<String>());
		assertTrue(validator.isParsedValueValid("", null));
	}
	
	@Test
	public void testIsParsedValueValid_NullAcceptedValueList() {
		validator = new CommaDelimitedValueValidator(null);
		assertFalse(validator.isParsedValueValid("any", null));
	}
	
	@Test
	public void testIsParsedValueValid_EmptyAcceptedValueList() {
		validator = new CommaDelimitedValueValidator(new ArrayList<String>());
		assertFalse(validator.isParsedValueValid("any", null));
	}
	
	@Test
	public void testIsParsedValueValid_NotAccepted() {
		List<String> valuesList = createAcceptedValueList();
		validator = new CommaDelimitedValueValidator(valuesList);
		assertFalse(validator.isParsedValueValid("SID1-4", null));
	}
	
	@Test
	public void testIsParsedValueValid_Accepted() {
		List<String> valuesList = createAcceptedValueList();
		validator = new CommaDelimitedValueValidator(valuesList);
		assertTrue(validator.isParsedValueValid("SID1-2", null));
	}
	
	@Test
	public void testIsParsedValueValid_Accepted_Multiple() {
		List<String> valuesList = createAcceptedValueList();
		validator = new CommaDelimitedValueValidator(valuesList);
		assertTrue(validator.isParsedValueValid("SID1-3,SID1-1", null));
	}
	
	@Test
	public void testIsParsedValueValid_NotAccepted_Multiple() {
		List<String> valuesList = createAcceptedValueList();
		validator = new CommaDelimitedValueValidator(valuesList);
		assertFalse(validator.isParsedValueValid("SID1-4,SID1-1", null));
	}
	
	@Test
	public void testIsParsedValueValid_NotAccepted_Duplicate() {
		List<String> valuesList = createAcceptedValueList();
		validator = new CommaDelimitedValueValidator(valuesList);
		assertFalse(validator.isParsedValueValid("SID1-1,SID1-1", null));
	}

	private List<String> createAcceptedValueList() {
		List<String> valuesList = new ArrayList<String>();
		valuesList.add("SID1-1");
		valuesList.add("SID1-2");
		valuesList.add("SID1-3");
		return valuesList;
	}
}
