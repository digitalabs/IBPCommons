package org.generationcp.commons.vaadin.ui.fields;

import com.vaadin.ui.TextField;
import org.owasp.html.Sanitizers;

/**
 * This class extends Vaadin's text field that sanitizes and trims values
 */
public class SanitizedTextField extends TextField {
	@Override
	public Object getValue() {
		return super.getValue() != null ? Sanitizers.FORMATTING.sanitize(super.getValue().toString().trim()) : null;
	}
}
