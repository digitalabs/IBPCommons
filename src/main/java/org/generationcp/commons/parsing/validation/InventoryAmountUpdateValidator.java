package org.generationcp.commons.parsing.validation;

import org.springframework.util.CollectionUtils;

import java.util.Map;

public class InventoryAmountUpdateValidator extends ParsingValidator {

	private static final String ERROR_KEY = "error.inventory.amount.cannot.be.updated";
	private final Map<String, Double> restrictedStockIds;

	public InventoryAmountUpdateValidator(final Integer amountColumnIndex, final Map<String, Double> restrictedStockIds) {
		super(true);
		this.setPairedColumnIndex(amountColumnIndex);
		this.restrictedStockIds = restrictedStockIds;
		this.setValidationErrorMessage(InventoryAmountUpdateValidator.ERROR_KEY);
	}

	@Override
	public boolean isParsedValueValid(final String value, final Map<String, Object> additionalParams) {
		// If the current row is for one of the "restricted" stock IDs for editing,
		// Verify that the amount is not being updated from what is saved
		if (!CollectionUtils.isEmpty(this.restrictedStockIds) && this.restrictedStockIds.containsKey(value)) {
			final String amount = (String) additionalParams.get(ParsingValidator.PAIRED_COLUMN_VALUE_KEY);
			return this.restrictedStockIds.get(value).equals(Double.valueOf(amount));
		}
		return true;
	}
}
