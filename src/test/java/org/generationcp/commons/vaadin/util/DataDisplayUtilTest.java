package org.generationcp.commons.vaadin.util;

import junit.framework.Assert;

import org.junit.Test;

public class DataDisplayUtilTest {
	
	@Test
	public void testTruncateDisplay_WhenInputIsNull(){
		Assert.assertEquals("Expecting to return empty string for null input.","",DataDisplayUtil.truncateDisplay(null, 10));
	}
	
	@Test
	public void testTruncateDisplay_WhenMaxlengthIsZero(){
		String input = "Test Input";
		Assert.assertEquals("Expecting to return the input as is when maxlength is set to 0.",input,DataDisplayUtil.truncateDisplay(input, 10));
	}
	
	@Test
	public void testTruncateDisplay_WhenMaxlengthGreaterThanTheInputLength(){
		String input = "Test Input";
		int maxLength = 5;
		
		String expectedOutput = input.substring(0, maxLength) + "...";
		Assert.assertEquals("Expecting to return the truncated input when maxlength is less than the input length.",expectedOutput,DataDisplayUtil.truncateDisplay(input, maxLength));
	}
	
	@Test
	public void testTruncateDisplay_WhenMaxlengthLessThanTheInputLength(){
		String input = "Test Input";
		int maxLength = 15;
		
		Assert.assertEquals("Expecting to return the input as is when maxlength is less than the input length.",input,DataDisplayUtil.truncateDisplay(input, maxLength));
	}
}
