package org.generationcp.commons.ruleengine.naming.expression;

import java.util.List;

import org.generationcp.commons.pojo.AdvancingSource;
import org.generationcp.commons.ruleengine.naming.expression.BaseExpression;
import org.springframework.stereotype.Component;

@Component
public class SelectionTraitExpression extends BaseExpression {

    public static final String KEY = "[SELTRAIT]";

    public SelectionTraitExpression() {
    }

    @Override
    public void apply(List<StringBuilder> values, AdvancingSource source, final String capturedText) {
        for (StringBuilder container : values) {
            this.replaceExpressionWithValue(container, source.getSelectionTraitValue());
        }

    }

    @Override
    public String getExpressionKey() {
        return KEY;
    }
}
