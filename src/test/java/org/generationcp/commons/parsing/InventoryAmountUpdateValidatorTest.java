package org.generationcp.commons.parsing;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.commons.parsing.validation.InventoryAmountUpdateValidator;
import org.generationcp.commons.parsing.validation.ParsingValidator;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class InventoryAmountUpdateValidatorTest {

	@Test
	public void testNoRestrictedStockIds() {
		final InventoryAmountUpdateValidator validator = new InventoryAmountUpdateValidator(5, Collections.emptyMap());
		Assert.assertTrue(validator.isParsedValueValid(RandomStringUtils.randomAlphanumeric(20),
			Collections.singletonMap(ParsingValidator.PAIRED_COLUMN_VALUE_KEY, new Random().nextDouble())));

		Assert.assertTrue(validator.isParsedValueValid(RandomStringUtils.randomAlphanumeric(20),
			Collections.emptyMap()));
	}

	@Test
	public void testWithRestrictedStockIds() {
		final Map<String, Double> restrictedStockIds = new HashMap<>();
		restrictedStockIds.put("STK1-2", 18.0);
		restrictedStockIds.put("STK1-4", 28.0);

		final InventoryAmountUpdateValidator validator = new InventoryAmountUpdateValidator(5, restrictedStockIds);

		// Stock ID to be updated not in restricted stock IDs
		Assert.assertTrue(validator.isParsedValueValid(RandomStringUtils.randomAlphanumeric(20),
			Collections.singletonMap(ParsingValidator.PAIRED_COLUMN_VALUE_KEY, new Random().nextDouble())));

		// Stock ID to be updated is restricted but the amount is the same
		Assert.assertTrue(validator.isParsedValueValid("STK1-2",
			Collections.singletonMap(ParsingValidator.PAIRED_COLUMN_VALUE_KEY, "18.0")));

		Assert.assertTrue(validator.isParsedValueValid("STK1-4",
			Collections.singletonMap(ParsingValidator.PAIRED_COLUMN_VALUE_KEY, "28.0")));
	}

}
