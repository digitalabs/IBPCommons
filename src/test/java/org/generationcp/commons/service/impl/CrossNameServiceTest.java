
package org.generationcp.commons.service.impl;

import junit.framework.Assert;
import org.generationcp.commons.settings.CrossNameSetting;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CrossNameServiceTest {

	public static final Integer TEST_CROSS_SEQUENCE_NUMBER = 5;
	public static final String TEST_CROSS_SEQUENCE = Integer.toString(TEST_CROSS_SEQUENCE_NUMBER);
	public static final String TEST_PREFIX = "PREFIX";
	public static final String TEST_SUFFIX = "SUFFIX";

	@Mock
	private GermplasmDataManager germplasmDataManager;

	@InjectMocks
	private CrossNameServiceImpl unitUnderTest;

	private CrossNameSetting nameSetting;

	@Before
	public void setUp() {
		nameSetting = new CrossNameSetting();
		nameSetting.setPrefix("");
	}

	@Test
	public void testNextNumberInSequenceEmptyStartSequence() {
		nameSetting.setStartNumber(null);

		Mockito.when(germplasmDataManager.getNextSequenceNumberForCrossName(Mockito.anyString())).thenReturn(TEST_CROSS_SEQUENCE);

		Integer nextSequenceNumber = unitUnderTest.getNextNumberInSequence(nameSetting);

		Assert.assertEquals("Service should return the sequence number provided by germplasm data manager ", TEST_CROSS_SEQUENCE_NUMBER,
				nextSequenceNumber);
		Mockito.verify(germplasmDataManager).getNextSequenceNumberForCrossName(Mockito.anyString());
	}

	@Test
	public void testNextNumberInSequenceWithStartNumber() {
		nameSetting.setStartNumber(TEST_CROSS_SEQUENCE_NUMBER);

		Integer nextSequenceNumber = unitUnderTest.getNextNumberInSequence(nameSetting);
		Assert.assertEquals("Service should return the user provided start number as next number in sequence", TEST_CROSS_SEQUENCE_NUMBER,
				nextSequenceNumber);
		Mockito.verify(germplasmDataManager, Mockito.never()).getNextSequenceNumberForCrossName(Mockito.anyString());
	}

	@Test
	public void testGetNumberWithLeadingZeroesAsStringNoDigitsSupplied() {
		nameSetting.setNumOfDigits(null);

		String output = unitUnderTest.getNumberWithLeadingZeroesAsString(TEST_CROSS_SEQUENCE_NUMBER, nameSetting);
		Assert.assertEquals("Service should output the provided number as is when user does not specify num of digits",
				Integer.toString(TEST_CROSS_SEQUENCE_NUMBER), output);
	}

	@Test
	public void testGetNumberWithLeadingZeroesAsStringExtraDigits() {
		nameSetting.setNumOfDigits(5);

		String output = unitUnderTest.getNumberWithLeadingZeroesAsString(TEST_CROSS_SEQUENCE_NUMBER, nameSetting);
		Assert.assertTrue("Service should be able to output a string with the amount of digits provided by the user",
				output.length() == nameSetting.getNumOfDigits());
		Assert.assertTrue("Service should be able to output a string ending with the initial provided number",
				output.endsWith(TEST_CROSS_SEQUENCE_NUMBER.toString()));
	}

	@Test
	public void testBuildPrefixStringNoSpace() {
		nameSetting.setPrefix(TEST_PREFIX + " ");
		String output = unitUnderTest.buildPrefixString(nameSetting);

		Assert.assertEquals("Service should provide the trimmed version of the prefix", TEST_PREFIX, output);
	}

	@Test
	public void testBuildPrefixStringWithSpace() {
		nameSetting.setPrefix(TEST_PREFIX + " ");
		nameSetting.setAddSpaceBetweenPrefixAndCode(true);

		String output = unitUnderTest.buildPrefixString(nameSetting);

		Assert.assertEquals("Service should provide a trimmer version of the prefix, with an additional space", TEST_PREFIX + " ", output);
	}

	@Test
	public void testBuildSuffixNoSpace() {
		nameSetting.setSuffix(TEST_SUFFIX + "  ");

		String output = unitUnderTest.buildSuffixString(nameSetting);

		Assert.assertEquals("Service should provide a trimmed version of the suffix", TEST_SUFFIX, output);
	}

}
