package org.generationcp.commons.ruleengine.naming.expression;

import org.generationcp.commons.constant.AppConstants;
import org.generationcp.commons.pojo.AdvancingSource;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AttributeSourceExpression extends AttributeExpression {

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	public static final String ATTRIBUTE_KEY = "ATTRSC";
	public static final String PATTERN_KEY = "\\[" + ATTRIBUTE_KEY + "\\.([^\\.]*)\\]"; // Example: ATTRSC.1010

	@Override
	public void apply(final List<StringBuilder> values, final AdvancingSource source, final String capturedText) {
		for (final StringBuilder value : values) {
			String newValue = "";
			final Integer variableId = Integer.valueOf(capturedText.substring(1, capturedText.length() - 1).split("\\.")[1]);
			if (source.getBreedingMethod().getMtype().equals(AppConstants.METHOD_TYPE_DER.getString())
				|| source.getBreedingMethod().getMtype().equals(AppConstants.METHOD_TYPE_MAN.getString())) {
				newValue = germplasmDataManager.getAttributeValue(Integer.parseInt(source.getGermplasm().getGid()), variableId);
			}
			this.replaceAttributeExpressionWithValue(value, ATTRIBUTE_KEY, variableId, newValue);
		}
	}

	@Override
	public String getExpressionKey() {
		return AttributeSourceExpression.PATTERN_KEY;
	}

}
