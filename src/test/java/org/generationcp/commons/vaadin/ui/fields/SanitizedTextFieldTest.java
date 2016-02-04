package org.generationcp.commons.vaadin.ui.fields;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test fro SanitizedTextField
 */
public class SanitizedTextFieldTest {
	public static final String XSS_VULNERABLE_STRING = ">'>\"><img src=x onerror=alert(0)>";
	public static final String SANITIZED_STRING = "&gt;&#39;&gt;&#34;&gt;";
	public static final String SPACES_TEXT = "Spaces Text     ";

	private SanitizedTextField sanitizedTextField = new SanitizedTextField();
	private SanitizedTextField trimmedTextField = new SanitizedTextField();

	@Before
	public void setUp() throws Exception {
		sanitizedTextField.setValue(XSS_VULNERABLE_STRING);
		trimmedTextField.setValue(SPACES_TEXT);
	}

	@Test
	public void testGetValueIsTrimmed() throws Exception {
		Assert.assertEquals("Text is trimmed",SPACES_TEXT.trim(),trimmedTextField.getValue());

	}

	@Test
	public void testGetValueIsSanitized() throws Exception {
		Assert.assertEquals("Text is sanitized", SANITIZED_STRING,sanitizedTextField.getValue());
	}

}
