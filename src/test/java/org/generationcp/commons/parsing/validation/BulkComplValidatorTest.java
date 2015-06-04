
package org.generationcp.commons.parsing.validation;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class BulkComplValidatorTest {

	private final BulkComplValidator validator = new BulkComplValidator(7, 6);

	@Test
	public void testIsParsedValueValid_NullValue() {
		Assert.assertTrue(this.validator.isParsedValueValid(null, this.createAdditionalParams(null)));
	}

	@Test
	public void testIsParsedValueValid_EmptyValue() {
		Assert.assertTrue(this.validator.isParsedValueValid("", this.createAdditionalParams(null)));
	}

	@Test
	public void testIsParsedValueValid_InvalidValue() {
		Assert.assertFalse(this.validator.isParsedValueValid("wrong", this.createAdditionalParams(null)));
	}

	private Map<String, Object> createAdditionalParams(String bulkWithValue) {
		return BulkComplValidator.createAdditionalParams(bulkWithValue);
	}

	@Test
	public void testIsParsedValueValid_WithBulkWithValue_InvalidValue() {
		Assert.assertFalse(this.validator.isParsedValueValid("any", "SID1-2"));
	}

	@Test
	public void testIsParsedValueValid_WithBulkWithValue_ValidValue() {
		Assert.assertTrue(this.validator.isParsedValueValid("Y", "SID1-2"));
	}

	@Test
	public void testIsParsedValueValid_WithBulkWithValue_NullValue() {
		Assert.assertFalse(this.validator.isParsedValueValid(null, "SID1-2"));
	}

	@Test
	public void testIsParsedValueValid_WithBulkWithValue_EmptyValue() {
		Assert.assertFalse(this.validator.isParsedValueValid("", "SID1-2"));
	}

	@Test
	public void testIsParsedValueValid_EmptyBulkWithValue_NullValue() {
		Assert.assertTrue(this.validator.isParsedValueValid(null, ""));
	}

	@Test
	public void testIsParsedValueValid_EmptyBulkWithValue_EmptyValue() {
		Assert.assertTrue(this.validator.isParsedValueValid("", ""));
	}

	@Test
	public void testIsParsedValueValid_EmptyBulkWithValue_WithBulkComplValue() {
		Assert.assertFalse(this.validator.isParsedValueValid("Y", ""));
	}

}
