
package org.generationcp.commons.parsing.validation;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class CommaDelimitedValueValidatorTest {

	private CommaDelimitedValueValidator validator;

	@Test
	public void testIsParsedValueValid_NullValue() {
		this.validator = new CommaDelimitedValueValidator(new ArrayList<String>());
		Assert.assertTrue(this.validator.isParsedValueValid(null, null));
	}

	@Test
	public void testIsParsedValueValid_EmptyValue() {
		this.validator = new CommaDelimitedValueValidator(new ArrayList<String>());
		Assert.assertTrue(this.validator.isParsedValueValid("", null));
	}

	@Test
	public void testIsParsedValueValid_NullAcceptedValueList() {
		this.validator = new CommaDelimitedValueValidator(null);
		Assert.assertFalse(this.validator.isParsedValueValid("any", null));
	}

	@Test
	public void testIsParsedValueValid_EmptyAcceptedValueList() {
		this.validator = new CommaDelimitedValueValidator(new ArrayList<String>());
		Assert.assertFalse(this.validator.isParsedValueValid("any", null));
	}

	@Test
	public void testIsParsedValueValid_NotAccepted() {
		List<String> valuesList = this.createAcceptedValueList();
		this.validator = new CommaDelimitedValueValidator(valuesList);
		Assert.assertFalse(this.validator.isParsedValueValid("SID1-4", null));
	}

	@Test
	public void testIsParsedValueValid_Accepted() {
		List<String> valuesList = this.createAcceptedValueList();
		this.validator = new CommaDelimitedValueValidator(valuesList);
		Assert.assertTrue(this.validator.isParsedValueValid("SID1-2", null));
	}

	@Test
	public void testIsParsedValueValid_Accepted_Multiple() {
		List<String> valuesList = this.createAcceptedValueList();
		this.validator = new CommaDelimitedValueValidator(valuesList);
		Assert.assertTrue(this.validator.isParsedValueValid("SID1-3, SID1-1", null));
	}

	@Test
	public void testIsParsedValueValid_NotAccepted_Multiple() {
		List<String> valuesList = this.createAcceptedValueList();
		this.validator = new CommaDelimitedValueValidator(valuesList);
		Assert.assertFalse(this.validator.isParsedValueValid("SID1-4,SID1-1", null));
	}

	@Test
	public void testIsParsedValueValid_NotAccepted_Duplicate() {
		List<String> valuesList = this.createAcceptedValueList();
		this.validator = new CommaDelimitedValueValidator(valuesList);
		Assert.assertFalse(this.validator.isParsedValueValid("SID1-1,SID1-1", null));
	}

	private List<String> createAcceptedValueList() {
		List<String> valuesList = new ArrayList<String>();
		valuesList.add("SID1-1");
		valuesList.add("SID1-2");
		valuesList.add("SID1-3");
		return valuesList;
	}
}
